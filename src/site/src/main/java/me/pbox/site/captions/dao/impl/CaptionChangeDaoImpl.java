/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions.dao.impl;

import com.google.inject.Singleton;
import me.pbox.site.captions.dao.CaptionChangeDao;
import me.pbox.site.captions.database.CaptionDataSource;
import me.pbox.site.captions.model.CaptionChange;
import org.jacuzzi.core.GenericDaoImpl;

/**
 * Database DAO for CaptionChange.
 *
 * @author Mike Mirzayanov
 */
@Singleton
public class CaptionChangeDaoImpl extends GenericDaoImpl<CaptionChange, Long> implements CaptionChangeDao {
    CaptionChangeDaoImpl() {
        super(CaptionDataSource.getInstance());
    }

    @Override
    public void insert(CaptionChange captionChange) {
        captionChange.setCreationTime(findNow());
        super.insert(captionChange);
    }
}
