/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import entity.Status;
import entity.User;

/**
 *
 * @author hp
 */
public class ContactProfileLayout {

    private User contact;
    private String profileImage;
    private Status contactStatus;
    private Status contactLockStatus;
    private String groupStatus = null;

    public ContactProfileLayout() {
    }

    public ContactProfileLayout(User contact, String profileImage, Status contactStatus, Status contactLockStatus) {
        this.contact = contact;
        this.profileImage = profileImage;
        this.contactStatus = contactStatus;
        this.contactLockStatus = contactLockStatus;
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
     * @return the contactStatus
     */
    public Status getContactStatus() {
        return contactStatus;
    }

    /**
     * @param contactStatus the contactStatus to set
     */
    public void setContactStatus(Status contactStatus) {
        this.contactStatus = contactStatus;
    }

    /**
     * @return the contact
     */
    public User getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(User contact) {
        this.contact = contact;
    }

    /**
     * @return the contactLockStatus
     */
    public Status getContactLockStatus() {
        return contactLockStatus;
    }

    /**
     * @param contactLockStatus the contactLockStatus to set
     */
    public void setContactLockStatus(Status contactLockStatus) {
        this.contactLockStatus = contactLockStatus;
    }

    /**
     * @return the groupStatus
     */
    public String getGroupStatus() {
        return groupStatus;
    }

    /**
     * @param groupStatus the groupStatus to set
     */
    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }
}
