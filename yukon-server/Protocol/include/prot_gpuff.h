#pragma once

#include "dlldefs.h"

#include "msg_pdata.h"
#include "packet_finder.h"

#include <string>
#include <vector>

namespace Cti       {
namespace Protocols {

class IM_EX_PROT GpuffProtocol : private boost::noncopyable
{
    typedef std::vector< CtiPointDataMsg * > pointlist_t;
    typedef std::vector< std::string * >     stringlist_t;

    GpuffProtocol();

    enum
    {
        HeaderLength = 13
    };

    template<typename T>
    static void serialize(std::vector<unsigned char> &dst, const T src );

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
        GCVTx_Status_Fault                = 1,
        GCVTx_Status_NoPower              = 2,
        GCVTx_Status_ReedSwitch           = 3,
        GCVTx_Status_Momentary            = 4,
        GCVTx_Status_RSSI                 = 100,    // 5 state status 0-4 "bars".

        GCVTx_Analog_NominalAmps          =   1,
        GCVTx_Analog_PeakAmps             =   2,
        GCVTx_Analog_BatteryVoltage       =   3,
        GCVTx_Analog_Temperature          =   4,
        GCVTx_Analog_Latitude             =  11,
        GCVTx_Analog_Longitude            =  12,
        GCVTx_Analog_CurrentSurvey        =  31,
        GCVTx_Analog_RSSI                 = 100,
        GCVTx_Analog_BER                  = 101,

        GCVTx_Accum_MomentaryCount        =   1,

    };

    enum GVAR1APoints
    {
        GVARx_Status_OverCurrent       = 1,
        GVARx_Status_ResetOverCurrent  = 2,
        GVARx_Status_ReedSwitch        = 3,
        GVARx_Status_Calibrated        = 10,
        GVARx_Status_RSSI              = 100,   // 5 state status 0-4 bars.

        GVARx_Analog_MaxCurrentReport  =   1,
        GVARx_Analog_MinCurrentReport  =   2,
        GVARx_Analog_BatteryVoltage    =   3,
        GVARx_Analog_Temperature       =   4,
        GVARx_Analog_MaxCurrentPeriod  =   5,
        GVARx_Analog_MinCurrentPeriod  =   6,
        GVARx_Analog_Period            =   10,
        GVARx_Analog_HighAlertSetpoint =   11,
        GVARx_Analog_ResetAlertSetpoint=   12,
        GVARx_Analog_CurrentSurvey     =   31,
        GVARx_Analog_RSSI              =  100,
        GVARx_Analog_BER               =  101,
    };

    struct decoded_packet
    {
        pointlist_t points;
        bool ack_required;
        boost::uint16_t seq;
        boost::uint16_t devt;
        boost::uint8_t  devr;
        boost::uint32_t ser;
        boost::uint16_t cid;
    };

    static void describeFrame(unsigned char *p_data, int p_len, int len, bool crc_included, bool ack_required, int devt, int ser);

    //int generate( CtiXfer &xfer );
    static decoded_packet decode( const unsigned char *p_data, unsigned last_seq, const std::string device_name );

    static std::vector<unsigned char> generateAck(decoded_packet p);

    static bool isPacketValid( const unsigned char *buf, const size_t len );

    struct GpuffPacketFinder : public PacketFinder
    {
    public:

        GpuffPacketFinder() :
            PacketFinder(0xa5, 0x96, isPacketValid)
        { };
    };

    //bool isTransactionComplete( void ) const;
};


}
}

