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
@Table(name = "team_msg_status")
public class GroupMsgStatus extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "team_chat_id")
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public GroupMsgStatus() {
    }

    public GroupMsgStatus(Status status, GroupChat groupChat, User user) {
        this.status = status;
        this.groupChat = groupChat;
        this.user = user;
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
     * @return the groupChat
     */
    public GroupChat getGroupChat() {
        return groupChat;
    }

    /**
     * @param groupChat the groupChat to set
     */
    public void setGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
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

}
