#include "precompiled.h"

#include "lmmessage.h"
#include "lmid.h"
#include "utility.h"

#include <time.h>

using std::vector;
using std::string;
using std::endl;

/*===========================================================================
    CtiLMMessage

    Base class for all Load Management messages
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMMessage, CTILMMESSAGE_ID )

/*---------------------------------------------------------------------------
    getMessage

    Returns text describing the message
---------------------------------------------------------------------------*/
const string& CtiLMMessage::getMessage() const
{
    return _message;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMMessage::CtiLMMessage(const string& message) :  _message(message)
{
}


/*===========================================================================
    CtiLMCommand

    Represents a command message to Load Management
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMCommand, CTILMCOMMAND_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMCommand::~CtiLMCommand()
{
}

/*---------------------------------------------------------------------------
    getCommand

    Returns the specific lm command that self represents
---------------------------------------------------------------------------*/
LONG CtiLMCommand::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the object that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiLMCommand::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getNumber

    Returns the number of the object that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiLMCommand::getNumber() const
{
    return _number;
}

/*---------------------------------------------------------------------------
    getValue

    Returns the Value of the object that is associated with command.
---------------------------------------------------------------------------*/
DOUBLE CtiLMCommand::getValue() const
{
    return _value;
}

/*---------------------------------------------------------------------------
    getCount

    Returns the count (normally cycle count) that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiLMCommand::getCount() const
{
    return _count;
}

/*---------------------------------------------------------------------------
    getAuxId

    Returns the auxilary id (normally route id) that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiLMCommand::getAuxId() const
{
    return _auxid;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMCommand& CtiLMCommand::operator=(const CtiLMCommand& right)
{
    if( this != &right )
    {
        _command = right._command;
        _paoid = right._paoid;
        _number = right._number;
        _value = right._value;
        _count = right._count;
        _auxid = right._auxid;
    }

    return *this;
}


/*===========================================================================
    CtiLMManualControlRequest

    Represents a manual program control message from the Load Management
    Client
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMManualControlRequest, CTILMMANUALCONTROLREQUEST_ID )

CtiLMManualControlRequest::CtiLMManualControlRequest(LONG cmd,
                                                     LONG pao_id,
                                                     const CtiTime& notify_time,
                                                     const CtiTime& start_time,
                                                     const CtiTime& stop_time,
                                                     LONG start_gear,
                                                     LONG start_priority,
                                                     const string& addl_info,
                                                     LONG constraint_cmd,
                                                     const string& origin) :
_command(cmd),
_paoid(pao_id),
_notifytime(notify_time),
_starttime(start_time),
_stoptime(stop_time),
_startgear(start_gear),
_startpriority(start_priority),
_additionalinfo(addl_info),
_constraint_cmd(constraint_cmd),
_origin(origin)
{
}

CtiLMManualControlRequest::CtiLMManualControlRequest(const CtiLMManualControlRequest& req)
{
    operator=( req );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMManualControlRequest::~CtiLMManualControlRequest()
{
}


std::string CtiLMManualControlRequest::toString() const
{
    Cti::FormattedList list;

    list << "LM Manual Control Request";
    list.add("Command")         << _command;
    list.add("Paobject ID")     << _paoid;
    list.add("Notify Time")     << _notifytime;
    list.add("Start Time")      << _starttime;
    list.add("Stop Time")       << _stoptime;
    list.add("Start Gear")      << _startgear;
    list.add("Start Priority")  << _startpriority;
    list.add("Additional Info") << _additionalinfo;
    list.add("Constraint")      << _constraint_cmd;
    list.add("Origin")          << _origin;

    return Inherited::toString() += list.toString();
}

/*---------------------------------------------------------------------------
    getCommand

    Returns the specific manual control command that self represents
---------------------------------------------------------------------------*/
LONG CtiLMManualControlRequest::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the object that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMManualControlRequest::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getNotifyTime

    Returns the notification time of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMManualControlRequest::getNotifyTime() const
{
    return _notifytime;
}

/*---------------------------------------------------------------------------
    getStartTime

    Returns the start time of the object that is associated with control.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMManualControlRequest::getStartTime() const
{
    return _starttime;
}

/*---------------------------------------------------------------------------
    getStopTime

    Returns the stop time of the object that is associated with control.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMManualControlRequest::getStopTime() const
{
    return _stoptime;
}

/*---------------------------------------------------------------------------
    getStartGear

    Returns the start gear of the object that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMManualControlRequest::getStartGear() const
{
    return _startgear;
}

/*---------------------------------------------------------------------------
    setStartGear

    Sets the start gear of the object that is associated with control.
---------------------------------------------------------------------------*/
void CtiLMManualControlRequest::setStartGear(LONG gear)
{
    _startgear = gear;
}

/*---------------------------------------------------------------------------
    getStartPriority

    Returns the start priority of the object that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMManualControlRequest::getStartPriority() const
{
    return _startpriority;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info string of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const string& CtiLMManualControlRequest::getAdditionalInfo() const
{
    return _additionalinfo;
}

/*----------------------------------------------------------------------------
  getConstraintCmd

  Returns the constraint command (how to handle constraints) for this request
---------------------------------------------------------------------------*/
LONG CtiLMManualControlRequest::getConstraintCmd() const
{
    return _constraint_cmd;
}

/*---------------------------------------------------------------------------
    getOrigin

    Returns the origin of the request.
---------------------------------------------------------------------------*/
const std::string & CtiLMManualControlRequest::getOrigin() const
{
    return _origin;
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiLMManualControlRequest::replicateMessage() const
{
    return CTIDBG_new CtiLMManualControlRequest(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMManualControlRequest& CtiLMManualControlRequest::operator=(const CtiLMManualControlRequest& right)
{
    if( this != &right )
    {
        _command = right._command;
        _paoid = right._paoid;
        _notifytime = right._notifytime;
        _starttime = right._starttime;
        _stoptime = right._stoptime;
        _startgear = right._startgear;
        _startpriority = right._startpriority;
        _additionalinfo = right._additionalinfo;
        _constraint_cmd = right._constraint_cmd;
        _origin = right._origin;
    }

    return *this;
}


/*===========================================================================
    CtiLMManualControlResponse

    Represents a manual program control response from the Load Management
    Server
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMManualControlResponse, CTILMMANUALCONTROLRESPONSE_ID )

CtiLMManualControlResponse::CtiLMManualControlResponse(const CtiLMManualControlResponse& resp)
{
    operator=( resp );
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiLMManualControlResponse::replicateMessage() const
{
    return CTIDBG_new CtiLMManualControlResponse(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMManualControlResponse& CtiLMManualControlResponse::operator=(const CtiLMManualControlResponse& right)
{//super?
    if( this != &right )
    {
        _constraintViolations = right.getConstraintViolations();
    }

    return *this;
}

/*----------------------------------------------------------------------------
  getPAOId

  Returns the program id
----------------------------------------------------------------------------*/
LONG CtiLMManualControlResponse::getPAOId() const
{
    return _paoid;
}

/*----------------------------------------------------------------------------
  getBestFitAction

  Returns a string describing the best fit action the server had to take,
  might be empty
----------------------------------------------------------------------------*/
const string& CtiLMManualControlResponse::getBestFitAction() const
{
    return _best_fit_action;
}

/*----------------------------------------------------------------------------
  getConstraintViolations

  Returns the contraint violations
----------------------------------------------------------------------------*/
const vector<ConstraintViolation>& CtiLMManualControlResponse::getConstraintViolations() const
{
    return _constraintViolations;
}

/*----------------------------------------------------------------------------
  setPAOId

  Sets the program id
----------------------------------------------------------------------------*/
CtiLMManualControlResponse& CtiLMManualControlResponse::setPAOId(LONG paoid)
{
    _paoid = paoid;
    return *this;
}

/*----------------------------------------------------------------------------
  setBestFitAction
----------------------------------------------------------------------------*/
CtiLMManualControlResponse& CtiLMManualControlResponse::setBestFitAction(const string& best_fit_action)
{
    _best_fit_action = best_fit_action;
    return *this;
}

/*----------------------------------------------------------------------------
  setConstraintViolations

  Sets the constraint violations
----------------------------------------------------------------------------*/
CtiLMManualControlResponse& CtiLMManualControlResponse::setConstraintViolations(const vector<ConstraintViolation>& constraintViolations)
{
    _constraintViolations = constraintViolations;
    return *this;
}

// end response

/*===========================================================================
    CtiLMEnergyExchangeControlMsg

    Represents a energy exchange program offer message from a Load Management
    Client
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMEnergyExchangeControlMsg, CTILMENERGYEXCHANGECONTROLMSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeControlMsg::~CtiLMEnergyExchangeControlMsg()
{
}

/*---------------------------------------------------------------------------
    getCommand

    Returns the specific manual control command that self represents
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeControlMsg::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the object that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeControlMsg::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the id of the object that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeControlMsg::getOfferId() const
{
    return _offerid;
}

/*---------------------------------------------------------------------------
    getOfferDate

    Returns the date of the offer.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeControlMsg::getOfferDate() const
{
    return _offerdate;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime

    Returns the notification date time of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeControlMsg::getNotificationDateTime() const
{
    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getExpirationDateTime

    Returns the date and time when the offer will expire.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeControlMsg::getExpirationDateTime() const
{
    return _expirationdatetime;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info string of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeControlMsg::getAdditionalInfo() const
{
    return _additionalinfo;
}

/*---------------------------------------------------------------------------
    getAmountRequested

    Returns the list of amounts requested for each hour for the offer date.
---------------------------------------------------------------------------*/
DOUBLE CtiLMEnergyExchangeControlMsg::getAmountRequested(int i) const
{
    if( (i>=0) && (i<HOURS_IN_DAY) )
    {
        return _amountsrequested[i];
    }
    else
    {
        return 0.0;
    }
}

/*---------------------------------------------------------------------------
    getPriceOffered

    Returns the list of prices for each hour for the offer date.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeControlMsg::getPriceOffered(int i) const
{
    if( (i>=0) && (i<HOURS_IN_DAY) )
    {
        return _pricesoffered[i];
    }
    else
    {
        return 0.0;
    }
}

vector<DOUBLE> CtiLMEnergyExchangeControlMsg::getAmountsRequested() const
{
    return vector<DOUBLE>( _amountsrequested, _amountsrequested + HOURS_IN_DAY );
}

vector<LONG> CtiLMEnergyExchangeControlMsg::getPricesOffered() const
{
    return vector<LONG>( _pricesoffered, _pricesoffered + HOURS_IN_DAY );
}

// disable the warning generated for '_amountsrequested' and '_pricesoffered':
// new behavior: elements of array 'array' will be default initialized
#pragma warning(push)
#pragma warning(disable:4351)

CtiLMEnergyExchangeControlMsg::CtiLMEnergyExchangeControlMsg( LONG command,
                                                              LONG paoid,
                                                              LONG offerid,
                                                              const CtiTime& offerdate,
                                                              const CtiTime& notificationdatetime,
                                                              const CtiTime& expirationdatetime,
                                                              const string& additionalinfo,
                                                              const vector<DOUBLE>& amountsrequested,
                                                              const vector<LONG>& pricesoffered ) :
    _command( command ),
    _paoid( paoid ),
    _offerid( offerid ),
    _offerdate( offerdate ),
    _notificationdatetime( notificationdatetime ),
    _expirationdatetime( expirationdatetime ),
    _additionalinfo( additionalinfo ),
    _amountsrequested(),
    _pricesoffered()
{
    for( int hour=0; hour<HOURS_IN_DAY && hour<amountsrequested.size(); hour++ )
    {
        _amountsrequested[hour] = amountsrequested[hour];
    }

    for( int hour=0; hour<HOURS_IN_DAY && hour<pricesoffered.size(); hour++ )
    {
        _pricesoffered[hour] = pricesoffered[hour];
    }
}

#pragma warning(pop)

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeControlMsg& CtiLMEnergyExchangeControlMsg::operator=(const CtiLMEnergyExchangeControlMsg& right)
{
    if( this != &right )
    {
        _command = right._command;
        _paoid = right._paoid;
        _offerid = right._offerid;
        _offerdate = right._offerdate;
        _notificationdatetime = right._notificationdatetime;
        _expirationdatetime = right._expirationdatetime;
        _additionalinfo = right._additionalinfo;

        for( LONG i=0;i<HOURS_IN_DAY;i++ )
        {
            _amountsrequested[i] = right.getAmountRequested(i);
        }

        for( LONG j=0;j<HOURS_IN_DAY;j++ )
        {
            _pricesoffered[j] = right.getPriceOffered(j);
        }
    }

    return *this;
}


/*===========================================================================
    CtiLMEnergyExchangeAcceptMsg

    Represents a energy exchange program offer message from a Load Management
    Client
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMEnergyExchangeAcceptMsg, CTILMENERGYEXCHANGEACCEPTMSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeAcceptMsg::~CtiLMEnergyExchangeAcceptMsg()
{
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the program that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeAcceptMsg::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the id of the offer that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeAcceptMsg::getOfferId() const
{
    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the revision number of the offer that is associated with control.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeAcceptMsg::getRevisionNumber() const
{
    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getAcceptStatus

    Returns the accept status string for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeAcceptMsg::getAcceptStatus() const
{
    return _acceptstatus;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAcceptUser

    Returns the ip address of accept user for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeAcceptMsg::getIPAddressOfAcceptUser() const
{
    return _ipaddressofacceptuser;
}

/*---------------------------------------------------------------------------
    getUserIdName

    Returns the user id name for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeAcceptMsg::getUserIdName() const
{
    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAcceptPerson

    Returns the name of accept person for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeAcceptMsg::getNameOfAcceptPerson() const
{
    return _nameofacceptperson;
}

/*---------------------------------------------------------------------------
    getEnergyExchangeNotes

    Returns the energy exchange notes for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeAcceptMsg::getEnergyExchangeNotes() const
{
    return _energyexchangenotes;
}

/*---------------------------------------------------------------------------
    getAmountCommitted

    Returns the list of amounts requested for each hour for the offer date.
---------------------------------------------------------------------------*/
DOUBLE CtiLMEnergyExchangeAcceptMsg::getAmountCommitted(int i) const
{
    if( (i>=0) && (i<HOURS_IN_DAY) )
    {
        return _amountscommitted[i];
    }
    else
    {
        return 0.0;
    }
}

vector<DOUBLE> CtiLMEnergyExchangeAcceptMsg::getAmountsCommitted() const
{
    return vector<DOUBLE>( _amountscommitted, _amountscommitted + HOURS_IN_DAY );
}

// disable the warning generated for '_amountscommitted':
// new behavior: elements of array 'array' will be default initialized
#pragma warning(push)
#pragma warning(disable:4351)

CtiLMEnergyExchangeAcceptMsg::CtiLMEnergyExchangeAcceptMsg( LONG paoid,
                                                            LONG offerid,
                                                            LONG revisionnumber,
                                                            const string& acceptstatus,
                                                            const string& ipaddressofacceptuser,
                                                            const string& useridname,
                                                            const string& nameofacceptperson,
                                                            const string& energyexchangenotes,
                                                            const vector<DOUBLE>& amountscommitted ) :
    _paoid( paoid ),
    _offerid( offerid ),
    _revisionnumber( revisionnumber ),
    _acceptstatus( acceptstatus ),
    _ipaddressofacceptuser( ipaddressofacceptuser ),
    _useridname( useridname ),
    _nameofacceptperson( nameofacceptperson ),
    _energyexchangenotes( energyexchangenotes ),
    _amountscommitted()
{
    for( int hour=0; hour<HOURS_IN_DAY && hour<amountscommitted.size(); hour++ )
    {
        _amountscommitted[hour] = amountscommitted[hour];
    }
}

#pragma warning(pop)

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeAcceptMsg& CtiLMEnergyExchangeAcceptMsg::operator=(const CtiLMEnergyExchangeAcceptMsg& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _offerid = right._offerid;
        _revisionnumber = right._revisionnumber;
        _acceptstatus = right._acceptstatus;
        _ipaddressofacceptuser = right._ipaddressofacceptuser;
        _useridname = right._useridname;
        _nameofacceptperson = right._nameofacceptperson;
        _energyexchangenotes = right._energyexchangenotes;

        for( LONG i=0;i<HOURS_IN_DAY;i++ )
        {
            _amountscommitted[i] = right.getAmountCommitted(i);
        }
    }

    return *this;
}


/*===========================================================================
    CtiLMControlAreaMsg
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMControlAreaMsg, CTILMCONTROLAREA_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg::CtiLMControlAreaMsg(vector<CtiLMControlArea*>& contAreas, ULONG bitMask) : CtiLMMessage("ControlArea"), _controlAreas(NULL), _msgInfoBitMask(bitMask)
{
    _controlAreas = CTIDBG_new vector<CtiLMControlArea*>;
    for( int i=0;i<contAreas.size();i++ )
    {
        _controlAreas->push_back(((CtiLMControlArea*)contAreas[i])->replicate());
    }
}

CtiLMControlAreaMsg::CtiLMControlAreaMsg(const CtiLMControlAreaMsg& contAreaMsg) : CtiLMMessage("ControlArea"), _controlAreas(NULL), _msgInfoBitMask(0)
{
    operator=( contAreaMsg );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg::~CtiLMControlAreaMsg()
{
    delete_container(*_controlAreas);
    _controlAreas->clear();
    delete _controlAreas;
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiLMControlAreaMsg::replicateMessage() const
{
    return CTIDBG_new CtiLMControlAreaMsg(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg& CtiLMControlAreaMsg::operator=(const CtiLMControlAreaMsg& right)
{
    if( this != &right )
    {
        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _controlAreas != NULL &&
            _controlAreas->size() > 0 )
        {
            delete_container(*_controlAreas);
            _controlAreas->clear();
            delete _controlAreas;
        }
        _controlAreas = CTIDBG_new vector<CtiLMControlArea*>;
        for( int i=0;i<(right.getControlAreas())->size();i++ )
        {
            _controlAreas->push_back(((CtiLMControlArea*)(*right.getControlAreas())[i])->replicate());
        }
    }

    return *this;
}


// Static Members
ULONG CtiLMControlAreaMsg::AllControlAreasSent = 0x00000001;
ULONG CtiLMControlAreaMsg::ControlAreaDeleted  = 0x00000002;


/*===========================================================================
    CtiLMCurtailmentAcknowledgeMsg

    Represents a curtailment acknowledge message to Load Management
===========================================================================*/

DEFINE_COLLECTABLE( CtiLMCurtailmentAcknowledgeMsg, CTILMCURTAILMENTACK_MSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMCurtailmentAcknowledgeMsg::~CtiLMCurtailmentAcknowledgeMsg()
{
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the object that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiLMCurtailmentAcknowledgeMsg::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getCurtailReferenceId

    Returns the refernce id of the object that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiLMCurtailmentAcknowledgeMsg::getCurtailReferenceId() const
{
    return _curtailreferenceid;
}

/*---------------------------------------------------------------------------
    getAcknowledgeStatus

    Returns the acknowledge status string for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMCurtailmentAcknowledgeMsg::getAcknowledgeStatus() const
{
    return _acknowledgestatus;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAckUser

    Returns the ip address of ack user for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMCurtailmentAcknowledgeMsg::getIPAddressOfAckUser() const
{
    return _ipaddressofackuser;
}

/*---------------------------------------------------------------------------
    getUserIdName

    Returns the user id name for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMCurtailmentAcknowledgeMsg::getUserIdName() const
{
    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAckPerson

    Returns the name of ack person for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMCurtailmentAcknowledgeMsg::getNameOfAckPerson() const
{
    return _nameofackperson;
}

/*---------------------------------------------------------------------------
    getCurtailmentNotes

    Returns the curtailment notes for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const string& CtiLMCurtailmentAcknowledgeMsg::getCurtailmentNotes() const
{
    return _curtailmentnotes;
}

CtiLMCurtailmentAcknowledgeMsg::CtiLMCurtailmentAcknowledgeMsg( LONG paoid,
                                                                LONG curtailreferenceid,
                                                                const string& acknowledgestatus,
                                                                const string& ipaddressofackuser,
                                                                const string& useridname,
                                                                const string& nameofackperson,
                                                                const string& curtailmentnotes ) :
_paoid( paoid ),
_curtailreferenceid( curtailreferenceid ),
_acknowledgestatus( acknowledgestatus ),
_ipaddressofackuser( ipaddressofackuser ),
_useridname( useridname ),
_nameofackperson( nameofackperson ),
_curtailmentnotes( curtailmentnotes )
{}


/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMCurtailmentAcknowledgeMsg& CtiLMCurtailmentAcknowledgeMsg::operator=(const CtiLMCurtailmentAcknowledgeMsg& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _curtailreferenceid = right._curtailreferenceid;
        _acknowledgestatus = right._acknowledgestatus;
        _ipaddressofackuser = right._ipaddressofackuser;
        _useridname = right._useridname;
        _nameofackperson = right._nameofackperson;
        _curtailmentnotes = right._curtailmentnotes;
    }

    return *this;
}


DEFINE_COLLECTABLE( CtiLMDynamicGroupDataMsg, CTILMDYNAMICGROUPMSG_ID )
DEFINE_COLLECTABLE( CtiLMDynamicProgramDataMsg, CTILMDYNAMICPROGRAMMSG_ID )
DEFINE_COLLECTABLE( CtiLMDynamicControlAreaDataMsg, CTILMDYNAMICCONTROLAREAMSG_ID )
DEFINE_COLLECTABLE( CtiLMDynamicTriggerDataMsg, CTILMDYNAMICLMTRIGGERMSG_ID )


/* ==========================================================================
   Constructors
   ==========================================================================*/

CtiLMDynamicGroupDataMsg::CtiLMDynamicGroupDataMsg(CtiLMGroupPtr group)
{
    if( group )
    {
        _paoid = group->getPAOId();
        _disableflag = group->getDisableFlag();
        _groupcontrolstate = group->getGroupControlState();
        _currenthoursdaily = group->getCurrentHoursDaily();
        _currenthoursmonthly = group->getCurrentHoursMonthly();
        _currenthoursseasonal = group->getCurrentHoursSeasonal();
        _currenthoursannually = group->getCurrentHoursAnnually();
        _lastcontrolsent = group->getLastControlSent();
        _controlstarttime = group->getControlStartTime();
        _controlcompletetime = group->getControlCompleteTime();
        _next_control_time = group->getNextControlTime();
        _internalState = group->getIsRampingOut() | group->getIsRampingIn(); //What the heck is this???
        _daily_ops = group->getDailyOps();
    }
}

CtiLMDynamicProgramDataMsg::CtiLMDynamicProgramDataMsg(CtiLMProgramDirectSPtr program)
{
    if( program )
    {
        _paoid = program->getPAOId();
        _disableflag = program->getDisableFlag();
        _currentgearnumber = program->getCurrentGearNumber();
        _lastgroupcontrolled = program->getLastGroupControlled();
        _programstate = program->getProgramState();
        _reductiontotal = program->getReductionTotal();
        _directstarttime = program->getDirectStartTime();
        _directstoptime = program->getDirectStopTime();
        _notify_active_time = program->getNotifyActiveTime();
        _notify_inactive_time = program->getNotifyInactiveTime();
        _startedrampingouttime = program->getStartedRampingOutTime();
        _origin = program->getOrigin();
    }
}

CtiLMDynamicControlAreaDataMsg::CtiLMDynamicControlAreaDataMsg(CtiLMControlArea *controlArea)
{
    if( controlArea != NULL )
    {
        _paoid = controlArea->getPAOId();
        _disableflag = controlArea->getDisableFlag();
        _nextchecktime = controlArea->getNextCheckTime();
        _controlareastate = controlArea->getControlAreaState();
        _currentpriority = controlArea->getCurrentStartPriority();
        _currentdailystarttime = controlArea->getCurrentStartSecondsFromDayBegin();
        _currentdailystoptime = controlArea->getCurrentStopSecondsFromDayBegin();

        vector<CtiLMControlAreaTrigger*>& triggers = controlArea->getLMControlAreaTriggers();
        if( triggers.size() > 0 ) //No one else uses iterators. I still feel dirty though.
        {
            for( int x = 0; x < triggers.size(); x++ )
            {
                _triggers.push_back(CtiLMDynamicTriggerDataMsg((CtiLMControlAreaTrigger*)triggers.at(x)));
            }
        }
    }
}

CtiLMDynamicTriggerDataMsg::CtiLMDynamicTriggerDataMsg(CtiLMControlAreaTrigger *trigger)
{
    _paoid = trigger->getPAOId();
    _triggernumber = trigger->getTriggerNumber();
    _pointvalue = trigger->getPointValue();
    _lastpointvaluetimestamp = trigger->getLastPointValueTimestamp();
    _normalstate = trigger->getNormalState();
    _threshold = trigger->getThreshold();
    _peakpointvalue = trigger->getPeakPointValue();
    _lastpeakpointvaluetimestamp = trigger->getLastPeakPointValueTimestamp();
    _projectedpointvalue = trigger->getProjectedPointValue();
}


std::string CtiLMDynamicGroupDataMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList << "LM Dynamic Group Data";
    itemList.add("PAObject ID")             << _paoid;
    itemList.add("Disable Flag")            << _disableflag;
    itemList.add("Group Control State")     << _groupcontrolstate;
    itemList.add("Current Hours Daily")     << _currenthoursdaily;
    itemList.add("Current Hours Monthly")   << _currenthoursmonthly;
    itemList.add("Current Hours Seasonal")  << _currenthoursseasonal;
    itemList.add("Current Hours Annually")  << _currenthoursannually;
    itemList.add("Last Control Sent Time")  << _lastcontrolsent;
    itemList.add("Control Start Time")      << _controlstarttime;
    itemList.add("Control Complete Time")   << _controlcompletetime;
    itemList.add("Next Control Time")       << _next_control_time;
    itemList.add("Internal State")          << _internalState;
    itemList.add("Daily Ops")               << _daily_ops;

    return (Inherited::toString() += itemList.toString());
}

std::string CtiLMDynamicProgramDataMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList << "LM Dynamic Program Data";
    itemList.add("PAObject ID")                 << _paoid;
    itemList.add("Disable Flag")                << _disableflag;
    itemList.add("Current Gear Number")         << _currentgearnumber;
    itemList.add("Last Group Controlled")       << _lastgroupcontrolled;
    itemList.add("Program State")               << _programstate;
    itemList.add("Reduction Total")             << _reductiontotal;
    itemList.add("Direct Start Time")           << _directstarttime;
    itemList.add("Direct Stop Time")            << _directstoptime;
    itemList.add("Notify Active Time")          << _notify_active_time;
    itemList.add("Notify Inactive Time")        << _notify_inactive_time;
    itemList.add("Started Ramping Out Time")    << _startedrampingouttime;
    itemList.add("origin")                      << _origin;

    return (Inherited::toString() += itemList.toString());
}

std::string CtiLMDynamicControlAreaDataMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList << "LM Dynamic Control Area Data";
    itemList.add("PAObject ID")             << _paoid;
    itemList.add("Disable Flag")            << _disableflag;
    itemList.add("Next Check Time")         << _nextchecktime;
    itemList.add("Last Group Controlled")   << _controlareastate;
    itemList.add("Current Priority")        << _currentpriority;
    itemList.add("Current Start Time")      << _currentdailystarttime;
    itemList.add("Current Stop Time")       << _currentdailystoptime;
    if( _triggers.size() > 0 )
    {
        for( int i = 0; i < _triggers.size(); i++ )
        {
            itemList.add("Trigger " + CtiNumStr(i + 1)) << _triggers[i];
        }
    }

    return (Inherited::toString() += itemList.toString());
}

std::string CtiLMDynamicTriggerDataMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList << "LM Dynamic Trigger Data";
    itemList.add("PAObject ID")                     << _paoid;
    itemList.add("Trigger Number")                  << _triggernumber;
    itemList.add("Point Value")                     << _pointvalue;
    itemList.add("Last Point Value Timestamp")      << _lastpointvaluetimestamp;
    itemList.add("Normal State")                    << _normalstate;
    itemList.add("Threshold")                       << _threshold;
    itemList.add("Peak Point Value")                << _peakpointvalue;
    itemList.add("Last Pk Point Value Timestamp")   << _lastpeakpointvaluetimestamp;
    itemList.add("Projected Point Value")           << _projectedpointvalue;

    return (Inherited::toString() += itemList.toString());
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/

CtiMessage* CtiLMDynamicGroupDataMsg::replicateMessage() const
{
    CtiLMDynamicGroupDataMsg* msg = CTIDBG_new CtiLMDynamicGroupDataMsg();
    msg->_paoid = _paoid;
    msg->_disableflag = _disableflag;
    msg->_groupcontrolstate = _groupcontrolstate;
    msg->_currenthoursdaily = _currenthoursdaily;
    msg->_currenthoursmonthly = _currenthoursmonthly;
    msg->_currenthoursseasonal = _currenthoursseasonal;
    msg->_currenthoursannually = _currenthoursannually;
    msg->_lastcontrolsent = _lastcontrolsent;
    msg->_controlstarttime = _controlstarttime;
    msg->_controlcompletetime = _controlcompletetime;
    msg->_next_control_time = _next_control_time;
    msg->_internalState = _internalState;
    msg->_daily_ops = _daily_ops;
    return msg;
}

CtiMessage* CtiLMDynamicProgramDataMsg::replicateMessage() const
{
    CtiLMDynamicProgramDataMsg* msg = CTIDBG_new CtiLMDynamicProgramDataMsg();
    msg->_paoid = _paoid;
    msg->_disableflag = _disableflag;
    msg->_currentgearnumber = _currentgearnumber;
    msg->_lastgroupcontrolled = _lastgroupcontrolled;
    msg->_programstate = _programstate;
    msg->_reductiontotal = _reductiontotal;
    msg->_directstarttime = _directstarttime;
    msg->_directstoptime = _directstoptime;
    msg->_notify_active_time = _notify_active_time;
    msg->_notify_inactive_time = _notify_inactive_time;
    msg->_startedrampingouttime = _startedrampingouttime;
    msg->_origin = _origin;
    return msg;
}

CtiMessage* CtiLMDynamicControlAreaDataMsg::replicateMessage() const
{
    CtiLMDynamicControlAreaDataMsg* msg = CTIDBG_new CtiLMDynamicControlAreaDataMsg();
    msg->_paoid = _paoid;
    msg->_disableflag = _disableflag;
    msg->_nextchecktime = _nextchecktime;
    msg->_controlareastate = _controlareastate;
    msg->_currentpriority = _currentpriority;
    msg->_currentdailystarttime = _currentdailystarttime;
    msg->_currentdailystoptime = _currentdailystoptime;

    if( _triggers.size() > 0 )
    {
        for( int i = 0; i < _triggers.size(); i++ )
        {
            msg->_triggers.push_back(_triggers[i]); //This is a copy.
        }
    }
    return msg;
}

CtiMessage* CtiLMDynamicTriggerDataMsg::replicateMessage() const
{
    CtiLMDynamicTriggerDataMsg* msg = CTIDBG_new CtiLMDynamicTriggerDataMsg();
    msg->_paoid = _paoid;
    msg->_triggernumber = _triggernumber;
    msg->_pointvalue = _pointvalue;
    msg->_lastpointvaluetimestamp = _lastpointvaluetimestamp;
    msg->_normalstate = _normalstate;
    msg->_threshold = _threshold;
    msg->_peakpointvalue = _peakpointvalue;
    msg->_lastpeakpointvaluetimestamp = _lastpeakpointvaluetimestamp;
    msg->_projectedpointvalue = _projectedpointvalue;
    return msg;
}
