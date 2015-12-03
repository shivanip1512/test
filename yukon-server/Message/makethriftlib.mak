include $(COMPILEBASE)\global.inc

.PATH.lib = $(BIN)

INCLPATHS+= \
-I$(BOOST_INCLUDE) \
-I$(THRIFT_INCLUDE)

THRIFTMSG_DIR = Serialization\Thrift

SOURCES_H   = $(THRIFTMSG_DIR)\$[Separators," $(THRIFTMSG_DIR)\\",$[FileList, $(THRIFTMSG_DIR)\*.h]]
SOURCES_CPP = $(THRIFTMSG_DIR)\$[Separators," $(THRIFTMSG_DIR)\\",$[FileList, $(THRIFTMSG_DIR)\*.cpp]]
OBJS        = $[StrReplace,.cpp,.obj,$(SOURCES_CPP)]

CTIPROGS = ctithriftmsg.lib

all: $(CTIPROGS)

$(CTIPROGS): $(OBJS) makethriftlib.mak
        @echo:
        @echo Creating static lib
        @%cd $(THRIFTMSG_DIR)
        lib $[StrReplace,.cpp,.obj,$[FileList,*.cpp]] /OUT:"..\..\$@"
        @%cd $(CWD)
       -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
       -if exist $@ copy $@ $(COMPILEBASE)\lib
        @echo:
        @echo Done building Target $@

$(OBJS): $(SOURCES_H) $(SOURCES_CPP)
        @echo:
        @echo Compiling cpp to obj
        @%cd $(THRIFTMSG_DIR)
        $(CC) $(CFLAGS) $(PARALLEL) $(INCLPATHS) -Fo.\ -c $[FileList,*.cpp]
        @%cd $(CWD)

copy:
       -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
       -if exist $(CTIPROGS) copy $(CTIPROGS) $(COMPILEBASE)\lib

deps:
        scandeps -Output makedll.mak $(THRIFTMSG_DIR)\*.cpp

clean:
       -del \
        $(THRIFTMSG_DIR)\*.obj \
        $(THRIFTMSG_DIR)\*.target \
        $(THRIFTMSG_DIR)\*.pdb \
        $(BIN)\*.lib
