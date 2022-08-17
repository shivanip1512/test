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
    dataBlob += toAnsiIntParser(dataBlob, &_control_1, sizeof( FORMAT_CONTROL_1 ) );
    dataBlob += toAnsiIntParser(dataBlob, &_control_2, sizeof( FORMAT_CONTROL_2 ) );
    dataBlob += toAnsiIntParser(dataBlob, &_control_3, sizeof( FORMAT_CONTROL_3 ) );
    dataBlob += toAnsiIntParser(dataBlob, &_device_class, sizeof( unsigned char )*16 );
    
    _std_tbls_used = new unsigned char[_dim_std_tbls_used];
    dataBlob += toAnsiIntParser(dataBlob, _std_tbls_used, _dim_std_tbls_used );

    _mfg_tbls_used = new unsigned char[_dim_mfg_tbls_used];
    dataBlob += toAnsiIntParser(dataBlob, _mfg_tbls_used, _dim_mfg_tbls_used );

    _std_proc_used = new unsigned char[_dim_std_proc_used];
    dataBlob += toAnsiIntParser(dataBlob,  _std_proc_used, _dim_std_proc_used );
    
    _mfg_proc_used = new unsigned char[_dim_mfg_proc_used];
    dataBlob += toAnsiIntParser(dataBlob, _mfg_proc_used, _dim_mfg_proc_used );
    
    _std_tbls_write = new unsigned char[_dim_std_tbls_used];
    dataBlob += toAnsiIntParser(dataBlob, _std_tbls_write, _dim_std_tbls_used );

    _mfg_tbls_write = new unsigned char[_dim_mfg_status_used];
    dataBlob += toAnsiIntParser(dataBlob, _mfg_tbls_write, _dim_mfg_status_used );

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
    Cti::FormattedList itemList;

    itemList.add("Data Order")                     << getResolvedDataOrder()           <<" ("<< (int)getRawDataOrder()      <<")";
    itemList.add("Character Format")               << getResolvedCharFormat()          <<" ("<< getRawCharFormat()          <<")";
    itemList.add("Mfg Serial Number Flag")         << getResolvedMfgSerialNumberFlag() <<" ("<< getRawMfgSerialNumberFlag() <<")";
    itemList.add("Time Format")                    << getResolvedTimeFormat()          <<" ("<< getRawTimeFormat()          <<")";
    itemList.add("Data Access")                    << getResolvedDataAccess()          <<" ("<< getRawDataAccess()          <<")";
    itemList.add("Id Format")                      << getResolvedIdFormat()            <<" ("<< getRawIdFormat()            <<")";
    itemList.add("Int Format")                     << getResolvedIntFormat()           <<" ("<< getRawIntFormat()           <<")";
    itemList.add("Non integer format 1")           << getResolvedNIFormat1()           <<" ("<< getRawNIFormat1()           <<")";
    itemList.add("Non integer format 2")           << getResolvedNIFormat2()           <<" ("<< getRawNIFormat2()           <<")";
    itemList.add("Device Class")                   <<"("<< _device_class[0] <<" "<< _device_class[1] <<" "<< _device_class[2] <<" "<< _device_class[3] <<")";
    itemList.add("Nameplate Type")                 << getResolvedNameplateType()       <<" ("<< getRawNameplateType()  <<")";
    itemList.add("Default Set Used")               << getResolvedDefaultSetUsed()      <<" ("<< getRawDefaultSetUsed() <<")";
    itemList.add("Max Procedure Parameter Length") <<"("<< _max_proc_parm_len <<")";
    itemList.add("Max Response Data Length")       <<"("<< _max_resp_data_len <<")";
    itemList.add("Std Version No.")                <<"("<< _std_version_no    <<")";
    itemList.add("Std Revision No.")               <<"("<< _std_revision_no   <<")";
    itemList.add("Dim Std Tables Used")            <<"("<< _dim_std_tbls_used <<")";
    itemList.add("Dim Mfg Tables Used")            <<"("<< _dim_mfg_tbls_used <<")";
    itemList.add("Number Pending")                 <<"("<< _nbr_pending       <<")";

    {
        Cti::StreamBufferSink& values = itemList.add("STD TBLS USED");

        for(long xx = 0; xx < _dim_std_tbls_used; xx++ )
        {
            for(int x3 = 0; x3 < 8; x3++ )
            {
                int offset = (xx==0?1:8);
                if ((_std_tbls_used[xx] >> x3) & 0x01 == 0x01)
                {
                    values << (int)((offset * xx) + x3) <<" ";
                }
            }
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 0") <<
            itemList
            );
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
DataOrder CtiAnsiTable00::getRawDataOrder( void )
{
   return _control_1.data_order == 0 ? LSB : MSB ;
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


