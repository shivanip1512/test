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
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_ZERO_ZERO_H__
#define __STD_ANSI_TBL_ZERO_ZERO_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

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
   unsigned char data_access_meth   :2;
   unsigned char id_format          :1;
   unsigned char int_format         :2;
};

struct FORMAT_CONTROL_3
{
   unsigned char ni_format1         :4;
   unsigned char ni_format2         :4;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableZeroZero
{
protected:

   FORMAT_CONTROL_1  _control_1;
   FORMAT_CONTROL_2  _control_2;
   FORMAT_CONTROL_3  _control_3;
   unsigned char     _device_class[4];
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

   bool getMfgSerNumberFlag( void );
   bool getIdForm( void );

   int getTmFormat( void );
   int getIntFormat( void );
   int getNiFormatOne( void );
   int getNiFormatTwo( void );

   CtiAnsiTableZeroZero( BYTE *dataBlob );

   virtual ~CtiAnsiTableZeroZero();

   CtiAnsiTableZeroZero& operator=(const CtiAnsiTableZeroZero& aRef);

};
#endif // #ifndef __STD_ANSI_TBL_ZERO_ZERO_H__

