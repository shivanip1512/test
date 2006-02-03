/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangecustomerreply.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMEnergyExchangeCustomerReply

        Initial Date:  5/15/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMENERGYEXCHANGECUSTOMERREPLYIMPL_H
#define CTILMENERGYEXCHANGECUSTOMERREPLYIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include "ctitime.h"

#include "observe.h"
#include "lmenergyexchangehourlycustomer.h"

using std::vector;

class CtiLMEnergyExchangeCustomerReply : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeCustomerReply )

    CtiLMEnergyExchangeCustomerReply();
    CtiLMEnergyExchangeCustomerReply(RWDBReader& rdr);
    CtiLMEnergyExchangeCustomerReply(const CtiLMEnergyExchangeCustomerReply& customerreply);

    virtual ~CtiLMEnergyExchangeCustomerReply();
    
    LONG getCustomerId() const;
    LONG getOfferId() const;
    const string& getAcceptStatus() const;
    const CtiTime& getAcceptDateTime() const;
    LONG getRevisionNumber() const;
    const string& getIPAddressOfAcceptUser() const;
    const string& getUserIdName() const;
    const string& getNameOfAcceptPerson() const;
    const string& getEnergyExchangeNotes() const;
    vector<CtiLMEnergyExchangeHourlyCustomer*>& getLMEnergyExchangeHourlyCustomers();

    CtiLMEnergyExchangeCustomerReply& setCustomerId(LONG custid);
    CtiLMEnergyExchangeCustomerReply& setOfferId(LONG offid);
    CtiLMEnergyExchangeCustomerReply& setAcceptStatus(const string& accstatus);
    CtiLMEnergyExchangeCustomerReply& setAcceptDateTime(const CtiTime& acctime);
    CtiLMEnergyExchangeCustomerReply& setRevisionNumber(LONG revnumber);
    CtiLMEnergyExchangeCustomerReply& setIPAddressOfAcceptUser(const string& ipaddress);
    CtiLMEnergyExchangeCustomerReply& setUserIdName(const string& username);
    CtiLMEnergyExchangeCustomerReply& setNameOfAcceptPerson(const string& nameaccperson);
    CtiLMEnergyExchangeCustomerReply& setEnergyExchangeNotes(const string& exchangenotes);

    CtiLMEnergyExchangeCustomerReply* replicate() const;

    void addLMEnergyExchangeCustomerReplyTable();
    void updateLMEnergyExchangeCustomerReplyTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeCustomerReply& operator=(const CtiLMEnergyExchangeCustomerReply& right);

    int operator==(const CtiLMEnergyExchangeCustomerReply& right) const;
    int operator!=(const CtiLMEnergyExchangeCustomerReply& right) const;

    // Static Members

    // Possible accept statuses
    static const string NoResponseAcceptStatus;
    static const string AcceptedAcceptStatus;
    static const string DeclinedAcceptStatus;

protected:

    void restore(RWDBReader& rdr);

private:

    LONG _customerid;
    LONG _offerid;
    string _acceptstatus;
    CtiTime _acceptdatetime;
    LONG _revisionnumber;
    string _ipaddressofacceptuser;
    string _useridname;
    string _nameofacceptperson;
    string _energyexchangenotes;

    vector<CtiLMEnergyExchangeHourlyCustomer*> _lmenergyexchangehourlycustomers;
};
#endif

