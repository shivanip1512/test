#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable22 : public CtiAnsiTableBase
{
   unsigned char        *_summation_select;
   unsigned char        *_demand_select;
   unsigned char        *_set;
   unsigned char        *_coincident_select;
   unsigned char        *_coin_demand_assoc;

   int   _demandSelectSize;
   int   _totalTableSize;
   int   _numSums;
   int   _numDemands;
   int   _numCoins;

public:

   int copyDemandSelect( BYTE *dest );
   int getDemandSelectSize( void );
   int getTotalTableSize( void );
   unsigned char* getDemandSelect( void );
   unsigned char* getSummationSelect( void );

   CtiAnsiTable22( BYTE *dataBlob, int num_sums, int num_demands, int num_coins );
   virtual ~CtiAnsiTable22();
   CtiAnsiTable22& operator=(const CtiAnsiTable22& aRef);

   void printResult( const std::string& deviceName);
};
