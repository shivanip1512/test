/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_33
*
* Class:
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_33.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/10/21 16:30:32 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_33_H__
#define __STD_ANSI_TBL_33_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
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

class IM_EX_PROT CtiAnsiTable33 : public CtiAnsiTableBase
{
protected:


    PRI_DISP_LIST_RCD  _priDispListTable;


private:

    UINT16 _nbrPriDispListItems;
    UINT8 _nbrPriDispLists;

public:

   CtiAnsiTable33( );
   CtiAnsiTable33( BYTE *dataBlob, UINT8 nbrPriDispLists, UINT16 nbrPriDispListItems );
   virtual ~CtiAnsiTable33();
   CtiAnsiTable33& operator=(const CtiAnsiTable33& aRef);
   void printResult( const std::string& deviceName );

  // UINT8 getDisplaySources(int sourceIndex, int widthIndex);


};

#endif // #ifndef __STD_ANSI_TBL_33_H__
