
#pragma warning( disable : 4786)
#ifndef __TRX_711_H__
#define __TRX_711_H__

/*-----------------------------------------------------------------------------*
*
* File:   trx_711
*
* Class:  CtiTransmitter711Info
* Date:   3/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/trx_711.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "trx_info.h"

class CtiTransmitter711Info : public CtiTransmitterInfo
{
public:

   HCTIQUEUE        QueueHandle;
   HCTIQUEUE        ActinQueueHandle;

   USHORT           PortQueueEnts;
   USHORT           PortQueueConts;
   USHORT           RContInLength;
   USHORT           ReadyN;
   USHORT           NCsets;
   USHORT           NCOcts;
   USHORT           FreeSlots;
   USHORT           RColQMin;
   QUEENT           QueTable[MAXQUEENTRIES];

private:

public:
   CtiTransmitter711Info() {}

   CtiTransmitter711Info(const CtiTransmitter711Info& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiTransmitter711Info()
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }


   CtiTransmitter711Info& operator=(const CtiTransmitter711Info& aRef)
   {
      if(this != &aRef)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
      }
      return *this;
   }

   CtiTransmitter711Info& reduceEntsConts(BOOL rcontstoo)
   {
      /* Decrease the queue entry count */
      if(PortQueueEnts > 0) PortQueueEnts--;

      /* If this is an RCONT compatible command decrease said count */
      if(rcontstoo && PortQueueConts > 0)
      {
         PortQueueConts--;
      }

      return *this;
   }

};
#endif // #ifndef __TRX_711_H__
