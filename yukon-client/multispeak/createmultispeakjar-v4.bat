set MSP_PACKAGE=com.cannontech.msp.beans.v4
set JAR_LIB=C:\dev\jaxb-ri\bin
set JAVA_PATH=C:\Program Files\Java\jdk-11.0.8\lib
set JARCLASSPATH=%JAR_LIB%\jaxb-xjc.jar;%JAR_LIB%\jaxb2-default-value-1.1.jar
set XSD_LOCATION=C:\dev\msp4\multispeak\src\com\cannontech\multispeak\xsd
set SERVER_XSD_LOCATION=C:\dev\msp4\multispeak\src\com\cannontech\multispeak\wsdl\v4
set BINDING_FILE=C:\dev\msp4\multispeak\src\com\cannontech\multispeak\v4_wsdl-schemafiles
SET BEAN_PATH=C:\dev\jaxb-ri\bin\com\cannontech\msp\beans\v4

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -p %MSP_PACKAGE% -wsdl %XSD_LOCATION%\mspCPSM_V41_Release.xsd
call rename %BEAN_PATH%\ObjectFactory.java MspCPSMObjectFactory.java
call fart "%BEAN_PATH%\MspCPSMObjectFactory.java" ObjectFactory MspCPSMObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -p %MSP_PACKAGE% -wsdl %XSD_LOCATION%\mspGeometry_V41_Release.xsd
call rename %BEAN_PATH%\ObjectFactory.java MspGeometryObjectFactory.java
call fart "%BEAN_PATH%\MspGeometryObjectFactory.java" ObjectFactory MspGeometryObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -p %MSP_PACKAGE% -b %BINDING_FILE%\multiSpeak-schema.xml -wsdl %XSD_LOCATION%\MultiSpeak_V416_Release.xsd 
call rename %BEAN_PATH%\ObjectFactory.java MultispeakObjectFactory.java
call fart "%BEAN_PATH%\MultispeakObjectFactory.java" ObjectFactory MultispeakObjectFactory
call xjc -nv  -classpath %JARCLASSPATH% -p %MSP_PACKAGE% -wsdl %XSD_LOCATION%\xlinks.xsd

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -p %MSP_PACKAGE% -b %BINDING_FILE%\MultiSpeakBatchMsgHeader-schema.xml -wsdl %XSD_LOCATION%\MultiSpeakBatchMsgHeader.xsd
call rename %BEAN_PATH%\ObjectFactory.java MultiSpeakBatchMsgHeaderObjectFactory.java
call fart "%BEAN_PATH%\MultiSpeakBatchMsgHeaderObjectFactory.java" ObjectFactory MultiSpeakBatchMsgHeaderObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -p %MSP_PACKAGE% -b %BINDING_FILE%\MultispeakRealTime-schema.xml -wsdl %XSD_LOCATION%\MultispeakRealTime.xsd
call rename %BEAN_PATH%\ObjectFactory.java MultispeakRealTimeObjectFactory.java
call fart "%BEAN_PATH%\MultispeakRealTimeObjectFactory.java" ObjectFactory MultispeakRealTimeObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\CB-schema.xml -p %MSP_PACKAGE%  -wsdl %SERVER_XSD_LOCATION%\CB_Server.wsdl 
call rename %BEAN_PATH%\ObjectFactory.java   CBObjectFactory.java
call fart "%BEAN_PATH%\CBObjectFactory.java" ObjectFactory CBObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\CD-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\CD_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java CDObjectFactory.java
call fart "%BEAN_PATH%\CDObjectFactory.java" ObjectFactory CDObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\DR-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\DR_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java DRObjectFactory.java
call fart "%BEAN_PATH%\DRObjectFactory.java" ObjectFactory DRObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\EA-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\EA_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java EAObjectFactory.java
call fart "%BEAN_PATH%\EAObjectFactory.java" ObjectFactory EAObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\MDM-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\MDM_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java MDMObjectFactory.java
call fart "%BEAN_PATH%\MDMObjectFactory.java" ObjectFactory MDMObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\MR-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\MR_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java MRObjectFactory.java
call fart "%BEAN_PATH%\MRObjectFactory.java" ObjectFactory MRObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\NOT-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\NOT_Server.wsdl 
call rename %BEAN_PATH%\ObjectFactory.java NOTObjectFactory.java
call fart "%BEAN_PATH%\NOTObjectFactory.java" ObjectFactory NOTObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\OA-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\OA_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java OAObjectFactory.java
call fart "%BEAN_PATH%\OAObjectFactory.java" ObjectFactory OAObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\OD-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\OD_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java ODObjectFactory.java
call fart "%BEAN_PATH%\ODObjectFactory.java" ObjectFactory ODObjectFactory

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\SCADA-schema.xml -p %MSP_PACKAGE% -wsdl %SERVER_XSD_LOCATION%\SCADA_Server.wsdl
call rename %BEAN_PATH%\ObjectFactory.java SCADAObjectFactory.java
call fart "%BEAN_PATH%\SCADAObjectFactory.java" ObjectFactory SCADAObjectFactory
