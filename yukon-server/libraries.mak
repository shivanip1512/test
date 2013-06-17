!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

ALL:	copy

copy:
        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\dbghelp.dll copy $(DBGHELP)\bin\dbghelp.dll $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\thr$(BUILDTYPE).dll copy $(RWWORKSPACE)\lib\*.dll $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\msvcp90$(MICROSOFTDLL).dll copy $(MICROSOFT_VC)\msvc*90$(MICROSOFTDLL).dll $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\Microsoft.VC90.DebugCRT.manifest copy $(MICROSOFT_VC)\Microsoft.VC90.*CRT.manifest $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\ntwdblib.dll copy $(MICROSOFT_SQL)\ntwdblib.dll $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(XERCES_DLL).dll copy $(XERCES)\bin\$(XERCES_DLL).dll $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(SQLAPI_DLL).dll copy $(SQLAPI)\bin\$(SQLAPI_DLL).dll $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\activemq-cpp.dll  copy $(ACTIVEMQ)\bin\activemq-cpp.dll  $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\libapr-1.dll      copy $(ACTIVEMQ)\bin\libapr-1.dll      $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\libaprutil-1.dll  copy $(ACTIVEMQ)\bin\libaprutil-1.dll  $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\libapriconv-1.dll copy $(ACTIVEMQ)\bin\libapriconv-1.dll $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\libeay32.dll copy $(OPENSSL)\bin\libeay32.dll $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\ssleay32.dll copy $(OPENSSL)\bin\ssleay32.dll $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\tcl81.dll copy $(TCL)\bin\tcl81.dll $(YUKONOUTPUT)

clean:
        -del \
$(YUKONOUTPUT)\dbghelp.dll \
$(YUKONOUTPUT)\*15d.dll \
$(YUKONOUTPUT)\*12d.dll \
$(YUKONOUTPUT)\ntwdblib.dll \
$(YUKONOUTPUT)\msvcp90*.dll \
$(YUKONOUTPUT)\xerces-c*.dll \
$(YUKONOUTPUT)\Microsoft.VC90.*CRT.manifest \
$(YUKONOUTPUT)\activemq-cpp.dll \
$(YUKONOUTPUT)\libapr-1.dll \
$(YUKONOUTPUT)\libaprutil-1.dll \
$(YUKONOUTPUT)\libapriconv-1.dll \

deps:
