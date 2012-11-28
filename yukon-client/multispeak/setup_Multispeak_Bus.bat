set endpoint=http://localhost:8080/soap/AdminService
set MSP_PACKAGE=com.cannontech.multispeak.deploy.service
REM OLD set MSP_URL=--timeout -1 http://www.multispeak.org/Members/interface/30ab/
REM set MSP_URL=--timeout -1 https://apps.cooperative.com/content/multispeak/specifications/30ab/
set MSP_URL=--timeout -1 C:\Users\B1AFC\development\eclipsesvn\branch_head\multispeak\wsdl\v3_0_ab\
set MSP_DEPLOY_FILE=com\cannontech\multispeak\deploy\service\deploy.wsdd
set AXIS_LIB=C:\Users\B1AFC\development\eclipsesvn\branch_head\third-party\
set AXIS_PROJECT_LIB=C:\Users\B1AFC\development\eclipsesvn\branch_head\axis\lib\endorsed\
REM set URL_EXT=.asmx?WSDL
set URL_EXT=.wsdl
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging-1.1.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.17.jar;%AXIS_PROJECT_LIB\xml-apis-2.6.2.jar;%AXIS_PROJECT_LIB\xercesImpl-2.6.2.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar;

REM Client side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%OA_Server%URL_EXT% -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%CB_Server%URL_EXT% -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%EA_Server%URL_EXT% -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%MDM_Server%URL_EXT% -p%MSP_PACKAGE%

REM Server side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%MR_Server%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_server_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%CD_Server%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\cd_server_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%OD_Server%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\od_server_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side %MSP_URL%LM_Server%URL_EXT% -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\lm_server_deploy.wsdd

del %MSP_DEPLOY_FILE%

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_server_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\cd_server_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\od_server_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\lm_server_deploy.wsdd