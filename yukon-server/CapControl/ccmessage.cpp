/*-----------------------------------------------------------------------------
    Filename:  ccmessage.cpp

    Programmer:  Josh Wolberg

    Description:    Source file for message classes.

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "ccmessage.h"
#include "ccid.h"
#include "VoltageRegulator.h"

#include <time.h>
#include "utility.h"

using std::endl;

extern ULONG _CC_DEBUG;
/*===========================================================================
    CtiCCMessage

    Base class for all Cap Control messages
===========================================================================*/

/*---------------------------------------------------------------------------
    Message

    Returns text describing the message
---------------------------------------------------------------------------*/
const string& CtiCCMessage::getMessage() const
{
    return _message;
}

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self given a RWvistream
---------------------------------------------------------------------------*/
void CtiCCMessage::restoreGuts(RWvistream& strm)
{
    CtiMessage::restoreGuts(strm);

    strm >> _message;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCMessage::saveGuts(RWvostream& strm) const
{
    CtiMessage::saveGuts(strm);

    strm << _message;

    return;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCMessage::CtiCCMessage(const string& message) :  _message(message)
{
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCMessage& CtiCCMessage::operator=(const CtiCCMessage& right)
{

    if( this != &right )
    {
        CtiMessage::operator=(right);
        _message = right._message;
    }

    return *this;
}


/*===========================================================================
    CtiCCCommand

    Represents a command message to Cap Control
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCCommand, CTICCCOMMAND_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCCommand::CtiCCCommand(LONG commandId) :
    CtiCCMessage( string("" + commandId) ),
    _command(commandId),
    _id(0)
{
}

CtiCCCommand::CtiCCCommand(LONG commandId, LONG targetId) :
    CtiCCMessage( string("CtiCCCommand" + commandId) ),
    _command(commandId),
    _id( targetId )
{
}

CtiCCCommand::CtiCCCommand(const CtiCCCommand& commandMsg)
{
    operator=( commandMsg );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCommand::~CtiCCCommand()
{
}

/*---------------------------------------------------------------------------
    Command

    Returns the specific command that self represents
---------------------------------------------------------------------------*/
LONG CtiCCCommand::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    Id

    Returns the id of the object that is associated with command.
---------------------------------------------------------------------------*/
LONG CtiCCCommand::getId() const
{
    return _id;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiCCCommand::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _command
         >> _id;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCCommand::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm << _command
         << _id;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCCommand& CtiCCCommand::operator=(const CtiCCCommand& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);
        _command = right._command;
        _id = right._id;
    }

    return *this;
}

CtiMessage* CtiCCCommand::replicateMessage() const
{
    return new CtiCCCommand(*this);
}

/*===========================================================================
    CtiCCCapBankMoveMsg

    Message to dynamically move cap banks between feeders both temporarily
    or permanent.
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCObjectMoveMsg, CTICCOBJECTMOVEMSG_ID )

CtiCCObjectMoveMsg::CtiCCObjectMoveMsg(BOOL permanentflag, LONG oldparentid, LONG objectid,LONG newparentid,
                   float switchingorder, float closeOrder, float tripOrder) :
    CtiCCMessage( ),
    _permanentflag(permanentflag),
    _oldparentid(oldparentid),
    _objectid(objectid),
    _newparentid(newparentid),
    _switchingorder(switchingorder),
    _closeOrder(closeOrder),
    _tripOrder(tripOrder)

{


}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCObjectMoveMsg::~CtiCCObjectMoveMsg()
{
}


BOOL CtiCCObjectMoveMsg::getPermanentFlag() const
{
    return _permanentflag;
}
LONG CtiCCObjectMoveMsg::getOldParentId() const
{
    return _oldparentid;
}
LONG CtiCCObjectMoveMsg::getObjectId() const
{
    return _objectid;
}
LONG CtiCCObjectMoveMsg::getNewParentId() const
{
    return _newparentid;
}
float CtiCCObjectMoveMsg::getSwitchingOrder() const
{
    return _switchingorder;
}
float CtiCCObjectMoveMsg::getCloseOrder() const
{
    return _closeOrder;
}
float CtiCCObjectMoveMsg::getTripOrder() const
{
    return _tripOrder;
}
/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiCCObjectMoveMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _permanentflag
         >> _oldparentid
         >> _objectid
         >> _newparentid
         >> _switchingorder
         >> _closeOrder
         >> _tripOrder;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCObjectMoveMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm << _permanentflag
         << _oldparentid
         << _objectid
         << _newparentid
         << _switchingorder
         << _closeOrder
         << _tripOrder;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCObjectMoveMsg& CtiCCObjectMoveMsg::operator=(const CtiCCObjectMoveMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);

        _permanentflag   = right._permanentflag;
        _oldparentid     = right._oldparentid;
        _objectid        = right._objectid;
        _newparentid     = right._newparentid;
        _switchingorder  = right._switchingorder;
        _closeOrder      = right._closeOrder;
        _tripOrder       = right._tripOrder;

    }

    return *this;
}

/*===========================================================================
    CtiCCCapBankMoveMsg

    Message to dynamically move cap banks between feeders both temporarily
    or permanent.
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCCapBankMoveMsg, CTICCCAPBANKMOVEMSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCapBankMoveMsg::~CtiCCCapBankMoveMsg()
{
}

INT CtiCCCapBankMoveMsg::getPermanentFlag() const
{
    return _permanentflag;
}
LONG CtiCCCapBankMoveMsg::getOldFeederId() const
{
    return _oldfeederid;
}
LONG CtiCCCapBankMoveMsg::getCapBankId() const
{
    return _capbankid;
}
LONG CtiCCCapBankMoveMsg::getNewFeederId() const
{
    return _newfeederid;
}
float CtiCCCapBankMoveMsg::getCapSwitchingOrder() const
{
    return _capswitchingorder;
}
float CtiCCCapBankMoveMsg::getCloseOrder() const
{
    return _closeOrder;
}
float CtiCCCapBankMoveMsg::getTripOrder() const
{
    return _tripOrder;
}
/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiCCCapBankMoveMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _permanentflag
         >> _oldfeederid
         >> _capbankid
         >> _newfeederid
         >> _capswitchingorder
         >> _closeOrder
         >> _tripOrder;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCCapBankMoveMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm << _permanentflag
         << _oldfeederid
         << _capbankid
         << _newfeederid
         << _capswitchingorder
         << _closeOrder
         << _tripOrder;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCCapBankMoveMsg& CtiCCCapBankMoveMsg::operator=(const CtiCCCapBankMoveMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);

        _permanentflag    = right._permanentflag    ;
        _oldfeederid      = right._oldfeederid      ;
        _capbankid        = right._capbankid        ;
        _newfeederid      = right._newfeederid      ;
        _capswitchingorder= right._capswitchingorder;
        _closeOrder       = right._closeOrder;
        _tripOrder        = right._tripOrder;
    }

    return *this;
}
/*===========================================================================
    CtiCCSubstationVerificationMsg


===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSubstationVerificationMsg, CTICCSUBVERIFICATIONMSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationVerificationMsg::~CtiCCSubstationVerificationMsg()
{
}

/*LONG CtiCCSubstationVerificationMsg::getStrategy() const
{
    return _strategy;
} */

/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiCCSubstationVerificationMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _action
         >> _id
         >>_strategy
         >>_cbInactivityTime
         >> _disableOvUvFlag;

      return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSubstationVerificationMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm <<  _action
         <<  _id
         << _strategy
         << _cbInactivityTime
         << _disableOvUvFlag;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationVerificationMsg& CtiCCSubstationVerificationMsg::operator=(const CtiCCSubstationVerificationMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);
        _action           = right._action;
        _strategy         = right._strategy;
        _id               = right._id;
        _cbInactivityTime = right._cbInactivityTime;
        _disableOvUvFlag  = right._disableOvUvFlag;
    }

    return *this;
}


