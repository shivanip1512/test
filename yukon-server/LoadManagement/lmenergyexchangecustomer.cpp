/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangecustomer.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMEnergyExchangeCustomer.
                        CtiLMEnergyExchangeCustomer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/8/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern BOOL _LM_DEBUG;

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

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeCustomer::getPAOId() const
{

    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomer::getPAOCategory() const
{

    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomer::getPAOClass() const
{

    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomer::getPAOName() const
{

    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeCustomer::getPAOType() const
{

    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomer::getPAODescription() const
{

    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag
    
    Returns the disable flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMEnergyExchangeCustomer::getDisableFlag() const
{

    return _disableflag;
}

/*---------------------------------------------------------------------------
    getCustomerOrder

    Returns the order of the customer in a program
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeCustomer::getCustomerOrder() const
{

    return _customerorder;
}

/*---------------------------------------------------------------------------
    getCustTimeZone

    Returns the customer time zone of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomer::getCustTimeZone() const
{

    return _custtimezone;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeCustomerReplies

    Returns a list of replies to offer of the customer
---------------------------------------------------------------------------*/
RWOrdered& CtiLMEnergyExchangeCustomer::getLMEnergyExchangeCustomerReplies()
{

    return _lmenergyexchangecustomerreplies;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setPAOId(LONG id)
{

    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setPAOCategory(const RWCString& category)
{

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setPAOClass(const RWCString& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setPAOName(const RWCString& name)
{

    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setPAOType(LONG type)
{

    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setPAODescription(const RWCString& description)
{

    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag
    
    Sets the disable flag of the customer
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setDisableFlag(BOOL disable)
{

    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustomerOrder

    Sets the order of the customer in a program
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setCustomerOrder(LONG order)
{

    _customerorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustTimeZone
    
    Sets the time zone of the customer
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeCustomer& CtiLMEnergyExchangeCustomer::setCustTimeZone(const RWCString& timezone)
{

    _custtimezone = timezone;
    return *this;
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
                if( ((CtiLMEnergyExchangeCustomerReply*)_lmenergyexchangecustomerreplies[i])->getAcceptStatus() == CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus )
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



    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
          >> _paocategory
          >> _paoclass
          >> _paoname
          >> _paotype
          >> _paodescription
          >> _disableflag
          >> _customerorder
          >> _custtimezone
          >> _lmenergyexchangecustomerreplies;

}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomer::saveGuts(RWvostream& ostrm ) const  
{


        
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
          << _paocategory
          << _paoclass
          << _paoname
          << _paotype
          << _paodescription
          << _disableflag
          << _customerorder
          << _custtimezone
          << _lmenergyexchangecustomerreplies;

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
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
        _customerorder = right._customerorder;
        _custtimezone = right._custtimezone;
        
        _lmenergyexchangecustomerreplies.clearAndDestroy();
        for(LONG i=0;i<right._lmenergyexchangecustomerreplies.entries();i++)
        {
            _lmenergyexchangecustomerreplies.insert(((CtiLMEnergyExchangeCustomerReply*)right._lmenergyexchangecustomerreplies[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeCustomer::operator==(const CtiLMEnergyExchangeCustomer& right) const
{

    return ( (getPAOId() == right.getPAOId()) && (getCustomerOrder() == right.getCustomerOrder()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeCustomer::operator!=(const CtiLMEnergyExchangeCustomer& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomer::restore(RWDBReader& rdr)
{


    RWCString tempBoolString;
    RWCString tempTypeString;
    
    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> tempTypeString;
    _paotype = resolvePAOType(_paocategory,tempTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["customerorder"] >> _customerorder;
    rdr["custtimezone"] >> _custtimezone;
}

// Static Members

// Possible 

