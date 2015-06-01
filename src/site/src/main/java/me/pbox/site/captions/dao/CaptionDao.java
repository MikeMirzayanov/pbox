/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions.dao;

import me.pbox.site.captions.model.Caption;

import java.util.List;

public interface CaptionDao {
    String shaHex(String s);

    Caption find(long id);

    Caption find(String shortcutSha1, String locale);

    List<Caption> findAll();

    void save(Caption caption);

    void insert(Caption caption);

    void clearCache();

    void deleteByShortcutSha1(String shortcutSha1);

    List<Caption> findByShortcutSha1(String shortcutSha1);

    List<Caption> findByValue(String value);
}