CtiMessage* CtiCCSubstationVerificationMsg::replicateMessage() const
{
    return new CtiCCSubstationVerificationMsg(*this);
}



/*===========================================================================
    CtiCCEventLogMsg


===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCEventLogMsg, CTICCEVENTLOG_ID )

CtiCCEventLogMsg::CtiCCEventLogMsg(const CtiCCEventLogMsg& aRef)
{
    operator=(aRef);
}
/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCEventLogMsg::~CtiCCEventLogMsg()
{
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiCCEventLogMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _logId
         >> _timeStamp
         >> _pointId
         >> _subId
         >> _feederId
         >> _eventType
         >> _seqId
         >> _value
         >> _text
         >> _userName
         >> _kvarBefore
         >> _kvarAfter
         >> _kvarChange
         >> _ipAddress
         >> _actionId
         >> _aVar
         >> _bVar
         >> _cVar
         >> _stationId
         >> _areaId
         >> _spAreaId
         >> _regulatorId;

      return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCEventLogMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm << _logId
         << _timeStamp
         << _pointId
         << _subId
         << _feederId
         << _eventType
         << _seqId
         << _value
         << _text
         << _userName
         << _kvarBefore
         << _kvarAfter
         << _kvarChange
         << _ipAddress
         << _actionId
         << _aVar
         << _bVar
         << _cVar
         << _stationId
         << _areaId
         << _spAreaId
         << _regulatorId;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCEventLogMsg& CtiCCEventLogMsg::operator=(const CtiCCEventLogMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);
        _logId      = right._logId;
        _timeStamp  = right._timeStamp;
        _pointId    = right._pointId;
        _spAreaId      = right._spAreaId;
        _areaId      = right._areaId;
        _stationId      = right._stationId;
        _subId      = right._subId;
        _feederId   = right._feederId;
        _eventType  = right._eventType;
        _seqId      = right._seqId;
        _value      = right._value;
        _text       = right._text;
        _userName   = right._userName;
        _kvarBefore = right._kvarBefore;
        _kvarAfter  = right._kvarAfter;
        _kvarChange = right._kvarChange;
        _ipAddress  = right._ipAddress;
        _actionId   = right._actionId;
        _stateInfo  = right._stateInfo;

        _aVar = right._aVar;
        _bVar = right._bVar;
        _cVar = right._cVar;

        _regulatorId = right._regulatorId;

    }

    return *this;
}

CtiMessage* CtiCCEventLogMsg::replicateMessage() const
{
    return new CtiCCEventLogMsg(*this);
}
/*===========================================================================
    CtiCCSubstationBusMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSubstationBusMsg, CTICCSUBSTATIONBUS_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, ULONG bitMask) : CtiCCMessage("CCSubstationBuses"), _ccSubstationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationBusMsg has "<< buses.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < buses.size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Sub: "<<((CtiCCSubstationBus*)buses[h])->getPaoName()<<" "<<((CtiCCSubstationBus*)buses[h])->getCurrentVarLoadPointValue()<<" "<<((CtiCCSubstationBus*)buses[h])->getEstimatedVarLoadPointValue() <<" "<<
                    ((CtiCCSubstationBus*)buses[h])->getStrategy()->getPeakLead()<<" "<<((CtiCCSubstationBus*)buses[h])->getStrategy()->getPeakLag()<< endl;
            }
            CtiFeeder_vec& feeds =   ((CtiCCSubstationBus*)buses[h])->getCCFeeders();
            for (int hh = 0; hh < feeds.size(); hh++)
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " -    Feed: "<<((CtiCCFeeder*)feeds[hh])->getPaoName()<<" "<<((CtiCCFeeder*)feeds[hh])->getCurrentVarLoadPointValue()<<" "<<((CtiCCFeeder*)feeds[hh])->getEstimatedVarLoadPointValue() <<" " <<
                        ((CtiCCFeeder*)feeds[hh])->getStrategy()->getPeakLead()<<" "<<((CtiCCFeeder*)feeds[hh])->getStrategy()->getPeakLag()<< endl;
                }

                CtiCCCapBank_SVector& caps =   ((CtiCCFeeder*)feeds[hh])->getCCCapBanks();
                for (int hhh = 0; hhh < caps.size(); hhh++)
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " -        Cap: "<<((CtiCCCapBank*)caps[hhh])->getPaoName() <<" "<<
                            ((CtiCCCapBank*)caps[hhh])->getControlStatusText()<< endl;
                    }
                }

            }
        }
    }
    for(int i=0;i<buses.size();i++)
    {
        _ccSubstationBuses->push_back(((CtiCCSubstationBus*)buses.at(i))->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_set& buses, ULONG bitMask) : CtiCCMessage("CCSubstationBuses"), _ccSubstationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationBusMsg has "<< buses.size()<<" entries." << endl;
    }
    CtiCCSubstationBus_set::iterator it;
    for(it = buses.begin(); it != buses.end();it++)
    {
        _ccSubstationBuses->push_back(((CtiCCSubstationBus*)*it)->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus* substationBus) : CtiCCMessage("CCSubstationBuses"), _ccSubstationBuses(NULL), _msgInfoBitMask(0)
{

    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    _ccSubstationBuses->push_back(substationBus->replicate());
}


CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusMsg) : CtiCCMessage("CCSubstationBuses"), _ccSubstationBuses(NULL), _msgInfoBitMask(0)
{
    operator=(substationBusMsg);
}


/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationBusMsg::~CtiCCSubstationBusMsg()
{
    if( _ccSubstationBuses != NULL &&
        _ccSubstationBuses->size() > 0 )
    {
        delete_container(*_ccSubstationBuses);
        _ccSubstationBuses->clear();
        delete _ccSubstationBuses;
    }
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiCCSubstationBusMsg::replicateMessage() const
{
    return new CtiCCSubstationBusMsg(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationBusMsg& CtiCCSubstationBusMsg::operator=(const CtiCCSubstationBusMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);

        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _ccSubstationBuses != NULL &&
            _ccSubstationBuses->size() > 0 )
        {
            delete_container(*_ccSubstationBuses);
            _ccSubstationBuses->clear();
            delete _ccSubstationBuses;
        }
        if ( _ccSubstationBuses == NULL )
            _ccSubstationBuses = new CtiCCSubstationBus_vec;
        for(int i=0;i<(right.getCCSubstationBuses())->size();i++)
        {
            _ccSubstationBuses->push_back(((CtiCCSubstationBus*)(*right.getCCSubstationBuses()).at(i))->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCSubstationBusMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _msgInfoBitMask
         >> _ccSubstationBuses;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSubstationBusMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    strm << _msgInfoBitMask
         << _ccSubstationBuses;
}

// Static Members
ULONG CtiCCSubstationBusMsg::AllSubBusesSent = 0x00000001;
ULONG CtiCCSubstationBusMsg::SubBusDeleted   = 0x00000002;
ULONG CtiCCSubstationBusMsg::SubBusAdded     = 0x00000004;
ULONG CtiCCSubstationBusMsg::SubBusModified  = 0x00000008;

/*===========================================================================
    CtiCCCapBankStatesMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCCapBankStatesMsg, CTICCCAPBANKSTATES_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg::CtiCCCapBankStatesMsg(CtiCCState_vec& ccCapBankStates) : CtiCCMessage("CCCapBankStates"), _ccCapBankStates(NULL)
{
    _ccCapBankStates = new CtiCCState_vec;
    int y = ccCapBankStates.size();
    for(int i=0;i<y;i++)
    {
        _ccCapBankStates->push_back(((CtiCCState*)ccCapBankStates.at(i))->replicate());
    }
}

CtiCCCapBankStatesMsg::CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg) : CtiCCMessage("CCCapBankStates"), _ccCapBankStates(NULL)
{
    operator=(ccCapBankStatesMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg::~CtiCCCapBankStatesMsg()
{
    if( _ccCapBankStates != NULL &&
            _ccCapBankStates->size() > 0 )
        {
            delete_container(*_ccCapBankStates);
            _ccCapBankStates->clear();
            delete _ccCapBankStates;
        }

}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiCCCapBankStatesMsg::replicateMessage() const
{
    return new CtiCCCapBankStatesMsg(*this);

}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg& CtiCCCapBankStatesMsg::operator=(const CtiCCCapBankStatesMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);

        if( _ccCapBankStates != NULL &&
            _ccCapBankStates->size() > 0 )
        {
            delete_container(*_ccCapBankStates);
            _ccCapBankStates->clear();
            delete _ccCapBankStates;
        }

        int y = (right.getCCCapBankStates())->size();

        if ( _ccCapBankStates == NULL )
            _ccCapBankStates = new CtiCCState_vec;

        for(int i=0;i < y;i++)
        {
            _ccCapBankStates->push_back(((CtiCCState*)(*right.getCCCapBankStates()).at(i))->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCCapBankStatesMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _ccCapBankStates;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCCapBankStatesMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    strm << _ccCapBankStates;
}


// Static Members
ULONG CtiCCGeoAreasMsg::AllAreasSent = 0x00000001;
ULONG CtiCCGeoAreasMsg::AreaDeleted   = 0x00000002;
ULONG CtiCCGeoAreasMsg::AreaAdded     = 0x00000004;
ULONG CtiCCGeoAreasMsg::AreaModified  = 0x00000008;
/*===========================================================================
    CtiCCGeoAreasMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCGeoAreasMsg, CTICCGEOAREAS_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCArea_vec& ccGeoAreas, ULONG bitMask) : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL), _msgInfoBitMask(bitMask)
{
    _ccGeoAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCGeoAreasMsg has "<< ccGeoAreas.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < ccGeoAreas.size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Area: "<<((CtiCCArea*)ccGeoAreas[h])->getPaoName()<< endl;
            }
        }
    }

    for(int i=0;i<ccGeoAreas.size();i++)
    {
        _ccGeoAreas->push_back(((CtiCCArea*)ccGeoAreas.at(i))->replicate());
    }
}


CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCArea_set& ccGeoAreas, ULONG bitMask) : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL), _msgInfoBitMask(bitMask)
{
    _ccGeoAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCGeoAreasMsg has "<< ccGeoAreas.size()<<" entries." << endl;
    }
    CtiCCArea_set::iterator it;
    for(it = ccGeoAreas.begin(); it != ccGeoAreas.end(); it++)
    {
        _ccGeoAreas->push_back(((CtiCCArea*)*it)->replicate());
    }
}




CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreasMsg) : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL), _msgInfoBitMask(ccGeoAreasMsg._msgInfoBitMask)
{
    operator=(ccGeoAreasMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCGeoAreasMsg::~CtiCCGeoAreasMsg()
{
    if( _ccGeoAreas != NULL &&
            _ccGeoAreas->size() > 0 )
        {
            delete_container(*_ccGeoAreas);
            _ccGeoAreas->clear();
            delete _ccGeoAreas;
        }
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiCCGeoAreasMsg::replicateMessage() const
{
    return new CtiCCGeoAreasMsg(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCGeoAreasMsg& CtiCCGeoAreasMsg::operator=(const CtiCCGeoAreasMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);

        if( _ccGeoAreas != NULL &&
            _ccGeoAreas->size() > 0 )
        {
            delete_container(*_ccGeoAreas);
            _ccGeoAreas->clear();
            delete _ccGeoAreas;
        }

        if ( _ccGeoAreas == NULL )
            _ccGeoAreas = new CtiCCArea_vec;
        for(int i=0;i<(right.getCCGeoAreas())->size();i++)
        {
            _ccGeoAreas->push_back(((CtiCCArea*)(*right.getCCGeoAreas()).at(i))->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCGeoAreasMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _msgInfoBitMask;
    strm >> _ccGeoAreas;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCGeoAreasMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    strm << _msgInfoBitMask;
    strm << _ccGeoAreas;
}



/*===========================================================================
    CtiCCGeoAreasMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSpecialAreasMsg, CTICCSPECIALAREAS_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(CtiCCSpArea_vec& ccSpecialAreas) : CtiCCMessage("CCSpecialAreas"), _ccSpecialAreas(NULL)
{
    _ccSpecialAreas = new CtiCCSpArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < ccSpecialAreas.size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Area: "<<((CtiCCSpecial*)ccSpecialAreas[h])->getPaoName()<< endl;
            }
        }
    }

    for(int i=0;i<ccSpecialAreas.size();i++)
    {
        _ccSpecialAreas->push_back((CtiCCSpecial*)(ccSpecialAreas.at(i))->replicate());
    }
}

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(CtiCCSpArea_set& ccSpecialAreas) : CtiCCMessage("CCSpecialAreas"), _ccSpecialAreas(NULL)
{
    _ccSpecialAreas = new CtiCCSpArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries." << endl;
    }
    CtiCCSpArea_set::iterator it;
    for(it = ccSpecialAreas.begin(); it != ccSpecialAreas.end(); it++)
    {
        _ccSpecialAreas->push_back((CtiCCSpecial*)(*it)->replicate());
    }
}

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(const CtiCCSpecialAreasMsg& ccSpecialAreasMsg) : CtiCCMessage("CCSpecialAreas"), _ccSpecialAreas(NULL)
{
    operator=(ccSpecialAreasMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSpecialAreasMsg::~CtiCCSpecialAreasMsg()
{
    if( _ccSpecialAreas != NULL &&
            _ccSpecialAreas->size() > 0 )
        {
            delete_container(*_ccSpecialAreas);
            _ccSpecialAreas->clear();
            delete _ccSpecialAreas;
        }
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiCCSpecialAreasMsg::replicateMessage() const
{
    return new CtiCCSpecialAreasMsg(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSpecialAreasMsg& CtiCCSpecialAreasMsg::operator=(const CtiCCSpecialAreasMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);

        if( _ccSpecialAreas != NULL &&
            _ccSpecialAreas->size() > 0 )
        {
            delete_container(*_ccSpecialAreas);
            _ccSpecialAreas->clear();
            delete _ccSpecialAreas;
        }
        if ( _ccSpecialAreas == NULL )
            _ccSpecialAreas = new CtiCCSpArea_vec;
        for(int i=0;i<(right.getCCSpecialAreas())->size();i++)
        {

            _ccSpecialAreas->push_back(((CtiCCSpecial*)(*right.getCCSpecialAreas()).at(i))->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCSpecialAreasMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _ccSpecialAreas;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSpecialAreasMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - CtiCCSpecialAreasMsg has "<< _ccSpecialAreas->size()<<" entries." << endl;
        }
        for (int h=0;h < _ccSpecialAreas->size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Area: "<<((CtiCCSpecial*)_ccSpecialAreas->at(h))->getPaoName()<<
                    " : "<<((CtiCCSpecial*)_ccSpecialAreas->at(h))->getPaoId()<< endl;
            }
        }
    }


    strm << _ccSpecialAreas;
}

ULONG CtiCCSubstationsMsg::AllSubsSent= 0x00000001;
ULONG CtiCCSubstationsMsg::SubDeleted = 0x00000002;
ULONG CtiCCSubstationsMsg::SubAdded   = 0x00000004;
ULONG CtiCCSubstationsMsg::SubModified= 0x00000008;
/*===========================================================================
    CtiCCGeoAreasMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSubstationsMsg, CTICCSUBSTATION_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCSubstationsMsg::CtiCCSubstationsMsg(CtiCCSubstation_vec& ccSubstations, ULONG bitMask) : CtiCCMessage("CCSubstations"), _ccSubstations(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationsMsg has "<< ccSubstations.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < ccSubstations.size(); h++)
        {
            CtiCCSubstation* station = (CtiCCSubstation*)ccSubstations[h];
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Substation: "<<station->getPaoName()<< " Parent: "<< station->getParentId() <<" SAEnabled ?"<<station->getSaEnabledFlag() << " SAEnId : " << station->getSaEnabledId()<< endl;
            }
            Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
            while (iterBus  != station->getCCSubIds()->end())
            {
                LONG busId = *iterBus;
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " -    SubBus: "<< busId << endl;
                }
                iterBus++;
            }
        }
    }

    for(int i=0;i<ccSubstations.size();i++)
    {
        _ccSubstations->push_back((CtiCCSubstation*)(ccSubstations.at(i))->replicate());
    }

}

CtiCCSubstationsMsg::CtiCCSubstationsMsg(CtiCCSubstation_set& ccSubstations, ULONG bitMask) : CtiCCMessage("CCSubstations"), _ccSubstations(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationsMsg has "<< ccSubstations.size()<<" entries." << endl;
    }
    CtiCCSubstation_set::iterator it;
    for(it = ccSubstations.begin(); it != ccSubstations.end(); it++)
    {
        _ccSubstations->push_back((CtiCCSubstation*)(*it)->replicate());
    }

}


CtiCCSubstationsMsg::CtiCCSubstationsMsg(const CtiCCSubstationsMsg& ccSubstationsMsg) : CtiCCMessage("CCSubstations"), _ccSubstations(NULL)
{
    operator=(ccSubstationsMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationsMsg::~CtiCCSubstationsMsg()
{
    if( _ccSubstations != NULL &&
            _ccSubstations->size() > 0 )
        {
            delete_container(*_ccSubstations);
            _ccSubstations->clear();
            delete _ccSubstations;
        }
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiCCSubstationsMsg::replicateMessage() const
{
    return new CtiCCSubstationsMsg(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationsMsg& CtiCCSubstationsMsg::operator=(const CtiCCSubstationsMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);
        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _ccSubstations != NULL &&
            _ccSubstations->size() > 0 )
        {
            delete_container(*_ccSubstations);
            _ccSubstations->clear();
            delete _ccSubstations;
        }
        if ( _ccSubstations == NULL )
            _ccSubstations = new CtiCCSubstation_vec;
        for(int i=0;i<(right.getCCSubstations())->size();i++)
        {
            _ccSubstations->push_back(((CtiCCSubstation*)(*right.getCCSubstations()).at(i))->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCSubstationsMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _msgInfoBitMask;
    strm >> _ccSubstations;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSubstationsMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    strm << _msgInfoBitMask;
    strm << _ccSubstations;
}


/*===========================================================================
    CtiCCShutdown
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCShutdown, CTICCSHUTDOWN_ID )

/*---------------------------------------------------------------------------
    restoreGuts

    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCShutdown::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCShutdown::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
}








/*===========================================================================
    CtiCCSubstationVerificationMsg


===========================================================================*/

