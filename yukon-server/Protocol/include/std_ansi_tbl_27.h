/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_27
*
* Class:
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_27.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:32 $

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_27_H__
#define __STD_ANSI_TBL_27_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"


#define UINT16             __int16
#define UINT8               __int8
class IM_EX_PROT CtiAnsiTable27 : public CtiAnsiTableBase
{
protected:

  unsigned char *_presentDemandSelect;
  unsigned char *_presentValueSelect;

private:

    UINT8 _nbrPresentDemands;
    UINT8 _nbrPresentValues;

public:

   CtiAnsiTable27(  );
   CtiAnsiTable27( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues );
   virtual ~CtiAnsiTable27();
   CtiAnsiTable27& operator=(const CtiAnsiTable27& aRef);
   void printResult( const std::string& deviceName );

   unsigned char* getDemandSelect( );
   unsigned char* getValueSelect( );

};
#endif // #ifndef __STD_ANSI_TBL_SIX_TWO_H__

