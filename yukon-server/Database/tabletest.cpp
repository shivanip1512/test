/*-----------------------------------------------------------------------------*
*
* File:   tabletest
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tabletest.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/17 19:02:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;

#include <rw/thr/thrutil.h>

#include "tbl_pthist.h"
#include "tbl_rtroute.h"
#include "resolvers.h"
#include "rtdb.h"
#include "ctibase.h"
void main()
{
    RWDBDateTime now;

    {
        CtiTablePointHistory p_hist( 18L, now, 0, 100.1 );

        cout << "Inserting row into point hist table." << endl;
        p_hist.Insert();
  rwSleep(10000);
        cout << "UPdating row in point hist table" << endl;
        p_hist.setQuality(5);
        p_hist.setValue(200.1);
        p_hist.Update();
  rwSleep(10000);
    }

    {
        CtiTablePointHistory p_hist;
        p_hist.setPointID(18L);
        p_hist.setTimeStamp(now);

        cout << "Restoring row from the point hist table" << endl;
        p_hist.Restore();

        cout << "Deleting row from the point hist table" << endl;
        p_hist.Delete();
    }

    exit(0);

    {
    long this_is_silly = 5000L;

    {
        CtiTableRoute t_route( this_is_silly, "testroute", CCURouteType );

        t_route.DumpData();

        cout << "Inserting route 5000 into the database" << endl;
        t_route.Insert();

        cout << "Updating route 5000 in the database" << endl;
        t_route.setName("modifiedname");
        t_route.Update();
    }

    {
        CtiTableRoute t_route;
        t_route.setRouteID( this_is_silly );

        cout << "Restoring route 5000 from the database" << endl;
        t_route.Restore();
        t_route.DumpData();

        cout << "Deleteing route 5000 from the database" << endl;
        t_route.Delete();

    }
   }
    cout << "done" << endl;
}
