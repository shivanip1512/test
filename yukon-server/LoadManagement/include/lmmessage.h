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
        REQUEST_ALL_CONTROL_AREAS
    };

    CtiLMCommand() { }; //provided for polymorphic persitence only
    /*CtiLMCommand(UINT command);
    CtiLMCommand(UINT command, ULONG id);
    CtiLMCommand(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMCommand();

    UINT getCommand() const;
    ULONG getPAOId() const;
    ULONG getNumber() const;
    DOUBLE getValue() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMCommand& operator=(const CtiLMCommand& right);

private:
    
    UINT _command;
    ULONG _paoid;
    ULONG _number;
    DOUBLE _value;
};


class CtiLMManualControlMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMManualControlMsg )

public:

    enum 
    {
        SCHEDULED_START = 0,
        SCHEDULED_STOP,
        START_NOW,
        STOP_NOW
    };

    CtiLMManualControlMsg() { }; //provided for polymorphic persitence only
    /*CtiLMControlMsg(UINT command);
    CtiLMControlMsg(UINT command, ULONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMManualControlMsg();

    UINT getCommand() const;
    ULONG getPAOId() const;
    const RWDBDateTime& getNotifyTime() const;
    const RWDBDateTime& getStartTime() const;
    const RWDBDateTime& getStopTime() const;
    ULONG getStartGear() const;
    ULONG getStartPriority() const;
    const RWCString& getAdditionalInfo() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMManualControlMsg& operator=(const CtiLMManualControlMsg& right);
private:
    
    UINT _command;
    ULONG _paoid;
    RWDBDateTime _notifytime;
    RWDBDateTime _starttime;
    RWDBDateTime _stoptime;
    ULONG _startgear;
    ULONG _startpriority;
    RWCString _additionalinfo;
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
    /*CtiLMControlMsg(UINT command);
    CtiLMControlMsg(UINT command, ULONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMEnergyExchangeControlMsg();

    UINT getCommand() const;
    ULONG getPAOId() const;
    ULONG getOfferId() const;
    const RWDBDateTime& getOfferDate() const;
    const RWDBDateTime& getNotificationDateTime() const;
    const RWDBDateTime& getExpirationDateTime() const;
    const RWCString& getAdditionalInfo() const;
    DOUBLE getAmountRequested(int i) const;
    ULONG getPriceOffered(int i) const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMEnergyExchangeControlMsg& operator=(const CtiLMEnergyExchangeControlMsg& right);
private:

    UINT _command;
    ULONG _paoid;
    ULONG _offerid;
    RWDBDateTime _offerdate;
    RWDBDateTime _notificationdatetime;
    RWDBDateTime _expirationdatetime;
    RWCString _additionalinfo;
    DOUBLE _amountsrequested[HOURS_IN_DAY];
    ULONG _pricesoffered[HOURS_IN_DAY];
};


class CtiLMEnergyExchangeAcceptMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeAcceptMsg )

public:

    CtiLMEnergyExchangeAcceptMsg() { }; //provided for polymorphic persitence only
    /*CtiLMControlMsg(UINT command);
    CtiLMControlMsg(UINT command, ULONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/
    
    virtual ~CtiLMEnergyExchangeAcceptMsg();

    ULONG getPAOId() const;
    ULONG getOfferId() const;
    ULONG getRevisionNumber() const;
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

    ULONG _paoid;
    ULONG _offerid;
    ULONG _revisionnumber;
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
    CtiLMControlAreaMsg() : CtiLMMessage("ControlAreas"){};
    CtiLMControlAreaMsg(RWOrdered& contAreas);
    CtiLMControlAreaMsg(const CtiLMControlAreaMsg& contAreaMsg);

    virtual ~CtiLMControlAreaMsg();

    const RWOrdered& getControlAreas() const     { return _controlAreas; }
    RWOrdered& getControlAreas()                 { return _controlAreas; }
    CtiLMControlAreaMsg& setControlAreas(const RWOrdered& contAreas);
    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiLMControlAreaMsg& operator=(const CtiLMControlAreaMsg& right);
private:
    
    RWOrdered _controlAreas;
};


class CtiLMCurtailmentAcknowledgeMsg : public CtiLMMessage
{
RWDECLARE_COLLECTABLE( CtiLMCurtailmentAcknowledgeMsg )

public:

    CtiLMCurtailmentAcknowledgeMsg() { }; //provided for polymorphic persitence only

    virtual ~CtiLMCurtailmentAcknowledgeMsg();

    ULONG getPAOId() const;
    ULONG getCurtailReferenceId() const;
    const RWCString& getAcknowledgeStatus() const;
    const RWCString& getIPAddressOfAckUser() const;
    const RWCString& getUserIdName() const;
    const RWCString& getNameOfAckPerson() const;
    const RWCString& getCurtailmentNotes() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiLMCurtailmentAcknowledgeMsg& operator=(const CtiLMCurtailmentAcknowledgeMsg& right);
private:
    
    ULONG _paoid;
    ULONG _curtailreferenceid;
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
