
%foreach word in $(BUILD_VERSION)
	%ifndef D_PRODUCT_VERSION
		D_PRODUCT_VERSION=$[StrReplace,".",",",$(word)]
	%endif
%endfor

D_PRODUCT_VERSION_STR="$(BUILD_VERSION)"

%foreach word in $(BUILD_VERSION_DETAILS)
	%ifndef D_FILE_VERSION
		D_FILE_VERSION=$[StrReplace,".",",",$(word)]
	%endif
%endfor

D_FILE_VERSION_STR="$(BUILD_VERSION_DETAILS)"

%ifdef PROGS_VERSION
	RES_FILES=$(OBJ)\$[StrReplace,".dll",".res",$[StrReplace,".exe",".res",$[Separators," $(OBJ)\\",$(PROGS_VERSION)]]]
%endif

$(RES_FILES): $(COMPILEBASE)\versioninfo.rc
   	       @echo Creating $@
           rc -R -DD_FILE_VERSION_STR=$(D_FILE_VERSION_STR) -DD_PRODUCT_VERSION_STR=$(D_PRODUCT_VERSION_STR) -DD_FILE_VERSION=$(D_FILE_VERSION) -DD_PRODUCT_VERSION=$(D_PRODUCT_VERSION) -DD_FILE_NAME="$[StrReplace,"$(OBJ)\\","",$[StrReplace,".res","",$@]]" -FO$@ $<
