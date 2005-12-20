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
    const string& getAcknowledgeStatus() const;
    const CtiTime& getAckDateTime() const;
    const string& getIPAddressOfAckUser() const;
    const string& getUserIdName() const;
    const string& getNameOfAckPerson() const;
    const string& getCurtailmentNotes() const;
    BOOL getAckLateFlag() const;

    CtiLMCurtailCustomer& setRequireAck(BOOL reqack);
    CtiLMCurtailCustomer& setCurtailReferenceId(LONG refid);
    CtiLMCurtailCustomer& setAcknowledgeStatus(const string& ackstatus);
    CtiLMCurtailCustomer& setAckDateTime(const CtiTime& acktime);
    CtiLMCurtailCustomer& setIPAddressOfAckUser(const string& ipaddress);
    CtiLMCurtailCustomer& setUserIdName(const string& username);
    CtiLMCurtailCustomer& setNameOfAckPerson(const string& nameackperson);
    CtiLMCurtailCustomer& setCurtailmentNotes(const string& curtailnotes);
    CtiLMCurtailCustomer& setAckLateFlag(BOOL acklate);

    CtiLMCurtailCustomer* replicate() const;

    void addLMCurtailCustomerActivityTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMCurtailCustomer& operator=(const CtiLMCurtailCustomer& right);

    // Static Members

    // Possible acknowledge statuses
    static const string UnAcknowledgedAckStatus;
    static const string AcknowledgedAckStatus;
    static const string NotRequiredAckStatus;
    static const string VerbalAckStatus;

protected:

    void restore(RWDBReader& rdr);

private:

    BOOL _requireack;
    LONG _curtailreferenceid;
    string _acknowledgestatus;
    CtiTime _ackdatetime;
    string _ipaddressofackuser;
    string _useridname;
    string _nameofackperson;
    string _curtailmentnotes;
    BOOL _acklateflag;

    void updateLMCurtailCustomerActivityTable(RWDBConnection& conn, CtiTime& currentDateTime);
};
#endif

