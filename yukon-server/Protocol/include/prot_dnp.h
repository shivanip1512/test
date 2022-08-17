#pragma once

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"
#include "packet_finder.h"

#include "dnp_application.h"

#include "dnp_object_binaryoutput.h"
#include "dnp_configuration.h"

#include <boost/scoped_ptr.hpp>

#include <map>

namespace Cti::Protocols {

class IM_EX_PROT DnpProtocol : public Interface
{
    enum   Command;
    struct output_point;

    DNP::ApplicationLayer _app_layer;
    DNP::TransportLayer   _transport;
    DNP::DatalinkLayer    _datalink;

    CtiTime _nextTimeComplaint;

    Command _command;
    std::deque<Command> _additional_commands;

    std::vector<output_point> _command_parameters;
    boost::scoped_ptr<DNP::config_data> _config;

    DNP::ApplicationLayer::object_block_queue _object_blocks;

    stringlist_t _string_results;
    pointlist_t  _point_results;

    typedef std::map<unsigned, double> pointtype_values;
    pointtype_values _analog_inputs;
    pointtype_values _binary_inputs;
    pointtype_values _analog_outputs;
    pointtype_values _binary_outputs;
    pointtype_values _counters;

    void recordPoints(int group, const pointlist_t &points);
    std::string pointSummary(unsigned points);
    std::string pointDataReport(const std::map<unsigned, double> &pointdata, unsigned points);

    CtiTime convertFromDeviceTimeOffset(const DNP::Time &dnpTime) const;
    std::unique_ptr<DNP::Time> convertToDeviceTimeOffset(const CtiTime utc) const;

public:

    DnpProtocol();
    ~DnpProtocol();

    void setAddresses( unsigned short slaveAddress, unsigned short masterAddress );
    void setDatalinkConfirm();

    bool setCommand( Command command );
    bool setCommand( Command command, output_point &point );

    void setConfigData( unsigned internalRetries, DNP::TimeOffset timeOffset, bool enableDnpTimesyncs,
                        bool omitTimeRequest, bool enableUnsolicitedClass1, bool enableUnsolicitedClass2,
                        bool enableUnsolicitedClass3, bool enableNonUpdatedOnFailedScan);

    YukonError_t generate( CtiXfer &xfer ) override;
    YukonError_t decode  ( CtiXfer &xfer, YukonError_t status ) override;

    bool isTransactionComplete( void ) const override;

    void getInboundPoints ( pointlist_t  &point_list ) override;
    stringlist_t getInboundStrings() override;

    static std::string getControlResultString( unsigned char result_status );

    enum OutputPointType
    {
        AnalogOutputPointType,
        AnalogOutputFloatPointType,
        DigitalOutputPointType
    };

    struct output_point
    {
        union
        {
            struct ao_point
            {
                double value;
            } aout;

            struct do_point
            {
                DNP::BinaryOutputControl::ControlCode control;
                DNP::BinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
            } dout;
        };

        unsigned long control_offset;
        OutputPointType type;
        unsigned long expiration;
    };

    enum Command
    {
        Command_Invalid = 0,
        Command_WriteTime,
        Command_ReadTime,
        Command_Class0Read,
        Command_Class1Read,
        Command_Class2Read,
        Command_Class3Read,
        Command_Class123Read,
        Command_Class123Read_WithTime,
        Command_Class1230Read,
        Command_Class1230Read_WithTime,
        Command_SetAnalogOut,
        Command_SetDigitalOut_Direct,
        Command_SetDigitalOut_SBO_Select,
        Command_SetDigitalOut_SBO_Operate,
        Command_SetDigitalOut_SBO_SelectOnly,

        Command_ResetDeviceRestartBit,

        Command_Loopback,  
        Command_ReadInternalIndications,  //  actually a time-delay request

        Command_UnsolicitedEnable,
        Command_UnsolicitedDisable,

        Command_UnsolicitedInbound,  //  special case - just greases the wheels for the inbound message

        Command_Complete
    };
};

namespace DNP {

struct DnpPacketFinder : public Protocols::PacketFinder
{
    DnpPacketFinder() :
        PacketFinder(0x05, 0x64, DatalinkLayer::isPacketValid)
    { };
};

}

}

