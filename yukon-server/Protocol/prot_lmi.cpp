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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/12/08 21:19:30 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <rw/rwdate.h>

#include "logger.h"
#include "porter.h"
#include "msg_pdata.h"
#include "prot_lmi.h"
#include "utility.h"
#include "numstr.h"
#include "cparms.h"

#include "verification_objects.h"


CtiProtocolLMI::CtiProtocolLMI() :
    _transmitter_power_time(0),
    _address(0),
    _transmitter_id(0),
    _first_comm(true),
    _verification_pending(false)
{
}


CtiProtocolLMI::CtiProtocolLMI(const CtiProtocolLMI &aRef)
{
    *this = aRef;
}


CtiProtocolLMI::~CtiProtocolLMI()
{
    while( !_verification_objects.empty() )
    {
        CtiVerificationBase *vob = _verification_objects.front();
        _verification_objects.pop();

        delete vob;
    }

    while( !_codes.empty() )
    {
        CtiOutMessage *om = _codes.front();
        _codes.pop();

        delete om;
    }
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


void CtiProtocolLMI::setName( const RWCString &name )
{
    _name = name;
}


void CtiProtocolLMI::setCommand( LMICommand cmd, unsigned control_offset, unsigned control_parameter )
{
    //  if the command needs to percolate to the seriesv, catch it here
    //    this is necessary because the commands between the LMI and the Series V may not always match as a passthrough
    switch( cmd )
    {
        case Command_ScanAccumulator:   _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanAccumulator);   break;
        case Command_ScanIntegrity:     _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanIntegrity);     break;
        case Command_ScanException:     _seriesv.setCommand(CtiProtocolSeriesV::Command_ScanException);     break;

        case Command_Control:           _seriesv.setCommandControl(CtiProtocolSeriesV::Command_Control, control_offset, control_parameter);         break;
        case Command_AnalogSetpoint:    _seriesv.setCommandControl(CtiProtocolSeriesV::Command_AnalogSetpoint, control_offset, control_parameter);  break;
    }

    _command = cmd;
    _control_offset    = control_offset;
    _control_parameter = control_parameter;
    _num_codes_retrieved = 0;
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
        tmp_om_struct.control_offset    = _control_offset;      //  control point
        tmp_om_struct.control_parameter = _control_parameter;   //  control duration, setpoint value

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

    //  copy out the codes
    for( int i = 0; i < lmi_in.num_codes; i++ )
    {
        _returned_codes.push(*((unsigned int *)(buf + offset)));

        offset += sizeof(unsigned int);
    }

    //  restore the seriesv inmessage
    memcpy(seriesv_inmess.Buffer.InMessage, buf + offset, lmi_in.seriesv_inmess_length);

    //  ACH:  make sure to fill in any INMESS parameters the seriesv may need - i think it's
    //          pretty simple, but watch this space for breakage if it gets more complex
    _seriesv.recvCommResult(&seriesv_inmess, outList);

    return 0;
}


bool CtiProtocolLMI::hasInboundData( void )
{
    return _seriesv.hasInboundPoints() | (_transmitter_power_time != 0) | !_returned_codes.empty();
}


void CtiProtocolLMI::getInboundData( RWTPtrSlist< CtiPointDataMsg > &pointList, RWCString &info )
{
    int i = 1;
    CtiPointDataMsg *pdm;

    _seriesv.getInboundPoints(pointList);

    if( _transmitter_power >= 0 )
    {
        pdm = CTIDBG_new CtiPointDataMsg(LMITransmitterPowerPointOffset, _transmitter_power, NormalQuality, AnalogPointType);
        pdm->setTime(_transmitter_power_time);

        pointList.append(pdm);

        _transmitter_power      = -1;
        _transmitter_power_time =  0;
    }

    if( !_returned_codes.empty() )
    {
        info += "Codes:\n";
    }

    while( !_returned_codes.empty() )
    {
        info += CtiNumStr(_returned_codes.front()).zpad(6) + " ";
        _returned_codes.pop();

        if( !(i++ % 6) )
        {
            info += "\n";
        }
    }
}


