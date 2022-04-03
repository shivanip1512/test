set JAR_LIB=C:\Dev\JAXB\jaxb-ri\bin
set JAVA_PATH=C:\Dev\JAXB\jaxb-ri\JDK
set JARCLASSPATH=%JAR_LIB%\jaxb-xjc.jar;%JAR_LIB%\jaxb2-default-value-1.1.jar
set XSD_LOCATION=C:\Dev\JAXB\msp4\xsd
set SERVER_XSD_LOCATION=C:\Dev\JAXB\msp4\wsdl
set BINDING_FILE=C:\Dev\JAXB\msp4\binding

call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -wsdl %XSD_LOCATION%\mspCPSM_V41_Release.xsd
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -wsdl %XSD_LOCATION%\mspGeometry_V41_Release.xsd
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution  -b %BINDING_FILE%\multiSpeak-schema.xml -wsdl %XSD_LOCATION%\MultiSpeak_V416_Release.xsd 
call xjc -nv  -classpath %JARCLASSPATH% -wsdl %XSD_LOCATION%\xlinks.xsd
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution  -b %BINDING_FILE%\MultiSpeakBatchMsgHeader-schema.xml -wsdl %XSD_LOCATION%\MultiSpeakBatchMsgHeader.xsd
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\MultispeakRealTime-schema.xml -wsdl %XSD_LOCATION%\MultispeakRealTime.xsd
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\CB-schema.xml -wsdl %SERVER_XSD_LOCATION%\CB_Server.wsdl 
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\CD-schema.xml -wsdl %SERVER_XSD_LOCATION%\CD_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\DR-schema.xml -wsdl %SERVER_XSD_LOCATION%\DR_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\EA-schema.xml -wsdl %SERVER_XSD_LOCATION%\EA_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\MDM-schema.xml -wsdl %SERVER_XSD_LOCATION%\MDM_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\MR-schema.xml -wsdl %SERVER_XSD_LOCATION%\MR_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\NOT-schema.xml -wsdl %SERVER_XSD_LOCATION%\NOT_Server.wsdl 
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\OA-schema.xml -wsdl %SERVER_XSD_LOCATION%\OA_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\OD-schema.xml -wsdl %SERVER_XSD_LOCATION%\OD_Server.wsdl
call xjc -nv  -classpath %JARCLASSPATH% -XautoNameResolution -b %BINDING_FILE%\SCADA-schema.xml -wsdl %SERVER_XSD_LOCATION%\SCADA_Server.wsdl