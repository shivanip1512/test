echo Current Directory is : %cd%
cd %cd%/Yukon
sonar-scanner.bat -D"sonar.projectKey=YukonUnitTesting" -D"sonar.host.url=http://10.6.1.121:9005" -Dproject.settings=sonar-junit-project.properties -D"sonar.login=a7d6cd00a3ca191219c91d65f4e97d5f35e8b5fc"