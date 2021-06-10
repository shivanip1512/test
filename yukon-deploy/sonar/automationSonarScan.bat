echo Current Directory is : %cd%
cd %cd%/Yukon
sonar-scanner.bat -D"sonar.projectKey=YukonQAAutomation" -D"sonar.host.url=http://10.6.1.121:9000" -Dproject.settings=sonar-automation-project.properties -D"sonar.login=d384c395201c54f2ec7a644b212c7f9cbf71c5f1"