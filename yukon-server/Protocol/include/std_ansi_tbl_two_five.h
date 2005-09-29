

#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_TWO_FIVE_H__
#define __STD_ANSI_TBL_TWO_FIVE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_five
*
* Class:
* Date:   7/28/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_two_five.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/29 21:19:47 $
*    History: 
      $Log: std_ansi_tbl_two_five.h,v $
      Revision 1.2  2005/09/29 21:19:47  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.1.2.1  2005/08/01 17:08:07  jrichter
      added frozen register retreival functionality.  cleanup.

      
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"
#include "std_ansi_tbl_two_three.h"

#define BCD   unsigned char

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableTwoFive : public CtiAnsiTableBase
{
protected:


private:

    BOOL _dateTimeFieldFlag;
    BOOL _seasonInfoFieldFlag;

    ULONG _endDateTime;
    unsigned char _season;

    CtiAnsiTableTwoThree *_prevDemandResetData;

public:

   //CtiAnsiTableTwoFive( BOOL dateTimeFieldFlag, BOOL seasonInfoFieldFlag );
   //CtiAnsiTableTwoFive( BYTE *dataBlob, BOOL dateTimeFieldFlag, BOOL seasonInfoFieldFlag );
   CtiAnsiTableTwoFive( int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, bool season );
   CtiAnsiTableTwoFive( BYTE *dataBlob, int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, bool season );

   virtual ~CtiAnsiTableTwoFive();
   CtiAnsiTableTwoFive& operator=(const CtiAnsiTableTwoFive& aRef);

   CtiAnsiTableTwoThree *getDemandResetDataTable( );
   double getEndDateTime();
   unsigned char getSeason();

   void printResult(  );
   


};

#endif // #ifndef __STD_ANSI_TBL_TWO_FIVE_H__
