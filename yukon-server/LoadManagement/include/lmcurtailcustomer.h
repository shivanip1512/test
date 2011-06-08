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
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "msg_pcrequest.h"
#include "lmcicustomerbase.h"
#include "database_connection.h"
                
class CtiLMCurtailCustomer : public CtiLMCICustomerBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMCurtailCustomer )

    CtiLMCurtailCustomer();
    CtiLMCurtailCustomer(Cti::RowReader &rdr);
    CtiLMCurtailCustomer(const CtiLMCurtailCustomer& customer);

    virtual ~CtiLMCurtailCustomer();
    
    BOOL getRequireAck() const;
    LONG getCurtailReferenceId() const;
    const std::string& getAcknowledgeStatus() const;
    const CtiTime& getAckDateTime() const;
    const std::string& getIPAddressOfAckUser() const;
    const std::string& getUserIdName() const;
    const std::string& getNameOfAckPerson() const;
    const std::string& getCurtailmentNotes() const;
    BOOL getAckLateFlag() const;

    CtiLMCurtailCustomer& setRequireAck(BOOL reqack);
    CtiLMCurtailCustomer& setCurtailReferenceId(LONG refid);
    CtiLMCurtailCustomer& setAcknowledgeStatus(const std::string& ackstatus);
    CtiLMCurtailCustomer& setAckDateTime(const CtiTime& acktime);
    CtiLMCurtailCustomer& setIPAddressOfAckUser(const std::string& ipaddress);
    CtiLMCurtailCustomer& setUserIdName(const std::string& username);
    CtiLMCurtailCustomer& setNameOfAckPerson(const std::string& nameackperson);
    CtiLMCurtailCustomer& setCurtailmentNotes(const std::string& curtailnotes);
    CtiLMCurtailCustomer& setAckLateFlag(BOOL acklate);

    CtiLMCurtailCustomer* replicate() const;

    void addLMCurtailCustomerActivityTable();
    void restoreDynamicData();
    void dumpDynamicData();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMCurtailCustomer& operator=(const CtiLMCurtailCustomer& right);

    // Static Members

    // Possible acknowledge statuses
    static const std::string UnAcknowledgedAckStatus;
    static const std::string AcknowledgedAckStatus;
    static const std::string NotRequiredAckStatus;
    static const std::string VerbalAckStatus;

protected:

    void restore(Cti::RowReader &rdr);

private:

    BOOL _requireack;
    LONG _curtailreferenceid;
    std::string _acknowledgestatus;
    CtiTime _ackdatetime;
    std::string _ipaddressofackuser;
    std::string _useridname;
    std::string _nameofackperson;
    std::string _curtailmentnotes;
    BOOL _acklateflag;

    void updateLMCurtailCustomerActivityTable(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
};
#endif

