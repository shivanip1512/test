/*-----------------------------------------------------------------------------*
*
* File:   prot_seriesv
*
* Date:   2004-jan-14
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2008/04/25 21:45:14 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "prot_seriesv.h"
#include "logger.h"
#include "porter.h"
using namespace std;

CtiProtocolSeriesV::CtiProtocolSeriesV() :
    _deadbandsSent(false),
    _configRead(false),
    _analog_min(-1), _analog_max(-1),
    _status_min(-1), _status_max(-1),
    _accum_min(-1),  _accum_max(-1),
    _setpt_min(-1),  _setpt_max(-1),
    _command(Command_ScanAccumulator),
    _scan_startpoint(0),
    _scan_numpoints(0),
    _control_offset(0),
    _control_parameter(0),
    _address(0),
    _retry_count(0),
    _state(State_Invalid)
{
    _rtu_info.accum_count  = 0;
    _rtu_info.analog_count = 0;
    _rtu_info.status_count = 0;
    _rtu_info.aber = 0;
    _rtu_info.cosr = 0;

    _inbound = CTIDBG_new unsigned char[MaxInbound];

    memset( &_outbound, 0, sizeof(seriesv_master_packet) );
}

CtiProtocolSeriesV::~CtiProtocolSeriesV()
{
    delete [] _inbound;
}


void CtiProtocolSeriesV::setAddress( unsigned char address )
{
    _address = address;
}


void CtiProtocolSeriesV::setCommand( SeriesVCommand cmd )
{
    _command = cmd;
    _state   = State_Init;

    _retry_count = 0;
}

void CtiProtocolSeriesV::setCommandControl( SeriesVCommand cmd, unsigned control_offset, unsigned control_parameter )
{
    setCommand(cmd);

    _control_offset    = control_offset;
    _control_parameter = control_parameter;
}


void CtiProtocolSeriesV::setDeadbands( const vector<unsigned> &points, const vector<unsigned> &deadbands )
{
    vector<unsigned>::const_iterator pt_itr, db_itr;

    pt_itr = points.begin();
    db_itr = deadbands.begin();

    for( ; pt_itr != points.end() && db_itr != deadbands.end(); pt_itr++, db_itr++ )
    {
        _deadbands.insert(make_pair(*pt_itr, *db_itr));
    }
}


//  MAGIC NUMBER WARNING:  all of the below do a one-based to zero-based conversion
void CtiProtocolSeriesV::setAnalogPoints( int min, int max )
{
    _analog_min = min - 1;
    _analog_max = max - 1;
}

void CtiProtocolSeriesV::setStatusPoints( int min, int max )
{
    _status_min = min - 1;
    _status_max = max - 1;
}

void CtiProtocolSeriesV::setAccumPoints( int min, int max )
{
    _accum_min = min - 1;
    _accum_max = max - 1;
}

void CtiProtocolSeriesV::setAnalogOutPoints( int min, int max )
{
    _setpt_min = min - 1;
    _setpt_max = max - 1;
}


YukonError_t CtiProtocolSeriesV::sendCommRequest( OUTMESS *&OutMessage, list< OUTMESS* > &outList )
{
    YukonError_t retVal = ClientErrors::None;

    seriesv_outmess_struct tmp_om_struct;

    if( OutMessage != NULL )
    {
        tmp_om_struct.command = _command;
        tmp_om_struct.control_offset    = 0;  //  control point
        tmp_om_struct.control_parameter = 0;  //  control duration, setpoint value

        memcpy( OutMessage->Buffer.OutMessage, &tmp_om_struct, sizeof(tmp_om_struct) );
        OutMessage->OutLength = sizeof(tmp_om_struct);

        OutMessage->EventCode = RESULT;

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        retVal = ClientErrors::Memory;
    }

    return retVal;
}


YukonError_t CtiProtocolSeriesV::recvCommResult( const INMESS &InMessage, list< OUTMESS* > &outList )
{
    const seriesv_inmess_struct &response = *((const seriesv_inmess_struct *)InMessage.Buffer.InMessage);
    const seriesv_pointdata *pd = (const seriesv_pointdata *)(InMessage.Buffer.InMessage + sizeof(seriesv_inmess_struct));

    for( int i = 0; i < response.num_points; i++ )
    {
        _returned_points.push(pd[i]);
    }

    return ClientErrors::None;
}


bool CtiProtocolSeriesV::hasInboundPoints( void )
{
    return !_returned_points.empty();
}


void CtiProtocolSeriesV::getInboundPoints( list< CtiPointDataMsg* > &pointList )
{
    CtiPointDataMsg *pdm;

    while( !_returned_points.empty() )
    {
        seriesv_pointdata &pd = _returned_points.front();

        pdm = CTIDBG_new CtiPointDataMsg(pd.offset, pd.value, NormalQuality, pd.type);
        pdm->setTime(pd.time);
        pdm->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);

        pointList.push_back(pdm);

        _returned_points.pop();
    }
}


YukonError_t CtiProtocolSeriesV::recvCommRequest( OUTMESS *OutMessage )
{
    seriesv_outmess_struct &request = *((seriesv_outmess_struct *)OutMessage->Buffer.OutMessage);

    if( request.control_offset )
    {
        setCommandControl(request.command, request.control_offset, request.control_parameter);
    }
    else
    {
        setCommand(request.command);
    }

    return ClientErrors::None;
}


YukonError_t CtiProtocolSeriesV::sendCommResult( INMESS &InMessage )
{
    int offset = 0;

    seriesv_inmess_struct &response = *((seriesv_inmess_struct *)InMessage.Buffer.InMessage);
    seriesv_pointdata *pd = (seriesv_pointdata *)(InMessage.Buffer.InMessage + sizeof(seriesv_inmess_struct));

    response.num_points = _collected_points.size();

    while( !_collected_points.empty() )
    {
        pd[offset++] = _collected_points.front();

        _collected_points.pop();
    }

    InMessage.InLength = sizeof(seriesv_inmess_struct) + (sizeof(seriesv_pointdata) * response.num_points);

    return ClientErrors::None;
}


bool CtiProtocolSeriesV::isTransactionComplete( void ) const
{
    return (_state == State_Complete) || (_retry_count >= Retries);
}


YukonError_t CtiProtocolSeriesV::generate( CtiXfer &xfer )
{
    unsigned char opcode = Opcode_Invalid,
                  param1 = 0,
                  param2 = 0,
                  points_requested = 0;
    YukonError_t retval = ClientErrors::None;

    if( !_configRead )
    {
        opcode = Opcode_RTUConfigRequest;
    }
    else if( _rtu_info.status.raw )  //  if any of the bits are set
    {
        opcode = Opcode_RTUStatusClear;

        param1 = _rtu_info.status.raw;
    }
    else
    {
        switch( _command )
        {
            case Command_Loopback:
            {
                opcode = Opcode_RTUStatusClear;

                param1 = 0;
                param2 = 0;

                break;
            }
            case Command_ScanException:
            case Command_ScanIntegrity:
            case Command_ScanAccumulator:
            {
                switch( _state )
                {
                    case State_Init:
                    case State_RequestAccumulators:
                    {
                        _state = State_RequestAccumulators;
                        opcode = Opcode_ScanAccumulator;

                        setRange( _rtu_info.accum_count, _accum_min, _accum_max, param1, param2 );
                        points_requested = param2;

                        if( points_requested )
                        {
                            break;
                        }
                        //  else fall through, we're skipping this state!
                    }
                    case State_RequestAnalogs:
                    {
                        _state = State_RequestAnalogs;
                        opcode = Opcode_ScanAnalog;

                        setRange( _rtu_info.analog_count, _analog_min, _analog_max, param1, param2 );
                        points_requested = param2;

                        if( points_requested )
                        {
                            break;
                        }
                        //  else fall through, we're skipping this state!
                    }
/*                    case State_RequestAnalogOutputs:
                    {
                        _state = State_RequestAnalogOutputs;
                        opcode = Opcode_AnalogSetpointScan;

                        setRange( 1, _setpt_min, _setpt_max, param1, param2 );
                        points_requested = param2;

                        if( points_requested )
                        {
                            break;
                        }
                        //  else fall through, we're skipping this state!
                    }*/
                    case State_RequestStatuses:
                    {
                        _state = State_RequestStatuses;
                        opcode = Opcode_ScanStatus;

                        setRange( _rtu_info.status_count, _status_min, _status_max, param1, param2 );
                        points_requested = param2;

                        if( !points_requested )
                        {
                            //  we need a more elegant way of skipping out of this...
                            //    it'll probably require a rewrite of the decode routine, since that
                            //    wants to see a valid incoming packet before it does anything
                            opcode = Opcode_Invalid;
                        }

                        break;
                    }

                    default:
                    {
                        opcode = Opcode_Invalid;
                    }
                }

                break;
            }
            case Command_Control:
            {
                switch( _state )
                {
                    case State_Init:
                    case State_ControlSelect:
                    {
                        opcode = Opcode_ControlSelect;

                        param1 = SelectTimeout;
                        param2 = _control_offset;

                        break;
                    }
                    case State_ControlExecute:
                    {
                        opcode = Opcode_ControlExecute;

                        param1 = _control_parameter;  //  ACH:  adjust execute time for ms
                        param2 = _control_offset;

                        break;
                    }

                    default:
                    {
                        opcode = Opcode_Invalid;
                    }
                }

                break;
            }
            case Command_AnalogSetpointDirect:
            case Command_AnalogSetpoint:
            {
                switch( _state )
                {
                    case State_Init:
                    {
                        if( _command == Command_AnalogSetpointDirect )  _state = State_AnalogOutputDirectControl;
                        if( _command == Command_AnalogSetpoint )        _state = State_AnalogOutputSelect;
                    }
                    case State_AnalogOutputSelect:
                    case State_AnalogOutputExecute:
                    case State_AnalogOutputDirectControl:
                    {
                        //  they're exactly the same, which is kind of convenient
                        if( _state == State_AnalogOutputSelect )        opcode = Opcode_AnalogOutputSelect;
                        if( _state == State_AnalogOutputExecute )       opcode = Opcode_AnalogOutputOperate;
                        if( _state == State_AnalogOutputDirectControl ) opcode = Opcode_AnalogOutputDirectControl;

                        opcode += _control_offset / 16;

                        param1  = (_control_offset % 16) << 4;
                        param1 |= (_control_parameter & 0xf00) >> 8;
                        param2  =  _control_parameter & 0x0ff;

                        break;
                    }

                    default:
                    {
                        opcode = Opcode_Invalid;
                    }
                }

                break;
            }
            default:
            {
                opcode = Opcode_Invalid;

                CTILOG_ERROR(dout, "invalid command \""<< _command <<"\"");
            }
        }
    }

    if( opcode != Opcode_Invalid )
    {
        _outbound.station_id  = _address;
        _outbound.opcode      = opcode;
        _outbound.direction   = 1;
        _outbound.aber        = _rtu_info.aber;
        _outbound.cosr        = _rtu_info.cosr;
        _outbound.payload[0]  = param1;
        _outbound.payload[1]  = param2;

        _crc.process_bytes((void *)&_outbound, sizeof(_outbound) - 2);

        _outbound.crc[0]     =  _crc.checksum() & 0x00ff;
        _outbound.crc[1]     = (_crc.checksum() & 0xff00) >> 8;

        xfer.setOutBuffer((unsigned char *)&_outbound);
        xfer.setOutCount(sizeof(_outbound));

        xfer.setInBuffer(_inbound);
        xfer.setInCountExpected(expectedResponseSize(opcode, points_requested));
    }
    else
    {
        CTILOG_ERROR(dout, "opcode not assigned");

        xfer.setOutBuffer(NULL);
        xfer.setOutCount(0);
        xfer.setInBuffer(NULL);
        xfer.setInCountExpected(0);
    }

    return retval;
}


