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
@Table(name = "team_chat")
public class GroupChat extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "files", nullable = false, columnDefinition = "LONGTEXT")
    private String files;

    @Column(name = "message", columnDefinition = "LONGTEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "msg_type", length = 30, nullable = false)
    private Status msgType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private Status status = Status.SENT;

    @ManyToOne
    @JoinColumn(name = "chat_groups_id")
    private Groups group;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = true)
    private User toUser;

    public GroupChat() {
    }

    public GroupChat(String files, String message, Status msgType, Groups group, User fromUser, User toUser) {
        this.files = files;
        this.message = message;
        this.msgType = msgType;
        this.group = group;
        this.fromUser = fromUser;
        this.toUser = toUser;
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
     * @return the files
     */
    public String getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(String files) {
        this.files = files;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the msgType
     */
    public Status getMsgType() {
        return msgType;
    }

    /**
     * @param msgType the msgType to set
     */
    public void setMsgType(Status msgType) {
        this.msgType = msgType;
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
     * @return the fromUser
     */
    public User getFromUser() {
        return fromUser;
    }

    /**
     * @param fromUser the fromUser to set
     */
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    /**
     * @return the toUser
     */
    public User getToUser() {
        return toUser;
    }

    /**
     * @param toUser the toUser to set
     */
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

}
