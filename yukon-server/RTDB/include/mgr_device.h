#ifndef __MGR_DEVICE_H__
#define __MGR_DEVICE_H__
/*************************************************************************
 *
 * mgr_route.h      7/7/99
 *
 *****
 *
 * The class which owns and manages route real time database
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_device.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2002/09/06 19:03:42 $
*
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/

#include <rw/db/connect.h>

#include "dlldefs.h"
#include "rtdb.h"
#include "dev_base.h"
#include "slctdev.h"

class CtiCommandMsg;
/*
 *  The following functions may be used to create sublists for the points in our database.
 */
class IM_EX_DEVDB CtiDeviceManager : public CtiRTDB<CtiDeviceBase>
{
private:

   bool _includeScanInfo;

   // Inherit "List" from Parent

   void RefreshDevices(bool &rowFound, RWDBReader& rdr, CtiDeviceBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiDeviceBase*,void*), void *arg);

public:
   CtiDeviceManager();
   virtual ~CtiDeviceManager();

   void RefreshList(LONG paoID);
   void RefreshList(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, BOOL (*fn)(CtiDeviceBase*,void*) = isADevice, void *d = NULL);

   // void RefreshDeviceRoute(LONG id = 0);
   // void RefreshScanRates(LONG id = 0);
   // void RefreshDeviceWindows(LONG id = 0);

   void DumpList(void);
   void DeleteList(void);

   CtiDeviceBase* getEqual(LONG Remote);
   CtiDeviceBase* RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
   CtiDeviceBase* RemoteGetEqual(LONG Remote);
   CtiDeviceBase* RemoteGetEqualbyName (const RWCString &RemoteName);

   void setIncludeScanInfo();
   void resetIncludeScanInfo();
};

#endif                  // #ifndef __MGR_DEVICE_H__
