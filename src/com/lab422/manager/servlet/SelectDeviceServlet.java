package com.lab422.manager.servlet;

import com.lab422.manager.JsonHelper;
import com.lab422.manager.dao.Database;
import com.lab422.manager.dao.Device;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SelectDeviceServlet extends HttpServlet {

    public static final int SUCCESS = 0;
    public static final int NEED_LIKE = 1;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String like = req.getParameter("like");
        if (like == null) {
            resp.getWriter().print("{\"code\":" + NEED_LIKE + "}");
            return;
        }
        try {
            List<Device> devices = Database.selectDeviceByLocation(like);
            StringBuilder sb = new StringBuilder();
            sb.append("{\"code\":");
            sb.append(SUCCESS);
            sb.append(",\"devices\":[");
            boolean first = true;
            for (Device device : devices) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append("{\"id\":");
                sb.append(device.id);
                sb.append(",\"location\":");
                sb.append(JsonHelper.json(device.location));
                sb.append(",\"type\":");
                sb.append(device.type);
                sb.append("}");
            }
            sb.append("]}");
            resp.getWriter().print(sb.toString());
        } catch (SQLException e) {
            log("SQLError", e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

}
