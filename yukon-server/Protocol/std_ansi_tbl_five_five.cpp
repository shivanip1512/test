
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_five
*
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_five_five.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:17 $
*
*    History: 
      $Log: std_ansi_tbl_five_five.cpp,v $
      Revision 1.4  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_five_five.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveFive::CtiAnsiTableFiveFive( BYTE *dataBlob )
{
   memcpy( (void *)&_clockStateTbl, dataBlob, sizeof( CLOCK_55_STATE_RCD ));
   dataBlob +=  sizeof( CLOCK_55_STATE_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveFive::~CtiAnsiTableFiveFive()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableFiveFive& CtiAnsiTableFiveFive::operator=(const CtiAnsiTableFiveFive& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveFive::generateResultPiece( BYTE **dataBlob )
{
    memcpy( *dataBlob, (void *)&_clockStateTbl, sizeof( CLOCK_55_STATE_RCD ));
    *dataBlob +=  sizeof( CLOCK_55_STATE_RCD );

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveFive::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void *)&_clockStateTbl, *dataBlob, sizeof( CLOCK_55_STATE_RCD ));
    *dataBlob +=  sizeof( CLOCK_55_STATE_RCD );
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableFiveFive::printResult(  )
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
        dout << endl << "=======================  Std Table 55  ========================" << endl;
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


