#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "msg_pcrequest.h"
#include "row_reader.h"
                
class CtiLMCICustomerBase : public RWCollectable
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

    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMCICustomerBase& operator=(const CtiLMCICustomerBase& right);

    int operator==(const CtiLMCICustomerBase& right) const;
    int operator!=(const CtiLMCICustomerBase& right) const;

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
