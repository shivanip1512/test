#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"
#include "logger.h"

#define BCD   unsigned char

#pragma pack( push, 1)


struct COINCIDENTS_RCD
{
   double               *coincident_values;
};

struct DEMANDS_RCD
{
//   STIME_DATE           *event_time;
   ULONG                *event_time;
   double               cum_demand;
   double               cont_cum_demand;
   double               *demand;
};

struct DATA_BLK_RCD
{
   double               *summations;
   DEMANDS_RCD          *demands;
   COINCIDENTS_RCD      *coincidents;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable23 : public CtiAnsiTableBase
{
protected:

   unsigned char        _nbr_demand_resets;
   DATA_BLK_RCD         _tot_data_block;
   DATA_BLK_RCD         *_tier_data_block;

private:

   int                  _totSize;
   int                  _ocNums;
   int                  _sumNums;
   int                  _demandNums;
   int                  _coinNums;
   int                  _tierNums;
   int                  _format1;
   int                  _format2;
   bool                 _reset;
   bool                 _time;
   bool                 _cumd;
   bool                 _cumcont;
   int                  _timefmt;
   DataOrder                 _dataOrder;

   int                  _tablePrintNumber;


public:

   DATA_BLK_RCD getTotDataBlock( void );

   int copyTotDataBlock( BYTE *ptr );

   int getTotSize( void );
   int getOccurs( void );
   int getSums( void );
   int getDemands( void );
   int getCoins( void );
   int getTier( void );
   bool getReset( void );
   bool getTime( void );
   bool getCumd( void );
   bool getCumcont( void );
   CtiAnsiTable23( BYTE *dataBlob, int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, int tableNbr,  DataOrder dataOrder = LSB);
   virtual ~CtiAnsiTable23();
   CtiAnsiTable23& operator=(const CtiAnsiTable23& aRef);
   void printResult( const std::string& deviceName );

   void appendResult      ( Cti::FormattedList &itemList );
   void appendSummations  ( DATA_BLK_RCD data_block, Cti::FormattedList &itemList );
   void appendDemands     ( DATA_BLK_RCD data_block, Cti::FormattedList &itemList );
   void appendCoincidents ( DATA_BLK_RCD data_block, Cti::FormattedList &itemList );

   void populateSummations( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset );
   void populateDemandsRecord(BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset);
   void populateCoincidentsRecord(BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset);
   void retrieveSummations( BYTE *dataBlob, DATA_BLK_RCD data_block, int &offset );
   void retrieveDemandsRecord(BYTE *dataBlob, DATA_BLK_RCD data_block, int &offset);
   void retrieveCoincidentsRecord(BYTE *dataBlob, DATA_BLK_RCD data_block, int &offset);


double getDemandValue ( int index, int dataBlock);
double getSummationsValue ( int index, int dataBlock);
double getDemandEventTime( int index, int dataBlock );

};
