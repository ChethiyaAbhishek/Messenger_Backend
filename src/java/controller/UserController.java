/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.ContactList;
import entity.Status;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import socket.ChatService;
import socket.UserService;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
@MultipartConfig
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = requestObject.get("firstName").getAsString();
        String lastName = requestObject.get("lastName").getAsString();
        String countryCode = requestObject.get("countryCode").getAsString();
        String contactNo = requestObject.get("contactNo").getAsString();

        if (firstName.isEmpty()) {
            responseObject.addProperty("msg", "First name is required.");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("msg", "Last name is required.");
        } else if (countryCode.isEmpty()) {
            responseObject.addProperty("msg", "Country code is required.");
        } else if (contactNo.isEmpty()) {
            responseObject.addProperty("msg", "Contact No is required.");
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = s.createCriteria(User.class);
            c1.add(Restrictions.eq("countryCode", countryCode));
            c1.add(Restrictions.eq("contactNo", contactNo));

            User user = (User) c1.uniqueResult();

            if (user != null) {
                responseObject.addProperty("msg", "An error occured.");
            } else {

                user = new User(firstName, lastName, countryCode, contactNo);
                user.setCreatedAt(new Date());
                user.setUpdatedAt(new Date());

                int id = (int) s.save(user);
                s.beginTransaction().commit();
                s.close();

                responseObject.add("user", gson.toJsonTree(user));
                responseObject.addProperty("status", Boolean.TRUE);

            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

                if (contactList.getStatus() == Status.UNSAVED) {
                    contactList.setStatus(Status.ACTIVE);
                } else if (contactList.getStatus() == Status.ACTIVE) {
                    contactList.setStatus(Status.UNSAVED);
                }

            } else {

                User user = (User) s.get(User.class, Integer.parseInt(userId));
                User contact = (User) s.get(User.class, Integer.parseInt(contactId));

                ContactList newContact = new ContactList(user, contact, Status.ACTIVE);
                s.save(newContact);

            }

            s.update(contactList);
            s.beginTransaction().commit();

            responseObject.addProperty("status", Boolean.TRUE);

            ChatService.sendToUser(Integer.parseInt(userId),
                    ChatService.contactListEnvelope(
                            ChatService.getChatList(Integer.parseInt(userId))));
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
