#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_zero
*
* Date:   9/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_zero_zero.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*    History: 
      $Log: std_ansi_tbl_zero_zero.cpp,v $
      Revision 1.6  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/12/10 21:58:41  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.4  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_zero_zero.h"
#include "logger.h"

//=========================================================================================================================================
//We've gotten all the data back from the device and we're going to fill up our table
//Note: we have to use some of the pieces in this table to fill other pieces in this table..
//
//This will feel.... a little weird....
//=========================================================================================================================================
CtiAnsiTableZeroZero::CtiAnsiTableZeroZero( )
{
    _std_tbls_used = NULL;
    _mfg_tbls_used = NULL;
    _std_proc_used = NULL;
    _mfg_proc_used = NULL;
    _std_tbls_write = NULL;
    _mfg_tbls_write = NULL;
}

CtiAnsiTableZeroZero::CtiAnsiTableZeroZero( BYTE *dataBlob )
{
   int   byteCount;

   byteCount = sizeof( FORMAT_CONTROL_1 ) + sizeof( FORMAT_CONTROL_2 ) + sizeof( FORMAT_CONTROL_3 ) + sizeof( unsigned char )*16;

   memcpy(( void *)&_control_1, dataBlob, byteCount );
   dataBlob += byteCount;

   _std_tbls_used = new unsigned char[_dim_std_tbls_used];
   memcpy( _std_tbls_used, dataBlob, _dim_std_tbls_used );
   dataBlob += _dim_std_tbls_used;

   _mfg_tbls_used = new unsigned char[_dim_mfg_tbls_used];
   memcpy( _mfg_tbls_used, dataBlob, _dim_mfg_tbls_used );
   dataBlob += _dim_mfg_tbls_used;

   _std_proc_used = new unsigned char[_dim_std_proc_used];
   memcpy( _std_proc_used, dataBlob, _dim_std_proc_used );
   dataBlob += _dim_std_proc_used;

   _mfg_proc_used = new unsigned char[_dim_mfg_proc_used];
   memcpy( _mfg_proc_used, dataBlob, _dim_mfg_proc_used );
   dataBlob += _dim_mfg_proc_used;

   _std_tbls_write = new unsigned char[_dim_std_tbls_used];
   memcpy( _std_tbls_write, dataBlob, _dim_std_tbls_used );
   dataBlob += _dim_std_tbls_used;

   _mfg_tbls_write = new unsigned char[_dim_mfg_status_used];
   memcpy( _mfg_tbls_write, dataBlob, _dim_mfg_status_used );
   dataBlob += _dim_mfg_status_used;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroZero::~CtiAnsiTableZeroZero()
{
    if (_std_tbls_used != NULL)
    {
        delete   []_std_tbls_used;
        _std_tbls_used = NULL;
    }
    if (_mfg_tbls_used != NULL)
    {
        delete   []_mfg_tbls_used;
        _mfg_tbls_used = NULL;
    }
    if (_std_proc_used != NULL)
    {
        delete   []_std_proc_used;
        _std_proc_used = NULL;
    }
    if (_mfg_proc_used != NULL)
    {
        delete   []_mfg_proc_used;
        _mfg_proc_used = NULL;
    }
    if (_std_tbls_write != NULL)
    {
        delete   []_std_tbls_write;
        _std_tbls_write = NULL;
    }
    if (_mfg_tbls_write != NULL)
    {
        delete   []_mfg_tbls_write;
        _mfg_tbls_write = NULL;
    }
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
void CtiAnsiTableZeroZero::generateResultPiece( BYTE **dataBlob )
{
    printResult();
    memcpy (*dataBlob, ( void *)&_control_1, sizeof (FORMAT_CONTROL_1));
    *dataBlob += sizeof (FORMAT_CONTROL_1);
    memcpy (*dataBlob, ( void *)&_control_2, sizeof (FORMAT_CONTROL_2));
    *dataBlob += sizeof (FORMAT_CONTROL_2);
    memcpy (*dataBlob, ( void *)&_control_3, sizeof (FORMAT_CONTROL_3));
    *dataBlob += sizeof (FORMAT_CONTROL_3);
    memcpy( *dataBlob, ( void *)&_device_class[0], sizeof (unsigned char) * 16 );
    *dataBlob += sizeof (unsigned char) * 16;


    memcpy(*dataBlob,  _std_tbls_used, _dim_std_tbls_used );
    *dataBlob += _dim_std_tbls_used;

    memcpy(*dataBlob,  _mfg_tbls_used, _dim_mfg_tbls_used );
    *dataBlob += _dim_mfg_tbls_used;

    memcpy(*dataBlob,  _std_proc_used, _dim_std_proc_used );
    *dataBlob += _dim_std_proc_used;

    memcpy(*dataBlob,  _mfg_proc_used, _dim_mfg_proc_used );
    *dataBlob += _dim_mfg_proc_used;

    memcpy(*dataBlob,  _std_tbls_write, _dim_std_tbls_used );
    *dataBlob += _dim_std_tbls_used;

    memcpy(*dataBlob,  _mfg_tbls_write, _dim_mfg_status_used );
    *dataBlob += _dim_mfg_status_used;
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableZeroZero::decodeResultPiece( BYTE **dataBlob )
{
    memcpy (( void *)&_control_1, *dataBlob, sizeof (FORMAT_CONTROL_1));
    *dataBlob += sizeof (FORMAT_CONTROL_1);
    memcpy (( void *)&_control_2, *dataBlob, sizeof (FORMAT_CONTROL_2));
    *dataBlob += sizeof (FORMAT_CONTROL_2);
    memcpy (( void *)&_control_3, *dataBlob, sizeof (FORMAT_CONTROL_3));
    *dataBlob += sizeof (FORMAT_CONTROL_3);

    memcpy(( void *)&_device_class[0], *dataBlob, sizeof (unsigned char) * 16 );
    *dataBlob += sizeof (unsigned char) * 16;
   
    _std_tbls_used = new unsigned char[_dim_std_tbls_used];
    memcpy( _std_tbls_used, *dataBlob, _dim_std_tbls_used );
    *dataBlob += _dim_std_tbls_used;

    _mfg_tbls_used = new unsigned char[_dim_mfg_tbls_used];
    memcpy( _mfg_tbls_used, *dataBlob, _dim_mfg_tbls_used );
    *dataBlob += _dim_mfg_tbls_used;

    _std_proc_used = new unsigned char[_dim_std_proc_used];
    memcpy( _std_proc_used, *dataBlob, _dim_std_proc_used );
    *dataBlob += _dim_std_proc_used;

    _mfg_proc_used = new unsigned char[_dim_mfg_proc_used];
    memcpy( _mfg_proc_used, *dataBlob, _dim_mfg_proc_used );
    *dataBlob += _dim_mfg_proc_used;

    _std_tbls_write = new unsigned char[_dim_std_tbls_used];
    memcpy( _std_tbls_write, *dataBlob, _dim_std_tbls_used );
    *dataBlob += _dim_std_tbls_used;

    _mfg_tbls_write = new unsigned char[_dim_mfg_status_used];
    memcpy( _mfg_tbls_write, *dataBlob, _dim_mfg_status_used );
    *dataBlob += _dim_mfg_status_used;

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableZeroZero::printResult(  )
{
    int integer;
    RWCString string;
    bool flag;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    integer = getRawDataOrder();
    string = getResolvedDataOrder();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 0 =========================" << endl;
        dout << "   Data Order: " << string << " (" << integer <<")" << endl;
    }

    integer = getRawCharFormat();
    string = getResolvedCharFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Character Format: " << string << " (" << integer <<")" << endl;
    }

    flag = getRawMfgSerialNumberFlag();
    string = getResolvedMfgSerialNumberFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Mfg Serial Number Flag: " << string << " (" << flag <<")" << endl;
    }

    integer = getRawTimeFormat();
    string = getResolvedTimeFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Time Format: " << string << " (" << integer <<")" << endl;
    }

    integer = getRawDataAccess();
    string = getResolvedDataAccess() ;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Data Access: " << string << " (" << integer <<")" << endl;
    }

    integer = getRawIdFormat();
    string = getResolvedIdFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Id Format: " << string << " (" << integer <<")" << endl;
    }

    integer = getRawNIFormat1() ;
    string = getResolvedNIFormat1();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Non integer format 1: " << string << " (" << integer <<")" << endl;
    }


    integer = getRawNIFormat2() ;
    string = getResolvedNIFormat2();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Non integer format 2: " << string << " (" << integer <<")" << endl;
    } 

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Device Class:  (" <<(int) _device_class[0] <<" "<<(int)_device_class[1]<<" "<< (int)_device_class[2]<<" "<<(int)_device_class[3]<<")" << endl;
    }

    integer = getRawNameplateType() ;
    string = getResolvedNameplateType();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Nameplate Type: " << string << " (" << integer <<")" << endl;
    }

    integer = getRawDefaultSetUsed() ;
    string = getResolvedDefaultSetUsed();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Default Set Used: " << string << " (" << integer <<")" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Max Procedure Parameter Length:  (" <<(int)_max_proc_parm_len <<")" << endl;
        dout << "   Max Response Data Length:  (" <<(int)_max_resp_data_len <<")" << endl;
        dout << "   Std Version No.:  (" <<(int)_std_version_no <<")" << endl;
        dout << "   Std Revision No.:  (" <<(int)_std_revision_no <<")" << endl;
        dout << "   Dim Std Tables Used:  (" <<(int)_dim_std_tbls_used <<")" << endl;
        dout << "   Dim Mfg Tables Used:  (" <<(int)_dim_mfg_tbls_used <<")" << endl;
        dout << "   Number Pending:  (" <<(int)_nbr_pending <<")" << endl;
    }
    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "   STD TBLS USED: ";
    }
    for (int xx = 0; xx < _dim_std_tbls_used; xx++ )
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "  "<<  (int)_std_tbls_used[xx];
        }
    }
    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
