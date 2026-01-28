/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import socket.UserService;

/**
 *
 * @author hp
 */
@MultipartConfig
@WebServlet(name = "UserProfileController", urlPatterns = {"/UserProfileController"})
public class UserProfileController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part profileImage = request.getPart("profileImage");
        int userId = Integer.parseInt(request.getParameter("userId"));

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (userId == 0) {
            responseObject.addProperty("msg", "User not found.");
        } else if (profileImage == null) {
            responseObject.addProperty("msg", "Upload your profile image.");
        } else {
            boolean profileStatus = new UserService().saveProfileImage(userId, request);
            if (profileStatus) {
                responseObject.addProperty("status", true);
            } else {
                responseObject.addProperty("message", "Profile image uploading failed.");
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
