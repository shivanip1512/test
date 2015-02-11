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

java -cp %COMMAND_PATH% %MSP_PACKAGE%
cd %BEANS_PATH%
javac *.*
del /s /q *.java
call>jaxb.index
echo ObjectFactory > jaxb.index
cd %COMMAND_PATH%
jar -cf0 msp-beans-v3.jar com
del /s /q com
rmdir com /s /q


