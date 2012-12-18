
%foreach word in $(BUILD_VERSION)
	%ifndef PRODUCT_VERSION_DOT
		no_digits=$[StrReplace,"0","",$[StrReplace,"1","",$[StrReplace,"2","",$[StrReplace,"3","",$[StrReplace,"4","",$[StrReplace,"5","",$[StrReplace,"6","",$[StrReplace,"7","",$[StrReplace,"8","",$[StrReplace,"9","",$[StrReplace,"..","BADWORD",$(word)]]]]]]]]]]]
		%if "$(no_digits)" == "..."
			PRODUCT_VERSION_DOT=$(word)
		%elseif "$(no_digits)" == ".."
			PRODUCT_VERSION_DOT=$(word).0
		%elseif "$(no_digits)" == "."
			PRODUCT_VERSION_DOT=$(word).0.0
		%elseif "$(no_digits)" == ""
			PRODUCT_VERSION_DOT=$(word).0.0.0
		%endif
	%endif
%endfor

%ifndef PRODUCT_VERSION_DOT
	PRODUCT_VERSION_DOT=0.0.0.0
%endif

D_PRODUCT_VERSION=$[StrReplace,".",",",$(PRODUCT_VERSION_DOT)]
D_PRODUCT_VERSION_STR="$(PRODUCT_VERSION_DOT)"

%foreach word in $(BUILD_VERSION_DETAILS)
	%ifndef FILE_VERSION_DOT
		no_digits=$[StrReplace,"0","",$[StrReplace,"1","",$[StrReplace,"2","",$[StrReplace,"3","",$[StrReplace,"4","",$[StrReplace,"5","",$[StrReplace,"6","",$[StrReplace,"7","",$[StrReplace,"8","",$[StrReplace,"9","",$[StrReplace,"..","BADWORD",$(word)]]]]]]]]]]]
		%if "$(no_digits)" == "..."
			FILE_VERSION_DOT=$(word)
		%elseif "$(no_digits)" == ".."
			FILE_VERSION_DOT=$(word).0
		%elseif "$(no_digits)" == "."
			FILE_VERSION_DOT=$(word).0.0
		%elseif "$(no_digits)" == ""
			FILE_VERSION_DOT=$(word).0.0.0
		%endif
	%endif
%endfor

%ifndef FILE_VERSION_DOT
	FILE_VERSION_DOT=0.0.0.0
%endif

D_FILE_VERSION=$[StrReplace,".",",",$(FILE_VERSION_DOT)]
D_FILE_VERSION_STR="$(FILE_VERSION_DOT)"

%ifdef PROGS_VERSION
	RES_FILES=$(OBJ)\$[StrReplace,".dll",".res",$[StrReplace,".exe",".res",$[Separators," $(OBJ)\\",$(PROGS_VERSION)]]]
%endif

$(RES_FILES): $(COMPILEBASE)\versioninfo.rc FORCE
		@echo Creating $@
		rc -R -DD_FILE_VERSION_STR=$(D_FILE_VERSION_STR) -DD_PRODUCT_VERSION_STR=$(D_PRODUCT_VERSION_STR) -DD_FILE_VERSION=$(D_FILE_VERSION) -DD_PRODUCT_VERSION=$(D_PRODUCT_VERSION) -DD_FILE_NAME="$[StrReplace,"$(OBJ)\\","",$[StrReplace,".res","",$@]]" -FO$@ $<

FORCE:
		