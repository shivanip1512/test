#pragma once

#include "message.h"

class IM_EX_MSG CtiCommandMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiCommandMsg )

public:
    int                 iOperation;
    std::string         iOpString;
    std::vector< int >  iOpArgList;

public:
    typedef  CtiMessage           Inherited;
    typedef  std::vector< int >   OpArgList;


    CtiCommandMsg(int Op = NoOp, int Pri = 7);
    CtiCommandMsg(const CtiCommandMsg &aRef, int Pri = 7);

    INT               getOperation() const;

    CtiCommandMsg&    operator=(const CtiCommandMsg& aRef);

    OpArgList    getOpArgList() const;
    OpArgList&   getOpArgList();
    std::string  getOpString() const;

    void setOpArgList(const OpArgList &aRef);
    void setOpArgList(OpArgList&& aRef);
    void setOpString(const std::string &aRef);
    void setOperation(const INT &aInt);

    void insert(int i);

    CtiMessage* replicateMessage() const override;

    virtual std::string toString() const override;

    // Defines OpArg[1] for the Operation "UpdateFailed"
    enum Operation
    {
        OP_DEVICEID = 0,
        OP_POINTID  = 1
    };

    // Supported command operations.
    // Real codes to be sent from client to server must be divisible by 10.
    enum
    {
        NoOp = 0,                        // Does a LoopClient command
        Shutdown = 10,                   // Bye bye to VanGogh
        ClientAppShutdown = 20,          // Client App has gone away
        LoopClient = 40,                 // Sends message back to the client
        TraceRoute = 50,                 // Prints a blurb out in each place it is encountered.
        AreYouThere = 60,                // Used to find out if a client is still there. Receipt causes a response.
        AcknowledgeAlarm = 70,           //
        ClearAlarm = 80,                 //
        TokenGrant = 90,                 // Server is providing client with a security pass key.
        ReregistrationRequest = 100,     // Client must re-register with server application.
        UpdateFailed = 110,              // scan completed with an error, vector contains (token, idtype, dev/pointid, scantype, failcode) This causes Quality to go nonUpdated on all affected points!
        ControlRequest = 120,            // Vector contains token, deviceid (0 if not available), pointid/controloffset, raw state number, (optional) TRUE if cmd is to use control offset.
        Ablement = 130,                  // Vector contains token, idtype (0 = Device, 1 = Point), id, ablement bit (0 = dis, 1 = enable).
        CommStatus = 140,                // Vector contains token, deviceid, status (communication result in porter, 0 = NORMAL).
        AlternateScanRate = 150,         // Vector contains token, deviceid, seconds since midnight start (may be negative - use receipt time for start), duration in seconds (may be zero)
        ControlAblement = 160,           // Vector contains token, idtype (0 = Device, 1 = Point), id, ablement bit (0 = dis, 1 = enable).
        PointTagAdjust = 170,            // Vector contains token, pointid, tag(s) to set, tag(s) to reset.
        PorterConsoleInput = 180,        // Vector contains token, operation (same as keyboard character. see PorterConsoleInput:porter.cpp)
        ResetControlHours = 190,         // By default resets the Seasonal control history, could add to the op arg list to allow for resets of other control histories; Vector contains token
        PointDataRequest = 200,          // Vector of point ids to be reported if known.
        AlarmCategoryRequest = 210,      // Vector of alarm categories to return to the requestor.
        AnalogOutput = 220,              // Vector contains pointid and value.
        ControlStatusVerification,       // Internal Message used by Dispatch, value does not matter.
        InitiateScan = 250,               //Vector contains token, paoid, ...
        PointDataDebug = 260,
    };
};

