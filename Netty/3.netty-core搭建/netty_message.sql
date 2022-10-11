/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : netty_message

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 04/11/2021 00:20:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for msg_ws_content
-- ----------------------------
DROP TABLE IF EXISTS `msg_ws_content`;
CREATE TABLE `msg_ws_content` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sender` bigint(20) DEFAULT NULL COMMENT '发送者ID',
  `topic` varchar(255) DEFAULT NULL COMMENT '主题',
  `type` int(1) DEFAULT NULL COMMENT '消息类型: 1 文本; 2 图片; 3 链接; 4 图文',
  `title` varchar(255) DEFAULT NULL COMMENT '消息标题',
  `content` varchar(1024) DEFAULT NULL COMMENT '消息内容',
  `additional` varchar(2048) DEFAULT NULL COMMENT '扩展信息,JSON格式',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COMMENT='websocket消息内容表';

-- ----------------------------
-- Table structure for msg_ws_receiver
-- ----------------------------
DROP TABLE IF EXISTS `msg_ws_receiver`;
CREATE TABLE `msg_ws_receiver` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` bigint(20) DEFAULT NULL COMMENT '内容ID',
  `receiver` bigint(20) DEFAULT NULL COMMENT '接收者ID',
  `is_read` int(1) DEFAULT NULL COMMENT '已读标识: 0 已读, 1 未读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COMMENT='websocket消息接收者表';

SET FOREIGN_KEY_CHECKS = 1;
