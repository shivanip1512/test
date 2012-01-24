/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_00
*
* Date:   9/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_00.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_zero_zero.cpp,v $
      Revision 1.9  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.8  2005/12/20 17:19:58  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7  2005/12/12 20:34:30  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

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
#include "precompiled.h"

#include "std_ansi_tbl_00.h"
#include "logger.h"
using namespace std;


CtiAnsiTable00::CtiAnsiTable00( BYTE *dataBlob ) :
    _nameplate_type(0),
    _default_set_used(0),
    _max_proc_parm_len(0),
    _max_resp_data_len(0),
    _std_version_no(0),
    _std_revision_no(0),
    _nbr_pending(0)
{
   //int   byteCount;
   //byteCount = sizeof( FORMAT_CONTROL_1 ) + sizeof( FORMAT_CONTROL_2 ) + sizeof( FORMAT_CONTROL_3 ) + sizeof( unsigned char )*16;

   memcpy(( void *)&_control_1, dataBlob, sizeof( FORMAT_CONTROL_1 ) );
   dataBlob += sizeof( FORMAT_CONTROL_1 );


   memcpy(( void *)&_control_2, dataBlob, sizeof( FORMAT_CONTROL_2 ) );
   dataBlob += sizeof( FORMAT_CONTROL_2 );


   memcpy(( void *)&_control_3, dataBlob, sizeof( FORMAT_CONTROL_3 ) );
   dataBlob += sizeof( FORMAT_CONTROL_3 );


   memcpy(( void *)&_device_class, dataBlob, sizeof( unsigned char )*16 );
   dataBlob += sizeof( unsigned char )*16;

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

CtiAnsiTable00::~CtiAnsiTable00()
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

CtiAnsiTable00& CtiAnsiTable00::operator=(const CtiAnsiTable00& aRef)
{
  if(this != &aRef)
  {
  }
  return *this;
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable00::printResult(const string& deviceName)
{
    int integer;
    string aString;
    bool flag;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    integer = getRawDataOrder();
    aString = getResolvedDataOrder();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<"  Std Table 0 =========================" << endl;
        dout << "   Data Order: " << aString << " (" << integer <<")" << endl;
    }

    integer = getRawCharFormat();
    aString = getResolvedCharFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Character Format: " << aString << " (" << integer <<")" << endl;
    }

    flag = getRawMfgSerialNumberFlag();
    aString = getResolvedMfgSerialNumberFlag();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Mfg Serial Number Flag: " << aString << " (" << flag <<")" << endl;
    }

    integer = getRawTimeFormat();
    aString = getResolvedTimeFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Time Format: " << aString << " (" << integer <<")" << endl;
    }

    integer = getRawDataAccess();
    aString = getResolvedDataAccess() ;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Data Access: " << aString << " (" << integer <<")" << endl;
    }

    integer = getRawIdFormat();
    aString = getResolvedIdFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Id Format: " << aString << " (" << integer <<")" << endl;
    }

    integer = getRawIntFormat();
    aString = getResolvedIntFormat();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Int Format: " << aString << " (" << integer <<")" << endl;
    }
    integer = getRawNIFormat1() ;
    aString = getResolvedNIFormat1();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Non integer format 1: " << aString << " (" << integer <<")" << endl;
    }


    integer = getRawNIFormat2() ;
    aString = getResolvedNIFormat2();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Non integer format 2: " << aString << " (" << integer <<")" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Device Class:  (" <<(int) _device_class[0] <<" "<<(int)_device_class[1]<<" "<< (int)_device_class[2]<<" "<<(int)_device_class[3]<<")" << endl;
    }

    integer = getRawNameplateType() ;
    aString = getResolvedNameplateType();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Nameplate Type: " << aString << " (" << integer <<")" << endl;
    }

    integer = getRawDefaultSetUsed() ;
    aString = getResolvedDefaultSetUsed();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Default Set Used: " << aString << " (" << integer <<")" << endl;
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
    for (long xx = 0; xx < _dim_std_tbls_used; xx++ )
    {
        for (int x3 = 0; x3 < 8; x3++ )
        {
            int offset = (xx==0?1:8);
            if ((_std_tbls_used[xx] >> x3) & 0x01 == 0x01)
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  "<<  (int)((offset * xx) + x3);
            }
        }
    }
    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
