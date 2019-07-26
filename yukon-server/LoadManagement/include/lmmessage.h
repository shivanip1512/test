#pragma once

#include "ctitime.h"

#include "message.h"
#include "lmcontrolarea.h"
#include "lmprogramdirect.h"

#include "ConstraintViolation.h"

#define HOURS_IN_DAY    24

class CtiLMMessage : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMMessage );

public:
    CtiLMMessage() { };
    CtiLMMessage(const std::string& message);

    virtual ~CtiLMMessage() { };

    const std::string& getMessage() const;

private:
    // The connection that received/produced this message
    std::string _message;
};


class CtiLMCommand : public CtiLMMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMCommand );

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

    CtiLMCommand( LONG command,
                  LONG paoid,
                  LONG number,
                  DOUBLE value,
                  LONG count,
                  LONG auxid ) :
    _command( command ),
    _paoid( paoid ),
    _number( number ),
    _value( value ),
    _count( count ),
    _auxid( auxid )
    {}

    virtual ~CtiLMCommand();

    LONG getCommand() const;
    LONG getPAOId() const;
    LONG getNumber() const;
    DOUBLE getValue() const;
    LONG getCount() const;
    LONG getAuxId() const;

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
public:
    DECLARE_COLLECTABLE( CtiLMManualControlRequest );

public:

    enum
    {
        SCHEDULED_START = 0,
        SCHEDULED_STOP,
        START_NOW,
        STOP_NOW,
        CHANGE_GEAR
    };

     enum
     {   // How to deal with constraint checking
         USE_CONSTRAINTS = 0,  // accept the request and use constraints
         OVERRIDE_CONSTRAINTS,  // accept the request and set the override constraints flag on the groups
         CHECK_CONSTRAINTS    // don't accept the request if constraints look like they will be violated

     };

    CtiLMManualControlRequest() { }; //provided for polymorphic persitence only
    CtiLMManualControlRequest(LONG cmd,
                  LONG pao_id,
                  const CtiTime& notify_time,
                  const CtiTime& start_time,
                  const CtiTime& stop_time,
                  LONG start_gear,
                  LONG start_priority,
                  const std::string& addl_info,
                  LONG constraint_cmd,
                  const std::string& origin
                  );
    CtiLMManualControlRequest(const CtiLMManualControlRequest& req);

    virtual ~CtiLMManualControlRequest();

    LONG getCommand() const;
    LONG getPAOId() const;
    const CtiTime& getNotifyTime() const;
    const CtiTime& getStartTime() const;
    const CtiTime& getStopTime() const;
    LONG getStartGear() const;
    void setStartGear(LONG gear);
    LONG getStartPriority() const;
    const std::string& getAdditionalInfo() const;
    LONG getConstraintCmd() const;
    const std::string & getOrigin() const;
    std::string toString() const override;

    virtual CtiMessage* replicateMessage() const;

    CtiLMManualControlRequest& operator=(const CtiLMManualControlRequest& right);
private:

    typedef CtiLMMessage Inherited;

    LONG _command;
    LONG _paoid;
    CtiTime _notifytime;
    CtiTime _starttime;
    CtiTime _stoptime;
    LONG _startgear;
    LONG _startpriority;
    std::string _additionalinfo;
    LONG _constraint_cmd;
    std::string _origin;
};

class CtiLMManualControlResponse : public CtiLMMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMManualControlResponse );

public:

    CtiLMManualControlResponse() { }; //provided for polymorphic persitence only
    CtiLMManualControlResponse(const CtiLMManualControlResponse& resp);
    virtual ~CtiLMManualControlResponse() { };

    LONG getPAOId() const;
    const std::vector< ConstraintViolation >& getConstraintViolations() const;
    const std::string& getBestFitAction() const;

    CtiLMManualControlResponse& setPAOId(LONG pao_id);
    CtiLMManualControlResponse& setConstraintViolations(const std::vector< ConstraintViolation >& constraintViolations);
    CtiLMManualControlResponse& setBestFitAction(const std::string& best_fit_action);

    virtual CtiMessage* replicateMessage() const;

    CtiLMManualControlResponse& operator=(const CtiLMManualControlResponse& right);
