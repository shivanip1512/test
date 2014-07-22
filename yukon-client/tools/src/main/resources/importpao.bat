rem imports a pao object from a binary file

call setjavapath.bat
java -cp %YUKON_BASE%/Client/bin/tools.jar com.cannontech.dbtools.tools.ImportPAO %1 %2