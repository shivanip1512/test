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
    
    ULONG getCustomerId() const;
    ULONG getOfferId() const;
    const RWCString& getAcceptStatus() const;
    const RWDBDateTime& getAcceptDateTime() const;
    ULONG getRevisionNumber() const;
    const RWCString& getIPAddressOfAcceptUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAcceptPerson() const;
    const RWCString& getEnergyExchangeNotes() const;
    RWOrdered& getLMEnergyExchangeHourlyCustomers();

    CtiLMEnergyExchangeCustomerReply& setCustomerId(ULONG custid);
    CtiLMEnergyExchangeCustomerReply& setOfferId(ULONG offid);
    CtiLMEnergyExchangeCustomerReply& setAcceptStatus(const RWCString& accstatus);
    CtiLMEnergyExchangeCustomerReply& setAcceptDateTime(const RWDBDateTime& acctime);
    CtiLMEnergyExchangeCustomerReply& setRevisionNumber(ULONG revnumber);
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

    ULONG _customerid;
    ULONG _offerid;
    RWCString _acceptstatus;
    RWDBDateTime _acceptdatetime;
    ULONG _revisionnumber;
    RWCString _ipaddressofacceptuser;
    RWCString _useridname;
    RWCString _nameofacceptperson;
    RWCString _energyexchangenotes;

    RWOrdered _lmenergyexchangehourlycustomers;
};
#endif

