/*---------------------------------------------------------------------------------*
*
* File:   ansi_kv2_mtable_seventy.cpp
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_kv2_mtable_seventy.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/07 18:16:45 $
*    History:
      $Log: ansi_kv2_mtable_seventy.cpp,v $
      Revision 1.5  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.4  2005/12/20 17:19:53  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3.2.1  2005/07/12 21:08:42  jliu
      rpStringWithoutCmpParser

      Revision 1.3  2005/02/10 23:23:56  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.2  2004/09/30 21:37:16  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

*----------------------------------------------------------------------------------*/
#include "yukon.h"
#include "dsm2.h"
#include "logger.h"
#include "ansi_kv2_mtable_seventy.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableSeventy::CtiAnsiKV2ManufacturerTableSeventy( BYTE *dataBlob )
{
    memcpy( (void *)&_displayConfigTable, dataBlob, sizeof( unsigned char ) * 46);
    dataBlob +=  (sizeof( unsigned char ) * 46);
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableSeventy::~CtiAnsiKV2ManufacturerTableSeventy()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableSeventy::DisplayUnits_e CtiAnsiKV2ManufacturerTableSeventy::getDemandDisplayUnits()
{
    BYTEUSHORT tmp;
    DisplayUnits_e ret;

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


USHORT CtiAnsiKV2ManufacturerTableSeventy::getDisplayScalar()
{
    BYTEUSHORT tmp;

    tmp.ch[0] = _displayConfigTable._displayScalar;
    return tmp.sh;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiKV2ManufacturerTableSeventy::generateResultPiece( BYTE **dataBlob )
{
    memcpy( *dataBlob, (void *)&_displayConfigTable, sizeof( unsigned char ) * 46);
    *dataBlob +=  (sizeof( unsigned char ) * 46);

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiKV2ManufacturerTableSeventy::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void *)&_displayConfigTable, *dataBlob, sizeof( unsigned char ) * 46);
    *dataBlob +=  (sizeof( unsigned char ) * 46);
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiKV2ManufacturerTableSeventy::printResult(  )
{

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  kV2 MFG Table 70  ========================" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "     DISPLAY CONFIGURATION TABLE " << endl;
        dout << "        Date Format:  " <<getResolvedDateFormat()<<" ("<< getRawDateFormat()<<")"<< endl;
        dout << "        Suppress Leading Zeros: (" <<getRawSuppressLeadingZeros()<<")"<< endl;
        dout << "        Display Scalar:       " <<getResolvedDisplayScalar()<<" ("<<getRawDisplayScalar()<<")"<< endl;
        dout << "        Demand Display Units: " <<getResolvedDemandDispUnits()<<" ("<<getRawDemandDispUnits()<<")"<< endl;
        dout << "        Primary Display:  (" << (int)_displayConfigTable._primaryDisplay<<")"<<endl;
        dout << "        Display Multiplier:  (" << (long)_displayConfigTable._displayMultiplier<<")"<<endl;
        dout << "        Cumulative Demand Digits: ";
    }
    displayDigitPlaces(_displayConfigTable._cumulativeDemandDigits);
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "        Demand Digits: ";
    }
    displayDigitPlaces(_displayConfigTable._demandDigits);
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "        Energy Digits: ";
    }
    displayDigitPlaces(_displayConfigTable._energyDigits);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTableSeventy::getRawDateFormat( void )
{
   return ((int)_displayConfigTable._dateFormat);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiKV2ManufacturerTableSeventy::getResolvedDateFormat( void )
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

int CtiAnsiKV2ManufacturerTableSeventy::getRawSuppressLeadingZeros( void )
{
   return ((int)_displayConfigTable._suppressLeadingZeros);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTableSeventy::getRawDisplayScalar( void )
{
   return ((int)_displayConfigTable._displayScalar);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiKV2ManufacturerTableSeventy::getResolvedDisplayScalar( void )
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
int CtiAnsiKV2ManufacturerTableSeventy::getRawDemandDispUnits( void )
{
   return ((int)_displayConfigTable._demandDisplayUnits);
}
//=========================================================================================================================================
//=========================================================================================================================================
string CtiAnsiKV2ManufacturerTableSeventy::getResolvedDemandDispUnits( void )
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

int CtiAnsiKV2ManufacturerTableSeventy::getRawPrimaryDisplay( void )
{
   return ((int)_displayConfigTable._primaryDisplay);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTableSeventy::getNbrRightDigits( Digits_Bfld_t bitfield )
{
   return ((int)bitfield._numberRightDigits);
}
//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiKV2ManufacturerTableSeventy::getNbrLeftDigits( Digits_Bfld_t bitfield )
{

   return ((int)bitfield._numberLeftDigits);
}
void CtiAnsiKV2ManufacturerTableSeventy::displayDigitPlaces( Digits_Bfld_t bitfield )
{
    int x;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " (";
    }
    for ( x = 0; x < (int)bitfield._numberLeftDigits; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "x";
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << ".";
    }
    for ( x = 0; x < (int)bitfield._numberRightDigits; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "x";
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<" )"<< endl;
    }
}


