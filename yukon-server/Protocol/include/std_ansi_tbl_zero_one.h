/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_one
*
* Class:
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_ZERO_ONE_H__
#define __STD_ANSI_TBL_ZERO_ONE_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

#pragma pack( push, 1)


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableZeroOne
{
protected:

  unsigned char      manufacturer[4];
  unsigned char      ed_model[8];
  unsigned char      hw_version_number;
  unsigned char      hw_revision_number;
  unsigned char      fw_version_number;
  unsigned char      fw_revision_number;

  //this choice is made in the 00 table
  union
  {
     BCD       bcd_sn[8];
     char      char_sn[16];
     UINT64    ll_sn;

  }mfg_serial_number;

private:

public:

   CtiAnsiTableZeroOne( BYTE *dataBlob );
   virtual ~CtiAnsiTableZeroOne();
   CtiAnsiTableZeroOne& operator=(const CtiAnsiTableZeroOne& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_ZERO_ONE_H__
