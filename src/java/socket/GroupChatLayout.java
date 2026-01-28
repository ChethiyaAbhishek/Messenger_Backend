/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import entity.GroupChat;

/**
 *
 * @author hp
 */
public class GroupChatLayout {

    private GroupChat chat;
    private String contactProfileImg;
    private String contactStatus;

    public GroupChatLayout() {
    }

    public GroupChatLayout(GroupChat chat, String contactProfileImg, String contactStatus) {
        this.chat = chat;
        this.contactProfileImg = contactProfileImg;
        this.contactStatus = contactStatus;
    }

    /**
     * @return the chat
     */
    public GroupChat getChat() {
        return chat;
    }

    /**
     * @param chat the chat to set
     */
    public void setChat(GroupChat chat) {
        this.chat = chat;
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
     * @return the contactStatus
     */
    public String getContactStatus() {
        return contactStatus;
    }

    /**
     * @param contactStatus the contactStatus to set
     */
    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }

}
