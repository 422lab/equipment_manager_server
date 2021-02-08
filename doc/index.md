# 设备管理服务器端

## 项目建立
项目使用`Tomcat`作为服务器，`MySQL`作为数据库
选择Java项目
![新建项目](img/1.png)

## 调试运行
先添加`Tomcat`
![调试运行](img/2.png)

## 配置`Tomcat`的数据库连接池
项目结构中
![添加配置文件](img/3.png)
键入数据库信息

添加`MySQL`数据库`JDBC`驱动库

## 导入`Tomcat`API
![导入Api](img/4.png)

## 配置数据库

先设置时区
```sql
# mysql -u root -p
# 密码
set global time_zone='+8:00';
```

在IDEA中添加数据源
![IDEA添加数据源](img/5.png)

创建数据库
```sql
create schema manager_database;
```

```sql

--
-- Table structure for table `devices`
--

DROP TABLE IF EXISTS `devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devices` (
  `id` int NOT NULL,
  `location` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `type` int NOT NULL DEFAULT '0',
  `user` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `start` timestamp NOT NULL DEFAULT '2000-12-31 16:00:00',
  `end` timestamp NOT NULL DEFAULT '2000-12-31 16:00:00',
  `time` timestamp NOT NULL DEFAULT '2000-12-31 16:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `devices_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

```

```sql

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `devices` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`username`),
  UNIQUE KEY `users_username_uindex` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

```