package me.pbox.site.database;

import com.zaxxer.hikari.HikariDataSource;
import me.pbox.site.exception.ApplicationException;
import org.jacuzzi.core.DatabaseException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Maxim Gusarov (gusarov.maxim@gmail.com)
 */
public class ApplicationDataSource {
    private static final Properties properties = new Properties();

    private ApplicationDataSource() {
        throw new UnsupportedOperationException("Can't create ApplicationDataSource.");
    }

    public static DataSource getInstance() {
        try {
            DataSourceHolder.INSTANCE.getConnection().close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return DataSourceHolder.INSTANCE;
    }

    private static class DataSourceHolder {
        private static final DataSource INSTANCE;

        static {
            final HikariDataSource hikariDataSource = new HikariDataSource();
            int maxPoolSize = Integer.parseInt(properties.getProperty("database.max-pool-size"));

            hikariDataSource.setMaximumPoolSize(maxPoolSize);
            hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikariDataSource.setConnectionInitSql(String.format("SET time_zone='%s'", properties.getProperty("database.timezone")));
            hikariDataSource.setConnectionTestQuery("SELECT 1");
            hikariDataSource.setAutoCommit(true);
            hikariDataSource.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
            hikariDataSource.setIdleTimeout(150000);
            hikariDataSource.setMaxLifetime(450000);
            hikariDataSource.setLeakDetectionThreshold(120000);

            hikariDataSource.addDataSourceProperty("url", properties.getProperty("database.url"));
            hikariDataSource.addDataSourceProperty("user", properties.getProperty("database.user"));
            hikariDataSource.addDataSourceProperty("password", properties.getProperty("database.password"));
            hikariDataSource.addDataSourceProperty("cachePrepStmts", true);
            hikariDataSource.addDataSourceProperty("prepStmtCacheSize", 500);
            hikariDataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            hikariDataSource.addDataSourceProperty("useServerPrepStmts", true);

            INSTANCE = hikariDataSource;
        }

        private DataSourceHolder() {
            throw new UnsupportedOperationException();
        }
    }

    static {
        try {
            properties.load(ApplicationDataSource.class.getResourceAsStream("/database.properties"));
            Class.forName(properties.getProperty("database.driver"));
        } catch (IOException e) {
            throw new ApplicationException("Can't load /database.properties.", e);
        } catch (ClassNotFoundException e) {
            throw new ApplicationException(
                    "Can't load database driver " + properties.getProperty("database.driver") + '.', e
            );
        }
    }
}
