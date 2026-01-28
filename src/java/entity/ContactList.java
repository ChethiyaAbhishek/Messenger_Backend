/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author hp
 */
@Entity
@Table(name = "contact_list")
public class ContactList implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private User contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "lock_status", length = 30)
    private Status lockStatus = Status.NORMAL;

    public ContactList() {
    }

    public ContactList(User user, User contact, Status status) {
        this.user = user;
        this.contact = contact;
        this.status = status;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
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
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the lockStatus
     */
    public Status getLockStatus() {
        return lockStatus;
    }

    /**
     * @param lockStatus the lockStatus to set
     */
    public void setLockStatus(Status lockStatus) {
        this.lockStatus = lockStatus;
    }

}
