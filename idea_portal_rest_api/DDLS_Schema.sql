
--Creates table comments

CREATE TABLE `comments` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `comment_date` date NOT NULL,
  `comment_value` longtext NOT NULL,
  `idea_idea_id` bigint DEFAULT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FKchngunxdvs0us307jjl9koj33` (`idea_idea_id`),
  KEY `FK575rn648djycglw0wgo1adq31` (`user_user_id`),
  CONSTRAINT `FK575rn648djycglw0wgo1adq31` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKchngunxdvs0us307jjl9koj33` FOREIGN KEY (`idea_idea_id`) REFERENCES `ideas` (`idea_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


--Creates table ideas

CREATE TABLE `ideas` (
  `idea_id` bigint NOT NULL AUTO_INCREMENT,
  `idea_date` date NOT NULL,
  `idea_description` text NOT NULL,
  `idea_docxurl` longtext,
  `idea_name` text NOT NULL,
  `idea_ppturl` longtext,
  `theme_theme_id` bigint DEFAULT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`idea_id`),
  KEY `FKd293pkw6f7b40n0ctftchmi44` (`theme_theme_id`),
  KEY `FK8ml8ao9m8cv8o4u8dppufj1j1` (`user_user_id`),
  CONSTRAINT `FK8ml8ao9m8cv8o4u8dppufj1j1` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKd293pkw6f7b40n0ctftchmi44` FOREIGN KEY (`theme_theme_id`) REFERENCES `themes` (`theme_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


--Creates table likes

CREATE TABLE `likes` (
  `like_id` bigint NOT NULL AUTO_INCREMENT,
  `like_date` date NOT NULL,
  `like_value` int DEFAULT NULL,
  `idea_idea_id` bigint DEFAULT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`like_id`),
  KEY `FK7ygmvg0109ftxssqlu611e6il` (`idea_idea_id`),
  KEY `FKew6bvl26y0uy7x1vgidr91mv0` (`user_user_id`),
  CONSTRAINT `FK7ygmvg0109ftxssqlu611e6il` FOREIGN KEY (`idea_idea_id`) REFERENCES `ideas` (`idea_id`),
  CONSTRAINT `FKew6bvl26y0uy7x1vgidr91mv0` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


-- Creates table participation_responses

CREATE TABLE `participation_responses` (
  `response_id` bigint NOT NULL AUTO_INCREMENT,
  `participation_date` date NOT NULL,
  `idea_idea_id` bigint DEFAULT NULL,
  `theme_theme_id` bigint DEFAULT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`response_id`),
  KEY `FKehlsvaujj2tj7mv6i5rknnbhn` (`idea_idea_id`),
  KEY `FK2mud667cpv1a5qw0wmhw3j179` (`theme_theme_id`),
  KEY `FK3y5lcmgvtpsjyq89ijfrlew21` (`user_user_id`),
  CONSTRAINT `FK2mud667cpv1a5qw0wmhw3j179` FOREIGN KEY (`theme_theme_id`) REFERENCES `themes` (`theme_id`),
  CONSTRAINT `FK3y5lcmgvtpsjyq89ijfrlew21` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKehlsvaujj2tj7mv6i5rknnbhn` FOREIGN KEY (`idea_idea_id`) REFERENCES `ideas` (`idea_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

-- Creates table password_log

CREATE TABLE `password_log` (
  `password_logid` bigint NOT NULL AUTO_INCREMENT,
  `new_password` text NOT NULL,
  `old_password` text NOT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`password_logid`),
  KEY `FK9sh78pgowccbd02wfldxqjsgv` (`user_user_id`),
  CONSTRAINT `FK9sh78pgowccbd02wfldxqjsgv` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

-- Creates table password_reset_token

CREATE TABLE `password_reset_token` (
  `password_reset_id` bigint NOT NULL AUTO_INCREMENT,
  `token` text NOT NULL,
  `token_expiry` bigint NOT NULL,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`password_reset_id`),
  KEY `FKd9dlb18d0isoxolmswxgxsyok` (`user_user_id`),
  CONSTRAINT `FKd9dlb18d0isoxolmswxgxsyok` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

-- Creates table roles

CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

-- Creates table themes

CREATE TABLE `themes` (
  `theme_id` bigint NOT NULL AUTO_INCREMENT,
  `theme_date` date NOT NULL,
  `theme_description` text NOT NULL,
  `theme_docxurl` longtext,
  `theme_name` text NOT NULL,
  `theme_ppturl` longtext,
  `theme_videourl` longtext,
  `user_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`theme_id`),
  KEY `FK7ehr87t8j6lwi14ed1986akj7` (`user_user_id`),
  CONSTRAINT `FK7ehr87t8j6lwi14ed1986akj7` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

-- Creates table user

CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `user_company` text,
  `user_email` text NOT NULL,
  `user_name` text NOT NULL,
  `user_password` text NOT NULL,
  `role_role_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FK4keqlw3ucfmfsbeu8r1ijdppf` (`role_role_id`),
  CONSTRAINT `FK4keqlw3ucfmfsbeu8r1ijdppf` FOREIGN KEY (`role_role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
