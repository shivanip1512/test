#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_two
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_five_two.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/12/20 17:19:56 $
*
*    History: 
      $Log: std_ansi_tbl_five_two.cpp,v $
      Revision 1.9  2005/12/20 17:19:56  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

<<<<<<< std_ansi_tbl_five_two.cpp
      Revision 1.6.2.2  2005/08/12 19:54:04  jliu
      Date Time Replaced

      Revision 1.6.2.1  2005/07/27 19:28:01  alauinger
      merged from the head 20050720

=======
      Revision 1.8  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.7.2.1  2005/12/12 19:50:39  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

>>>>>>> 1.8
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

#include "logger.h"
#include "std_ansi_tbl_five_two.h"
#include "ctidate.h"
#include "ctitime.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableFiveTwo::CtiAnsiTableFiveTwo( int timefmat )
{
    _timefmt = timefmat;
}


CtiAnsiTableFiveTwo::CtiAnsiTableFiveTwo( BYTE *dataBlob, int timefmat )
{
    int bytes = 0;
    _timefmt = timefmat;
      
    // Clock Calendar - LTIME_DATE
    bytes = toUint32LTime( dataBlob, clock_table.clock_calendar, _timefmt );
    dataBlob += bytes;
    
    memcpy( (void *)&clock_table.time_date_qual, dataBlob, sizeof( unsigned char ));
    dataBlob +=  sizeof( unsigned char);

    if ((bool)clock_table.time_date_qual.dst_applied_flag && (bool)clock_table.time_date_qual.dst_flag)
    {
        clock_table.clock_calendar -= 3600;
    }


    ULONG timeNow = CtiTime().seconds();
    _meterServerTimeDifference = abs(timeNow - CtiTime(clock_table.clock_calendar).seconds());

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveTwo::~CtiAnsiTableFiveTwo()
{
   //delete clock_table;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveTwo& CtiAnsiTableFiveTwo::operator=(const CtiAnsiTableFiveTwo& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveTwo::generateResultPiece( BYTE **dataBlob )
{
    memcpy( *dataBlob, (void *)&clock_table, sizeof( CLOCK_STATE_RCD ));
    *dataBlob +=  sizeof( CLOCK_STATE_RCD );

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveTwo::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void *)&clock_table, *dataBlob, sizeof( CLOCK_STATE_RCD ));
    *dataBlob +=  sizeof( CLOCK_STATE_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveTwo::printResult( const string& deviceName )
{
 
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<"  Std Table 52 ========================" << endl;
        dout << " ** Clock Table ** "<<endl;
        dout << "          Clock Calendar - year "<<(int)getClkCldrYear()<<endl;
        dout << "                        - month "<<(int)getClkCldrMon()<<endl;
        dout << "                          - day "<<(int)getClkCldrDay()<<endl;
        dout << "                         - hour "<<(int)getClkCldrHour()<<endl;
        dout << "                       - minute "<<(int)getClkCldrMin()<<endl;
        dout << "                       - second "<<(int)getClkCldrSec()<<endl;
        dout << "          Time Date Quality - day of the week "<<(int)clock_table.time_date_qual.day_of_week<<endl;
        dout << "                      - daylight savings flag "<<(bool)clock_table.time_date_qual.dst_flag<<endl;
        dout << "                    - greenwich meantime flag "<<(bool)clock_table.time_date_qual.gmt_flag<<endl;
        dout << "                     - time zone applied flag "<<(bool)clock_table.time_date_qual.tm_zn_applied_flag<<endl;
        dout << "         - daylight savings time applied flag "<<(bool)clock_table.time_date_qual.dst_applied_flag<<endl;
        dout << "         - TIME  "<<CtiTime(clock_table.clock_calendar)<<endl;

        /* switch (_timefmt) 
        {
        case 1:
            {
                break;
            }
            case 2:
            {
                break;
            }
            case 3:
            {
                break;
            }
            case 4:
            {
                break;
            }
        }*/
    }


}

int CtiAnsiTableFiveTwo::getClkCldrYear()
{
    return (int)CtiDate(CtiTime(clock_table.clock_calendar)).year();
}

int CtiAnsiTableFiveTwo::getClkCldrMon()
{
    return (int)CtiDate(CtiTime(clock_table.clock_calendar)).month();
}

int CtiAnsiTableFiveTwo::getClkCldrDay()
{
    return (int)CtiDate(CtiTime(clock_table.clock_calendar)).dayOfMonth();
}

int CtiAnsiTableFiveTwo::getClkCldrHour()
{
    return (int)CtiTime(clock_table.clock_calendar).hour();
}

int CtiAnsiTableFiveTwo::getClkCldrMin()
{
    return (int)CtiTime(clock_table.clock_calendar).minute();
}

int CtiAnsiTableFiveTwo::getClkCldrSec()
{
    return (int)CtiTime(clock_table.clock_calendar).second();
}

ULONG CtiAnsiTableFiveTwo::getMeterServerTimeDifference()
{
    return _meterServerTimeDifference;
}

bool CtiAnsiTableFiveTwo::adjustTimeForDST()
{
    if ((bool)clock_table.time_date_qual.dst_applied_flag && (bool)clock_table.time_date_qual.dst_flag)
        return true;
    else
        return false;
}

