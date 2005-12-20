/*---------------------------------------------------------------------------
        Filename:  lmcicustomerbase.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMCICustomerBase

        Initial Date:  2/11/2003

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2003
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCICUSTOMERBASEIMPL_H
#define CTILMCICUSTOMERBASEIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "msg_pcrequest.h"
                
class CtiLMCICustomerBase : public RWCollectable
{

public:

    CtiLMCICustomerBase();
    CtiLMCICustomerBase(RWDBReader& rdr);
    CtiLMCICustomerBase(const CtiLMCICustomerBase& customer);

    virtual ~CtiLMCICustomerBase();
    
    LONG getCustomerId() const;
    const string& getCompanyName() const;
    DOUBLE getCustomerDemandLevel() const;
    DOUBLE getCurtailAmount() const;
    const string& getCurtailmentAgreement() const;
    const string& getTimeZone() const;
    LONG getCustomerOrder() const;

    CtiLMCICustomerBase& setCustomerId(LONG id);
    CtiLMCICustomerBase& setCompanyName(const string& name);
    CtiLMCICustomerBase& setCustomerDemandLevel(DOUBLE cdl);
    CtiLMCICustomerBase& setCurtailAmount(DOUBLE amount);
    CtiLMCICustomerBase& setCurtailmentAgreement(const string& agreement);
    CtiLMCICustomerBase& setTimeZone(const string& timezone);
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

    void restore(RWDBReader& rdr);

private:

    LONG _customerid;
    string _companyname;
    DOUBLE _customerdemandlevel;
    DOUBLE _curtailamount;
    string _curtailmentagreement;
    string _timezone;
    LONG _customerorder;
};
#endif

