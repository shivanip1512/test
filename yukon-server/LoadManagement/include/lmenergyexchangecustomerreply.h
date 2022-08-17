#pragma once

#include "ctitime.h"

#include "lmenergyexchangehourlycustomer.h"
#include "row_reader.h"

class CtiLMEnergyExchangeCustomerReply
{

public:

DECLARE_COLLECTABLE( CtiLMEnergyExchangeCustomerReply );

    CtiLMEnergyExchangeCustomerReply();
    CtiLMEnergyExchangeCustomerReply(Cti::RowReader &rdr);
    CtiLMEnergyExchangeCustomerReply(const CtiLMEnergyExchangeCustomerReply& customerreply);

    virtual ~CtiLMEnergyExchangeCustomerReply();

    LONG getCustomerId() const;
    LONG getOfferId() const;
    const std::string& getAcceptStatus() const;
    const CtiTime& getAcceptDateTime() const;
    LONG getRevisionNumber() const;
    const std::string& getIPAddressOfAcceptUser() const;
    const std::string& getUserIdName() const;
    const std::string& getNameOfAcceptPerson() const;
    const std::string& getEnergyExchangeNotes() const;
    std::vector<CtiLMEnergyExchangeHourlyCustomer*>& getLMEnergyExchangeHourlyCustomers();
    const std::vector<CtiLMEnergyExchangeHourlyCustomer*>& getLMEnergyExchangeHourlyCustomers() const;

    CtiLMEnergyExchangeCustomerReply& setCustomerId(LONG custid);
    CtiLMEnergyExchangeCustomerReply& setOfferId(LONG offid);
    CtiLMEnergyExchangeCustomerReply& setAcceptStatus(const std::string& accstatus);
    CtiLMEnergyExchangeCustomerReply& setAcceptDateTime(const CtiTime& acctime);
    CtiLMEnergyExchangeCustomerReply& setRevisionNumber(LONG revnumber);
    CtiLMEnergyExchangeCustomerReply& setIPAddressOfAcceptUser(const std::string& ipaddress);
    CtiLMEnergyExchangeCustomerReply& setUserIdName(const std::string& username);
    CtiLMEnergyExchangeCustomerReply& setNameOfAcceptPerson(const std::string& nameaccperson);
    CtiLMEnergyExchangeCustomerReply& setEnergyExchangeNotes(const std::string& exchangenotes);

    CtiLMEnergyExchangeCustomerReply* replicate() const;

    void addLMEnergyExchangeCustomerReplyTable();
    void updateLMEnergyExchangeCustomerReplyTable();
    void restoreDynamicData(Cti::RowReader &rdr);
    void dumpDynamicData();

    CtiLMEnergyExchangeCustomerReply& operator=(const CtiLMEnergyExchangeCustomerReply& right);

    int operator==(const CtiLMEnergyExchangeCustomerReply& right) const;
    int operator!=(const CtiLMEnergyExchangeCustomerReply& right) const;

    // Static Members

    // Possible accept statuses
    static const std::string NoResponseAcceptStatus;
    static const std::string AcceptedAcceptStatus;
    static const std::string DeclinedAcceptStatus;

protected:

    void restore(Cti::RowReader &rdr);

private:

    LONG _customerid;
    LONG _offerid;
    std::string _acceptstatus;
    CtiTime _acceptdatetime;
    LONG _revisionnumber;
    std::string _ipaddressofacceptuser;
    std::string _useridname;
    std::string _nameofacceptperson;
    std::string _energyexchangenotes;

    std::vector<CtiLMEnergyExchangeHourlyCustomer*> _lmenergyexchangehourlycustomers;
};
