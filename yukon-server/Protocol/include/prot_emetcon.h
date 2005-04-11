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
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2005/04/11 21:06:19 $
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


#define PRIORITY_VALUE           10
#define PRIORITY_STATUS          9
#define PRIORITY_STATUS_FORCED   13


namespace Cti       {
namespace Protocol  {

class IM_EX_PROT Emetcon  //  note that we do NOT inherit from the Protocol::Interface class - Emetcon doesn't fit the generate/decode paradigm
{
private:

protected:

    INT  _sspec;                      // SSpec of the attached MCT
    INT  _deviceType;
    INT  _transmitterType;
    INT  _ied;                        // What ied type is attached?
    INT  _last;
    BOOL _double;                    // Double the sent messages (protocol operation, sends twice)

    RWTPtrSlist< OUTMESS >  _out;

public:

    Emetcon(INT devType, INT transmitterType);
    Emetcon(const Emetcon& aRef);
    virtual ~Emetcon();

    Emetcon& operator=(const Emetcon& aRef);

    INT entries() const;

    INT advanceAndPrime(const OUTMESS &OutTemp);
    INT primeOut(const OUTMESS &OutTemplate);

    /*-------------------------------------------------------------------------*
     * This method MUST be called prior to any command building method.
     *-------------------------------------------------------------------------*/
    Emetcon& setTransmitterType(INT type);
    INT      getTransmitterType() const;

    Emetcon& setDeviceType(INT type);
    INT      getDeviceType() const;

    Emetcon& setSSpec(INT spec);
    INT      getSSpec() const;

    Emetcon& setIED(INT ied);
    INT      getIED() const;

    Emetcon& setDouble(BOOL dbl);
    BOOL     getDouble() const;

    INT parseRequest(CtiCommandParser  &parse, OUTMESS &aOut);
    INT assembleCommand(CtiCommandParser  &parse, OUTMESS &aOutTemplate);

    static INT determineDWordCount(INT Length);

    OUTMESS  getOutMessage(INT pos) const;
    OUTMESS& getOutMessage(INT pos);
    OUTMESS* popOutMessage();

    INT buildMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate);
    INT buildAWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate);
    INT buildBWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate);

    int getControlInterval(CtiCommandParser &parse) const;

    enum IO_Bits
    {
        IO_Write          = 0,
        IO_Read           = 1,
        IO_Function_Write = 2,
        IO_Function_Read  = 3
    };

    enum CtiDLCCommand_t
    {
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
    };

#ifdef OLD_CODE
    bool dlcGeneralFunctionAddress(BSTRUCT &BSt) const;
    bool dlcIntegrityFunctionAddress(BSTRUCT &BSt) const;
    bool dlcAccumulatorFunctionAddress(BSTRUCT &BSt) const;
#endif

};


};
};


#endif // #ifndef __PROT_EMETCON_H__
