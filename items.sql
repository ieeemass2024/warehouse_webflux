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

 Date: 26/05/2024 11:24:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for items
-- ----------------------------
DROP TABLE IF EXISTS `items`;
CREATE TABLE `items`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `stock` int(11) NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of items
-- ----------------------------
INSERT INTO `items` VALUES (1, '物品1', '漫画书', '一本书', 12, 234.00, '2024-05-23 15:20:19', '2024-05-23 15:20:21');
INSERT INTO `items` VALUES (6, '物品6', '工具', '多功能手动工具', 100, 250.00, '2023-04-01 13:00:00', '2023-04-24 13:00:00');
INSERT INTO `items` VALUES (7, '物品7', '电子产品', '智能手机', 60, 2000.00, '2023-04-01 14:00:00', '2023-04-24 14:00:00');
INSERT INTO `items` VALUES (8, '物品8', '书籍', '科幻小说', 75, 120.00, '2023-04-01 15:00:00', '2023-04-24 15:00:00');
INSERT INTO `items` VALUES (10, '物品10', '装饰品', '现代艺术雕塑', 15, 1100.00, '2023-04-01 17:00:00', '2023-04-24 17:00:00');
INSERT INTO `items` VALUES (11, '物品11', '工具', '电动钻', 90, 700.00, '2023-04-01 18:00:00', '2023-04-24 18:00:00');
INSERT INTO `items` VALUES (12, '物品12', '电子产品', '高清电视', 70, 3000.00, '2023-04-01 19:00:00', '2023-04-24 19:00:00');
INSERT INTO `items` VALUES (13, '物品13', '书籍', '历史学术书籍', 60, 200.00, '2023-04-01 20:00:00', '2023-04-24 20:00:00');
INSERT INTO `items` VALUES (14, '物品14', '家具', '儿童书桌', 35, 450.00, '2023-04-01 21:00:00', '2023-04-24 21:00:00');
INSERT INTO `items` VALUES (15, '物品15', '装饰品', '花瓶', 55, 180.00, '2023-04-01 22:00:00', '2023-04-24 22:00:00');
INSERT INTO `items` VALUES (16, '物品16', '工具', '梯子', 40, 300.00, '2023-04-01 23:00:00', '2023-04-24 23:00:00');
INSERT INTO `items` VALUES (18, '徐泽成', '天才', 'TEST', 150, 150.00, '2024-05-25 16:00:07', '2024-05-25 16:00:33');

SET FOREIGN_KEY_CHECKS = 1;
