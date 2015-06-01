/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions.dao;

import me.pbox.site.captions.model.CaptionChange;

/**
 * DAO for CaptionChange.
 *
 * @author Mike Mirzayanov
 */
public interface CaptionChangeDao {
    void insert(CaptionChange captionChange);
}
