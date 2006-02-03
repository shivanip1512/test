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
#include "rwutil.h"
#include "utility.h"

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
LONG CtiCCCapBankMoveMsg::getCapSwitchingOrder() const
{
    return _capswitchingorder;
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
         >> _capswitchingorder;
 
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
         << _capswitchingorder;

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
    CtiCCSubstationBusMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSubstationBusMsg, CTICCSUBSTATIONBUS_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, ULONG bitMask) : CtiCCMessage("CCSubstationBuses"), _ccSubstationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
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
    /*CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << CtiTime() << " - Do not call me!!! " << __FILE__ << __LINE__ << endl;
    return NULL;*/
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
CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCGeoArea_vec& ccGeoAreas) : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL)
{
    _ccGeoAreas = new CtiCCGeoArea_vec;
    for(int i=0;i<ccGeoAreas.size();i++)
    {
        RWCollectableString* tempStringPtr = new RWCollectableString();
        RWCollectableString* tempStringPtr2 = (RWCollectableString*)(ccGeoAreas.at(i));
        *tempStringPtr = *tempStringPtr2;
        _ccGeoAreas->push_back(tempStringPtr);
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
    /*CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << CtiTime() << " - Do not call me!!! " << __FILE__ << __LINE__ << endl;
    return NULL;*/
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
			_ccGeoAreas = new CtiCCGeoArea_vec;
        //for(int i=0;i<(right.getCCGeoAreas())->size();i++)
		for(CtiCCGeoArea_vec::iterator itr = right.getCCGeoAreas()->begin(); 
		    itr != right.getCCGeoAreas()->end(); 
			itr++ )
        {
            RWCollectableString* tempStringPtr = new RWCollectableString();
            RWCollectableString* tempStringPtr2 = *itr;
            *tempStringPtr = *tempStringPtr2;
            _ccGeoAreas->push_back(tempStringPtr);
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



