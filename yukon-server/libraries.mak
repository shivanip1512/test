CONFIGURATION=$(BUILD_MODE)

ACTIVEMQ_BIN    = $(ACTIVEMQ)\$(CONFIGURATION)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
ACTIVEMQ_DLL    = activemq-cppd.dll
!ELSE
ACTIVEMQ_DLL    = activemq-cpp.dll
!ENDIF

APR_BIN         = $(APR)\$(CONFIGURATION)\bin
APR_DLL         = libapr-1.dll

APR_UTIL_BIN    = $(APR_UTIL)\$(CONFIGURATION)\bin
APR_UTIL_DLL    = libaprutil-1.dll

APR_ICONV_BIN   = $(APR_ICONV)\$(CONFIGURATION)\bin
APR_ICONV_DLL   = libapriconv-1.dll

BOOST_BIN       = $(BOOST)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
BOOST_VERSION = vc140-mt-gd-1_60
!ELSE
BOOST_VERSION = vc140-mt-1_60
!ENDIF
BOOST_DLL_1 = boost_chrono-$(BOOST_VERSION).dll
BOOST_DLL_2 = boost_date_time-$(BOOST_VERSION).dll
BOOST_DLL_3 = boost_thread-$(BOOST_VERSION).dll
BOOST_DLL_4 = boost_regex-$(BOOST_VERSION).dll
BOOST_DLL_5 = boost_filesystem-$(BOOST_VERSION).dll
BOOST_DLL_6 = boost_system-$(BOOST_VERSION).dll
BOOST_DLL_7 = boost_unit_test_framework-$(BOOST_VERSION).dll

DBGHELP_BIN       = $(DBGHELP)\bin
DBGHELP_DLL       = dbghelp.dll

LOG4CXX_BIN     = $(LOG4CXX)\$(CONFIGURATION)\bin
LOG4CXX_DLL     = log4cxx.dll

SQLAPI_BIN      = $(SQLAPI)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
SQLAPI_DLL      = sqlapid.dll
!ELSE
SQLAPI_DLL      = sqlapi.dll
!ENDIF

MICROSOFT_VC    = $(MICROSOFT)\vc\14.0\$(CONFIGURATION)
!IF "$(CONFIGURATION)"=="DEBUG"
MICROSOFT_VCP_DLL = msvcp140d.dll
MICROSOFT_RT_DLL  = vcruntime140d.dll
!ELSE
MICROSOFT_VCP_DLL = msvcp140.dll
MICROSOFT_RT_DLL  = vcruntime140.dll
!ENDIF

SQLAPI_BIN      = $(SQLAPI)\bin
MICROSOFT_SQL_DLL = ntwdblib.dll

OPENSSL_BIN     = $(OPENSSL)\$(CONFIGURATION)\bin
OPENSSL_DLL_1   = libeay32.dll
OPENSSL_DLL_2   = ssleay32.dll

TCL_BIN         = $(TCL)\$(CONFIGURATION)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
TCL_DLL         = tcl86tg.dll
!ELSE
TCL_DLL         = tcl86t.dll
!ENDIF

XERCES_BIN      = $(XERCES)\$(CONFIGURATION)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
XERCES_DLL      = xerces-c_3_1D.dll
!ELSE
XERCES_DLL      = xerces-c_3_1.dll
!ENDIF

ALL: bin lib \
  bin\$(ACTIVEMQ_DLL) \
  bin\$(APR_DLL) \
  bin\$(APR_ICONV_DLL) \
  bin\$(APR_UTIL_DLL) \
  bin\$(BOOST_DLL_1) \
  bin\$(BOOST_DLL_2) \
  bin\$(BOOST_DLL_3) \
  bin\$(BOOST_DLL_4) \
  bin\$(BOOST_DLL_5) \
  bin\$(BOOST_DLL_6) \
  bin\$(BOOST_DLL_7) \
  bin\$(DBGHELP_DLL) \
  bin\$(LOG4CXX_DLL) \
  bin\$(MICROSOFT_RT_DLL) \
  bin\$(MICROSOFT_SQL_DLL) \
  bin\$(MICROSOFT_VCP_DLL) \
  bin\$(OPENSSL_DLL_1) \
  bin\$(OPENSSL_DLL_2) \
  bin\$(SQLAPI_DLL) \
  bin\$(TCL_DLL) \
  bin\$(XERCES_DLL)

bin:; md bin
lib:; md lib

bin\$(ACTIVEMQ_DLL):$(ACTIVEMQ_BIN)\$(ACTIVEMQ_DLL); copy $? $@

bin\$(APR_DLL):$(APR_BIN)\$(APR_DLL); copy $? $@
bin\$(APR_UTIL_DLL):$(APR_UTIL_BIN)\$(APR_UTIL_DLL); copy $? $@
bin\$(APR_ICONV_DLL):$(APR_ICONV_BIN)\$(APR_ICONV_DLL); copy $? $@

bin\$(BOOST_DLL_1):$(BOOST_BIN)\$(BOOST_DLL_1); copy $? $@
bin\$(BOOST_DLL_2):$(BOOST_BIN)\$(BOOST_DLL_2); copy $? $@
bin\$(BOOST_DLL_3):$(BOOST_BIN)\$(BOOST_DLL_3); copy $? $@
bin\$(BOOST_DLL_4):$(BOOST_BIN)\$(BOOST_DLL_4); copy $? $@
bin\$(BOOST_DLL_5):$(BOOST_BIN)\$(BOOST_DLL_5); copy $? $@
bin\$(BOOST_DLL_6):$(BOOST_BIN)\$(BOOST_DLL_6); copy $? $@
bin\$(BOOST_DLL_7):$(BOOST_BIN)\$(BOOST_DLL_7); copy $? $@

bin\$(DBGHELP_DLL):$(DBGHELP_BIN)\$(DBGHELP_DLL); copy $? $@

bin\$(LOG4CXX_DLL):$(LOG4CXX_BIN)\$(LOG4CXX_DLL); copy $? $@

bin\$(MICROSOFT_VCP_DLL):$(MICROSOFT_VC)\$(MICROSOFT_VCP_DLL); copy $? $@
bin\$(MICROSOFT_RT_DLL):$(MICROSOFT_VC)\$(MICROSOFT_RT_DLL);  copy $? $@
bin\$(MICROSOFT_SQL_DLL):$(MICROSOFT_SQL)\$(MICROSOFT_SQL_DLL); copy $? $@

bin\$(OPENSSL_DLL_1):$(OPENSSL_BIN)\$(OPENSSL_DLL_1); copy $? $@
bin\$(OPENSSL_DLL_2):$(OPENSSL_BIN)\$(OPENSSL_DLL_2); copy $? $@

bin\$(SQLAPI_DLL):$(SQLAPI_BIN)\$(SQLAPI_DLL); copy $? $@

bin\$(TCL_DLL):$(TCL_BIN)\$(TCL_DLL); copy $? $@

bin\$(XERCES_DLL):$(XERCES_BIN)\$(XERCES_DLL); copy $? $@


