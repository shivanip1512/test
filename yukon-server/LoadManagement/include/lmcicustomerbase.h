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
    const RWCString& getCompanyName() const;
    DOUBLE getCustomerDemandLevel() const;
    DOUBLE getCurtailAmount() const;
    const RWCString& getCurtailmentAgreement() const;
    const RWCString& getTimeZone() const;
    LONG getCustomerOrder() const;

    CtiLMCICustomerBase& setCustomerId(LONG id);
    CtiLMCICustomerBase& setCompanyName(const RWCString& name);
    CtiLMCICustomerBase& setCustomerDemandLevel(DOUBLE cdl);
    CtiLMCICustomerBase& setCurtailAmount(DOUBLE amount);
    CtiLMCICustomerBase& setCurtailmentAgreement(const RWCString& agreement);
    CtiLMCICustomerBase& setTimeZone(const RWCString& timezone);
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
    RWCString _companyname;
    DOUBLE _customerdemandlevel;
    DOUBLE _curtailamount;
    RWCString _curtailmentagreement;
    RWCString _timezone;
    LONG _customerorder;
};
#endif

