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

#include <rw/cstring.h>
#include "dlldefs.h"
#include "fdr.h"


class IM_EX_FDRBASE CtiFDR_LodeStarInfoTable 
{
public:
    CtiFDR_LodeStarInfoTable(RWCString &aDrivePath, RWCString &aFileName,RWCString &aFolderName);
    ~CtiFDR_LodeStarInfoTable();
    CtiFDR_LodeStarInfoTable& operator=( const CtiFDR_LodeStarInfoTable &other );
    RWCString & getLodeStarDrivePath(void);
    RWCString getLodeStarDrivePath(void) const;
    RWCString & getLodeStarFileName(void);
    RWCString getLodeStarFileName(void) const;
    RWCString & getLodeStarFolderName(void);
    RWCString getLodeStarFolderName(void) const;
    CtiFDR_LodeStarInfoTable& setLodeStarDrivePath(RWCString aDrivePath);
    CtiFDR_LodeStarInfoTable& setLodeStarFileName(RWCString aFileName);
    CtiFDR_LodeStarInfoTable& setLodeStarFolderName(RWCString aFolderName);
private:
    RWCString _lodeStarDrivePath;
    RWCString _lodeStarFileName;
    RWCString _lodeStarFolderName;

};

#endif
