
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_zero
*
* Date:   9/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/11/15 20:36:22 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_zero_zero.h"

//=========================================================================================================================================
//We've gotten all the data back from the device and we're going to fill up our table
//Note: we have to use some of the pieces in this table to fill other pieces in this table..
//
//This will feel.... a little weird....
//=========================================================================================================================================

CtiAnsiTableZeroZero::CtiAnsiTableZeroZero( BYTE *dataBlob )
{
   int   byteCount;

   byteCount = sizeof( FORMAT_CONTROL_1 ) + sizeof( FORMAT_CONTROL_2 ) + sizeof( FORMAT_CONTROL_3 ) + sizeof( unsigned char )*16;

   memcpy( ( void *)&_control_1, dataBlob, byteCount );
   dataBlob += byteCount;

   _std_tbls_used = new unsigned char[];
   memcpy( _std_tbls_used, dataBlob, _dim_std_tbls_used );
   dataBlob += sizeof( _std_tbls_used );

   _mfg_tbls_used = new unsigned char[];
   memcpy( _mfg_tbls_used, dataBlob, _dim_mfg_tbls_used );
   dataBlob += sizeof( _mfg_tbls_used );

   _std_proc_used = new unsigned char[];
   memcpy( _std_proc_used, dataBlob, _dim_std_proc_used );
   dataBlob += sizeof( _std_proc_used );

   _mfg_proc_used = new unsigned char[];
   memcpy( _mfg_proc_used, dataBlob, _dim_mfg_proc_used );
   dataBlob += sizeof( _mfg_proc_used );

   _std_tbls_write = new unsigned char[];
   memcpy( _std_tbls_write, dataBlob, _dim_std_tbls_used );
   dataBlob += sizeof( _std_tbls_write );

   _mfg_tbls_write = new unsigned char[];
   memcpy( _mfg_tbls_write, dataBlob, _dim_mfg_status_used );
   dataBlob += sizeof( _mfg_tbls_write );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroZero::~CtiAnsiTableZeroZero()
{
   delete   []_std_tbls_used;
   delete   []_mfg_tbls_used;
   delete   []_std_proc_used;
   delete   []_mfg_proc_used;
   delete   []_std_tbls_write;
   delete   []_mfg_tbls_write;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroZero& CtiAnsiTableZeroZero::operator=(const CtiAnsiTableZeroZero& aRef)
{
  if(this != &aRef)
  {
  }
  return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableZeroZero::getMfgSerNumberFlag( void )
{
   return _control_1.mfg_sn_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableZeroZero::getIdForm( void )
{
   return _control_2.id_format;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableZeroZero::getNiFormatOne( void )
{
   return _control_3.ni_format1;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableZeroZero::getNiFormatTwo( void )
{
   return _control_3.ni_format2;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableZeroZero::getTmFormat( void )
{
   return _control_2.tm_format;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableZeroZero::getIntFormat( void )
{
   return _control_2.int_format;
}

