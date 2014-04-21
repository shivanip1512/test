#include "precompiled.h"

#include "dbaccess.h"
#include "lmcicustomerbase.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"

using std::string;

extern ULONG _LM_DEBUG;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMCICustomerBase::CtiLMCICustomerBase() :
_customerid(0),
_customerdemandlevel(0),
_curtailamount(0),
_customerorder(0)
{
}

CtiLMCICustomerBase::CtiLMCICustomerBase(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMCICustomerBase::CtiLMCICustomerBase(const CtiLMCICustomerBase& customer)
{
    operator=(customer);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMCICustomerBase::~CtiLMCICustomerBase()
{
}

LONG CtiLMCICustomerBase::getCustomerId() const
{
    return _customerid;
}

const string& CtiLMCICustomerBase::getCompanyName() const
{
    return _companyname;
}

DOUBLE CtiLMCICustomerBase::getCustomerDemandLevel() const
{
    return _customerdemandlevel;
}

DOUBLE CtiLMCICustomerBase::getCurtailAmount() const
{
    return _curtailamount;
}

const string& CtiLMCICustomerBase::getCurtailmentAgreement() const
{
    return _curtailmentagreement;
}

const string& CtiLMCICustomerBase::getTimeZone() const
{
    return _time_zone;
}

LONG CtiLMCICustomerBase::getCustomerOrder() const
{
    return _customerorder;
}



CtiLMCICustomerBase& CtiLMCICustomerBase::setCustomerId(LONG id)
{
    _customerid = id;
    //do not notify observers of this!
    return *this;
}

CtiLMCICustomerBase& CtiLMCICustomerBase::setCompanyName(const string& name)
{
    _companyname = name;
    return *this;
}

CtiLMCICustomerBase& CtiLMCICustomerBase::setCustomerDemandLevel(DOUBLE cdl)
{
    _customerdemandlevel = cdl;
    return *this;
}

CtiLMCICustomerBase& CtiLMCICustomerBase::setCurtailAmount(DOUBLE amount)
{
    _curtailamount = amount;
    return *this;
}

CtiLMCICustomerBase& CtiLMCICustomerBase::setCurtailmentAgreement(const string& agreement)
{
    _curtailmentagreement = agreement;
    return *this;
}

CtiLMCICustomerBase& CtiLMCICustomerBase::setTimeZone(const string& timezone)
{

    _time_zone = timezone;
    return *this;
}

CtiLMCICustomerBase& CtiLMCICustomerBase::setCustomerOrder(LONG order)
{
    _customerorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMCICustomerBase& CtiLMCICustomerBase::operator=(const CtiLMCICustomerBase& right)
{
    if( this != &right )
    {
        _customerid             = right._customerid;
        _companyname            = right._companyname;
        _customerdemandlevel    = right._customerdemandlevel;
        _curtailamount          = right._curtailamount;
        _curtailmentagreement   = right._curtailmentagreement;
        _time_zone              = right._time_zone;
        _customerorder          = right._customerorder;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMCICustomerBase::operator==(const CtiLMCICustomerBase& right) const
{

    return( (getCustomerId() == right.getCustomerId()) && (getCustomerOrder() == right.getCustomerOrder()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMCICustomerBase::operator!=(const CtiLMCICustomerBase& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMCICustomerBase::restore(Cti::RowReader &rdr)
{
    string tempBoolString;

    rdr["customerid"] >> _customerid;
    rdr["companyname"] >> _companyname;
    rdr["customerdemandlevel"] >> _customerdemandlevel;
    rdr["curtailamount"] >> _curtailamount;
    rdr["curtailmentagreement"] >> _curtailmentagreement;
    rdr["timezone"] >> _time_zone;
    rdr["customerorder"] >> _customerorder;
}

// Static Members

// Possible acknowledge statuses

