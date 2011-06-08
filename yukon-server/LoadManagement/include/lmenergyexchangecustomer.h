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
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmcicustomerbase.h"
#include "lmenergyexchangecustomerreply.h"

class CtiLMEnergyExchangeCustomer : public CtiLMCICustomerBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeCustomer )

    CtiLMEnergyExchangeCustomer();
    CtiLMEnergyExchangeCustomer(Cti::RowReader &rdr);
    CtiLMEnergyExchangeCustomer(const CtiLMEnergyExchangeCustomer& customer);

    virtual ~CtiLMEnergyExchangeCustomer();
    
    std::vector<CtiLMEnergyExchangeCustomerReply*>& getLMEnergyExchangeCustomerReplies();

    BOOL hasAcceptedOffer(LONG offerid) const;
    CtiLMEnergyExchangeCustomer* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeCustomer& operator=(const CtiLMEnergyExchangeCustomer& right);

    /*int operator==(const CtiLMEnergyExchangeCustomer& right) const;
    int operator!=(const CtiLMEnergyExchangeCustomer& right) const;*/

    // Static Members

    // Possible 

protected:

    void restore(Cti::RowReader &rdr);

private:

    std::vector<CtiLMEnergyExchangeCustomerReply*> _lmenergyexchangecustomerreplies;
};
#endif