int CtiProtocolLMI::recvCommRequest( OUTMESS *OutMessage )
{
    switch( OutMessage->Sequence )
    {
        case Sequence_QueuedWork:
        {
            setCommand(Command_SendQueuedCodes, 0, 0);
            _completion_time    = OutMessage->ExpirationTime;

            _transmitting_until = RWTime::now();

            break;
        }

        case Sequence_RetrieveEchoedCodes:
        {
            setCommand(Command_ReadEchoedCodes, 0, 0);
            _completion_time    = OutMessage->ExpirationTime;

            break;
        }

        default:
        {
            lmi_outmess_struct &lmi_om = *((lmi_outmess_struct *)OutMessage->Buffer.OutMessage);

            setCommand(lmi_om.command, lmi_om.control_offset, lmi_om.control_parameter);
            _completion_time = 0UL;

            break;
        }
    }

    _transmitter_id = OutMessage->DeviceID;
    _transactionComplete = false;
    _first_comm = true;
    _untransmitted_codes = false;
    _status.c = 0;
    _in_count = 0;
    _in_total = 0;

    return 0;
}


int CtiProtocolLMI::sendCommResult( INMESS  *InMessage )
{
    int offset = 0;
    INMESS seriesv_inmess;
    lmi_inmess_struct lmi_in;
    unsigned char *buf = InMessage->Buffer.InMessage;

    _seriesv.sendCommResult(&seriesv_inmess);

    lmi_in.num_codes             = _retrieved_codes.size();
    lmi_in.seriesv_inmess_length = seriesv_inmess.InLength;

    //  store the header in the inmessage
    memcpy(buf + offset, &lmi_in, sizeof(lmi_in));
    offset += sizeof(lmi_in);

    //  store the retrieved codes
    while( !_retrieved_codes.empty() )
    {
        *((unsigned int *)(buf + offset)) = _retrieved_codes.front();
        _retrieved_codes.pop();

        offset += sizeof(unsigned int);
    }

    //  store the seriesv data after the header
    memcpy(buf + offset, seriesv_inmess.Buffer.InMessage, seriesv_inmess.InLength);
    offset += seriesv_inmess.InLength;

    //  store the total length
    InMessage->InLength = offset;

    return 0;
}


void CtiProtocolLMI::queueCode(CtiOutMessage *om)
{
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - OutMessage->VerificationSequence = " << om->VerificationSequence << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _codes.push(om);
}


bool CtiProtocolLMI::hasQueuedCodes( void ) const
{
    return !_codes.empty();
}


bool CtiProtocolLMI::codeVerificationPending( void ) const
{
    return _verification_pending;
}


bool CtiProtocolLMI::canTransmit( const RWTime &allowed_time ) const
{
    bool retval = false;

    long percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250),
         fudge   = gConfigParms.getValueAsULong("PORTER_LMI_FUDGE", 1000);

    RWTime start_time, required_time;

    if( _transmitting_until < start_time )
    {
        required_time = start_time + ((percode + fudge) / 1000);

        if( required_time <= allowed_time )
        {
            retval = true;
        }
    }

    return retval;
}

int CtiProtocolLMI::getNumCodes( void ) const
{
    return _codes.size();
}


RWTime CtiProtocolLMI::getTransmittingUntil( void ) const
{
    return _transmitting_until;
}


bool CtiProtocolLMI::isTransactionComplete( void )
{
    bool retval = false;

    if( _completion_time.seconds() && _completion_time < RWTime::now() )
    {
        if( _command == Command_SendQueuedCodes && _untransmitted_codes )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - late in loop for \"" << _name << "\", using one more pass to transmit codes **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            //  if we don't have any untransmitted codes, it's okay to break
            retval = true;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - breaking out of late loop in \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        retval = _transactionComplete;
    }

    return retval;  //  this is rather naive - maybe it should check state instead
}


void CtiProtocolLMI::getVerificationObjects(queue< CtiVerificationBase * > &vq)
{
    while( !_verification_objects.empty() )
    {
        vq.push(_verification_objects.front());
        _verification_objects.pop();
    }
}


