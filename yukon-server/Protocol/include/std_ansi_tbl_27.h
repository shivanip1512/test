#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"


#define UINT16             __int16
#define UINT8               __int8

class IM_EX_PROT CtiAnsiTable27 : public CtiAnsiTableBase
{
  unsigned char *_presentDemandSelect;
  unsigned char *_presentValueSelect;

    UINT8 _nbrPresentDemands;
    UINT8 _nbrPresentValues;

public:

   CtiAnsiTable27(  );
   CtiAnsiTable27( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues );
   virtual ~CtiAnsiTable27();
   CtiAnsiTable27& operator=(const CtiAnsiTable27& aRef);
   void printResult( const std::string& deviceName );

   unsigned char* getDemandSelect( );
   unsigned char* getValueSelect( );

};
