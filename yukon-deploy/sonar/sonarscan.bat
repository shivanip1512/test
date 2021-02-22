echo Current Directory is : %cd%
cd %cd%/Yukon
sonar-scanner.bat -D"sonar.projectKey=Yukon" -D"sonar.host.url=http://127.0.0.1:9000" -D"sonar.login=87730cb40a9f75413cfe44671dc66822423c289a"