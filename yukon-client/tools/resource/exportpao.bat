rem Export a pao object to a file for later importing

call setjavapath.bat
java -cp %YUKON_BASE%/Client/bin/tools.jar com.cannontech.dbtools.tools.ExportPAO %1 %2