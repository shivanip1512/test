/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_16
*
* Class:
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_16.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_one_six.h,v $
      Revision 1.9  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.7  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5.6.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:54  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_16_H__
#define __STD_ANSI_TBL_16_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"

#include "std_ansi_tbl_base.h"

#define ANSI_TBL16_UOM_FLAG                     0x01
#define ANSI_TBL16_DEMAND_CTRL_FLAG             0x02
#define ANSI_TBL16_DATA_CTRL_FLAG               0x04
#define ANSI_TBL16_CONSTANTS_FLAG               0x08
#define ANSI_TBL16_PULSE_ENGR_FLAG              0x10
#define ANSI_TBL16_CONSTANT_TO_BE_APPLIED_FLAG  0x20

#pragma pack( push, 1)

struct SOURCE_LINK_BFLD
{
   unsigned char     uom_entry_flag          :1;
   unsigned char     demand_ctrl_flag        :1;
   unsigned char     data_ctrl_flag          :1;
   unsigned char     constants_flag          :1;
   unsigned char     pulse_engr_flag         :1;
   unsigned char     constant_to_be_applied  :1;
   unsigned char     filler                  :2;
};


#pragma pack( pop )


class IM_EX_PROT CtiAnsiTable16 : public CtiAnsiTableBase
{
public:

   CtiAnsiTable16( int num_sources );
   CtiAnsiTable16( BYTE *dataBlob, int num_sources );
   virtual ~CtiAnsiTable16();
   CtiAnsiTable16& operator=(const CtiAnsiTable16& aRef);


   SOURCE_LINK_BFLD getSourceLink ( int aOffset );
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );
   bool getUOMEntryFlag( int index );
   bool getDemandCtrlFlag( int index );
   bool getDataCtrlFlag( int index );
   bool getConstantsFlag( int index );
   bool getPulseEngrFlag( int index );
   bool getConstToBeAppliedFlag( int index );



private:
    SOURCE_LINK_BFLD  *_source_link;
    int _numberOfConstants;

};

#endif // #ifndef __STD_ANSI_TBL_16_H__
