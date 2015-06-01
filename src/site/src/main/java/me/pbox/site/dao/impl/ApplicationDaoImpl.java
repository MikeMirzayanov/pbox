/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.dao.impl;

import com.codeforces.commons.process.ThreadUtil;
import com.codeforces.commons.properties.PropertiesUtil;
import com.codeforces.commons.time.TimeUtil;
import me.pbox.site.database.ApplicationDataSource;
import me.pbox.site.exception.ApplicationException;
import me.pbox.site.model.ApplicationEntity;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.jacuzzi.core.GenericDaoImpl;
import org.jacuzzi.core.Row;
import org.jacuzzi.core.TypeOracle;

import java.security.SecureRandom;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.codeforces.commons.time.TimeUtil.MILLIS_PER_SECOND;
import static java.lang.StrictMath.abs;

/**
 * Base class for many jacuzzi implementations of application's DAOs.
 *
 * @author Dmitry Levshunov
 */
@SuppressWarnings({"StaticVariableMayNotBeInitialized", "StaticVariableNamingConvention", "StaticNonFinalField"})
public abstract class ApplicationDaoImpl<T extends ApplicationEntity> extends GenericDaoImpl<T, Long> {
    private static final TimeZone TIME_ZONE
            = TimeZone.getTimeZone(PropertiesUtil.getProperty("database.timezone", "Europe/Moscow", "/database.properties"));

    private static final Logger logger = Logger.getLogger(ApplicationDaoImpl.class);
    private final Random random = new SecureRandom();

    private static long lastFindNowCall;
    private static Date lastFindNowResult;
    private static volatile boolean databaseTimeSynchronized;
    private static boolean warnedThatTimeIsNotSynchronized;

    private static final ReadWriteLock findNowReadWriteLock = new ReentrantReadWriteLock();
    private final Lock findNowReadLock = findNowReadWriteLock.readLock();
    private final Lock findNowWriteLock = findNowReadWriteLock.writeLock();

    protected ApplicationDaoImpl() {
        super(ApplicationDataSource.getInstance());
    }

    protected long getRandomLong() {
        return random.nextLong()
                + 31 * System.nanoTime()
                + 1009 * Runtime.getRuntime().freeMemory()
                + 10017 * Runtime.getRuntime().totalMemory();
    }

    protected String getRandomToken() {
        return DigestUtils.sha1Hex(String.valueOf(getRandomLong()) + getRandomLong());
    }

    @SuppressWarnings({"UnnecessaryBoxing"})
    public T find(long id) {
        return find(Long.valueOf(id));
    }

    /**
     * @return current date rounded down to seconds
     */
    @SuppressWarnings({"AssignmentToStaticFieldFromInstanceMethod", "RefusedBequest"})
    @Override
    public Date findNow() {
        Date pastDate = new GregorianCalendar(1998, 1, 1).getTime();
        Date result;
        int iterationCount = 0;

        while (true) {
            result = internalFindNow();
            if (result.compareTo(pastDate) > 0) {
                break;
            }

            /* We hope it is impossible case. */
            logger.error("Unable to get current time via internalFindNow(), found " + result + '.');
            iterationCount++;
            if (iterationCount > 2) {
                long sleepTimeMillis = iterationCount * TimeUtil.MILLIS_PER_SECOND;
                logger.error("Sleeping for " + sleepTimeMillis + " ms because of internalFindNow() returns " + result + '.');
                ThreadUtil.sleep(sleepTimeMillis);
            }
        }

        return result;
    }

    private Date internalFindNow() {
        Calendar calendar = new GregorianCalendar(TIME_ZONE);
        long systemTimeMillis = System.currentTimeMillis();

        if (databaseTimeSynchronized) {
            calendar.setTimeInMillis(systemTimeMillis / MILLIS_PER_SECOND * MILLIS_PER_SECOND);
            return calendar.getTime();
        } else {
            findNowReadLock.lock();
            try {
                if (lastFindNowResult != null && abs(systemTimeMillis - lastFindNowCall) < 250L) {
                    calendar.setTimeInMillis(lastFindNowResult.getTime());
                    return calendar.getTime();
                }
            } finally {
                findNowReadLock.unlock();
            }

            findNowWriteLock.lock();
            try {
                lastFindNowCall = systemTimeMillis;
                long databaseTimeMillis = getJacuzzi().findLong("SELECT UNIX_TIMESTAMP(NOW())") * MILLIS_PER_SECOND;

                if (abs(databaseTimeMillis - systemTimeMillis) < MILLIS_PER_SECOND) {
                    logger.info("Times between database and system are synchronized. " +
                            "Future findNow() invocations will use System.currentTimeMillis()."
                    );
                    databaseTimeSynchronized = true;
                } else if (!warnedThatTimeIsNotSynchronized) {
                    warnedThatTimeIsNotSynchronized = true;
                    logger.warn("Times between database and system are NOT synchronized. " +
                            "This can slow down your application."
                    );
                }

                calendar.setTimeInMillis(databaseTimeMillis);
                lastFindNowResult = calendar.getTime();
                return calendar.getTime();
            } finally {
                findNowWriteLock.unlock();
            }
        }
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    protected void beginTransaction() {
        getJacuzzi().beginTransaction(Connection.TRANSACTION_READ_COMMITTED);
    }

    @Override
    protected void commit() {
        super.commit();
    }

    protected void insertBySpecificId(T object) {
        super.insert(object);
    }

    @Override
    public void insert(T object) {
        if (object.isPersistent()) {
            throw new ApplicationException("Object is already persistent [object=" + object + "].");
        }
        super.insert(object);
    }

    @Override
    protected void rollback() {
        super.rollback();
    }

    protected static <O extends ApplicationEntity> List<O> convertFromRows(TypeOracle<O> typeOracle, List<Row> rows) {
        return typeOracle.convertFromRows(rows);
    }

    static {
        TimeZone.setDefault(TIME_ZONE);
    }
}
