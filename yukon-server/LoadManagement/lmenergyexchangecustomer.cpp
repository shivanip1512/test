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
#include "device.h"
#include "resolvers.h"

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

    _lmenergyexchangecustomerreplies.clearAndDestroy();
}

RWOrdered& CtiLMEnergyExchangeCustomer::getLMEnergyExchangeCustomerReplies()
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

    if( _lmenergyexchangecustomerreplies.entries() > 0 )
    {
        for(LONG i=0;i<_lmenergyexchangecustomerreplies.entries();i++)
        {
            if( ((CtiLMEnergyExchangeCustomerReply*)_lmenergyexchangecustomerreplies[i])->getOfferId() == offerid )
            {
                if( !(((CtiLMEnergyExchangeCustomerReply*)_lmenergyexchangecustomerreplies[i])->getAcceptStatus().compareTo(CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus,RWCString::ignoreCase)) )
                {
                    returnBoolean = TRUE;
                    break;
                }
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
    return (new CtiLMEnergyExchangeCustomer(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::operator=(const CtiLMEnergyExchangeCustomer& right)
{
    if( this != &right )
    {
        CtiLMCICustomerBase::operator=(right);
        
        _lmenergyexchangecustomerreplies.clearAndDestroy();
        for(LONG i=0;i<right._lmenergyexchangecustomerreplies.entries();i++)
        {
            _lmenergyexchangecustomerreplies.insert(((CtiLMEnergyExchangeCustomerReply*)right._lmenergyexchangecustomerreplies[i])->replicate());
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

