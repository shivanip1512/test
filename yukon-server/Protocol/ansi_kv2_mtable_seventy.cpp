#pragma warning( disable : 4786)
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/09/30 21:37:16 $
*    History: 
      $Log: ansi_kv2_mtable_seventy.cpp,v $
      Revision 1.2  2004/09/30 21:37:16  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

*----------------------------------------------------------------------------------*/

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
RWCString CtiAnsiKV2ManufacturerTableSeventy::getResolvedDateFormat( void )
{
    RWCString ret;
    switch (getRawDateFormat()) 
    {
        case 0:
            {
                ret = RWCString ("DD MM YY");
                break;
            }
        case 1:
            {
                ret = RWCString ("MM DD YY");
                break;
            }
        case 2:
            {
                ret = RWCString ("YY MM DD");
                break;
            }
        default:
            {
                ret = RWCString ("Invalid _dateFormat Value");
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
RWCString CtiAnsiKV2ManufacturerTableSeventy::getResolvedDisplayScalar( void )
{
    RWCString ret;
    switch (getRawDisplayScalar()) 
    {
        case 0:
            {
                ret = RWCString ("Do Not Scale");
                break;
            }
        case 1:
            {
                ret = RWCString ("Scale by 0.1");
                break;
            }
        case 2:
            {
                ret = RWCString ("Scale by 0.01");
                break;
            }
        case 3:
            {
                ret = RWCString ("Scale by 0.001");
                break;
            }
        default:
            {
                ret = RWCString ("Invalid _displayScalar Value");
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
RWCString CtiAnsiKV2ManufacturerTableSeventy::getResolvedDemandDispUnits( void )
{
    RWCString ret;
    switch (getRawDemandDispUnits()) 
    {
        case 0:
            {
                ret = RWCString ("kW/kVA");
                break;
            }
        case 1:
            {
                ret = RWCString ("W/VA");
                break;
            }
        default:
            {
                ret = RWCString ("Invalid _demandDispUnits Value");
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


