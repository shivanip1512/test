

#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_THREE_THREE_H__
#define __STD_ANSI_TBL_THREE_THREE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_three_three
*
* Class:
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_three_three.h-arc  $
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

struct DISP_SCROLL1_BFLD
{
    unsigned char onTime:4;
    unsigned char offTime:4;
};
struct DISP_SCROLL2_BFLD
{
    unsigned char holdTime:4;
    unsigned char defaultList:4;
};

struct DISP_LIST_DESC_RCD
{
    DISP_SCROLL1_BFLD  dispScroll1;
    DISP_SCROLL2_BFLD  dispScroll2;
    UINT8  nbrListItems;
    
};

struct PRI_DISP_LIST_RCD
{
    DISP_LIST_DESC_RCD  *priDispList;
    UINT16 *priDispSources;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableThreeThree : public CtiAnsiTableBase
{
protected:

  
    PRI_DISP_LIST_RCD  _priDispListTable;


private:

    UINT16 _nbrPriDispListItems;
    UINT8 _nbrPriDispLists;
    
public:

   CtiAnsiTableThreeThree( );
   CtiAnsiTableThreeThree( BYTE *dataBlob, UINT8 nbrPriDispLists, UINT16 nbrPriDispListItems );
   virtual ~CtiAnsiTableThreeThree();
   CtiAnsiTableThreeThree& operator=(const CtiAnsiTableThreeThree& aRef);
   void printResult(  );

  // UINT8 getDisplaySources(int sourceIndex, int widthIndex);

   
};

#endif // #ifndef __STD_ANSI_TBL_THREE_THREE_H__
