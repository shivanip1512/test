#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_remote.h"
#include "tbl_dv_ied.h"
#include "logger.h"
#include "xfer.h"

class CtiPort;
class CtiDeviceBase;
static const std::string TAP_HANDSHAKE_CPARM = "TAP_HANDSHAKE_FAIL_COUNT";

class CtiDeviceIED : public CtiDeviceRemote
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceIED(const CtiDeviceIED&);
    CtiDeviceIED& operator=(const CtiDeviceIED&);

public:
      // possible states in our big list of state machines
    enum CtiMeterMachineStates_t
    {
        StateInvalid = 0,
        StateHandshakeSendStart = 1,
        StateHandshakeInitialize,
        StateHandshakeDecodeStart,
        StateHandshakeSendAttn,
        StateHandshakeDecodeAttn,
        StateHandshakeSendIdentify,
        StateHandshakeSendIdentify_2,
        StateHandshakeDecodeIdentify,
        StateHandshakeDecodeIdentify_2,
        StateHandshakeSendSecurity,        // 10
        StateHandshakeDecodeSecurity,
        StateHandshakeComplete,
        StateHandshakeAbort,
        StateHandshakeSendTerminate,
        StateHandshakeDecodeTerminate,
        StateScanValueSet1FirstScan,
        StateScanValueSet1,
        StateScanDecode1,
        StateScanValueSet2FirstScan,
        StateScanValueSet2,                // 20
        StateScanDecode2,
        StateScanValueSet3FirstScan,
        StateScanValueSet3,
        StateScanDecode3,
        StateScanValueSet4FirstScan,
        StateScanValueSet4,
        StateScanDecode4,
        StateScanValueSet5FirstScan,
        StateScanValueSet5,
        StateScanDecode5,                 // 30
        StateScanValueSet6FirstScan,
        StateScanValueSet6,
        StateScanDecode6,
        StateScanValueSet7FirstScan,
        StateScanValueSet7,
        StateScanDecode7,
        StateScanTimeFirstScan,
        StateScanTime,
        StateScanDecodeTime,
        StateScanTestFirstScan,            // 40
        StateScanTest,
        StateScanComplete,
        StateScanAbort,
        StateScanSendTerminate,
        StateScanDecodeTerminate,
        StateScanInitialClassZeroComplete,
        StateScanInitialClassZeroAbort,
        StateScanReadClassComplete,
        StateScanDecodeInitialClassZeroComplete,
        StateScanDecodeInitialClassZeroAbort,   //50
        StateScanDecodeReadClassComplete,
        StateScanReadClass2,
        StateScanDecodeReadClass2,
        StateScanReturnLoadProfile,

        StateGenerate_1,
        StateGenerate_2,
        StateGenerate_3,
        StateGenerate_4,
        StateGenerate_5,
        StateGenerate_6,                            //60
        StateGenerate_7,
        StateGenerate_8,
        StateGenerate_9,
        StateGenerate_10,
        StateDecode_1,
        StateDecode_2,
        StateDecode_3,
        StateDecode_4,
        StateDecode_5,
        StateDecode_6,                           //70
        StateDecode_7,
        StateDecode_8,
        StateDecode_9,
        StateDecode_10,
        StateScanPageSentResponse,
        StateAbsorb,
        StateAbsorb_1,
        StateAbsorb_2,
        StateAbsorb_3,
        StateAbsorb_4,                           // 80
        StateAbsorb_5,
        StateAbsorb_6,
        StateAbsorb_7,
        StateAbsorb_8,
        StateAbsorb_9,
        StateAbsorb_10,
        StateRepeat,
        StateRepeat_1,
        StateRepeat_2,
        StateRepeat_3,                           // 90
        StateRepeat_4,
        StateRepeat_5,
        StateAbort,
        StateCompleteNoHUP,                                  // Scan is complete DO NOT HANGUP!
        StateComplete,

        StateScanValueSet8FirstScan,
        StateScanValueSet8,
        StateScanDecode8,
        StateScanCRCError,
        StateScanResendRequest,
    };


    enum CtiMeterCmdStates_t
    {
        CmdScanData,
        CmdLoadProfileData,
        CmdLoadProfileTransition,
        CmdSelectMeter,
        CmdSchlumbergerUploadAll,
        CmdSchlumbergerUploadData,
        CmdAlphaNoData,
        CmdAlphaWithData,
        CmdAlphaClassRead,
        CmdAlphaFullClassRead,
        CmdAlphaRetrieveClassData,
        CmdAlphaPartialRead
    };

protected:

    CtiTableDeviceIED   _ied;

private:

    typedef CtiDeviceRemote Inherited;

    // current state of the device
    CtiMeterMachineStates_t    _currentState;

    // where we are going next
    CtiMeterMachineStates_t    _previousState;

    // number of attempts to communicate to device
    INT                        _attemptsRemaining;
    INT                        _handshakesRemaining;

    //  current command
    CtiMeterCmdStates_t        _currentCommand;

