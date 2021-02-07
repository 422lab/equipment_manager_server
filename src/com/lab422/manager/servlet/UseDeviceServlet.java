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

public class UseDeviceServlet extends HttpServlet {

    public static final int SUCCESS = 0;
    public static final int NO_PARAMETER = 1;
    public static final int NO_LOGIN = 2;
    public static final int NO_USE = 3;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int deviceId;
        long start;
        long end;
        try {
            deviceId = Integer.parseInt(req.getParameter("device"));
            start = Long.parseLong(req.getParameter("start"));
            end = Long.parseLong(req.getParameter("end"));
        } catch (NullPointerException | NumberFormatException e) {
            resp.getWriter().print("{\"code\":" + NO_PARAMETER + "}");
            return;
        }
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("username");
        if (username == null || password == null) {
            resp.getWriter().print("{\"code\":" + NO_LOGIN + "}");
            return;
        }
        try {
            int used = Database.useDevice(username, password, deviceId, start, end);
            if (used == Database.SUCCESS) {
                resp.getWriter().print("{\"code\":" + SUCCESS + "}");
            } else {
                resp.getWriter().print("{\"code\":" + NO_USE
                        + ",\"error\":" + used + "}");
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
