
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_THREE_TWO_H__
#define __STD_ANSI_TBL_THREE_TWO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_three_two
*
* Class:
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_three_two.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/03/14 21:45:15 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct DISP_SOURCE_DESC_RCD
{
    UINT8 *displaySource;
};

/*struct DISPLAY_SOURCE_RCD
{
    DISP_SOURCE_DESC_RCD  *displaySources;
}; */

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableThreeTwo : public CtiAnsiTableBase
{
protected:

  //DISPLAY_SOURCE_RCD *_displaySourceTable;
    DISP_SOURCE_DESC_RCD  *_displaySources;


private:

    UINT16 _nbrDispSources;
    UINT8 _widthDispSources;
    
public:

   CtiAnsiTableThreeTwo( );
   CtiAnsiTableThreeTwo( BYTE *dataBlob, UINT16 nbrDispSources, UINT8 widthDispSources );
   virtual ~CtiAnsiTableThreeTwo();
   CtiAnsiTableThreeTwo& operator=(const CtiAnsiTableThreeTwo& aRef);
   void printResult(  );

   UINT8 getDisplaySources(int sourceIndex, int widthIndex);

   
};

#endif // #ifndef __STD_ANSI_TBL_TWO_EIGHT_H__
