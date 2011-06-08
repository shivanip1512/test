/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_00
*
* Class:  CtiAnsiTable00
* Date:   9/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_00.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_zero_zero.h,v $
      Revision 1.9  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.7  2005/12/20 17:20:01  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.6  2005/12/12 20:34:48  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.5  2004/12/10 21:58:43  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.4  2004/09/30 21:37:21  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_00_H__
#define __STD_ANSI_TBL_00_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
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

class IM_EX_PROT CtiAnsiTable00 : public CtiAnsiTableBase
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
    std::string getResolvedDataOrder( void );
    int getRawCharFormat( void );
    std::string getResolvedCharFormat( void );
    bool getRawMfgSerialNumberFlag( void );
    std::string getResolvedMfgSerialNumberFlag( void );
    int getRawTimeFormat( void );
    std::string getResolvedTimeFormat( void );
    int getRawDataAccess( void );
    std::string getResolvedDataAccess( void );
    bool getRawIdFormat( void );
    std::string getResolvedIdFormat( void );
    int getRawIntFormat( void );
    std::string getResolvedIntFormat( void );
    int getRawNIFormat1( void );
    std::string getResolvedNIFormat1( void );
    int getRawNIFormat2( void );
    std::string getResolvedNIFormat2( void );
    int getRawDeviceClass( void );
    std::string getResolvedDeviceClass( void );
    int getRawNameplateType( void );
    std::string getResolvedNameplateType( void );
    int getRawDefaultSetUsed( void );
    std::string getResolvedDefaultSetUsed( void );
    int getRawMaxProcParmLength( void );
    std::string getResolvedMaxProcParmLength( void );
    int getRawMaxRespDataLen( void );
    std::string getResolvedMaxRespDataLen( void );
    int getRawStdVersionNo( void );
    std::string getResolvedStdVersionNo( void );
    int getRawStdRevisionNo( void );
    std::string getResolvedStdRevisionNo( void );

    unsigned char * getStdTblsUsed(void);
    int getDimStdTblsUsed(void);
    unsigned char * getMfgTblsUsed(void);
    int getDimMfgTblsUsed(void);


    std::string getNonIntegerFormat( int aFormat );

    void generateResultPiece( BYTE **dataBlob );
    void decodeResultPiece( BYTE **dataBlob );
    void printResult( const std::string& deviceName );

    CtiAnsiTable00( );
   CtiAnsiTable00( BYTE *dataBlob );

   virtual ~CtiAnsiTable00();

   CtiAnsiTable00& operator=(const CtiAnsiTable00& aRef);

};
#endif // #ifndef __STD_ANSI_TBL_00_H__

