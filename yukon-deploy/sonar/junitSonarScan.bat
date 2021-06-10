echo Current Directory is : %cd%
cd %cd%/Yukon
sonar-scanner.bat -D"sonar.projectKey=YukonUnitTesting" -D"sonar.host.url=http://10.6.1.121:9000" -Dproject.settings=sonar-junit-project.properties -D"sonar.login=bbe271dd9cd064b5777c30a2e725278f8ee75ecb"