/*-----------------------------------------------------------------------------*
*
* File:   prot_lmi
*
* Date:   2004-jan-14
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/14 17:08:10 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "logger.h"
#include "porter.h"
#include "prot_lmi.h"
#include "rw/rwdate.h"
#include "utility.h"


CtiProtocolLMI::CtiProtocolLMI()
{
    _address = 0;
}


CtiProtocolLMI::CtiProtocolLMI(const CtiProtocolLMI &aRef)
{
    *this = aRef;
}


CtiProtocolLMI::~CtiProtocolLMI()
{
}


CtiProtocolLMI &CtiProtocolLMI::operator=(const CtiProtocolLMI &aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( this != &aRef )
    {
    }

    return *this;
}


void CtiProtocolLMI::setAddress( unsigned char address )
{
    _address = address;

    _seriesv.setAddress(address);
}


void CtiProtocolLMI::setCommand( LMICommand cmd, unsigned control_offset, unsigned control_parameter )
{
    //  if the command needs any additional parameters (or needs to percolate to the seriesv) catch it here
    switch( cmd )
    {
        case Command_ScanAccumulator:   _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanAccumulator);   break;
        case Command_ScanIntegrity:     _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanIntegrity);     break;
        case Command_ScanException:     _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanException);     break;

        case Command_Control:           _seriesv.setCommandControl(CtiProtocolSeriesV::Command_Control, control_offset, control_parameter);         break;
        case Command_AnalogSetpoint:    _seriesv.setCommandControl(CtiProtocolSeriesV::Command_AnalogSetpoint, control_offset, control_parameter);  break;

        default:
        //case Command_Loopback:
            break;
    }

    _command = cmd;
}


void CtiProtocolLMI::setDeadbands( const vector<unsigned> &points, const vector<unsigned> &deadbands )
{
    _seriesv.setDeadbands(points, deadbands);
}


int CtiProtocolLMI::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;
    OUTMESS seriesv_outmess;

    lmi_outmess_struct tmp_om_struct;

    if( OutMessage != NULL )
    {
        //  on the trip over, the seriesv stuff doesn't matter;  it'll be set when setCommand gets called

        tmp_om_struct.command = _command;
        tmp_om_struct.control_offset    = 0;  //  control point
        tmp_om_struct.control_parameter = 0;  //  control duration, setpoint value

        memcpy( OutMessage->Buffer.OutMessage, &tmp_om_struct, sizeof(tmp_om_struct) );
        OutMessage->OutLength = sizeof(tmp_om_struct);

        OutMessage->EventCode = RESULT;

        outList.append(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


int CtiProtocolLMI::recvCommResult( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int offset = 0;
    lmi_inmess_struct &lmi_in = *((lmi_inmess_struct *)InMessage->Buffer.InMessage);
    INMESS seriesv_inmess;
    unsigned char *buf = InMessage->Buffer.InMessage;

    offset += sizeof(lmi_in);

    seriesv_inmess.InLength = lmi_in.seriesv_inmess_length;
    memcpy(&seriesv_inmess, buf + offset, lmi_in.seriesv_inmess_length);

    //  ACH:  make sure to fill in any INMESS parameters the seriesv may need
    _seriesv.recvCommResult(&seriesv_inmess, outList);

    return 0;
}


bool CtiProtocolLMI::hasInboundPoints( void )
{
    return _seriesv.hasInboundPoints();
}


void CtiProtocolLMI::getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    _seriesv.getInboundPoints(pointList);
}


int CtiProtocolLMI::recvCommRequest( OUTMESS *OutMessage )
{
    lmi_outmess_struct &lmi_om = *((lmi_outmess_struct *)OutMessage->Buffer.OutMessage);

    setCommand(lmi_om.command, lmi_om.control_offset, lmi_om.control_parameter);

    _transactionComplete = false;

    return 0;
}


int CtiProtocolLMI::sendCommResult( INMESS  *InMessage )
{
    int offset = 0;
    INMESS seriesv_inmess;
    lmi_inmess_struct lmi_in;
    unsigned char *buf = InMessage->Buffer.InMessage;

    _seriesv.sendCommResult(&seriesv_inmess);

    //  record the seriesv inmessage length
    lmi_in.seriesv_inmess_length = seriesv_inmess.InLength;

    //  store the header in the inmessage
    memcpy(buf + offset, &lmi_in, sizeof(lmi_in));
    offset += sizeof(lmi_in);

    //  store the seriesv data after the header
    memcpy(buf + offset, seriesv_inmess.Buffer.InMessage, seriesv_inmess.InLength);
    offset += seriesv_inmess.InLength;

    //  store the total length
    InMessage->InLength = offset;

    return 0;
}


bool CtiProtocolLMI::isTransactionComplete( void )
{
    return _transactionComplete;  //  this is rather naive - maybe it should check state instead
}


int CtiProtocolLMI::generate( CtiXfer &xfer )
{
    int retval = NoError;
    int in_body_expected;
    RWTime NowTime;
    RWDate NowDate;


    _outbound.preamble = 0x01;
    _outbound.dest_sat_id  = 0x08; //_address;
    _outbound.dest_node    = 0x08; //0x01;
    _outbound.src_sat_id   = 0x01; //0x01;  //  picked at random - they seem like nice enough numbers
    _outbound.src_sat_node = 0x01; //0x01;  //
    _outbound.body_header.flush_codes  = 0;

    switch( _command )
    {
        case Command_Loopback:
        {
            _outbound.length  = 3;
            _outbound.body_header.message_type = Opcode_RetransmitCodes;
            _outbound.data[0] = 0;
            _outbound.data[1] = 0;

            in_body_expected  = 4;

            break;
        }

        case Command_Timesync:
        {
            _outbound.length  = 7;
            _outbound.body_header.message_type = Opcode_SetTime;
            _outbound.data[0] = NowDate.month();
            _outbound.data[0] = NowDate.dayOfMonth();
            _outbound.data[0] = NowDate.year() % 1900;
            _outbound.data[0] = NowTime.hour();
            _outbound.data[0] = NowTime.minute();
            _outbound.data[0] = NowTime.second();

            in_body_expected = 8;

            break;
        }

        case Command_ScanAccumulator:
        case Command_ScanIntegrity:
        case Command_ScanException:
        case Command_Control:
        case Command_AnalogSetpoint:
        {
            if( !_seriesv.isTransactionComplete() )
            {
                _outbound.body_header.message_type = Opcode_SeriesVWrap;

                retval = _seriesv.generate(xfer);

                //  copy the packet into our outbound, without the CRC
                memcpy((unsigned char *)_outbound.data, xfer.getOutBuffer(), xfer.getOutCount() - 2);
                _outbound.length = 1 + xfer.getOutCount() - 2;

                //  reset the count - knock off the series V CRC
                _seriesv_inbuffer = xfer.getInBuffer();
                in_body_expected  = xfer.getInCountExpected() - 2;
            }
            else
            {
                retval = !NORMAL;
            }

            break;
        }
    }

    if( !retval )
    {
        xfer.setInBuffer((unsigned char *)&_inbound);
        xfer.setInCountExpected(in_body_expected + LMIPacketOverheadLen);
        xfer.setInCountActual(&_inCountActual);

        xfer.setOutBuffer((unsigned char *)&_outbound);
        xfer.setOutCount(_outbound.length + LMIPacketOverheadLen);



        //  tack on the CRC
        if( xfer.getOutCount() > 0 )
        {
            unsigned short crc = CCITT16CRC(-1, (unsigned char *)&_outbound, _outbound.length + LMIPacketHeaderLen, false);
            /*_crc.reset();

            _crc.process_bytes((unsigned char *)&_outbound, _outbound.length + LMIPacketHeaderLen);*/

            _outbound.data[(_outbound.length - 1) + 0] = (crc & 0xff00) >> 8;
            _outbound.data[(_outbound.length - 1) + 1] =  crc & 0x00ff;
        }
    }

    return retval;
}


