package com.lab422.manager.dao;

import java.sql.*;
import java.util.Date;
import java.util.Timer;

public class Device {

    public Device() {

    }

    /**
     * 设备标识.
     */
    public int id;

    /**
     * 设备地址.
     */
    public String location;

    /**
     * 设备类型.
     */
    public int type;

    /**
     * 使用者.
     */
    public String user;

    /**
     * 预约开始时间.
     */
    public long start;

    /**
     * 预约结束时间.
     */
    public long end;

    /**
     * 上次询问时间.
     */
    public long time;

    public void set(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt("id");
        location = resultSet.getString("location");
        type = resultSet.getInt("type");
        user = resultSet.getString("user");
        start = resultSet.getTimestamp("start").getTime();
        end = resultSet.getTimestamp("end").getTime();
        time = resultSet.getTimestamp("time").getTime();
    }


}
