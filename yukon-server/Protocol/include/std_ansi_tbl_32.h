/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_32
*
* Class:
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_32.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/21 16:30:32 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_32_H__
#define __STD_ANSI_TBL_32_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
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

class IM_EX_PROT CtiAnsiTable32 : public CtiAnsiTableBase
{
protected:

  //DISPLAY_SOURCE_RCD *_displaySourceTable;
    DISP_SOURCE_DESC_RCD  *_displaySources;


private:

    UINT16 _nbrDispSources;
    UINT8 _widthDispSources;

public:

   CtiAnsiTable32( );
   CtiAnsiTable32( BYTE *dataBlob, UINT16 nbrDispSources, UINT8 widthDispSources );
   virtual ~CtiAnsiTable32();
   CtiAnsiTable32& operator=(const CtiAnsiTable32& aRef);
   void printResult( const std::string& deviceName );

   UINT8 getDisplaySources(int sourceIndex, int widthIndex);


};

#endif // #ifndef __STD_ANSI_TBL_32_H__
