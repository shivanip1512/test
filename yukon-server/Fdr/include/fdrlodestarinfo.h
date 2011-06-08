#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrlodestarinfo.h
*
*    DATE: 03/31/2004
*
*    AUTHOR: Julie Richter
*
*    PURPOSE: CtiFDR_LodeStarInfoTable class header
*
*    DESCRIPTION: contains destination information for a point 
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRLODESTARINFO_H__
#define __FDRLODESTARINFO_H__

/** include files **/

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    
#include <string>
#include "dlldefs.h"
#include "fdr.h"


class IM_EX_FDRBASE CtiFDR_LodeStarInfoTable 
{
public:
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

#endif
