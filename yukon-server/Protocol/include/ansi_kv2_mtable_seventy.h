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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/09/30 21:37:19 $
    History         
      $Log: ansi_kv2_mtable_seventy.h,v $
      Revision 1.2  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

         
*----------------------------------------------------------------------------------*/
#include "dlldefs.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

struct Digits_Bfld_t
{
   unsigned char        _numberRightDigits     :4;
   unsigned char        _numberLeftDigits     :4;
};

struct DISPLAY_CONFIG_RCD
{
    unsigned char           _dateFormat;
    unsigned char           _suppressLeadingZeros;
    unsigned char           _displayScalar;
    unsigned char           _demandDisplayUnits;
    unsigned char           _primaryDisplay;
    UINT32                  _displayMultiplier;

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

#pragma pack( pop )

class IM_EX_PROT CtiAnsiKV2ManufacturerTableSeventy : public CtiAnsiTableBase
{

public:

    typedef enum
    {
        kW_kVA,
        W_VA,
    } DisplayUnits_e;

    /*struct Digits_Bfld_t
    {
       unsigned char        _numberRightDigits     :4;
       unsigned char        _numberLeftDigits     :4;
    };
    */
    CtiAnsiKV2ManufacturerTableSeventy( BYTE *dataBlob );
    virtual ~CtiAnsiKV2ManufacturerTableSeventy();

    // only things needed at this time
    USHORT  getDisplayScalar(void);
    DisplayUnits_e getDemandDisplayUnits(void);

    void generateResultPiece( BYTE **dataBlob );
    void decodeResultPiece( BYTE **dataBlob );
    void printResult(  );

    int getRawDateFormat( void );
    RWCString getResolvedDateFormat( void );

    int getRawSuppressLeadingZeros( void );
    int getRawDemandDispUnits( void );
    RWCString getResolvedDemandDispUnits( void );
    int getRawDisplayScalar( void );
    RWCString getResolvedDisplayScalar( void );
    int getRawPrimaryDisplay( void );
    int getNbrRightDigits( Digits_Bfld_t bitfield );
    int getNbrLeftDigits( Digits_Bfld_t bitfield );
    void displayDigitPlaces( Digits_Bfld_t bitfield );
    
private:

    DISPLAY_CONFIG_RCD  _displayConfigTable;

    /*unsigned char           _dateFormat;
    unsigned char           _suppressLeadingZeros;
    unsigned char           _displayScalar;
    unsigned char           _demandDisplayUnits;
    unsigned char           _primaryDisplay;
    UINT32                  _displayMultiplier;

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
    */
};

#endif // #ifndef ANSI_KV2_MTABLE_SEVENTY_H

