/*-----------------------------------------------------------------------------
    Filename:  ccmessage.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Source file for message classes.

    Initial Date:  9/04/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#include "ccmessage.h"
#include "ccid.h"

#include <rw/collstr.h>
#include <time.h>

/*===========================================================================
    CtiCCMessage
    
    Base class for all Cap Control messages
===========================================================================*/

/*---------------------------------------------------------------------------
    Message
    
    Returns text describing the message
---------------------------------------------------------------------------*/
const RWCString& CtiCCMessage::getMessage() const
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
CtiCCMessage::CtiCCMessage(const RWCString& message) :  _message(message)
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
CtiCCCommand::CtiCCCommand(UINT command) : 
    CtiCCMessage( RWCString("" + command) ),
    _command(command)
{
}
    
CtiCCCommand::CtiCCCommand(UINT command, ULONG id) :
    CtiCCMessage( RWCString("" + command) ),
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
UINT CtiCCCommand::getCommand() const
{
    return _command;
}

/*---------------------------------------------------------------------------
    Id
    
    Returns the id of the object that is associated with command.
---------------------------------------------------------------------------*/
ULONG CtiCCCommand::getId() const
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


/*===========================================================================
    CtiCCSubstationBusMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCSubstationBusMsg, CTICCSUBSTATIONBUS_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(RWOrdered& buses) : CtiCCMessage("CCSubstationBuses")
{
    _ccSubstationBuses.clearAndDestroy();
    for(UINT i=0;i<buses.entries();i++)
    {
        _ccSubstationBuses.insert(((CtiCCSubstationBus*)buses[i])->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusMsg) : CtiCCMessage("CCSubstationBuses")
{
    operator=(substationBusMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstationBusMsg::~CtiCCSubstationBusMsg()
{
    _ccSubstationBuses.clearAndDestroy();
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
        _ccSubstationBuses.clearAndDestroy();
        for(UINT i=0;i<right._ccSubstationBuses.entries();i++)
        {
            _ccSubstationBuses.insert(((CtiCCSubstationBus*)right._ccSubstationBuses[i])->replicate());
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
	strm >> _ccSubstationBuses;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSubstationBusMsg::saveGuts(RWvostream& strm) const
{
    CtiCCMessage::saveGuts(strm);
    strm << _ccSubstationBuses;
}


/*===========================================================================
    CtiCCCapBankStatesMsg
===========================================================================*/

RWDEFINE_COLLECTABLE( CtiCCCapBankStatesMsg, CTICCCAPBANKSTATES_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg::CtiCCCapBankStatesMsg(RWOrdered* ccCapBankStates) : CtiCCMessage("CCCapBankStates")
{
    _ccCapBankStates.clearAndDestroy();
    for(UINT i=0;i<ccCapBankStates->entries();i++)
    {
        _ccCapBankStates.insert(((CtiCCState*)((*ccCapBankStates)[i]))->replicate());
    }
}

CtiCCCapBankStatesMsg::CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg) : CtiCCMessage("CCCapBankStates")
{
    operator=(ccCapBankStatesMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg::~CtiCCCapBankStatesMsg()
{
    _ccCapBankStates.clearAndDestroy();
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
        _ccCapBankStates.clearAndDestroy();
        for(UINT i=0;i<right._ccCapBankStates.entries();i++)
        {
            _ccCapBankStates.insert(((CtiCCState*)right._ccCapBankStates[i])->replicate());
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
CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(RWOrdered* ccGeoAreas) : CtiCCMessage("CCGeoAreas")
{
    _ccGeoAreas.clearAndDestroy();
    for(UINT i=0;i<ccGeoAreas->entries();i++)
    {
        RWCollectableString* tempStringPtr = new RWCollectableString();
        RWCollectableString* tempStringPtr2 = (RWCollectableString*)((*ccGeoAreas)[i]);
        *tempStringPtr = *tempStringPtr2;
        _ccGeoAreas.insert(tempStringPtr);
    }
}

CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreasMsg) : CtiCCMessage("CCGeoAreas")
{
    operator=(ccGeoAreasMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCGeoAreasMsg::~CtiCCGeoAreasMsg()
{
    _ccGeoAreas.clearAndDestroy();
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
        _ccGeoAreas.clearAndDestroy();
        for(UINT i=0;i<right._ccGeoAreas.entries();i++)
        {
            RWCollectableString* tempStringPtr = new RWCollectableString();
            RWCollectableString* tempStringPtr2 = (RWCollectableString*)(right._ccGeoAreas[i]);
            *tempStringPtr = *tempStringPtr2;
            _ccGeoAreas.insert(tempStringPtr);
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

