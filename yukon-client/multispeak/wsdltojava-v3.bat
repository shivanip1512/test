set MSP_PACKAGE=com.cannontech.msp.beans.v3
set COMMAND_PATH=C:\MSP_Implementation\multispeak\lib
set MSP_URL=C:\MSP_Implementation\multispeak\src\com\cannontech\multispeak\wsdl
set JAR_LIB=C:\xjc_parser\jaxb-ri-2.2.7\jaxb-ri-2.2.7\bin
set JAVA_PATH=C:\Program Files (x86)\Java\jre7\lib
set JARCLASSPATH=%JAR_LIB%\jaxb-xjc.jar;%JAR_LIB%\jaxb2-default-value-1.1.jar
set BINDING_FILE=C:\MSP_Implementation\multispeak\v3_wsdl-schemafiles
set BEANS_PATH=%COMMAND_PATH%\com\cannontech\msp\beans\v3

mkdir %BEANS_PATH%
cd %COMMAND_PATH%

xjc -nv -b %BINDING_FILE%\CB-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\CB_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java CBObjectFactory.java
fart "%BEANS_PATH%\CBObjectFactory.java" ObjectFactory CBObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java CBArrayOfString2.java
fart "%BEANS_PATH%\ArrayOfString2 CBArrayOfString2

xjc -nv -b %BINDING_FILE%\CD-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\CD_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java CDObjectFactory.java
fart "%BEANS_PATH%\CDObjectFactory.java" ObjectFactory CDObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java CDArrayOfString2.java
fart "%BEANS_PATH%\ ArrayOfString2 CDArrayOfString2

xjc -nv -b %BINDING_FILE%\LM-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\LM_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java LMObjectFactory.java
fart "%BEANS_PATH%\LMObjectFactory.java" ObjectFactory LMObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java LMArrayOfString2.java
fart "%BEANS_PATH%\ ArrayOfString2 LMArrayOfString2

xjc -nv -b %BINDING_FILE%\MDM-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\MDM_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java MDMObjectFactory.java
fart "%BEANS_PATH%\MDMObjectFactory.java" ObjectFactory MDMObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java MDMArrayOfString2.java
fart "%BEANS_PATH%\ArrayOfString2 MDMArrayOfString2

xjc -nv -b %BINDING_FILE%\MR-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\MR_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java MRObjectFactory.java
fart "%BEANS_PATH%\MRObjectFactory.java" ObjectFactory MRObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java MRArrayOfString2.java
fart "%BEANS_PATH%\ ArrayOfString2 MRArrayOfString2

xjc -nv -b %BINDING_FILE%\OA-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\OA_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java OAObjectFactory.java
fart "%BEANS_PATH%\OAObjectFactory.java" ObjectFactory OAObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java OAArrayOfString2.java
fart "%BEANS_PATH%\ ArrayOfString2 OAArrayOfString2

xjc -nv -b %BINDING_FILE%\OD-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\OD_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java ODObjectFactory.java
fart "%BEANS_PATH%\ODObjectFactory.java" ObjectFactory ODObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java ODArrayOfString2.java
fart "%BEANS_PATH%\ ArrayOfString2 ODArrayOfString2
	
xjc -nv -b %BINDING_FILE%\SCADA-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\SCADA_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java SCADAObjectFactory.java
fart "%BEANS_PATH%\SCADAObjectFactory.java" ObjectFactory SCADAObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java SCADAArrayOfString2.java
fart "%BEANS_PATH%\  ArrayOfString2 SCADAArrayOfString2

xjc -nv -b %BINDING_FILE%\EA-schema.xml -classpath %JARCLASSPATH% -wsdl %MSP_URL%\EA_Server.wsdl -p %MSP_PACKAGE%
rename %BEANS_PATH%\ObjectFactory.java EAObjectFactory.java
fart "%BEANS_PATH%\EAObjectFactory.java" ObjectFactory EAObjectFactory
rename %BEANS_PATH%\ArrayOfString2.java EAArrayOfString2.java
fart "%BEANS_PATH%\ ArrayOfString2 EAArrayOfString2


