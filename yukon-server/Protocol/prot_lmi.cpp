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
* REVISION     :  $Revision: 1.35 $
* DATE         :  $Date: 2005/10/04 20:10:10 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/rwdate.h>

#include "logger.h"
#include "porter.h"
#include "msg_pdata.h"
#include "prot_lmi.h"
#include "prot_sa3rdparty.h"
#include "utility.h"
#include "numstr.h"
#include "cparms.h"

#include "verification_objects.h"


CtiProtocolLMI::CtiProtocolLMI() :
    _transmitter_power_time(0),
    _transmitter_power(-1),
    _num_codes_loaded(0),
    _config_sent(0),
    _address(0),
    _transmitter_id(0),
    _first_code_block(true),
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


void CtiProtocolLMI::setSystemData(int ticktime, int timeoffset, int transmittime, int transmitterlow, int transmitterhigh, string startcode, string stopcode)
{
    _tick_time   = ticktime;
    _time_offset = timeoffset;

    _transmit_duration = transmittime;

    _transmitter_power_low_limit  = transmitterlow;
    _transmitter_power_high_limit = transmitterhigh;

    _start_code = startcode;
    _stop_code  = stopcode;
}


CtiProtocolLMI::LMICommand CtiProtocolLMI::getCommand() const
{
    return _command;
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

    //  this should eventually be sent in the sendDispatchResults portion and returned as text for ResultDecode
    _transmitter_power      = lmi_in.transmitter_power;
    _transmitter_power_time = lmi_in.transmitter_power_time;

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

    if( _transmitter_power_time != 0 )
    {
        //  note that this will be analog offset LMIPointOffsetTransmitterPower + 1 when it gets back to the system
        pdm = CTIDBG_new CtiPointDataMsg(LMIPointOffset_TransmitterPower, _transmitter_power, NormalQuality, AnalogPointType);
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
    _comm_end_time = 0UL;
    _preload_sequence = false;

    switch( OutMessage->Sequence )
    {
        case Sequence_Preload:
        {
            setCommand(Command_Timesync, 0, 0);

            _preload_sequence = true;

            //  this is the only type of command that requires us to complete by a certain
            //    time - all of the other ones aren't governed by exclusion logic
            _comm_end_time = OutMessage->ExpirationTime;

            break;
        }

        /*
        case Sequence_QueueCodes:
        {
            setCommand(Command_QueueCodes, 0, 0);

            break;
        }

        case Sequence_ReadEchoedCodes:
        {
            setCommand(Command_ReadEchoedCodes, 0, 0);

            break;
        }

        case Sequence_ClearQueuedCodes:
        {
            setCommand(Command_ClearQueuedCodes, 0, 0);

            break;
        }
        */

        case Sequence_TimeSync:
        {
            setCommand(Command_Timesync, 0, 0);

            break;
        }

        default:
        {
            lmi_outmess_struct &lmi_om = *((lmi_outmess_struct *)OutMessage->Buffer.OutMessage);
            setCommand(lmi_om.command, lmi_om.control_offset, lmi_om.control_parameter);

            break;
        }
    }

    _transmitter_id = OutMessage->DeviceID;
    _transaction_complete = false;
    _transmission_end = 0;
    _first_code_block = true;
    _final_code_block = false;
    _status_read = false;
    _status_read_count = 0;
    _echoed_error_count = 0;
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

    lmi_in.num_codes              = _retrieved_codes.size();
    lmi_in.transmitter_power      = _transmitter_power;
    lmi_in.transmitter_power_time = _transmitter_power_time;

    lmi_in.seriesv_inmess_length  = seriesv_inmess.InLength;

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


bool CtiProtocolLMI::canDownloadCodes( void ) const
{
    RWTime now, transmit_begin, transmit_end;
    int percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250);

    transmit_begin -= now.seconds() % (_tick_time * 60);
    transmit_begin += _time_offset;

    transmit_end = transmit_begin.seconds() + ((_num_codes_loaded * percode) / 1000);

    return (now < transmit_begin) || (now > transmit_end);
}

int CtiProtocolLMI::getNumCodes( void ) const
{
    return _codes.size();
}


int CtiProtocolLMI::getPreloadDataLength( void ) const
{
    int total_comms = 0;
    int codes_to_download = _codes.size();

    //  NOTE - this is where we set the max codes for the transmit window of the device -
    //    add it here when the value is available from the DB

    //  we can only download this many per cycle anyway
    if( codes_to_download > LMIMaxCodesDownloaded )
    {
        codes_to_download = LMIMaxCodesDownloaded;
    }

    if( codes_to_download )
    {
        //  a couple of magic numbers here, but basically this assumes we get one full header packet back
        //    for every batch of codes we send out - this is approximately true
        total_comms += (codes_to_download * 6) + (((codes_to_download / LMIMaxCodesPerTransaction) + 1) * (LMIPacketOverheadLen * 2));
    }
    else
    {
        //  if no codes to download, we must clear the codes
        total_comms += LMIPacketOverheadLen * 2;

    }
    if( _num_codes_loaded )
    {
        //  This is an exact copy of the code-loading behavior, except the outbound and inbound sizes are swapped
        total_comms += (_num_codes_loaded * 6) + (((_num_codes_loaded / LMIMaxCodesPerTransaction) + 1) * (LMIPacketOverheadLen * 2));
    }

    //  add on the config length
    if( _config_sent < (RWTime::now().seconds() - gConfigParms.getValueAsULong("PORTER_LMI_SYSTEMDATA_INTERVAL", 86400)) )
    {
        total_comms += LMIPacketOverheadLen * 2 + 18;
    }

    //  add on the time sync
    total_comms += LMIPacketOverheadLen * 2;

    return total_comms;
}


RWTime CtiProtocolLMI::getTransmissionEnd( void ) const
{
    return _transmission_end;
}


RWTime CtiProtocolLMI::getLastCodeDownload( void ) const
{
    return _last_code_download;
}


bool CtiProtocolLMI::isTransactionComplete( void )
{
    bool retval = false;

    if( _comm_end_time && _comm_end_time < RWTime::now().seconds() )
    {
        retval = true;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - breaking out of late loop in \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        retval = _transaction_complete;
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


void CtiProtocolLMI::getStatuses(pointlist_t &points)
{
    pointlist_t::iterator itr;

    points.insert(points.end(), _lmi_statuses.begin(), _lmi_statuses.end());

    _lmi_statuses.clear();
}


int CtiProtocolLMI::generate( CtiXfer &xfer )
{
    int retval = NoError;
    bool reply_expected = true;
    RWTime NowTime;
    RWDate NowDate;

    {
        CtiLockGuard<CtiLogger> doubt_guard(slog);
        slog << RWTime() << " **** prot_lmi generating with command = " << _command << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

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
        _outbound.dest_node    = 0x08;
        _outbound.src_sat_id   = 0x01;
        _outbound.src_sat_node = 0x01;
        _outbound.body_header.flush_codes  = 0;

        //  if we haven't ever read the statuses OR if there's an existing status that needs to be reset
        //    also make sure that we don't infinitely read the statuses
        if( (!_status_read || _status.c) && (++_status_read_count < MaxStatusReads) )
        {
            _status_read = true;  //  set here instead of the decode to prevent looping

            _outbound.length  = 3;
            _outbound.body_header.message_type = Opcode_ClearAndReadStatus;
            _outbound.data[0] = _status.c;
            _outbound.data[1] = 0;
            _outbound.data[2] = 0;
        }
        else
        {
            //  we can survive any other status (except maybe "lmi comm failure", but the RTU should still respond, even if it can't communicate with the transmitter)
            if( _status.s.questionable_request )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - persistent 'questionable request' respose from LMI **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _transaction_complete = true;

                retval = NOTNORMAL;
            }
            //  if we've never downloaded the system parameters, send them out (once per day by default)
            else if( _config_sent < (RWTime::now().seconds() - gConfigParms.getValueAsULong("PORTER_LMI_SYSTEMDATA_INTERVAL", 86400)) )
            {
                _outbound.length  = 19;
                _outbound.body_header.message_type = Opcode_DownloadSystemData;
                _outbound.data[ 0] =  _tick_time;
                _outbound.data[ 1] =  _time_offset & 0xff;
                _outbound.data[ 2] = (_time_offset >> 8) & 0xff;
                _outbound.data[ 3] =  _transmitter_power_low_limit & 0xff;
                _outbound.data[ 4] = (_transmitter_power_low_limit >> 8) & 0xff;
                _outbound.data[ 5] =  _transmitter_power_high_limit & 0xff;
                _outbound.data[ 6] = (_transmitter_power_high_limit >> 8) & 0xff;
                _outbound.data[ 7] = _start_code[0];
                _outbound.data[ 8] = _start_code[1];
                _outbound.data[ 9] = _start_code[2];
                _outbound.data[10] = _start_code[3];
                _outbound.data[11] = _start_code[4];
                _outbound.data[12] = _start_code[5];
                _outbound.data[13] = _stop_code[0];
                _outbound.data[14] = _stop_code[1];
                _outbound.data[15] = _stop_code[2];
                _outbound.data[16] = _stop_code[3];
                _outbound.data[17] = _stop_code[4];
                _outbound.data[18] = _stop_code[5];
            }
            else
            {
                switch( _command )
                {
                    case Command_QueueCodes:
                    {
                        int percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250);
                        int transmit_time, slots_available, max_codes, expired_codes;

                        bool final_block = false;

                        queue< CtiOutMessage * > viable_codes;
                        CtiOutMessage *om;

                        if( _first_code_block )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(slog);
                                slog << RWTime() << " LMI device \"" << _name << "\" removing previous outbound codes" << endl;
                            }

                            _num_codes_loaded = 0;
                        }

                        //  number of millis we have available to transmit
                        transmit_time   = (_transmit_duration * 1000) - (_num_codes_loaded * percode);
                        slots_available = transmit_time / percode;

                        expired_codes = 0;
                        while( !_codes.empty() && viable_codes.size() < slots_available &&
                                                  viable_codes.size() < LMIMaxCodesPerTransaction )
                        {
                            om = _codes.front();
                            _codes.pop();

                            if( om )
                            {
                                if( (om->ExpirationTime <= 0 || om->ExpirationTime >= NowTime.seconds()) )
                                {
                                    viable_codes.push(om);
                                }
                                else
                                {
                                    delete om;

                                    expired_codes++;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - removing null OM from device queue for LMI device \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        if( expired_codes )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << RWTime() << " LMI device \"" << _name << "\" pruned " << expired_codes << " codes this pass" << endl;
                        }

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << RWTime() << " LMI device \"" << _name << "\" loading " << viable_codes.size() << " (of " << (viable_codes.size() + _codes.size()) << " potential) codes this pass (" << slots_available << " slots available):" << endl;
                        }

                        if( _codes.empty() || (viable_codes.size() == slots_available) )
                        {
                            _final_code_block = true;
                        }

                        _num_codes_loaded += viable_codes.size();
                        _outbound.data[0]  = viable_codes.size();

                        _outbound.length  = (viable_codes.size() * 6) + 2;
                        _outbound.body_header.message_type = Opcode_SendCodes;
                        _outbound.body_header.flush_codes  = _first_code_block;

                        if( _final_code_block )
                        {
                            _outbound.data[0] |= LMILastCodeGroup;
                        }

                        unsigned offset = 1;

                        while( !viable_codes.empty() )
                        {
                            om = viable_codes.front();
                            viable_codes.pop();

                            om->Buffer.SASt._codeSimple[6] = 0;  //  make sure it's null-terminated, just to be safe...
                            char (&codestr)[7] = om->Buffer.SASt._codeSimple;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(slog);
                                slog << om->Buffer.SASt._codeSimple << " ";
                            }

                            //  all offset by one because of the "num_codes" byte at the beginning
                            _outbound.data[offset++] = codestr[0];
                            _outbound.data[offset++] = codestr[1];
                            _outbound.data[offset++] = codestr[2];
                            _outbound.data[offset++] = codestr[3];
                            _outbound.data[offset++] = codestr[4];
                            _outbound.data[offset++] = codestr[5];

                            //  new CtiVerificationWork message here
                            if( !om->VerificationSequence )
                            {
                                om->VerificationSequence = VerificationSequenceGen();
                            }

                            long id = om->DeviceID;

                            if( !gConfigParms.getValueAsString("PROTOCOL_LMI_VERIFY").contains("false", RWCString::ignoreCase) )
                            {
                                ptime::time_duration_type expiration(seconds(60));
                                CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_Golay, *om, CtiProtocolSA3rdParty::asString(om->Buffer.SASt).data(), codestr, expiration);

                                _verification_objects.push(work);
                            }

                            //  this isn't very robust yet, as far as error-handling goes - one error, and all of these 40-something codes will go down the toilet
                            //    i need to move them to a pending list or something until i get to decode(), where i can then pop them with vigor and prejudice
                            delete om;
                        }

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << endl;
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
                    case Command_ClearEchoedCodes:
                    {
                        _outbound.length  = 2;

                        _outbound.body_header.message_type = Opcode_FlushCodes;

                        _outbound.data[0] = 0x01;  //  clear only ECHOED codes - note that we do clear the queued codes after a full
                                                   //    interval/cycle has passed with no downloads
                        break;
                    }
                    case Command_ClearQueuedCodes:
                    {
                        _outbound.length = 2;

                        _outbound.body_header.message_type = Opcode_FlushCodes;

                        _outbound.data[0] = 0x02;  //  clear only LOADED codes - this way, we can clear the queued codes and still do
                                                   //    verification if we haven't already

                        break;
                    }
                    case Command_Loopback:
                    {
                        _outbound.length  = 3;
                        _outbound.body_header.message_type = Opcode_ClearAndReadStatus;
                        _outbound.data[0] = 0;
                        _outbound.data[1] = 0;
                        _outbound.data[2] = 0;

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
        else
        {
            //  what should we do if the generate causes an error?

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - retval = " << retval << " in CtiProtocolLMI::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            _transaction_complete = true;
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
        _in_total += xfer.getInCountActual();

        if( _in_total >= LMIPacketHeaderLen && _in_total >= (_inbound.length + LMIPacketOverheadLen) )
        {
            tmp_crc = CCITT16CRC(-1, (unsigned char *)&_inbound, _in_total - 2, false);

            inbound_crc  = buf[_in_total - 2] << 8;
            inbound_crc |= buf[_in_total - 1];

            if( tmp_crc == inbound_crc )
            {
                //  also verify the inbound address
                if( _inbound.dest_sat_id == 0x01 && _inbound.dest_node == 0x01 )
                {
                    if( _inbound.body_header.message_type == Opcode_ClearAndReadStatus )
                    {
                        _status.c = _inbound.data[0];

                        decodeStatuses(_status.s);

                        if( _status.s.questionable_request )
                        {
                            //  this needs to be handled more intelligently - we've just gotten a "questionable" response - we need
                            //    to handle this instead of just quitting
                            _transaction_complete = true;

                        }

                        if( _outbound.body_header.message_type == Opcode_GetEchoedCodes &&
                            (_status.s.loadshed_codes_locked || !_status.s.loadshed_verify_state) )
                        {
                            if( ++_echoed_error_count > 5 )
                            {
                                //  we had an error while retrieving codes; we're done
                                //  this may be from the statuses not being correct (verify_complete, et al) - maybe we should scan a couple more times?
                                _verification_pending = false;
                                _transaction_complete = true;
                            }
                        }
                    }
                    else if( _inbound.body_header.message_type == Opcode_DownloadSystemData )
                    {
                        _config_sent = RWTime::now().seconds();
                    }
                    else
                    {
                        switch( _command )
                        {
                            case Command_QueueCodes:
                            {
                                _first_code_block = false;

                                _verification_pending = true;

                                _transaction_complete = _final_code_block;

                                _last_code_download = RWTime::now().seconds();

                                _transmission_end   = RWTime::now().seconds();
                                _transmission_end  -= _transmission_end % (_tick_time * 60);
                                _transmission_end  += _time_offset;

                                while( _transmission_end < RWTime::now().seconds() )
                                {
                                    _transmission_end += _tick_time * 60;
                                }

                                break;
                            }

                            case Command_ClearEchoedCodes:
                            {
                                _verification_pending = false;

                                //  if this set of commands was triggered through the
                                //    preload behavior, do the next step
                                if( _preload_sequence )
                                {
                                    if( _codes.size() )
                                    {
                                        _command = Command_QueueCodes;
                                    }
                                    else
                                    {
                                        _command = Command_ClearQueuedCodes;
                                    }
                                }
                                else
                                {
                                    _transaction_complete = true;
                                }

                                break;
                            }

                            case Command_ClearQueuedCodes:
                            {
                                _num_codes_loaded = 0;
                                _transaction_complete = true;

                                break;
                            }

                            case Command_ReadQueuedCodes:
                            case Command_ReadEchoedCodes:
                            {
                                int offset = 0;

                                offset++;  //  move past the UPA status for the time being

                                if( !(_inbound.data[offset++] & 0x80) )
                                {
                                    //  final block of codes, so we're done
                                    if( _command == Command_ReadEchoedCodes )
                                    {
                                        _command = Command_ClearEchoedCodes;
                                    }
                                }

                                while( offset < (_inbound.length - 1) )
                                {
                                    char buf[7];

                                    memcpy(buf, _inbound.data + offset, 6);
                                    buf[6] = 0;

                                    if( _command == Command_ReadEchoedCodes )
                                    {
                                        if( !gConfigParms.getValueAsString("PROTOCOL_LMI_VERIFY").contains("false", RWCString::ignoreCase) )
                                        {
                                            CtiVerificationReport *report = CTIDBG_new CtiVerificationReport(CtiVerificationBase::Protocol_Golay, _transmitter_id, string(buf), second_clock::universal_time());
                                            _verification_objects.push(report);
                                        }

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
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint - exceeded maximum codes for device \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    //  clear them out for safety
                                    _command = Command_ClearEchoedCodes;
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
                                        _transaction_complete = _seriesv.isTransactionComplete();
                                    }
                                }
                                else
                                {
                                    if( _command == Command_ScanAccumulator )
                                    {
                                        _transmitter_power  = _inbound.data[1];
                                        _transmitter_power |= _inbound.data[2] << 8;

                                        _transmitter_power_time = RWTime::now().seconds();

                                        _transaction_complete = true;
                                    }
                                    else
                                    {
                                        retval = !NORMAL;
                                    }
                                }

                                break;
                            }

                            case Command_Timesync:
                            {
                                if( _preload_sequence )
                                {
                                    _command = Command_ReadEchoedCodes;
                                }
                                else
                                {
                                    _transaction_complete = true;
                                }

                                break;
                            }

                            case Command_Loopback:
                            //  all we have to do is verify that the message got to us okay - nothing more
                            //    what will it take to return a message from a loopback?
                            default:
                            {
                                _transaction_complete = true;
                            }
                        }
                    }
                }
                else
                {
                    if( _inbound.preamble     == _outbound.preamble     &&
                        _inbound.length       == _outbound.length       &&
                        _inbound.dest_sat_id  == _outbound.dest_sat_id  &&
                        _inbound.dest_node    == _outbound.dest_node    &&
                        _inbound.src_sat_id   == _outbound.src_sat_id   &&
                        _inbound.src_sat_node == _outbound.src_sat_node &&
                        _inbound.body_header.message_type == _outbound.body_header.message_type &&
                        _inbound.body_header.flush_codes  == _outbound.body_header.flush_codes )
                    {
                        retval = ErrPortEchoResponse;
                    }
                    else
                    {
                        retval = ADDRESSERROR;
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

    if( status == ErrPortSimulated )
    {
        if( _outbound.body_header.message_type == Opcode_DownloadSystemData )
        {
            _config_sent = RWTime::now().seconds();
        }

        if( _preload_sequence )
        {
            if( _command == Command_Timesync )
            {
                _command = Command_ReadEchoedCodes;
            }
            else if( _command == Command_ReadEchoedCodes )
            {
                _command = Command_ClearEchoedCodes;
            }
            else if( _command == Command_ClearEchoedCodes )
            {
                if( _codes.size() )
                {
                    _command = Command_QueueCodes;
                }
                else
                {
                    _command = Command_ClearQueuedCodes;
                }
            }
            else if( _command == Command_QueueCodes )
            {
                _first_code_block = false;

                if( _codes.empty() )
                {
                    _transaction_complete = true;
                }
            }
            else
            {
                _transaction_complete = true;
            }
        }
        else
        {
            _transaction_complete = true;
        }

        retval = NoError;
    }
    else if( status )
    {
        //  retries go here eventually...  or something like that
        _transaction_complete = true;
    }

    return retval;
}


void CtiProtocolLMI::decodeStatuses(lmi_status statuses)
{
    CtiPointDataMsg *pd_template = CTIDBG_new CtiPointDataMsg(), *pd;

    pd_template->setType(StatusPointType);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(LMIPointOffset_CodeVerification);
    pd->setValue(statuses.loadshed_verify_state && !statuses.loadshed_verify_complete);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(LMIPointOffset_LMIComm);
    pd->setValue(statuses.comm_failure);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(LMIPointOffset_Transmitting);
    pd->setValue(statuses.loadshed_codes_locked);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(LMIPointOffset_PowerReset);
    pd->setValue(statuses.reset);
    _lmi_statuses.push_back(pd);

    delete pd_template;
}

