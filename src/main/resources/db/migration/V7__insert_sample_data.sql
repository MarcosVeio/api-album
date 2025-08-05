INSERT INTO tb_users (username, password) VALUES
    ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'), -- password: admin123
    ('user1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'), -- password: admin123
    ('user2', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'); -- password: admin123

INSERT INTO tb_users_roles (user_id, role_id)
SELECT u.user_id, r.role_id 
FROM tb_users u, tb_roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

INSERT INTO tb_users_roles (user_id, role_id) 
SELECT u.user_id, r.role_id 
FROM tb_users u, tb_roles r 
WHERE u.username IN ('user1', 'user2') AND r.name = 'BASIC';

INSERT INTO tb_album (user_id, title, description)
SELECT u.user_id, 'My First Album', 'This is my first photo album'
FROM tb_users u 
WHERE u.username = 'user1';

INSERT INTO tb_album (user_id, title, description) 
SELECT u.user_id, 'Vacation Photos', 'Photos from my vacation'
FROM tb_users u 
WHERE u.username = 'user2'; 