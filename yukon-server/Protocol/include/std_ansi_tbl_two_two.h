
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_TWO_TWO_H__
#define __STD_ANSI_TBL_TWO_TWO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_two
*
* Class:
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_two_two.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/12 20:34:48 $
*    History: 
      $Log: std_ansi_tbl_two_two.h,v $
      Revision 1.6  2005/12/12 20:34:48  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5.6.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2004/09/30 21:37:21  jrichter
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

#pragma pack( push, 1)


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableTwoTwo : public CtiAnsiTableBase
{
protected:

   unsigned char        *_summation_select;
   unsigned char        *_demand_select;
   unsigned char        *_set;
   unsigned char        *_coincident_select;
   unsigned char        *_coin_demand_assoc;


private:

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

   CtiAnsiTableTwoTwo(int num_sums, int num_demands, int num_coins );
   CtiAnsiTableTwoTwo( BYTE *dataBlob, int num_sums, int num_demands, int num_coins );
   virtual ~CtiAnsiTableTwoTwo();
   CtiAnsiTableTwoTwo& operator=(const CtiAnsiTableTwoTwo& aRef);
   
   void generateResultPiece( BYTE **dataBlob );
   void printResult(RWCString deviceName);
   void decodeResultPiece( BYTE **dataBlob );


};

#endif // #ifndef __STD_ANSI_TBL_TWO_TWO_H__
