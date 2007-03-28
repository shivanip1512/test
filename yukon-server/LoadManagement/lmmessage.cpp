/*-----------------------------------------------------------------------------
    Filename:  lmmessage.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Source file for message classes.

    Initial Date:  2/13/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmmessage.h"
#include "lmid.h"
#include "rwutil.h"
#include "utility.h"
#include <rw/collstr.h>
#include <time.h>

using std::vector;

/*===========================================================================
    CtiLMMessage
    
    Base class for all Load Management messages
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMMessage, CTILMMESSAGE_ID ) 

/*---------------------------------------------------------------------------
    getMessage
    
    Returns text describing the message
---------------------------------------------------------------------------*/
const string& CtiLMMessage::getMessage() const
{
    return _message;
}

/*-----------------------------------------------------------------------------
  getConnection

  Returns a shared pointer to the connecition that produced this message.
  It is possible for the shared_ptr that is returned to be invalid and it
  must be tested against NULL or 0 before used!
-----------------------------------------------------------------------------*/  
CtiLMConnectionPtr CtiLMMessage::getConnection()
{
    return _connection.lock();
}
    
/*---------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self given a RWvistream
---------------------------------------------------------------------------*/
void CtiLMMessage::restoreGuts(RWvistream& strm)
{
    CtiMessage::restoreGuts(strm);

    strm >> _message;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMMessage::saveGuts(RWvostream& strm) const
{
    CtiMessage::saveGuts(strm);

    strm << _message;

    return;
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

RWDEFINE_COLLECTABLE( CtiLMCommand, CTILMCOMMAND_ID ) 

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

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMCommand::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    strm >> _command
         >> _paoid
         >> _number
         >> _value
         >> _count
         >> _auxid;
 
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMCommand::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);

    strm << _command
         << _paoid
         << _number
         << _value
         << _count
         << _auxid;

    return;
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

RWDEFINE_COLLECTABLE( CtiLMManualControlRequest, CTILMMANUALCONTROLREQUEST_ID ) 

CtiLMManualControlRequest::CtiLMManualControlRequest(LONG cmd,
						     LONG pao_id,
						     const CtiTime& notify_time,
						     const CtiTime& start_time,
						     const CtiTime& stop_time,
						     LONG start_gear,
						     LONG start_priority,
						     const string& addl_info,
						     LONG constraint_cmd) :
    _command(cmd),
    _paoid(pao_id),
    _notifytime(notify_time),
    _starttime(start_time),
    _stoptime(stop_time),
    _startgear(start_gear),
    _startpriority(start_priority),
    _additionalinfo(addl_info),
    _constraint_cmd(constraint_cmd)
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
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiLMManualControlRequest::replicateMessage() const
{
    return CTIDBG_new CtiLMManualControlRequest(*this);
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMManualControlRequest::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    CtiTime tempTime1;
    CtiTime tempTime2;
    CtiTime tempTime3;

    strm >> _command
         >> _paoid
         >> tempTime1
         >> tempTime2
         >> tempTime3
         >> _startgear
         >> _startpriority
         >> _additionalinfo
	 >>_constraint_cmd;

    _notifytime = CtiTime(tempTime1);
    _starttime = CtiTime(tempTime2);
    _stoptime = CtiTime(tempTime3);
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMManualControlRequest::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);
    
    strm << _command
         << _paoid
         << _notifytime
         << _starttime
         << _stoptime
         << _startgear
         << _startpriority
         << _additionalinfo
	 << _constraint_cmd;

    return;
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
    }

    return *this;
}


