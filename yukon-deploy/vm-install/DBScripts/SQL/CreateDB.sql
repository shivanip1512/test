USE MASTER;
CREATE DATABASE yukon_automation_yukon_install_head_lastSuccessfulBuild;
USE yukon_automation_yukon_install_head_lastSuccessfulBuild;
CREATE LOGIN yukon_automation_yukon_install_head_lastSuccessfulBuild
WITH PASSWORD = 'yukon_automation_yukon_install_head_lastSuccessfulBuild', 
     CHECK_POLICY = OFF, 
     DEFAULT_DATABASE = yukon_automation_yukon_install_head_lastSuccessfulBuild;
CREATE USER yukon_automation_yukon_install_head_lastSuccessfulBuild FOR LOGIN yukon_automation_yukon_install_head_lastSuccessfulBuild;
GRANT SELECT, INSERT, DELETE, UPDATE TO yukon_automation_yukon_install_head_lastSuccessfulBuild; 
