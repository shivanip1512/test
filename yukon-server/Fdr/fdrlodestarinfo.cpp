#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*****************************************************************************
*
*    FILE NAME: fdrlodestarinfo.cpp
*
*    DATE: 03/31/2004
*
*    AUTHOR: Julie Richter
*
*    PURPOSE: class header CtiFDRD_LodeStarInfoTable
*
*    DESCRIPTION: represents a FDR destination record
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/


/** include files **/
#include "fdrlodestarinfo.h"

CtiFDR_LodeStarInfoTable::CtiFDR_LodeStarInfoTable(RWCString &aDrivePath, RWCString &aFileName)
: _lodeStarDrivePath(aDrivePath),
  _lodeStarFileName(aFileName)
{
} 
    
CtiFDR_LodeStarInfoTable::~CtiFDR_LodeStarInfoTable()
{
}

CtiFDR_LodeStarInfoTable &CtiFDR_LodeStarInfoTable::operator=( const CtiFDR_LodeStarInfoTable &other )
{   
    if(this != &other)
    {
        _lodeStarDrivePath = other.getLodeStarDrivePath();
        _lodeStarFileName = other.getLodeStarFileName();
    }
    return *this;
}

RWCString & CtiFDR_LodeStarInfoTable::getLodeStarDrivePath(void)
{
    return _lodeStarDrivePath;
}    
RWCString CtiFDR_LodeStarInfoTable::getLodeStarDrivePath(void) const
{
    return _lodeStarDrivePath;
}
CtiFDR_LodeStarInfoTable& CtiFDR_LodeStarInfoTable::setLodeStarDrivePath(RWCString aDrivePath)
{
    _lodeStarDrivePath = aDrivePath;
    return *this;
}
    
RWCString & CtiFDR_LodeStarInfoTable::getLodeStarFileName(void)
{
    return _lodeStarFileName;
}
RWCString CtiFDR_LodeStarInfoTable::getLodeStarFileName(void) const
{
    return _lodeStarFileName;
}
    
CtiFDR_LodeStarInfoTable& CtiFDR_LodeStarInfoTable::setLodeStarFileName(RWCString aFileName)
{
    _lodeStarFileName = aFileName; 
    return *this;

}
/*int operator==(const CtiFDR_LodeStarInfoTable &lodeStarInfoList1, const CtiFDR_LodeStarInfoTable &lodeStarInfoList2)
{
    const char * ptr1;
    const char * ptr2;

    ptr1 = lodeStarInfoList1.getLodeStarFileName().data();
    ptr2 = lodeStarInfoList2.getLodeStarFileName().data();

    return !strcmp(ptr1, ptr2);
}
*/
