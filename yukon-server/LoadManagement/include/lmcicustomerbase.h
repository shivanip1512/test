#pragma once

#include "msg_pcrequest.h"
#include "row_reader.h"

class CtiLMCICustomerBase
{

public:

    CtiLMCICustomerBase();
    CtiLMCICustomerBase(Cti::RowReader &rdr);
    CtiLMCICustomerBase(const CtiLMCICustomerBase& customer);

    virtual ~CtiLMCICustomerBase();

    LONG getCustomerId() const;
    const std::string& getCompanyName() const;
    DOUBLE getCustomerDemandLevel() const;
    DOUBLE getCurtailAmount() const;
    const std::string& getCurtailmentAgreement() const;
    const std::string& getTimeZone() const;
    LONG getCustomerOrder() const;

    CtiLMCICustomerBase& setCustomerId(LONG id);
    CtiLMCICustomerBase& setCompanyName(const std::string& name);
    CtiLMCICustomerBase& setCustomerDemandLevel(DOUBLE cdl);
    CtiLMCICustomerBase& setCurtailAmount(DOUBLE amount);
    CtiLMCICustomerBase& setCurtailmentAgreement(const std::string& agreement);
    CtiLMCICustomerBase& setTimeZone(const std::string& timezone);
    CtiLMCICustomerBase& setCustomerOrder(LONG order);

    CtiLMCICustomerBase& operator=(const CtiLMCICustomerBase& right);

    int operator==(const CtiLMCICustomerBase& right) const;
    int operator!=(const CtiLMCICustomerBase& right) const;

    virtual std::size_t getMemoryConsumption() const;

    // Static Members

    // Possible acknowledge statuses

protected:

    void restore(Cti::RowReader &rdr);

private:

    LONG _customerid;
    std::string _companyname;
    DOUBLE _customerdemandlevel;
    DOUBLE _curtailamount;
    std::string _curtailmentagreement;
    std::string _time_zone;
    LONG _customerorder;
};
