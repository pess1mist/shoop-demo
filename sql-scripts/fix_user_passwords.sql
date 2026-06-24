-- Fix user passwords to MD5
USE test;

-- Update all user passwords to MD5 hash
UPDATE sys_user SET password = MD5('123456');

-- Verify the fix
SELECT username, password FROM sys_user;

-- Show MD5 value for reference
SELECT MD5('123456') AS md5_password;
