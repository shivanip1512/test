/*---------------------------------------------------------------------------
        Filename:  strategylist.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiCCStrategyList
                        CtiCCStrategyList maintains a pool of 
                        CtiCCStrategy's.
                        
        Initial Date:  10/16/2000
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/

#include "strategylist.h"
#include "ctibase.h"
#include "logger.h"

RWDEFINE_COLLECTABLE( CtiCCStrategyList, CTICCSTRATEGYLIST_ID )

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCStrategyList::CtiCCStrategyList()
{
}

/*---------------------------------------------------------------------------
    Copy Constructor
---------------------------------------------------------------------------*/
CtiCCStrategyList::CtiCCStrategyList(const CtiCCStrategyList& stratList)
{
    operator=( stratList );
}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiCCStrategyList::~CtiCCStrategyList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    for(UINT i=0;i<_strategies.entries();i++)
    {
        ((CtiCCStrategy*)_strategies[i])->CapBankList().clearAndDestroy();
    }
    _strategies.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCStrategyList& CtiCCStrategyList::operator=(const CtiCCStrategyList& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _strategies.clearAndDestroy();
        for(UINT i=0;i<right._strategies.entries();i++)
        {
            _strategies.insert(((CtiCCStrategy*)right._strategies[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    StrategyList
    
    Returns a list of the CtiCCStrategy's
---------------------------------------------------------------------------*/    
RWOrdered& CtiCCStrategyList::Strategies( )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    return _strategies;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCStrategyList::restoreGuts(RWvistream& istrm)
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime().asString() << " - " << "***  strategystore.cpp::restoreGuts should never be called  ***" << endl;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCStrategyList::saveGuts(RWvostream& ostrm )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    RWCollectable::saveGuts( ostrm );

    ostrm << _strategies;

    return;
}

