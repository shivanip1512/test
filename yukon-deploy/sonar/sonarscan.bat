echo Current Directory is : %cd%
cd %cd%/Yukon
sonar-scanner.bat -D"sonar.projectKey=Yukon" -D"sonar.host.url=http://127.0.0.1:9000" -D"sonar.login=ce2612bf98d9636a4b22c2f75466e0bd6efab60a"