
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_APPLICATION_H__
#define __TRANSDATA_APPLICATION_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_application
*
* Class:
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/10/06 15:19:00 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <vector>

#include "xfer.h"
#include "utility.h"
#include "transdata_tracker.h"
#include "transdata_data.h"

class IM_EX_PROT CtiTransdataApplication
{
   enum
   {
      doLogOn = 0,
      doTalk,
      doLogOff
   };
/*
   struct Channel
   {
      FLOAT    totalUsage_A;
      FLOAT    totalUsage_B;
      FLOAT    totalUsage_C;
      FLOAT    totalUsage_D;
      FLOAT    totalUsage_All;
      ULONG    currentDemand;
      ULONG    peakDemand;
      ULONG    timeOfPeak;
      ULONG    dateOfPeak;
      ULONG    previousDemand;
   };


   struct Real
   {
      //
      // put some of the stuff from the orig (end of doc) back in later.....
      //
      Channel                       channel[8];

      CHAR                          deviceID[15];

      USHORT                        powerOutages;

      DOUBLE                        prevIntervalDemand;

      ULONG                         serialNumber;

      FLOAT                         kFactor;
      USHORT                        demandInterval;

      DOUBLE                        maxkM3;
      FLOAT                         powerFactorAtMaxkM3;
      DOUBLE                        coincidentkM3atMaxDemand;
      DOUBLE                        totalkMh3;

   };
*/
   enum
   {
      powerFactor    = 71,
      phaseAVoltage  = 505,
      phaseBVoltage,
      phaseCVoltage,
      phaseAAmpere,
      phaseBAmpere,
      phaseCAmpere,
      phase3Watt,
      phase3Var      = 516
   };

   //need to add ch5-8
   enum
   {
      ch1_TotalUsage       = 5,
      ch1_CurrentDemand,
      ch1_PeakDemand,
      ch1_TimePeak,
      ch1_PreviousDemand,
      ch1_DateOfPeak       = 300,
      ch1_RecorderValidRead= 363,

      ch2_TotalUsage       = 13,
      ch2_CurrentDemand,
      ch2_PeakDemand,
      ch2_TimePeak,
      ch2_PreviousDemand,
      ch2_DateOfPeak       = 301,
      ch2_RecorderValidRead= 364,

      ch3_TotalUsage       = 21,
      ch3_CurrentDemand,
      ch3_PeakDemand,
      ch3_TimePeak,
      ch3_PreviousDemand,
      ch3_DateOfPeak       = 302,
      ch3_RecorderValidRead= 365,

      ch4_TotalUsage       = 29,
      ch4_CurrentDemand,
      ch4_PeakDemand,
      ch4_TimePeak,
      ch4_PreviousDemand,
      ch4_DateOfPeak       = 303,
      ch4_RecorderValidRead= 366,
   };

   enum
   {
      ch1_TotalUsage_A     = 105,
      ch1_TotalUsage_B     = 129,
      ch1_TotalUsage_C     = 153,
      ch1_TotalUsage_D     = 177,
      ch1_TotalUsage_All   = 5,

      ch1_PeakDemand_A     = 106,
      ch1_PeakDemand_B     = 130,
      ch1_PeakDemand_C     = 154,
      ch1_PeakDemand_D     = 178,

      ch1_DateOfPeak_A     = 315,
      ch1_DateOfPeak_B     = 316,
      ch1_DateOfPeak_C     = 317,
      ch1_DateOfPeak_D     = 318,

      ch1_TimeOfPead_A     = 107,
      ch1_TimeOfPead_B     = 131,
      ch1_TimeOfPead_C     = 155,
      ch1_TimeOfPead_D     = 179,

      ch2_TotalUsage_A     = 109,
      ch2_TotalUsage_B     = 133,
      ch2_TotalUsage_C     = 157,
      ch2_TotalUsage_D     = 181,
      ch2_TotalUsage_All   = 13,

      ch2_PeakDemand_A     = 110,
      ch2_PeakDemand_B     = 134,
      ch2_PeakDemand_C     = 158,
      ch2_PeakDemand_D     = 182,

      ch2_DateOfPeak_A     = 323,
      ch2_DateOfPeak_B     = 324,
      ch2_DateOfPeak_C     = 325,
      ch2_DateOfPeak_D     = 326,

      ch2_TimeOfPead_A     = 111,
      ch2_TimeOfPead_B     = 135,
      ch2_TimeOfPead_C     = 159,
      ch2_TimeOfPead_D     = 183,

      ch3_TotalUsage_A     = 113,
      ch3_TotalUsage_B     = 137,
      ch3_TotalUsage_C     = 161,
      ch3_TotalUsage_D     = 185,
      ch3_TotalUsage_All   = 21,

      ch3_PeakDemand_A     = 114,
      ch3_PeakDemand_B     = 138,
      ch3_PeakDemand_C     = 162,
      ch3_PeakDemand_D     = 186,

      ch3_DateOfPeak_A     = 331,
      ch3_DateOfPeak_B     = 332,
      ch3_DateOfPeak_C     = 333,
      ch3_DateOfPeak_D     = 334,

      ch3_TimeOfPead_A     = 115,
      ch3_TimeOfPead_B     = 139,
      ch3_TimeOfPead_C     = 163,
      ch3_TimeOfPead_D     = 187,

      ch4_TotalUsage_A     = 117,
      ch4_TotalUsage_B     = 141,
      ch4_TotalUsage_C     = 165,
      ch4_TotalUsage_D     = 189,
      ch4_TotalUsage_All   = 29,

      ch4_PeakDemand_A     = 118,
      ch4_PeakDemand_B     = 142,
      ch4_PeakDemand_C     = 166,
      ch4_PeakDemand_D     = 190,

      ch4_DateOfPeak_A     = 339,
      ch4_DateOfPeak_B     = 340,
      ch4_DateOfPeak_C     = 341,
      ch4_DateOfPeak_D     = 342,

      ch4_TimeOfPead_A     = 119,
      ch4_TimeOfPead_B     = 143,
      ch4_TimeOfPead_C     = 167,
      ch4_TimeOfPead_D     = 191,
   };


   public:

      CtiTransdataApplication();
      ~CtiTransdataApplication();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      void injectData( RWCString str );

      void setNextState( void );
      bool processData( BYTE *data, int numBytes );

      int getError( void );
      void destroyMe( void );
      void reinitalize( void );

      int retreiveData( BYTE *data );

//      vector<CtiTransdataData *> getConverted( void );

   protected:

   private:

      CtiTransdataTracker  _tracker;

      int                  _lastState;
      int                  _numBytes;

      bool                 _finished;

//      vector<CtiTransdataData *>    _transVector;

//      CtiTransdataData     *_converted;

      BYTE                 *_storage;
};

#endif // #ifndef __TRANSDATA_APPLICATION_H__

