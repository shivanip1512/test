/*---------------------------------------------------------------------------
        Filename:  lmcurtailcustomer.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMCurtailCustomer

        Initial Date:  3/26/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCURTAILCUSTOMERIMPL_H
#define CTILMCURTAILCUSTOMERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "msg_pcrequest.h"
#include "lmcicustomerbase.h"
                
class CtiLMCurtailCustomer : public CtiLMCICustomerBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMCurtailCustomer )

    CtiLMCurtailCustomer();
    CtiLMCurtailCustomer(RWDBReader& rdr);
    CtiLMCurtailCustomer(const CtiLMCurtailCustomer& customer);

    virtual ~CtiLMCurtailCustomer();
    
    BOOL getRequireAck() const;
    LONG getCurtailReferenceId() const;
    const RWCString& getAcknowledgeStatus() const;
    const RWDBDateTime& getAckDateTime() const;
    const RWCString& getIPAddressOfAckUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAckPerson() const;
    const RWCString& getCurtailmentNotes() const;
    BOOL getAckLateFlag() const;

    CtiLMCurtailCustomer& setRequireAck(BOOL reqack);
    CtiLMCurtailCustomer& setCurtailReferenceId(LONG refid);
    CtiLMCurtailCustomer& setAcknowledgeStatus(const RWCString& ackstatus);
    CtiLMCurtailCustomer& setAckDateTime(const RWDBDateTime& acktime);
    CtiLMCurtailCustomer& setIPAddressOfAckUser(const RWCString& ipaddress);
    CtiLMCurtailCustomer& setUserIdName(const RWCString& username);
    CtiLMCurtailCustomer& setNameOfAckPerson(const RWCString& nameackperson);
    CtiLMCurtailCustomer& setCurtailmentNotes(const RWCString& curtailnotes);
    CtiLMCurtailCustomer& setAckLateFlag(BOOL acklate);

    CtiLMCurtailCustomer* replicate() const;

    void addLMCurtailCustomerActivityTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMCurtailCustomer& operator=(const CtiLMCurtailCustomer& right);

    // Static Members

    // Possible acknowledge statuses
    static const RWCString UnAcknowledgedAckStatus;
    static const RWCString AcknowledgedAckStatus;
    static const RWCString NotRequiredAckStatus;
    static const RWCString VerbalAckStatus;

protected:

    void restore(RWDBReader& rdr);

private:

    BOOL _requireack;
    LONG _curtailreferenceid;
    RWCString _acknowledgestatus;
    RWDBDateTime _ackdatetime;
    RWCString _ipaddressofackuser;
    RWCString _useridname;
    RWCString _nameofackperson;
    RWCString _curtailmentnotes;
    BOOL _acklateflag;

    void updateLMCurtailCustomerActivityTable(RWDBConnection& conn, RWDBDateTime& currentDateTime);
};
#endif

