/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_13
*
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_13.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_three.cpp,v $
      Revision 1.9  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.6  2005/09/29 21:18:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.5  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_13.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable13::CtiAnsiTable13( int nbr_demand_cntl_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude )
{
   _numberDemandCtrlEntries = nbr_demand_cntl_entries;
   _pfExcludeFlag = pf_exclude;
   _slidingDemandFlag = sliding_demand;
   _resetExcludeFlag = reset_exclude;
   /*if (reset_exclude)
   {
       _demand_control_record->reset_exclusion = 1;
   }
   if (pf_exclude)
   {
       _demand_control_record->excludes.p_fail_recogntn_tm = 1;
       _demand_control_record->excludes.p_fail_exclusion = 1;
       _demand_control_record->excludes.cold_load_pickup = 1;
   }
   */
   _demand_control_record._int_control_rec = new INT_CONTROL_RCD[nbr_demand_cntl_entries];


}
/*CtiAnsiTableOneTwo::CtiAnsiTableOneTwo( BYTE *dataBlob, int num_uom_entries )
{
   int index;

   _numUomEntries = num_uom_entries;
   _uom_entries = new UOM_ENTRY_BFLD[num_uom_entries];

   for( index = 0; index < num_uom_entries; index++ )
   {
      memcpy( (void *)&_uom_entries[index], dataBlob, sizeof( UOM_ENTRY_BFLD ));
      dataBlob += sizeof( UOM_ENTRY_BFLD );
   }

} */

