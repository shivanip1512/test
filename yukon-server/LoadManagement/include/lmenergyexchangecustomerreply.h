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

#include "observe.h"
                
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
    const RWCString& getAcceptStatus() const;
    const RWDBDateTime& getAcceptDateTime() const;
    LONG getRevisionNumber() const;
    const RWCString& getIPAddressOfAcceptUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAcceptPerson() const;
    const RWCString& getEnergyExchangeNotes() const;
    RWOrdered& getLMEnergyExchangeHourlyCustomers();

    CtiLMEnergyExchangeCustomerReply& setCustomerId(LONG custid);
    CtiLMEnergyExchangeCustomerReply& setOfferId(LONG offid);
    CtiLMEnergyExchangeCustomerReply& setAcceptStatus(const RWCString& accstatus);
    CtiLMEnergyExchangeCustomerReply& setAcceptDateTime(const RWDBDateTime& acctime);
    CtiLMEnergyExchangeCustomerReply& setRevisionNumber(LONG revnumber);
    CtiLMEnergyExchangeCustomerReply& setIPAddressOfAcceptUser(const RWCString& ipaddress);
    CtiLMEnergyExchangeCustomerReply& setUserIdName(const RWCString& username);
    CtiLMEnergyExchangeCustomerReply& setNameOfAcceptPerson(const RWCString& nameaccperson);
    CtiLMEnergyExchangeCustomerReply& setEnergyExchangeNotes(const RWCString& exchangenotes);

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
    static const RWCString NoResponseAcceptStatus;
    static const RWCString AcceptedAcceptStatus;
    static const RWCString DeclinedAcceptStatus;

protected:

    void restore(RWDBReader& rdr);

private:

    LONG _customerid;
    LONG _offerid;
    RWCString _acceptstatus;
    RWDBDateTime _acceptdatetime;
    LONG _revisionnumber;
    RWCString _ipaddressofacceptuser;
    RWCString _useridname;
    RWCString _nameofacceptperson;
    RWCString _energyexchangenotes;

    RWOrdered _lmenergyexchangehourlycustomers;
};
#endif

