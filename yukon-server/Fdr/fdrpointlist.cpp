/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrpointlist.cpp
*
*    DATE: 04/25/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrpointlist.cpp-arc  $
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
      $Log: fdrpointlist.cpp,v $
      Revision 1.4  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.3  2002/04/16 15:58:35  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:56  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 1.0   14 Dec 2001 17:23:22   dsutton
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
#include "fdrpointlist.h"
#include "mgr_fdrpoint.h"


CtiFDRPointList::CtiFDRPointList()
{
    iPointList=NULL;
}

CtiFDRPointList::~CtiFDRPointList()
{
     delete iPointList;
}

CtiFDRPointList& CtiFDRPointList::operator=( CtiFDRPointList &other )
{
    if(this != &other)
    {
        CtiLockGuard<CtiMutex> guard(iMux);  
        iPointList = other.getPointList();
    }

    return *this;
}

CtiMutex& CtiFDRPointList::getMutex ()
{
    return iMux;
}

CtiFDRManager *CtiFDRPointList::getPointList ()
{
    return iPointList;
}

CtiFDRPointList& CtiFDRPointList::setPointList (CtiFDRPointList &aList)
{
    CtiLockGuard<CtiMutex> guard(iMux);  
    iPointList = aList.getPointList();
    return *this;
}

CtiFDRPointList& CtiFDRPointList::setPointList (CtiFDRManager *aList)
{
    CtiLockGuard<CtiMutex> guard(iMux);  
    iPointList = aList;
    return *this;
}

void CtiFDRPointList::deletePointList ()
{
    if (iPointList != NULL)
    {
        CtiLockGuard<CtiMutex> guard(iMux);  
        delete iPointList;
        iPointList = NULL;
    }
}