int CtiProtocolLMI::decode( CtiXfer &xfer, int status )
{
    int retval = NoError;
    unsigned long  tmp_crc;
    unsigned char *in_buffer = xfer.getInBuffer();

    if( !status )
    {
        tmp_crc  = in_buffer[xfer.getInCountActual() - 2] << 8;
        tmp_crc |= in_buffer[xfer.getInCountActual() - 1];

        _crc.process_bytes((unsigned char *)&_inbound, xfer.getInCountActual() - 2);

        if( tmp_crc == _crc.checksum() )
        {
            //  also verify the inbound address

            switch( _command )
            {
                case Command_Loopback:
                {
                    //  all we have to do is verify that the message got to us okay - nothing more
                    //    what will it take to return a message from a loopback?

                    _transactionComplete = true;

                    break;
                }

                case Command_Timesync:
                {   /*
                    _outbound.body_header.message_type = Opcode_SetTime;
                    _outbound.data[0] = NowDate.month();
                    _outbound.data[0] = NowDate.dayOfMonth();
                    _outbound.data[0] = NowDate.year() % 1900;
                    _outbound.data[0] = NowTime.hour();
                    _outbound.data[0] = NowTime.minute();
                    _outbound.data[0] = NowTime.second();

                    in_body_expected = 8;
                    */
                    break;
                }

                case Command_ScanAccumulator:
                case Command_ScanIntegrity:
                case Command_ScanException:
                case Command_Control:
                case Command_AnalogSetpoint:
                {
                    memcpy(_seriesv_inbuffer, xfer.getInBuffer() + LMIPacketHeaderLen, xfer.getInCountActual() - LMIPacketOverheadLen);
                    //  tack on the Series V passthrough CRC
                    _seriesv_inbuffer[xfer.getInCountActual() - LMIPacketHeaderLen - 2] = (CtiProtocolSeriesV::PassthroughCRC & 0x00ff);
                    _seriesv_inbuffer[xfer.getInCountActual() - LMIPacketHeaderLen - 1] = (CtiProtocolSeriesV::PassthroughCRC & 0xff00) >> 8;

                    if( !_seriesv.isTransactionComplete() )
                    {
                        retval = _seriesv.decode(xfer, status);

                        _transactionComplete = true;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - got into lmi.decode() even though seriesv.istransactioncomplete() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    break;
                }

                default:
                {
                    _transactionComplete = true;
                }
            }
        }
    }

    return retval;
}


