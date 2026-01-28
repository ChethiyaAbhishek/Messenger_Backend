/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.GroupUsers;
import entity.Groups;
import entity.Status;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.hibernate.Transaction;
import socket.GroupService;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
@MultipartConfig
@WebServlet(name = "GroupCreationController", urlPatterns = {"/GroupCreationController"})
public class GroupCreationController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String groupName = request.getParameter("groupName");
        String userId = request.getParameter("userId");
        Part profileImage = request.getPart("profileImage");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (groupName.isEmpty()) {
            responseObject.addProperty("msg", "Group name is required.");
        } else if (userId.isEmpty()) {
            responseObject.addProperty("msg", "An error occured. Please try again later.");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = session.beginTransaction();

            Groups newGroup = new Groups();
            newGroup.setName(groupName);
            newGroup.setCreatedAt(new Date());
            newGroup.setUpdatedAt(new Date());
            Integer groupId = (Integer) session.save(newGroup);

            User user = (User) session.get(User.class, Integer.parseInt(userId));

            GroupUsers groupAdmin = new GroupUsers();
            groupAdmin.setCreatedAt(new Date());
            groupAdmin.setUpdatedAt(new Date());
            groupAdmin.setStatus(Status.ADMIN);
            groupAdmin.setUserStatus(Status.ACTIVE);
            groupAdmin.setGroup(newGroup);
            groupAdmin.setUser(user);
            groupAdmin.setLockedStatus(Status.NORMAL);
            session.save(groupAdmin);

            tr.commit();

            if (profileImage != null) {
                boolean profileStatus = new GroupService().saveProfileImage(groupId, request);
                if (profileStatus) {
                    responseObject.addProperty("status", true);
                } else {
                    responseObject.addProperty("message", "Profile image uploading failed.");
                }
            }

            responseObject.addProperty("status", true);
            responseObject.addProperty("groupId", groupId);

            session.close();

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
