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
                
class CtiLMCurtailCustomer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMCurtailCustomer )

    CtiLMCurtailCustomer();
    CtiLMCurtailCustomer(RWDBReader& rdr);
    CtiLMCurtailCustomer(const CtiLMCurtailCustomer& customer);

    virtual ~CtiLMCurtailCustomer();
    
    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    LONG getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getCustomerOrder() const;
    DOUBLE getCustFPL() const;
    const RWCString& getCustTimeZone() const;
    BOOL getRequireAck() const;
    LONG getCurtailReferenceId() const;
    const RWCString& getAcknowledgeStatus() const;
    const RWDBDateTime& getAckDateTime() const;
    const RWCString& getIPAddressOfAckUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAckPerson() const;
    const RWCString& getCurtailmentNotes() const;
    BOOL getAckLateFlag() const;
    /*LONG getGroupControlState() const;
    LONG getCurrentHoursDaily() const;
    LONG getCurrentHoursMonthly() const;
    LONG getCurrentHoursSeasonal() const;
    LONG getCurrentHoursAnnually() const;
    const RWDBDateTime& getLastControlSent() const;*/

    CtiLMCurtailCustomer& setPAOId(LONG id);
    CtiLMCurtailCustomer& setPAOCategory(const RWCString& category);
    CtiLMCurtailCustomer& setPAOClass(const RWCString& pclass);
    CtiLMCurtailCustomer& setPAOName(const RWCString& name);
    CtiLMCurtailCustomer& setPAOType(LONG type);
    CtiLMCurtailCustomer& setPAODescription(const RWCString& description);
    CtiLMCurtailCustomer& setDisableFlag(BOOL disable);
    CtiLMCurtailCustomer& setCustomerOrder(LONG order);
    CtiLMCurtailCustomer& setCustFPL(DOUBLE fpl);
    CtiLMCurtailCustomer& setCustTimeZone(const RWCString& timezone);
    CtiLMCurtailCustomer& setRequireAck(BOOL reqack);
    CtiLMCurtailCustomer& setCurtailReferenceId(LONG refid);
    CtiLMCurtailCustomer& setAcknowledgeStatus(const RWCString& ackstatus);
    CtiLMCurtailCustomer& setAckDateTime(const RWDBDateTime& acktime);
    CtiLMCurtailCustomer& setIPAddressOfAckUser(const RWCString& ipaddress);
    CtiLMCurtailCustomer& setUserIdName(const RWCString& username);
    CtiLMCurtailCustomer& setNameOfAckPerson(const RWCString& nameackperson);
    CtiLMCurtailCustomer& setCurtailmentNotes(const RWCString& curtailnotes);
    CtiLMCurtailCustomer& setAckLateFlag(BOOL acklate);
    /*CtiLMCurtailCustomer& setGroupControlState(LONG controlstate);
    CtiLMCurtailCustomer& setCurrentHoursDaily(LONG daily);
    CtiLMCurtailCustomer& setCurrentHoursMonthly(LONG monthly);
    CtiLMCurtailCustomer& setCurrentHoursSeasonal(LONG seasonal);
    CtiLMCurtailCustomer& setCurrentHoursAnnually(LONG annually);
    CtiLMCurtailCustomer& setLastControlSent(const RWDBDateTime& controlsent);*/

    CtiLMCurtailCustomer* replicate() const;

    void addLMCurtailCustomerActivityTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMCurtailCustomer& operator=(const CtiLMCurtailCustomer& right);

    int operator==(const CtiLMCurtailCustomer& right) const;
    int operator!=(const CtiLMCurtailCustomer& right) const;

    // Static Members

    // Possible acknowledge statuses
    static const RWCString UnAcknowledgedAckStatus;
    static const RWCString AcknowledgedAckStatus;
    static const RWCString NotRequiredAckStatus;
    static const RWCString VerbalAckStatus;

protected:

    void restore(RWDBReader& rdr);

private:

    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    LONG _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    LONG _customerorder;
    DOUBLE _custfpl;
    RWCString _custtimezone;
    BOOL _requireack;
    LONG _curtailreferenceid;
    RWCString _acknowledgestatus;
    RWDBDateTime _ackdatetime;
    RWCString _ipaddressofackuser;
    RWCString _useridname;
    RWCString _nameofackperson;
    RWCString _curtailmentnotes;
    BOOL _acklateflag;
    /*LONG _groupcontrolstate;
    LONG _currenthoursdaily;
    LONG _currenthoursmonthly;
    LONG _currenthoursseasonal;
    LONG _currenthoursannually;
    RWDBDateTime _lastcontrolsent;*/

    void updateLMCurtailCustomerActivityTable(RWDBConnection& conn, RWDBDateTime& currentDateTime);
};
#endif

