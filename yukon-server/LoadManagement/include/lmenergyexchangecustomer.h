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
    
    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    LONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getCustomerOrder() const;
    const RWCString& getCustTimeZone() const;
    RWOrdered& getLMEnergyExchangeCustomerReplies();

    CtiLMEnergyExchangeCustomer& setPAOId(LONG id);
    CtiLMEnergyExchangeCustomer& setPAOCategory(const RWCString& category);
    CtiLMEnergyExchangeCustomer& setPAOClass(const RWCString& pclass);
    CtiLMEnergyExchangeCustomer& setPAOName(const RWCString& name);
    CtiLMEnergyExchangeCustomer& setPAOType(LONG type);
    CtiLMEnergyExchangeCustomer& setPAODescription(const RWCString& description);
    CtiLMEnergyExchangeCustomer& setDisableFlag(BOOL disable);
    CtiLMEnergyExchangeCustomer& setCustomerOrder(LONG order);
    CtiLMEnergyExchangeCustomer& setCustTimeZone(const RWCString& timezone);

    BOOL hasAcceptedOffer(LONG offerid) const;
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

    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    LONG _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    LONG _customerorder;
    RWCString _custtimezone;

    RWOrdered _lmenergyexchangecustomerreplies;
};
#endif

