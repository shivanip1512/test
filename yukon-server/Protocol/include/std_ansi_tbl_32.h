#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct DISP_SOURCE_DESC_RCD
{
    UINT8 *displaySource;
};

/*struct DISPLAY_SOURCE_RCD
{
    DISP_SOURCE_DESC_RCD  *displaySources;
}; */

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable32 : public CtiAnsiTableBase
{
protected:

  //DISPLAY_SOURCE_RCD *_displaySourceTable;
    DISP_SOURCE_DESC_RCD  *_displaySources;


private:

    UINT16 _nbrDispSources;
    UINT8 _widthDispSources;

public:

   CtiAnsiTable32( );
   CtiAnsiTable32( BYTE *dataBlob, UINT16 nbrDispSources, UINT8 widthDispSources );
   virtual ~CtiAnsiTable32();
   CtiAnsiTable32& operator=(const CtiAnsiTable32& aRef);
   void printResult( const std::string& deviceName );

   UINT8 getDisplaySources(int sourceIndex, int widthIndex);
};
