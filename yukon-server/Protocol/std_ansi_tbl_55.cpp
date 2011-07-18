/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_55
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_55.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*
*    History:
      $Log: std_ansi_tbl_five_five.cpp,v $
      Revision 1.8  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.7  2005/12/20 17:19:56  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6  2005/12/12 20:34:28  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5.4.1  2005/12/12 19:50:39  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.4  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_table_55.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable055::CtiAnsiTable055( BYTE *dataBlob )
{
   memcpy( (void *)&_clockStateTbl, dataBlob, sizeof( CLOCK_55_STATE_RCD ));
   dataBlob +=  sizeof( CLOCK_55_STATE_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable55::~CtiAnsiTable55()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable55& CtiAnsiTable55::operator=(const CtiAnsiTable55& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable55::generateResultPiece( BYTE **dataBlob )
{
    memcpy( *dataBlob, (void *)&_clockStateTbl, sizeof( CLOCK_55_STATE_RCD ));
    *dataBlob +=  sizeof( CLOCK_55_STATE_RCD );

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable55::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void *)&_clockStateTbl, *dataBlob, sizeof( CLOCK_55_STATE_RCD ));
    *dataBlob +=  sizeof( CLOCK_55_STATE_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable55::printResult( const string& deviceName )
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
        dout << endl << "================== "<<deviceName<<"  Std Table 55  ========================" << endl;
        dout << " ** Clock State Table ** "<<endl;
        dout << "          Clock Calendar ---yy.mm.dd hh:mm:ss  "<<(int)_clockStateTbl.clock_calendar.cases.c2.year<<".";
        dout <<(int)_clockStateTbl.clock_calendar.cases.c2.month<<"."<<(int)_clockStateTbl.clock_calendar.cases.c2.day<<" ";
        dout <<(int)_clockStateTbl.clock_calendar.cases.c2.hour<<":"<<(int)_clockStateTbl.clock_calendar.cases.c2.minute<<":";
        dout <<(int)_clockStateTbl.clock_calendar.cases.c2.second<<endl;
        dout << "          Time Date Quality - day of the week "<<(int)_clockStateTbl.time_date.day_of_week<<endl;
        dout << "                      - daylight savings flag "<<(bool)_clockStateTbl.time_date.dst_flag<<endl;
        dout << "                    - greenwich meantime flag "<<(bool)_clockStateTbl.time_date.gmt_flag<<endl;
        dout << "                     - time zone applied flag "<<(bool)_clockStateTbl.time_date.tm_zn_applied_flag<<endl;
        dout << "                           - DST applied flag "<<(bool)_clockStateTbl.time_date.dst_applied_flag<<endl;

        dout << "          Status - curr tier  "<<(int)_clockStateTbl.status.u.s2.curr_tier<<endl;
        dout << "                 - tier drive "<<(int)_clockStateTbl.status.tier_drive<<endl;
        dout << "        - special schd active "<<(int)_clockStateTbl.status.special_schd_active<<endl;
        dout << "                     - season "<<(int)_clockStateTbl.status.season<<endl;



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


