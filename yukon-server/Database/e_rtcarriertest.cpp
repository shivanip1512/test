
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   e_rtcarriertest
*
* Date:   8/21/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/e_rtcarriertest.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <iostream>
using namespace std;

#include <rw/thr/thrutil.h>

#include "tbl_rtcarrier.h"
#include "resolvers.h"
#include "desolvers.h"
#include "rtdb.h"
#include "ctibase.h"

void main()
{
   CtiTableCarrierRoute testRC;
   int inVal;

   {
      testRC.DumpData();

      testRC.Restore();
      cout << "Doing RESTORE" << endl;
      cout << " The ROUTEID = " << testRC.getRouteID() << endl;
      cout << " The BUS = " << testRC.getBus() << endl;
      cout << " The CCUFIXBITS = " << testRC.getCCUFixBits() << endl;
      cout << " The CCUVARBITS = " << testRC.getCCUVarBits() << endl;
      cout << " The AMPUSETYPE = " << testRC.getAmpUseType() << endl;
      cout << " The AMP = " << testRC.getAmp() << endl;
      cout << endl;

      cout << "Doing DUMP " << endl;
      testRC.DumpData();
      cout << endl;

      cout << "ROUTEID to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testRC.setRouteID(inVal);

      cout << "BUS to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testRC.setBus(inVal);

      cout << "CCUFIXBITS to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testRC.setCCUFixBits(inVal);

      cout << "CCUVARBITS to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testRC.setCCUFixBits(inVal);

      cout << "AMP to set? " << endl;
      cin >> inVal;
      cout << "Doing SET " << endl;
      testRC.setAmpUseType(inVal);

      cout << "Doing DUMP " << endl;
      testRC.DumpData();
      cout << endl;

      cout << "Doing UPDATE" << endl;
      testRC.Update();
      cout << "Doing GETs " << endl;
      cout << " Table Name = " << testRC.getTableName() << endl;
      cout << " The ROUTEID = " << testRC.getRouteID() << endl;
      cout << " The BUS = " << testRC.getBus() << endl;
      cout << " The CCUFIXBITS = " << testRC.getCCUFixBits() << endl;
      cout << " The CCUVARBITS = " << testRC.getCCUVarBits() << endl;
      cout << " The AMP = " << testRC.getAmp() << endl;

      cout << "Doing INSERT " << endl;
      cout << testRC.Insert().message() << endl;

      cout << "Doing DUMP " << endl;
      testRC.DumpData();
      cout << endl;

      cout << "Doing GETs " << endl;
      cout << " The ROUTEID = " << testRC.getRouteID() << endl;
      cout << " The BUS = " << testRC.getBus() << endl;
      cout << " The CCUFIXBITS = " << testRC.getCCUFixBits() << endl;
      cout << " The CCUVARBITS = " << testRC.getCCUVarBits() << endl;
      cout << " The AMP = " << testRC.getAmp() << endl;

      cout << endl;

      testRC.Restore();

      cout << " The ROUTEID = " << testRC.getRouteID() << endl;
      cout << " The BUD = " << testRC.getBus() << endl;
      cout << " The CCUFIXBITS = " << testRC.getCCUFixBits() << endl;
      cout << " The CCUVARBITS = " << testRC.getCCUVarBits() << endl;
      cout << " The AMP = " << testRC.getAmp() << endl;

      cout << "ROUTEID to delete? " << endl;
      cin >> inVal;
      cout << "Setting ROUTEID to " << inVal << endl;
      testRC.setRouteID(inVal);
      cout << "Verifyig ROUTEID = " << testRC.getRouteID() << endl;
/*
      cout << "SCANTYPE to match for delete? " << endl;
      cin >> inVal;
      cout << "Setting SCANTYPE = " << inVal << endl;
      testRC.setScanType(inVal);
*/
      cout << "Verifyig" << endl;
      testRC.DumpData();

      cout << "Trying DELETE" << endl;
      testRC.Delete();

      cout << endl;

   }
   exit(0);
}
