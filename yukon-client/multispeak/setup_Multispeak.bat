set endpoint=http://localhost:8080/head/soap/AdminService
set MSP_PACKAGE=com.cannontech.multispeak.service
set MSP_URL=http://www.multispeak.org/interface/30j/
set MSP_DEPLOY_FILE=com\cannontech\multispeak\service\deploy.wsdd
set AXIS_LIB=C:\MyEclipse\eclipse\head\third-party\
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.8.jar;%AXIS_LIB%\xml-apis.jar;%AXIS_LIB%\xercesImpl.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar

REM Client side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%27_OA_MR.asmx?WSDL -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%2A_CB_MR.asmx?WSDL -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%2B_CB_CD.asmx?WSDL -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%4_EA_MR.asmx?WSDL -p%MSP_PACKAGE%

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java %MSP_URL%5_OA_OD.asmx?WSDL -p%MSP_PACKAGE%

REM Server side implementations
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %MSP_URL%27_MR_OA.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_oa_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %MSP_URL%2A_MR_CB.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_cb_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %MSP_URL%2B_CD_CB.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\cd_cb_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %MSP_URL%4_MR_EA.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\mr_ea_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %MSP_URL%5_OD_OA.asmx?WSDL -p%MSP_PACKAGE%
copy /Y %MSP_DEPLOY_FILE% ..\deploy\od_oa_deploy.wsdd

del %MSP_DEPLOY_FILE%

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_oa_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_cb_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\cd_cb_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_ea_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\od_oa_deploy.wsdd