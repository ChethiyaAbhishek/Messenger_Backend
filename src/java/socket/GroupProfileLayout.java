/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

/**
 *
 * @author hp
 */
public class GroupProfileLayout {

    private String groupName;
    private String profileImg;
    private int memberCount;
    private String createdAt;
    private String lockedStatus;
    private String userStatus;
    private String memberStatus;

    public GroupProfileLayout() {
    }

    public GroupProfileLayout(String groupName, String profileImg, int memberCount, String createdAt, String lockedStatus, String userStatus, String memberStatus) {
        this.groupName = groupName;
        this.profileImg = profileImg;
        this.memberCount = memberCount;
        this.createdAt = createdAt;
        this.lockedStatus = lockedStatus;
        this.userStatus = userStatus;
        this.memberStatus = memberStatus;
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
     * @return the memberCount
     */
    public int getMemberCount() {
        return memberCount;
    }

    /**
     * @param memberCount the memberCount to set
     */
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
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

    /**
     * @return the userStatus
     */
    public String getUserStatus() {
        return userStatus;
    }

    /**
     * @param userStatus the userStatus to set
     */
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * @return the profileImg
     */
    public String getProfileImg() {
        return profileImg;
    }

    /**
     * @param profileImg the profileImg to set
     */
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    /**
     * @return the memberStatus
     */
    public String getMemberStatus() {
        return memberStatus;
    }

    /**
     * @param memberStatus the memberStatus to set
     */
    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }
}
