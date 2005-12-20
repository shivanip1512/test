/*-----------------------------------------------------------------------------*
*
*    FILE NAME: TextFileInterfaceParts.cpp
*
*    DATE: 04/14/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/textfileinterfaceparts.cpp-arc  $
*    REVISION     :  $Revision: 1.4 $
*    DATE         :  $Date: 2005/12/20 17:17:15 $
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
      Revision 1.4  2005/12/20 17:17:15  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/02/10 23:23:52  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

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
#include "yukon.h"


#include <windows.h>

/** include files **/

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "TextFileInterfaceParts.h"


// Constructors, Destructor, and Operators
CtiFDRTextFileInterfaceParts::CtiFDRTextFileInterfaceParts(string &aFileName, string &aPath, int aInterval)
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

string & CtiFDRTextFileInterfaceParts::getFileName()
{
    return _fileName;
}

string  CtiFDRTextFileInterfaceParts::getFileName() const
{
    return _fileName;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setFileName (string aFile)
{
    _fileName = aFile;
    return *this;
}

string & CtiFDRTextFileInterfaceParts::getDriveAndPath()
{
    return _driveAndPath;
}

string  CtiFDRTextFileInterfaceParts::getDriveAndPath() const
{
    return _driveAndPath;
}

CtiFDRTextFileInterfaceParts &CtiFDRTextFileInterfaceParts::setDriveAndPath (string aPath)
{
    _driveAndPath = aPath;
    return *this;
}



