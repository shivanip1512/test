
/*-----------------------------------------------------------------------------*
*
* File:   mgr_exclusion
*
* Class:  CtiExclusionManager
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/05/10 21:35:51 $
* HISTORY      :
* $Log: mgr_exclusion.h,v $
* Revision 1.3  2004/05/10 21:35:51  cplender
* Exclusions a'la GRE are a bit closer here.  The proximity exclusions should work ok now.
*
* Revision 1.2  2004/04/29 20:22:38  cplender
* IR
*
* Revision 1.1.2.1  2004/04/15 23:30:19  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __MGR_EXCLUSION_H__
#define __MGR_EXCLUSION_H__

#include "dlldefs.h"
#include "dev_base.h"
#include "smartmap.h"

class IM_EX_DEVDB CtiExclusionManager
{
public:

    typedef CtiLockGuard<CtiMutex>      LockGuard;
    typedef CtiSmartMap< CtiDevice >    coll_type;              // This is the collection type!
    typedef coll_type::ptr_type         ptr_type;
    typedef coll_type::spiterator       spiterator;
    typedef coll_type::insert_pair      insert_pair;

protected:

    coll_type   _smartMap;
    CtiMutex    _mux;

private:

public:

    CtiExclusionManager() {}

    CtiExclusionManager(const CtiExclusionManager& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiExclusionManager() {}

    spiterator begin();
    spiterator end();

    CtiMutex & getMux()     { return _mux; }

    void addDevice(CtiExclusionManager::ptr_type dev);
    ptr_type getEqual(LONG id);
    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    void dumpList(void);


};
#endif // #ifndef __MGR_EXCLUSION_H__
