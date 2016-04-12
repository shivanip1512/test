!include $(COMPILEBASE)\global.inc

ALL:	copy

copy:
        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(DBGHELP_DLL)       copy $(DBGHELP_BIN)\$(DBGHELP_DLL)         $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(MICROSOFT_VCP_DLL)  copy $(MICROSOFT_VC)\$(MICROSOFT_VCP_DLL)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(MICROSOFT_SQL_DLL) copy $(MICROSOFT_SQL)\$(MICROSOFT_SQL_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(XERCES_DLL) copy $(XERCES_BIN)\$(XERCES_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(SQLAPI_DLL) copy $(SQLAPI_BIN)\$(SQLAPI_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(ACTIVEMQ_DLL)   copy $(ACTIVEMQ_BIN)\$(ACTIVEMQ_DLL)   $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(APR_DLL)        copy $(APR_BIN)\$(APR_DLL)             $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(APR_UTIL_DLL)   copy $(APR_UTIL_BIN)\$(APR_UTIL_DLL)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(APR_ICONV_DLL)  copy $(APR_ICONV_BIN)\$(APR_ICONV_DLL) $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_1)    copy $(BOOST_BIN)\$(BOOST_DLL_1)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_2)    copy $(BOOST_BIN)\$(BOOST_DLL_2)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_3)    copy $(BOOST_BIN)\$(BOOST_DLL_3)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_4)    copy $(BOOST_BIN)\$(BOOST_DLL_4)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_5)    copy $(BOOST_BIN)\$(BOOST_DLL_5)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_6)    copy $(BOOST_BIN)\$(BOOST_DLL_6)       $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(BOOST_DLL_7)    copy $(BOOST_BIN)\$(BOOST_DLL_7)       $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(OPENSSL_DLL_1)  copy $(OPENSSL_BIN)\$(OPENSSL_DLL_1)   $(YUKONOUTPUT)
        -@if not exist $(YUKONOUTPUT)\$(OPENSSL_DLL_2)  copy $(OPENSSL_BIN)\$(OPENSSL_DLL_2)   $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(TCL_DLL)        copy $(TCL_BIN)\$(TCL_DLL)             $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(XERCES_DLL)     copy $(XERCES_BIN)\$(XERCES_DLL)       $(YUKONOUTPUT)

        -@if not exist $(YUKONOUTPUT)\$(LOG4CXX_DLL)    copy $(LOG4CXX_BIN)\$(LOG4CXX_DLL)     $(YUKONOUTPUT)

clean:
        -del /Q \
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
