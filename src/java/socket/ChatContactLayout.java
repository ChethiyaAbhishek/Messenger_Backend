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
public class ChatContactLayout {

    private User contact;
    private String status;

    public ChatContactLayout() {
    }

    public ChatContactLayout(User contact, String status) {
        this.contact = contact;
        this.status = status;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
