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
import java.util.Date;

public class GetDeviceStateServlet extends HttpServlet {

    public static final int SUCCESS = 0;
    public static final int NEED_DEVICE_ID = 1;
    public static final int DEVICE_ID_FORMAT = 2;
    public static final int NU_SUCH_DEVICE = 3;

    public static final int NO_USE = 0;
    public static final int WAIT = 1;
    public static final int ACTIVE = 2;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log(req.getMethod());
        try {
            String idStr = req.getParameter("id");
            if (idStr == null) {
                resp.getWriter().print("{\"code\":" + NEED_DEVICE_ID + "}");
                return;
            }
            int idInt = Integer.parseInt(idStr);
            Device device = Database.getDeviceStateAndUpdateTimestamp(idInt);
            if (device == null) {
                resp.getWriter().print("{\"code\":" + NU_SUCH_DEVICE + "}");
                return;
            }
            int state = NO_USE;
            if (device.user != null) {
                long now = new Date().getTime();
                if (device.start > now) {
                    state = WAIT;
                } else if (device.end > now) {
                    state = ACTIVE;
                } else {
                    device.user = null;
                }
            }
            resp.getWriter().print("{\"code\":" + SUCCESS
                    + ",\"id\":" + device.id
                    + ",\"location\":" + JsonHelper.json(device.location)
                    + ",\"type\":" + device.type
                    + ",\"state\":" + state
                    + ",\"serverTime\":" + new Date().getTime()
                    +
                    (state == NO_USE ? "" : ",\"user\":" + JsonHelper.json(device.user)
                            + ",\"start\":" + device.start
                            + ",\"end\":" + device.end)
                    + "}");
        } catch (NumberFormatException e) {
            resp.getWriter().print("{\"code\":" + DEVICE_ID_FORMAT + "}");
        } catch (SQLException e) {
            log("SQLError", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

}