/*
            case State_RequestAnalogChangeCount:
            {
                opcode = Opcode_AnalogChangeCountRequest;

                break;
            }
            case State_RequestAnalogChangeDump:
            {
                opcode = Opcode_AnalogChangeDump;

                param2 = _rtu_info.analog_change_count;
                points_requested = param2;

                break;
            }
            case State_RequestCOSDump:
            {
                opcode = Opcode_StatusChangeDumpCOS;

                param1 = _rtu_info.cos_count;
                points_requested = param1;

                break;
            }
*/

/*
            case Opcode_AnalogChangeCountRequest:
            {
                _rtu_info.analog_change_count = packet.data[0];

                _state = State_RequestAnalogChangeDump;

                break;
            }

            case Opcode_AnalogChangeDump:
            {
                for( int i = 0; i < _outbound.payload[1]; i++ )
                {
                    pd.offset = i + _outbound.payload[0];
                    pd.time    = Now.seconds();
                    pd.type    = StatusPointType;
                    pd.value   = (packet.data[i/8] >> (i % 8)) & 0x01;

                    _collected_points.push(pd);
                }

                _rtu_info.aber = true;

                _state = State_RequestCOSDump;

                break;
            }

            case Opcode_StatusChangeDumpCOS:
            {
                for( int i = 0; i < _outbound.payload[1]; i++ )
                {
                    pd.offset = i + _outbound.payload[0];
                    pd.time    = Now.seconds();
                    pd.type    = StatusPointType;
                    pd.value   = (packet.data[i/8] >> (i % 8)) & 0x01;

                    _collected_points.push(pd);
                }

                _rtu_info.cosr = true;

                _state = State_Complete;

                break;
            }
*/



