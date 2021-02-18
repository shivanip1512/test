echo Current Directory is : %cd%
cd .\yukon-test\uiAutomation\
gradle -Dhttps.proxyHost=proxy.etn.com -Dhttps.proxyPort=8080 -Dtestng.dtd.http=true clean regression