int CtiProtocolLMI::generate( CtiXfer &xfer )
{
    int retval = NoError;
    bool reply_expected = true;
    RWTime NowTime;
    RWDate NowDate;

    if( _in_total > 0 )
    {
        lmi_message &packet = *((lmi_message *)&_inbound);

        xfer.setInBuffer((unsigned char *)&_inbound + _in_total);
        xfer.setInCountActual(&_in_count);

        //  we also expect the CRC
        xfer.setInCountExpected(packet.length + LMIPacketOverheadLen - _in_total);

        xfer.setOutBuffer((unsigned char *)&_outbound);
        xfer.setOutCount(0);
    }
    else
    {
        _outbound.preamble = 0x01;
        _outbound.dest_sat_id  = _address;
        _outbound.dest_node    = _address;
        _outbound.src_sat_id   = 0x01;
        _outbound.src_sat_node = 0x01;
        _outbound.body_header.flush_codes  = 0;

        if( _status.c )
        {
            _outbound.length  = 2;
            _outbound.body_header.message_type = Opcode_ClearAndReadStatus;
            _outbound.data[0] = _status.c;
            _outbound.data[1] = 0;
        }
        else
        {
            switch( _command )
            {
                case Command_SendQueuedCodes:
                {
                    unsigned long transmit_time, waiting_codes, max_codes, num_codes;
                    bool final_block = false;
                    CtiOutMessage *om;

                    long percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250);

                    waiting_codes = _codes.size();

                    if( _completion_time > _transmitting_until )
                    {
                        transmit_time  = _completion_time.seconds() - _transmitting_until.seconds();
                        transmit_time *= 1000;

                        max_codes = transmit_time / percode;
                    }
                    else
                    {
                        max_codes = 0;
                    }

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << RWTime() << " LMI device \"" << _name << "\" has enough time to load " << max_codes << " (of " << waiting_codes << ") codes" << endl;
                    }

                    _outbound.body_header.message_type = Opcode_SendCodes;
                    _outbound.body_header.flush_codes  = _first_comm;

                    if( _outbound.body_header.flush_codes )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << RWTime() << " LMI device \"" << _name << "\" removing previously-transmitted codes" << endl;
                        }
                    }

                    if( waiting_codes > max_codes )
                    {
                        num_codes = max_codes;
                    }
                    else
                    {
                        num_codes = waiting_codes;
                    }

                    if( num_codes > LMIMaxCodesPerTransaction )
                    {
                        num_codes = LMIMaxCodesPerTransaction;
                    }
                    else
                    {
                        final_block = true;
                    }

                    _outbound.data[0] = num_codes;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << RWTime() << " LMI device \"" << _name << "\" loading " << num_codes << " (of " << waiting_codes << ") codes this pass:" << endl;
                    }

                    _outbound.length = (num_codes * 6) + 2;
                    _transmitting_until += (num_codes * percode) / 1000;

                    for( int i = 0; i < (num_codes * 6); i += 6 )
                    {
                        _untransmitted_codes = true;

                        om = _codes.front();

                        if(om && ( om->ExpirationTime <= 0 || om->ExpirationTime >= NowTime.seconds()) )
                        {
                            om->Buffer.SASt._codeSimple[6] = 0;  //  make sure it's null-terminated, just to be safe...
                            char (&codestr)[7] = om->Buffer.SASt._codeSimple;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(slog);
                                slog << om->Buffer.SASt._codeSimple << " ";
                            }

                            //  all offset by one because of the "num_codes" byte at the beginning
                            _outbound.data[i+1] = codestr[0];
                            _outbound.data[i+2] = codestr[1];
                            _outbound.data[i+3] = codestr[2];
                            _outbound.data[i+4] = codestr[3];
                            _outbound.data[i+5] = codestr[4];
                            _outbound.data[i+6] = codestr[5];


                            //  new CtiVerificationWork message here
                            if( !om->VerificationSequence )
                            {
                                om->VerificationSequence = VerificationSequenceGen();
                            }

                            long id = om->DeviceID;

                            ptime::time_duration_type expiration(seconds(60));
                            CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_Golay, *om, codestr, expiration);

                            _verification_objects.push(work);
                        }

                        //  this isn't very robust yet, as far as error-handling goes - one error, and all of these 40-something codes will go down the toilet
                        //    i need to move them to a pending list or something until i get to decode(), where i can then pop them with vigor and prejudice
                        _codes.pop();

                        delete om;
                    }

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << endl;
                    }

                    if( final_block )
                    {
                        _outbound.data[0] |= 0xc0;    //  last group of codes, send immediately

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << RWTime() << " LMI device \"" << _name << "\" transmitting" << endl;
                        }
                    }

                    break;
                }

                case Command_ReadQueuedCodes:
                case Command_ReadEchoedCodes:
                {
                    _outbound.length  = 2;

                    if( _command == Command_ReadQueuedCodes )   _outbound.body_header.message_type = Opcode_GetOriginalCodes;
                    if( _command == Command_ReadEchoedCodes )   _outbound.body_header.message_type = Opcode_GetEchoedCodes;

                    _outbound.data[0] = _num_codes_retrieved + 1;  //  starts out at 0, incremented by every subsequent retrieval

                    break;
                }

                case Command_Loopback:
                {
                    _outbound.length  = 2;
                    _outbound.body_header.message_type = Opcode_ClearAndReadStatus;
                    _outbound.data[0] = 0;
                    _outbound.data[1] = 0;

                    break;
                }

                case Command_Timesync:
                {
                    _outbound.length  = 7;
                    _outbound.body_header.message_type = Opcode_SetTime;
                    _outbound.data[0] = NowDate.month();
                    _outbound.data[1] = NowDate.dayOfMonth();
                    _outbound.data[2] = NowDate.year() % 1900;
                    _outbound.data[3] = NowTime.hour();
                    _outbound.data[4] = NowTime.minute();
                    _outbound.data[5] = NowTime.second();

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
                    }
                    else
                    {
                        //  make into a switch if any other commands need to perform commands after the Series V is done
                        if( _command == Command_ScanAccumulator )
                        {
                            _outbound.length = 1;
                            _outbound.body_header.message_type = Opcode_GetTransmitterPower;
                        }
                        else
                        {
                            retval = !NORMAL;
                        }
                    }

                    break;
                }
            }
        }


        if( !retval )
        {
            xfer.setInBuffer((unsigned char *)&_inbound);
            xfer.setInCountActual(&_in_count);

            if( reply_expected )
            {
                xfer.setInCountExpected(LMIPacketHeaderLen);
            }
            else
            {
                xfer.setInCountExpected(0);
            }

            xfer.setOutBuffer((unsigned char *)&_outbound);
            xfer.setOutCount(_outbound.length + LMIPacketOverheadLen);

            //  tack on the CRC
            if( xfer.getOutCount() > 0 )
            {
                unsigned short crc = CCITT16CRC(-1, (unsigned char *)&_outbound, _outbound.length + LMIPacketHeaderLen, false);

                _outbound.data[(_outbound.length - 1) + 0] = (crc & 0xff00) >> 8;
                _outbound.data[(_outbound.length - 1) + 1] =  crc & 0x00ff;
            }
        }
    }


    return retval;
}


