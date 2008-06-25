/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangecustomer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeCustomer.
                        CtiLMEnergyExchangeCustomer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/8/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "utility.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeCustomer, CTILMENERGYEXCHANGECUSTOMER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer::CtiLMEnergyExchangeCustomer()
{
}

CtiLMEnergyExchangeCustomer::CtiLMEnergyExchangeCustomer(RWDBReader& rdr)
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
            if( !stringCompareIgnoreCase(((CtiLMEnergyExchangeCustomerReply*)_lmenergyexchangecustomerreplies[i])->getAcceptStatus(), CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus) )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomer::restoreGuts(RWvistream& istrm)
{
    CtiLMCICustomerBase::restoreGuts( istrm );

    istrm >> _lmenergyexchangecustomerreplies;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomer::saveGuts(RWvostream& ostrm ) const
{
    CtiLMCICustomerBase::saveGuts( ostrm );

    ostrm << _lmenergyexchangecustomerreplies;

    return;
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

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomer::restore(RWDBReader& rdr)
{
    CtiLMCICustomerBase::restore(rdr);
}

// Static Members

// Possible

