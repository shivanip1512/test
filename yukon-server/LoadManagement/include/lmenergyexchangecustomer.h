/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangecustomer.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMEnergyExchangeCustomer

        Initial Date:  5/8/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMENERGYEXCHANGECUSTOMERIMPL_H
#define CTILMENERGYEXCHANGECUSTOMERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiLMEnergyExchangeCustomer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeCustomer )

    CtiLMEnergyExchangeCustomer();
    CtiLMEnergyExchangeCustomer(RWDBReader& rdr);
    CtiLMEnergyExchangeCustomer(const CtiLMEnergyExchangeCustomer& customer);

    virtual ~CtiLMEnergyExchangeCustomer();
    
    ULONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    ULONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    ULONG getCustomerOrder() const;
    const RWCString& getCustTimeZone() const;
    RWOrdered& getLMEnergyExchangeCustomerReplies();

    CtiLMEnergyExchangeCustomer& setPAOId(ULONG id);
    CtiLMEnergyExchangeCustomer& setPAOCategory(const RWCString& category);
    CtiLMEnergyExchangeCustomer& setPAOClass(const RWCString& pclass);
    CtiLMEnergyExchangeCustomer& setPAOName(const RWCString& name);
    CtiLMEnergyExchangeCustomer& setPAOType(ULONG type);
    CtiLMEnergyExchangeCustomer& setPAODescription(const RWCString& description);
    CtiLMEnergyExchangeCustomer& setDisableFlag(BOOL disable);
    CtiLMEnergyExchangeCustomer& setCustomerOrder(ULONG order);
    CtiLMEnergyExchangeCustomer& setCustTimeZone(const RWCString& timezone);

    BOOL hasAcceptedOffer(ULONG offerid) const;
    CtiLMEnergyExchangeCustomer* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeCustomer& operator=(const CtiLMEnergyExchangeCustomer& right);

    int operator==(const CtiLMEnergyExchangeCustomer& right) const;
    int operator!=(const CtiLMEnergyExchangeCustomer& right) const;

    // Static Members

    // Possible 

protected:

    void restore(RWDBReader& rdr);

private:

    ULONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    ULONG _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    ULONG _customerorder;
    RWCString _custtimezone;

    RWOrdered _lmenergyexchangecustomerreplies;

    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
#endif

