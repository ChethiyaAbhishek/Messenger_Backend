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
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import socket.ChatService;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
@WebServlet(name = "ContactBlockController", urlPatterns = {"/ContactBlockController"})
public class ContactBlockController extends HttpServlet {

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

                if (contactList.getStatus() == Status.BLOCKED) {
                    contactList.setStatus(Status.UNSAVED);
                } else if (contactList.getStatus() == Status.UNSAVED || contactList.getStatus() == Status.ACTIVE) {
                    contactList.setStatus(Status.BLOCKED);
                }
                s.update(contactList);
                s.beginTransaction().commit();
                responseObject.addProperty("status", Boolean.TRUE);

                ChatService.sendToUser(Integer.parseInt(userId),
                        ChatService.contactListEnvelope(
                                ChatService.getChatList(Integer.parseInt(userId))));
            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
