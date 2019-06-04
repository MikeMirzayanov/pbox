package me.pbox.site.index.impl;

import com.google.inject.Inject;
import me.pbox.site.dao.PackageDao;
import me.pbox.site.exception.ApplicationException;
import me.pbox.site.index.IllegalQueryException;
import me.pbox.site.index.Index;
import me.pbox.site.model.Package;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class IndexImpl implements Index {
    private static final Lock LOCK = new ReentrantLock();

    private Directory directory;

    @Inject
    private PackageDao packageDao;

    public void insertOrUpdate(IndexWriter writer, Package p) {
        Document document = new Document();

        document.add(new StringField("name", p.getName().toLowerCase(), Field.Store.YES));
        document.add(new StringField("title", p.getTitle().toLowerCase(), Field.Store.YES));
        document.add(new StringField("version", p.getVersion().toLowerCase(), Field.Store.YES));
        document.add(new TextField("description", p.getDescription().toLowerCase(), Field.Store.YES));
        document.add(new TextField("tags", p.getTags().replaceAll(",", " ").toLowerCase(), Field.Store.YES));
        document.add(new TextField("authors", p.getAuthors().toLowerCase(), Field.Store.YES));

        try {
            writer.addDocument(document);
        } catch (IOException e) {
            throw new ApplicationException("Can't add document.", e);
        }
    }

    @Override
    public void insertOrUpdate(List<Package> packages) {
        LOCK.lock();

        try {
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriter writer;
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            try {
                if (directory != null) {
                    directory.close();
                }
            } catch (IOException e) {
                // No operations.
            }

            directory = new RAMDirectory();

            try {
                writer = new IndexWriter(directory, indexWriterConfig);
            } catch (IOException e) {
                throw new ApplicationException("Can't create index.", e);
            }

            for (Package p : packages) {
                insertOrUpdate(writer, p);
            }

            writeClose(writer);
        } finally {
            LOCK.unlock();
        }
    }

    private void writeClose(IndexWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            throw new ApplicationException("Can't close index writer.", e);
        }
    }

    @Override
    public List<Package> find(String query) throws IllegalQueryException {
        String[] fields = {
                "name",
                "title",
                "version",
                "description",
                "tags",
                "authors"
        };

        return findPackagesByFieldsAndQuery(query, fields);
    }

    @Override
    public List<Package> findByName(String query) throws IllegalQueryException {
        String[] fields = {
                "name"
        };

        return findPackagesByFieldsAndQuery(query, fields);
    }

    private List<Package> findPackagesByFieldsAndQuery(String query, String[] fields) throws IllegalQueryException {
        List<Package> packages = new ArrayList<>();

        LOCK.lock();
        try {
            Analyzer analyzer = new StandardAnalyzer();
            IndexReader indexReader = DirectoryReader.open(directory);

            if (StringUtils.isBlank(query)) {
                for (int i = 0; i < indexReader.maxDoc(); i++) {
                    Document document = indexReader.document(i);
                    addDocument(packages, document);
                }
            } else {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
                TopDocs topDocs = indexSearcher.search(queryParser.parse(query), Integer.MAX_VALUE);

                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document document = indexSearcher.doc(scoreDoc.doc);
                    addDocument(packages, document);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("Can't find package(s).", e);
        } catch (ParseException e) {
            throw new IllegalQueryException("Illegal query '" + query + "'.", e);
        } finally {
            LOCK.unlock();
        }

        return packages;
    }

    private void addDocument(List<Package> packages, Document document) {
        Package p = packageDao.find(document.get("name"), document.get("version"));
        if (p != null) {
            packages.add(p);
        }
    }
}
