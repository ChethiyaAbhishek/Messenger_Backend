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
public class ChatLayout {

    private int friendId;
    private String friendFirstName;
    private String friendLastName;
    private String contactCountryCode;
    private String contactNumber;
    private String lastMessage;
    private Date lastMsgTimeStamp;
    private Date lastTimeStamp;
    private int unreadCount;
    private String profileImage;
    private String friendStatus;
    private String savedStatus;
    private String lockedStatus;

    public ChatLayout() {
    }

    public ChatLayout(int friendId, String friendFirstName, String friendLastName, String contactCountryCode, String contactNumber, String lastMessage, Date lastMsgTimeStamp, Date lastTimeStamp, int unreadCount, String profileImage, String friendStatus, String savedStatus, String lockedStatus) {
        this.friendId = friendId;
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.contactCountryCode = contactCountryCode;
        this.contactNumber = contactNumber;
        this.lastMessage = lastMessage;
        this.lastMsgTimeStamp = lastMsgTimeStamp;
        this.lastTimeStamp = lastTimeStamp;
        this.unreadCount = unreadCount;
        this.profileImage = profileImage;
        this.friendStatus = friendStatus;
        this.savedStatus = savedStatus;
        this.lockedStatus = lockedStatus;
    }

    /**
     * @return the friendId
     */
    public int getFriendId() {
        return friendId;
    }

    /**
     * @param friendId the friendId to set
     */
    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    /**
     * @return the friendFirstName
     */
    public String getFriendFirstName() {
        return friendFirstName;
    }

    /**
     * @param friendFirstName the friendFirstName to set
     */
    public void setFriendFirstName(String friendFirstName) {
        this.friendFirstName = friendFirstName;
    }

    /**
     * @return the friendLastName
     */
    public String getFriendLastName() {
        return friendLastName;
    }

    /**
     * @param friendLastName the friendLastName to set
     */
    public void setFriendLastName(String friendLastName) {
        this.friendLastName = friendLastName;
    }

    /**
     * @return the lastMessage
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * @param lastMessage the lastMessage to set
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * @return the lastTimeStamp
     */
    public Date getLastTimeStamp() {
        return lastTimeStamp;
    }

    /**
     * @param lastTimeStamp the lastTimeStamp to set
     */
    public void setLastTimeStamp(Date lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
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
     * @return the profileImage
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * @param profileImage the profileImage to set
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * @return the friendStatus
     */
    public String getFriendStatus() {
        return friendStatus;
    }

    /**
     * @param friendStatus the friendStatus to set
     */
    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    /**
     * @return the lastMsgTimeStamp
     */
    public Date getLastMsgTimeStamp() {
        return lastMsgTimeStamp;
    }

    /**
     * @param lastMsgTimeStamp the lastMsgTimeStamp to set
     */
    public void setLastMsgTimeStamp(Date lastMsgTimeStamp) {
        this.lastMsgTimeStamp = lastMsgTimeStamp;
    }

    /**
     * @return the savedStatus
     */
    public String getSavedStatus() {
        return savedStatus;
    }

    /**
     * @param savedStatus the savedStatus to set
     */
    public void setSavedStatus(String savedStatus) {
        this.savedStatus = savedStatus;
    }

    /**
     * @return the contactCountryCode
     */
    public String getContactCountryCode() {
        return contactCountryCode;
    }

    /**
     * @param contactCountryCode the contactCountryCode to set
     */
    public void setContactCountryCode(String contactCountryCode) {
        this.contactCountryCode = contactCountryCode;
    }

    /**
     * @return the contactNumber
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * @param contactNumber the contactNumber to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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
