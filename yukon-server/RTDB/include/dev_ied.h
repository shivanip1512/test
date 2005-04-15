/*-----------------------------------------------------------------------------*
*
* File:   dev_ied
*
* Class:  CtiDeviceIED
* Date:   2/25/2000
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/04/15 19:02:51 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_IED_H__
#define __DEV_IED_H__
#pragma warning( disable : 4786)


#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_remote.h"
#include "tbl_dv_ied.h"
#include "logger.h"
#include "xfer.h"

class CtiPort;
class CtiDeviceBase;

class CtiDeviceIED : public CtiDeviceRemote
{
public:
      // possible states in our big list of state machines
   typedef enum
   {
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

   } CtiMeterMachineStates_t;


   typedef enum
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
   } CtiMeterCmdStates_t;

protected:

   CtiTableDeviceIED   _ied;

private:

   // current state of the device
   CtiMeterMachineStates_t    _currentState;

   // where we are going next
   CtiMeterMachineStates_t    _previousState;

   // number of attempts to communicate to device
   INT                        _attemptsRemaining;

   //  current command
   CtiMeterCmdStates_t        _currentCommand;

public:

   typedef CtiDeviceRemote Inherited;

   CtiDeviceIED()  :
      _currentState(StateHandshakeInitialize),
      _previousState(StateHandshakeInitialize),
      _attemptsRemaining (7),  //schlumberger spec ??
      _currentCommand(CmdScanData)
   {}

   CtiDeviceIED(const CtiDeviceIED& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiDeviceIED() {}

   CtiDeviceIED& operator=(const CtiDeviceIED& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
         _ied = getIED();

#if 0 // CGP 051700 I don't think we really want to copy the device as being in a scan state.. Do we?

         _currentState = (CtiMeterMachineStates_t) aRef.getCurrentState();
         _previousState = (CtiMeterMachineStates_t) aRef.getPreviousState();
         _attemptsRemaining = aRef.getAttemptsRemaining();
         _currentCommand = (CtiMeterCmdStates_t) aRef.getCurrentCommand();
#else

        _currentState = StateHandshakeSendStart;
        _previousState = StateHandshakeSendStart;
        _attemptsRemaining = 3;
        _currentCommand = CmdScanData;
#endif

      }
      return *this;
   }

   CtiTableDeviceIED&   getIED()            { return _ied; }
   CtiTableDeviceIED    getIED() const      { return _ied; }
   CtiDeviceIED&        setIED(const CtiTableDeviceIED &aIED )
   {
      _ied = aIED;
      return *this;
   }

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTableDeviceIED::getSQL(db, keyTable, selector);
   }

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

      if(getDebugLevel() & DEBUGLEVEL_DATABASE) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      _ied.DecodeDatabaseReader(getType(), rdr);
   }


   // functions for all IEDs


   /*
    *  A paired set which implements a state machine (before/do port work/after) in conjunction with
    *  the port's function out/inMess pair.
    */
   virtual INT generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)                        { return NoHandShakeMethod;}
   virtual INT decodeResponseHandshake (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)     { return NoHandShakeMethod;}

   virtual INT generateCommandDisconnect (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)                       { return NoHandShakeMethod;}
   virtual INT decodeResponseDisconnect (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)    { return NoHandShakeMethod;}

   virtual INT generateCommand    (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)                              { return NoGenerateCmdMethod;}
   virtual INT decodeResponse (CtiXfer &Transfer,INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)               { return NoDecodeResponseMethod;}

   virtual INT allocateDataBins (OUTMESS *)                                         { return MemoryError;}
   virtual INT freeDataBins ()                                                      { return MemoryError;}
   virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived)      { return NoDataCopyMethod;}
   virtual INT copyLoadProfileData (BYTE *aInMessBuffer, ULONG &aBytesReceived)     { return NoDataCopyMethod;}


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
         if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Port has indicated a connected device swap. " << endl;
            dout << "  " << getName() << " has replaced DEVID " << oldid << " as the currently connected device" << endl;
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

   virtual RWCString getPassword() const       { return getIED().getPassword(); }

};
#endif // #ifndef __DEV_IED_H__
