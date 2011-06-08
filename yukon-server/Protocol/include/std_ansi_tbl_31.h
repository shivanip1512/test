/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_31
*
* Class:
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_31.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/21 16:30:32 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_31_H__
#define __STD_ANSI_TBL_31_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct DISP_FLAG_BFLD
{
    unsigned char  onTimeFlag:1;
    unsigned char  offTimeFlag:1;
    unsigned char  holdTimeFlag:1;
    unsigned char  filler:5;
};

struct DISP_RCD
{
    DISP_FLAG_BFLD displayCtrl;
    UINT16         nbrDispSources;
    UINT8          widthDispSources;
    UINT16         nbrPriDispListItems;
    UINT8          nbrPriDispLists;
    UINT16         nbrSecDispListItems;
    UINT8          nbrSecDispLists;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable31 : public CtiAnsiTableBase
{
protected:

  DISP_RCD _displayTable;

private:

public:

   CtiAnsiTable31( );
   CtiAnsiTable31( BYTE *dataBlob );
   virtual ~CtiAnsiTable31();
   CtiAnsiTable31& operator=(const CtiAnsiTable31& aRef);
   void printResult( const std::string& deviceName );

   UINT16 getNbrDispSources( );
   UINT8  getWidthDispSources( );
   UINT16 getNbrPriDispListItems( );
   UINT8 getNbrPriDispLists( );
   UINT16 getNbrSecDispListItems( );
   UINT8  getNbrSecDispLists( );

   bool  getOnTimeFlag( );
   bool  getOffTimeFlag( );
   bool  getHoldTimeFlag( );


};

#endif // #ifndef __STD_ANSI_TBL_31_H__
