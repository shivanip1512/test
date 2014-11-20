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

namespace Cti {
namespace Protocols {

class IM_EX_PROT DnpProtocol : public Interface
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    DnpProtocol(const DnpProtocol&);
    DnpProtocol& operator=(const DnpProtocol&);

    enum   Command;
    struct output_point;

private:

    DNP::ApplicationLayer _app_layer;
    unsigned short   _masterAddress, _slaveAddress;
    int              _options;
    unsigned long    _last_complaint;

    typedef std::deque<Command>  Command_deq;
    typedef Command_deq::iterator Command_deq_itr;

    Command     _command;
    Command_deq _additional_commands;

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

    enum
    {
        TimeDifferential  =   60,
        ComplaintInterval = 3600,
    };

    enum IINStatusPointOffset
    {
        IINStatusPointOffset_RestartBit = 2001
    };

    const char *getControlResultString( int result_status ) const;

protected:

    DNP::ApplicationLayer& getApplicationLayer();
    Command getCommand();
    void addStringResults(std::string *s);

    unsigned convertLocalSecondsToUtcSeconds( const unsigned seconds );
    unsigned convertUtcSecondsToLocalSeconds( const unsigned seconds );

public:

    DnpProtocol();
    virtual ~DnpProtocol();

    void setAddresses( unsigned short slaveAddress, unsigned short masterAddress );
    void setOptions( int options );

    bool setCommand( Command command );
    bool setCommand( Command command, output_point &point );

    void setConfigData( unsigned internalRetries, bool useLocalTime, bool enableDnpTimesyncs,
                        bool omitTimeRequest, bool enableUnsolicitedClass1,
                        bool enableUnsolicitedClass2, bool enableUnsolicitedClass3 );

    void setInternalRetries( unsigned retries ) const;

    YukonError_t generate( CtiXfer &xfer );
    YukonError_t decode  ( CtiXfer &xfer, YukonError_t status );

    bool isTransactionComplete( void ) const;

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

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

        Command_Loopback,  //  actually a time-delay request

        Command_UnsolicitedEnable,
        Command_UnsolicitedDisable,

        Command_UnsolicitedInbound,  //  special case - just greases the wheels for the inbound message

        Command_Complete
    };

    enum Options
    {
        //  to be logically OR'd together - keep bit patterns unique
        Options_None            = 0x00,
        Options_DatalinkConfirm = 0x01,
        Options_SlaveResponse   = 0x40
    };

    enum
    {
        DefaultMasterAddress =    5,
        DefaultSlaveAddress  =    1
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
}

