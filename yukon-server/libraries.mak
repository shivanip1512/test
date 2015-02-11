!include $(COMPILEBASE)\global.inc

ALL:	copy

copy:
        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(DBGHELP_DLL)       copy $(DBGHELP)\bin\$(DBGHELP_DLL)         $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(MICROSOFT_VCR_DLL)  copy $(MICROSOFT_VC)\$(MICROSOFT_VCR_DLL)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(MICROSOFT_VCP_DLL)  copy $(MICROSOFT_VC)\$(MICROSOFT_VCP_DLL)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(MICROSOFT_SQL_DLL) copy $(MICROSOFT_SQL)\$(MICROSOFT_SQL_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(XERCES_DLL) copy $(XERCES)\bin\$(XERCES_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(SQLAPI_DLL) copy $(SQLAPI)\bin\$(SQLAPI_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(ACTIVEMQ_DLL)   copy $(ACTIVEMQ)\bin\$(ACTIVEMQ_DLL)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(APR_DLL)        copy $(APR)\bin\$(APR_DLL)             $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(APR_UTIL_DLL)   copy $(APR_UTIL)\bin\$(APR_UTIL_DLL)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(APR_ICONV_DLL)  copy $(APR_ICONV)\bin\$(APR_ICONV_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_1)    copy $(BOOST)\bin\$(BOOST_DLL_1)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_2)    copy $(BOOST)\bin\$(BOOST_DLL_2)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_3)    copy $(BOOST)\bin\$(BOOST_DLL_3)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_4)    copy $(BOOST)\bin\$(BOOST_DLL_4)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_5)    copy $(BOOST)\bin\$(BOOST_DLL_5)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_6)    copy $(BOOST)\bin\$(BOOST_DLL_6)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_7)    copy $(BOOST)\bin\$(BOOST_DLL_7)       $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(OPENSSL_DLL_1)  copy $(OPENSSL)\bin\$(OPENSSL_DLL_1)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(OPENSSL_DLL_2)  copy $(OPENSSL)\bin\$(OPENSSL_DLL_2)   $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(TCL_DLL)        copy $(TCL)\bin\$(TCL_DLL)             $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(XERCES_DLL)     copy $(XERCES)\bin\$(XERCES_DLL)       $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(LOG4CXX_DLL)    copy $(LOG4CXX)\bin\$(LOG4CXX_DLL)     $(YUKONOUTPUT)

clean:
        -del \
$(YUKONOUTPUT)\$(DBGHELP_DLL) \
$(YUKONOUTPUT)\$(MICROSOFT_VCR_DLL) \
$(YUKONOUTPUT)\$(MICROSOFT_VCP_DLL) \
$(YUKONOUTPUT)\$(MICROSOFT_SQL_DLL) \
$(YUKONOUTPUT)\$(XERCES_DLL) \
$(YUKONOUTPUT)\$(SQLAPI_DLL) \
$(YUKONOUTPUT)\$(ACTIVEMQ_DLL) \
$(YUKONOUTPUT)\$(APR_DLL) \
$(YUKONOUTPUT)\$(APR_UTIL_DLL) \
$(YUKONOUTPUT)\$(APR_ICONV_DLL) \
$(YUKONOUTPUT)\$(BOOST_DLL_1) \
$(YUKONOUTPUT)\$(BOOST_DLL_2) \
$(YUKONOUTPUT)\$(BOOST_DLL_3) \
$(YUKONOUTPUT)\$(BOOST_DLL_4) \
$(YUKONOUTPUT)\$(BOOST_DLL_5) \
$(YUKONOUTPUT)\$(BOOST_DLL_6) \
$(YUKONOUTPUT)\$(BOOST_DLL_7) \
$(YUKONOUTPUT)\$(OPENSSL_DLL_1) \
$(YUKONOUTPUT)\$(OPENSSL_DLL_2) \
$(YUKONOUTPUT)\$(TCL_DLL) \
$(YUKONOUTPUT)\$(XERCES_DLL) \
$(YUKONOUTPUT)\$(LOG4CXX_DLL) \

deps:
