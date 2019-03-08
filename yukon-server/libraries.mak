CONFIGURATION=$(BUILD_MODE)
!IF "$(CONFIGURATION)"=="DEBUG"
BIN=bin-debug
LIB=lib-debug
PDB=pdb-debug
!ELSE
BIN=bin
LIB=lib
PDB=pdb
!ENDIF

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
BOOST_VERSION = vc141-mt-gd-x32-1_67
!ELSE
BOOST_VERSION = vc141-mt-x32-1_67
!ENDIF
BOOST_DLL_1 = boost_chrono-$(BOOST_VERSION).dll
BOOST_DLL_2 = boost_date_time-$(BOOST_VERSION).dll
BOOST_DLL_3 = boost_thread-$(BOOST_VERSION).dll
BOOST_DLL_4 = boost_regex-$(BOOST_VERSION).dll
BOOST_DLL_5 = boost_filesystem-$(BOOST_VERSION).dll
BOOST_DLL_6 = boost_system-$(BOOST_VERSION).dll
BOOST_DLL_7 = boost_unit_test_framework-$(BOOST_VERSION).dll
BOOST_DLL_8 = boost_iostreams-$(BOOST_VERSION).dll

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
OPENSSL_DLL_1   = libcrypto-1_1.dll
OPENSSL_DLL_2   = libssl-1_1.dll

TCL_BIN         = $(TCL)\$(CONFIGURATION)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
TCL_DLL         = tcl86tg.dll
!ELSE
TCL_DLL         = tcl86t.dll
!ENDIF

XERCES_BIN      = $(XERCES)\$(CONFIGURATION)\bin
!IF "$(CONFIGURATION)"=="DEBUG"
XERCES_DLL      = xerces-c_3_2D.dll
!ELSE
XERCES_DLL      = xerces-c_3_2.dll
!ENDIF

ALL: $(BIN) $(LIB) $(PDB) \
  $(BIN)\$(ACTIVEMQ_DLL) \
  $(BIN)\$(APR_DLL) \
  $(BIN)\$(APR_ICONV_DLL) \
  $(BIN)\$(APR_UTIL_DLL) \
  $(BIN)\$(BOOST_DLL_1) \
  $(BIN)\$(BOOST_DLL_2) \
  $(BIN)\$(BOOST_DLL_3) \
  $(BIN)\$(BOOST_DLL_4) \
  $(BIN)\$(BOOST_DLL_5) \
  $(BIN)\$(BOOST_DLL_6) \
  $(BIN)\$(BOOST_DLL_7) \
  $(BIN)\$(BOOST_DLL_8) \
  $(BIN)\$(DBGHELP_DLL) \
  $(BIN)\$(LOG4CXX_DLL) \
  $(BIN)\$(MICROSOFT_RT_DLL) \
  $(BIN)\$(MICROSOFT_SQL_DLL) \
  $(BIN)\$(MICROSOFT_VCP_DLL) \
  $(BIN)\$(OPENSSL_DLL_1) \
  $(BIN)\$(OPENSSL_DLL_2) \
  $(BIN)\$(SQLAPI_DLL) \
  $(BIN)\$(TCL_DLL) \
  $(BIN)\$(XERCES_DLL)

$(BIN):; md $(BIN)
$(LIB):; md $(LIB)
$(PDB):; md $(PDB)

$(BIN)\$(ACTIVEMQ_DLL):$(ACTIVEMQ_BIN)\$(ACTIVEMQ_DLL); copy $? $@

$(BIN)\$(APR_DLL):$(APR_BIN)\$(APR_DLL); copy $? $@
$(BIN)\$(APR_UTIL_DLL):$(APR_UTIL_BIN)\$(APR_UTIL_DLL); copy $? $@
$(BIN)\$(APR_ICONV_DLL):$(APR_ICONV_BIN)\$(APR_ICONV_DLL); copy $? $@

$(BIN)\$(BOOST_DLL_1):$(BOOST_BIN)\$(BOOST_DLL_1); copy $? $@
$(BIN)\$(BOOST_DLL_2):$(BOOST_BIN)\$(BOOST_DLL_2); copy $? $@
$(BIN)\$(BOOST_DLL_3):$(BOOST_BIN)\$(BOOST_DLL_3); copy $? $@
$(BIN)\$(BOOST_DLL_4):$(BOOST_BIN)\$(BOOST_DLL_4); copy $? $@
$(BIN)\$(BOOST_DLL_5):$(BOOST_BIN)\$(BOOST_DLL_5); copy $? $@
$(BIN)\$(BOOST_DLL_6):$(BOOST_BIN)\$(BOOST_DLL_6); copy $? $@
$(BIN)\$(BOOST_DLL_7):$(BOOST_BIN)\$(BOOST_DLL_7); copy $? $@
$(BIN)\$(BOOST_DLL_8):$(BOOST_BIN)\$(BOOST_DLL_8); copy $? $@

$(BIN)\$(DBGHELP_DLL):$(DBGHELP_BIN)\$(DBGHELP_DLL); copy $? $@

$(BIN)\$(LOG4CXX_DLL):$(LOG4CXX_BIN)\$(LOG4CXX_DLL); copy $? $@

$(BIN)\$(MICROSOFT_VCP_DLL):$(MICROSOFT_VC)\$(MICROSOFT_VCP_DLL); copy $? $@
$(BIN)\$(MICROSOFT_RT_DLL):$(MICROSOFT_VC)\$(MICROSOFT_RT_DLL);  copy $? $@
$(BIN)\$(MICROSOFT_SQL_DLL):$(MICROSOFT_SQL)\$(MICROSOFT_SQL_DLL); copy $? $@

$(BIN)\$(OPENSSL_DLL_1):$(OPENSSL_BIN)\$(OPENSSL_DLL_1); copy $? $@
$(BIN)\$(OPENSSL_DLL_2):$(OPENSSL_BIN)\$(OPENSSL_DLL_2); copy $? $@

$(BIN)\$(SQLAPI_DLL):$(SQLAPI_BIN)\$(SQLAPI_DLL); copy $? $@

$(BIN)\$(TCL_DLL):$(TCL_BIN)\$(TCL_DLL); copy $? $@

$(BIN)\$(XERCES_DLL):$(XERCES_BIN)\$(XERCES_DLL); copy $? $@


