#pragma once
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "msg_pdata.h"
#include <string>

namespace Cti       {
namespace Protocols {

class IM_EX_PROT GpuffProtocol
{
    typedef std::vector< CtiPointDataMsg * > pointlist_t;
    typedef std::vector< std::string * >     stringlist_t;

private:

    GpuffProtocol();
    //virtual ~GpuffProtocol();
    GpuffProtocol &operator=(const GpuffProtocol &aRef);

    enum
    {
        HeaderLength = 13
    };

protected:

    static void add_to_csv_summary( std::string &keys, std::string &values, std::string key, bool     value );
    static void add_to_csv_summary( std::string &keys, std::string &values, std::string key, int      value );
    static void add_to_csv_summary( std::string &keys, std::string &values, std::string key, unsigned value );
    static void add_to_csv_summary( std::string &keys, std::string &values, std::string key, float    value );
    static void add_to_csv_summary( std::string &keys, std::string &values, std::string key, std::string value );

    static std::string convertBytesToString( const unsigned char *buf, int &position, int bytes_to_combine );
    static unsigned int convertBytes( const unsigned char *buf, int &position, int bytes_to_combine );
    static int convertSignedBytes( const unsigned char *buf, int &position, int bytes_to_combine );

public:

    enum FCIPoints
    {
        FCI_Status_Fault      = 1,
        FCI_Status_NoPower    = 2,
        FCI_Status_BatteryLow = 3,      // per YUK-4101
        FCI_Status_Momentary  = 4,      //
        FCI_Status_CurrentSurveyEnabled = 101,
        FCI_Status_UDPAck               = 102,

        FCI_Analog_NominalAmps    =   1,
        FCI_Analog_PeakAmps       =   2,
        FCI_Analog_Phase          =   3,
        FCI_Analog_Temperature    =   4,
        FCI_Analog_BatteryVoltage =   5,
        FCI_Analog_Latitude       =  11,
        FCI_Analog_Longitude      =  12,
        FCI_Analog_FaultHistory   =  21,
        FCI_Analog_CurrentSurvey  =  31,
        FCI_Analog_UDPRepeats     = 101,

        FCI_Accum_MomentaryCount =   1,
    };

    enum GVAR1APoints
    {
        GVAR1A_Status_OverCurrent      = 1,
        GVAR1A_Status_ResetOverCurrent = 2,
        GVAR1A_Status_ReedSwitch       = 3,
        GVAR1A_Status_Calibrated       = 10,

        GVAR1A_Analog_MaxCurrentReport  =   1,
        GVAR1A_Analog_MinCurrentReport  =   2,
        GVAR1A_Analog_Battery           =   3,
        GVAR1A_Analog_Temperature       =   4,
        GVAR1A_Analog_MaxCurrentPeriod  =   5,
        GVAR1A_Analog_MinCurrentPeriod  =   6,
        GVAR1A_Analog_Period            =   10,
        GVAR1A_Analog_HighAlertSetpoint =   11,
        GVAR1A_Analog_ResetAlertSetpoint=   12,

        GVAR1A_Analog_RSSI              =  100,
        GVAR1A_Analog_BER               =  101,
    };

    enum CBNMPoints
    {
        //CBNM_Status_Fault      = 1,
        CBNM_Status_NoPower    = 2,
        CBNM_Status_BatteryLow = 3,      // per YUK-4101
        CBNM_Status_Momentary  = 4,      //
        CBNM_Status_CurrentSurveyEnabled = 101,
        CBNM_Status_UDPAck               = 102,

        CBNM_Analog_NominalAmps    =   1,
        CBNM_Analog_PeakAmps       =   2,
        CBNM_Analog_Phase          =   3,
        CBNM_Analog_Temperature    =   4,
        CBNM_Analog_BatteryVoltage =   5,
        CBNM_Analog_Latitude       =  11,
        CBNM_Analog_Longitude      =  12,
        CBNM_Analog_CurrentSurvey  =  31,
        CBNM_Analog_UDPRepeats     = 101,

        CBNM_Accum_MomentaryCount =   1,
    };

    static void describeFrame(unsigned char *p_data, int p_len, int len, bool crc_included, bool ack_required, int devt, int ser);

    //int generate( CtiXfer &xfer );
    static unsigned decode( const unsigned char *p_data, unsigned last_seq, const std::string device_name, pointlist_t &point_list );

    static bool isPacketValid( const unsigned char *buf, const int len );
    static bool findPacket(unsigned char *&itr, unsigned char *&end);

    //bool isTransactionComplete( void ) const;
};


}
}