int CtiProtocolLMI::decode( CtiXfer &xfer, int status )
{
    int retval = NoError;
    unsigned long  tmp_crc, inbound_crc;
    CtiXfer seriesv_xfer;
    unsigned long seriesv_incount_actual;
    const unsigned char *buf = (unsigned char *)&_inbound;
    RWTime Now;

    if( !status )
    {
        _first_comm = false;

        _in_total += xfer.getInCountActual();

        if( _in_total >= LMIPacketHeaderLen && _in_total >= (_inbound.length + LMIPacketOverheadLen) )
        {
            tmp_crc = CCITT16CRC(-1, (unsigned char *)&_inbound, _in_total - 2, false);

            inbound_crc  = buf[_in_total - 2] << 8;
            inbound_crc |= buf[_in_total - 1];

            if( tmp_crc == inbound_crc )
            {
                //  also verify the inbound address

                if( _inbound.body_header.message_type == Opcode_ClearAndReadStatus )
                {
                    _status.c = _inbound.data[0];
                }
                else
                {
                    switch( _command )
                    {
                        case Command_SendQueuedCodes:
                        {
                            _verification_pending = true;

                            if( _outbound.data[0] & 0xc0 )
                            {
                                _untransmitted_codes = false;
                            }

                            //  well, theory goes that if we're here and the code queue is empty, we probably transmitted them
                            //    (this should be FIX 'd to make the transactioncomplete stuff more robust, ick)
                            if( _codes.empty() )
                            {
                                _transactionComplete = true;
                            }

                            //  also, if we're out of time, stop loading codes
                            if( _transmitting_until >= _completion_time )
                            {
                                _transactionComplete = true;
                            }

                            break;
                        }

                        case Command_ReadQueuedCodes:
                        case Command_ReadEchoedCodes:
                        {
                            int offset = 0;

                            offset++;  //  move past the UPA status for the time being

                            //  final block of codes, so we're done
                            if( !(_inbound.data[offset++] & 0x80) )
                            {
                                _transactionComplete = true;
                            }

                            while( offset < (_inbound.length - 1) )
                            {
                                char buf[7];

                                memcpy(buf, _inbound.data + offset, 6);
                                buf[6] = 0;

                                if( _command == Command_ReadEchoedCodes )
                                {
                                    CtiVerificationReport *report = CTIDBG_new CtiVerificationReport(CtiVerificationBase::Protocol_Golay, _transmitter_id, string(buf), second_clock::universal_time());

                                    _verification_objects.push(report);

                                    _verification_pending = false;
                                }
                                else
                                {
                                    _retrieved_codes.push(atoi(buf));
                                }

                                offset += 6;

                                _num_codes_retrieved++;
                            }

                            //  shouldn't be possible
                            if( _num_codes_retrieved > 0xff )
                            {
                                _transactionComplete = true;

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - exceeded maximum codes for device \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }

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
                                memcpy(_seriesv_inbuffer, buf + LMIPacketHeaderLen + 1, _in_total - LMIPacketHeaderLen - 1);

                                seriesv_xfer.setInBuffer(_seriesv_inbuffer);
                                seriesv_xfer.setInCountActual(&seriesv_incount_actual);
                                seriesv_xfer.setInCountActual(_in_total - LMIPacketHeaderLen - 1);

                                //  tack on the Series V passthrough CRC
                                _seriesv_inbuffer[seriesv_xfer.getInCountActual() - 2] = (CtiProtocolSeriesV::PassthroughCRC & 0x00ff);
                                _seriesv_inbuffer[seriesv_xfer.getInCountActual() - 1] = (CtiProtocolSeriesV::PassthroughCRC & 0xff00) >> 8;

                                retval = _seriesv.decode(seriesv_xfer, status);

                                if( _command != Command_ScanAccumulator )
                                {
                                    _transactionComplete = _seriesv.isTransactionComplete();
                                }
                            }
                            else
                            {
                                if( _command == Command_ScanAccumulator )
                                {
                                    _transmitter_power  = _inbound.data[0];
                                    _transmitter_power |= _inbound.data[1] << 8;

                                    _transmitter_power_time = RWTime::now().seconds();

                                    _transactionComplete = true;
                                }
                                else
                                {
                                    retval = !NORMAL;
                                }
                            }

                            break;
                        }

                        case Command_Loopback:
                        //  all we have to do is verify that the message got to us okay - nothing more
                        //    what will it take to return a message from a loopback?
                        case Command_Timesync:
                        default:
                        {
                            _transactionComplete = true;
                        }
                    }
                }
            }
            else
            {
                retval = BADCRC;
            }

            _in_total = 0;
        }
    }
    else if( status == ErrPortSimulated )
    {
        if( _command == Command_SendQueuedCodes )
        {
            if( _outbound.data[0] & 0xc0 )
            {
                _untransmitted_codes = false;
            }

            if( _codes.empty() )
            {
                _transactionComplete = true;
            }

            if( _transmitting_until >= _completion_time )
            {
                _transactionComplete = true;
            }
        }
        else
        {
            _transactionComplete = true;
        }

        retval = NoError;
    }

//  this is handled by isTransactionComplete, and i want to make sure the complaint there gets printed
/*
    //  always exit if the expiration time is past
    if( _completion_time.seconds() && _completion_time <= Now )
    {
        _transactionComplete = true;
    }
*/
    return retval;
}


