if not"%1" == "" goto single

for %%i in (*.jar) do jarsigner -keystore .keystore -storepass CTI123 %%i mykey
goto done

:single
jarsigner -keystore .keystore -storepass CTI123 %1 mykey

:done