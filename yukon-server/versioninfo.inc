
#
# Product Version correspond to the first word taken from the BUILD_VERSION that have a valid versioning format
# The allowed formats is one of the following: A.B.C.D or A.B.C or A.B
# The versioning information is given to the resource compiler (see versioninfo.rc) in 2 distinct formats :
# binary and text, where the binary version is reformatted to A,B,C,D
#

%foreach word in $(BUILD_VERSION)
        %ifndef PRODUCT_VERSION
                start_end=START$(word)END
        bad_word=$[StrReplace,"..","BADWORD",$[StrReplace,"START.","BADWORD",$[StrReplace,".END","BADWORD",$(start_end)]]]
        no_digits=$[StrReplace,"0","",$[StrReplace,"1","",$[StrReplace,"2","",$[StrReplace,"3","",$[StrReplace,"4","",$[StrReplace,"5","",$[StrReplace,"6","",$[StrReplace,"7","",$[StrReplace,"8","",$[StrReplace,"9","",$(bad_word)]]]]]]]]]]
                %if "$(no_digits)" == "START...END"
                        PRODUCT_VERSION=$(word)
                        PRODUCT_VERSION_DOT=$(word)
                %elseif "$(no_digits)" == "START..END"
                        PRODUCT_VERSION=$(word)
                        PRODUCT_VERSION_DOT=$(word).0
                %elseif "$(no_digits)" == "START.END"
                        PRODUCT_VERSION=$(word)
                        PRODUCT_VERSION_DOT=$(word).0.0
                %endif
        %endif
%endfor

%ifndef PRODUCT_VERSION_DOT
        PRODUCT_VERSION_DOT=0.0.0.0
%endif

D_PRODUCT_VERSION=$[StrReplace,".",",",$(PRODUCT_VERSION_DOT)]
D_PRODUCT_VERSION_STR="$(PRODUCT_VERSION)"

#
# File Version correspond to the first word taken from the BUILD_VERSION_DETAILS that have a valid versioning format
# The allowed formats is one of the following: A.B.C.D or A.B.C or A.B
# The versioning information is given to the resource compiler (see versioninfo.rc) in 2 distinct formats :
# binary and text, where the binary version is reformatted to A,B,C,D
#

%ifndef BUILD_NUMBER
        BUILD_NUMBER=0
%endif

%foreach word in $(BUILD_VERSION_DETAILS)
        %ifndef FILE_VERSION
                start_end=START$(word)END
        bad_word=$[StrReplace,"..","BADWORD",$[StrReplace,"START.","BADWORD",$[StrReplace,".END","BADWORD",$(start_end)]]]
        no_digits=$[StrReplace,"0","",$[StrReplace,"1","",$[StrReplace,"2","",$[StrReplace,"3","",$[StrReplace,"4","",$[StrReplace,"5","",$[StrReplace,"6","",$[StrReplace,"7","",$[StrReplace,"8","",$[StrReplace,"9","",$(bad_word)]]]]]]]]]]
                %if "$(no_digits)" == "START...END"
                        FILE_VERSION=$(word)
                        FILE_VERSION_DOT=$(word)
                %elseif "$(no_digits)" == "START..END"
                        FILE_VERSION=$(word)
                        FILE_VERSION_DOT=$(word).$(BUILD_NUMBER)
                %elseif "$(no_digits)" == "START.END"
                        FILE_VERSION=$(word)
                        FILE_VERSION_DOT=$(word).0.$(BUILD_NUMBER)
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

$(RES_FILES): $(COMPILEBASE)\versioninfo.rc
                @echo Creating $@
                rc -R -I..\COMMON\include -DD_FILE_NAME="$[StrReplace,"$(OBJ)\\","",$[StrReplace,".res","",$@]]" -FO$@ $<

FORCE:
