/*-----------------------------------------------------------------------------*
*
* File:   prot_versacom
*
* Date:   6/26/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2004/05/21 16:04:11 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_VERSACOM_H__
#define __PROT_VERSACOM_H__
#pragma warning( disable : 4786)


#include <rw/tpslist.h>

#include "dsm2.h"
#include "dllbase.h"
#include "cmdparse.h"

#define MAX_VERSACOM_MESSAGE  40

#define VC_REPORT_LED_ENABLED          0x20
#define VC_STATUS_LED_ENABLED          0x40
#define VC_LOAD_LED_ENABLED            0x80

#define VC_PROPTERMINATE               0x01
#define VC_PROPINCREMENT               0x02
#define VC_PROPDISPLAY                 0x04

#define VC_SERVICE_T_OUT               0x01
#define VC_SERVICE_T_IN                0x02
#define VC_SERVICE_C_OUT               0x04
#define VC_SERVICE_C_IN                0x0a
#define VC_SERVICE_MASK                0x0f


#define VC_RESETR4COUNT                0x80
#define VC_RESETRTCCOUNT               0x40
#define VC_RESETLUFCOUNT               0x20
#define VC_RESETRELAY1COUNT            0x10

#define VC_RESETRELAY2COUNT            0x08
#define VC_RESETRELAY3COUNT            0x04
#define VC_RESETCOMMLOSSCOUNT          0x02
#define VC_RESETPROPAGATIONCOUNT       0x01

typedef enum
{
   VCT_NotUsed00 = 0,
   VCT_RippleFrequency,
   VCT_MessageBitTiming,
   VCT_DiscreteCommandOffsetR1,
   VCT_DiscreteCommandOffsetR2,
   VCT_DiscreteCommandOffsetR3,
   VCT_CyclingCommandOffsetR1,
   VCT_CyclingCommandOffsetR2,
   VCT_CyclingCommandOffsetR3,
   VCT_FastRelayControl,
   VCT_VersacomFlag,
   VCT_CommLossDetectTime,
   VCT_CommLossRCFlagR1,
   VCT_CommLossRCFlagR2,
   VCT_CommLossRCFlagR3,
   VCT_PropDisplayTime,

   VCT_CommandInitiatorTableCode0 = 0x10,
   VCT_CommandInitiatorTableCode1,
   VCT_CommandInitiatorTableCode2,
   VCT_CommandInitiatorTableCode3,
   VCT_CommandInitiatorTableCode4,
   VCT_CommandInitiatorTableCode5,
   VCT_CommandInitiatorTableCode6,
   VCT_CommandInitiatorTableCode7,
   VCT_CommandInitiatorTableCode8,
   VCT_CommandInitiatorTableCode9,
   VCT_CommandInitiatorTableCodeA,
   VCT_CommandInitiatorTableCodeB,
   VCT_CommandInitiatorTableCodeC,
   VCT_CommandInitiatorTableCodeD,
   VCT_CommandInitiatorTableCodeE,
   VCT_CommandInitiatorTableCodeF,

   VCT_ColdLoadPickupR1 = 0x20,
   VCT_ColdLoadPickupR2,
   VCT_ColdLoadPickupR3,
   VCT_CycleRepeatsR1,
   VCT_CycleRepeatsR2,
   VCT_CycleRepeatsR3,
   VCT_ScramTimeR1,
   VCT_ScramTimeR2,
   VCT_ScramTimeR3,
   VCT_PrimaryUtilityID,
   VCT_SectionAddress,
   VCT_ClassAddress,
   VCT_DivisionAddress,
   VCT_LUFEnable,
   VCT_LUFInResponse,
   VCT_LEDConfig,

   VCT_AuxUtilityID = 0x30,
   VCT_FMFrequency,
   VCT_SCAFrequencies,
   VCT_FMScanEnable,
   VCT_LUFOutResponse,
   VCT_RequiredAddressLevel,
   VCT_LCRMode,
   VCT_SerialNumberAddress,
   VCT_EmetconGoldAddress,
   VCT_EmetconSilverAddress,
   VCT_EmetconColdLoad

} VersacomConfigurationType_t;

class CtiRequestMsg;

#ifdef _DLL_PROT
   #define IM_EX_PROT   __declspec(dllexport)
#else
   #define IM_EX_PROT   __declspec(dllimport)
#endif


class IM_EX_PROT CtiProtocolVersacom
{
protected:

   INT      _transmitterType;
   INT      _addressMode;           // Bookkeeping info used to build a full message
   INT      _last;

   RWTPtrSlist< VSTRUCT >  _vst;

private:

   UINT VersacomControlDuration(UINT type, UINT controltime);
   UINT VersacomControlDurationEx(UINT type, UINT controltime);

   /*-------------------------------------------------------------------------*
    * This method MUST be called prior to any command building method.  It
    * establishes the addressing mode of the message and initializes the
    * addressing variables in the VSTRUCT based upon the arguments passed in.
    *
    * Group addressing is COMPLETELY ignored if Serial Number (Serial) is non
    * zero!
    *-------------------------------------------------------------------------*/
   INT adjustVersacomAddress(VSTRUCT &vTemp, ULONG Serial, UINT Uid, UINT Section, UINT Class, UINT Division);
   void removeLastVStruct();





   INT setNibble (INT iNibble, INT iValue);

   INT initVersacomMessage();
   INT assembleCommandToMessage();
   INT assembleAddressing();

