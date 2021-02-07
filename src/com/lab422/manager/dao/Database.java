package com.lab422.manager.dao;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Database {

    public static Connection getConnection() throws SQLException {
        try {
            return InitialContext.<DataSource>doLookup("java:comp/env/database").getConnection();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Device getDeviceStateAndUpdateTimestamp(int deviceId) throws SQLException {
        Device device = new Device();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from devices where id = ?;")) {
                statement.setInt(1, deviceId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    device.set(resultSet);
                    if (resultSet.next()) {
                        return null;
                    }
                }
            }
            try (PreparedStatement statement = connection.prepareStatement("update devices set time = ? where id = ?;")) {
                statement.setTimestamp(1, new Timestamp(new Date().getTime()));
                statement.setInt(2, deviceId);
                statement.executeUpdate();
            }
        }
        return device;
    }

    public static User getUserState(String username) throws SQLException {
        User user = new User();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from users where username = ?;")) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    user.set(resultSet);
                    if (resultSet.next()) {
                        return null;
                    }
                }
            }
        }
        return user;
    }

    public static List<Device> selectDeviceByLocation(String like) throws SQLException {
        List<Device> devices = new LinkedList<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from devices where location like ? and time > ? and (user is null or end < ?);")) {
                statement.setString(1, like);
                Calendar time = Calendar.getInstance();
                time.add(Calendar.MINUTE, -10);
                statement.setTimestamp(2, new Timestamp(time.getTime().getTime()));
                statement.setTimestamp(3, new Timestamp(new Date().getTime()));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Device device = new Device();
                        device.set(resultSet);
                        devices.add(device);
                    }
                }
            }
        }
        return devices;
    }

    public static final int SUCCESS = 0;
    public static final int NO_USER = 1;
    public static final int NO_DEVICE = 2;

    public static int useDevice(String username, String password, int deviceId, long start, long end) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            int flag = SUCCESS;
            long now = new Date().getTime();

            Calendar time = Calendar.getInstance();
            time.add(Calendar.MINUTE, -10);
            long last = time.getTime().getTime();

            User user = new User();
            Device device = new Device();

            try (PreparedStatement statement = connection.prepareStatement("select * from devices where id = ? for update;")) {
                statement.setInt(1, deviceId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        flag = NO_DEVICE;
                    }
                    device.set(resultSet);
                    if (resultSet.next()) {
                        flag = NO_DEVICE;
                    }
                }
            }

            if (flag != SUCCESS) {
                connection.rollback();
                return flag;
            }

            try (PreparedStatement statement = connection.prepareStatement("select * from users where username = ? for update;")) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        flag = NO_USER;
                    }
                    user.set(resultSet);
                    if (resultSet.next()) {
                        flag = NO_USER;
                    }
                }
            }


            if (flag != SUCCESS) {
                connection.rollback();
                return flag;
            }

            if (device.time < last) {
                flag = NO_DEVICE;
            }

            if (device.user != null && device.end > now) {
                flag = NO_DEVICE;
            }

            for (User.DeviceUse deviceUse : user.devices) {
                if (deviceUse.type == device.type) {
                    flag = NO_USER;
                }
            }

            if (flag != SUCCESS) {
                connection.rollback();
                return flag;
            }

            user.devices.add(new User.DeviceUse(deviceId, device.type, end));
            String devicesStr = user.devicesToString();

            try (PreparedStatement statement = connection.prepareStatement("update users set devices = ? where username = ?;")) {
                statement.setString(1, devicesStr);
                statement.setString(2, username);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("update devices set user = ?, start = ?, end = ? where id = ?;")) {
                statement.setString(1, username);
                statement.setTimestamp(2, new Timestamp(start));
                statement.setTimestamp(3, new Timestamp(end));
                statement.setInt(4, deviceId);
                statement.executeUpdate();
            }
            connection.commit();
            return flag;
        }
    }

}
