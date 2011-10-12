#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define INT16             __int16
#define INT8               __int8
#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct SETUP_STRINGS_RCD
{
    unsigned char *setup_string;
};
struct GLOBAL_PARMS_RCD
{
    UINT8 psem_identity;
    UINT32 bit_rate;
    SETUP_STRINGS_RCD *modem_setup_strings;
};


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable92 : public CtiAnsiTableBase
{
protected:

   GLOBAL_PARMS_RCD      _globalParmsTbl;

private:


public:

   CtiAnsiTable92(  );
   CtiAnsiTable92( BYTE *dataBlob );

   virtual ~CtiAnsiTable92();

   CtiAnsiTable92& operator=(const CtiAnsiTable92& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );
};
