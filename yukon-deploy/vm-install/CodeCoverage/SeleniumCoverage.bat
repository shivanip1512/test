cd C:\Yukon\Runtime\bin
java -jar C:\CodeCoverage\jacococli.jar dump --address 127.0.0.1 --port 6300 --destfile C:\CodeCoverage\jacoco-CodeCoverageAPI.exec
java -jar C:\CodeCoverage\jacococli.jar report C:\CodeCoverage\jacoco-CodeCoverageAPI.exec --xml C:\CodeCoverage\apiCoverage.xml --classfiles C:\CodeCoverage\com\cannontech
