#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: TextFileInterfaceParts.cpp
*
*    DATE: 04/14/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/textfileinterfaceparts.cpp-arc  $
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2003/04/22 20:44:46 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: pieces all text file interfaces need
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: textfileinterfaceparts.cpp,v $
      Revision 1.2  2003/04/22 20:44:46  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

   
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/


#include <windows.h>

/** include files **/
#include <rw/cstring.h>

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "TextFileInterfaceParts.h"


// Constructors, Destructor, and Operators
CtiFDRTextFileInterfaceParts::CtiFDRTextFileInterfaceParts(RWCString &aFileName, RWCString &aPath, int aInterval)
:    _fileName(aFileName),
    _driveAndPath (aPath),
    _interval(aInterval)
{ 

}


CtiFDRTextFileInterfaceParts::~CtiFDRTextFileInterfaceParts()
{
}

int CtiFDRTextFileInterfaceParts::getInterval() const
{
    return _interval;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setInterval (int aInterval)
{
    _interval = aInterval;
    return *this;
}

RWCString & CtiFDRTextFileInterfaceParts::getFileName()
{
    return _fileName;
}

RWCString  CtiFDRTextFileInterfaceParts::getFileName() const
{
    return _fileName;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setFileName (RWCString aFile)
{
    _fileName = aFile;
    return *this;
}

RWCString & CtiFDRTextFileInterfaceParts::getDriveAndPath()
{
    return _driveAndPath;
}

RWCString  CtiFDRTextFileInterfaceParts::getDriveAndPath() const
{
    return _driveAndPath;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setDriveAndPath (RWCString aPath)
{
    _driveAndPath = aPath;
    return *this;
}



