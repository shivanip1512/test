#pragma once

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

class IM_EX_PROT CtiAnsiKV2ManufacturerTable070 : public CtiAnsiTableBase
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
    CtiAnsiKV2ManufacturerTable070( BYTE *dataBlob );
    virtual ~CtiAnsiKV2ManufacturerTable070();

    // only things needed at this time
    USHORT  getDisplayScalar(void);
    DisplayUnits_e getDemandDisplayUnits(void);

    void printResult(  );

    int getRawDateFormat( void );
    std::string getResolvedDateFormat( void );

    int getRawSuppressLeadingZeros( void );
    int getRawDemandDispUnits( void );
    std::string getResolvedDemandDispUnits( void );
    int getRawDisplayScalar( void );
    std::string getResolvedDisplayScalar( void );
    int getRawPrimaryDisplay( void );
    int getNbrRightDigits( Digits_Bfld_t bitfield );
    int getNbrLeftDigits( Digits_Bfld_t bitfield );
    std::string displayDigitPlaces( Digits_Bfld_t bitfield );

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

