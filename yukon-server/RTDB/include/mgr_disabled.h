/*-----------------------------------------------------------------------------*
*
* File:   mgr_disabled.cpp
*
* Class:  CtiDisabledManager
* Date:   1/6/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_disabled.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2006/02/10 17:15:11 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DISABLED_MGR_H__
#define __DISABLED_MGR_H__
#include "yukon.h"

#include <rw/db/db.h>

#include "dlldefs.h"
#include "disable_entry.h"
#include "msg_dbchg.h"
#include "smartmap.h"

class IM_EX_DEVDB CtiDisabledManager
{
private:

    CtiSmartMap< CtiDisabledEntry > _smartMap;  //This contains all of the objects
    CtiSmartMap< CtiDisabledEntry > _portMap;   //These have the objects split among them
    CtiSmartMap< CtiDisabledEntry > _deviceMap;
    CtiSmartMap< CtiDisabledEntry > _routeMap;
    CtiSmartMap< CtiDisabledEntry > _transmitterMap;

    void appendDependencies(RWDBReader &rdr);
    void appendMacroDependencies(RWDBReader &rdr);
    void loadPaoObjects(RWDBReader &rdr);
    void addDisabled(CtiDisabledEntrySPtr object);
    void removeDisabled(CtiDisabledEntrySPtr object);
    void markDisabledObjects();

public:

    typedef CtiLockGuard<CtiMutex>                          LockGuard;
    typedef CtiSmartMap< CtiDisabledEntry >                 coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiDisabledEntry >::ptr_type       ptr_type;
    typedef CtiSmartMap< CtiDisabledEntry >::spiterator     spiterator;
    typedef CtiSmartMap< CtiDisabledEntry >::insert_pair    insert_pair;

    CtiDisabledManager();
    virtual ~CtiDisabledManager();

    void RefreshList(CtiDBChangeMsg *pChg);
    bool isObjectDisabled(long paoID);
};

#endif                  // #ifndef __DISABLED_MGR_H__
