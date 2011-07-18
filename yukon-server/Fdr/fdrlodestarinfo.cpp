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
****************************************************************************/
#include "precompiled.h"

#include "fdrlodestarinfo.h"

using std::string;

CtiFDR_LodeStarInfoTable::CtiFDR_LodeStarInfoTable(string &aDrivePath, string &aFileName,
                                                   string &aFolderName)
: _lodeStarDrivePath(aDrivePath),
  _lodeStarFileName(aFileName),
  _lodeStarFolderName(aFolderName)
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

string & CtiFDR_LodeStarInfoTable::getLodeStarDrivePath(void)
{
    return _lodeStarDrivePath;
}
string CtiFDR_LodeStarInfoTable::getLodeStarDrivePath(void) const
{
    return _lodeStarDrivePath;
}
CtiFDR_LodeStarInfoTable& CtiFDR_LodeStarInfoTable::setLodeStarDrivePath(string aDrivePath)
{
    _lodeStarDrivePath = aDrivePath;
    return *this;
}

string & CtiFDR_LodeStarInfoTable::getLodeStarFileName(void)
{
    return _lodeStarFileName;
}
string CtiFDR_LodeStarInfoTable::getLodeStarFileName(void) const
{
    return _lodeStarFileName;
}

CtiFDR_LodeStarInfoTable& CtiFDR_LodeStarInfoTable::setLodeStarFileName(string aFileName)
{
    _lodeStarFileName = aFileName;
    return *this;

}

string & CtiFDR_LodeStarInfoTable::getLodeStarFolderName(void)
{
    return _lodeStarFolderName;
}
string CtiFDR_LodeStarInfoTable::getLodeStarFolderName(void) const
{
    return _lodeStarFolderName;
}

CtiFDR_LodeStarInfoTable& CtiFDR_LodeStarInfoTable::setLodeStarFolderName(string aFolderName)
{
    _lodeStarFolderName = aFolderName;
    return *this;

}
/*int operator==(const CtiFDR_LodeStarInfoTable &lodeStarInfoList1, const CtiFDR_LodeStarInfoTable &lodeStarInfoList2)
{
    const char * ptr1;
    const char * ptr2;

    ptr1 = lodeStarInfoList1.getLodeStarFileName().c_str();
    ptr2 = lodeStarInfoList2.getLodeStarFileName().c_str();

    return !strcmp(ptr1, ptr2);
}
*/
