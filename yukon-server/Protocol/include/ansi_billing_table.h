#pragma once

#include "dlldefs.h"
#include "dsm2.h"

#include "types.h"
#include "std_ansi_tbl_base.h"

#define BCD unsigned char

#pragma pack( push, 1)

   struct BILLING_TABLE
   {
      //some sizes to help us on the scanner side
      int      billing_table_size;
      int      size_demand_select;
      int      size_tot_data_block;

      //from tbl 21
      int      nbr_summations;
      int      nbr_demands;
      int      nbr_coincidents;
      int      nbr_occurances;

      int      nbr_tiers;
      bool     demand_reset_flag;
      bool     time_data_flag;
      bool     cum_demand_flag;
      bool     cont_cum_demand_flag;
/*
      //from tbl 12
      int      id_code;
      int      time_base;
      int      multiplier;
*/
      //from tbl 11
      int      nbr_uom_entries;
      int      nbr_demand_ctrl_entries;
      int      data_ctrl_length;
      int      nbr_data_ctrl_entries;
      int      nbr_constants_entries;

      //from tbl 00
      int      tm_format;
      int      int_format;
      int      ni_format1;
      int      ni_format2;
   };

#pragma pack( pop )

class IM_EX_PROT CtiAnsiBillingTable : public CtiAnsiTableBase
{
protected:

   BILLING_TABLE    _billingTable;

private:

public:

   int copyDataOut( BYTE *dest );
   int getTableSize( void );

   int getDemandSelectSize( void );
   int getTotDataBlockSize( void );
   int getNumSummations( void );
   int getNumDemands( void );
   int getNumCoins( void );
   int getNumOccurs( void );
   int getNumUOMEntries( void );
   int getNumDemandCntlEntries( void );
   int getDataCntlLength( void );
   int getNumDataCntlEntries( void );
   int getConstantEntries( void );
   int getTmFormat( void );
   int getIntFormat( void );
   int getNiFormat1( void );
   int getNiFormat2( void );
   int getNumTiers( void );

   bool getDemandResetFlag( void );
   bool getTimeDateFlag( void );
   bool getCumDemandFlag( void );
   bool getContCumDemandFlag( void );

   CtiAnsiBillingTable( int demSelSize, int totDataBlkSize, int numSums, int numDemds, int numCoins, int numOccurs, int uom, int demandCntl,
                        int dataCntlLen, int dataCntl, int constants, int tFormat, int iFormat, int ni1Format, int ni2Format, int nbrtiers,
                        bool dreset, bool timedata, bool cumdemand, bool contcumdemand );

   CtiAnsiBillingTable( BYTE *blob );

   virtual ~CtiAnsiBillingTable();

   CtiAnsiBillingTable& operator=(const CtiAnsiBillingTable& aRef);
};



