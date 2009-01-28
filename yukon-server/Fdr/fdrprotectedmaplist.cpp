/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrprotectedmaplist.cpp
*
*    DATE: 04/25/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrprotectedmaplist.cpp-arc  $
*    REVISION     :  $Revision: 1.5.24.1 $
*    DATE         :  $Date: 2008/11/13 17:23:47 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Point lists protected with mutexs
*
*    DESCRIPTION: This class implements a point list with built in mutex protection
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrprotectedmaplist.cpp,v $
      Revision 1.5.24.1  2008/11/13 17:23:47  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.5  2006/03/02 23:03:19  tspar
      Phase Three: Final  phase of RWTPtrSlist replacement.

      Revision 1.4  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.3  2002/04/16 15:58:35  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:56  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.1   14 Dec 2001 17:22:18   dsutton
   no longer used, not fdrpointlist.cpp
   
      Rev 2.0   06 Sep 2001 13:21:36   cplender
   Promote revision
   
      Rev 1.0   10 May 2001 11:16:06   dsutton
   Initial revision.
   
      Rev 1.0   23 Apr 2001 11:17:58   dsutton
   Initial revision.
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"

#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrpoint.h"
#include "fdrprotectedmaplist.h"


CtiFDRProtectedIdMapList::CtiFDRProtectedIdMapList()
{}

CtiFDRProtectedIdMapList::~CtiFDRProtectedIdMapList()
{
     iPointList.clearAndDestroy();
}

CtiFDRProtectedIdMapList& CtiFDRProtectedIdMapList::operator=( const CtiFDRProtectedIdMapList &other )
{
    if(this != &other)
    {
        CtiLockGuard<CtiMutex> guard(iMux);  
        iPointList = other.getIdMapList();
#if 0  //If you remove this   below needs to be changed to deal with a std::list from RWptrSlist.
        iPointList.clearAndDestroy();

        for (int x=0; x < other.getIdMapList().entries(); x++)
        {
            CtiFDRPointIdMap *pointIdMap = new CtiFDRPointIdMap (*other.getIdMapList()[x]);

            iPointList.insert(pointIdMap);
        }
#endif
    }

    return *this;
}

CtiMutex& CtiFDRProtectedIdMapList::getMutex ()
{
    return iMux;
}

/*
CtiFDRProtectedIdMapList& setMutex (CtiMutex aMutex)
{
    iMux = aMutex;
    return *this;
}
*/
FDRIdMapList CtiFDRProtectedIdMapList::getIdMapList () const
{
    return iPointList;
}

FDRIdMapList& CtiFDRProtectedIdMapList::getIdMapList ()
{
    return iPointList;
}



CtiFDRProtectedIdMapList& CtiFDRProtectedIdMapList::setIdMapList (CtiFDRProtectedIdMapList &aList)
{
    iPointList = aList.getIdMapList();
    return *this;
}

