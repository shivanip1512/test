
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct22X
*
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct22X.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:04 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "devicetypes.h"
#include "dev_mct22X.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "prot_emetcon.h"
#include "pt_numeric.h"
#include "numstr.h"

set< CtiDLCCommandStore > CtiDeviceMCT22X::_commandStore;

CtiDeviceMCT22X::CtiDeviceMCT22X() {}

CtiDeviceMCT22X::CtiDeviceMCT22X(const CtiDeviceMCT22X &aRef)
{
   *this = aRef;
}

CtiDeviceMCT22X::~CtiDeviceMCT22X() {}

CtiDeviceMCT22X& CtiDeviceMCT22X::operator=(const CtiDeviceMCT22X &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


bool CtiDeviceMCT22X::initCommandStore()
{
   bool failed = false;

   CtiDLCCommandStore cs;

   cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT22X_MReadAddr,
                            (int)MCT22X_MReadLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT22X_MReadAddr,
                            (int)MCT22X_MReadLen );
   _commandStore.insert( cs );

//  if'n'when - this meter requires you to subtract the current and previous meter readings to get a 5-minute demand
//    value...
//   cs._cmd     = CtiProtocolEmetcon::GetValue_Demand;
//   cs._io      = IO_READ;
//   cs._funcLen = make_pair( (int)MCT22X_DemandAddr,
//                            (int)MCT22X_DemandLen );
//   _commandStore.insert( cs );

   return failed;
}


bool CtiDeviceMCT22X::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
   bool found = false;

   if(_commandStore.empty())  // Must initialize!
   {
      CtiDeviceMCT22X::initCommandStore();
   }

   CTICMDSET::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

   if( itr != _commandStore.end() )
   {
      CtiDLCCommandStore &cs = *itr;
      function = cs._funcLen.first;             // Copy over the found function!
      length = cs._funcLen.second;              // Copy over the found length!
      io = cs._io;                              // Copy over the found io indicator!

      found = true;
   }
   else                                         // Look in the parent if not found in the child!
   {
      found = Inherited::getOperation(cmd, function, length, io);
   }

   return found;
}

/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT22X::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
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
