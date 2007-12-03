/*-----------------------------------------------------------------------------*
*
* File:   prot_dnp
*
* Class:  CtiProtocolDNP
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2007/12/03 15:20:11 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_DNP_H__
#define __PROT_DNP_H__
#pragma warning( disable : 4786)


#include <list>
#include <map>

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"

#include "dnp_application.h"
#include "dnp_objects.h"
#include "dnp_object_binaryoutput.h"

namespace Cti       {
namespace Protocol  {

using std::list;
using std::map;

using DNP::Application::object_block_queue;

class IM_EX_PROT DNPInterface : public Interface
{
    enum   Command;
    struct output_point;

private:

    DNP::Application _app_layer;  //  be explicit to ensure Slick doesn't confuse it with anything else :rolleyes:
    unsigned short   _masterAddress, _slaveAddress;
    int              _options;
    unsigned long    _last_complaint;

    Command              _command;
    vector<output_point> _command_parameters;

    object_block_queue _object_blocks;

    stringlist_t _string_results;
    pointlist_t  _point_results;

    map<unsigned, double> _analog_inputs;
    map<unsigned, double> _binary_inputs;
    map<unsigned, double> _analog_outputs;
    map<unsigned, double> _binary_outputs;
    map<unsigned, double> _counters;

    map<unsigned, unsigned> _point_count;

    void recordPoints(int group, const pointlist_t &points);
    string pointSummary(unsigned points);
    string pointDataReport(const map<unsigned, double> &pointdata, unsigned points);

    enum
    {
        TimeDifferential  =   60,
        ComplaintInterval = 3600,
    };

    enum Retries
    {
        Retries_Default = 2
    };

    void initLayers( void );

    const char *getControlResultString( int result_status ) const;

protected:

    static const char const *ControlResultStr_RequestAccepted;
    static const char const *ControlResultStr_ArmTimeout;
    static const char const *ControlResultStr_NoSelect;
    static const char const *ControlResultStr_FormattingError;
    static const char const *ControlResultStr_PointNotControllable;
    static const char const *ControlResultStr_QueueFullPointActive;
    static const char const *ControlResultStr_HardwareError;
    static const char const *ControlResultStr_InvalidStatus;

public:

    DNPInterface();
    DNPInterface(const DNPInterface &aRef);

    virtual ~DNPInterface();

    DNPInterface &operator=(const DNPInterface &aRef);

    void setAddresses( unsigned short slaveAddress, unsigned short masterAddress );
    void setOptions( int options );

    bool setCommand( Command command );
    bool setCommand( Command command, output_point &point );
    //bool setCommand( Command command, vector<output_point> &point_vector );

    bool commandRequiresRequeueOnFail( void );
    int  commandRetries( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void ) const;

    virtual void getInboundPoints ( pointlist_t  &point_list );
    virtual void getInboundStrings( stringlist_t &string_list );

    enum OutputPointType
    {
        AnalogOutput,
        DigitalOutput
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
        Options_DatalinkConfirm = 0x01
    };

    enum
    {
        DefaultMasterAddress =    5,
        DefaultSlaveAddress  =    1
    };
};

}
}


#endif // #ifndef __PROT_DNP_H__
