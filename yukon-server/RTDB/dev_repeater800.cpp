/*-----------------------------------------------------------------------------*
*
* File:   dev_repeater800
*
* Date:   8/24/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_repeater800.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"

set< CtiDLCCommandStore > CtiDeviceRepeater800::_commandStore;


CtiDeviceRepeater800::CtiDeviceRepeater800() {}

CtiDeviceRepeater800::CtiDeviceRepeater800(const CtiDeviceRepeater800& aRef)
{
   *this = aRef;
}

CtiDeviceRepeater800::~CtiDeviceRepeater800() {}

CtiDeviceRepeater800& CtiDeviceRepeater800::operator=(const CtiDeviceRepeater800& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}

bool CtiDeviceRepeater800::initCommandStore()
{
   bool failed = false;

   CtiDLCCommandStore cs;

   cs._cmd = CtiProtocolEmetcon::GetValue_PFCount;
   cs._io = IO_READ;
   cs._funcLen = make_pair((int)Rpt800_PFCountPos,
                           (int)Rpt800_PFCountLen);
   _commandStore.insert( cs );

   return failed;
}


bool CtiDeviceRepeater800::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
   bool found = false;

   if(_commandStore.empty())  // Must initialize!
   {
      CtiDeviceRepeater800::initCommandStore();
   }

   DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

   if( itr != _commandStore.end() )
   {
      CtiDLCCommandStore &cs = *itr;
      function = cs._funcLen.first;             // Copy over the found function!
      length = cs._funcLen.second;              // Copy over the found length!
      io = cs._io;                              // Copy over the found io indicator!

      found = true;
   }
   //  CtiDevRepeater900 is the base...
   else                                         // Look in the parent if not found in the child!
   {
      found = Inherited::getOperation(cmd, function, length, io);
   }

   return found;
}


INT CtiDeviceRepeater800::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;


   switch(InMessage->Sequence)
   {
   case (CtiProtocolEmetcon::GetValue_PFCount):
      {
         status = decodeGetValuePFCount(InMessage, TimeNow, vgList, retList, outList);
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


INT CtiDeviceRepeater800::decodeGetValuePFCount(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   DSTRUCT *DSt   = &InMessage->Buffer.DSt;


   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      // No error occured, we must do a real decode!

      INT   j;
      ULONG pfCount = 0;

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);

      for(j = 0; j < 2; j++)
      {
         pfCount = (pfCount << 8) + InMessage->Buffer.DSt.Message[j];
      }

      {
         RWCString resultString;

         LockGuard guard(monitor());               // Lock the MCT device!
         CtiPointBase *pPoint;

         resultString = getName() + " / powerfail count = " + CtiNumStr((int)pfCount);

         pData = CTIDBG_new CtiPointDataMsg(20, (double)pfCount, NormalQuality, PulseAccumulatorPointType, resultString);
         if(pData != NULL)
         {
            ReturnMsg->PointData().insert(pData);
            pData = NULL;  // We just put it on the list...
         }
      }

      retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
   }

   return status;
}



