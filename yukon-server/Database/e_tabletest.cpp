#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   e_tabletest
*
* Date:   8/1/2001
*
* PVCS KEYWORDS:
* Author : Eric Schmit
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/e_tabletest.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:57:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <iostream>
using namespace std;

#include <rw/thr/thrutil.h>

#include "tbl_2way.h"
#include "tbl_dv_wnd.h"
#include "resolvers.h"
#include "rtdb.h"
#include "ctibase.h"

void main()
{
   RWDBDateTime now;
   LONG retrievedTime = 0;
   LONG deviceID = 0;
   INT tempVal = 0;

   {

      /*
      MonthlyStats
      DailyStats
      HourlyStats
      FailureAlarm
      PerformAlarm
      Perform24Alarm
      */

      CtiTableDevice2Way d_twoway(-1);

      cout << "Doing a Restore()" << endl;
      d_twoway.Restore();

      cout << "Reading the DEVICE ID = " << d_twoway.getDeviceID() << endl;
      cout << "Reading the MONTHLY STATS = " << d_twoway.getMonthlyStats() << endl;
      cout << "Reading the DAILY STATS = " << d_twoway.getDailyStats() << endl;
      cout << "Reading the HOURLY STATS = " << d_twoway.getHourlyStats() << endl;
      cout << "Reading the FAILURE ALARM = " << d_twoway.getFailureAlarm() << endl;
      cout << "Reading the PERFORM ALARM = " << d_twoway.getPerformAlarm() << endl;
      cout << "Reading the PERFORM 24 ALARM = " << d_twoway.getPerform24Alarm() << endl;

      cout << "Setting DEVICE ID. " << endl;
      d_twoway.setDeviceID(666);
      rwSleep(1000);

      cout << "Reading new DEVICE ID from table." << endl;
      deviceID = d_twoway.getDeviceID();
      cout << "DEVICE ID I got = " << deviceID << endl;
      rwSleep(1000);

      cout << "Setting MONTHLY STATS to n" << endl;
      d_twoway.setMonthlyStats('n');
      rwSleep(1000);

      cout << "Reading new MONTHLY STATS from table. " << endl;
      tempVal = d_twoway.getMonthlyStats();
      cout << "MONTHLY STATS I got = " << tempVal << endl;
      rwSleep(1000);

      cout << "Setting MONTHLY STATS to y" << endl;
      d_twoway.setMonthlyStats('y');
      rwSleep(1000);

      cout << "Reading new MONTHLY STATS from table. " << endl;
      tempVal = d_twoway.getMonthlyStats();
      cout << "MONTHLY STATS I got = " << tempVal << endl;
      rwSleep(1000);


      cout << "Name of table = " << d_twoway.getTableName() << endl;

      cout << "Setting HOURLY STATS to " << "y" << endl;
      d_twoway.setHourlyStats('y');
      cout << "Getting new HOURLY STATS = " << d_twoway.getHourlyStats() << endl;

      cout << "Doing update..." << endl;
      d_twoway.Update();
      cout << "Doing insert..." << endl;
      d_twoway.Insert();

      cout << "Reading the DEVICE ID = " << d_twoway.getDeviceID() << endl;
      cout << "Reading the MONTHLY STATS = " << d_twoway.getMonthlyStats() << endl;
      cout << "Reading the DAILY STATS = " << d_twoway.getDailyStats() << endl;
      cout << "Reading the HOURLY STATS = " << d_twoway.getHourlyStats() << endl;
      cout << "Reading the FAILURE ALARM = " << d_twoway.getFailureAlarm() << endl;
      cout << "Reading the PERFORM ALARM = " << d_twoway.getPerformAlarm() << endl;
      cout << "Reading the PERFORM 24 ALARM = " << d_twoway.getPerform24Alarm() << endl;

      cout << "Doing restore...." << endl;
      d_twoway.Restore();

      cout << "Reading the DEVICE ID = " << d_twoway.getDeviceID() << endl;
      cout << "Altering the DEVICE ID = " << d_twoway.setDeviceID(1212).getDeviceID() << endl;

      cout << "Doing restore...." << endl;
      d_twoway.Restore();
      rwSleep(1000);

      cout << "Reading the DEVICE ID = " << d_twoway.getDeviceID() << endl;
      cout << "Reading MONTHLY STATS = " << d_twoway.getMonthlyStats() << endl;
      cout << "Reading the PERFORM ALARM = " << d_twoway.getPerformAlarm() << endl;
      rwSleep(1000);

      cout << endl;


   }

   //this was for testing tbl_dv_wnd.h

   {
      CtiTableDeviceWindow d_window(98);

      d_window.Restore();
      cout << "Reading the id = " << d_window.getDeviceID() << endl;
      cout << "Reading the type = " << d_window.getType() << endl;
      cout << "Reading the start time = " << d_window.getStartTime() << endl;
      cout << "Reading the stop time = " << d_window.getStopTime() << endl;

      cout << "Setting start time into window table. " << endl;
      d_window.setStartTime(999);
      rwSleep(1000);

      cout << "Reading new start time from window table. " << endl;
      retrievedTime = d_window.getStartTime();
      cout << "Start time I got = " << retrievedTime << endl;
      rwSleep(1000);

      cout << "Setting stop time into window table. " << endl;
      d_window.setStopTime(888);
      rwSleep(1000);

      cout << "Reading new stop time from window table. " << endl;
      retrievedTime = d_window.getStopTime();
      cout << "Stop time I got = " << retrievedTime << endl;
      rwSleep(1000);

      cout << "Original name of table = " << d_window.getTableName() << endl;

      cout << "Doing update...." << endl;
      d_window.Update();

      cout << "Reading the type = " << d_window.getType() << endl;
      cout << "Altering the type = " << d_window.setType(1212).getType() << endl;

      cout << "Doing update...." << endl;
      d_window.Update();
      rwSleep(1000);

      cout << "Reading the id = " << d_window.getDeviceID() << endl;
      cout << "Reading type again = " << d_window.getType() << endl;
      cout << "Reading the start time = " << d_window.getStartTime() << endl;
      cout << "Reading the stop time = " << d_window.getStopTime() << endl;
      rwSleep(1000);
   }

   cout << endl;
   exit(0);

   cout << "done" << endl;
}

