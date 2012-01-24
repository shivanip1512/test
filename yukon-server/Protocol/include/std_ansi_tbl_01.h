#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

#pragma pack( push, 1)


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable01 : public CtiAnsiTableBase
{

private:
    unsigned char      _manufacturer[4];
    unsigned char      _ed_model[8];
    unsigned char      _hw_version_number;
    unsigned char      _hw_revision_number;
    unsigned char      _fw_version_number;
    unsigned char      _fw_revision_number;

    //this choice is made in the 00 table
    union
    {
       BCD       bcd_sn[8];
       char      char_sn[16];
       UINT64    ll_sn;

    }_mfg_serial_number;

    // table 0
    bool _serialNumberFlag;
    int _idForm;


public:


    CtiAnsiTable01( BYTE *dataBlob, bool sn_flag, bool id_form );
    virtual ~CtiAnsiTable01();
    CtiAnsiTable01& operator=(const CtiAnsiTable01& aRef);

    std::string getRawManufacturer( void );
    std::string getResolvedManufacturer( void );
    std::string getRawModel( void );
    std::string getResolvedModel( void );
    std::string getRawSerialNumber( void );
    std::string getResolvedSerialNumber( void );

    void printResult(const std::string& deviceName );

    int getFWVersionNumber();
    int getFWRevisionNumber();
};
