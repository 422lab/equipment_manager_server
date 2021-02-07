package com.lab422.manager.servlet;

import com.lab422.manager.dao.Database;
import com.lab422.manager.dao.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class UserLoginServlet extends HttpServlet {

    public static final int SUCCESS = 0;
    public static final int NEED_USERNAME = 1;
    public static final int NEED_PASSWORD = 2;
    public static final int NO_USERNAME = 3;
    public static final int NO_PASSWORD = 4;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        if (username == null) {
            resp.getWriter().print("{\"code\":" + NEED_USERNAME + "}");
            return;
        }
        String password = req.getParameter("password");
        if (password == null) {
            resp.getWriter().print("{\"code\":" + NEED_PASSWORD + "}");
            return;
        }
        try {
            User user = Database.getUserState(username);
            if (user == null) {
                resp.getWriter().print("{\"code\":" + NO_USERNAME + "}");
            } else if (user.password.equals(password)) {
                session.setAttribute("username", username);
                session.setAttribute("password", password);
                resp.getWriter().print("{\"code\":" + SUCCESS + "}");
            } else {
                resp.getWriter().print("{\"code\":" + NO_PASSWORD + "}");
            }
        } catch (SQLException e) {
            log("SQLError", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
