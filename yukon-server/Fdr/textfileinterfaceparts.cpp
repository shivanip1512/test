/*-----------------------------------------------------------------------------*
*
*    FILE NAME: TextFileInterfaceParts.cpp
*
*    DATE: 04/14/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/textfileinterfaceparts.cpp-arc  $
*    REVISION     :  $Revision: 1.4.24.1 $
*    DATE         :  $Date: 2008/11/13 17:23:48 $
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
      Revision 1.4.24.1  2008/11/13 17:23:48  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

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


/** include files **/

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "TextFileInterfaceParts.h"

using std::string;

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