/*===========================================================================
    CtiLMManualControlResponse
    
    Represents a manual program control response from the Load Management
    Server
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMManualControlResponse, CTILMMANUALCONTROLRESPONSE_ID )

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

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMManualControlResponse::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    vector<RWCollectableString*>* rw_ordered = CTIDBG_new vector<RWCollectableString*>;

    strm >> _paoid;
    strm >> rw_ordered;
    strm >> _best_fit_action;
    
    for(int i = 0; i < rw_ordered->size(); i++)
    {
        _constraintViolations.push_back(  ((RWCollectableString*) (*rw_ordered)[i])->data());
    }
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMManualControlResponse::saveGuts(RWvostream& strm) const
{

/* NEW */
   CtiLMMessage::saveGuts(strm);
    vector<RWCollectableString*>* vect = CTIDBG_new vector<RWCollectableString*>;
    for(std::vector< std::string >::const_iterator iter = _constraintViolations.begin();
	iter != _constraintViolations.end();
	iter++)
    {
	vect->push_back(CTIDBG_new RWCollectableString(iter->c_str()));
    }
    strm << _paoid;
    strm << vect;
    strm << _best_fit_action;
    return;

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
const vector<string>& CtiLMManualControlResponse::getConstraintViolations() const
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
CtiLMManualControlResponse& CtiLMManualControlResponse::setConstraintViolations(const vector<string>& constraintViolations)
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

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeControlMsg, CTILMENERGYEXCHANGECONTROLMSG_ID ) 

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

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeControlMsg::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    CtiTime tempTime1;
    CtiTime tempTime2;
    CtiTime tempTime3;

    strm >> _command
         >> _paoid
         >> _offerid
         >> tempTime1
         >> tempTime2
         >> tempTime3
         >> _additionalinfo;

    for(LONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm >> _amountsrequested[i];
    }
    
    for(LONG j=0;j<HOURS_IN_DAY;j++)
    {
        strm >> _pricesoffered[j];
    }

    _offerdate = CtiTime(tempTime1);
    _notificationdatetime = CtiTime(tempTime2);
    _expirationdatetime = CtiTime(tempTime3);
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeControlMsg::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);

    strm << _command
         << _paoid
         << _offerid
         << _offerdate
         << _notificationdatetime
         << _expirationdatetime
         << _additionalinfo;
        
    for(LONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm << _amountsrequested[i];
    }

    for(LONG j=0;j<HOURS_IN_DAY;j++)
    {
        strm << _pricesoffered[j];
    }

    return;
}

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

        for(LONG i=0;i<HOURS_IN_DAY;i++)
        {
            _amountsrequested[i] = right.getAmountRequested(i);
        }

        for(LONG j=0;j<HOURS_IN_DAY;j++)
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

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeAcceptMsg, CTILMENERGYEXCHANGEACCEPTMSG_ID ) 

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

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeAcceptMsg::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    //CtiTime tempTime1;

    strm >> _paoid
         >> _offerid
         >> _revisionnumber
         >> _acceptstatus
         >> _ipaddressofacceptuser
         >> _useridname
         >> _nameofacceptperson
         >> _energyexchangenotes;

    for(LONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm >> _amountscommitted[i];
    }
    
    //_offerdate = CtiTime(tempTime1);
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeAcceptMsg::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);

    strm << _paoid
         << _offerid
         << _revisionnumber
         << _acceptstatus
         << _ipaddressofacceptuser
         << _useridname
         << _nameofacceptperson
         << _energyexchangenotes;
        
    for(LONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm << _amountscommitted[i];
    }

    return;
}

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

        for(LONG i=0;i<HOURS_IN_DAY;i++)
        {
            _amountscommitted[i] = right.getAmountCommitted(i);
        }
    }

    return *this;
}


/*===========================================================================
    CtiLMControlAreaMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMControlAreaMsg, CTILMCONTROLAREA_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg::CtiLMControlAreaMsg(vector<CtiLMControlArea*>& contAreas, ULONG bitMask) : CtiLMMessage("ControlArea"), _controlAreas(NULL), _msgInfoBitMask(bitMask)
{
    _controlAreas = CTIDBG_new vector<CtiLMControlArea*>;
    for(int i=0;i<contAreas.size();i++)
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
    delete_vector(_controlAreas);
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
            delete_vector(_controlAreas);
            _controlAreas->clear();
            delete _controlAreas;
        }
        _controlAreas = CTIDBG_new vector<CtiLMControlArea*>;
        for(int i=0;i<(right.getControlAreas())->size();i++)
        {
            _controlAreas->push_back(((CtiLMControlArea*)(*right.getControlAreas())[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiLMControlAreaMsg::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
	strm >> _msgInfoBitMask
         >> _controlAreas;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMControlAreaMsg::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);
    strm << _msgInfoBitMask
         << _controlAreas;
}

// Static Members
ULONG CtiLMControlAreaMsg::AllControlAreasSent = 0x00000001;
ULONG CtiLMControlAreaMsg::ControlAreaDeleted  = 0x00000002;


/*===========================================================================
    CtiLMCurtailmentAcknowledgeMsg
    
    Represents a curtailment acknowledge message to Load Management
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMCurtailmentAcknowledgeMsg, CTILMCURTAILMENTACK_MSG_ID ) 

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

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMCurtailmentAcknowledgeMsg::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    strm >> _paoid
         >> _curtailreferenceid
         >> _acknowledgestatus
         >> _ipaddressofackuser
         >> _useridname
         >> _nameofackperson
         >> _curtailmentnotes;
 
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMCurtailmentAcknowledgeMsg::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);

    strm << _paoid
         << _curtailreferenceid
         << _acknowledgestatus
         << _ipaddressofackuser
         << _useridname
         << _nameofackperson
         << _curtailmentnotes;

    return;
}

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


/*===========================================================================
    CtiLMShutdown
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMShutdown, CTILMSHUTDOWN_ID )


/*---------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiLMShutdown::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMShutdown::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);
}
