package com.example.httplearningapi.servlets;

import com.example.httplearningapi.model.user.User;
import com.example.httplearningapi.controller.UserController;
import com.example.httplearningapi.util.JsonSerializationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@WebServlet(name = "Users Servlet", urlPatterns = {"/users", "/users/*"})
public class UsersServlet extends HttpServlet {

    private static Predicate<User> createPredicateForFilteringUsersByQueryParams(String id,
                                                                                 String name,
                                                                                 String email,
                                                                                 String phone,
                                                                                 String website) {
        return user ->
                (id == null || id.equals(String.valueOf(user.getId()))) &&
                        (name == null || name.equals(user.getName())) &&
                        (email == null || email.equals(user.getEmail())) &&
                        (phone == null || phone.equals(user.getPhone())) &&
                        (website == null || website.equals(user.getWebsite()));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo != null) {
                String[] pathSegments = pathInfo.substring(1).split("/");
                int userId = Integer.parseInt(pathSegments[0]);

                if (pathSegments.length > 1) {
                    req.setAttribute("userId", userId);
                    // forward to another servlet
                } else {
                    UserController userController = new UserController();
                    User requestedUser = userController.getUserById(userId).orElseThrow();
                    JsonSerializationUtil.serializeObjectToJsonStream(requestedUser, resp.getWriter());
                }
            } else {
                UserController userController = new UserController();
                List<User> users = userController.getUsers();

                String queryString = req.getQueryString();
                if (queryString != null) {

                    Predicate<User> predicateForFiltering = createPredicateForFilteringUsersByQueryParams(
                            req.getParameter("id"),
                            req.getParameter("name"),
                            req.getParameter("email"),
                            req.getParameter("phone"),
                            req.getParameter("website")
                    );

                    users = users.stream()
                            .filter(predicateForFiltering)
                            .collect(Collectors.toList());
                }

                JsonSerializationUtil.serializeObjectToJsonStream(users, resp.getWriter());
            }
        } catch (Exception e) {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}