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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author hp
 */
@WebServlet(name = "UserVerification", urlPatterns = {"/UserVerification"})
public class UserVerification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        String countryCode = requestObject.get("countryCode").getAsString();
        String contactNo = requestObject.get("contactNo").getAsString();

        if (countryCode.isEmpty()) {
            responseObject.addProperty("msg", "Country code is required.");
        } else if (contactNo.isEmpty()) {
            responseObject.addProperty("msg", "Contact number is required.");
        } else {

            Session ses = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = ses.createCriteria(User.class);
            c1.add(Restrictions.eq("countryCode", countryCode));
            c1.add(Restrictions.eq("contactNo", contactNo));

            if (!c1.list().isEmpty()) {
                User user = (User) c1.uniqueResult();

                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.add("user", gson.toJsonTree(user));
                responseObject.addProperty("msg", "1");

            } else {

                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.addProperty("msg", "2");

            }

            ses.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
