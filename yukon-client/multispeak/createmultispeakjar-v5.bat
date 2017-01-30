set JAR_LIB=C:\dev\jaxb-ri\bin
set JAVA_PATH=C:\Program Files\Java\jdk1.8_31\lib
set JARCLASSPATH=%JAR_LIB%\jaxb-xjc.jar;%JAR_LIB%\jaxb2-default-value-1.1.jar
set XSD_LOCATION=C:\dev\msp5\multispeak\src\com\cannontech\multispeak\xsd
set SERVER_XSD_LOCATION=C:\dev\msp5\multispeak\src\com\cannontech\multispeak\wsdl\v5


xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\xlinks.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\mspPrimitives.xsd 
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\mspEnumerations.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\mspGeometry.xsd  

xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\mspCommonTypes.xsd  
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\mspCPSM.xsd

xjc -nv  -classpath %JARCLASSPATH%   -wsdl %XSD_LOCATION%\SandBox.xsd  
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\SandBoxArrayTypes.xsd

xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\MultiSpeak.xsd 
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\mspArrayTypes.xsd

xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\MultiSpeakBatchMsgHeader.xsd 
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\MultiSpeakWebServicesRequestMsgHeader.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %XSD_LOCATION%\MultiSpeakWebServicesResponseMsgHeader.xsd

xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\NOT_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\CB_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\CD_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\EA_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\MDM_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\OA_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\OD_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\SCADA_Server.xsd
xjc -nv  -classpath %JARCLASSPATH%  -wsdl %SERVER_XSD_LOCATION%\MR_Server.xsd


