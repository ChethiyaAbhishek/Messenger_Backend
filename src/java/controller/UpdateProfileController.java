/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import org.hibernate.criterion.Restrictions;
import socket.ChatService;
import socket.UserService;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
@MultipartConfig
@WebServlet(name = "UpdateProfileController", urlPatterns = {"/UpdateProfileController"})
public class UpdateProfileController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Part profileImage = request.getPart("profileImage");
        String userId = request.getParameter("userId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        if (firstName.isEmpty()) {
            responseObject.addProperty("msg", "First name is required.");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("msg", "Last name is required.");
        } else if (userId.isEmpty()) {
            responseObject.addProperty("msg", "An error occured.");
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = s.beginTransaction();

            User user = (User) s.get(User.class, Integer.parseInt(userId));

            if (user == null) {
                responseObject.addProperty("msg", "An error occured.");
            } else {

                if (!user.getFirstName().equals(firstName) || !user.getLastName().equals(lastName)) {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    s.update(user);
                }

                if (profileImage != null) {
                    boolean profileStatus = new UserService().saveProfileImage(Integer.parseInt(userId), request);
                    if (profileStatus) {
                        responseObject.addProperty("status", true);
                    } else {
                        responseObject.addProperty("message", "Profile image uploading failed.");
                    }
                } else {
                    responseObject.addProperty("status", true);
                }

                tr.commit();
                s.close();

                ChatService.sendToUser(Integer.parseInt(userId), UserService.getUserProfileDetails(Integer.parseInt(userId)));

            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
