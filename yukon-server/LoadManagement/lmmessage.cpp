/*-----------------------------------------------------------------------------
    Filename:  lmmessage.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Source file for message classes.

    Initial Date:  2/13/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "lmmessage.h"
#include "lmid.h"

#include <rw/collstr.h>
#include <time.h>

/*===========================================================================
    CtiLMMessage
    
    Base class for all Load Management messages
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMMessage, CTILMMESSAGE_ID ) 

/*---------------------------------------------------------------------------
    getMessage
    
    Returns text describing the message
---------------------------------------------------------------------------*/
const RWCString& CtiLMMessage::getMessage() const
{
    return _message;
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
CtiLMMessage::CtiLMMessage(const RWCString& message) :  _message(message)
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
UINT CtiLMCommand::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    getPAOId
    
    Returns the id of the object that is associated with command.
---------------------------------------------------------------------------*/
ULONG CtiLMCommand::getPAOId() const
{
    return _paoid;
}
    
/*---------------------------------------------------------------------------
    getNumber
    
    Returns the number of the object that is associated with command.
---------------------------------------------------------------------------*/
ULONG CtiLMCommand::getNumber() const
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
         >> _value;
 
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
         << _value;

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
    }

    return *this;
}


/*===========================================================================
    CtiLMManualControlMsg
    
    Represents a manual program control message from the Load Management
    Client
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiLMManualControlMsg, CTILMMANUALCONTROLMSG_ID ) 

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMManualControlMsg::~CtiLMManualControlMsg()
{
}

/*---------------------------------------------------------------------------
    getCommand
    
    Returns the specific manual control command that self represents
---------------------------------------------------------------------------*/
UINT CtiLMManualControlMsg::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the object that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMManualControlMsg::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getNotifyTime

    Returns the notification time of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMManualControlMsg::getNotifyTime() const
{
    return _notifytime;
}

/*---------------------------------------------------------------------------
    getStartTime

    Returns the start time of the object that is associated with control.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMManualControlMsg::getStartTime() const
{
    return _starttime;
}

/*---------------------------------------------------------------------------
    getStopTime
    
    Returns the stop time of the object that is associated with control.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMManualControlMsg::getStopTime() const
{
    return _stoptime;
}

/*---------------------------------------------------------------------------
    getStartGear
    
    Returns the start gear of the object that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMManualControlMsg::getStartGear() const
{
    return _startgear;
}

/*---------------------------------------------------------------------------
    getStartPriority
    
    Returns the start priority of the object that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMManualControlMsg::getStartPriority() const
{
    return _startpriority;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo
    
    Returns the additional info string of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const RWCString& CtiLMManualControlMsg::getAdditionalInfo() const
{
    return _additionalinfo;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiLMManualControlMsg::restoreGuts(RWvistream& strm)
{
    CtiLMMessage::restoreGuts(strm);
    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;

    strm >> _command
         >> _paoid
         >> tempTime1
         >> tempTime2
         >> tempTime3
         >> _startgear
         >> _startpriority
         >> _additionalinfo;

    _notifytime = RWDBDateTime(tempTime1);
    _starttime = RWDBDateTime(tempTime2);
    _stoptime = RWDBDateTime(tempTime3);
    return;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMManualControlMsg::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);

    strm << _command
         << _paoid
         << _notifytime.rwtime()
         << _starttime.rwtime()
         << _stoptime.rwtime()
         << _startgear
         << _startpriority
         << _additionalinfo;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMManualControlMsg& CtiLMManualControlMsg::operator=(const CtiLMManualControlMsg& right)
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
    }

    return *this;
}


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
UINT CtiLMEnergyExchangeControlMsg::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the id of the object that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeControlMsg::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the id of the object that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeControlMsg::getOfferId() const
{
    return _offerid;
}

/*---------------------------------------------------------------------------
    getOfferDate

    Returns the date of the offer.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeControlMsg::getOfferDate() const
{
    return _offerdate;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime

    Returns the notification date time of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeControlMsg::getNotificationDateTime() const
{
    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getExpirationDateTime

    Returns the date and time when the offer will expire.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeControlMsg::getExpirationDateTime() const
{
    return _expirationdatetime;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo
    
    Returns the additional info string of the object that is associated with
    control.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeControlMsg::getAdditionalInfo() const
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
ULONG CtiLMEnergyExchangeControlMsg::getPriceOffered(int i) const
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
    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;

    strm >> _command
         >> _paoid
         >> _offerid
         >> tempTime1
         >> tempTime2
         >> tempTime3
         >> _additionalinfo;

    for(ULONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm >> _amountsrequested[i];
    }
    
    for(ULONG j=0;j<HOURS_IN_DAY;j++)
    {
        strm >> _pricesoffered[j];
    }

    _offerdate = RWDBDateTime(tempTime1);
    _notificationdatetime = RWDBDateTime(tempTime2);
    _expirationdatetime = RWDBDateTime(tempTime3);
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
         << _offerdate.rwtime()
         << _notificationdatetime.rwtime()
         << _expirationdatetime.rwtime()
         << _additionalinfo;
        
    for(ULONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm << _amountsrequested[i];
    }

    for(ULONG j=0;j<HOURS_IN_DAY;j++)
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

        for(ULONG i=0;i<HOURS_IN_DAY;i++)
        {
            _amountsrequested[i] = right.getAmountRequested(i);
        }

        for(ULONG j=0;j<HOURS_IN_DAY;j++)
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
ULONG CtiLMEnergyExchangeAcceptMsg::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the id of the offer that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeAcceptMsg::getOfferId() const
{
    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the revision number of the offer that is associated with control.
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeAcceptMsg::getRevisionNumber() const
{
    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getAcceptStatus
    
    Returns the accept status string for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeAcceptMsg::getAcceptStatus() const
{
    return _acceptstatus;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAcceptUser
    
    Returns the ip address of accept user for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeAcceptMsg::getIPAddressOfAcceptUser() const
{
    return _ipaddressofacceptuser;
}

/*---------------------------------------------------------------------------
    getUserIdName
    
    Returns the user id name for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeAcceptMsg::getUserIdName() const
{
    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAcceptPerson
    
    Returns the name of accept person for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeAcceptMsg::getNameOfAcceptPerson() const
{
    return _nameofacceptperson;
}

/*---------------------------------------------------------------------------
    getEnergyExchangeNotes
    
    Returns the energy exchange notes for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeAcceptMsg::getEnergyExchangeNotes() const
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
    //RWTime tempTime1;

    strm >> _paoid
         >> _offerid
         >> _revisionnumber
         >> _acceptstatus
         >> _ipaddressofacceptuser
         >> _useridname
         >> _nameofacceptperson
         >> _energyexchangenotes;

    for(ULONG i=0;i<HOURS_IN_DAY;i++)
    {
        strm >> _amountscommitted[i];
    }
    
    //_offerdate = RWDBDateTime(tempTime1);
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
        
    for(ULONG i=0;i<HOURS_IN_DAY;i++)
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

        for(ULONG i=0;i<HOURS_IN_DAY;i++)
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

ULONG CtiLMControlAreaMsg::numberOfReferences = 0;
/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg::CtiLMControlAreaMsg(RWOrdered& contAreas) : CtiLMMessage("ControlArea"), _controlAreas(NULL)
{
    _controlAreas = new RWOrdered(contAreas.entries());
    for(int i=0;i<contAreas.entries();i++)
    {
        _controlAreas->insert(((CtiLMControlArea*)contAreas[i])->replicate());
    }
    /*numberOfReferences++;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Number of CtiLMControlAreaMsg increased to: " << numberOfReferences << endl;
    }*/
}

