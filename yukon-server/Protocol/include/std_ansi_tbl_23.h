/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_23
*
* Class:
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_23.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_two_three.h,v $
      Revision 1.10  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.9  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.8  2005/12/20 17:20:01  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7  2005/12/12 20:34:48  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.6  2005/09/29 21:19:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.5  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.4  2004/09/30 21:37:21  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_23_H__
#define __STD_ANSI_TBL_23_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

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
   bool                 _lsbDataOrder;

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
   CtiAnsiTable23( int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, int tableNbr, bool lsbDataOrder = true );
   CtiAnsiTable23( BYTE *dataBlob, int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, int tableNbr,  bool lsbDataOrder = true);
   virtual ~CtiAnsiTable23();
   CtiAnsiTable23& operator=(const CtiAnsiTable23& aRef);
   void printResult( const std::string& deviceName );
   void printSummations( DATA_BLK_RCD data_block );
   void printDemands( DATA_BLK_RCD data_block );
   void printCoincidents( DATA_BLK_RCD data_block );

   void decodeResultPiece( BYTE **dataBlob );
   void generateResultPiece( BYTE **dataBlob );
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

#endif // #ifndef __STD_ANSI_TBL_23_H__
