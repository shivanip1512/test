
#pragma warning( disable : 4786)
#ifndef __DEV_IDLC_H__
#define __DEV_IDLC_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_idlc
*
* Class:  CtiDeviceIDLC
* Date:   2/25/2000
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:24 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_remote.h"
#include "tbl_dv_idlcremote.h"
#include "trx_info.h"
#include "trx_711.h"

class CtiDeviceIDLC : public CtiDeviceRemote
{
protected:

   CtiTableDeviceIDLC   _idlc;

   CtiTransmitterInfo   *_trxInfo;
private:

   public:

   typedef CtiDeviceRemote Inherited;

   CtiDeviceIDLC() :
   _trxInfo(NULL)
   {}

   CtiDeviceIDLC(const CtiDeviceIDLC& aRef) :
   _trxInfo(NULL)
   {
      *this = aRef;
   }

   virtual ~CtiDeviceIDLC()
   {
      if(_trxInfo != NULL)
      {
         delete _trxInfo;
         _trxInfo = NULL;
      }
   }

   CtiDeviceIDLC& operator=(const CtiDeviceIDLC& aRef)
   {
      Inherited::operator=(aRef);

      if(this != &aRef)
      {
         _idlc = aRef.getIDLC();

         if(_trxInfo != NULL)
         {
            delete _trxInfo;
            _trxInfo = NULL;
         }

         if( aRef.getTrxInfo() != NULL )
         {
            *initTrxInfo() = *(aRef.getTrxInfo()); // Do an init + assignment
         }
      }
      return *this;
   }

   CtiTableDeviceIDLC  getIDLC() const
   {
      return _idlc;
   }

   CtiTableDeviceIDLC& getIDLC()
   {
      return _idlc;
   }

   CtiDeviceIDLC& setIDLC( const CtiTableDeviceIDLC &aRef )
   {
      LockGuard guard(monitor());
      _idlc = aRef;
      return *this;
   }

   virtual LONG getAddress() const             { return _idlc.getAddress();}
   virtual INT  getPostDelay() const           { return _idlc.getPostDelay();}


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTableDeviceIDLC::getSQL(db, keyTable, selector);
   }

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

      if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      _idlc.DecodeDatabaseReader(rdr);
   }

   virtual CtiTransmitterInfo* getTrxInfo() const // Porter side info to retrieve transmitter device bookkeeping!
   {
      return _trxInfo;
   }
   virtual bool hasTrxInfo() const    // This device type does indeed have a TrxInfo!
   {
      return true;
   }
   virtual CtiTransmitterInfo* initTrxInfo()  // Porter side info to setup transmitter device bookkeeping!
   {
      // set up and return the _trxInfo

      if(_trxInfo != NULL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** UNEXPECTED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }

         delete _trxInfo;
      }

      switch( getType() )
      {
      case TYPE_CCU711:
         {
            int i;
            CtiTransmitter711Info *p711 = new CtiTransmitter711Info;

            p711->ControlOutMessage = NULL;
            p711->FiveMinuteCount = 0;
            p711->Type = getType();
            p711->Status = 0;
            p711->ReadyN = 32;
            p711->NCsets = 0;
            p711->NCOcts = 0;
            p711->PortQueueEnts = 0;
            p711->PortQueueConts = 0;
            p711->RColQMin = 0;

            p711->FreeSlots = MAXQUEENTRIES;

            for(i = 0; i < MAXQUEENTRIES; i++)
            {
               p711->QueTable[i].InUse = 0;
               p711->QueTable[i].TimeSent = 0L;
            }

            /* create the queue */
            if(CreateQueue (&p711->QueueHandle, QUE_PRIORITY))
            {
               printf ("Error Creating CCU Queue\n");
            }

            /* create the queue */
            if(CreateQueue (&p711->ActinQueueHandle, QUE_PRIORITY))
            {
               printf ("Error Creating Actin Queue\n");
            }

            _trxInfo = (CtiTransmitterInfo*)p711;

            break;

         }
      default:
         {
            _trxInfo = new CtiTransmitterInfo;

            _trxInfo->Type                   = getType();
            _trxInfo->Status                 = 0;
            _trxInfo->StageTime              = 0;
            _trxInfo->FiveMinuteCount        = 0;
            _trxInfo->NextCommandTime        = 0;
            _trxInfo->LCUFlags               = 0;
            _trxInfo->ControlOutMessage      = NULL;
            _trxInfo->RemoteSequence.Reply   = 0;

            break;
         }
      }

      return _trxInfo;
   }


};
#endif // #ifndef __DEV_IDLC_H__
