
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   e_scanratetest
*
* Date:   8/17/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/e_scanratetest.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <iostream>
using namespace std;

#include <rw/thr/thrutil.h>

#include "tbl_scanrate.h"
#include "resolvers.h"
#include "desolvers.h"
#include "rtdb.h"
#include "ctibase.h"

void main()
{
   CtiTableDeviceScanRate testSR;
   int inVal;

   {
      testSR.setDeviceID(1);

//      testSR.Update();
      testSR.Restore();
      cout << "Doing RESTORE" << endl;
      cout << " The DEVICEID = " << testSR.getDeviceID() << endl;
      cout << " The SCANRATE = " << testSR.getScanRate() << endl;
      cout << " The SCANTYPE = " << testSR.getScanType() << endl;
      cout << " The SCANGROUP = " << testSR.getScanGroup() << endl;
      cout << " The SCANGROUP = " << testSR.getScanGroup() << endl;
      cout << " The UPDATED = " << testSR.getUpdated() << endl;
      cout << endl;

      cout << "Doing DUMP " << endl;
      testSR.DumpData();
      cout << endl;

      cout << "DEVICEID to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testSR.setDeviceID(inVal);

      cout << "SCANGROUP to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testSR.setScanGroup(inVal);

      cout << "SCANRATE to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testSR.setScanRate(inVal);

      cout << "SCANTYPE to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testSR.setScanType(inVal);

      cout << "Doing DUMP " << endl;
      testSR.DumpData();
      cout << endl;

      cout << "Doing UPDATE" << endl;
      testSR.Update();
      cout << " The UPDATED = " << testSR.getUpdated() << endl;
      cout << "Doing GETs " << endl;
      cout << " Table Name = " << testSR.getTableName() << endl;
      cout << " The DEVICEID = " << testSR.getDeviceID() << endl;
      cout << " The SCANRATE = " << testSR.getScanRate() << endl;
      cout << " The SCANTYPE = " << testSR.getScanType() << endl;
      cout << " The SCANGROUP = " << testSR.getScanGroup() << endl;

      cout << "Doing INSERT " << endl;
      cout << testSR.Insert().message() << endl;

      cout << "Doing DUMP " << endl;
      testSR.DumpData();
      cout << endl;

      cout << "Doing GETs " << endl;
      cout << " The DEVICEID = " << testSR.getDeviceID() << endl;
      cout << " The SCANRATE = " << testSR.getScanRate() << endl;
      cout << " The SCANTYPE = " << testSR.getScanType() << endl;
      cout << " The SCANGROUP = " << testSR.getScanGroup() << endl;

      cout << endl;

      testSR.Restore();

      cout << " The DEVICEID = " << testSR.getDeviceID() << endl;
      cout << " The SCANRATE = " << testSR.getScanRate() << endl;
      cout << " The SCANTYPE = " << testSR.getScanType() << endl;
      cout << " The SCANGROUP = " << testSR.getScanGroup() << endl;
      cout << " The UPDATED = " << testSR.getUpdated() << endl;

      cout << "DEVICEID to delete? " << endl;
      cin >> inVal;
      cout << "Setting DEVICEID to " << inVal << endl;
      testSR.setDeviceID(inVal);
      cout << "Verifyig DEVICEID = " << testSR.getDeviceID() << endl;
      cout << "SCANTYPE to match for delete? " << endl;
      cin >> inVal;
      cout << "Setting SCANTYPE = " << inVal << endl;
      testSR.setScanType(inVal);
      cout << "Verifyig" << endl;
      testSR.DumpData();

      cout << "Trying DELETE" << endl;
      testSR.Delete();


      cout << endl;

//      testSR.Delete();
   }
   exit(0);
}
