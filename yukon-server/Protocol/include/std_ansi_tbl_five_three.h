

#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_FIVE_THREE_H__
#define __STD_ANSI_TBL_FIVE_THREE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_five_three
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_five_three.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/10/01 17:54:27 $
*    History: 
      $Log: std_ansi_tbl_five_three.h,v $
      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

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

#define BCD                unsigned char

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableFiveThree : public CtiAnsiTableBase
{
protected:

   TIME_OFFSET_RCD      time_offset_table;

private:


public:

   CtiAnsiTableFiveThree(int timefmat  );
   CtiAnsiTableFiveThree( BYTE *dataBlob, int timefmat );
   
   virtual ~CtiAnsiTableFiveThree();

   CtiAnsiTableFiveThree& operator=(const CtiAnsiTableFiveThree& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );

};
#endif // #ifndef __STD_ANSI_TBL_FIVE_THREE_H__
