echo Current Directory is : %cd%
cd %cd%/Yukon
sonar-scanner.bat -D"sonar.projectKey=Yukon" -D"sonar.host.url=http://127.0.0.1:9005" -D"sonar.login=a7d6cd00a3ca191219c91d65f4e97d5f35e8b5fc"