private:
    LONG _paoid;
    std::vector< ConstraintViolation > _constraintViolations;
    std::string _best_fit_action;
};

class CtiLMEnergyExchangeControlMsg : public CtiLMMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMEnergyExchangeControlMsg );

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
    const CtiTime& getOfferDate() const;
    const CtiTime& getNotificationDateTime() const;
    const CtiTime& getExpirationDateTime() const;
    const std::string& getAdditionalInfo() const;
    DOUBLE getAmountRequested(int i) const;
    LONG getPriceOffered(int i) const;

    std::vector<DOUBLE> getAmountsRequested() const;
    std::vector<LONG> getPricesOffered() const;

    CtiLMEnergyExchangeControlMsg( LONG command,
                                   LONG paoid,
                                   LONG offerid,
                                   const CtiTime& offerdate,
                                   const CtiTime& notificationdatetime,
                                   const CtiTime& expirationdatetime,
                                   const std::string& additionalinfo,
                                   const std::vector<DOUBLE>& amountsrequested,
                                   const std::vector<LONG>& pricesoffered );

    CtiLMEnergyExchangeControlMsg& operator=(const CtiLMEnergyExchangeControlMsg& right);
private:

    LONG _command;
    LONG _paoid;
    LONG _offerid;
    CtiTime _offerdate;
    CtiTime _notificationdatetime;
    CtiTime _expirationdatetime;
    std::string _additionalinfo;
    DOUBLE _amountsrequested[HOURS_IN_DAY];
    LONG _pricesoffered[HOURS_IN_DAY];
};


class CtiLMEnergyExchangeAcceptMsg : public CtiLMMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMEnergyExchangeAcceptMsg );

public:

    CtiLMEnergyExchangeAcceptMsg() { }; //provided for polymorphic persitence only
    /*CtiLMControlMsg(LONG command);
    CtiLMControlMsg(LONG command, LONG id);
    CtiLMControlMsg(const CtiLMCommand& commandMsg);*/

    virtual ~CtiLMEnergyExchangeAcceptMsg();

    LONG getPAOId() const;
    LONG getOfferId() const;
    LONG getRevisionNumber() const;
    const std::string& getAcceptStatus() const;
    const std::string& getIPAddressOfAcceptUser() const;
    const std::string& getUserIdName() const;
    const std::string& getNameOfAcceptPerson() const;
    const std::string& getEnergyExchangeNotes() const;
    DOUBLE getAmountCommitted(int i) const;

    std::vector<DOUBLE> getAmountsCommitted() const;

    CtiLMEnergyExchangeAcceptMsg( LONG paoid,
                                  LONG offerid,
                                  LONG revisionnumber,
                                  const std::string& acceptstatus,
                                  const std::string& ipaddressofacceptuser,
                                  const std::string& useridname,
                                  const std::string& nameofacceptperson,
                                  const std::string& energyexchangenotes,
                                  const std::vector<DOUBLE>& amountscommitted );

    CtiLMEnergyExchangeAcceptMsg& operator=(const CtiLMEnergyExchangeAcceptMsg& right);
private:

    LONG _paoid;
    LONG _offerid;
    LONG _revisionnumber;
    std::string _acceptstatus;
    std::string _ipaddressofacceptuser;
    std::string _useridname;
    std::string _nameofacceptperson;
    std::string _energyexchangenotes;
    DOUBLE _amountscommitted[HOURS_IN_DAY];
};


class CtiLMControlAreaMsg : public CtiLMMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMControlAreaMsg );

public:
    CtiLMControlAreaMsg(std::vector<CtiLMControlArea*>& contAreas, ULONG bitMask = 0);

    CtiLMControlAreaMsg(const CtiLMControlAreaMsg& contAreaMsg);

    virtual ~CtiLMControlAreaMsg();

    ULONG getMsgInfoBitMask() const { return _msgInfoBitMask; };
    std::vector<CtiLMControlArea*>* getControlAreas() const { return _controlAreas; };
    virtual CtiMessage* replicateMessage() const;

    CtiLMControlAreaMsg& operator=(const CtiLMControlAreaMsg& right);

    // Possible bit mask settings
    static ULONG AllControlAreasSent;
    static ULONG ControlAreaDeleted;

