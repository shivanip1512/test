
/*-----------------------------------------------------------------------------*
*
* File:   mgr_exclusion
*
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/29 20:22:38 $
*
* HISTORY      :
* $Log: mgr_exclusion.cpp,v $
* Revision 1.2  2004/04/29 20:22:38  cplender
* IR
*
* Revision 1.1.2.1  2004/04/15 23:30:19  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "mgr_exclusion.h"
#include "guard.h"
#include "sema.h"
#include "dbaccess.h"

inline void applyClearExclusions(const long key, CtiExclusionSPtr Exclusion, void* d)
{
    Exclusion->clearExclusions();
    return;
}

void CtiExclusionManager::refreshExclusions(LONG id)
{
    LONG        lTemp = 0;
    ptr_type    pTemp;

    LockGuard  guard(getMux());       // Protect our iteration!

    // clear the exclusion lists.
    apply( applyClearExclusions, NULL);


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();

    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Looking for Device Exclusions" << endl;
    }
    CtiTablePaoExclusion::getSQL( db, keyTable, selector );

    if(id > 0)
    {
        selector.where(keyTable["paoid"] == id && selector.where());
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << selector.asString() << endl;
    }

    RWDBReader rdr = selector.reader(conn);

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["paoid"] >> lTemp;            // get the DeviceID

        pTemp = getEqual(lTemp);

        if(!pTemp)
        {
            CtiDeviceExclusion *pSp = new CtiDeviceExclusion(lTemp);

            CtiTablePaoExclusion exclusion;
            exclusion.DecodeDatabaseReader(rdr);

            pSp->addExclusion(exclusion);

            _smartMap.insert( pSp->getId(), pSp );    // Stuff it in the list
        }
        else
        {
            CtiTablePaoExclusion paox;
            paox.DecodeDatabaseReader(rdr);
            // Add this exclusion into the list.
            pTemp->addExclusion(paox);
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Done looking for Device Exclusions" << endl;
    }
}

CtiExclusionManager::ptr_type CtiExclusionManager::getEqual(LONG id)
{
    ptr_type p;
    try
    {
        p = _smartMap.find(id);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return p;
}

void CtiExclusionManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiPortManager::apply " << endl;
                }
                return;
            }
        }

        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            applyFun( itr->first, itr->second, d);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiExclusionManager::spiterator CtiExclusionManager::begin()
{
    return _smartMap.getMap().begin();
}
CtiExclusionManager::spiterator CtiExclusionManager::end()
{
    return _smartMap.getMap().end();
}


void CtiExclusionManager::DumpList(void)
{
    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);
        }

        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            itr->second->Dump();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


