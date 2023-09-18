insert into user_info (id, email_address, password, name, status) values ('00000000-0000-0000-0000-000000000001', 'admin@example.com', /* Admin_1234 */ '{bcrypt}$2a$10$WFLozdm0s7yLiR/rXSij0eFtOjhsFYlXE959R2WQsh5dD28r3EPJG', 'Admin', 1);
insert into user_role (user_id, role) values ('00000000-0000-0000-0000-000000000001', 'ADMIN');
insert into user_role (user_id, role) values ('00000000-0000-0000-0000-000000000001', 'USER');