void CtiProtocolSeriesV::setRange( int point_count, int point_min, int point_max, unsigned char &req_begin, unsigned char &req_count )
{
    if( point_min < point_count && point_min >= 0 )
    {
        req_begin = point_min;
    }
    else
    {
        req_begin = 0;
    }

    if( point_max < point_count && point_max >= req_begin )
    {
        req_count = point_max   - req_begin + 1;  //  for example, 0 - 0 + 1
    }
    else
    {
        req_count = point_count - req_begin;      //  for example, 1 - 0
    }
}



YukonError_t CtiProtocolSeriesV::decode( CtiXfer &xfer, YukonError_t status )
{
    YukonError_t retval = ClientErrors::None;
    seriesv_pointdata pd;
    CtiTime Now;
    
    if (xfer.getInCountActual() < 5) 
    {
        CTILOG_ERROR(dout, "Value of _inCountActual() < 5");
        return ClientErrors::BadLength;
    }
    
    unsigned long in_actual = xfer.getInCountActual();
    unsigned long in_body   = in_actual - 3;

    if( status )
    {
        retval = status;
        //  check for short return!
    }
    else if( _state != State_Invalid )
    {
        const seriesv_slave_packet &packet = *((seriesv_slave_packet *)_inbound);

        //  NOTE - i am ONLY checking for the passthrough CRC, NOT the crc16!  Add crc16 later if need be!
        if( (packet.data[in_body - 2] == (CtiProtocolSeriesV::PassthroughCRC & 0x00ff) &&
             packet.data[in_body - 1] == (CtiProtocolSeriesV::PassthroughCRC & 0xff00) >> 8) )
        {
            if( packet.station_id != _address )
            {
                CTILOG_ERROR(dout, "incoming address ("<< (int)packet.station_id <<") doesn't match ("<< (int)_address <<")");

                retval = ClientErrors::Address;
            }
            else
            {
                _rtu_info.status = packet.status;

                //  clear them out ONLY after a successful comm return
                _rtu_info.aber = false;
                _rtu_info.cosr = false;

                if( _rtu_info.status.bits.request_questionable )
                {
                    //  i don't think any other status prohibits decode... ?
                    CTILOG_ERROR(dout, "RTU status: request questionable");

                    retval = ClientErrors::Abnormal;
                }
                else if( !_configRead )
                {
                    _rtu_info.analog_count = packet.data[0];
                    _rtu_info.status_count = packet.data[1];
                    _rtu_info.accum_count  = packet.data[2];

                    _configRead = true;
                }
                else
                {
                    //if( packet.

                    switch( _command )
                    {
                        case Command_Loopback:
                        {
                            break;  //  noop, all we wanted was the reply
                        }
                        /*case Command_ScanException:
                        case Command_ScanIntegrity:    */
                        case Command_ScanAccumulator:
                        {
                            switch( _state )
                            {
                                case State_Init:
                                case State_RequestAccumulators:
                                {
                                    for( int i = 0; i < _outbound.payload[1]; i++ )
                                    {
                                        pd.offset  = i + _outbound.payload[0];
                                        pd.time    = Now.seconds();
                                        pd.type    = PulseAccumulatorPointType;
                                        pd.value   = (packet.data[i*2] << 8) | packet.data[i*2+1];

                                        _collected_points.push(pd);
                                    }

                                    _state = State_RequestAnalogs;

                                    break;
                                }
                                case State_RequestAnalogs:
                                {
                                    for( int i = 0; i < _outbound.payload[1]; i++ )
                                    {
                                        pd.offset  = i + _outbound.payload[0];
                                        pd.time    = Now.seconds();
                                        pd.type    = AnalogPointType;

                                        //  these analogs are 16-bit signed - make sure that it's cast properly
                                        pd.value  = (short)((packet.data[i*2] << 8) | packet.data[i*2+1]);

                                        _collected_points.push(pd);
                                    }

                                    _state = State_RequestStatuses;

                                    break;
                                }
/*                                case State_RequestAnalogOutputs:
                                {
                                    for( int i = 0; i < _outbound.payload[1]; i++ )
                                    {
                                        pd.offset = i + _outbound.payload[0] + AnalogOutputOffset;
                                        pd.time    = Now.seconds();
                                        pd.type    = AnalogPointType;  //  maybe AnalogOutputPointType someday?
                                        pd.value   = (packet.data[i*2] << 8) | packet.data[i*2+1];

                                        _collected_points.push(pd);
                                    }

                                    _state = State_RequestStatuses;

                                    break;
                                }*/
                                case State_RequestStatuses:
                                {
                                    for( int i = 0; i < _outbound.payload[1]; i++ )
                                    {
                                        pd.offset  = i + _outbound.payload[0];
                                        pd.time    = Now.seconds();
                                        pd.type    = StatusPointType;
                                        pd.value   = (packet.data[i/8] >> (i % 8)) & 0x01;

                                        _collected_points.push(pd);
                                    }

                                    _state = State_Complete;

                                    break;
                                }
                            }

                            break;
                        }
                        case Command_Control:
                        {
                            switch( _state )
                            {
                                case State_Init:
                                case State_ControlSelect:
                                {
                                    _state = State_ControlExecute;

                                    break;
                                }

                                case State_ControlExecute:
                                {
                                    _state = State_Complete;

                                    break;
                                }
                            }

                            break;

                        }
                        case Command_AnalogSetpoint:
                        case Command_AnalogSetpointDirect:
                        {
                            switch( _state )
                            {
                                case State_AnalogOutputSelect:
                                {
                                    //  maybe do some verification here
                                    _state = State_AnalogOutputExecute;

                                    break;
                                }

                                case State_AnalogOutputExecute:
                                case State_AnalogOutputDirectControl:
                                {
                                    //  complain if the point doesn't match, as above
                                    _state = State_Complete;

                                    break;
                                }
                            }

                            break;
                        }
                        default:
                        {
                            CTILOG_ERROR(dout, "invalid command \""<< _command <<"\"");
                        }
                    }



                    switch( _outbound.opcode )
                    {

                        case Opcode_RTUStatusClear:
                        {
                            //  ACH:  this should probably propagate the expanded error to the requesting command (controls, especially),
                            //    and also abort (no retries) the requesting command
                            _state = State_Complete;

                            break;
                        }
                    }
                }
            }
        }
        else
        {
            retval = ClientErrors::BadCrc;
        }
    }
    else
    {
        retval = ClientErrors::Abnormal;
    }

    if( retval )
    {
        if( ++_retry_count < Retries )
        {
            //  since we didn't change anything, we'll just whip around for another pass
            retval = ClientErrors::None;
        }
    }

    return retval;
}


