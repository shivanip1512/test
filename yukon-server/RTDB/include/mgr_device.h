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
 * REVISION     :  $Revision: 1.17 $
 * DATE         :  $Date: 2004/01/26 21:32:29 $
 *
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __MGR_DEVICE_H__
#define __MGR_DEVICE_H__

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

   bool (*_removeFunc)(CtiDeviceBase*,void*);
   bool _includeScanInfo;

   // Inherit "List" from Parent

   void refreshDevices(bool &rowFound, RWDBReader& rdr, CtiDeviceBase* (*Factory)(RWDBReader &));
   // void RefreshDeviceRoute(LONG id = 0);
   void refreshScanRates(LONG id = 0);
   void refreshDeviceWindows(LONG id = 0);

   void refreshList(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, bool (*removeFunc)(CtiDeviceBase*,void*) = isNotADevice, void *d = NULL, LONG paoID = 0);
   bool refreshDeviceByPao(CtiDeviceBase *&pDev, LONG paoID);
   void refreshExclusions(LONG id = 0);
   void refreshIONMeterGroups(LONG paoID = 0);
   void refreshMacroSubdevices(LONG paoID = 0);
   void refreshDeviceProperties(LONG paoID = 0);
   void refreshMCTConfigs(LONG paoID = 0);


public:
   CtiDeviceManager();
   virtual ~CtiDeviceManager();

   void refresh(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, bool (*removeFunc)(CtiDeviceBase*,void*) = isNotADevice, void *d = NULL, LONG paoID = 0, RWCString category = RWCString(""), RWCString devicetype = RWCString(""));

   void DumpList(void);
   void DeleteList(void);

   CtiDeviceBase* getEqual(LONG Remote);
   CtiDeviceBase* RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
   CtiDeviceBase* RemoteGetEqual(LONG Remote);
   CtiDeviceBase* RemoteGetEqualbyName (const RWCString &RemoteName);

   void setIncludeScanInfo();
   void resetIncludeScanInfo();

   bool mayDeviceExecuteExclusionFree(CtiDeviceBase *anxiousDevice, CtiTablePaoExclusion &deviceexclusion);
   bool removeDeviceExclusionBlocks(CtiDeviceBase *anxiousDevice);

};

#endif                  // #ifndef __MGR_DEVICE_H__
