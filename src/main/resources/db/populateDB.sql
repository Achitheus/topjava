DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, calories, description)
VALUES (100000, '2022-06-22 19:10+03', 1000, 'user havka'),
       (100000, '2022-06-24 10:10+03', 2000, 'user big havka'),
       (100000, '2022-06-27 15:10', 1400, 'another user havka'),
       (100001, '2022-06-22 19:10:25+03', 1000, 'admin havka'),
       (100001, '2022-06-23 10:10:25+03', 2000, 'admin big havka');