CtiAnsiTable13::CtiAnsiTable13( BYTE *dataBlob, int nbr_demand_cntl_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude )
{
   int      index;

   _pfExcludeFlag = pf_exclude;
   _slidingDemandFlag = sliding_demand;
   _resetExcludeFlag = reset_exclude;

   if( reset_exclude != false )
   {
      memcpy( (void *)&_demand_control_record.reset_exclusion, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }

   if( pf_exclude != false )
   {
      memcpy( (void *)&_demand_control_record.excludes.p_fail_recogntn_tm, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );

      memcpy( (void *)&_demand_control_record.excludes.p_fail_exclusion, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );

      memcpy( (void *)&_demand_control_record.excludes.cold_load_pickup, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char );
   }


   dataBlob += sizeof( unsigned char );
   _numberDemandCtrlEntries = nbr_demand_cntl_entries;
   _demand_control_record._int_control_rec = new INT_CONTROL_RCD[nbr_demand_cntl_entries];

   for( index = 0; index < _numberDemandCtrlEntries; index++ )
   {
      if( sliding_demand != false )
      {
         memcpy( (void *)&_demand_control_record._int_control_rec[index].cntl_rec.sub_int, dataBlob, sizeof( unsigned char ));
         dataBlob += sizeof( unsigned char );

         memcpy( (void *)&_demand_control_record._int_control_rec[index].cntl_rec.int_mulitplier, dataBlob, sizeof( unsigned char ));
         dataBlob += sizeof( unsigned char );
      }
      else
      {
         memcpy( (void *)&_demand_control_record._int_control_rec[index].int_length, dataBlob, sizeof( unsigned char) *2 );
         dataBlob += ( sizeof( unsigned char) *2 );
      }
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable13::~CtiAnsiTable13()
{
    if (_demand_control_record._int_control_rec != NULL)
    {
        delete []_demand_control_record._int_control_rec;
        _demand_control_record._int_control_rec = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable13& CtiAnsiTable13::operator=(const CtiAnsiTable13& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

void CtiAnsiTable13::decodeResultPiece( BYTE **dataBlob )
{
   if( _resetExcludeFlag != false )
   {
      memcpy( (void *)&_demand_control_record.reset_exclusion, *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }
   /*else
      _demand_control_record->reset_exclusion = 500;      //invalid value
     */
   if( _pfExcludeFlag != false )
   {
      memcpy( (void *)&_demand_control_record.excludes.p_fail_recogntn_tm, *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );

      memcpy( (void *)&_demand_control_record.excludes.p_fail_exclusion, *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );

      memcpy( (void *)&_demand_control_record.excludes.cold_load_pickup, *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }

   //*dataBlob += sizeof( unsigned char );
  /* else
   {
      //default invalid vals
      _demand_control_record->excludes.p_fail_recogntn_tm = 500;
      _demand_control_record->excludes.p_fail_exclusion = 500;
      _demand_control_record->excludes.cold_load_pickup = 500;
   }*/

   _demand_control_record._int_control_rec = new INT_CONTROL_RCD[_numberDemandCtrlEntries];
   //memcpy( (void *)&_demand_control_record->_int_control_rec, *dataBlob, sizeof( USHORT ) * _numberDemandCtrlEntries);
   //*dataBlob += (sizeof( USHORT ) * _numberDemandCtrlEntries);


   for( int index = 0; index < _numberDemandCtrlEntries; index++ )
   {
      if( _slidingDemandFlag != false )
      {
         memcpy( (void *)&_demand_control_record._int_control_rec[index].cntl_rec.sub_int, *dataBlob, sizeof( unsigned char ));
         *dataBlob += sizeof( unsigned char );

         memcpy( (void *)&_demand_control_record._int_control_rec[index].cntl_rec.int_mulitplier, *dataBlob, sizeof( unsigned char ));
         *dataBlob += sizeof( unsigned char );
      }
      else
      {
         memcpy( (void *)&_demand_control_record._int_control_rec[index].int_length, *dataBlob, sizeof( unsigned char) *2 );
         *dataBlob += ( sizeof( unsigned char) *2 );
      }
   }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable13::generateResultPiece( BYTE **dataBlob )
{
   if( _resetExcludeFlag != false )
   {
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "_resetExcludeFlag != false" << endl;
    }
      memcpy(*dataBlob, (void *)&_demand_control_record.reset_exclusion, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }
   if( _pfExcludeFlag != false )
   {
       {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "_pfExcludeFlag != false" << endl;
    }
      memcpy( *dataBlob, (void *)&_demand_control_record.excludes.p_fail_recogntn_tm, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );

      memcpy( *dataBlob, (void *)&_demand_control_record.excludes.p_fail_exclusion, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );

      memcpy( *dataBlob, (void *)&_demand_control_record.excludes.cold_load_pickup, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char );
   }
   for( int index = 0; index < _numberDemandCtrlEntries; index++ )
   {
      if( _slidingDemandFlag != false )
      {
          {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "_slidingDemandFlag != false" << endl;
    }

         memcpy( *dataBlob, (void *)&_demand_control_record._int_control_rec[index].cntl_rec.sub_int, sizeof( unsigned char ));
         *dataBlob += sizeof( unsigned char );

         memcpy( *dataBlob, (void *)&_demand_control_record._int_control_rec[index].cntl_rec.int_mulitplier, sizeof( unsigned char ));
         *dataBlob += sizeof( unsigned char );
      }
      else
      {
         memcpy( *dataBlob, (void *)&_demand_control_record._int_control_rec[index].int_length, sizeof( unsigned char) *2 );
         *dataBlob += ( sizeof( unsigned char) *2 );
      }
   }
}

//=========================================================================================================================================
//=========================================================================================================================================
bool CtiAnsiTable13::getPFExcludeFlag()
{
    return _pfExcludeFlag;
}
bool CtiAnsiTable13::getSlidingDemandFlag()
{
    return _slidingDemandFlag;
}
bool CtiAnsiTable13::getResetExcludeFlag()
{
    return _resetExcludeFlag;
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable13::printResult( const string& deviceName )
{
    int integer;
    string string1,string2;
    double double1;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "==================== "<<deviceName<<" Std Table 13 ========================" << endl;
    }

    if (_resetExcludeFlag)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   RESET_EXCLUSION:  " << _demand_control_record.reset_exclusion << endl;
    }
    if (_pfExcludeFlag)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   P_FAIL_RECOGNTN_TM:  " <<(int)_demand_control_record.excludes.p_fail_recogntn_tm << endl;
        dout << "   P_FAIL_EXCLUSION:  " <<(int)_demand_control_record.excludes.p_fail_exclusion << endl;
        dout << "   COLD_LOAD_PICKUP:  " << (int)_demand_control_record.excludes.cold_load_pickup << endl;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   INTERVAL_VALUE:  " << endl;
    }
    if (_slidingDemandFlag)
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "       INDEX       SUB_INT     INT_MULTIPLIER" << endl;
        }
        for (int x = 0; x < _numberDemandCtrlEntries; x++)
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "         "<<x<<"       "<<(int)_demand_control_record._int_control_rec[x].cntl_rec.sub_int<<"        "<<(int)_demand_control_record._int_control_rec[x].cntl_rec.int_mulitplier<< endl;
        }
    }
    else
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "       INDEX       INT_LENGTH  " << endl;
        }
        for (int x = 0; x < _numberDemandCtrlEntries; x++)
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "         "<<x<<"       "<<(int)_demand_control_record._int_control_rec[x].int_length<< endl;
        }
    }

}





