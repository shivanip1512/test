#pragma once

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

DECLARE_COLLECTABLE( CtiLMEnergyExchangeCustomer );

    CtiLMEnergyExchangeCustomer();
    CtiLMEnergyExchangeCustomer(Cti::RowReader &rdr);
    CtiLMEnergyExchangeCustomer(const CtiLMEnergyExchangeCustomer& customer);

    virtual ~CtiLMEnergyExchangeCustomer();
    
    std::vector<CtiLMEnergyExchangeCustomerReply*>& getLMEnergyExchangeCustomerReplies();
    const std::vector<CtiLMEnergyExchangeCustomerReply*>& getLMEnergyExchangeCustomerReplies() const;

    BOOL hasAcceptedOffer(LONG offerid) const;
    CtiLMEnergyExchangeCustomer* replicate() const;

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
