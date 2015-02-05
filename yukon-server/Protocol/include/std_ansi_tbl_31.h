#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct DISP_FLAG_BFLD
{
    unsigned char  onTimeFlag:1;
    unsigned char  offTimeFlag:1;
    unsigned char  holdTimeFlag:1;
    unsigned char  filler:5;
};

struct DISP_RCD
{
    DISP_FLAG_BFLD displayCtrl;
    UINT16         nbrDispSources;
    UINT8          widthDispSources;
    UINT16         nbrPriDispListItems;
    UINT8          nbrPriDispLists;
    UINT16         nbrSecDispListItems;
    UINT8          nbrSecDispLists;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable31 : public CtiAnsiTableBase
{
  DISP_RCD _displayTable;

public:

   CtiAnsiTable31( BYTE *dataBlob, DataOrder dataOrder = LSB );

   virtual ~CtiAnsiTable31();
   CtiAnsiTable31& operator=(const CtiAnsiTable31& aRef);
   void printResult( const std::string& deviceName );

   UINT16 getNbrDispSources( );
   UINT8  getWidthDispSources( );
   UINT16 getNbrPriDispListItems( );
   UINT8 getNbrPriDispLists( );
   UINT16 getNbrSecDispListItems( );
   UINT8  getNbrSecDispLists( );

   bool  getOnTimeFlag( );
   bool  getOffTimeFlag( );
   bool  getHoldTimeFlag( );
};
