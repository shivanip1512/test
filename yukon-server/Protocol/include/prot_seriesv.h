/*-----------------------------------------------------------------------------*
*
* File:   prot_seriesv
*
* Class:  CtiProtocolSeriesV
* Date:   2004-jan-14
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/05/11 18:31:26 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_SERIESV_H__
#define __PROT_SERIESV_H__
#pragma warning( disable : 4786)

#include <map>
#include <vector>
#include <queue>
using namespace std;

#include "boost/crc.hpp"
using boost::crc_16_type;

#include "dlldefs.h"
#include "prot_base.h"
#include "pointtypes.h"

class IM_EX_PROT CtiProtocolSeriesV : public CtiProtocolBase
{
public:
    enum SeriesVCommand
    {
        Command_ScanAccumulator = 2,
        Command_ScanException,
        Command_ScanIntegrity,
        Command_Control,
        Command_Loopback,
        Command_AnalogSetpoint,
        Command_AnalogSetpointDirect
    };

private:
    crc_16_type _crc;

    SeriesVCommand _command;

    int _scan_startpoint, _scan_numpoints,
        _control_offset,  _control_parameter;

    unsigned char  _address;

    bool           _configRead;

    bool           _deadbandsSent;
    map<int, int>  _deadbands;

    int _analog_min, _analog_max,
        _status_min, _status_max,
        _accum_min,  _accum_max,
        _setpt_min,  _setpt_max;

    int _retry_count;

    enum SeriesVOpcode
    {
        Opcode_Invalid           = 0,
        Opcode_ScanAnalog        = 1,
        Opcode_ScanAccumulator,
        Opcode_ScanStatus,

        Opcode_ControlSelect,
        Opcode_ControlExecute,

        Opcode_AccumulatorFreeze = 7,
        Opcode_AccumulatorReset,
        Opcode_AccumulatorFreezeAndReset,

        Opcode_StatusChangeDumpCOS,

        Opcode_RTUStatusClear,
        Opcode_RTUConfigRequest,

        Opcode_AnalogDeadbandDownload,
        Opcode_AnalogChangeCountRequest,
        Opcode_AnalogChangeDump,
        Opcode_AnalogOutputSelect           = 16,
        Opcode_AnalogOutputOperate          = 20,
        Opcode_AnalogOutputDirectControl    = 24,

        Opcode_AnalogSetpointScan = 28,

        Opcode_SOETimeSync,
        Opcode_StatusChangeDumpSOE,

        Opcode_PulseOutputNoReply = 34,
        Opcode_PulseOutput
    };

    enum SeriesVStates
    {
        State_Invalid,
        State_Init,
        State_Complete,

        //  Accumulator scan
        State_RequestAccumulators,

        //  Integrity scan
        State_RequestAnalogs,
        State_RequestAnalogOutputs,
        State_RequestStatuses,

        //  Exception scan
        State_RequestAnalogChangeCount,
        State_RequestAnalogChangeDump,
        State_RequestCOSDump,

        //  Controls
        State_ControlSelect,
        State_ControlExecute,

        State_AnalogOutputSelect,
        State_AnalogOutputExecute,

        State_AnalogOutputDirectControl,

        //  Misc
        State_Loopback

        //  Accumulator freeze/reset
        //  Time sync
    } _state;

    enum SeriesVConstants
    {
        DefaultDeadband    =     7,
        MaxInbound         =  1024,
        Retries            =     3,
        NumAnalogSetpoints =    64,
        SelectTimeout      =    50,  //  10 seconds
        AnalogOutputOffset = 10000
    };

    struct seriesv_outmess_struct
    {
        SeriesVCommand command;

        unsigned short control_offset;
        unsigned short control_parameter;  //  for controls, it's duration;  for setpoints, it's value
    };

    struct seriesv_inmess_struct
    {
        unsigned short num_points;
    };

#pragma pack(push, 1)

    struct seriesv_master_packet
    {
        unsigned char station_id : 7;
        unsigned char direction  : 1;

        unsigned char opcode     : 6;
        unsigned char aber       : 1;
        unsigned char cosr       : 1;

        unsigned char payload[2];

        unsigned char crc[2];
    } _outbound;

    union seriesv_status
    {
        struct
        {
            unsigned char power_cycle          : 1;
            unsigned char request_questionable : 1;
            unsigned char config_questionable  : 1;
            unsigned char accumulators_frozen  : 1;
            unsigned char hardware_malfunction : 1;
            unsigned char control_failure      : 1;
            unsigned char unused               : 2;
        } bits;

        unsigned char raw;
    };

    struct seriesv_slave_packet
    {
        unsigned char station_id : 7;
        unsigned char direction  : 1;

        seriesv_status status;

        unsigned char cos_count;

        unsigned char data[1024];
    };

    unsigned char *_inbound;

    struct seriesv_info
    {
        int analog_count;
        int accum_count;
        int status_count;
        seriesv_status status;
        int cos_count;
        int analog_change_count;
        bool cosr;
        bool aber;
    } _rtu_info;

    struct seriesv_pointdata
    {
        unsigned short offset;
        unsigned short value;
        unsigned long  time;
        CtiPointType_t type;
    };

    queue< seriesv_pointdata > _collected_points;
    queue< seriesv_pointdata > _returned_points;

#pragma pack(pop)

protected:

    int expectedResponseSize( unsigned char opcode, int points_requested = 0 );
    void setRange( int point_count, int point_min, int point_max, unsigned char &req_begin, unsigned char &req_count );

public:
    CtiProtocolSeriesV();
    CtiProtocolSeriesV(const CtiProtocolSeriesV &aRef);

    virtual ~CtiProtocolSeriesV();

    CtiProtocolSeriesV &operator=(const CtiProtocolSeriesV &aRef);

    void setAddress( unsigned char address );

    void setCommand( SeriesVCommand cmd );
    void setCommandControl( SeriesVCommand cmd, unsigned control_offset, unsigned control_parameter );

    void setDeadbands( const vector<unsigned> &points, const vector<unsigned> &deadbands );
    void setAnalogPoints    ( int min, int max );
    void setStatusPoints    ( int min, int max );
    void setAccumPoints     ( int min, int max );
    void setAnalogOutPoints ( int min, int max );

    //  client-side (Scanner, PIL) functions
    int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int recvCommResult ( INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList );

    bool hasInboundPoints( void );
    void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    //  porter-side (portfield, specificially) functions
    int recvCommRequest( OUTMESS *OutMessage );
    int sendCommResult ( INMESS  *InMessage );

    bool isTransactionComplete( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    int generateLoopback( CtiXfer &xfer );
    int decodeLoopback( CtiXfer &xfer, int status );
    int generateScanAccumulator( CtiXfer &xfer );
    int decodeScanAccumulator( CtiXfer &xfer, int status );
/*    int generateScanException( CtiXfer &xfer );
    int decodeScanException( CtiXfer &xfer, int status );
    int generateScanIntegrity( CtiXfer &xfer );
    int decodeScanIntegrity( CtiXfer &xfer, int status );*/
    int generateControl( CtiXfer &xfer );
    int decodeControl( CtiXfer &xfer, int status );
    int generateAnalogSetpoint( CtiXfer &xfer );
    int decodeAnalogSetpoint( CtiXfer &xfer, int status );

    enum
    {
        PassthroughCRC = 0x1234
    };
};

#endif // #ifndef __PROT_SERIESV_H__
