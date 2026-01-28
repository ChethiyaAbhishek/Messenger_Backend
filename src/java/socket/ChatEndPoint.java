/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.GroupChat;
import java.util.List;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author hp
 */
@ServerEndpoint(value = "/ChatEndPoint")
public class ChatEndPoint {

    private static final Gson GSON = new Gson();
    private int userId;

    @OnOpen
    public void onOpen(Session session) {
        String query = session.getQueryString();
        if (query != null && query.startsWith("userId=")) {
            userId = Integer.parseInt(query.substring("userId=".length()));
            ChatService.register(userId, session);
            UserService.updateLogInStatus(userId);
            UserService.updateContactChatStatus(userId);
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (userId > 0) { // userId != null
            ChatService.unregister(userId);
            UserService.updateLogOutStatus(userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (userId > 0) {
            UserService.updateLogOutStatus(userId);
        }
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {

            Map<String, Object> map = ChatEndPoint.GSON.fromJson(msg, Map.class);
            String type = map.get("type").toString();

            switch (type) {
                case "PING": {
                    JsonObject responseObject = new JsonObject();
                    responseObject.addProperty("type", "PONG");
                    ChatService.sendToUser(userId, responseObject);
                    break;
                }
                case "get_chat_list": {
                    ChatService.sendToUser(userId,
                            ChatService.contactListEnvelope(
                                    ChatService.getChatList(userId)));
                    break;
                }
                case "get_search_list": {
                    ChatService.sendToUser(userId, ChatService.getSearchList(userId));
                    break;
                }
                case "get_single_chat": {
                    int contactId = (int) ((double) map.get("contactId"));

                    List<Chat> chats = ChatService.getChatHistory(userId, contactId);
                    Map<String, Object> envelop = ChatService.singleChatEnvelope(chats);
                    ChatService.sendToUser(userId, envelop);
                    ChatService.sendToUser(userId,
                            ChatService.contactListEnvelope(ChatService.getChatList(userId)));
                    break;
                }
                case "get_contact_data": {
                    int contactId = (int) ((double) map.get("contactId"));
                    Map<String, Object> envelope = UserService.getContactDetails(userId, contactId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "send_message": {
                    int toUserId = (int) ((double) map.get("toUserId"));
                    String message = String.valueOf(map.get("message"));
                    ChatService.saveNewMessage(userId, toUserId, message);
                    break;
                }
                case "get_contact_profile_details": {
                    int contactId = (int) ((double) map.get("contactId"));
                    ChatService.sendToUser(userId, UserService.getContactProfileDetails(userId, contactId));
                    break;
                }
                case "get_group_list": {

                    List<GroupLayout> list = ChatService.getGroupList(userId);

                    ChatService.sendToUser(userId,
                            ChatService.groupListEnvelope(list));
                    break;
                }
                case "get_group_chat": {
                    int groupId = (int) ((double) map.get("groupId"));

                    Map<String, Object> envelop = ChatService.groupChatEnvelope(
                            ChatService.getGroupChatHistory(userId, groupId));
                    ChatService.sendToUser(userId, envelop);

                    ChatService.sendToUser(userId,
                            ChatService.groupListEnvelope(ChatService.getGroupList(userId)));
                    break;
                }
                case "get_group_data": {
                    int groupId = (int) ((double) map.get("groupId"));

                    Map<String, Object> envelope = GroupService.getGroupDetails(userId, groupId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "send_group_message": {
                    int groupId = (int) ((double) map.get("groupId"));
                    String message = String.valueOf(map.get("message"));
                    ChatService.saveNewGroupMessage(groupId, userId, message);
                    break;
                }
                case "get_group_profile_details": {
                    int groupId = (int) ((double) map.get("groupId"));
                    Map<String, Object> envelope = GroupService.getGroupProfileDetails(userId, groupId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "get_group_members": {
                    int groupId = (int) ((double) map.get("groupId"));
                    Map<String, Object> envelope = GroupService.getGroupMembers(userId, groupId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "get_all_users": {
                    int groupId = (int) ((double) map.get("groupId"));
                    ChatService.sendToUser(userId, ChatService.getAllUsers(userId, groupId));
                    break;
                }
                case "add_group_member": {
                    int groupId = (int) ((double) map.get("groupId"));
                    int memberId = (int) ((double) map.get("memberId"));
                    GroupService.addGroupMember(groupId, memberId, userId);
                    break;
                }
                case "remove_group_member": {
                    int groupId = (int) ((double) map.get("groupId"));
                    int memberId = (int) ((double) map.get("memberId"));
                    GroupService.removeGroupMember(groupId, memberId, userId);
                    break;
                }
                case "leave_group": {
                    int groupId = (int) ((double) map.get("groupId"));
                    GroupService.leaveGroup(userId, groupId);
                    break;
                }
                case "delete_group": {
                    int groupId = (int) ((double) map.get("groupId"));
                    GroupService.deleteGroup(userId, groupId);
                    break;
                }
                case "get_profile_details": {
                    ChatService.sendToUser(userId, UserService.getUserProfileDetails(userId));
                    break;
                }
                default: {
                    System.out.println("Unknown msg type: " + type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
