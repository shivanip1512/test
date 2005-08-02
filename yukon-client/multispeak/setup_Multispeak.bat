set endpoint=http://localhost:8080/head/services/AdminService
set AXIS_LIB=C:\WebSphere\dev\workspace\third-party\
set AXISCLASSPATH=%AXIS_LIB%\axis.jar;%AXIS_LIB%\commons-discovery.jar;%AXIS_LIB%\commons-logging.jar;%AXIS_LIB%\jaxrpc.jar;%AXIS_LIB%\saaj.jar;%AXIS_LIB%\log4j-1.2.8.jar;%AXIS_LIB%\xml-apis.jar;%AXIS_LIB%\xercesImpl.jar;%AXIS_LIB%\wsdl4j.jar;%AXIS_LIB%\mail.jar;%AXIS_LIB%\activation.jar

java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true ..\wsdl/2A_MR_CB.wsdl -pcom.cannontech.multispeak
copy /Y com\cannontech\multispeak\deploy.wsdd ..\deploy\mr_cb_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true ..\wsdl/2A_CB_MR.wsdl -pcom.cannontech.multispeak
copy /Y com\cannontech\multispeak\deploy.wsdd ..\deploy\cb_mr_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true ..\wsdl/5_OA_OD.wsdl -pcom.cannontech.multispeak
copy /Y com\cannontech\multispeak\deploy.wsdd ..\deploy\oa_od_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true ..\wsdl/5_OD_OA.wsdl -pcom.cannontech.multispeak
copy /Y com\cannontech\multispeak\deploy.wsdd ..\deploy\od_oa_deploy.wsdd

java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\mr_cb_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\cb_mr_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\oa_od_deploy.wsdd
java -cp %AXISCLASSPATH% org.apache.axis.client.AdminClient -l%endpoint% ..\deploy\od_oa_deploy.wsdd