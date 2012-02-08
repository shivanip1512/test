#pragma once

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"
#include "packet_finder.h"

#include "dnp_application.h"
#include "dnp_objects.h"
#include "dnp_object_binaryoutput.h"

#include <map>

namespace Cti {
namespace Protocol {

class IM_EX_PROT DNPInterface : public Interface
{
    enum   Command;
    struct output_point;

private:

    DNP::ApplicationLayer _app_layer;
    unsigned short   _masterAddress, _slaveAddress;
    int              _options;
    unsigned long    _last_complaint;

    Command              _command;
    std::vector<output_point> _command_parameters;

    DNP::ApplicationLayer::object_block_queue _object_blocks;

    stringlist_t _string_results;
    pointlist_t  _point_results;

    std::map<unsigned, double> _analog_inputs;
    std::map<unsigned, double> _binary_inputs;
    std::map<unsigned, double> _analog_outputs;
    std::map<unsigned, double> _binary_outputs;
    std::map<unsigned, double> _counters;

    std::map<unsigned, unsigned> _point_count;

    void recordPoints(int group, const pointlist_t &points);
    std::string pointSummary(unsigned points);
    std::string pointDataReport(const std::map<unsigned, double> &pointdata, unsigned points);

    enum
    {
        TimeDifferential  =   60,
        ComplaintInterval = 3600,
    };

    const char *getControlResultString( int result_status ) const;

protected:

    static const char * const ControlResultStr_RequestAccepted;
    static const char * const ControlResultStr_ArmTimeout;
    static const char * const ControlResultStr_NoSelect;
    static const char * const ControlResultStr_FormattingError;
    static const char * const ControlResultStr_PointNotControllable;
    static const char * const ControlResultStr_QueueFullPointActive;
    static const char * const ControlResultStr_HardwareError;
    static const char * const ControlResultStr_InvalidStatus;

    DNP::ApplicationLayer& getApplicationLayer();
    Command getCommand();
    void addStringResults(std::string *s);

public:

    DNPInterface();

    void setAddresses( unsigned short slaveAddress, unsigned short masterAddress );
    void setOptions( int options );

    bool setCommand( Command command );
    bool setCommand( Command command, output_point &point );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

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
        Command_Class1230Read,
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




class IM_EX_PROT DNPSlaveInterface : public DNPInterface
{
    struct input_point;

    typedef DNPInterface Inherited;
    std::vector<input_point> _input_point_list;
    void addObjectBlock(DNP::ObjectBlock *objBlock);

public:

    DNPSlaveInterface();

    bool setSlaveCommand( Command command );
    void setOptions( int options, int seqNumber=0 );

    int slaveGenerate( CtiXfer &xfer );
    void slaveTransactionComplete();

    void addInputPoint(const input_point &ip);

    enum InputPointType
    {
        AnalogInputType,
        DigitalInput,
        Counters
    };

    struct input_point
    {
        union
        {
            struct ai_point
            {
                double value;
            } ain;

            struct di_point
            {
                DNP::BinaryOutputControl::ControlCode control;
                DNP::BinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
            } din;

            struct ci_point
            {
                double value;
            } counterin;
        };

        unsigned long control_offset;
        InputPointType type;
        unsigned long expiration;
        bool online;
        bool includeTime;
        CtiTime timestamp;
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

