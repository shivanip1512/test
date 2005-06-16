
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ZERO_ONE_H__
#define __STD_ANSI_TBL_ZERO_ONE_H__

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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_zero_one.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/06/16 19:18:00 $
*    History: 
      $Log: std_ansi_tbl_zero_one.h,v $
      Revision 1.4  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

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

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

#pragma pack( push, 1)


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableZeroOne : public CtiAnsiTableBase
{

private:
    unsigned char      _manufacturer[4];
    unsigned char      _ed_model[8];
    unsigned char      _hw_version_number;
    unsigned char      _hw_revision_number;
    unsigned char      _fw_version_number;
    unsigned char      _fw_revision_number;

    //this choice is made in the 00 table
    union
    {
       BCD       bcd_sn[8];
       char      char_sn[16];
       UINT64    ll_sn;

    }_mfg_serial_number;

    // table 0
    bool _serialNumberFlag;
    int _idForm;


public:

    CtiAnsiTableZeroOne( bool sn_flag, bool id_form  );
   CtiAnsiTableZeroOne( BYTE *dataBlob, bool sn_flag, bool id_form );
   virtual ~CtiAnsiTableZeroOne();
   CtiAnsiTableZeroOne& operator=(const CtiAnsiTableZeroOne& aRef);

    RWCString getRawManufacturer( void );
    RWCString getResolvedManufacturer( void );
    RWCString getRawModel( void );
    RWCString getResolvedModel( void );
    RWCString getRawSerialNumber( void );
    RWCString getResolvedSerialNumber( void );

    void printResult( );
    void generateResultPiece( BYTE **dataBlob );
    void decodeResultPiece( BYTE **dataBlob );

    int getFWVersionNumber();
    int getFWRevisionNumber();


};

#endif // #ifndef __STD_ANSI_TBL_ZERO_ONE_H__
