/*-----------------------------------------------------------------------------
    Filename:  lmmessage.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for message classes.
                            
    Initial Date:  2/5/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
   
#ifndef LMMESSAGE_H
#define LMMESSAGE_H

#include <vector>

#include <rw/cstring.h>
#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/rwtime.h>

#include "message.h"
#include "lmcontrolarea.h"

#define HOURS_IN_DAY    24

class CtiLMMessage : public CtiMessage
{
RWDECLARE_COLLECTABLE( CtiLMMessage )

public:
    CtiLMMessage() { };
    CtiLMMessage(const RWCString& message);

    virtual ~CtiLMMessage() { };

    const RWCString& getMessage() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

private:
    RWCString _message;
};


class CtiLMCommand : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMCommand )

public:

    enum 
    {
        CHANGE_THRESHOLD = 0,
        CHANGE_RESTORE_OFFSET,
        CHANGE_CURRENT_START_TIME,
        CHANGE_CURRENT_STOP_TIME,
        CHANGE_CURRENT_OPERATIONAL_STATE,
        ENABLE_CONTROL_AREA,
        DISABLE_CONTROL_AREA,
        ENABLE_PROGRAM,
        DISABLE_PROGRAM,
        REQUEST_ALL_CONTROL_AREAS,
        SHED_GROUP,
        SMART_CYCLE_GROUP,
        TRUE_CYCLE_GROUP,
        RESTORE_GROUP,
        ENABLE_GROUP,
        DISABLE_GROUP,
        CONFIRM_GROUP,
	RESET_PEAK_POINT_VALUE,
	EMERGENCY_DISABLE_PROGRAM
    };

    CtiLMCommand() { }; //provided for polymorphic persitence only
    
    virtual ~CtiLMCommand();

    LONG getCommand() const;
    LONG getPAOId() const;
    LONG getNumber() const;
    DOUBLE getValue() const;
    LONG getCount() const;
    LONG getAuxId() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMCommand& operator=(const CtiLMCommand& right);

private:
    
    LONG _command;
    LONG _paoid;
    LONG _number;
    DOUBLE _value;
    LONG _count;
    LONG _auxid;
};


class CtiLMManualControlRequest : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMManualControlRequest )

public:

    enum 
    { 
        SCHEDULED_START = 0,
        SCHEDULED_STOP,
        START_NOW,
        STOP_NOW
    };

    CtiLMManualControlRequest() { }; //provided for polymorphic persitence only
    CtiLMManualControlRequest(const CtiLMManualControlRequest& req);
    
    /*CtiLMControlMsg(LONG command);
    CtiLMControlMsg(LONG command, LONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMManualControlRequest();

    LONG getCommand() const;
    LONG getPAOId() const;
    const RWDBDateTime& getNotifyTime() const;
    const RWDBDateTime& getStartTime() const;
    const RWDBDateTime& getStopTime() const;
    LONG getStartGear() const;
    LONG getStartPriority() const;
    const RWCString& getAdditionalInfo() const;
    BOOL getOverrideConstraints() const;
    BOOL getCoerceStartStopTime() const;
    
    virtual CtiMessage* replicateMessage() const;
	
    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMManualControlRequest& operator=(const CtiLMManualControlRequest& right);
private:
    
    LONG _command;
    LONG _paoid;
    RWDBDateTime _notifytime;
    RWDBDateTime _starttime;
    RWDBDateTime _stoptime;
    LONG _startgear;
    LONG _startpriority;
    RWCString _additionalinfo;
    BOOL _override_constraints;
    BOOL _coerce_start_stop_time;
};

class CtiLMManualControlResponse : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMManualControlResponse )

public:

    CtiLMManualControlResponse() { }; //provided for polymorphic persitence only
    CtiLMManualControlResponse(const CtiLMManualControlResponse& resp);
    virtual ~CtiLMManualControlResponse() { };

    LONG getPAOId() const;
    const vector< string >& getConstraintViolations() const;
    const string& getBestFitAction() const;

    CtiLMManualControlResponse& setPAOId(LONG pao_id);
    CtiLMManualControlResponse& setConstraintViolations(const vector< string >& constraintViolations);
    CtiLMManualControlResponse& setBestFitAction(const string& best_fit_action);

    virtual CtiMessage* replicateMessage() const;
    
    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMManualControlResponse& operator=(const CtiLMManualControlResponse& right);
private:
    LONG _paoid;
    vector< string > _constraintViolations;
    string _best_fit_action;
};

class CtiLMEnergyExchangeControlMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeControlMsg )

public:

    enum 
    {
        NEW_OFFER = 0,
        OFFER_UPDATE,
        OFFER_REVISION,
        CLOSE_OFFER,
        CANCEL_OFFER
    };

    CtiLMEnergyExchangeControlMsg() { }; //provided for polymorphic persitence only
    /*CtiLMControlMsg(LONG command);
    CtiLMControlMsg(LONG command, LONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMEnergyExchangeControlMsg();

    LONG getCommand() const;
    LONG getPAOId() const;
    LONG getOfferId() const;
    const RWDBDateTime& getOfferDate() const;
    const RWDBDateTime& getNotificationDateTime() const;
    const RWDBDateTime& getExpirationDateTime() const;
    const RWCString& getAdditionalInfo() const;
    DOUBLE getAmountRequested(int i) const;
    LONG getPriceOffered(int i) const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMEnergyExchangeControlMsg& operator=(const CtiLMEnergyExchangeControlMsg& right);
private:

    LONG _command;
    LONG _paoid;
    LONG _offerid;
    RWDBDateTime _offerdate;
    RWDBDateTime _notificationdatetime;
    RWDBDateTime _expirationdatetime;
    RWCString _additionalinfo;
    DOUBLE _amountsrequested[HOURS_IN_DAY];
    LONG _pricesoffered[HOURS_IN_DAY];
};


class CtiLMEnergyExchangeAcceptMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeAcceptMsg )

public:

    CtiLMEnergyExchangeAcceptMsg() { }; //provided for polymorphic persitence only
    /*CtiLMControlMsg(LONG command);
    CtiLMControlMsg(LONG command, LONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMEnergyExchangeAcceptMsg();

    LONG getPAOId() const;
    LONG getOfferId() const;
    LONG getRevisionNumber() const;
    const RWCString& getAcceptStatus() const;
    const RWCString& getIPAddressOfAcceptUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAcceptPerson() const;
    const RWCString& getEnergyExchangeNotes() const;
    DOUBLE getAmountCommitted(int i) const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMEnergyExchangeAcceptMsg& operator=(const CtiLMEnergyExchangeAcceptMsg& right);
private:

    LONG _paoid;
    LONG _offerid;
    LONG _revisionnumber;
    RWCString _acceptstatus;
    RWCString _ipaddressofacceptuser;
    RWCString _useridname;
    RWCString _nameofacceptperson;
    RWCString _energyexchangenotes;
    DOUBLE _amountscommitted[HOURS_IN_DAY];
};


class CtiLMControlAreaMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMControlAreaMsg )

public:
    CtiLMControlAreaMsg(RWOrdered& contAreas, ULONG bitMask = 0);
    CtiLMControlAreaMsg(const CtiLMControlAreaMsg& contAreaMsg);

    virtual ~CtiLMControlAreaMsg();

    ULONG getMsgInfoBitMask() const { return _msgInfoBitMask; };
    RWOrdered* getControlAreas() const { return _controlAreas; };
    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiLMControlAreaMsg& operator=(const CtiLMControlAreaMsg& right);

    // Possible bit mask settings
    static ULONG AllControlAreasSent;
    static ULONG ControlAreaDeleted;

private:
    CtiLMControlAreaMsg() : CtiLMMessage("ControlAreas"), _controlAreas(NULL), _msgInfoBitMask(0) {};
    
    ULONG _msgInfoBitMask;
    RWOrdered* _controlAreas;
};


class CtiLMCurtailmentAcknowledgeMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMCurtailmentAcknowledgeMsg )

public:

    CtiLMCurtailmentAcknowledgeMsg() { }; //provided for polymorphic persitence only

    virtual ~CtiLMCurtailmentAcknowledgeMsg();

    LONG getPAOId() const;
    LONG getCurtailReferenceId() const;
    const RWCString& getAcknowledgeStatus() const;
    const RWCString& getIPAddressOfAckUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAckPerson() const;
    const RWCString& getCurtailmentNotes() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMCurtailmentAcknowledgeMsg& operator=(const CtiLMCurtailmentAcknowledgeMsg& right);
private:
    
    LONG _paoid;
    LONG _curtailreferenceid;
    RWCString _acknowledgestatus;
    RWCString _ipaddressofackuser;
    RWCString _useridname;
    RWCString _nameofackperson;
    RWCString _curtailmentnotes;
};


class CtiLMShutdown : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMShutdown )

public:
    CtiLMShutdown() : CtiLMMessage("Shutdown") { } ;
    
    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;
};

#endif
