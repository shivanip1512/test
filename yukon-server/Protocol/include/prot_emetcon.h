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
* REVISION     :  $Revision: 1.48 $
* DATE         :  $Date: 2008/03/14 23:33:55 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_EMETCON_H__
#define __PROT_EMETCON_H__
#pragma warning( disable : 4786)


#include <utility>

#include "ctidbgmem.h" // defines CTIDBG_new
#include "dsm2.h"
#include "cmdparse.h"


namespace Cti       {
namespace Protocol  {

class IM_EX_PROT Emetcon  //  note that we do NOT inherit from the Protocol::Interface class - Emetcon doesn't fit the generate/decode paradigm
{
private:

protected:

    bool _double;                    // Double the sent messages (protocol operation, sends twice)

public:

    Emetcon();
    Emetcon(const Emetcon& aRef);
    virtual ~Emetcon();

    Emetcon& operator=(const Emetcon& aRef);

    /*-------------------------------------------------------------------------*
     * This method MUST be called prior to any command building method.
     *-------------------------------------------------------------------------*/
    Emetcon& setDouble(bool dbl);
    bool     getDouble() const;

    static unsigned determineDWordCount(unsigned length);
    static int calculateControlInterval(int interval);

    static void buildAWordMessage(OUTMESS *&out_result);
    static void buildBWordMessage(OUTMESS *&out_result, bool double_message = false);

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
        GetValue_KWH,
        GetValue_FrozenKWH,
        GetValue_Demand,
        GetValue_PeakDemand,
        GetValue_FrozenPeakDemand,
        GetValue_Voltage,
        GetValue_FrozenVoltage,
        GetValue_IEDKwh,
        GetValue_IEDKvarh,
        GetValue_IEDKvah,
        GetValue_IEDDemand,
        GetValue_IED,
        GetValue_LoadProfile,
        GetValue_LoadProfilePeakReport,
        GetValue_Outage,
        GetValue_TOU,
        GetValue_FreezeCounter,
        GetValue_DailyRead,

        // PutValue Commands
        PutValue_IEDReset,
        PutValue_ResetPFCount,
        PutValue_KYZ,  //  KYZ resets are implemented as a putvalue of 0
        PutValue_KYZ2,
        PutValue_KYZ3,
        PutValue_TOUReset,

        // GetStatus Commands
        GetStatus_Default,
        GetStatus_General,
        GetStatus_Disconnect,
        GetStatus_IEDLink,
        GetStatus_IEDDNP,
        GetStatus_LoadProfile,
        GetStatus_Powerfail,
        GetStatus_Internal,
        GetStatus_External,
        GetStatus_Freeze,

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
        GetConfig_IEDPassword,
        GetConfig_IEDTime,
        GetConfig_IEDDNP,
        GetConfig_Role,  //  only for repeaters
        GetConfig_Raw,
        GetConfig_Intervals,
        GetConfig_Thresholds,
        GetConfig_ChannelSetup,
        GetConfig_LoadProfileInterval,
        GetConfig_LongLoadProfile,
        GetConfig_DemandInterval,
        GetConfig_Options,
        GetConfig_GroupAddress,
        GetConfig_UniqueAddress,
        GetConfig_Disconnect,
        GetConfig_TOU,
        GetConfig_Holiday,
        GetConfig_CentronParameters,  //  not ideal - hopefully we can manage the InMessage->Sequence better for very specialized
                                      //    commands like this one, i don't like this being a big mess of non-general commands

        // PutConfig commands
        PutConfig_Install,
        PutConfig_GroupAddressInhibit,
        PutConfig_GroupAddressEnable,
        PutConfig_GroupAddress_GoldSilver,
        PutConfig_GroupAddress_Bronze,
        PutConfig_GroupAddress_Lead,
        PutConfig_UniqueAddress,
        PutConfig_IEDScan,
        PutConfig_IEDClass,
        PutConfig_Role,   //  only for repeaters
        PutConfig_Raw,
        PutConfig_TSync,
        PutConfig_LoadProfileInterval,
        PutConfig_DemandInterval,
        PutConfig_Intervals,
        PutConfig_ChannelSetup,
        PutConfig_LoadProfileInterest,
        PutConfig_LoadProfileReportPeriod,
        PutConfig_LongLoadProfile,
        PutConfig_DailyReadInterest,
        PutConfig_Multiplier,
        PutConfig_Multiplier2,
        PutConfig_Multiplier3,
        PutConfig_MinMax,
        PutConfig_OnOffPeak,
        PutConfig_Disconnect,
        PutConfig_Addressing,
        PutConfig_VThreshold,
        PutConfig_DST,
        PutConfig_Holiday,
        PutConfig_Options,
        PutConfig_Outage,
        PutConfig_OutageThreshold,
        PutConfig_TimeAdjustTolerance,
        PutConfig_TimeZoneOffset,
        PutConfig_TOU,  //  this may need to be removed in light of the new config install commands
        PutConfig_TOUEnable,
        PutConfig_TOUDisable,

        PutConfig_ARMC,
        PutConfig_ARML,
        PutConfig_ARMS,

        // Control Commands
        Control_Shed,     //  for MCT Group Addressing
        Control_Restore,  //
        Control_Connect,
        Control_Disconnect,
        Control_Latch,

        Command_Loop,

        DLCCmd_LAST    // PLACEHOLDER!!!
    };
};


};
};


#endif // #ifndef __PROT_EMETCON_H__
