/*-----------------------------------------------------------------------------
    Filename:  ccmessage.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for message classes.

    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
   
#ifndef CCMESSAGE_H
#define CCMESSAGE_H

#include <rw/cstring.h>
#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/rwtime.h>

#include "message.h"
#include "ccsubstationbus.h"
#include "ccstate.h"

class CtiCCMessage : public CtiMessage
{
//RWDECLARE_COLLECTABLE( CtiCCMessage )

public:
    CtiCCMessage() { };
    CtiCCMessage(const RWCString& message);

    virtual ~CtiCCMessage() { };

    const RWCString& getMessage() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

private:
    RWCString _message;
};

class CtiCCCommand : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCCommand )

public:

    enum 
    {
        ENABLE_SUBSTATION_BUS = 0,
        DISABLE_SUBSTATION_BUS,
        ENABLE_FEEDER,
        DISABLE_FEEDER,
        ENABLE_CAPBANK,
        DISABLE_CAPBANK,
        OPEN_CAPBANK,
        CLOSE_CAPBANK,
        CONFIRM_OPEN,
        CONFIRM_CLOSE,
        REQUEST_ALL_SUBSTATION_BUSES
    };

    CtiCCCommand() { }; //provided for polymorphic persitence only
    CtiCCCommand(UINT command);
    CtiCCCommand(UINT command, ULONG id);
    CtiCCCommand(const CtiCCCommand& commandMsg);
    
    virtual ~CtiCCCommand();

    UINT getCommand() const;
    ULONG getId() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCCommand& operator=(const CtiCCCommand& right);
private:
    
    UINT _command;
    ULONG _id;
};

class CtiCCSubstationBusMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCSubstationBusMsg )

public:
    CtiCCSubstationBusMsg() : CtiCCMessage("CCSubstationBuses"){};
    CtiCCSubstationBusMsg(RWOrdered& buses);
    CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusesMsg);

    virtual ~CtiCCSubstationBusMsg();

    const RWOrdered& getCCSubstationBuses() const     { return _ccSubstationBuses; }
    RWOrdered& getCCSubstationBuses()                 { return _ccSubstationBuses; }
    CtiCCSubstationBusMsg& setCCSubstationBuses(const RWOrdered& buses);
    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCSubstationBusMsg& operator=(const CtiCCSubstationBusMsg& right);
private:
    
    RWOrdered _ccSubstationBuses;
};

class CtiCCCapBankStatesMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCCapBankStatesMsg )

public:
    CtiCCCapBankStatesMsg() : CtiCCMessage("CCCapBankStates"){};
    CtiCCCapBankStatesMsg(RWOrdered* ccCapBankStates);
    CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg);

    virtual ~CtiCCCapBankStatesMsg();

    const RWOrdered& getCCCapBankStates() const     { return _ccCapBankStates; }
    RWOrdered& getCCCapBankStates()                 { return _ccCapBankStates; }
    CtiCCCapBankStatesMsg& setCCCapBankStates(const RWOrdered& ccCapBankStates);
    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCCapBankStatesMsg& operator=(const CtiCCCapBankStatesMsg& right);
private:
    
    RWOrdered _ccCapBankStates;
};

class CtiCCGeoAreasMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCGeoAreasMsg )

public:
    CtiCCGeoAreasMsg() : CtiCCMessage("CCGeoAreas"){};
    CtiCCGeoAreasMsg(RWOrdered* areaList);
    CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreas);

    virtual ~CtiCCGeoAreasMsg();

    const RWOrdered& getCCGeoAreas() const     { return _ccGeoAreas; }
    RWOrdered& getCCGeoAreas()                 { return _ccGeoAreas; }
    CtiCCGeoAreasMsg& setCCGeoAreas(const RWOrdered& ccGeoAreas);
    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCGeoAreasMsg& operator=(const CtiCCGeoAreasMsg& right);
private:
    
    RWOrdered _ccGeoAreas;
};

class CtiCCShutdown : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCShutdown )

public:
    CtiCCShutdown() : CtiCCMessage("Shutdown") { } ;
    
    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;
};

#endif

