
/*-----------------------------------------------------------------------------*
*
* File:   mgr_exclusion
*
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/05/10 21:35:50 $
*
* HISTORY      :
* $Log: mgr_exclusion.cpp,v $
* Revision 1.3  2004/05/10 21:35:50  cplender
* Exclusions a'la GRE are a bit closer here.  The proximity exclusions should work ok now.
*
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


void CtiExclusionManager::addDevice(CtiExclusionManager::ptr_type dev)
{
    insert_pair ip = _smartMap.insert( dev->getID(), dev );    // This may occur multiple times...
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


void CtiExclusionManager::dumpList(void)
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
            itr->second->DumpData();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


