#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

struct FORMAT_CONTROL_1
{
   unsigned char data_order         :1;
   unsigned char char_format        :3;
   unsigned char model_select       :3;
   unsigned char mfg_sn_flag        :1;
};

struct FORMAT_CONTROL_2
{
   unsigned char tm_format          :3;
   unsigned char data_access_method :2;
   unsigned char id_format          :1;
   unsigned char int_format         :2;
};

struct FORMAT_CONTROL_3
{
   unsigned char ni_format1         :4;
   unsigned char ni_format2         :4;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable00 : public CtiAnsiTableBase
{
   FORMAT_CONTROL_1  _control_1;
   FORMAT_CONTROL_2  _control_2;
   FORMAT_CONTROL_3  _control_3;
   unsigned char     _device_class[4];
   unsigned char     _nameplate_type;
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
   unsigned char     *_std_tbls_used;
   unsigned char     *_mfg_tbls_used;
   unsigned char     *_std_proc_used;
   unsigned char     *_mfg_proc_used;
   unsigned char     *_std_tbls_write;
   unsigned char     *_mfg_tbls_write;

public:

    DataOrder getRawDataOrder( void );
    std::string getResolvedDataOrder( void );
    int getRawCharFormat( void );
    std::string getResolvedCharFormat( void );
    bool getRawMfgSerialNumberFlag( void );
    std::string getResolvedMfgSerialNumberFlag( void );
    int getRawTimeFormat( void );
    std::string getResolvedTimeFormat( void );
    int getRawDataAccess( void );
    std::string getResolvedDataAccess( void );
    bool getRawIdFormat( void );
    std::string getResolvedIdFormat( void );
    int getRawIntFormat( void );
    std::string getResolvedIntFormat( void );
    int getRawNIFormat1( void );
    std::string getResolvedNIFormat1( void );
    int getRawNIFormat2( void );
    std::string getResolvedNIFormat2( void );
    int getRawDeviceClass( void );
    std::string getResolvedDeviceClass( void );
    int getRawNameplateType( void );
    std::string getResolvedNameplateType( void );
    int getRawDefaultSetUsed( void );
    std::string getResolvedDefaultSetUsed( void );
    int getRawMaxProcParmLength( void );
    std::string getResolvedMaxProcParmLength( void );
    int getRawMaxRespDataLen( void );
    std::string getResolvedMaxRespDataLen( void );
    int getRawStdVersionNo( void );
    std::string getResolvedStdVersionNo( void );
    int getRawStdRevisionNo( void );
    std::string getResolvedStdRevisionNo( void );

    unsigned char * getStdTblsUsed(void);
    int getDimStdTblsUsed(void);
    unsigned char * getMfgTblsUsed(void);
    int getDimMfgTblsUsed(void);


    std::string getNonIntegerFormat( int aFormat );

    void printResult( const std::string& deviceName );

    CtiAnsiTable00( BYTE *dataBlob );

    virtual ~CtiAnsiTable00();

    CtiAnsiTable00& operator=(const CtiAnsiTable00& aRef);

};
