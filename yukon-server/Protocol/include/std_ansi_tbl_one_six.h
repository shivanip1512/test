
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_SIX_H__
#define __STD_ANSI_TBL_ONE_SIX_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_six
*
* Class:
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_six.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/09/30 21:37:20 $
*    History: 
      $Log: std_ansi_tbl_one_six.h,v $
      Revision 1.5  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:54  dsutton
      Last known revision DLS

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


class IM_EX_PROT CtiAnsiTableOneSix : public CtiAnsiTableBase
{
public:

   CtiAnsiTableOneSix( int num_sources );
   CtiAnsiTableOneSix( BYTE *dataBlob, int num_sources );
   virtual ~CtiAnsiTableOneSix();
   CtiAnsiTableOneSix& operator=(const CtiAnsiTableOneSix& aRef);


   SOURCE_LINK_BFLD getSourceLink ( int aOffset );
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
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

#endif // #ifndef __STD_ANSI_TBL_ONE_SIX_H__
