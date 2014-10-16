/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_52
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_52.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*
*    History:
      $Log: std_ansi_tbl_five_two.cpp,v $
      Revision 1.10  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.9  2005/12/20 17:19:56  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.8  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.7  2005/06/16 19:17:59  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.6  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/12/10 21:58:41  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.4  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_52.h"
#include "ctidate.h"
#include "ctitime.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable52::CtiAnsiTable52( BYTE *dataBlob, int timefmat )
{
    int bytes = 0;
    _timefmt = timefmat;

    // Clock Calendar - LTIME_DATE
    bytes = toUint32LTime( dataBlob, clock_table.clock_calendar, _timefmt );
    dataBlob += bytes;

    dataBlob += toAnsiIntParser(dataBlob, &clock_table.time_date_qual, sizeof( unsigned char ));

    ULONG timeNow = CtiTime().seconds();
    _meterServerTimeDifference = (int)abs((int)(timeNow - CtiTime(clock_table.clock_calendar).seconds()));

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable52::~CtiAnsiTable52()
{
   //delete clock_table;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable52& CtiAnsiTable52::operator=(const CtiAnsiTable52& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable52::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList <<"Clock Calendar";
    itemList.add("year")   << getClkCldrYear();
    itemList.add("month")  << getClkCldrMon();
    itemList.add("day")    << getClkCldrDay();
    itemList.add("hour")   << getClkCldrHour();
    itemList.add("minute") << getClkCldrMin();
    itemList.add("second") << getClkCldrSec();

    itemList <<"Time Date Quality";
    itemList.add("day of the week")                    <<       clock_table.time_date_qual.day_of_week;
    itemList.add("daylight savings flag")              << (bool)clock_table.time_date_qual.dst_flag;
    itemList.add("greenwich meantime flag")            << (bool)clock_table.time_date_qual.gmt_flag;
    itemList.add("time zone applied flag")             << (bool)clock_table.time_date_qual.tm_zn_applied_flag;
    itemList.add("daylight savings time applied flag") << (bool)clock_table.time_date_qual.dst_applied_flag;

    const CtiTime::DisplayOffset displayOffset =
            (isCalendarValid() && meterTimeIsGMT())
                ? CtiTime::Gmt
                : CtiTime::Local;

    itemList.add("Time") << CtiTime(clock_table.clock_calendar).asString(displayOffset, CtiTime::OmitTimezone);

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 52") <<
            endl <<"** Clock Table **"<< 
            itemList
            );
}

bool CtiAnsiTable52::isCalendarValid()
{
    return CtiTime(clock_table.clock_calendar).isValid();
}

bool CtiAnsiTable52::meterTimeIsGMT() const
{
    return clock_table.time_date_qual.tm_zn_applied_flag && !clock_table.time_date_qual.gmt_flag;
}

CtiTime CtiAnsiTable52::getClkCldrTime()
{
    return CtiTime(clock_table.clock_calendar);
}

CtiDate CtiAnsiTable52::getClkCldrDate()
{
    return meterTimeIsGMT() ? getClkCldrTime().dateGMT() : getClkCldrTime().date();
}

int CtiAnsiTable52::getClkCldrYear()
{
    if (!isCalendarValid())
        return 0;

    return (int)getClkCldrDate().year();
}

int CtiAnsiTable52::getClkCldrMon()
{
    if (!isCalendarValid())
        return 0;

    return (int)getClkCldrDate().month();
}

int CtiAnsiTable52::getClkCldrDay()
{
    if (!isCalendarValid())
        return 0;

    return (int)getClkCldrDate().dayOfMonth();
}

int CtiAnsiTable52::getClkCldrHour()
{
    if (!isCalendarValid())
        return 0;

    return meterTimeIsGMT() ? getClkCldrTime().hourGMT() : getClkCldrTime().hour();
}

int CtiAnsiTable52::getClkCldrMin()
{
    if (!isCalendarValid())
        return 0;

    return meterTimeIsGMT() ? getClkCldrTime().minuteGMT() : getClkCldrTime().minute();
}

int CtiAnsiTable52::getClkCldrSec()
{
    if (!isCalendarValid())
        return 0;

    return meterTimeIsGMT() ? getClkCldrTime().secondGMT() : getClkCldrTime().second();
}

ULONG CtiAnsiTable52::getMeterServerTimeDifference()
{
    return _meterServerTimeDifference;
}

bool CtiAnsiTable52::adjustTimeForDST()
{
    if ((bool)!clock_table.time_date_qual.dst_applied_flag && (bool)clock_table.time_date_qual.dst_flag)
        return true;
    else
        return false;
}

bool CtiAnsiTable52::getTimeZoneAppliedFlag()
{
    return clock_table.time_date_qual.tm_zn_applied_flag;
}

ULONG CtiAnsiTable52::adjustTimeZoneAndDST(ULONG adjustedTime)
{
    if (!isCalendarValid())
        return 0;

    if (getTimeZoneAppliedFlag() )
        adjustedTime = (CtiTime::fromLocalSeconds(adjustedTime)).seconds();
    if (adjustTimeForDST() )
    {
        if ( CtiTime(adjustedTime) > CtiTime().beginDST( CtiDate().year() ) )
            adjustedTime += 3600;
    }
    return adjustedTime;
}


