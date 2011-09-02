#pragma once

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
