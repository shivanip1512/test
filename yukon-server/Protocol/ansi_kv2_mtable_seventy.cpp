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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/04/25 14:54:54 $
*    History: 
      $Log: ansi_kv2_mtable_seventy.cpp,v $
      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

*----------------------------------------------------------------------------------*/

#include "ansi_kv2_mtable_seventy.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableSeventy::CtiAnsiKV2ManufacturerTableSeventy( BYTE *dataBlob )
{
    BYTE *ptr=dataBlob;

    _dateFormat                     =   *ptr++;
    _suppressLeadingZeros           =   *ptr++;
    _displayScalar                  =   *ptr++;
    _demandDisplayUnits             =   *ptr++;
    _primaryDisplay                 =   *ptr++;
    _displayMultiplier[0]           =   *ptr++;
    _displayMultiplier[1]           =   *ptr++;
    _displayMultiplier[2]           =   *ptr++;
    _displayMultiplier[3]           =   *ptr++;

    // need to finish this


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

    tmp.ch[0] = _demandDisplayUnits;
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

    tmp.ch[0] = _displayScalar;
    return tmp.sh;
}