public:

   CtiProtocolVersacom(INT tt) :
      _last(0),
      _transmitterType(tt),
      _addressMode(0)
   { }

   CtiProtocolVersacom(const CtiProtocolVersacom& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiProtocolVersacom()
   {
      _vst.clearAndDestroy();
   }

   INT   entries() const
   {
      return _vst.entries();
   }

   CtiProtocolVersacom& operator=(const CtiProtocolVersacom& aRef)
   {
      if(this != &aRef)
      {
         _vst.clearAndDestroy();

         for( int i = 0; i < aRef.entries(); i++ )
         {
            VSTRUCT *Vst = CTIDBG_new VSTRUCT;
            memcpy((void*)Vst, &aRef.getVStruct(i), sizeof(VSTRUCT));

            _vst.insert( Vst );
         }

         _transmitterType  = aRef.getTransmitterType();
         _addressMode      = aRef.getAddressMode();
      }
      return *this;
   }

   bool                    isConfig63Valid(LONG sn) const;
   bool                    isConfigFullAddressValid(LONG sn) const;
   INT                     getAddressMode() const     { return _addressMode; }

   INT                     primeAndAppend(const VSTRUCT &vTemp);

   /*-------------------------------------------------------------------------*
    * This method will seldom if ever be called by the user directly.  Its
    * inclusion in the public interface was debated.  The only time I expect this
    * to be a solution is if the user constructed a CtiProtocolVersacom with
    * a FULLY completed (and correct) VSTRUCT.  In order to generate a message
    * buffer this method is required.
    *-------------------------------------------------------------------------*/
   INT                     updateVersacomMessage();


   /*-------------------------------------------------------------------------*
    * These methods are likely of little or no use unless one wishes to get
    * himself into trouble.
    *-------------------------------------------------------------------------*/
   VSTRUCT                 getVStruct(INT pos = 0) const;
   VSTRUCT&                getVStruct(INT pos = 0);
   CtiProtocolVersacom&    setVStruct(const VSTRUCT &aVst);

   /*-------------------------------------------------------------------------*
    * This method MUST be called prior to any command building method.
    * It's importance is derived only from the fact that the LCU_415 type behaves
    * differently from other types.  One could make the assumption that this
    * means only that LCU-415's need call this method, but this may not always
    * be the case...
    *-------------------------------------------------------------------------*/
   INT                     getTransmitterType() const;
   CtiProtocolVersacom&    setTransmitterType(INT type);
   CtiProtocolVersacom&    VersacomTransmitter(INT type);

   /*-------------------------------------------------------------------------*
    * This method implements a shed type VCONTROL (command 4) versacom command.
    * relaymask is a bitmask of bits 0, 1, and 2 indicating the relays involved
    * in the operation.
    * controltime is in seconds and is resolved by a seperate method and is
    * ALWAYS rounded up to the next larger valid control amount.
    *-------------------------------------------------------------------------*/
   INT    VersacomShedCommand(UINT controltime = 0, UINT relaymask = 0);

   /*-------------------------------------------------------------------------*
    * This method implements an extended shed type VECONTROL versacom command.
    * Only one specific relay may be targeted, or relay zero indicating ALL
    * relays may be targeted.  There is a default random start time of 120
    * seconds, and a zero second delay prepended to the operation.  The
    * controltime parameter is resolved by a different method and is always
    * rounded up to the next larger valid shed time...
    *-------------------------------------------------------------------------*/
   INT    VersacomShedCommandEx(UINT controltime = 0,
                                                 UINT relay = 0,
                                                 UINT rand  = 120,
                                                 UINT delay = 0);

   /*-------------------------------------------------------------------------*
    * This method implements a cycle type VCONTROL (command 4) versacom command.
    * relaymask is a bitmask of bits 0, 1, and 2 indicating the relays involved
    * in the operation.
    * controltime is in percent and is resolved by a seperate method and is
    * ALWAYS rounded up to the next larger valid cycle percent.
    *-------------------------------------------------------------------------*/
   INT    VersacomCycleCommand(UINT controltime = 0, UINT relaymask = 0);

   /*-------------------------------------------------------------------------*
    * This method implements an extended cycle type VECONTROL versacom command.
    * Only one specific relay may be targeted, or relay zero indicating ALL
    * relays may be targeted.
    * The DSM/2 default 30 minute period and repeats of 8 is used and a zero
    * second delay prepended to the operation.  The percent is resolved by a
    * different internal method.
    *-------------------------------------------------------------------------*/
   INT    VersacomCycleCommandEx(UINT percent       = 0,
                                                  UINT relay         = 0,
                                                  UINT period        = 30,
                                                  UINT repeat        = 8,
                                                  UINT delay         = 0);


   /*-------------------------------------------------------------------------*
    * This method implements a ZERO SECOND shed type VCONTROL (command 4)
    * versacom command.
    * relaymask is a bitmask of bits 0, 1, and 2 indicating the relays involved
    * in the operation.
    *-------------------------------------------------------------------------*/
   INT    VersacomRestoreCommand(UINT relaymask = 0);

   /*-------------------------------------------------------------------------*
    * This method implements a ZERO PERCENT cycle type VCONTROL (command 4)
    * versacom command.
    * relaymask is a bitmask of bits 0, 1, and 2 indicating the relays involved
    * in the operation.
    *-------------------------------------------------------------------------*/
   INT    VersacomTerminateCommand(UINT relaymask = 0);

   /*-------------------------------------------------------------------------*
    * Constructs a message buffer for a capacitor control (VDATA)
    * command.  The argument trips control if non-zero, or closes
    * it if zero.
    *-------------------------------------------------------------------------*/
   INT    VersacomCapacitorControlCommand(BOOL Trip);

   /*-------------------------------------------------------------------------*
    * Constructs a message buffer for a voltage capacitor control (VDATA)
    * command.  The argument enables voltage control if non-zero, or disables
    * it if zero.
    *-------------------------------------------------------------------------*/
   INT    VersacomVoltageControlCommand(BOOL Enable);

   /*-------------------------------------------------------------------------*
    * Constructs a message buffer for a VINITIATOR command
    *-------------------------------------------------------------------------*/
   INT    VersacomInitiatorCommand(UINT Initiator);

   /*-------------------------------------------------------------------------*
    * Builds a message to remove or restore service to a versacom switch.
    *  ONE or more of the following may be defined, so long as they are not
    *  mutually exclusive.
    *  serviceflag == VC_SERVICE_T_OUT == 1 is Temporary OUT of service
    *  serviceflag == VC_SERVICE_T_IN  == 2 is Temporary IN service
    *  serviceflag == VC_SERVICE_C_OUT == 4 is Contractual OUT of service
    *  serviceflag == VC_SERVICE_C_IN  == 8 is Contractual IN service
    *-------------------------------------------------------------------------*/
   INT    VersacomServiceCommand(UINT serviceflag);

   /*-------------------------------------------------------------------------*
    * Builds a message to temporarily remove or restore service to a versacom switch.
    * cacel restores a prior Extended Temporary OOS command
    * led_off disables the lights on the switch during the OOS
    * half seconds off is the OOS timeout in half seconds.
    *-------------------------------------------------------------------------*/
   INT    VersacomExtendedServiceCommand(bool cancel, bool led_off, int offtimeinhours );

   /*-----------------------------------------------------------------*
    * A VDATA Command
    * If a len parameter greater than 6 is provided, we will use the
    * extended version of this command.  Note that a maximum of 30 bytes
    * may be sent.
    *-----------------------------------------------------------------*/
   INT    VersacomDataCommand(BYTE *message, INT len = 6);

   /*-----------------------------------------------------------------*
    * This is the raw VCONFIG command.
    * The enumeration above covers the more common "configtype"
    * arguments. The cfg parameter must point to a 6 byte buffer
    * of a suitable format to accomplish the config operation for that
    * configtype.
    *-----------------------------------------------------------------*/
   INT    VersacomConfigCommand(UINT configtype, BYTE *cfg);
   INT    VersacomRawConfigCommand(const BYTE *cfg);

   /*-----------------------------------------------------------------*
    * A VCONFIG Command wrapper to configure the operation of the LEDS
    * The argument "leds" is a mask of the following:
    * VC_REPORT_LED_ENABLED
    * VC_STATUS_LED_ENABLED
    * VC_LOAD_LED_ENABLED
    *-----------------------------------------------------------------*/
   INT    VersacomConfigLEDCommand(BYTE leds);

   /*-----------------------------------------------------------------*
    * A VPROPOGATION command which accepts the following args:
    *
    * VC_PROPTERMINATE               0x01
    * VC_PROPINCREMENT               0x02
    * VC_PROPDISPLAY                 0x04
    *-----------------------------------------------------------------*/
   INT    VersacomPropagationCommand(BYTE propmask);
   INT    VersacomConfigPropagationTimeCommand(BYTE duration);

   /*-----------------------------------------------------------------*
    * A VCOUNTRESET command which accepts the following args:
    *
    * VC_RESETR4COUNT                0x80
    * VC_RESETRTCCOUNT               0x40
    * VC_RESETLUFCOUNT               0x20
    * VC_RESETRELAY1COUNT            0x10
    *
    * VC_RESETRELAY2COUNT            0x08
    * VC_RESETRELAY3COUNT            0x04
    * VC_RESETCOMMLOSSCOUNT          0x02
    * VC_RESETPROPAGATIONCOUNT       0x01
    *-----------------------------------------------------------------*/
   INT    VersacomCountResetCommand(UINT resetmask);

   INT    VersacomConfigCycleRepeatsCommand(INT relay, USHORT repeats);
   INT    VersacomConfigScramTimeCommand(INT relay,INT seconds);
   INT    VersacomConfigColdLoadCommand(INT relay, INT seconds);

   void                    dumpMessageBuffer();
   INT                     parseRequest(CtiCommandParser &parse, const VSTRUCT &aVst);

   INT                     assemblePutConfig(CtiCommandParser  &parse, const VSTRUCT &aVst);
   INT                     assemblePutStatus(CtiCommandParser  &parse, const VSTRUCT &aVst);
   INT                     assembleControl(CtiCommandParser  &parse, const VSTRUCT &aVst);

   INT VersacomFillerCommand(BYTE uid);

   INT VersacomFullAddressCommand(BYTE uid, BYTE aux, BYTE sec, USHORT clsmask, USHORT divmask, BYTE svc);

};
#endif // #ifndef __PROT_VERSACOM_H__
