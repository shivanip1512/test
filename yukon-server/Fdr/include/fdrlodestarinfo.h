#pragma once

#include <windows.h>    
#include <string>
#include "dlldefs.h"
#include "fdr.h"


class IM_EX_FDRBASE CtiFDR_LodeStarInfoTable 
{
public:
    DEBUG_INSTRUMENTATION;

    CtiFDR_LodeStarInfoTable(std::string &aDrivePath, std::string &aFileName,std::string &aFolderName);
    ~CtiFDR_LodeStarInfoTable();
    CtiFDR_LodeStarInfoTable& operator=( const CtiFDR_LodeStarInfoTable &other );
    std::string & getLodeStarDrivePath(void);
    std::string getLodeStarDrivePath(void) const;
    std::string & getLodeStarFileName(void);
    std::string getLodeStarFileName(void) const;
    std::string & getLodeStarFolderName(void);
    std::string getLodeStarFolderName(void) const;
    CtiFDR_LodeStarInfoTable& setLodeStarDrivePath(std::string aDrivePath);
    CtiFDR_LodeStarInfoTable& setLodeStarFileName(std::string aFileName);
    CtiFDR_LodeStarInfoTable& setLodeStarFolderName(std::string aFolderName);
private:
    std::string _lodeStarDrivePath;
    std::string _lodeStarFileName;
    std::string _lodeStarFolderName;

};
