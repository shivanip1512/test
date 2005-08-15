set endpoint=http://localhost:8080/head/services/AdminService
set AXIS_LIB=C:\WebSphere\dev\workspace\third-party\
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.8.jar;%AXIS_LIB%\xml-apis.jar;%AXIS_LIB%\xercesImpl.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --skeletonDeploy true ..\wsdl/PSS2WS.wsdl -pcom.cannontech.custom.pss2ws
copy /Y com\cannontech\custom\pss2ws\deploy.wsdd ..\deploy\pss2ws_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\pss2ws_deploy.wsdd
