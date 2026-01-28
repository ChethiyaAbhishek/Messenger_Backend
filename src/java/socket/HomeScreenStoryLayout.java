/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import entity.Status;

/**
 *
 * @author hp
 */
public class HomeScreenStoryLayout {

    private int contactId;
    private String contactName;
    private String contactProfileImg;
    private Boolean viewStatus;

    public HomeScreenStoryLayout() {
    }

    public HomeScreenStoryLayout(int contactId, String contactName, String contactProfileImg, Boolean viewStatus) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactProfileImg = contactProfileImg;
        this.viewStatus = viewStatus;
    }

    /**
     * @return the contactId
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * @param contactId the contactId to set
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * @return the contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName the contactName to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return the contactProfileImg
     */
    public String getContactProfileImg() {
        return contactProfileImg;
    }

    /**
     * @param contactProfileImg the contactProfileImg to set
     */
    public void setContactProfileImg(String contactProfileImg) {
        this.contactProfileImg = contactProfileImg;
    }

    /**
     * @return the viewStatus
     */
    public Boolean getViewStatus() {
        return viewStatus;
    }

    /**
     * @param viewStatus the viewStatus to set
     */
    public void setViewStatus(Boolean viewStatus) {
        this.viewStatus = viewStatus;
    }

}