private:
    CtiLMControlAreaMsg() : CtiLMMessage("ControlAreas"), _controlAreas(NULL), _msgInfoBitMask(0) {};

    ULONG _msgInfoBitMask;
    std::vector<CtiLMControlArea*>* _controlAreas;
};


class CtiLMCurtailmentAcknowledgeMsg : public CtiLMMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMCurtailmentAcknowledgeMsg );

public:

    CtiLMCurtailmentAcknowledgeMsg() { }; //provided for polymorphic persitence only

    virtual ~CtiLMCurtailmentAcknowledgeMsg();

    LONG getPAOId() const;
    LONG getCurtailReferenceId() const;
    const std::string& getAcknowledgeStatus() const;
    const std::string& getIPAddressOfAckUser() const;
    const std::string& getUserIdName() const;
    const std::string& getNameOfAckPerson() const;
    const std::string& getCurtailmentNotes() const;

    CtiLMCurtailmentAcknowledgeMsg( LONG paoid,
                                    LONG curtailreferenceid,
                                    const std::string& acknowledgestatus,
                                    const std::string& ipaddressofackuser,
                                    const std::string& useridname,
                                    const std::string& nameofackperson,
                                    const std::string& curtailmentnotes );

    CtiLMCurtailmentAcknowledgeMsg& operator=(const CtiLMCurtailmentAcknowledgeMsg& right);
private:

    LONG _paoid;
    LONG _curtailreferenceid;
    std::string _acknowledgestatus;
    std::string _ipaddressofackuser;
    std::string _useridname;
    std::string _nameofackperson;
    std::string _curtailmentnotes;
};

class CtiLMDynamicGroupDataMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMDynamicGroupDataMsg );

public:
    CtiLMDynamicGroupDataMsg(CtiLMGroupPtr group);

    std::string toString() const override;

    virtual CtiMessage* replicateMessage() const;

    LONG getPaoId() const                           { return _paoid; };
    BOOL getDisableFlag() const                     { return _disableflag; };
    LONG getGroupControlState() const               { return _groupcontrolstate; };
    LONG getCurrentHoursDaily() const               { return _currenthoursdaily; };
    LONG getCurrentHoursMonthly() const             { return _currenthoursmonthly; };
    LONG getCurrentHoursSeasonal() const            { return _currenthoursseasonal; };
    LONG getCurrentHoursAnnually() const            { return _currenthoursannually; };
    const CtiTime& getLastControlSent() const       { return _lastcontrolsent; };
    const CtiTime& getControlStartTime() const      { return _controlstarttime; };
    const CtiTime& getControlCompleteTime() const   { return _controlcompletetime; };
    const CtiTime& getNextControlTime() const       { return _next_control_time; };
    unsigned getInternalState() const               { return _internalState; };
    LONG getDailyOps() const                        { return _daily_ops; };

private:
    typedef CtiMessage Inherited;
    CtiLMDynamicGroupDataMsg() {};
    LONG _paoid;
    BOOL _disableflag;
    LONG _groupcontrolstate;
    LONG _currenthoursdaily;
    LONG _currenthoursmonthly;
    LONG _currenthoursseasonal;
    LONG _currenthoursannually;
    CtiTime _lastcontrolsent;
    CtiTime _controlstarttime;
    CtiTime _controlcompletetime;
    CtiTime _next_control_time;
    unsigned _internalState; //What the heck is this???
    LONG _daily_ops;
};

class CtiLMDynamicProgramDataMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMDynamicProgramDataMsg );
    friend class CtiLMConnection;

