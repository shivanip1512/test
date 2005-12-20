
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_THREE_H__
#define __STD_ANSI_TBL_ONE_THREE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_three
*
* Class:
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_three.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:20:00 $
*    History: 
      $Log: std_ansi_tbl_one_three.h,v $
      Revision 1.7  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5.2.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2005/09/29 21:19:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

union INT_CONTROL_RCD
{
   struct CONTROL_RECORD
   {
      unsigned char  sub_int;
      unsigned char  int_mulitplier;
   }cntl_rec;
   USHORT  int_length;
};

struct DEMAND_CONTROL_RCD
{
   unsigned char  reset_exclusion;

   struct PF_EXCLUDE
   {
      unsigned char  p_fail_recogntn_tm;
      unsigned char  p_fail_exclusion;
      unsigned char  cold_load_pickup;
   }excludes;

   INT_CONTROL_RCD   *_int_control_rec;
};


#pragma pack( pop )


class IM_EX_PROT CtiAnsiTableOneThree : public CtiAnsiTableBase
{

public:

   CtiAnsiTableOneThree( int num_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude );
   CtiAnsiTableOneThree( BYTE *dataBlob, int num_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude );
   virtual ~CtiAnsiTableOneThree();
   CtiAnsiTableOneThree& operator=(const CtiAnsiTableOneThree& aRef);
   void printResult( const string& deviceName );
   
   void decodeResultPiece( BYTE **dataBlob );
   void generateResultPiece( BYTE **dataBlob );


   bool getPFExcludeFlag();
   bool getSlidingDemandFlag();
   bool getResetExcludeFlag();
   /*int getResetExclusion();
   int getPFailRecogntnTm();
   int getPFailExclusion();
   int getColdLoadPickup();
   int getCtrlRecordSubInt();
   int getCtrlRecordIntMultiplier();
   int getCtrlRecordIntLength();
    */

private:

    int                  _numberDemandCtrlEntries;
    bool                 _pfExcludeFlag;
    bool                 _slidingDemandFlag; 
    bool                 _resetExcludeFlag;

    DEMAND_CONTROL_RCD   _demand_control_record;


};

#endif // #ifndef __STD_ANSI_TBL_ONE_THREE_H__
