/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import java.util.Date;

/**
 *
 * @author hp
 */
public class GroupLayout {

    private int groupId;
    private String groupName;
    private String lastUserName;
    private String lastMsg;
    private Date lastMsgTime;
    private int unreadCount;
    private String groupProfileImage;
    private String createdAt = null;
    private String lockedStatus;

    public GroupLayout() {
    }

    public GroupLayout(int groupId, String groupName, String lastUserName, String lastMsg, Date lastMsgTime, int unreadCount, String groupProfileImage, String lockedStatus) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.lastUserName = lastUserName;
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.unreadCount = unreadCount;
        this.groupProfileImage = groupProfileImage;
        this.lockedStatus = lockedStatus;
    }

 

    /**
     * @return the groupId
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the lastUserName
     */
    public String getLastUserName() {
        return lastUserName;
    }

    /**
     * @param lastUserName the lastUserName to set
     */
    public void setLastUserName(String lastUserName) {
        this.lastUserName = lastUserName;
    }

    /**
     * @return the lastMsg
     */
    public String getLastMsg() {
        return lastMsg;
    }

    /**
     * @param lastMsg the lastMsg to set
     */
    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    /**
     * @return the unreadCount
     */
    public int getUnreadCount() {
        return unreadCount;
    }

    /**
     * @param unreadCount the unreadCount to set
     */
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    /**
     * @return the groupProfileImage
     */
    public String getGroupProfileImage() {
        return groupProfileImage;
    }

    /**
     * @param groupProfileImage the groupProfileImage to set
     */
    public void setGroupProfileImage(String groupProfileImage) {
        this.groupProfileImage = groupProfileImage;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the lastMsgTime
     */
    public Date getLastMsgTime() {
        return lastMsgTime;
    }

    /**
     * @param lastMsgTime the lastMsgTime to set
     */
    public void setLastMsgTime(Date lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    /**
     * @return the createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the lockedStatus
     */
    public String getLockedStatus() {
        return lockedStatus;
    }

    /**
     * @param lockedStatus the lockedStatus to set
     */
    public void setLockedStatus(String lockedStatus) {
        this.lockedStatus = lockedStatus;
    }
}
