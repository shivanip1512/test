echo Current Directory is : %cd%
cd .\yukon-test\uiAutomation\
gradle -Dhttps.proxyHost=proxy.etn.com -Dhttps.proxyPort=8080 clean regression