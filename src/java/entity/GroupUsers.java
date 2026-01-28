/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

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
@Table(name = "team_users")
public class GroupUsers extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "chat_groups_id")
    private Groups group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", length = 30, nullable = false)
    private Status userStatus = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "locked_status", length = 30, nullable = false)
    private Status lockedStatus = Status.NORMAL;

    public GroupUsers() {
    }

    public GroupUsers(Groups group, User user, Status status) {
        this.group = group;
        this.user = user;
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
     * @return the group
     */
    public Groups getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(Groups group) {
        this.group = group;
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
     * @return the userStatus
     */
    public Status getUserStatus() {
        return userStatus;
    }

    /**
     * @param userStatus the userStatus to set
     */
    public void setUserStatus(Status userStatus) {
        this.userStatus = userStatus;
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
     * @return the lockedStatus
     */
    public Status getLockedStatus() {
        return lockedStatus;
    }

    /**
     * @param lockedStatus the lockedStatus to set
     */
    public void setLockedStatus(Status lockedStatus) {
        this.lockedStatus = lockedStatus;
    }

}
