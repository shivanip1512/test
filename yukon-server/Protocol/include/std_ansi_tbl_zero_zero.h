#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ZERO_ZERO_H__
#define __STD_ANSI_TBL_ZERO_ZERO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_zero
*
* Class:  CtiAnsiTableZeroZero
* Date:   9/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_zero_zero.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/12/10 21:58:43 $
*    History: 
      $Log: std_ansi_tbl_zero_zero.h,v $
      Revision 1.5  2004/12/10 21:58:43  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.4  2004/09/30 21:37:21  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

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

#pragma pack( push, 1)

struct FORMAT_CONTROL_1
{
   unsigned char data_order         :1;
   unsigned char char_format        :3;
   unsigned char model_select       :3;
   unsigned char mfg_sn_flag        :1;
};

struct FORMAT_CONTROL_2
{
   unsigned char tm_format          :3;
   unsigned char data_access_method :2;
   unsigned char id_format          :1;
   unsigned char int_format         :2;
};

struct FORMAT_CONTROL_3
{
   unsigned char ni_format1         :4;
   unsigned char ni_format2         :4;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableZeroZero : public CtiAnsiTableBase
{
protected:

   FORMAT_CONTROL_1  _control_1;
   FORMAT_CONTROL_2  _control_2;
   FORMAT_CONTROL_3  _control_3;
   unsigned char     _device_class[4];
   unsigned char     _nameplate_type;
   unsigned char     _default_set_used;
   unsigned char     _max_proc_parm_len;
   unsigned char     _max_resp_data_len;
   unsigned char     _std_version_no;
   unsigned char     _std_revision_no;
   unsigned char     _dim_std_tbls_used;
   unsigned char     _dim_mfg_tbls_used;
   unsigned char     _dim_std_proc_used;
   unsigned char     _dim_mfg_proc_used;
   unsigned char     _dim_mfg_status_used;
   unsigned char     _nbr_pending;
   unsigned char     *_std_tbls_used;
   unsigned char     *_mfg_tbls_used;
   unsigned char     *_std_proc_used;
   unsigned char     *_mfg_proc_used;
   unsigned char     *_std_tbls_write;
   unsigned char     *_mfg_tbls_write;

private:

public:

    int getRawDataOrder( void );
    RWCString getResolvedDataOrder( void );
    int getRawCharFormat( void );
    RWCString getResolvedCharFormat( void );
    bool getRawMfgSerialNumberFlag( void );
    RWCString getResolvedMfgSerialNumberFlag( void );
    int getRawTimeFormat( void );
    RWCString getResolvedTimeFormat( void );
    int getRawDataAccess( void );
    RWCString getResolvedDataAccess( void );
    bool getRawIdFormat( void );
    RWCString getResolvedIdFormat( void );
    int getRawIntFormat( void );
    RWCString getResolvedIntFormat( void );
    int getRawNIFormat1( void );
    RWCString getResolvedNIFormat1( void );
    int getRawNIFormat2( void );
    RWCString getResolvedNIFormat2( void );
    int getRawDeviceClass( void );
    RWCString getResolvedDeviceClass( void );
    int getRawNameplateType( void );
    RWCString getResolvedNameplateType( void );
    int getRawDefaultSetUsed( void );
    RWCString getResolvedDefaultSetUsed( void );
    int getRawMaxProcParmLength( void );
    RWCString getResolvedMaxProcParmLength( void );
    int getRawMaxRespDataLen( void );
    RWCString getResolvedMaxRespDataLen( void );
    int getRawStdVersionNo( void );
    RWCString getResolvedStdVersionNo( void );
    int getRawStdRevisionNo( void );
    RWCString getResolvedStdRevisionNo( void );

    unsigned char * getStdTblsUsed(void);
    int getDimStdTblsUsed(void);
    unsigned char * getMfgTblsUsed(void);
    int getDimMfgTblsUsed(void);


    RWCString getNonIntegerFormat( int aFormat );

    void generateResultPiece( BYTE **dataBlob );
    void decodeResultPiece( BYTE **dataBlob );
    void printResult( );

    CtiAnsiTableZeroZero( );
   CtiAnsiTableZeroZero( BYTE *dataBlob );

   virtual ~CtiAnsiTableZeroZero();

   CtiAnsiTableZeroZero& operator=(const CtiAnsiTableZeroZero& aRef);

};
#endif // #ifndef __STD_ANSI_TBL_ZERO_ZERO_H__