public:

    CtiDeviceIED()  :
        _currentState(StateHandshakeInitialize),
        _previousState(StateHandshakeInitialize),
        _attemptsRemaining (7),  //schlumberger spec ??
        _currentCommand(CmdScanData),
        _handshakesRemaining(3)
    {}

    const CtiTableDeviceIED& getIED() const { return _ied; }

    virtual std::string getSQLCoreStatement() const
    {
        static const std::string sqlCore =
            "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
              "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, "
              "CS.portid, DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, "
              "DUS.linesettings, DUS.baudrate, IED.password, IED.slaveaddress "
            "FROM Device DV, DeviceIED IED, DeviceDirectCommSettings CS, YukonPAObject YP "
              "LEFT OUTER JOIN DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
              "WHERE YP.paobjectid = IED.deviceid AND YP.paobjectid = DV.deviceid AND "
              "YP.paobjectid = CS.deviceid";

        return sqlCore;
    }

    void DecodeDatabaseReader(Cti::RowReader &rdr) override
    {
        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CTILOG_DEBUG(dout, "Decoding DB reader");
        }

        _ied.DecodeDatabaseReader(getType(), rdr);
    }


    // functions for all IEDs


    /*
     *  A paired set which implements a state machine (before/do port work/after) in conjunction with
     *  the port's function out/inMess pair.
     */
    virtual YukonError_t generateCommandHandshake(CtiXfer &Transfer, CtiMessageList &traceList)                                { return ClientErrors::NoMethodForHandshake;}
    virtual YukonError_t decodeResponseHandshake (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)  { return ClientErrors::NoMethodForHandshake;}

    virtual YukonError_t generateCommandDisconnect(CtiXfer &Transfer, CtiMessageList &traceList)                                { return ClientErrors::NoMethodForHandshake;}
    virtual YukonError_t decodeResponseDisconnect (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)  { return ClientErrors::NoMethodForHandshake;}

    virtual YukonError_t generateCommand    (CtiXfer  &Transfer, CtiMessageList &traceList)                              { return ClientErrors::NoMethodForGenerateCmd;}
    virtual YukonError_t decodeResponse (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)               { return ClientErrors::NoMethodForDecodeResponse;}

    virtual INT allocateDataBins (OUTMESS *)                                         { return ClientErrors::Memory;}
    virtual INT freeDataBins ()                                                      { return ClientErrors::Memory;}
    virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived)      { return ClientErrors::NoMethodForDataCopy;}
    virtual INT copyLoadProfileData (BYTE *aInMessBuffer, ULONG &aBytesReceived)     { return ClientErrors::NoMethodForDataCopy;}


    CtiMeterMachineStates_t  getCurrentState () const
    {
        return _currentState;
    }
    CtiDeviceIED& setCurrentState (CtiMeterMachineStates_t aState)
    {
        _currentState = aState;
        return *this;
    }

    virtual CtiDeviceIED& setInitialState (const LONG oldid)
    {
        if( oldid > 0 )
        {
            if(isDebugLudicrous())
            {
                CTILOG_DEBUG(dout, "Port has indicated a connected device swap."<< getName() <<" has replaced DEVID "<< oldid <<" as the currently connected device");
            }

            setCurrentState(StateHandshakeComplete);
        }
        if( getLogOnNeeded() )
        {
            setCurrentState(StateHandshakeSendStart);
        }
        else                                                                 // Device is already online and init
        {
            setCurrentState(StateHandshakeComplete);
        }

        return *this;
    }

    CtiMeterMachineStates_t getPreviousState () const
    {
        return _previousState;
    }

    CtiDeviceIED& setPreviousState (CtiMeterMachineStates_t aState)
    {
        _previousState = aState;
        return *this;
    }

    INT getAttemptsRemaining () const
    {
        return _attemptsRemaining;
    }
    CtiDeviceIED& setAttemptsRemaining (INT aAttemptsRemaining)
    {
        _attemptsRemaining = aAttemptsRemaining;
        return *this;
    }

    CtiMeterCmdStates_t getCurrentCommand () const
    {
        return _currentCommand;
    }
    CtiDeviceIED& setCurrentCommand (CtiMeterCmdStates_t aCommand)
    {
        _currentCommand = aCommand;
        return *this;
    }

    bool isStandaloneMaster () const
    {
        bool retVal;

        if (_ied.getSlaveAddress() == -1)
            retVal = true;
        else
            retVal = false;

        return retVal;
    }

    bool isMaster () const
    {
        bool retVal;

        if (_ied.getSlaveAddress() <= 0)
            retVal = true;
        else
            retVal = false;

        return retVal;
    }

    bool isSlave () const
    {
        bool retVal;

        if (_ied.getSlaveAddress() > 0)
            retVal = true;
        else
            retVal = false;

        return retVal;
    }

    virtual std::string getPassword() const       { return getIED().getPassword(); }

    int getHandshakesRemaining() const { return _handshakesRemaining; }
    void resetHandshakesRemaining() { _handshakesRemaining = gConfigParms.getValueAsInt(TAP_HANDSHAKE_CPARM, 3); }
    void decreaseHandshakesRemaining() { _handshakesRemaining--; }

};
