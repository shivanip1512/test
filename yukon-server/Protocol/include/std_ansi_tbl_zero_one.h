
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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/10/07 18:16:46 $
*    History:
      $Log: std_ansi_tbl_zero_one.h,v $
      Revision 1.7  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.6  2005/12/20 17:20:01  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

<<<<<<< std_ansi_tbl_zero_one.h
      Revision 1.3.18.2  2005/07/27 19:28:01  alauinger
      merged from the head 20050720


      Revision 1.3.18.1  2005/07/12 21:08:43  jliu
      rpStringWithoutCmpParser

=======
      Revision 1.5  2005/12/12 20:34:48  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.4.2.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

>>>>>>> 1.5
      Revision 1.4  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
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

    string getRawManufacturer( void );
    string getResolvedManufacturer( void );
    string getRawModel( void );
    string getResolvedModel( void );
    string getRawSerialNumber( void );
    string getResolvedSerialNumber( void );

    void printResult(const string& deviceName );
    void generateResultPiece( BYTE **dataBlob );
    void decodeResultPiece( BYTE **dataBlob );

    int getFWVersionNumber();
    int getFWRevisionNumber();


};

#endif // #ifndef __STD_ANSI_TBL_ZERO_ONE_H__
