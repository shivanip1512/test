set endpoint=http://localhost:8080/soap/AdminService
set MSP_PACKAGE=com.cannontech.multispeak.deploy.service
set MSP_URL=--timeout -1 http://www.multispeak.org/interface/30qb/
set MSP_DEPLOY_FILE=com\cannontech\multispeak\deploy\service\deploy.wsdd
set AXIS_LIB=C:\eclipsedev\branch_4_1\third-party\
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging-1.1.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.14.jar;C:\axis1_2\lib\xml-apis.jar;C:\axis1_2\lib\xercesImpl.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar;

REM Client side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%OA_Server.asmx?WSDL -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%CB_Server.asmx?WSDL -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%EA_Server.asmx?WSDL -p%MSP_PACKAGE%

REM Server side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%MR_Server.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_server_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%CD_Server.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\cd_server_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%OD_Server.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\od_server_deploy.wsdd

del %MSP_DEPLOY_FILE%

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_server_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\cd_server_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\od_server_deploy.wsdd