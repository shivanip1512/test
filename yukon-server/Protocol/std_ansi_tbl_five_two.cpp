
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:17 $
*
*    History: 
      $Log: std_ansi_tbl_five_two.cpp,v $
      Revision 1.4  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_five_two.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableFiveTwo::CtiAnsiTableFiveTwo( int timefmat )
{
    _timefmt = timefmat;
}


CtiAnsiTableFiveTwo::CtiAnsiTableFiveTwo( BYTE *dataBlob, int timefmat )
{
    int offset, bytes = 0;
    _timefmt = timefmat;
      
    // Clock Calendar - LTIME_DATE
    bytes = toUint32LTime( dataBlob, clock_table.clock_calendar, _timefmt );
    dataBlob += bytes;
    offset += bytes;

    memcpy( (void *)&clock_table.time_date_qual, dataBlob, sizeof( TIME_DATE_QUAL_BFLD ));
    dataBlob +=  sizeof( TIME_DATE_QUAL_BFLD );







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
void CtiAnsiTableFiveTwo::printResult(  )
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
        dout << endl << "=======================  Std Table 52 ========================" << endl;
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
        dout << "         - TIME  "<<RWTime(clock_table.clock_calendar)<<endl;

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
    return (int)RWDate(RWTime(clock_table.clock_calendar)).year();
}

int CtiAnsiTableFiveTwo::getClkCldrMon()
{
    return (int)RWDate(RWTime(clock_table.clock_calendar)).month();
}

int CtiAnsiTableFiveTwo::getClkCldrDay()
{
    return (int)RWDate(RWTime(clock_table.clock_calendar)).dayOfMonth();
}

int CtiAnsiTableFiveTwo::getClkCldrHour()
{
    return (int)RWTime(clock_table.clock_calendar).hour();
}

int CtiAnsiTableFiveTwo::getClkCldrMin()
{
    return (int)RWTime(clock_table.clock_calendar).minute();
}

int CtiAnsiTableFiveTwo::getClkCldrSec()
{
    return (int)RWTime(clock_table.clock_calendar).second();
}