public:
    CtiLMDynamicProgramDataMsg(CtiLMProgramDirectSPtr program);

    std::string toString() const override;

    virtual CtiMessage* replicateMessage() const;

    LONG getPaoId() const                           { return _paoid; };
    BOOL getDisableFlag() const                     { return _disableflag; };
    LONG getCurrentGearNumber() const               { return _currentgearnumber; };
    LONG getLastGroupControlled() const             { return _lastgroupcontrolled; };
    LONG getProgramState() const                    { return _programstate; };
    DOUBLE getReductionTotal() const                { return _reductiontotal; };
    const CtiTime& getDirectStartTime() const       { return _directstarttime; };
    const CtiTime& getDirectStopTime() const        { return _directstoptime; };
    const CtiTime& getNotifyActiveTime() const      { return _notify_active_time; };
    const CtiTime& getNotifyInactiveTime() const    { return _notify_inactive_time; };
    const CtiTime& getStartedRampingOutTime() const { return _startedrampingouttime; };
    std::string getOrigin() const                   { return _origin; }

private:
    typedef CtiMessage Inherited;
    CtiLMDynamicProgramDataMsg() {};
    LONG _paoid;
    BOOL _disableflag;
    LONG _currentgearnumber;//+1
    LONG _lastgroupcontrolled;
    LONG _programstate;
    DOUBLE _reductiontotal;
    CtiTime _directstarttime;
    CtiTime _directstoptime;
    CtiTime _notify_active_time;
    CtiTime _notify_inactive_time;
    CtiTime _startedrampingouttime;
    std::string _origin;
};

class CtiLMDynamicTriggerDataMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMDynamicTriggerDataMsg );

public:
    CtiLMDynamicTriggerDataMsg(CtiLMControlAreaTrigger *trigger);

    std::string toString() const override;

    virtual CtiMessage* replicateMessage() const;

    LONG gePaoId() const                                  { return _paoid; }
    LONG getTriggerNumber() const                         { return _triggernumber; }
    DOUBLE getPointValue() const                          { return _pointvalue; };
    const CtiTime& getLastPointValueTimestamp() const     { return _lastpointvaluetimestamp; }
    LONG getNormalState() const                           { return _normalstate; }
    DOUBLE getThreshold() const                           { return _threshold; }
    DOUBLE getPeakPointValue() const                      { return _peakpointvalue; }
    const CtiTime& getLastPeakPointValueTimestamp() const { return _lastpeakpointvaluetimestamp; }
    DOUBLE getProjectedPointValue() const                 { return _projectedpointvalue; }

private:
    typedef CtiMessage Inherited;
    CtiLMDynamicTriggerDataMsg() {};
    LONG _paoid;
    LONG _triggernumber;
    DOUBLE _pointvalue;
    CtiTime _lastpointvaluetimestamp;
    LONG _normalstate;
    DOUBLE _threshold;
    DOUBLE _peakpointvalue;
    CtiTime _lastpeakpointvaluetimestamp;
    DOUBLE _projectedpointvalue;
};

class CtiLMDynamicControlAreaDataMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiLMDynamicControlAreaDataMsg );

public:
    CtiLMDynamicControlAreaDataMsg(CtiLMControlArea *controlArea);

    std::string toString() const override;

    virtual CtiMessage* replicateMessage() const;

    LONG getPaoId() const                   { return _paoid; };
    BOOL getDisableFlag() const             { return _disableflag; };
    const CtiTime& getNextCheckTime() const { return _nextchecktime; };
    LONG getControlAreaState() const        { return _controlareastate; };
    LONG getCurrentPriority() const         { return _currentpriority; };
    LONG getCurrentDailyStartTime() const   { return _currentdailystarttime; };
    LONG getCurrentDailyStopTime() const    { return _currentdailystoptime; };
    const std::vector<CtiLMDynamicTriggerDataMsg>& getTriggers() const { return _triggers; };

private:
    typedef CtiMessage Inherited;
    CtiLMDynamicControlAreaDataMsg() {};
    LONG _paoid;
    BOOL _disableflag;
    CtiTime _nextchecktime;
    LONG _controlareastate;
    LONG _currentpriority;
    LONG _currentdailystarttime;
    LONG _currentdailystoptime;
    std::vector<CtiLMDynamicTriggerDataMsg> _triggers;
};
