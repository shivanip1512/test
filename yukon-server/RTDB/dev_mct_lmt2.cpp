
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_lmt2
*
* Date:   6/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct_lmt2.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/11/05 19:37:42 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "devicetypes.h"
#include "dev_mct_lmt2.h"
#include "logger.h"
#include "mgr_point.h"
#include "prot_emetcon.h"
#include "pt_numeric.h"
#include "numstr.h"


set< CtiDLCCommandStore > CtiDeviceMCT_LMT2::_commandStore;

bool CtiDeviceMCT_LMT2::initCommandStore()
{
   bool failed = false;

   CtiDLCCommandStore cs;


   return failed;
}


bool CtiDeviceMCT_LMT2::getOperation( const UINT &cmd, USHORT &function, USHORT &length , USHORT &io)
{
   bool found = false;

   #if 0
   if(_commandStore.empty())  // Must initialize!
   {
      CtiDeviceMCT_LMT2::initCommandStore();
   }
   #endif

   CTICMDSET::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

   if( itr != _commandStore.end() )    // It's prego!
   {
      CtiDLCCommandStore &cs = *itr;
      function = cs._funcLen.first;          // Copy over the found funcLen pair!
      length = cs._funcLen.second;           // Copy over the found funcLen pair!
      io = cs._io;
      found = true;
   }

   // Look in the parent if not found in the child!
   if(!found)
   {
      found = Inherited::getOperation(cmd, function, length, io);
   }

   return found;
}

INT CtiDeviceMCT_LMT2::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   switch(InMessage->Sequence)
   {
   case (CtiProtocolEmetcon::GetConfig_Model):
      {
         status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
         break;
      }
   default:
      {
         status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

         if(status != NORMAL)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
         }
         break;
      }
   }

   return status;
}


INT CtiDeviceMCT_LMT2::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   resetScanFreezePending();
   resetScanFreezeFailed();

   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      // No error occured, we must do a real decode!

      INT ssp;
      char rev;

      RWCString sspec;
      RWCString options("Options:\n");

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

      ssp = InMessage->Buffer.DSt.Message[0];
      rev = 64 + InMessage->Buffer.DSt.Message[1];

      sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString(rev) + "\n";

      if(InMessage->Buffer.DSt.Message[2] & 0x01)
      {
         options+= RWCString("  Latched relay\n");
      }
      else
      {
         options+= RWCString("  No latched relay\n");
      }

      if(InMessage->Buffer.DSt.Message[2] & 0x04)
      {
         options+= RWCString("  No encoding meter\n");
      }
      else
      {
         options+= RWCString("  Encoding meter\n");
      }

      if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
         }

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);
      ReturnMsg->setResultString( sspec + options );

      retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
   }

   return status;
}

CtiDeviceMCT_LMT2::CtiDeviceMCT_LMT2() {}

CtiDeviceMCT_LMT2::CtiDeviceMCT_LMT2(const CtiDeviceMCT_LMT2& aRef)
{
  *this = aRef;
}

CtiDeviceMCT_LMT2::~CtiDeviceMCT_LMT2() {}

CtiDeviceMCT_LMT2& CtiDeviceMCT_LMT2::operator=(const CtiDeviceMCT_LMT2& aRef)
{
  if(this != &aRef)
  {
     Inherited::operator=(aRef);
  }
  return *this;
}


