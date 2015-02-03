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


