/*-----------------------------------------------------------------------------*
*
* File:   prot_emetcon
*
* Class:  CtiProtocolEmetcon
* Date:   1/29/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_emetcon.h-arc  $
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2005/02/25 21:42:40 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_EMETCON_H__
#define __PROT_EMETCON_H__
#pragma warning( disable : 4786)


#include <utility>
using namespace std;

#include <rw/tpslist.h>

#include "ctidbgmem.h" // defines CTIDBG_new
#include "dsm2.h"
#include "cmdparse.h"

#define IO_WRITE        0
#define IO_READ         1
#define IO_FCT_WRITE    2
#define IO_FCT_READ     3
#define IO_FCT_MASK     2


#define PRIORITY_VALUE           10
#define PRIORITY_STATUS          9
#define PRIORITY_STATUS_FORCED   13
#define RETRY_SCAN               0
#define RETRY_STATUS             0
#define POINT_NOT_QUEUED         0
#define POINT_QUEUED             1
#define REQUEUE_POINT            2
#define FORCED_DLC_SCAN          3

//  these were used in scan_dlc, only here for reference
//  ----

#define REQUEUE_DEVICE           100
#define FAILED_NO_MORE           200
#define INVALID_REMOTE           1

//  ----


#define DCT_ANALOG_ADDRESS       0x67
#define DCT_ANALOG_LENGTH        8

#define MCTALPHA_FUNCTION_READ   0xAB         /* Status Byte and Demand from Alpha Meter */
#define MCTLGS4_FUNCTION_READ    0xAB         /* Status Byte and Demand from LG S4 Meter */
#define MCTLGS4_FUNCTION_LENGTH  13           /* Status Byte and Demand from LG S4 Meter */

#define GENERAL_ACCUM_ADDRESS    0x86
#define GENERAL_ACCUM_LENGTH     6
#define GENERAL_DEMAND_ADDRESS   0x56
#define GENERAL_DEMAND_LENGTH    3
#define GENERAL_STATUS_ADDRESS   0x3f
#define GENERAL_STATUS_LENGTH    7


#define STATUS1_BIT_MCT3XX    0x80
#define STATUS2_BIT_MCT3XX    0x40
#define STATUS3_BIT_MCT3XX    0x20
#define STATUS4_BIT_MCT3XX    0x10
#define STATUS5_BIT_MCT3XX    0x01
#define STATUS6_BIT_MCT3XX    0x02
#define STATUS7_BIT_MCT3XX    0x08
#define STATUS8_BIT_MCT3XX    0x04
#define STATUS1_BIT           0x40
#define STATUS2_BIT           0x80
#define STATUS3_BIT           0x02
#define STATUS4_BIT           0x04
#define OVERFLOW_BIT          0x01
#define L_PWRFAIL_BIT         0x02
#define S_PWRFAIL_BIT         0x04
#define OVERFLOW310_BIT       0x04
#define L_PWRFAIL310_BIT      0x08
#define S_PWRFAIL310_BIT      0x10
#define TAMPER_BIT            0x08

#define DEV_CODE_OVERFLOW     16382
#define DEV_CODE_FILLER       16383
#define UNKNOWN_INVALID       100

/* filter for PECO max Amp pulse rate */
#define PECO_MAX_AMP_PULSES   16800
#define RTU_SCAN_RATE         300

/*
 *  Indicators used in decodeStati calls.. (this is the "which")
 */
#define MCT_STATUS_RELAYA_STATUS    0        // True status. 0 if relay is energized
#define MCT_STATUS_RELAYB_STATUS    1        // True status. 0 if relay is energized
#define MCT_STATUS_SELFTEST         2        // True status. (latched & alarm)
#define MCT_STATUS_RESET            3        // True status. (latched)
#define MCT_STATUS_LONGPF           4        // True status. (latched)
#define MCT_STATUS_OVERFLOW         5        // True status. (latched & alarm)
#define MCT_STATUS_ADDRESSING       6        // 4 state status. 0 = all address, 1 = unique and fct, 2 = fct, 3 illegal.




class IM_EX_PROT CtiProtocolEmetcon
{
public:

