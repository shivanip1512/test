#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrprotectedmaplist.cpp
*
*    DATE: 04/25/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrprotectedmaplist.cpp-arc  $
*    REVISION     :  $Revision: 1.1.1.1 $
*    DATE         :  $Date: 2002/04/12 13:59:40 $
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
      Revision 1.1.1.1  2002/04/12 13:59:40  cplender
      Imported sources 4/12/01

   
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