/*CtiLMControlAreaMsg::CtiLMControlAreaMsg(const CtiLMControlAreaMsg& contAreaMsg) : CtiLMMessage("ControlArea")
{
    operator=( contAreaMsg );
}*/

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg::~CtiLMControlAreaMsg()
{
    _controlAreas->clearAndDestroy();
    delete _controlAreas;
    /*numberOfReferences--;
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Number of CtiLMControlAreaMsg decreased to: " << numberOfReferences << endl;
    }*/
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiLMControlAreaMsg::replicateMessage() const
{
    //return new CtiLMControlAreaMsg(*this);
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - Do not call me!!! " << __FILE__ << __LINE__ << endl;
    return NULL;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMControlAreaMsg& CtiLMControlAreaMsg::operator=(const CtiLMControlAreaMsg& right)
{
    if( this != &right )
    {
        if( _controlAreas != NULL &&
            _controlAreas->entries() > 0 )
        {
            _controlAreas->clearAndDestroy();
            delete _controlAreas;
        }
        _controlAreas = new RWOrdered((right.getControlAreas())->entries());
        for(int i=0;i<(right.getControlAreas())->entries();i++)
        {
            _controlAreas->insert(((CtiLMControlArea*)(*right.getControlAreas())[i]));
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
	strm >> _controlAreas;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiLMControlAreaMsg::saveGuts(RWvostream& strm) const
{
    CtiLMMessage::saveGuts(strm);
    strm << _controlAreas;
}


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
ULONG CtiLMCurtailmentAcknowledgeMsg::getPAOId() const
{
    return _paoid;
}
    
/*---------------------------------------------------------------------------
    getCurtailReferenceId

    Returns the refernce id of the object that is associated with command.
---------------------------------------------------------------------------*/
ULONG CtiLMCurtailmentAcknowledgeMsg::getCurtailReferenceId() const
{
    return _curtailreferenceid;
}
    
/*---------------------------------------------------------------------------
    getAcknowledgeStatus
    
    Returns the acknowledge status string for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailmentAcknowledgeMsg::getAcknowledgeStatus() const
{
    return _acknowledgestatus;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAckUser
    
    Returns the ip address of ack user for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailmentAcknowledgeMsg::getIPAddressOfAckUser() const
{
    return _ipaddressofackuser;
}

/*---------------------------------------------------------------------------
    getUserIdName
    
    Returns the user id name for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailmentAcknowledgeMsg::getUserIdName() const
{
    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAckPerson
    
    Returns the name of ack person for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailmentAcknowledgeMsg::getNameOfAckPerson() const
{
    return _nameofackperson;
}

/*---------------------------------------------------------------------------
    getCurtailmentNotes
    
    Returns the curtailment notes for the object that is associated
    with command.
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailmentAcknowledgeMsg::getCurtailmentNotes() const
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