    typedef enum {

        DLCCmd_Invalid = 0,

        // Scan Commands
        Scan_General,
        Scan_Integrity,
        Scan_Accum,
        Scan_LoadProfile,

        // GetValue Commands
        GetValue_PFCount,
        GetValue_Default,
        GetValue_FrozenKWH,
        GetValue_Demand,
        GetValue_PeakDemand,
        GetValue_FrozenPeakDemand,
        GetValue_Voltage,
        GetValue_IEDKwh,
        GetValue_IEDKvarh,
        GetValue_IEDKvah,
        GetValue_IEDDemand,
        GetValue_IED,
        GetValue_LoadProfile,
        GetValue_LoadProfilePeakReport,
        GetValue_Outage,

        // PutValue Commands
        PutValue_IEDReset,
        PutValue_ResetPFCount,
        PutValue_KYZ,  //  KYZ resets are implemented as a putvalue of 0
        PutValue_KYZ2,
        PutValue_KYZ3,

        // GetStatus Commands
        GetStatus_Default,
        GetStatus_General,
        GetStatus_Disconnect,
        GetStatus_IEDLink,
        GetStatus_LoadProfile,
        GetStatus_Powerfail,
        GetStatus_Internal,
        GetStatus_External,

        // PutStatus Commands
        PutStatus_Reset,
        PutStatus_FreezeOne,
        PutStatus_FreezeTwo,
        PutStatus_FreezeVoltageOne,
        PutStatus_FreezeVoltageTwo,
        PutStatus_ResetOverride,
        PutStatus_PeakOn,
        PutStatus_PeakOff,

        // GetConfig commands
        GetConfig_Default,
        GetConfig_Model,
        GetConfig_Time,
        GetConfig_TSync,
        GetConfig_Multiplier,
        GetConfig_Multiplier2,
        GetConfig_Multiplier3,
        GetConfig_Multiplier4,
        GetConfig_IEDScan,
        GetConfig_IEDPwd,
        GetConfig_IEDTime,
        GetConfig_Role,  //  only for repeaters
        GetConfig_Raw,
        GetConfig_Intervals,
        GetConfig_LoadProfileInterval,
        GetConfig_DemandInterval,
        GetConfig_Options,
        GetConfig_GroupAddress,
        GetConfig_Disconnect,

        // PutConfig commands
        PutConfig_Install,
        PutConfig_GroupAddrInhibit,
        PutConfig_GroupAddrEnable,
        PutConfig_GroupAddr_GoldSilver,
        PutConfig_GroupAddr_Bronze,
        PutConfig_GroupAddr_Lead,
        PutConfig_UniqueAddr,
        PutConfig_IEDScan,
        PutConfig_IEDClass,
        PutConfig_Role,   //  only for repeaters
        PutConfig_Raw,
        PutConfig_TSync,
        PutConfig_LoadProfileInterval,
        PutConfig_DemandInterval,
        PutConfig_Intervals,
        PutConfig_LoadProfileInterest,
        PutConfig_LoadProfileReportPeriod,
        PutConfig_Multiplier,
        PutConfig_Multiplier2,
        PutConfig_Multiplier3,
        PutConfig_MinMax,
        PutConfig_OnOffPeak,
        PutConfig_Disconnect,

        PutConfig_ARMC,
        PutConfig_ARML,  //  these used to be "Control_ARML," etc - I wanted to make them consistent...
        PutConfig_ARMS,

        // Control Commands
        Control_Shed,     //  for MCT Group Addressing
        Control_Restore,  //
        Control_Close,
        Control_Open,
        Control_Conn,
        Control_Disc,

        Command_Loop,

        DLCCmd_LAST                // PLACEHOLDER!!!

    } CtiDLCCommand_t;


protected:

    INT _sspec;                      // SSpec of the attached MCT
    INT _deviceType;
    INT _transmitterType;
    INT _ied;                        // What ied type is attached?
    INT _last;
    BOOL _double;                    // Double the sent messages (protocol operation, sends twice)

    RWTPtrSlist< OUTMESS >  _out;

private:

public:

    CtiProtocolEmetcon(INT devType, INT transmitterType) :
        _ied(0),
        _last(0),
        _deviceType(devType),
        _transmitterType(transmitterType),
        _double(FALSE)
    { }

    CtiProtocolEmetcon(const CtiProtocolEmetcon& aRef)  :
        _ied(0),
        _last(0),
        _double(FALSE)
    {
        _out.clearAndDestroy();
        *this = aRef;
    }

    virtual ~CtiProtocolEmetcon()
    {
        _out.clearAndDestroy();
    }

    INT entries() const
    {
        return _out.entries();
    }

    CtiProtocolEmetcon& operator=(const CtiProtocolEmetcon& aRef)
    {
        if(this != &aRef)
        {
            _out.clearAndDestroy();

            for( int i = 0; i < aRef.entries(); i++ )
            {
                _ied = getIED();

                OUTMESS *Out = CTIDBG_new OUTMESS(aRef.getOutMessage(i));
                if(Out != NULL)
                {
                    _out.insert( Out );
                }
            }

            _sspec = aRef.getSSpec();
            _transmitterType  = aRef.getTransmitterType();
        }
        return *this;
    }

    INT advanceAndPrime(const OUTMESS &OutTemp);
    INT primeOut(const OUTMESS &OutTemplate);

    /*-------------------------------------------------------------------------*
     * This method MUST be called prior to any command building method.
     *-------------------------------------------------------------------------*/
    CtiProtocolEmetcon& setTransmitterType(INT type);
    INT getTransmitterType() const;

    CtiProtocolEmetcon& setDeviceType(INT type);
    INT getDeviceType() const;

    CtiProtocolEmetcon& setSSpec(INT spec);
    INT getSSpec() const;

    CtiProtocolEmetcon& setIED(INT ied);
    INT getIED() const;

    CtiProtocolEmetcon& setDouble(BOOL dbl);
    BOOL getDouble() const;

    INT parseRequest(CtiCommandParser  &parse, OUTMESS &aOut);
    INT assembleCommand(CtiCommandParser  &parse, OUTMESS &aOutTemplate);

    static INT determineDWordCount(INT Length);

    OUTMESS getOutMessage(INT pos) const;
    OUTMESS& getOutMessage(INT pos);
    OUTMESS* popOutMessage();

    INT buildMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate);
    INT buildAWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate);
    INT buildBWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate);

    int getControlInterval(CtiCommandParser &parse) const;


#ifdef OLD_CODE
    bool dlcGeneralFunctionAddress(BSTRUCT &BSt) const;
    bool dlcIntegrityFunctionAddress(BSTRUCT &BSt) const;
    bool dlcAccumulatorFunctionAddress(BSTRUCT &BSt) const;
#endif

};
#endif // #ifndef __PROT_EMETCON_H__
