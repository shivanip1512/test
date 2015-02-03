#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_01.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable01::CtiAnsiTable01( BYTE *dataBlob, bool sn_flag, bool id_form ) :
    _serialNumberFlag(sn_flag),
    _idForm(id_form)
{
   dataBlob += toAnsiIntParser(dataBlob, &_manufacturer, 4 * sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_ed_model, 8 * sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_hw_version_number, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_hw_revision_number, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_fw_version_number, sizeof( unsigned char ));
   dataBlob += toAnsiIntParser(dataBlob, &_fw_revision_number, sizeof( unsigned char ));
   
   if( _serialNumberFlag == false )
   {
      if( _idForm == false )
      {
         dataBlob += toAnsiIntParser(dataBlob, &_mfg_serial_number, 16 * sizeof( char ));
      }
      else
      {
         dataBlob += toAnsiIntParser(dataBlob, &_mfg_serial_number, 8 * sizeof( BCD ));
      }
   }
   else
   {
      dataBlob += toAnsiIntParser(dataBlob, &_mfg_serial_number, sizeof( UINT64 ));
   }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable01::~CtiAnsiTable01()
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable01::printResult(const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("Manufacturer")  << getResolvedManufacturer() <<" ("<< getRawManufacturer() <<")";
    itemList.add("Model")         << getResolvedModel()        <<" ("<< getRawModel()        <<")";
    itemList.add("Serial Number") << getResolvedSerialNumber() <<" ("<< getRawSerialNumber() <<")";
    itemList.add("FW Version")    << getFWVersionNumber();
    itemList.add("FW Revision")   << getFWRevisionNumber();

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 1") <<
            itemList
            );
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable01& CtiAnsiTable01::operator=(const CtiAnsiTable01& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;

}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable01::getRawManufacturer( void )
{
    CHAR temp[5];
    memset (temp,'\0',5);
    memcpy (temp, _manufacturer, 4);
   return string (temp);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable01::getResolvedManufacturer( void )
{
    CHAR temp[5];
    memset (temp,'\0',5);
    memcpy (temp, _manufacturer, 4);
   return string (temp);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable01::getRawModel( void )
{
    CHAR temp[9];
    memset (temp,'\0',9);
    memcpy (temp, _ed_model, 8);
    return string (temp);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable01::getResolvedModel( void )
{
    // need to take data format into account !!
    CHAR temp[9];
    memset (temp,'\0',9);
    memcpy (temp, _ed_model, 8);
    return string (temp);
}

//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable01::getRawSerialNumber( void )
{
    string ret;

    if( _serialNumberFlag == false )
    {
       if( _idForm == false )
       {
           CHAR temp[9];
           memset (temp,'\0',9);
           memcpy (temp, _mfg_serial_number.bcd_sn, 8);
           ret=string (temp);
       }
       else
       {
           CHAR temp[17];
           memset (temp,'\0',17);
           memcpy (temp, _mfg_serial_number.char_sn, 16);
           ret=string (temp);
       }
    }
    else
    {
        CHAR temp[8];
        ret=string (_ui64toa(_mfg_serial_number.ll_sn,temp,10));
    }

   return string(ret);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiTable01::getResolvedSerialNumber( void )
{
    string ret;

    if( _serialNumberFlag == false )
    {
       if( _idForm == false )
       {
           CHAR temp[9];
           memset (temp,'\0',9);
           for (int x=0; x < 8; x++)
           {
               temp[x] = _mfg_serial_number.bcd_sn[x];
           }
           ret=string (temp);
       }
       else
       {
           CHAR temp[17];
           memset (temp,'\0',17);
           for (int x=0; x < 16; x++)
           {
               temp[x] = _mfg_serial_number.char_sn[x];
           }
           ret=string (temp);
       }
    }
    else
    {
        BYTEUINT64 temp;
        BYTEUINT64 sn;

        temp.u64=_mfg_serial_number.ll_sn;
        for (int x=0; x < 8; x++)
        {
            sn.ch[x] = temp.ch[x];
        }
        CHAR strTmp[8];
        ret=string (_ui64toa(sn.u64,strTmp,10));
    }

   return string(ret);
}



int CtiAnsiTable01::getFWVersionNumber()
{
    return (int)_fw_version_number;
}
int CtiAnsiTable01::getFWRevisionNumber()
{
    return (int) _fw_revision_number;
}


