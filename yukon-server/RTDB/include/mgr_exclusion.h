
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/29 20:22:38 $
* HISTORY      :
* $Log: mgr_exclusion.h,v $
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
#include "dev_exclusion.h"
#include "smartmap.h"

class IM_EX_DEVDB CtiExclusionManager
{
public:

    typedef CtiLockGuard<CtiMutex>                         LockGuard;
    typedef CtiSmartMap< CtiDeviceExclusion >              coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiDeviceExclusion >::ptr_type    ptr_type;
    typedef CtiSmartMap< CtiDeviceExclusion >::spiterator  spiterator;
    typedef CtiSmartMap< CtiDeviceExclusion >::insert_pair insert_pair;

protected:

    CtiSmartMap< CtiDeviceExclusion >   _smartMap;
    CtiMutex                            _mux;

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

    void refreshExclusions(LONG id = 0);
    ptr_type getEqual(LONG id);
    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    void DumpList(void);


};
#endif // #ifndef __MGR_EXCLUSION_H__