int CtiProtocolSeriesV::expectedResponseSize( unsigned char opcode, int points_requested )
{
    int response_size = 5;

    switch( opcode )
    {
        case Opcode_ScanAnalog:
        case Opcode_ScanAccumulator:
        case Opcode_StatusChangeDumpCOS:
        case Opcode_AnalogSetpointScan:             response_size += 2 * points_requested;  break;

        case Opcode_AnalogChangeDump:               response_size += 3 * points_requested;  break;

        case Opcode_StatusChangeDumpSOE:            response_size += 4 * points_requested;  break;

        case Opcode_ScanStatus:
        {
            response_size += points_requested / 8;
            response_size += (points_requested % 8)?1:0;

            break;
        }

        case Opcode_PulseOutputNoReply:             response_size  = 0;     break;

        case Opcode_ControlSelect:
        case Opcode_ControlExecute:
        case Opcode_AccumulatorFreeze:
        case Opcode_AccumulatorReset:
        case Opcode_AccumulatorFreezeAndReset:
        case Opcode_AnalogChangeCountRequest:       response_size++;        break;

        case Opcode_AnalogOutputSelect:
        case Opcode_AnalogOutputSelect + 1:
        case Opcode_AnalogOutputSelect + 2:
        case Opcode_AnalogOutputSelect + 3:
        case Opcode_AnalogOutputOperate:
        case Opcode_AnalogOutputOperate + 1:
        case Opcode_AnalogOutputOperate + 2:
        case Opcode_AnalogOutputOperate + 3:
        case Opcode_AnalogOutputDirectControl:
        case Opcode_AnalogOutputDirectControl + 1:
        case Opcode_AnalogOutputDirectControl + 2:
        case Opcode_AnalogOutputDirectControl + 3:
        case Opcode_SOETimeSync:
        case Opcode_AnalogDeadbandDownload:
        case Opcode_PulseOutput:                    response_size += 2;     break;

        case Opcode_RTUConfigRequest:               response_size += 3;     break;

        case Opcode_RTUStatusClear:                 response_size += 5;     break;

        default:
        {
            CTILOG_ERROR(dout, "invalid opcode \""<< opcode <<"\"");

            response_size = 0;

            break;
        }
    }

    return response_size;
}
