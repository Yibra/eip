CREATE TABLE IF NOT EXISTS `user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `age` int(10) NOT NULL,
  PRIMARY KEY (`id`)
);
DELETE FROM `user`;

insert into user(id, name, age) values(1,'Freud1',29);
insert into user(id, name, age) values(2,'Freud2',29);
insert into user(id, name, age) values(3,'Freud3',29);
insert into user(id, name, age) values(4,'Freud4',29);
insert into user(id, name, age) values(5,'Freud5',29);
insert into user(id, name, age) values(6,'Freud6',29);
insert into user(id, name, age) values(7,'Freud7',29);