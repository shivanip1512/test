#pragma warning( disable : 4786)
#ifndef __ANSI_KV2_MTABLE_SEVENTY_H__
#define __ANSI_KV2_MTABLE_SEVENTY_H__

/*---------------------------------------------------------------------------------*
*
* File:   
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/ansi_kv2_mtable_seventy.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/04/25 14:54:54 $
    History         
      $Log: ansi_kv2_mtable_seventy.h,v $
      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

         
*----------------------------------------------------------------------------------*/
#include "dlldefs.h"
#include "std_ansi_tbl_base.h"


class IM_EX_PROT CtiAnsiKV2ManufacturerTableSeventy : public CtiAnsiTableBase
{

public:

    typedef enum
    {
        kW_kVA,
        W_VA,
    } DisplayUnits_e;

    CtiAnsiKV2ManufacturerTableSeventy( BYTE *dataBlob );
    virtual ~CtiAnsiKV2ManufacturerTableSeventy();

    // only things needed at this time
    USHORT  getDisplayScalar(void);
    DisplayUnits_e getDemandDisplayUnits(void);

    struct Digits_Bfld_t
    {
       unsigned char        _numberRightDigits     :4;
       unsigned char        _numberLeftDigits     :4;
    };


private:

    unsigned char           _dateFormat;
    unsigned char           _suppressLeadingZeros;
    unsigned char           _displayScalar;
    unsigned char           _demandDisplayUnits;
    unsigned char           _primaryDisplay;
    unsigned char           _displayMultiplier[4];
    unsigned char           _mfgTestVector;

    Digits_Bfld_t           _cumulativeDemandDigits;
    Digits_Bfld_t           _demandDigits;
    Digits_Bfld_t           _energyDigits;
    Digits_Bfld_t           _fixedPointFormat1;
    Digits_Bfld_t           _fixedPointFormat2;

    unsigned char           _numericFormat1;
    unsigned char           _numericFormat2;

    unsigned char           _userLabel1[5];
    unsigned char           _userLabel2[5];
    unsigned char           _userLabel3[5];
    unsigned char           _userLabel4[5];
    unsigned char           _userLabel5[5];
    unsigned char           _userLabel6[5];
};

#endif // #ifndef ANSI_KV2_MTABLE_SEVENTY_H

