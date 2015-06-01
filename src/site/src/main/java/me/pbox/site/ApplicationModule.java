package me.pbox.site;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import me.pbox.site.captions.dao.CaptionChangeDao;
import me.pbox.site.captions.dao.CaptionDao;
import me.pbox.site.captions.dao.impl.CaptionChangeDaoImpl;
import me.pbox.site.captions.dao.impl.CaptionDaoImpl;
import me.pbox.site.dao.CategoryDao;
import me.pbox.site.dao.DateDao;
import me.pbox.site.dao.PackageDao;
import me.pbox.site.dao.impl.CategoryDaoImpl;
import me.pbox.site.dao.impl.DateDaoImpl;
import me.pbox.site.dao.impl.PackageDaoImpl;
import me.pbox.site.index.Index;
import me.pbox.site.index.impl.IndexImpl;
import me.pbox.site.service.PackageService;
import me.pbox.site.service.impl.PackageServiceImpl;

/**
 * @author Mike Mirzayanov
 */
public class ApplicationModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(CaptionDao.class).to(CaptionDaoImpl.class).in(Scopes.SINGLETON);
        binder.bind(CaptionChangeDao.class).to(CaptionChangeDaoImpl.class).in(Scopes.SINGLETON);
        binder.bind(DateDao.class).to(DateDaoImpl.class).in(Scopes.SINGLETON);
        binder.bind(CategoryDao.class).to(CategoryDaoImpl.class).in(Scopes.SINGLETON);
        binder.bind(PackageDao.class).to(PackageDaoImpl.class).in(Scopes.SINGLETON);

        binder.bind(PackageService.class).to(PackageServiceImpl.class).in(Scopes.SINGLETON);
        binder.bind(Index.class).to(IndexImpl.class).in(Scopes.SINGLETON);
    }
}
