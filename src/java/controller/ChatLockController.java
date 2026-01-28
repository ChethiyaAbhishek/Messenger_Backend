/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.ContactList;
import entity.GroupUsers;
import entity.Status;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import socket.ChatService;
import socket.GroupLayout;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
@WebServlet(name = "ChatLockController", urlPatterns = {"/ChatLockController"})
public class ChatLockController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String userId = request.getParameter("userId");
        String contactId = request.getParameter("contactId");
        
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        
        if (userId.isEmpty() || contactId.isEmpty()) {
            responseObject.addProperty("msg", "An error occured. Please try again shortly.");
        } else {
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            
            Criteria c1 = s.createCriteria(ContactList.class);
            c1.add(Restrictions.eq("user.id", Integer.parseInt(userId)));
            c1.add(Restrictions.eq("contact.id", Integer.parseInt(contactId)));
            
            ContactList contactList = (ContactList) c1.uniqueResult();
            
            if (contactList != null) {
                
                if (contactList.getLockStatus() == Status.NORMAL) {
                    contactList.setLockStatus(Status.LOCKED);
                } else if (contactList.getLockStatus() == Status.LOCKED) {
                    contactList.setLockStatus(Status.NORMAL);
                }
                s.update(contactList);
            } else {
                
                User user = (User) s.get(User.class, Integer.parseInt(userId));
                User contact = (User) s.get(User.class, Integer.parseInt(contactId));
                
                ContactList newContact = new ContactList();
                newContact.setUser(user);
                newContact.setContact(contact);
                newContact.setStatus(Status.UNSAVED);
                newContact.setLockStatus(Status.LOCKED);
                s.save(newContact);
                
            }
            
            s.beginTransaction().commit();
            responseObject.addProperty("status", Boolean.TRUE);
            
            ChatService.sendToUser(Integer.parseInt(userId),
                    ChatService.contactListEnvelope(
                            ChatService.getChatList(Integer.parseInt(userId))));
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Gson gson = new Gson();
        
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
        
        String userId = requestObject.get("userId").getAsString();
        String groupId = requestObject.get("groupId").getAsString();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        
        if (userId.isEmpty() || groupId.isEmpty()) {
            responseObject.addProperty("msg", "An error occured. Please try again shortly.");
        } else {
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            
            Criteria c1 = s.createCriteria(GroupUsers.class);
            c1.add(Restrictions.eq("user.id", Integer.parseInt(userId)));
            c1.add(Restrictions.eq("group.id", Integer.parseInt(groupId)));
            c1.add(Restrictions.eq("userStatus", Status.ACTIVE));
            
            GroupUsers groupUser = (GroupUsers) c1.uniqueResult();
            
            if (groupUser != null) {
                
                if (groupUser.getLockedStatus() == Status.NORMAL) {
                    groupUser.setLockedStatus(Status.LOCKED);
                } else if (groupUser.getLockedStatus() == Status.LOCKED) {
                    groupUser.setLockedStatus(Status.NORMAL);
                }
                s.update(groupUser);
                s.beginTransaction().commit();
                responseObject.addProperty("status", Boolean.TRUE);
                
                List<GroupLayout> list = ChatService.getGroupList(Integer.parseInt(userId));
                ChatService.sendToUser(Integer.parseInt(userId), ChatService.groupListEnvelope(list));
            } else {
                responseObject.addProperty("msg", "An error occured. Please try again shortly.");
            }
            
            s.close();
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
        
    }
    
}
