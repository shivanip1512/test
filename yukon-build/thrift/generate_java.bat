FOR /F %%I IN ('dir /b *.thrift') DO (
    thrift-0.9.0.exe --gen java:beans %%I
)

pause