bool CtiAnsiTableZeroZero::getRawMfgSerialNumberFlag( void )
{
   return (bool)_control_1.mfg_sn_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedMfgSerialNumberFlag( void )
{
    RWCString ret;
    if ((int)_control_1.mfg_sn_flag == 0)
        ret = RWCString (ANSI_FALSE);
    else
        ret = RWCString (ANSI_TRUE);

   return ret;
}


//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableZeroZero::getRawIdFormat( void )
{
   return (bool)_control_2.id_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedIdFormat( void )
{
    RWCString ret;
    if ((int)_control_2.id_format == 0)
        ret = RWCString ("Character string");
    else
        ret = RWCString ("BCD string");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawNIFormat1( void )
{
   return (int)_control_3.ni_format1;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedNIFormat1( void )
{
   return getNonIntegerFormat((int)_control_3.ni_format1);
}
//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawNIFormat2( void )
{
   return (int)_control_3.ni_format2;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedNIFormat2( void )
{
   return getNonIntegerFormat((int)_control_3.ni_format2);
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawTimeFormat( void )
{
   return (int)_control_2.tm_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedTimeFormat( void )
{
    RWCString ret;
    if ((int)_control_2.tm_format == 0)
        ret = RWCString ("No Clock");
    else if ((int)_control_2.tm_format == 1)
        ret = RWCString ("BCD");
    else if ((int)_control_2.tm_format == 2)
        ret = RWCString ("UINT8");
    else if ((int)_control_2.tm_format == 3)
        ret = RWCString ("Binary long counter referenced to base in minutes");
    else if ((int)_control_2.tm_format == 4)
        ret = RWCString ("Binary long counter referenced to base in seconds");
    else
        ret = RWCString ("Unassigned");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawDataAccess( void )
{
   return (int)_control_2.data_access_method;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedDataAccess( void )
{
    RWCString ret;
    if ((int)_control_2.data_access_method == 0)
        ret = RWCString ("Complete reads only, no partial access allowed");
    else if ((int)_control_2.data_access_method == 1)
        ret = RWCString ("Offset-count access supported");
    else if ((int)_control_2.data_access_method == 2)
        ret = RWCString ("Index-count method is supported");
    else if ((int)_control_2.data_access_method == 3)
        ret = RWCString ("Both offset-count and index-count are supported");
    else
        ret = RWCString ("??? Unknown Data Access???");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawIntFormat( void )
{
   return (int)_control_2.int_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedIntFormat( void )
{
    RWCString ret;
    if ((int)_control_2.int_format == 0)
        ret = RWCString ("Signed integers represented in twos complement");
    else if ((int)_control_2.int_format == 1)
        ret = RWCString ("Signed integers represented in ones complement");
    else if ((int)_control_2.int_format == 2)
        ret = RWCString ("Signed integers represented in sign/magnitude format");
    else if ((int)_control_2.int_format == 3)
        ret = RWCString ("Reserved");
    else
        ret = RWCString ("??? Unknown Int Format ???");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawDataOrder( void )
{
   return (int)_control_1.data_order;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedDataOrder( void )
{
    RWCString ret;
    if ((int)_control_1.data_order == 0)
        ret = RWCString ("LSB");
    else
        ret = RWCString ("MSB");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTableZeroZero::getRawCharFormat( void )
{
   return (int)_control_1.char_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroZero::getResolvedCharFormat( void )
{
    RWCString ret;
    if ((int)_control_1.char_format == 0)
        ret= RWCString ("Unassigned");
    else if ((int)_control_1.char_format == 1)
        ret= RWCString ("ISO 7-bit");
    else if ((int)_control_1.char_format == 2)
        ret= RWCString ("ISo 8859/1");
    else
        ret= RWCString ("Unassigned");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
/*int CtiAnsiTableZeroZero::getRawStdTablesUsed( void )
{
   return (int)_control_1.char_format;
} */

RWCString CtiAnsiTableZeroZero::getNonIntegerFormat( int aFormat )
{
    RWCString retVal;

   switch( aFormat )
   {
       case ANSI_NI_FORMAT_FLOAT64:
           retVal = RWCString("Float64");
           break;
       case ANSI_NI_FORMAT_FLOAT32:
           retVal = RWCString("Float32");
            break;
       case ANSI_NI_FORMAT_ARRAY12_CHAR:
           retVal = RWCString("Array[12] Of Char");
           break;
       case ANSI_NI_FORMAT_ARRAY6_CHAR:
           retVal = RWCString("Array[6] Of Char");
           break;
       case ANSI_NI_FORMAT_INT32_IMPLIED:
           retVal = RWCString("Int32 Implied Decimal");
           break;
       case ANSI_NI_FORMAT_ARRAY6_BCD:
           retVal = RWCString("Array[6] Of BCD");
           break;
       case ANSI_NI_FORMAT_ARRAY4_BCD:
           retVal = RWCString("Array[4] Of BCD");
           break;
       case ANSI_NI_FORMAT_INT24:
           retVal = RWCString("Int24");
           break;
       case ANSI_NI_FORMAT_INT32:
           retVal = RWCString("Int32");
           break;
       case ANSI_NI_FORMAT_INT40:
           retVal = RWCString("Int40");
           break;
       case ANSI_NI_FORMAT_INT48:
           retVal = RWCString("Int48");
           break;
       case ANSI_NI_FORMAT_INT64:
           retVal = RWCString("Int64");
           break;
       case ANSI_NI_FORMAT_ARRAY8_BCD:
           retVal = RWCString("Array[8] Of BCD");
           break;
       case ANSI_NI_FORMAT_ARRAY21_CHAR:
           retVal = RWCString("Array[21] Of Char");
           break;
   }
   return( retVal );
}

/********************
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
**************/
int CtiAnsiTableZeroZero::getRawDeviceClass( void )
{
    return (int) _device_class[4];
}
/*RWCString CtiAnsiTableZeroZero::getResolvedDeviceClass( void )
{

} */
int CtiAnsiTableZeroZero::getRawNameplateType( void )
{
    return (int) _nameplate_type;
}
RWCString CtiAnsiTableZeroZero::getResolvedNameplateType( void )
{
    RWCString ret;
    if ((int)_nameplate_type == 0)
        ret= RWCString ("Gas");
    else if ((int)_nameplate_type == 1)
        ret= RWCString ("Water");
    else if ((int)_nameplate_type == 2)
        ret= RWCString ("Electric");
    else
        ret= RWCString ("Unassigned");

   return ret;
}
int CtiAnsiTableZeroZero::getRawDefaultSetUsed( void )
{
    return (int) _default_set_used;
}
RWCString CtiAnsiTableZeroZero::getResolvedDefaultSetUsed( void )
{
    RWCString ret;
    if ((int)_default_set_used == 0)
        ret= RWCString ("No default values in use");
    else if ((int)_default_set_used == 1)
        ret= RWCString ("Default Set #1, Simple Meter Register");
    else if ((int)_default_set_used == 2)
        ret= RWCString ("Default Set #2, Simple Demand Meter");
    else if ((int)_default_set_used == 3)
        ret= RWCString ("Default Set #3, Simple TOU Meter");
    else if ((int)_default_set_used == 4)
        ret= RWCString ("Default Set #4, Simple Profile Recorder");
    else
        ret= RWCString ("Unassigned");

   return ret;
}
int CtiAnsiTableZeroZero::getRawMaxProcParmLength( void )
{
    return (int) _max_proc_parm_len;
}
/*RWCString CtiAnsiTableZeroZero::getResolvedMaxProcParmLength( void ) 
{
} int CtiAnsiTableZeroZero::getRawMaxRespDataLen( void )
{
    return (int)  _max_resp_data_len;

}
RWCString CtiAnsiTableZeroZero::getResolvedMaxRespDataLen( void )
{
}
int CtiAnsiTableZeroZero::getRawStdVersionNo( void )
{
    return (int) _std_version_no;
}
RWCString CtiAnsiTableZeroZero::getResolvedStdVersionNo( void )
{
}
int CtiAnsiTableZeroZero::getRawStdRevisionNo( void )
{
    return (int) _std_revision_no;
}
RWCString CtiAnsiTableZeroZero::getResolvedStdRevisionNo( void )
{
}
*/

int CtiAnsiTableZeroZero::getRawStdRevisionNo( void )
{
    return (int) _std_revision_no;
}

unsigned char * CtiAnsiTableZeroZero::getStdTblsUsed(void)
{
    return _std_tbls_used;
}

int CtiAnsiTableZeroZero::getDimStdTblsUsed(void)
{
    return _dim_std_tbls_used;
}

unsigned char * CtiAnsiTableZeroZero::getMfgTblsUsed(void)
{
    return _mfg_tbls_used;
}

int CtiAnsiTableZeroZero::getDimMfgTblsUsed(void)
{
    return _dim_mfg_tbls_used;
}


