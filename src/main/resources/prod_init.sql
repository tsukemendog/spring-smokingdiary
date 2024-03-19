ALTER DATABASE nosmoking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE challenge_level CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE daily CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE members CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE progress CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE progress_log CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE resources CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

AWS RDS 인코딩 설정