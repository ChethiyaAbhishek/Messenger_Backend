/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Chat;
import entity.ContactList;
import entity.GroupChat;
import entity.GroupMsgStatus;
import entity.GroupUsers;
import entity.Groups;
import entity.Status;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
public class ChatService {

    private static final ConcurrentHashMap<Integer, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    public static final String URL = "https://d7fe02729e8a.ngrok-free.app";

    public static void register(int userId, Session session) {
        SESSIONS.put(userId, session);
    }

    public static void unregister(int userId) {
        SESSIONS.remove(userId);
    }

    public static void sendToUser(int userId, Object payload) {

        Session ws = SESSIONS.get(userId);

        if (ws != null && ws.isOpen()) {
            try {
                System.out.println(GSON.toJson(payload));
                ws.getBasicRemote().sendText(GSON.toJson(payload));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<ChatLayout> getChatList(int userId) {

        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria chatListCriteria = session.createCriteria(ContactList.class);
            chatListCriteria.add(Restrictions.eq("user.id", userId));
            chatListCriteria.add(Restrictions.or(
                    Restrictions.eq("status", Status.ACTIVE),
                    Restrictions.eq("status", Status.UNSAVED)));

            List<ContactList> chatList = chatListCriteria.list();

            Map<Integer, ChatLayout> map = new LinkedHashMap<>();

            for (ContactList cList : chatList) {

                User contact = cList.getContact();

                Criteria c1 = session.createCriteria(Chat.class);
                Criterion res1 = Restrictions.and(
                        Restrictions.eq("fromUser.id", userId),
                        Restrictions.eq("toUser.id", contact.getId())
                );

                Criterion res2 = Restrictions.and(
                        Restrictions.eq("fromUser.id", contact.getId()),
                        Restrictions.eq("toUser.id", userId)
                );

                Criterion res3 = Restrictions.or(res1, res2);

                c1.add(res3);
                c1.addOrder(Order.desc("updatedAt"));

                List<Chat> chats = c1.list();

                if (!chats.isEmpty()) {

                    int unread = 0;

                    for (Chat c : chats) {
                        if (c.getFromUser().getId() == contact.getId()
                                && c.getToUser().getId() == userId
                                && c.getStatus().equals(Status.DELIVERED)) {
                            unread += 1;
                        }
                    }

                    if (!map.containsKey(contact.getId())) {

                        String profileImage = UserService.getProfileUrl(contact.getId());

                        map.put(
                                contact.getId(), new ChatLayout(
                                contact.getId(),
                                contact.getFirstName(),
                                contact.getLastName(),
                                contact.getCountryCode(),
                                contact.getContactNo(),
                                chats.get(0).getMessage(),
                                chats.get(0).getCreatedAt(),
                                contact.getUpdatedAt(),
                                unread,
                                profileImage,
                                contact.getStatus().toString(),
                                cList.getStatus().toString(),
                                cList.getLockStatus().toString()
                        ));
                    }
                }
            }

            return new ArrayList<>(map.values());

        } finally {
            session.close();
        }
    }

    public static Map<String, Object> getSearchList(int userId) {

        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Map<Integer, ChatLayout> map = new LinkedHashMap<>();

            Criteria chatListCriteria = session.createCriteria(ContactList.class);
            chatListCriteria.add(Restrictions.eq("user.id", userId));

            List<ContactList> chatList = chatListCriteria.list();

            int unread = 0;
            String lastMsg = null;
            Date lastTimeStamp = null;
            String profileImage = null;

            List currentContactIds = new ArrayList();

            for (ContactList cList : chatList) {

                lastMsg = null;
                lastTimeStamp = null;
                unread = 0;
                profileImage = null;

                User contact = cList.getContact();

                if (!currentContactIds.contains(contact.getId())) {
                    currentContactIds.add(contact.getId());
                }

                if (cList.getLockStatus() != Status.LOCKED && cList.getStatus() != Status.BLOCKED) {

                    Criteria c1 = session.createCriteria(Chat.class);
                    Criterion res1 = Restrictions.and(
                            Restrictions.eq("fromUser.id", userId),
                            Restrictions.eq("toUser.id", contact.getId())
                    );

                    Criterion res2 = Restrictions.and(
                            Restrictions.eq("fromUser.id", contact.getId()),
                            Restrictions.eq("toUser.id", userId)
                    );

                    Criterion res3 = Restrictions.or(res1, res2);

                    c1.add(res3);
                    c1.addOrder(Order.desc("updatedAt"));

                    List<Chat> chats = c1.list();

                    if (!chats.isEmpty()) {
                        for (Chat c : chats) {
                            if (c.getFromUser().getId() == contact.getId()
                                    && c.getToUser().getId() == userId
                                    && c.getStatus().equals(Status.DELIVERED)) {
                                unread += 1;
                            }
                        }

                        lastMsg = chats.get(0).getMessage();
                        lastTimeStamp = chats.get(0).getCreatedAt();
                    }
                }

                if (!map.containsKey(contact.getId())) {

                    if (cList.getStatus() == Status.ACTIVE) {
                        profileImage = UserService.getProfileUrl(contact.getId());
                    }

                    map.put(
                            contact.getId(), new ChatLayout(
                            contact.getId(),
                            contact.getFirstName(),
                            contact.getLastName(),
                            contact.getCountryCode(),
                            contact.getContactNo(),
                            lastMsg,
                            lastTimeStamp,
                            contact.getUpdatedAt(),
                            unread,
                            profileImage,
                            contact.getStatus().toString(),
                            cList.getStatus().toString(),
                            cList.getLockStatus().toString()
                    ));
                }

            }

            Criteria c2 = session.createCriteria(User.class);
            c2.add(Restrictions.ne("id", userId));

            List<User> allUsers = c2.list();

            lastMsg = null;
            lastTimeStamp = null;
            unread = 0;
            profileImage = null;

            for (User user : allUsers) {
                if (!currentContactIds.contains(user.getId())) {
                    if (!map.containsKey(user.getId())) {

                        map.put(
                                user.getId(), new ChatLayout(
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getCountryCode(),
                                user.getContactNo(),
                                lastMsg,
                                lastTimeStamp,
                                user.getUpdatedAt(),
                                unread,
                                profileImage,
                                user.getStatus().toString(),
                                null,
                                null
                        ));
                    }
                }
            }

            List list = new ArrayList<>(map.values());

            Map<String, Object> envelope = new HashMap<>();

            envelope.put("type", "search_list");
            envelope.put("payload", list);

            return envelope;

        } finally {
            session.close();
        }
    }

    public static Map<String, Object> contactListEnvelope(List<ChatLayout> list) {

        Map<String, Object> envelope = new HashMap<>();

        envelope.put("type", "chat_list");
        envelope.put("payload", list);

        return envelope;
    }

    public static List<Chat> getChatHistory(int userId, int contactId) {

        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();

        try {
            Criteria c = session.createCriteria(Chat.class);

            c.add(Restrictions.or(
                    Restrictions.and(
                            Restrictions.eq("fromUser.id", userId),
                            Restrictions.eq("toUser.id", contactId)
                    ),
                    Restrictions.and(
                            Restrictions.eq("fromUser.id", contactId),
                            Restrictions.eq("toUser.id", userId)
                    )
            ));
            c.addOrder(Order.desc("createdAt"));

            List<Chat> list = c.list();

            for (Chat chat : list) {
                if (chat.getFromUser().getId() == contactId
                        && chat.getToUser().getId() == userId
                        && chat.getStatus().equals(Status.DELIVERED)) {
                    chat.setStatus(Status.READ);
                    session.update(chat);
                }
            }

            tr.commit();
            return list;
        } finally {
            session.close();
        }
    }

    public static Map<String, Object> singleChatEnvelope(List<Chat> chats) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "single_chat");
        envelope.put("payload", chats);

