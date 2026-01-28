/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import entity.Chat;
import entity.ContactList;
import entity.GroupChat;
import entity.GroupMsgStatus;
import entity.GroupUsers;
import entity.Status;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
public class UserService {
    public Boolean saveProfileImage(int userId, HttpServletRequest request) throws IOException, ServletException {
        Part profileImage = request.getPart("profileImage");
        String appPath = request.getServletContext().getRealPath("");
        String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "profile-images");
        File profileFolder = new File(newPath, String.valueOf(userId));
        if (!profileFolder.exists()) {
            profileFolder.mkdirs();
        }
        File file1 = new File(profileFolder, "profile.png");
        Files.copy(profileImage.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }
    public static String getProfileUrl(int userId) {
        try {
            URL url = new URI("http://localhost:8080/Messenger/profile-images/" + userId + "/profile.png").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            int responseCode = conn.getResponseCode();
            conn.connect();
            String profile;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                profile = ChatService.URL + "/Messenger/profile-images/" + userId + "/profile.png";
            } else {
                profile = null;
            }
            return profile;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateLogInStatus(int userId) {
        updateStatus(userId, Status.ONLINE);
    }
    public static void updateLogOutStatus(int userId) {
        updateStatus(userId, Status.OFFLINE);
    }
    private static void updateStatus(int userId, Status status) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        User fromUser = (User) s.get(User.class, userId);
        fromUser.setStatus(status);
        fromUser.setUpdatedAt(new Date());
        s.update(fromUser);
        s.beginTransaction().commit();
    }
    public static void updateContactChatStatus(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        // Private Chat
        Criteria c1 = s.createCriteria(ContactList.class);
        c1.add(Restrictions.eq("user.id", userId));
        c1.add(Restrictions.or(
                Restrictions.eq("status", Status.ACTIVE),
                Restrictions.eq("status", Status.UNSAVED)));

        List<ContactList> contacts = c1.list();

        for (ContactList contact : contacts) {
            User user = contact.getUser();
            User contactUser = contact.getContact();

            if (user.getStatus().equals(Status.ONLINE)) {

                Criteria c2 = s.createCriteria(Chat.class);
                Criterion res1 = Restrictions.and(
                        Restrictions.eq("fromUser", contactUser),
                        Restrictions.eq("toUser", user),
                        Restrictions.eq("status", Status.SENT));
                c2.add(res1);

                List<Chat> chats = c2.list();

                for (Chat chat : chats) {
                    chat.setStatus(Status.DELIVERED);
                    chat.setUpdatedAt(new Date());
                    s.update(chat);
                }
            }
        }

        // Group Chat
        Criteria c2 = s.createCriteria(GroupUsers.class);
        c2.add(Restrictions.eq("user.id", userId));
        c2.add(Restrictions.eq("userStatus", Status.ACTIVE));
        List<GroupUsers> groups = c2.list();
        for (GroupUsers group : groups) {
            Criteria c3 = s.createCriteria(GroupChat.class);
            c3.add(Restrictions.eq("group", group.getGroup()));
            c3.add(Restrictions.eq("status", Status.SENT));
            c3.add(Restrictions.ge("createdAt", group.getCreatedAt()));
            if (group.getUserStatus() != Status.ACTIVE) {
                c3.add(Restrictions.le("createdAt", group.getUpdatedAt()));
            }
            Criteria c5 = s.createCriteria(GroupUsers.class);
            c5.add(Restrictions.eq("group", group.getGroup()));
            c5.add(Restrictions.eq("userStatus", Status.ACTIVE));
            List<GroupUsers> activeUsers = c5.list();
            List<GroupChat> sentMsgList = c3.list();
            for (GroupChat groupMsg : sentMsgList) {
                boolean updateStatus = true;
                for (GroupUsers users : activeUsers) {
                    Criteria c6 = s.createCriteria(GroupMsgStatus.class);
                    c6.add(Restrictions.eq("groupChat", groupMsg));
                    c6.add(Restrictions.eq("user", users.getUser()));
                    c6.add(Restrictions.or(
                            Restrictions.eq("status", Status.DELIVERED),
                            Restrictions.eq("status", Status.READ)
                    ));
                    if (c6.list().isEmpty() && users.getUser().getId() == userId) {
                        GroupMsgStatus msgStatus = new GroupMsgStatus(
                                Status.DELIVERED, groupMsg, users.getUser());
                        msgStatus.setCreatedAt(new Date());
                        msgStatus.setUpdatedAt(new Date());
                        s.save(msgStatus);
                    }
                    if (c6.list().isEmpty() && users.getUser().getId() != userId) {
                        updateStatus = false;
                    }
                }
                if (updateStatus) {
                    groupMsg.setStatus(Status.DELIVERED);
                    groupMsg.setUpdatedAt(new Date());
                    s.update(groupMsg);
                }
            }
        }
        tr.commit();
        s.close();
    }

    public static Map<String, Object> getContactDetails(int userId, int contactId) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        User contact = (User) s.get(User.class, contactId);

        Criteria c1 = s.createCriteria(ContactList.class);
        c1.add(Restrictions.eq("user.id", contactId));
        c1.add(Restrictions.eq("contact.id", userId));

        String status = null;

        if (!c1.list().isEmpty()) {

            ContactList cl = (ContactList) c1.uniqueResult();

            status = cl.getStatus().toString();
        }

        ChatContactLayout layout = new ChatContactLayout(contact, status);

        s.close();

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "contact_data");
        envelope.put("payload", layout);
        return envelope;
    }

    public static Map<String, Object> getContactProfileDetails(int userId, int contactId) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        User contact = (User) s.get(User.class, contactId);

        String profileImg = UserService.getProfileUrl(contactId);

        Criteria c1 = s.createCriteria(ContactList.class);
        c1.add(Restrictions.eq("user.id", userId));
        c1.add(Restrictions.eq("contact.id", contactId));

        Status contactStatus = null;
        Status contactLockStatus = null;

        if (!c1.list().isEmpty()) {
            ContactList contactList = (ContactList) c1.uniqueResult();

            contactStatus = contactList.getStatus();
            contactLockStatus = contactList.getLockStatus();
        }

        s.close();

        ContactProfileLayout layout = new ContactProfileLayout(
                contact, profileImg, contactStatus, contactLockStatus);

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "contact_profile_details");
        envelope.put("payload", layout);
        return envelope;
    }

    public static Map<String, Object> getUserProfileDetails(int userId) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        User user = (User) s.get(User.class, userId);

        String profileImg = UserService.getProfileUrl(userId);

        s.close();

        ContactProfileLayout layout = new ContactProfileLayout(
                user,
                profileImg,
                null,
                null);

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "profile_details");
        envelope.put("payload", layout);
        return envelope;
    }
}
