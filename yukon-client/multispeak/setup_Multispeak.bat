set endpoint=http://localhost:8080/soap/AdminService
set MSP_PACKAGE=com.cannontech.multispeak.deploy.service
set MSP_URL=--timeout -1 http://www.multispeak.org/interface/30v/
set MSP_URL=--timeout -1 C:\eclipsesvn\branch_4_2\multispeak\wsdl\
set MSP_DEPLOY_FILE=com\cannontech\multispeak\deploy\service\deploy.wsdd
set AXIS_LIB=C:\eclipsesvn\branch_4_2\third-party\
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging-1.1.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.17.jar;C:\axis1_2\lib\xml-apis.jar;C:\axis1_2\lib\xercesImpl.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar;
set URL_EXT=.asmx?WSDL
set URL_EXT=.wsdl
REM Client side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%2A_CB_MR%URL_EXT% -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%2B_CB_CD%URL_EXT% -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%4_EA_MR%URL_EXT% -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%5_OA_OD%URL_EXT% -p%MSP_PACKAGE%

REM Server side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%2A_MR_CB%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_cb_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%2B_CD_CB%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\cd_cb_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%4_MR_EA%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_ea_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%5_OD_OA%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\od_oa_deploy.wsdd

del %MSP_DEPLOY_FILE%

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_cb_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\cd_cb_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_ea_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\od_oa_deploy.wsdd