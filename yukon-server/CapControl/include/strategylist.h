/*---------------------------------------------------------------------------
        Filename:  strategylist.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCStrategyList.
                        CtiCCStrategyList maintains a pool of
                        CtiCCStrategy handles.
                       

        Initial Date:  10/16/2000
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCSTRATEGYLIST_H
#define CTICCSTRATEGYLIST_H


#include <rw/vstream.h>
#include <rw/collect.h>

#include "strategy.h"
#include "ccid.h"

class CtiCCStrategyList  : public RWCollectable
{
public:   
RWDECLARE_COLLECTABLE( CtiCCStrategyList )

    RWOrdered &Strategies();

    CtiCCStrategyList();
    CtiCCStrategyList(const CtiCCStrategyList& list);
    
    virtual ~CtiCCStrategyList();
    
    CtiCCStrategyList& operator=(const CtiCCStrategyList& right);

private:

    void CtiCCStrategyList::restoreGuts(RWvistream& istrm);
    void CtiCCStrategyList::saveGuts(RWvostream& ostrm );

    RWOrdered _strategies;
        
    RWRecursiveLock<RWMutexLock> _mutex;
};

#endif

