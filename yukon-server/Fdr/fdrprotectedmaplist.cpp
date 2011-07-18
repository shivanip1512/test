#include "precompiled.h"

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

