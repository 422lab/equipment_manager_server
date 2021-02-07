package com.lab422.manager.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class User {

    public static class DeviceUse {

        public DeviceUse(int device, int type, long time) {
            this.device = device;
            this.type = type;
            this.time = time;
        }

        public DeviceUse(String str) {
            int p = str.indexOf('@');
            int q = str.indexOf(':');
            device = Integer.parseInt(str.substring(0, p));
            type = Integer.parseInt(str.substring(p + 1, q));
            time = Long.parseLong(str.substring(q + 1));
        }

        public int device;
        public int type;
        public long time;

        @Override
        public String toString() {
            return device + "@" + type + ":" + time + ";";
        }
    }

    public User() {

    }

    /**
     * 用户名.
     */
    public String username;

    /**
     * 密码
     */
    public String password;

    /**
     * 已预约设备.
     */
    public List<DeviceUse> devices;

    public void set(ResultSet resultSet) throws SQLException {
        username = resultSet.getString("username");
        password = resultSet.getString("password");
        String devicesStr = resultSet.getString("devices");
        long now = new Date().getTime();
        List<DeviceUse> devicesList = new LinkedList<>();
        for (String i : devicesStr.split(";")) {
            if (!i.isEmpty()) {
                DeviceUse deviceUse = new DeviceUse(i);
                if (deviceUse.time > now) {
                    devicesList.add(deviceUse);
                }
            }
        }
        devices = devicesList;
    }

    public String devicesToString() {
        StringBuilder sb = new StringBuilder();
        for (DeviceUse deviceUse : devices) {
            sb.append(deviceUse);
        }
        return sb.toString();
    }

/*
    public void set(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt("id");
        username = resultSet.getString("username");
        password = resultSet.getString("password");
        String use = resultSet.getString("useDevices");
        useDevices = new LinkedList<>();
        for (String i : use.split(";")) {
            if (!i.isEmpty()) {
                useDevices.add(Integer.parseInt(i));
            }
        }
    }

    public void update(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update devices set username = ?, password = ?, useDevices = ? where id = ?;");
        statement.setString(1, username);
        statement.setString(2, password);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : useDevices) {
            stringBuilder.append(i).append(";");
        }
        statement.setString(3, stringBuilder.toString());
        statement.setInt(4, id);
        statement.executeUpdate();
        statement.close();
    }

    public void use(int device) {
        try {
            Connection connection = Database.getConnection();

            // 禁用自动提交
            connection.setAutoCommit(false);

            // 先查询用户使用情况
            PreparedStatement getUser = connection.prepareStatement("select * from users where id = ? for updata;");
            ResultSet resultSet = getUser.executeQuery();
            User user = new User();
            user.set(resultSet);


            resultSet.close();
            getUser.close();

            // 查询设备占用情况
            PreparedStatement getDevice = connection.prepareStatement("select * from devices where id = ? for update;");
            ResultSet deResult = getDevice.executeQuery();
            Device deviceDao = new Device();
            deviceDao.set(deResult);


            deResult.close();
            getDevice.close();

            // 设置设备使用者

            deviceDao.userId = id;
            deviceDao.update(connection);


            // 设置用户使用

            //user.useDevices.add(device);
            //user.update(connection);

            // 提交更改
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
*/

}