RWDEFINE_COLLECTABLE( CtiPAOScheduleMsg, CTIPAOSCHEDULEMSG_ID )

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiPAOScheduleMsg::~CtiPAOScheduleMsg()
{
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiPAOScheduleMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> _scheduleId;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiPAOScheduleMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm << _scheduleId;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiPAOScheduleMsg& CtiPAOScheduleMsg::operator=(const CtiPAOScheduleMsg& right)
{
    if( this != &right )
    {
        CtiCCMessage::operator=(right);
        _scheduleId    = right._scheduleId;
    }

    return *this;
}

/*===========================================================================
    CtiCCServerResponse
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCServerResponse, CTICCSERVERRESPONSE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/

CtiCCServerResponse::CtiCCServerResponse(long type, string res) :
    CtiCCMessage( string("CtiCCServerResponse" + type) )
{
    response = res;
    responseType = type;
}

CtiCCServerResponse::CtiCCServerResponse(const CtiCCServerResponse& commandMsg)
{
    operator=( commandMsg );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCServerResponse::~CtiCCServerResponse()
{
}

long CtiCCServerResponse::getResponseType() const
{
    return responseType;
}

/*---------------------------------------------------------------------------
    Id

    Returns the id of the object that is associated with command.
---------------------------------------------------------------------------*/
string CtiCCServerResponse::getResponse() const
{
    return response;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restores the state of self from the given RWvistream
---------------------------------------------------------------------------*/
void CtiCCServerResponse::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
    strm >> responseType
         >> response;

    return;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCServerResponse::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);

    strm << responseType
         << response;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCServerResponse& CtiCCServerResponse::operator=(const CtiCCServerResponse& right)
{

    if( this != &right )
    {
        CtiCCMessage::operator=(right);
        response = right.response;
        responseType = right.responseType;
    }

    return *this;
}

CtiMessage* CtiCCServerResponse::replicateMessage() const
{
    return new CtiCCServerResponse(*this);
}


//////////////////////////
///
RWDEFINE_COLLECTABLE( VoltageRegulatorMessage, CTIVOLTAGEREGULATOR_MSG_ID )

VoltageRegulatorMessage::VoltageRegulatorMessage()
    : CtiCCMessage("VoltageRegulatorMessage")
{
    // empty
}


VoltageRegulatorMessage::VoltageRegulatorMessage(const VoltageRegulatorMessage & toCopy)
    : CtiCCMessage("VoltageRegulatorMessage")
{
    operator=(toCopy);
}


VoltageRegulatorMessage & VoltageRegulatorMessage::operator=(const VoltageRegulatorMessage & rhs)
{
    if( this != &rhs )
    {
        CtiCCMessage::operator=(rhs);

        cleanup();
        for each( Cti::CapControl::VoltageRegulator * regulator in rhs.regulators )
        {
            insert(regulator);
        }
    }

    return *this;
}


VoltageRegulatorMessage::~VoltageRegulatorMessage()
{
    cleanup();
}


CtiMessage * VoltageRegulatorMessage::replicateMessage() const
{
    return new VoltageRegulatorMessage(*this);
}


void VoltageRegulatorMessage::saveGuts(RWvostream & ostrm) const
{
    CtiCCMessage::saveGuts(ostrm);

    ostrm << regulators;
}


void VoltageRegulatorMessage::insert(Cti::CapControl::VoltageRegulator * regulator)
{
    regulators.push_back( regulator->replicate() );
}


void VoltageRegulatorMessage::cleanup()
{
    if (regulators.size() > 0)
    {
        delete_container(regulators);
        regulators.clear();
    }
}

