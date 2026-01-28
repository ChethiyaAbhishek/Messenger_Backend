/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socket;

import entity.ContactList;
import entity.GroupUsers;
import entity.Groups;
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
import java.util.ArrayList;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
public class GroupService {

    public Boolean saveProfileImage(int groupId, HttpServletRequest request) throws IOException, ServletException {

        Part profileImage = request.getPart("profileImage");
        String appPath = request.getServletContext().getRealPath("");

        String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "profile-images" + File.separator + "groups");

        File profileFolder = new File(newPath, String.valueOf(groupId));
        if (!profileFolder.exists()) {
            profileFolder.mkdirs();
        }

        File file1 = new File(profileFolder, "profile.png");
        Files.copy(profileImage.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return true;
    }

    public static String getProfileUrl(int groupId) {
        try {

            URL url = new URI("http://localhost:8080/Messenger/profile-images/groups/" + groupId + "/profile.png").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            int responseCode = conn.getResponseCode();
            conn.connect();

            String profile;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                profile = ChatService.URL + "/Messenger/profile-images/groups/" + groupId + "/profile.png";
            } else {
                profile = null;
            }

            return profile;

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> getGroupDetails(int userId, int groupId) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Groups group = (Groups) s.get(Groups.class, groupId);

        Criteria c1 = s.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("user.id", userId));
        c1.add(Restrictions.eq("group", group));

        GroupUsers user = (GroupUsers) c1.uniqueResult();

        GroupLayout layout = new GroupLayout(groupId,
                group.getName(),
                null,
                null,
                null,
                0,
                null,
                user.getUserStatus().toString());
        layout.setCreatedAt(group.getCreatedAt().toString());

