echo Current Directory is : %cd%
cd .\yukon-test\restAssured\
gradlew -Dhttps.proxyHost=proxy.etn.com -Dhttps.proxyPort=8080 -Dtestng.dtd.http=true test