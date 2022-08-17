#include "precompiled.h"

#include "dbaccess.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "utility.h"

using std::vector;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMEnergyExchangeCustomer, CTILMENERGYEXCHANGECUSTOMER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer::CtiLMEnergyExchangeCustomer()
{
}

CtiLMEnergyExchangeCustomer::CtiLMEnergyExchangeCustomer(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMEnergyExchangeCustomer::CtiLMEnergyExchangeCustomer(const CtiLMEnergyExchangeCustomer& customer)
{
    operator=(customer);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer::~CtiLMEnergyExchangeCustomer()
{
    delete_container( _lmenergyexchangecustomerreplies );
    _lmenergyexchangecustomerreplies.clear();
}

vector<CtiLMEnergyExchangeCustomerReply*>& CtiLMEnergyExchangeCustomer::getLMEnergyExchangeCustomerReplies()
{
    return _lmenergyexchangecustomerreplies;
}

const vector<CtiLMEnergyExchangeCustomerReply*>& CtiLMEnergyExchangeCustomer::getLMEnergyExchangeCustomerReplies() const
{
    return _lmenergyexchangecustomerreplies;
}

/*-------------------------------------------------------------------------
    hasAcceptedOffer

    Returns true if this customer has accepted any revisions for a given
    offerid.
--------------------------------------------------------------------------*/
BOOL CtiLMEnergyExchangeCustomer::hasAcceptedOffer(LONG offerid) const
{
    BOOL returnBoolean = FALSE;


    for(LONG i=0;i<_lmenergyexchangecustomerreplies.size();i++)
    {
        if( ((CtiLMEnergyExchangeCustomerReply*)_lmenergyexchangecustomerreplies[i])->getOfferId() == offerid )
        {
            if( ciStringEqual(((CtiLMEnergyExchangeCustomerReply*)_lmenergyexchangecustomerreplies[i])->getAcceptStatus(), CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus) )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer* CtiLMEnergyExchangeCustomer::replicate() const
{
    return (CTIDBG_new CtiLMEnergyExchangeCustomer(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::operator=(const CtiLMEnergyExchangeCustomer& right)
{
    if( this != &right )
    {
        CtiLMCICustomerBase::operator=(right);

        delete_container(_lmenergyexchangecustomerreplies );
        _lmenergyexchangecustomerreplies.clear();
        for(LONG i=0;i<right._lmenergyexchangecustomerreplies.size();i++)
        {
            _lmenergyexchangecustomerreplies.push_back(((CtiLMEnergyExchangeCustomerReply*)right._lmenergyexchangecustomerreplies[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomer::restore(Cti::RowReader &rdr)
{
    CtiLMCICustomerBase::restore(rdr);
}

// Static Members

// Possible

