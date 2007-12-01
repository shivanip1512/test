/*-----------------------------------------------------------------------------
    Filename:  ccmessage.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Source file for message classes.

    Initial Date:  9/04/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ccmessage.h"
#include "ccid.h"

#include <rw/collstr.h>
#include <time.h>
//#include "rwutil.h"
#include "utility.h"


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


/*===========================================================================
    CtiCCCommand
    
    Represents a command message to Cap Control
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCCommand, CTICCCOMMAND_ID ) 

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCCommand::CtiCCCommand(LONG command) : 
    CtiCCMessage( string("" + command) ),
    _command(command)
{
}
    
CtiCCCommand::CtiCCCommand(LONG command, LONG id) :
    CtiCCMessage( string("CtiCCCommand" + command) ),
    _command(command),
    _id( id )
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
         >>_cbInactivityTime;

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
         << _cbInactivityTime;   

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstationVerificationMsg& CtiCCSubstationVerificationMsg::operator=(const CtiCCSubstationVerificationMsg& right)
{
    if( this != &right )
    {
        _action           = right._action;
        _strategy         = right._strategy;
        _id               = right._id;
        _cbInactivityTime = right._cbInactivityTime;
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
         >> _actionId;

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
         << _actionId;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCEventLogMsg& CtiCCEventLogMsg::operator=(const CtiCCEventLogMsg& right)
{
    if( this != &right )
    {
        _logId      = right._logId;    
        _timeStamp  = right._timeStamp;
        _pointId    = right._pointId;  
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

    }

    return *this;
}


CtiMessage* CtiCCEventLogMsg::replicateMessage() const
{
    return new CtiCCEventLogMsg(*this);
}
/*===========================================================================
    CtiCCAreaMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCAreaMsg, CTICCAREA_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCAreaMsg::CtiCCAreaMsg(CtiCCArea_vec& areas, ULONG bitMask) : CtiCCMessage("CCAreas"), _ccAreas(NULL), _msgInfoBitMask(bitMask)
{
    _ccAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_EXTENDED )  
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCAreaMsg has "<< areas.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )  
    {
        for (int h=0;h < areas.size(); h++) 
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Area: "<<((CtiCCArea*)areas[h])->getPAOName()<< endl;
            }
        }
    }
    for(int i=0;i<areas.size();i++)
    {
        _ccAreas->push_back(((CtiCCArea*)areas.at(i))->replicate());
    }
}

CtiCCAreaMsg::CtiCCAreaMsg(const CtiCCAreaMsg& areaMsg) : CtiCCMessage("CCAreas"), _ccAreas(NULL), _msgInfoBitMask(0)
{
    operator=(areaMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCAreaMsg::~CtiCCAreaMsg()
{
    if( _ccAreas != NULL &&
        _ccAreas->size() > 0 )
    {
        delete_vector(_ccAreas);
        _ccAreas->clear();
        delete _ccAreas;
    }
}

/*---------------------------------------------------------------------------
    replicateMessage
---------------------------------------------------------------------------*/
CtiMessage* CtiCCAreaMsg::replicateMessage() const
{
    return new CtiCCAreaMsg(*this);
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCAreaMsg& CtiCCAreaMsg::operator=(const CtiCCAreaMsg& right)
{
    if( this != &right )
    {
        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _ccAreas != NULL &&
            _ccAreas->size() > 0 )
        {
            delete_vector(_ccAreas);
            _ccAreas->clear();
            delete _ccAreas;
        }
		if ( _ccAreas == NULL )
			_ccAreas = new CtiCCArea_vec;
        for(int i=0;i<(right.getCCAreas())->size();i++)
        {
            _ccAreas->push_back(((CtiCCArea*)(*right.getCCAreas()).at(i))->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restoreGuts
    
    Restores the state of self fromt he given RWvistream
---------------------------------------------------------------------------*/
void CtiCCAreaMsg::restoreGuts(RWvistream& strm)
{
    CtiCCMessage::restoreGuts(strm);
	strm >> _msgInfoBitMask
         >> _ccAreas;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCAreaMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    strm << _msgInfoBitMask
         << _ccAreas;
}

// Static Members
ULONG CtiCCAreaMsg::AllAreasSent = 0x00000001;
ULONG CtiCCAreaMsg::AreaDeleted   = 0x00000002;
ULONG CtiCCAreaMsg::AreaAdded     = 0x00000004;


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
    if( _CC_DEBUG & CC_DEBUG_EXTENDED )  
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
                dout << CtiTime() << " - Sub: "<<((CtiCCSubstationBus*)buses[h])->getPAOName()<<" "<<((CtiCCSubstationBus*)buses[h])->getCurrentVarLoadPointValue()<<" "<<((CtiCCSubstationBus*)buses[h])->getEstimatedVarLoadPointValue() <<" "<<
                    ((CtiCCSubstationBus*)buses[h])->getPeakLead()<<" "<<((CtiCCSubstationBus*)buses[h])->getPeakLag()<< endl;
            }
            CtiFeeder_vec& feeds =   ((CtiCCSubstationBus*)buses[h])->getCCFeeders();
            for (int hh = 0; hh < feeds.size(); hh++) 
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " -    Feed: "<<((CtiCCFeeder*)feeds[hh])->getPAOName()<<" "<<((CtiCCFeeder*)feeds[hh])->getCurrentVarLoadPointValue()<<" "<<((CtiCCFeeder*)feeds[hh])->getEstimatedVarLoadPointValue() <<" " <<
                        ((CtiCCFeeder*)feeds[hh])->getPeakLead()<<" "<<((CtiCCFeeder*)feeds[hh])->getPeakLag()<< endl;
                }
            }
        }
    }
    for(int i=0;i<buses.size();i++)
    {
        _ccSubstationBuses->push_back(((CtiCCSubstationBus*)buses.at(i))->replicate());
    }
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
        delete_vector(_ccSubstationBuses);
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
        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _ccSubstationBuses != NULL &&
            _ccSubstationBuses->size() > 0 )
        {
            delete_vector(_ccSubstationBuses);
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
            delete_vector(_ccCapBankStates);
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
        if( _ccCapBankStates != NULL &&
            _ccCapBankStates->size() > 0 )
        {
            delete_vector(_ccCapBankStates);
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


/*===========================================================================
    CtiCCGeoAreasMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCGeoAreasMsg, CTICCGEOAREAS_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCArea_vec& ccGeoAreas) : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL)
{
    _ccGeoAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_EXTENDED )  
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
                dout << CtiTime() << " - Area: "<<((CtiCCArea*)ccGeoAreas[h])->getPAOName()<< endl;
            }
        }
    }

    for(int i=0;i<ccGeoAreas.size();i++)
    {
        _ccGeoAreas->push_back(((CtiCCArea*)ccGeoAreas.at(i))->replicate());
    }
}


CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreasMsg) : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL)
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
            delete_vector(_ccGeoAreas);
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
        if( _ccGeoAreas != NULL &&
            _ccGeoAreas->size() > 0 )
        {
            delete_vector(_ccGeoAreas);
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
	strm >> _ccGeoAreas;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCGeoAreasMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
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
    if( _CC_DEBUG & CC_DEBUG_EXTENDED )  
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
                dout << CtiTime() << " - Area: "<<((CtiCCSpecial*)ccSpecialAreas[h])->getPAOName()<< endl;
            }
        }
    }

    for(int i=0;i<ccSpecialAreas.size();i++)
    {
        _ccSpecialAreas->push_back((CtiCCSpecial*)(ccSpecialAreas.at(i))->replicate());
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
            delete_vector(_ccSpecialAreas);
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
        if( _ccSpecialAreas != NULL &&
            _ccSpecialAreas->size() > 0 )
        {
            delete_vector(_ccSpecialAreas);
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
    strm << _ccSpecialAreas;
}



/*===========================================================================
    CtiCCGeoAreasMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSubstationsMsg, CTICCSUBSTATION_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCSubstationsMsg::CtiCCSubstationsMsg(CtiCCSubstation_vec& ccSubstations) : CtiCCMessage("CCSubstations"), _ccSubstations(NULL)
{
    _ccSubstations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_EXTENDED )  
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
                dout << CtiTime() << " - Substation: "<<station->getPAOName()<< endl;
            }
            std::list <LONG>::const_iterator iterBus = station->getCCSubIds()->begin();
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
            delete_vector(_ccSubstations);
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
        if( _ccSubstations != NULL &&
            _ccSubstations->size() > 0 )
        {
            delete_vector(_ccSubstations);
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
	strm >> _ccSubstations;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSubstationsMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
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
        _scheduleId    = right._scheduleId;
    }

    return *this;
}



