/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions.model;

import org.jacuzzi.mapping.Id;

import java.util.Date;

/**
 * Stores information who, when and how
 * changed caption.
 *
 * @author Mike Mirzayanov
 */
public class CaptionChange {
    @Id
    private long id;

    private long captionId;

    private long userId;

    private String userHandle;

    private String previousValue;

    private String newValue;

    private Date creationTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCaptionId() {
        return captionId;
    }

    public void setCaptionId(long captionId) {
        this.captionId = captionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