        s.close();

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "group_data");
        envelope.put("payload", layout);
        return envelope;
    }

    public static Map<String, Object> getGroupProfileDetails(int userId, int groupId) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Groups group = (Groups) s.get(Groups.class, groupId);

        Criteria c1 = s.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group", group));
        List<GroupUsers> groupUsers = c1.list();

        List activeList = new ArrayList<>();

        String lockedStatus = Status.NORMAL.toString();
        String userStatus = Status.ACTIVE.toString();
        String memberStatus = Status.MEMBER.toString();

        for (GroupUsers groupUser : groupUsers) {

            if (groupUser.getUserStatus() == Status.ACTIVE) {
                if (!activeList.contains(groupUser.getUser().getId())) {
                    activeList.add(groupUser.getUser().getId());
                }
            }

            if (groupUser.getUser().getId() == userId) {
                if (groupUser.getLockedStatus() == Status.LOCKED) {
                    lockedStatus = Status.LOCKED.toString();
                }

                if (groupUser.getUserStatus() != Status.ACTIVE) {
                    userStatus = groupUser.getUserStatus().toString();
                }

                if (groupUser.getStatus() == Status.ADMIN) {
                    memberStatus = groupUser.getStatus().toString();
                }
            }
        }

        GroupProfileLayout layout = new GroupProfileLayout(
                group.getName(),
                getProfileUrl(groupId),
                activeList.size(),
                group.getCreatedAt().toString(),
                lockedStatus,
                userStatus,
                memberStatus);

        s.close();

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "group_profile_details");
        envelope.put("payload", layout);
        return envelope;
    }

    public static Map<String, Object> getGroupMembers(int userId, int groupId) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Criteria c1 = s.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group.id", groupId));
        c1.add(Restrictions.eq("userStatus", Status.ACTIVE));
        c1.addOrder(Order.asc("status"));
        List<GroupUsers> groupUsers = c1.list();

        List<ContactProfileLayout> list = new ArrayList<>();

        for (GroupUsers groupUser : groupUsers) {

            User user = groupUser.getUser();

            String profileImg = null;
            Status contactStatus = null;
            Status lockedStatus = null;

            if (user.getId() != userId) {
                Criteria c2 = s.createCriteria(ContactList.class);
                c2.add(Restrictions.eq("user.id", userId));
                c2.add(Restrictions.eq("contact.id", user.getId()));

                ContactList contactList = (ContactList) c2.uniqueResult();

                if (contactList != null) {
                    if (contactList.getStatus() == Status.ACTIVE) {
                        profileImg = UserService.getProfileUrl(contactList.getContact().getId());
                    }

                    contactStatus = contactList.getStatus();
                    lockedStatus = contactList.getLockStatus();
                }

            } else {
                profileImg = UserService.getProfileUrl(userId);
            }

            ContactProfileLayout layout = new ContactProfileLayout(
                    user,
                    profileImg,
                    contactStatus,
                    lockedStatus);
            layout.setGroupStatus(groupUser.getStatus().toString());

            list.add(layout);
        }

        s.close();

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "group_members");
        envelope.put("payload", list);
        return envelope;
    }

    public static void addGroupMember(int groupId, int memberId, int userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();

        Criteria c1 = session.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group.id", groupId));
        c1.add(Restrictions.eq("user.id", memberId));

        if (!c1.list().isEmpty()) {
            GroupUsers member = (GroupUsers) c1.uniqueResult();

            if (member.getUserStatus() == Status.REMOVED || member.getUserStatus() == Status.LEFT || member.getUserStatus() == Status.DELETED) {
                member.setUserStatus(Status.ACTIVE);
                member.setStatus(Status.MEMBER);
                member.setUpdatedAt(new Date());
                session.update(member);
            }
        } else {

            User newMember = (User) session.get(User.class, memberId);
            Groups group = (Groups) session.get(Groups.class, groupId);

            GroupUsers newGroupUser = new GroupUsers(group, newMember, Status.MEMBER);
            newGroupUser.setCreatedAt(new Date());
            newGroupUser.setUpdatedAt(new Date());
            session.save(newGroupUser);
        }

        tr.commit();

        Criteria c4 = session.createCriteria(GroupUsers.class);
        c4.add(Restrictions.eq("group.id", groupId));
        List<GroupUsers> allUsers = c4.list();

        for (GroupUsers groupUser : allUsers) {

            if (groupUser.getStatus() == Status.ADMIN) {
                ChatService.sendToUser(groupUser.getUser().getId(), ChatService.getAllUsers(groupUser.getUser().getId(), groupId));
            }

            ChatService.sendToUser(groupUser.getUser().getId(), GroupService.getGroupProfileDetails(groupUser.getUser().getId(), groupId));
            ChatService.sendToUser(groupUser.getUser().getId(), GroupService.getGroupMembers(groupUser.getUser().getId(), groupId));
        }

        ChatService.sendToUser(userId, ChatService.groupListEnvelope(ChatService.getGroupList(userId)));
        ChatService.sendToUser(userId, ChatService.groupChatEnvelope(ChatService.getGroupChatHistory(userId, groupId)));
        ChatService.sendToUser(userId, ChatService.groupListEnvelope(ChatService.getGroupList(userId)));
        ChatService.sendToUser(userId, GroupService.getGroupDetails(userId, groupId));

        session.close();

    }

    public static void removeGroupMember(int groupId, int memberId, int userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();

        Criteria c1 = session.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group.id", groupId));
        c1.add(Restrictions.eq("user.id", memberId));

        if (!c1.list().isEmpty()) {
            GroupUsers member = (GroupUsers) c1.uniqueResult();

            if (member.getUserStatus() == Status.ACTIVE) {
                member.setUserStatus(Status.REMOVED);
                member.setUpdatedAt(new Date());
                session.update(member);
                tr.commit();

                Criteria c4 = session.createCriteria(GroupUsers.class);
                c4.add(Restrictions.eq("group.id", groupId));
                List<GroupUsers> allUsers = c4.list();

                for (GroupUsers groupUser : allUsers) {
                    ChatService.sendToUser(groupUser.getUser().getId(), GroupService.getGroupProfileDetails(groupUser.getUser().getId(), groupId));
                    ChatService.sendToUser(groupUser.getUser().getId(), GroupService.getGroupMembers(groupUser.getUser().getId(), groupId));
                }

                ChatService.sendToUser(userId, ChatService.groupChatEnvelope(ChatService.getGroupChatHistory(userId, groupId)));
                ChatService.sendToUser(userId, ChatService.groupListEnvelope(ChatService.getGroupList(userId)));
                ChatService.sendToUser(userId, GroupService.getGroupDetails(userId, groupId));
            }
        }

        session.close();
    }

    public static void leaveGroup(int userId, int groupId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();

        Criteria c1 = session.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group.id", groupId));
        c1.add(Restrictions.eq("user.id", userId));

        if (!c1.list().isEmpty()) {
            GroupUsers member = (GroupUsers) c1.uniqueResult();

            if (member.getUserStatus() == Status.ACTIVE) {

                if (member.getStatus() == Status.ADMIN) {
                    Criteria c2 = session.createCriteria(GroupUsers.class);
                    c2.add(Restrictions.eq("group.id", groupId));
                    c2.add(Restrictions.ne("user.id", userId));
                    c2.add(Restrictions.eq("status", Status.ADMIN));

                    if (c2.list().isEmpty()) {

                        Criteria c3 = session.createCriteria(GroupUsers.class);
                        c3.add(Restrictions.eq("group.id", groupId));
                        c3.add(Restrictions.ne("user.id", userId));
                        c3.addOrder(Order.asc("createdAt"));

                        GroupUsers newAdmin = (GroupUsers) c3.list().get(0);
                        newAdmin.setStatus(Status.ADMIN);
                        newAdmin.setUpdatedAt(new Date());
                        session.update(newAdmin);
                    }
                }

                member.setUserStatus(Status.LEFT);
                member.setStatus(Status.MEMBER);
                member.setUpdatedAt(new Date());
                session.update(member);
                tr.commit();

                Criteria c4 = session.createCriteria(GroupUsers.class);
                c4.add(Restrictions.eq("group.id", groupId));
                List<GroupUsers> allUsers = c4.list();

                for (GroupUsers groupUser : allUsers) {
                    ChatService.sendToUser(groupUser.getUser().getId(), GroupService.getGroupProfileDetails(groupUser.getUser().getId(), groupId));
                    ChatService.sendToUser(groupUser.getUser().getId(), GroupService.getGroupMembers(groupUser.getUser().getId(), groupId));
                }

                ChatService.sendToUser(userId, ChatService.groupChatEnvelope(ChatService.getGroupChatHistory(userId, groupId)));
                ChatService.sendToUser(userId, ChatService.groupListEnvelope(ChatService.getGroupList(userId)));
                ChatService.sendToUser(userId, GroupService.getGroupDetails(userId, groupId));
            }
        }

        session.close();
    }

    public static void deleteGroup(int userId, int groupId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();

        Criteria c1 = session.createCriteria(GroupUsers.class);
        c1.add(Restrictions.eq("group.id", groupId));
        c1.add(Restrictions.eq("user.id", userId));

        if (!c1.list().isEmpty()) {
            GroupUsers member = (GroupUsers) c1.uniqueResult();

            member.setUserStatus(Status.DELETED);
            member.setUpdatedAt(new Date());
            session.update(member);
            tr.commit();

            ChatService.sendToUser(userId, ChatService.groupListEnvelope(ChatService.getGroupList(userId)));
        }
        session.close();
    }
}
