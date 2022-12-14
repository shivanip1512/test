#include "precompiled.h"
#include "dsm2.h"
#include "logger.h"
#include "ansi_kv2_mtable_070.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable070::CtiAnsiKV2ManufacturerTable070( BYTE *dataBlob )
{
    memcpy( (void *)&_displayConfigTable, dataBlob, sizeof( unsigned char ) * 46);
    dataBlob +=  (sizeof( unsigned char ) * 46);
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable070::~CtiAnsiKV2ManufacturerTable070()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable070::DisplayUnits_e CtiAnsiKV2ManufacturerTable070::getDemandDisplayUnits()
{
    BYTEUSHORT tmp;
    DisplayUnits_e ret = kW_kVA;

    tmp.ch[0] = _displayConfigTable._demandDisplayUnits;
    switch (tmp.sh)
    {
        case 0:
            ret = kW_kVA;
            break;
        case 1:
            ret = W_VA;
            break;
    }
    return ret;
}


USHORT CtiAnsiKV2ManufacturerTable070::getDisplayScalar()
{
    BYTEUSHORT tmp;

    tmp.ch[0] = _displayConfigTable._displayScalar;
    return tmp.sh;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiKV2ManufacturerTable070::printResult(  )
{
    Cti::FormattedList itemList;

    itemList.add("Date Format")              << getResolvedDateFormat()      <<" ("<< getRawDateFormat()      <<")";
    itemList.add("Suppress Leading Zeros")   <<"("<< getRawSuppressLeadingZeros()<<")";
    itemList.add("Display Scalar")           << getResolvedDisplayScalar()   <<" ("<< getRawDisplayScalar()   <<")";
    itemList.add("Demand Display Units")     << getResolvedDemandDispUnits() <<" ("<< getRawDemandDispUnits() <<")";
    itemList.add("Primary Display")          <<"("<< _displayConfigTable._primaryDisplay    <<")";
    itemList.add("Display Multiplier")       <<"("<< _displayConfigTable._displayMultiplier <<")";
    itemList.add("Cumulative Demand Digits") << displayDigitPlaces(_displayConfigTable._cumulativeDemandDigits);
    itemList.add("Demand Digits")            << displayDigitPlaces(_displayConfigTable._demandDigits);
    itemList.add("Energy Digits")            << displayDigitPlaces(_displayConfigTable._energyDigits);

    CTILOG_INFO(dout,
            endl << formatTableName("kV2 MFG Table 70") <<
            endl <<"DISPLAY CONFIGURATION TABLE"<<
            itemList
            );
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTable070::getRawDateFormat( void )
{
   return ((int)_displayConfigTable._dateFormat);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiKV2ManufacturerTable070::getResolvedDateFormat( void )
{
    string ret;
    switch (getRawDateFormat())
    {
        case 0:
            {
                ret = string ("DD MM YY");
                break;
            }
        case 1:
            {
                ret = string ("MM DD YY");
                break;
            }
        case 2:
            {
                ret = string ("YY MM DD");
                break;
            }
        default:
            {
                ret = string ("Invalid _dateFormat Value");
                break;
            }
    }

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTable070::getRawSuppressLeadingZeros( void )
{
   return ((int)_displayConfigTable._suppressLeadingZeros);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTable070::getRawDisplayScalar( void )
{
   return ((int)_displayConfigTable._displayScalar);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiKV2ManufacturerTable070::getResolvedDisplayScalar( void )
{
    string ret;
    switch (getRawDisplayScalar())
    {
        case 0:
            {
                ret = string ("Do Not Scale");
                break;
            }
        case 1:
            {
                ret = string ("Scale by 0.1");
                break;
            }
        case 2:
            {
                ret = string ("Scale by 0.01");
                break;
            }
        case 3:
            {
                ret = string ("Scale by 0.001");
                break;
            }
        default:
            {
                ret = string ("Invalid _displayScalar Value");
                break;
            }
    }

   return ret;
}
//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiKV2ManufacturerTable070::getRawDemandDispUnits( void )
{
   return ((int)_displayConfigTable._demandDisplayUnits);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiKV2ManufacturerTable070::getResolvedDemandDispUnits( void )
{
    string ret;
    switch (getRawDemandDispUnits())
    {
        case 0:
            {
                ret = string ("kW/kVA");
                break;
            }
        case 1:
            {
                ret = string ("W/VA");
                break;
            }
        default:
            {
                ret = string ("Invalid _demandDispUnits Value");
                break;
            }
    }

   return ret;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTable070::getRawPrimaryDisplay( void )
{
   return ((int)_displayConfigTable._primaryDisplay);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTable070::getNbrRightDigits( Digits_Bfld_t bitfield )
{
   return ((int)bitfield._numberRightDigits);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTable070::getNbrLeftDigits( Digits_Bfld_t bitfield )
{

   return ((int)bitfield._numberLeftDigits);
}

std::string CtiAnsiKV2ManufacturerTable070::displayDigitPlaces( Digits_Bfld_t bitfield )
{
    std::string str = "(";

    str.append(bitfield._numberLeftDigits, 'x');

    str += ".";

    str.append(bitfield._numberRightDigits, 'x');

    str += ")";

    return str;
}


