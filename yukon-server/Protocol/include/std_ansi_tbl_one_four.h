
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_FOUR_H__
#define __STD_ANSI_TBL_ONE_FOUR_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_four
*
* Class:
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_four.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:20 $
*    History: 
      $Log: std_ansi_tbl_one_four.h,v $
      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

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

class IM_EX_PROT CtiAnsiTableOneFour : public CtiAnsiTableBase
{
protected:

   struct DATA_RCD
   {
      unsigned char     *source_id;
   };

   struct DATA_CONTROL_RCD
   {
      DATA_RCD          *data_rcd;
   };

   DATA_CONTROL_RCD  _data_control_record;


private:

   int         _controlLength;
   int         _controlEntries;

public:

   CtiAnsiTableOneFour( int dataCtrlLen, int numDataCtrlEntries );
   CtiAnsiTableOneFour( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries );
   virtual ~CtiAnsiTableOneFour();
   CtiAnsiTableOneFour& operator=(const CtiAnsiTableOneFour& aRef);
   void printResult(  );
   
   void decodeResultPiece( BYTE **dataBlob );
   void generateResultPiece( BYTE **dataBlob );


};

#endif // #ifndef __STD_ANSI_TBL_ONE_FOUR_H__