bool CtiAnsiTable00::getRawMfgSerialNumberFlag( void )
{
   return (bool)_control_1.mfg_sn_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedMfgSerialNumberFlag( void )
{
    string ret;
    if ((int)_control_1.mfg_sn_flag == 0)
        ret = string (ANSI_FALSE);
    else
        ret = string (ANSI_TRUE);

   return ret;
}


//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable00::getRawIdFormat( void )
{
   return (bool)_control_2.id_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedIdFormat( void )
{
    string ret;
    if ((int)_control_2.id_format == 0)
        ret = string ("Character string");
    else
        ret = string ("BCD string");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawNIFormat1( void )
{
   return (int)_control_3.ni_format1;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedNIFormat1( void )
{
   return getNonIntegerFormat((int)_control_3.ni_format1);
}
//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawNIFormat2( void )
{
   return (int)_control_3.ni_format2;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedNIFormat2( void )
{
   return getNonIntegerFormat((int)_control_3.ni_format2);
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawTimeFormat( void )
{
   return (int)_control_2.tm_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedTimeFormat( void )
{
    string ret;
    if ((int)_control_2.tm_format == 0)
        ret = string ("No Clock");
    else if ((int)_control_2.tm_format == 1)
        ret = string ("BCD");
    else if ((int)_control_2.tm_format == 2)
        ret = string ("UINT8");
    else if ((int)_control_2.tm_format == 3)
        ret = string ("Binary long counter referenced to base in minutes");
    else if ((int)_control_2.tm_format == 4)
        ret = string ("Binary long counter referenced to base in seconds");
    else
        ret = string ("Unassigned");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawDataAccess( void )
{
   return (int)_control_2.data_access_method;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedDataAccess( void )
{
    string ret;
    if ((int)_control_2.data_access_method == 0)
        ret = string ("Complete reads only, no partial access allowed");
    else if ((int)_control_2.data_access_method == 1)
        ret = string ("Offset-count access supported");
    else if ((int)_control_2.data_access_method == 2)
        ret = string ("Index-count method is supported");
    else if ((int)_control_2.data_access_method == 3)
        ret = string ("Both offset-count and index-count are supported");
    else
        ret = string ("??? Unknown Data Access???");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawIntFormat( void )
{
   return (int)_control_2.int_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedIntFormat( void )
{
    string ret;
    if ((int)_control_2.int_format == 0)
        ret = string ("Signed integers represented in twos complement");
    else if ((int)_control_2.int_format == 1)
        ret = string ("Signed integers represented in ones complement");
    else if ((int)_control_2.int_format == 2)
        ret = string ("Signed integers represented in sign/magnitude format");
    else if ((int)_control_2.int_format == 3)
        ret = string ("Reserved");
    else
        ret = string ("??? Unknown Int Format ???");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawDataOrder( void )
{
   return (int)_control_1.data_order;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedDataOrder( void )
{
    string ret;
    if ((int)_control_1.data_order == 0)
        ret = string ("LSB");
    else
        ret = string ("MSB");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable00::getRawCharFormat( void )
{
   return (int)_control_1.char_format;
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable00::getResolvedCharFormat( void )
{
    string ret;
    if ((int)_control_1.char_format == 0)
        ret= string ("Unassigned");
    else if ((int)_control_1.char_format == 1)
        ret= string ("ISO 7-bit");
    else if ((int)_control_1.char_format == 2)
        ret= string ("ISo 8859/1");
    else
        ret= string ("Unassigned");

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================
/*int CtiAnsiTable00::getRawStdTablesUsed( void )
{
   return (int)_control_1.char_format;
} */

string CtiAnsiTable00::getNonIntegerFormat( int aFormat )
{
    string retVal;

   switch( aFormat )
   {
       case ANSI_NI_FORMAT_FLOAT64:
           retVal = string("Float64");
           break;
       case ANSI_NI_FORMAT_FLOAT32:
           retVal = string("Float32");
            break;
       case ANSI_NI_FORMAT_ARRAY12_CHAR:
           retVal = string("Array[12] Of Char");
           break;
       case ANSI_NI_FORMAT_ARRAY6_CHAR:
           retVal = string("Array[6] Of Char");
           break;
       case ANSI_NI_FORMAT_INT32_IMPLIED:
           retVal = string("Int32 Implied Decimal");
           break;
       case ANSI_NI_FORMAT_ARRAY6_BCD:
           retVal = string("Array[6] Of BCD");
           break;
       case ANSI_NI_FORMAT_ARRAY4_BCD:
           retVal = string("Array[4] Of BCD");
           break;
       case ANSI_NI_FORMAT_INT24:
           retVal = string("Int24");
           break;
       case ANSI_NI_FORMAT_INT32:
           retVal = string("Int32");
           break;
       case ANSI_NI_FORMAT_INT40:
           retVal = string("Int40");
           break;
       case ANSI_NI_FORMAT_INT48:
           retVal = string("Int48");
           break;
       case ANSI_NI_FORMAT_INT64:
           retVal = string("Int64");
           break;
       case ANSI_NI_FORMAT_ARRAY8_BCD:
           retVal = string("Array[8] Of BCD");
           break;
       case ANSI_NI_FORMAT_ARRAY21_CHAR:
           retVal = string("Array[21] Of Char");
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
int CtiAnsiTable00::getRawDeviceClass( void )
{
    return (int) _device_class[4];
}
/*string CtiAnsiTable00::getResolvedDeviceClass( void )
{

} */
int CtiAnsiTable00::getRawNameplateType( void )
{
    return (int) _nameplate_type;
}
string CtiAnsiTable00::getResolvedNameplateType( void )
{
    string ret;
    if ((int)_nameplate_type == 0)
        ret= string ("Gas");
    else if ((int)_nameplate_type == 1)
        ret= string ("Water");
    else if ((int)_nameplate_type == 2)
        ret= string ("Electric");
    else
        ret= string ("Unassigned");

   return ret;
}
int CtiAnsiTable00::getRawDefaultSetUsed( void )
{
    return (int) _default_set_used;
}
string CtiAnsiTable00::getResolvedDefaultSetUsed( void )
{
    string ret;
    if ((int)_default_set_used == 0)
        ret= string ("No default values in use");
    else if ((int)_default_set_used == 1)
        ret= string ("Default Set #1, Simple Meter Register");
    else if ((int)_default_set_used == 2)
        ret= string ("Default Set #2, Simple Demand Meter");
    else if ((int)_default_set_used == 3)
        ret= string ("Default Set #3, Simple TOU Meter");
    else if ((int)_default_set_used == 4)
        ret= string ("Default Set #4, Simple Profile Recorder");
    else
        ret= string ("Unassigned");

   return ret;
}
int CtiAnsiTable00::getRawMaxProcParmLength( void )
{
    return (int) _max_proc_parm_len;
}
/*string CtiAnsiTable00::getResolvedMaxProcParmLength( void )
{
} int CtiAnsiTable00::getRawMaxRespDataLen( void )
{
    return (int)  _max_resp_data_len;

}
string CtiAnsiTable00::getResolvedMaxRespDataLen( void )
{
}
int CtiAnsiTable00::getRawStdVersionNo( void )
{
    return (int) _std_version_no;
}
string CtiAnsiTable00::getResolvedStdVersionNo( void )
{
}
int CtiAnsiTable00::getRawStdRevisionNo( void )
{
    return (int) _std_revision_no;
}
string CtiAnsiTable00::getResolvedStdRevisionNo( void )
{
}
*/

int CtiAnsiTable00::getRawStdRevisionNo( void )
{
    return (int) _std_revision_no;
}

unsigned char * CtiAnsiTable00::getStdTblsUsed(void)
{
    return _std_tbls_used;
}

int CtiAnsiTable00::getDimStdTblsUsed(void)
{
    return _dim_std_tbls_used;
}

unsigned char * CtiAnsiTable00::getMfgTblsUsed(void)
{
    return _mfg_tbls_used;
}

int CtiAnsiTable00::getDimMfgTblsUsed(void)
{
    return _dim_mfg_tbls_used;
}


