/*
 Navicat Premium Data Transfer

 Source Server         : contract
 Source Server Type    : MySQL
 Source Server Version : 50741 (5.7.41-log)
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50741 (5.7.41-log)
 File Encoding         : 65001

 Date: 26/05/2024 11:24:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `register_time` datetime NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'user1', 'pass1234', 'user1@example.com', '2023-04-24 08:00:00', 'USER');
INSERT INTO `users` VALUES (2, '234', '234', '234', '2024-04-05 13:40:16', 'USER');
INSERT INTO `users` VALUES (3, 'user3', 'pass9101', 'user3@example.com', '2023-04-24 10:00:00', 'USER');
INSERT INTO `users` VALUES (4, 'user4', 'pass1121', 'user4@example.com', '2023-04-24 11:00:00', 'USER');
INSERT INTO `users` VALUES (5, 'user5', 'pass3141', 'user5@example.com', '2023-04-24 12:00:00', 'ADMIN');
INSERT INTO `users` VALUES (6, 'user6', 'pass5161', 'user6@example.com', '2023-04-24 13:00:00', 'USER');
INSERT INTO `users` VALUES (7, 'user7', 'pass7181', 'user7@example.com', '2023-04-24 14:00:00', 'USER');
INSERT INTO `users` VALUES (8, 'user8', 'pass9202', 'user8@example.com', '2023-04-24 15:00:00', 'ADMIN');
INSERT INTO `users` VALUES (9, 'user9', 'pass1222', 'user9@example.com', '2023-04-24 16:00:00', 'USER');
INSERT INTO `users` VALUES (10, 'user10', 'pass3242', 'user10@example.com', '2023-04-24 17:00:00', 'USER');
INSERT INTO `users` VALUES (11, 'user11', 'pass5262', 'user11@example.com', '2023-04-24 18:00:00', 'ADMIN');
INSERT INTO `users` VALUES (12, 'user12', 'pass7282', 'user12@example.com', '2023-04-24 19:00:00', 'USER');
INSERT INTO `users` VALUES (13, 'user13', 'pass9303', 'user13@example.com', '2023-04-24 20:00:00', 'USER');
INSERT INTO `users` VALUES (14, 'user14', 'pass1323', 'user14@example.com', '2023-04-24 21:00:00', 'ADMIN');
INSERT INTO `users` VALUES (15, 'user15', 'pass3343', 'user15@example.com', '2023-04-24 22:00:00', 'USER');
INSERT INTO `users` VALUES (16, 'user16', 'pass5363', 'user16@example.com', '2023-04-24 23:00:00', 'USER');
INSERT INTO `users` VALUES (17, 'user17', 'pass7383', 'user17@example.com', '2023-04-25 00:00:00', 'ADMIN');
INSERT INTO `users` VALUES (18, 'user18', 'pass9404', 'user18@example.com', '2023-04-25 01:00:00', 'USER');
INSERT INTO `users` VALUES (19, 'user19', 'pass1424', 'user19@example.com', '2023-04-25 02:00:00', 'USER');
INSERT INTO `users` VALUES (20, 'user20', 'pass3444', 'user20@example.com', '2023-04-25 03:00:00', 'ADMIN');

SET FOREIGN_KEY_CHECKS = 1;
