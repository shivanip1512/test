#pragma once
  
#include "dlldefs.h"

class IM_EX_FDRBASE CtiFDRTextFileInterfaceParts 
{
    public:
        // constructors and destructors
        CtiFDRTextFileInterfaceParts(std::string &aFileName=std::string ("yukon.txt"), std::string &aPath=std::string("\\yukon\\server\\import"), int aInterval=900); 
        virtual ~CtiFDRTextFileInterfaceParts();
    
        std::string & getFileName();
        std::string  getFileName() const;
        CtiFDRTextFileInterfaceParts &setFileName (std::string aName);
    
        std::string & getDriveAndPath();
        std::string  getDriveAndPath() const;
        CtiFDRTextFileInterfaceParts &setDriveAndPath (std::string aDriveAndPath);

        int getInterval() const;
        CtiFDRTextFileInterfaceParts &setInterval (int aInterval);
    
    private:
        std::string      _fileName;
        std::string      _driveAndPath;
        int            _interval;
};
