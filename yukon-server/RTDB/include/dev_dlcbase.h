
#ifndef __DEV_DLCBASE_H__
#define __DEV_DLCBASE_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_dlcbase
*
* Class:  CtiDeviceDLCBase
* Date:   8/19/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_dlcbase.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/05/20 15:11:24 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\cstring.h>
#include <rw\thr\mutex.h>

#include "dev_single.h"
#include "tbl_2way.h"
#include "tbl_scanrate.h"
#include "tbl_route.h"
#include "tbl_carrier.h"
#include "dlldefs.h"
#include "rte_base.h"
#include "mgr_route.h"
#include "prot_emetcon.h"
#include "logger.h"
#include "msg_cmd.h"

#include <set>
#include <utility>
using namespace std;

class CtiDLCCommandStore
{
public:

   UINT _cmd;  // Command
   UINT _io;   // Function read/write ?

   pair< UINT, UINT > _funcLen;  // Function and Length.


   CtiDLCCommandStore() :
       _cmd(0),    // == DLCCmd_Invalid
       _io(0)
       { }

   explicit CtiDLCCommandStore( const UINT &cmd ) :
       _cmd(cmd),
       _io(0)
       { }

   bool operator<( const CtiDLCCommandStore &rhs ) const
   {
      return( _cmd < rhs._cmd );
   }
};

class IM_EX_DEVDB CtiDeviceDLCBase : public CtiDeviceSingle
{
protected:

   CtiTableDeviceCarrier      CarrierSettings;
   CtiTableDeviceRoute        DeviceRoutes;

private:

   public:

   typedef set< CtiDLCCommandStore > CTICMDSET;

   typedef CtiDeviceSingle Inherited;

   CtiDeviceDLCBase();
   CtiDeviceDLCBase(const CtiDeviceDLCBase& aRef);
   virtual ~CtiDeviceDLCBase();

   CtiDeviceDLCBase& operator=(const CtiDeviceDLCBase& aRef);

   CtiTableDeviceRoute  getDeviceRoute() const;
   CtiTableDeviceRoute& getDeviceRoute();
   CtiDeviceDLCBase& setDeviceRoute(const CtiTableDeviceRoute& aRoute);

   CtiTableDeviceCarrier  getCarrierSettings() const;
   CtiTableDeviceCarrier& getCarrierSettings();
   CtiDeviceDLCBase& setCarrierSettings( const CtiTableDeviceCarrier & aCarrierSettings );

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DecodeRoutesDatabaseReader(RWDBReader &rdr);

   virtual LONG getAddress() const;
   virtual LONG getRouteID() const;

   INT retMsgHandler( RWCString commandStr, CtiReturnMsg *retMsg, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList );
   INT decodeCheckErrorReturn(INMESS *InMessage, RWTPtrSlist< CtiMessage > &retList);

   virtual bool processAdditionalRoutes( INMESS *InMessage ) const;
   virtual ULONG selectInitialMacroRouteOffset() const;

};
#endif // #ifndef __DEV_DLCBASE_H__
