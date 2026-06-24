-- Backup approval_status data
USE test;

-- Export current data for backup
SELECT * FROM approval_status INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/approval_status_backup.csv'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n';
