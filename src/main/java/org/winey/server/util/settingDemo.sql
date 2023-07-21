-- 레벨업 시나리오
USE winey;
SET @user = 1;
DELETE FROM goal WHERE user_id = @user;
DELETE FROM feed WHERE user_id = @user;
UPDATE user SET user_level = 'COMMONER' WHERE user_id = @user;


