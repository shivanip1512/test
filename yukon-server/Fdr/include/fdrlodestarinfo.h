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
#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
                        //         with ctitypes.h...  i only put this in here because
                        //         the compiler was having fits with BOOL.
#include <string>
#include "dlldefs.h"
#include "fdr.h"
using std::string;


class IM_EX_FDRBASE CtiFDR_LodeStarInfoTable 
{
public:
    CtiFDR_LodeStarInfoTable(string &aDrivePath, string &aFileName,string &aFolderName);
    ~CtiFDR_LodeStarInfoTable();
    CtiFDR_LodeStarInfoTable& operator=( const CtiFDR_LodeStarInfoTable &other );
    string & getLodeStarDrivePath(void);
    string getLodeStarDrivePath(void) const;
    string & getLodeStarFileName(void);
    string getLodeStarFileName(void) const;
    string & getLodeStarFolderName(void);
    string getLodeStarFolderName(void) const;
    CtiFDR_LodeStarInfoTable& setLodeStarDrivePath(string aDrivePath);
    CtiFDR_LodeStarInfoTable& setLodeStarFileName(string aFileName);
    CtiFDR_LodeStarInfoTable& setLodeStarFolderName(string aFolderName);
private:
    string _lodeStarDrivePath;
    string _lodeStarFileName;
    string _lodeStarFolderName;

};

#endif
