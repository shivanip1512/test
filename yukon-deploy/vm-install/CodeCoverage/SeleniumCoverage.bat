cd C:\Yukon\Runtime\bin
java -jar C:\CodeCoverage\jacococli.jar dump --address 127.0.0.1 --port 6300 --destfile C:\CodeCoverage\jacoco-CodeCoverageSelenium.exec
java -jar C:\CodeCoverage\jacococli.jar report C:\CodeCoverage\jacoco-CodeCoverageSelenium.exec --xml C:\CodeCoverage\SeleniumCoverage.xml --classfiles C:\CodeCoverage\dumpClassFiles\com\cannontech
