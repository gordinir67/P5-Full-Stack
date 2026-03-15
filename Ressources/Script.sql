CREATE DATABASE MDD;
USE MDD;

CREATE TABLE `Users` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `Themes` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(50) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `Articles` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` INT NOT NULL,
  `theme_id` INT NOT NULL
);

CREATE TABLE `Comments` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `description` VARCHAR(2000) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` INT NOT NULL,
  `article_id` INT NOT NULL
);

CREATE TABLE `Subscriptions` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `theme_id` INT NOT NULL
);

CREATE UNIQUE INDEX `Users_index` ON `Users` (`email`);
CREATE UNIQUE INDEX `Subscriptions_index` ON `Subscriptions` (`user_id`, `theme_id`);

ALTER TABLE `Articles`
ADD CONSTRAINT `fk_articles_user`
FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `Articles`
ADD CONSTRAINT `fk_articles_theme`
FOREIGN KEY (`theme_id`) REFERENCES `Themes` (`id`)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `Comments`
ADD CONSTRAINT `fk_comments_user`
FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `Comments`
ADD CONSTRAINT `fk_comments_article`
FOREIGN KEY (`article_id`) REFERENCES `Articles` (`id`)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `Subscriptions`
ADD CONSTRAINT `fk_subscriptions_user`
FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `Subscriptions`
ADD CONSTRAINT `fk_subscriptions_theme`
FOREIGN KEY (`theme_id`) REFERENCES `Themes` (`id`)
ON DELETE CASCADE ON UPDATE CASCADE;

INSERT INTO `Users` (`email`, `name`, `password`)
VALUES 
('test@test.com', 'test', '$2a$10$wH7y2h5Q9Z2E5bYz3kGx3e6g4Yp7pY3yq4rQpC6Fv2XxQnZ0G9z4K'),
('test2@test.com', 'test2', '$2a$10$wH7y2h5Q9Z2E5bYz3kGx3e6g4Yp7pY3yq4rQpC6Fv2XxQnZ0G9z4K');

INSERT INTO `Themes` (`title`, `description`)
VALUES
('Java','Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.'),
('PHP', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.'),
('Angular', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.'),
('DevOps', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.');

INSERT INTO `Articles` (`title`, `description`, `user_id`, `theme_id`)
VALUES
('Introduction à Java', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 1, 1),
('Introduction à PHP', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 1, 2),
('Angular Components', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 2, 3),
('CI/CD', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 2, 4);

INSERT INTO `Comments` (`description`, `user_id`, `article_id`)
VALUES
('Super article, très clair !', 2, 1),
('Merci pour les exemples', 2, 2),
('Angular devient plus compréhensible', 1, 3),
('Le DevOps enfin expliqué simplement', 1, 4);

INSERT INTO `Subscriptions` (`user_id`, `theme_id`)
VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 4);
