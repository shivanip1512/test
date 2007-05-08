set endpoint=http://localhost:8080/soap/AdminService
set IEE_PACKAGE=com.cannontech.foreign.service
set IEE_URL=http://www.someurlhere/
set IEE_DEPLOY_FILE=com\cannontech\foreign\service\deploy.wsdd
set AXIS_LIB=C:\MyEclipse_5_0GA\eclipse\head\third-party\
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.8.jar;%AXIS_LIB%\xml-apis.jar;%AXIS_LIB%\xercesImpl.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar

REM Client side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %IEE_URL%<ENTER_NAME_HERE_>?WSDL -p%IEE_PACKAGE%

REM Server side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %IEE_URL%<ENTER_NAME_HERE>?WSDL -p%IEE_PACKAGE%
copy /Y %IEE_DEPLOY_FILE% ..\deploy\deploy.wsdd

del %IEE_DEPLOY_FILE%

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\deploy.wsdd