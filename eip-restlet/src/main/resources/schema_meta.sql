CREATE TABLE IF NOT EXISTS `resource_sql` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `resource` varchar(30) NOT NULL,
  `select_statement` varchar(500) NOT NULL,
  `limits` varchar(200),
  PRIMARY KEY (`id`)
);
DELETE FROM `resource_sql`;