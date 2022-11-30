CREATE TABLE `auth_demo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `field1` int(12) DEFAULT NULL,
  `field2` varchar(255) DEFAULT NULL,
  `node` smallint(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `node` (`node`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;