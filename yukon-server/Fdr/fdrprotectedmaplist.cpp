/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrprotectedmaplist.cpp
*
*    DATE: 04/25/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrprotectedmaplist.cpp-arc  $
*    REVISION     :  $Revision: 1.4 $
*    DATE         :  $Date: 2005/02/10 23:23:51 $
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


#include <windows.h>
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
#if 0
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