        return envelope;
    }

    public static void saveNewMessage(int fromUserId, int toUserId, String message) {

        org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();

        User fromUser = (User) s.get(User.class, fromUserId);
        User toUser = (User) s.get(User.class, toUserId);

        Criteria c1 = s.createCriteria(ContactList.class);
        c1.add(Restrictions.and(
                Restrictions.eq("user", fromUser),
                Restrictions.eq("contact", toUser))
        );

        ContactList fromUsercontact = (ContactList) c1.uniqueResult();

        if (fromUsercontact == null) {

            ContactList newContact = new ContactList();
            newContact.setContact(toUser);
            newContact.setUser(fromUser);
            newContact.setStatus(Status.ACTIVE);
            s.save(newContact);

        }

        Criteria c2 = s.createCriteria(ContactList.class);
        c2.add(Restrictions.and(
                Restrictions.eq("user", toUser),
                Restrictions.eq("contact", fromUser))
        );

        ContactList toUsercontact = (ContactList) c2.uniqueResult();

        if (toUsercontact == null) {

            ContactList newContact = new ContactList();
            newContact.setContact(fromUser);
            newContact.setUser(toUser);
            newContact.setStatus(Status.UNSAVED);
            s.save(newContact);
        }

        boolean saveMsg = true;

        if (toUsercontact != null && toUsercontact.getStatus() == Status.BLOCKED) {
            saveMsg = false;
        }

        if (saveMsg) {

            Chat chat = new Chat();
            chat.setFromUser(fromUser);
            chat.setToUser(toUser);
            chat.setMessage(message);
            chat.setCreatedAt(new Date());
            chat.setUpdatedAt(new Date());
            chat.setFiles("FILE:");
            chat.setMsgType(Status.NORMAL);
            s.save(chat);

            tr.commit();

            Map<String, Object> envelope = new HashMap();
            envelope.put("type", "new_message");
            envelope.put("payload", chat);

            ChatService.sendToUser(chat.getFromUser().getId(), envelope);
            ChatService.sendToUser(chat.getToUser().getId(), envelope);

            List<ChatLayout> fromList = ChatService.getChatList(chat.getFromUser().getId());
            List<ChatLayout> toList = ChatService.getChatList(chat.getToUser().getId());

            Map<String, Object> fromMap = ChatService.contactListEnvelope(fromList);
            Map<String, Object> toMap = ChatService.contactListEnvelope(toList);

            ChatService.sendToUser(chat.getFromUser().getId(), fromMap);
            ChatService.sendToUser(chat.getToUser().getId(), toMap);
        }

        s.close();

    }

    // GROUPS --
    public static Map<String, Object> groupListEnvelope(List<GroupLayout> list) {

        Map<String, Object> envelope = new HashMap<>();

        envelope.put("type", "group_list");
        envelope.put("payload", list);

        return envelope;
    }

    public static List<GroupLayout> getGroupList(int userId) {

        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Criteria groupListCriteria = session.createCriteria(GroupUsers.class);
            groupListCriteria.add(Restrictions.eq("user.id", userId));
            groupListCriteria.add(Restrictions.or(
                    Restrictions.eq("userStatus", Status.ACTIVE),
                    Restrictions.eq("userStatus", Status.REMOVED),
                    Restrictions.eq("userStatus", Status.LEFT)
            ));

            List<GroupUsers> groupList = groupListCriteria.list();

            Map<Integer, GroupLayout> map = new LinkedHashMap<>();

            for (GroupUsers gList : groupList) {

                Groups group = gList.getGroup();

                Criteria uc = session.createCriteria(GroupUsers.class);
                uc.add(Restrictions.eq("group", group));
                uc.add(Restrictions.eq("user.id", userId));
                GroupUsers member = (GroupUsers) uc.uniqueResult();

                Criteria c1 = session.createCriteria(GroupChat.class);
                c1.add(Restrictions.eq("group", group));
                c1.add(Restrictions.ge("createdAt", member.getCreatedAt()));

                if (member.getUserStatus() != Status.ACTIVE) {
                    c1.add(Restrictions.le("createdAt", member.getUpdatedAt()));
                }
                c1.addOrder(Order.desc("createdAt"));

                List<GroupChat> chats = c1.list();

                String lastUserName = null;
                String lastMsg = null;
                Date lastMsgTime = null;
                int unreadCount = 0;

                if (!chats.isEmpty()) {

                    int unread = 0;

                    for (GroupChat msg : chats) {

                        Criteria c2 = session.createCriteria(GroupMsgStatus.class);
                        c2.add(Restrictions.eq("groupChat", msg));
                        c2.add(Restrictions.eq("user.id", userId));
                        c2.add(Restrictions.eq("status", Status.DELIVERED));

                        if (!c2.list().isEmpty()) {
                            unread += 1;
                        }
                    }

                    if (chats.get(0).getFromUser().getId() == userId) {
                        lastUserName = "You";
                    } else {
                        lastUserName = chats.get(0).getFromUser().getFirstName() + " " + chats.get(0).getFromUser().getLastName();
                    }
                    lastMsg = chats.get(0).getMessage();
                    lastMsgTime = chats.get(0).getCreatedAt();
                    unreadCount = unread;

                } else {
                    lastMsg = "New Group";
                    lastMsgTime = group.getCreatedAt();
                }

                if (!map.containsKey(gList.getGroup().getId())) {

                    String profileImage = GroupService.getProfileUrl(gList.getGroup().getId());

                    map.put(group.getId(), new GroupLayout(
                            group.getId(),
                            group.getName(),
                            lastUserName,
                            lastMsg,
                            lastMsgTime,
                            unreadCount,
                            profileImage,
                            gList.getLockedStatus().toString()
                    ));
                }

            }

            return new ArrayList<>(map.values());

        } finally {
            session.close();
        }
    }

    public static List<GroupChatLayout> getGroupChatHistory(int userId, int groupId) {

        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();

        try {

            Criteria uc = session.createCriteria(GroupUsers.class);
            uc.add(Restrictions.eq("group.id", groupId));
            uc.add(Restrictions.eq("user.id", userId));
            GroupUsers member = (GroupUsers) uc.uniqueResult();

            Criteria c = session.createCriteria(GroupChat.class);
            c.add(Restrictions.eq("group.id", groupId));
            c.add(Restrictions.ge("createdAt", member.getCreatedAt()));

            if (member.getUserStatus() != Status.ACTIVE) {
                c.add(Restrictions.le("createdAt", member.getUpdatedAt()));
            }

            c.addOrder(Order.desc("createdAt"));

            List<GroupChat> list = c.list();

            List<GroupChatLayout> returnList = new ArrayList<>();

            Map<Integer, String> profileImgMap = new HashMap<>();
            Map<Integer, String> contactStatusMap = new HashMap<>();

            Criteria c1 = session.createCriteria(GroupUsers.class);
            c1.add(Restrictions.eq("group.id", groupId));
            c1.add(Restrictions.eq("userStatus", Status.ACTIVE));
//            c1.add(Restrictions.ne("user.id", userId));

            List<GroupUsers> usersList = c1.list();

            for (GroupUsers userRow : usersList) {
                if (userRow.getUser().getId() != userId) {

                    Criteria c3 = session.createCriteria(ContactList.class);
                    c3.add(Restrictions.eq("user.id", userId));
                    c3.add(Restrictions.eq("contact.id", userRow.getUser().getId()));

                    ContactList contactList = (ContactList) c3.uniqueResult();
                    if (contactList != null) {

                        contactStatusMap.put(userRow.getUser().getId(), contactList.getStatus().toString());

                        if (contactList.getStatus() == Status.ACTIVE) {
                            String profileImg = UserService.getProfileUrl(userRow.getUser().getId());
                            if (profileImg != null) {
                                profileImgMap.put(userRow.getUser().getId(), profileImg);
                            }
                        }
                    }
                }
            }

            for (GroupChat chat : list) {

                boolean updateStatus = true;

                for (GroupUsers users : usersList) {

                    Criteria c6 = session.createCriteria(GroupMsgStatus.class);
                    c6.add(Restrictions.eq("groupChat", chat));
                    c6.add(Restrictions.eq("user", users.getUser()));
                    c6.add(Restrictions.or(
                            Restrictions.eq("status", Status.DELIVERED),
                            Restrictions.eq("status", Status.READ)
                    ));

                    if (c6.list().isEmpty() && users.getUser().getId() == userId) {
                        GroupMsgStatus msgStatus = new GroupMsgStatus(Status.READ, chat, users.getUser());
                        msgStatus.setCreatedAt(new Date());
                        msgStatus.setUpdatedAt(new Date());
                        session.save(msgStatus);
                    } else if (!c6.list().isEmpty() && users.getUser().getId() == userId) {

                        GroupMsgStatus msgStatus = (GroupMsgStatus) c6.uniqueResult();

                        if (msgStatus.getStatus() == Status.DELIVERED) {
                            msgStatus.setStatus(Status.READ);
                            msgStatus.setUpdatedAt(new Date());
                            session.update(msgStatus);
                        }
                    }

                    if (c6.list().isEmpty() && users.getUser().getId() != userId) {
                        updateStatus = false;
                    } else {

                        GroupMsgStatus msgStatus = (GroupMsgStatus) c6.uniqueResult();

                        if (users.getUser().getId() != userId) {
                            if (msgStatus.getStatus() != Status.READ) {
                                updateStatus = false;
                            }
                        }
                    }
                }

                if (updateStatus) {
                    chat.setStatus(Status.READ);
                    chat.setUpdatedAt(new Date());

                    session.update(chat);
                }

                String profileImg = null;
                String contactStatus = null;

                if (profileImgMap.containsKey(chat.getFromUser().getId())) {
                    profileImg = profileImgMap.get(chat.getFromUser().getId());
                }

                if (contactStatusMap.containsKey(chat.getFromUser().getId())) {
                    contactStatus = contactStatusMap.get(chat.getFromUser().getId());
                }

                GroupChatLayout layout = new GroupChatLayout(chat, profileImg, contactStatus);
                returnList.add(layout);
            }

            tr.commit();
            return returnList;

        } finally {
            session.close();
        }
    }

    public static Map<String, Object> groupChatEnvelope(List<GroupChatLayout> chats) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "group_chat");
        envelope.put("payload", chats);

        return envelope;
    }

    public static void saveNewGroupMessage(int groupId, int userId, String message) {

        org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();

        Groups group = (Groups) s.get(Groups.class, groupId);

        User fromUser = (User) s.get(User.class, userId);

        GroupChat chat = new GroupChat(
                "FILES:",
                message,
                Status.NORMAL,
                group,
                fromUser,
                null);
        chat.setCreatedAt(new Date());
        chat.setUpdatedAt(new Date());

        s.save(chat);

        Criteria c1 = s.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group", group));
        c1.add(Restrictions.eq("userStatus", Status.ACTIVE));

        List<GroupUsers> activeUsersList = c1.list();

        boolean updateMsgStatus = true;

        for (GroupUsers activeUser : activeUsersList) {
//            ChatService.sendToUser(activeUser.getUser().getId(), envelope);
            if (activeUser.getUser().getStatus() == Status.ONLINE) {
                GroupMsgStatus msgStatus = new GroupMsgStatus(Status.DELIVERED, chat, activeUser.getUser());
                msgStatus.setCreatedAt(new Date());
                msgStatus.setUpdatedAt(new Date());
                s.save(msgStatus);
            } else {
                updateMsgStatus = false;
            }
        }

        if (updateMsgStatus) {
            chat.setStatus(Status.DELIVERED);
            chat.setUpdatedAt(new Date());

            s.update(chat);
        }

        tr.commit();

        Map<String, Object> envelope = new HashMap();
        envelope.put("type", "new_group_message");
        envelope.put("payload", chat);

        for (GroupUsers activeUser : activeUsersList) {
            Map<String, Object> envelop = ChatService.groupChatEnvelope(
                    ChatService.getGroupChatHistory(userId, groupId));
            ChatService.sendToUser(activeUser.getUser().getId(), envelop);

            List<GroupLayout> list = ChatService.getGroupList(activeUser.getUser().getId());
            ChatService.sendToUser(activeUser.getUser().getId(), ChatService.groupListEnvelope(list));
        }

        s.close();

    }

    public static Map<String, Object> getAllUsers(int userId, int groupId) {

        org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Map<Integer, ContactProfileLayout> map = new LinkedHashMap<>();

            Criteria contactList = session.createCriteria(ContactList.class);
            contactList.add(Restrictions.eq("user.id", userId));
            contactList.add(Restrictions.eq("status", Status.ACTIVE));

            List<ContactList> cList = contactList.list();

            List savedContacts = new ArrayList();

            for (ContactList contact : cList) {
                if (!savedContacts.contains(contact.getContact().getId())) {
                    savedContacts.add(contact.getContact().getId());
                }
            }

            Criteria c1 = session.createCriteria(User.class);
            List<User> users = c1.list();

            for (User user : users) {

                String profileImg = null;
                Status contactStatus = null;
                String groupStatus = null;

                Criteria c2 = session.createCriteria(GroupUsers.class);
                c2.add(Restrictions.eq("user", user));
                c2.add(Restrictions.eq("group.id", groupId));
                c2.add(Restrictions.eq("userStatus", Status.ACTIVE));

                if (!c2.list().isEmpty()) {
                    GroupUsers groupUser = (GroupUsers) c2.uniqueResult();
                    groupStatus = groupUser.getStatus().toString();
                }

                if (user.getId() == userId) {
                    profileImg = UserService.getProfileUrl(userId);
                }

                if (savedContacts.contains(user.getId())) {
                    profileImg = UserService.getProfileUrl(user.getId());
                    contactStatus = Status.ACTIVE;
                }

                ContactProfileLayout layout = new ContactProfileLayout(
                        user,
                        profileImg,
                        contactStatus,
                        null);
                layout.setGroupStatus(groupStatus);

                if (!map.containsKey(user.getId())) {
                    map.put(user.getId(), layout);
                }
            }

            List list = new ArrayList<>(map.values());

            Map<String, Object> envelope = new HashMap<>();

            envelope.put("type", "all_users");
            envelope.put("payload", list);

            return envelope;

        } finally {
            session.close();
        }
    }

}
