#include "precompiled.h"

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



