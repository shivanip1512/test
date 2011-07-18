#include "precompiled.h"

#include "logger.h"
#include "porter.h"
#include "msg_pdata.h"
#include "prot_lmi.h"
#include "prot_sa3rdparty.h"
#include "utility.h"
#include "numstr.h"
#include "cparms.h"
#include "ctidate.h"

#include "verification_objects.h"

using std::endl;
using std::list;
using std::vector;
using std::string;
using std::queue;
using std::pair;
using namespace boost::posix_time;

CtiProtocolLMI::CtiProtocolLMI() :
    _transmitter_power_time(0),
    _transmitter_power(-1),
    _num_codes_loaded(0),
    _config_sent(0),
    _address(0),
    _transmitter_id(0),
    _first_code_block(true),
    _verification_pending(false),
    _seriesv_inbuffer(NULL),
    _command(Command_ScanAccumulator),
    _control_offset(0),
    _control_parameter(0),
    _deadbands_sent(false),
    _transaction_complete(false),
    _retries(0),
    _tick_time(0),
    _transmit_window(0),
    _time_offset(0),
    _transmitter_power_low_limit(0),
    _transmitter_power_high_limit(0),
    _num_codes_retrieved(0),
    _outbound_code_count(0),
    _last_code_download(0),
    _comm_end_time(0),
    _transmission_end(0),
    _untransmitted_codes(false),
    _preload_sequence(false),
    _status_read(false),
    _codes_ready(false),
    _status_read_count(0),
    _echoed_error_count(0),
    _in_count(0),
    _in_total(0),
    _final_code_block(false)
{
    memset( &_outbound, 0, sizeof(lmi_message) );
    memset( &_inbound,  0, sizeof(lmi_message) );

    memset( &_status, 0, sizeof(lmi_status_union) );
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


void CtiProtocolLMI::setSystemData(int ticktime, int timeoffset, int transmittime, int transmitterlow, int transmitterhigh, unsigned startcode, unsigned stopcode)
{
    _tick_time   = ticktime;
    _time_offset = timeoffset;

    _transmit_window = transmittime;

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


int CtiProtocolLMI::sendCommRequest( OUTMESS *&OutMessage, std::list< OUTMESS* > &outList )
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

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        retVal = MemoryError;
    }

    return retVal;
}


int CtiProtocolLMI::recvCommResult( INMESS *InMessage, std::list< OUTMESS* > &outList )
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


void CtiProtocolLMI::getInboundData( std::list< CtiPointDataMsg* > &pointList, string &info )
{
    int i = 0;
    CtiPointDataMsg *pdm;

    _seriesv.getInboundPoints(pointList);

    if( _transmitter_power_time != 0 )
    {
        //  note that this will be analog offset LMIPointOffsetTransmitterPower + 1 when it gets back to the system
        pdm = CTIDBG_new CtiPointDataMsg(PointOffset_TransmitterPower, _transmitter_power, NormalQuality, AnalogPointType);
        pdm->setTime(_transmitter_power_time);
        pdm->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);

        pointList.push_back(pdm);

        _transmitter_power      = -1;
        _transmitter_power_time =  0;
    }

    if( !_returned_codes.empty() )
    {
        info += "Codes:\n";
    }

    while( !_returned_codes.empty() )
    {
        pair<int, int> golay_address = CtiProtocolSA3rdParty::parseGolayAddress(_returned_codes.front());
        _returned_codes.pop();

        info += CtiNumStr(golay_address.first).zpad(6) + "-" + CtiNumStr(golay_address.second) + " ";

        if( !(++i % 6) )
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
    _retries = RetryCount;
    _transmission_end = 0;
    _first_code_block = true;
    _final_code_block = false;
    _status_read = false;
    _codes_ready = false;
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
    if( isDebugLudicrous() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - OutMessage->VerificationSequence = " << om->VerificationSequence << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    CtiTime now, transmit_begin, transmit_end;
    int percode = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250);

    transmit_begin -= now.seconds() % (_tick_time * 60);
    transmit_begin += _time_offset;

    transmit_end = transmit_begin.seconds() + ((_num_codes_loaded * percode) / 1000);

    return (now < transmit_begin) || (now > transmit_end);
}

unsigned CtiProtocolLMI::getNumCodes( void ) const
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
    if( codes_to_download > MaxCodesDownloaded )
    {
        codes_to_download = MaxCodesDownloaded;
    }

    if( codes_to_download )
    {
        //  a couple of magic numbers here, but basically this assumes we get one full header packet back
        //    for every batch of codes we send out - this is approximately true
        total_comms += (codes_to_download * 6) + (((codes_to_download / MaxCodesPerTransaction) + 1) * (PacketOverheadLen * 2));
    }
    else
    {
        //  if no codes to download, we must clear the codes
        total_comms += PacketOverheadLen * 2;

    }
    if( _num_codes_loaded )
    {
        //  This is an exact copy of the code-loading behavior, except the outbound and inbound sizes are swapped
        total_comms += (_num_codes_loaded * 6) + (((_num_codes_loaded / MaxCodesPerTransaction) + 1) * (PacketOverheadLen * 2));
    }

    //  add on the config length
    if( _config_sent < (CtiTime::now().seconds() - gConfigParms.getValueAsULong("PORTER_LMI_SYSTEMDATA_INTERVAL", 86400)) )
    {
        total_comms += PacketOverheadLen * 2 + 18;
    }

    //  add on the time sync
    total_comms += PacketOverheadLen * 2;

    return total_comms;
}


CtiTime CtiProtocolLMI::getTransmissionEnd( void ) const
{
    return _transmission_end;
}


CtiTime CtiProtocolLMI::getLastCodeDownload( void ) const
{
    return _last_code_download;
}


bool CtiProtocolLMI::isTransactionComplete( void ) const
{
    bool retval = false;

    if( _comm_end_time && _comm_end_time <= CtiTime::now().seconds() )
    {
        retval = true;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - breaking out of late loop in \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    CtiTime NowTime;
    CtiDate NowDate;

    if( gConfigParms.getValueAsULong("LMI_DEBUGLEVEL", 0) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(slog);
        slog << CtiTime() << " **** prot_lmi generating with command = " << _command << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( _in_total > 0 )
    {
        lmi_message &packet = *((lmi_message *)&_inbound);

        xfer.setInBuffer((unsigned char *)&_inbound + _in_total);
        xfer.setInCountActual(&_in_count);

        //  we also expect the CRC
        xfer.setInCountExpected(packet.length + PacketOverheadLen - _in_total);

        xfer.setOutBuffer((unsigned char *)&_outbound);
        xfer.setOutCount(0);
    }
    else
    {
        _outbound.preamble = 0x01;
        _outbound.dest_sat_id  = 0x08;
        _outbound.dest_node    = _address;
        _outbound.src_sat_id   = 0x01;
        _outbound.src_sat_node = 0x01;
        _outbound.body_header.flush_codes  = 0;

        int count = _codes.size();
        while( !_codes.empty() && _codes.front() && _codes.front()->ExpirationTime && (_codes.front()->ExpirationTime < NowTime.seconds()) )
        {
            delete _codes.front();

            _codes.pop();
        }

        if( count != _codes.size() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(slog);
            slog << CtiTime() << " LMI device \"" << _name << "\" pruned " << (count - _codes.size()) << " codes this pass" << endl;
        }

        //  if we haven't ever read the statuses OR if there's an existing status that needs to be reset
        //    also make sure that we don't infinitely read the statuses
        if( (!_status_read || (_status.c & gConfigParms.getValueAsULong("PORTER_LMI_STATUS_RESETMASK", 0x80, 16)))
            && (++_status_read_count < MaxConsecutiveStatusScans) )
        {
            _status_read = true;  //  set here instead of the decode to prevent looping

            _outbound.length  = 3;
            _outbound.body_header.message_type = Opcode_ClearAndReadStatus;
            _outbound.data[0] = _status.c & gConfigParms.getValueAsULong("PORTER_LMI_STATUS_RESETMASK", 0x80, 16);
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
                    dout << CtiTime() << " **** Checkpoint - persistent 'questionable request' respose from LMI **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _transaction_complete = true;

                retval = NOTNORMAL;
            }
            //  if we've never downloaded the system parameters, send them out (once per day by default)
            else if( _config_sent < (CtiTime::now().seconds() - gConfigParms.getValueAsULong("PORTER_LMI_SYSTEMDATA_INTERVAL", 86400)) )
            {
                //  limit each to 6 characters
                string start_code = CtiNumStr(_start_code % 1000000).zpad(6);
                string stop_code  = CtiNumStr(_stop_code  % 1000000).zpad(6);

                _outbound.length  = 19;
                _outbound.body_header.message_type = Opcode_DownloadSystemData;
                _outbound.data[ 0] =  _tick_time;
                _outbound.data[ 1] =  _time_offset & 0xff;
                _outbound.data[ 2] = (_time_offset >> 8) & 0xff;
                _outbound.data[ 3] =  _transmitter_power_low_limit & 0xff;
                _outbound.data[ 4] = (_transmitter_power_low_limit >> 8) & 0xff;
                _outbound.data[ 5] =  _transmitter_power_high_limit & 0xff;
                _outbound.data[ 6] = (_transmitter_power_high_limit >> 8) & 0xff;
                _outbound.data[ 7] = start_code[0];
                _outbound.data[ 8] = start_code[1];
                _outbound.data[ 9] = start_code[2];
                _outbound.data[10] = start_code[3];
                _outbound.data[11] = start_code[4];
                _outbound.data[12] = start_code[5];
                _outbound.data[13] = stop_code[0];
                _outbound.data[14] = stop_code[1];
                _outbound.data[15] = stop_code[2];
                _outbound.data[16] = stop_code[3];
                _outbound.data[17] = stop_code[4];
                _outbound.data[18] = stop_code[5];
            }
            else
            {
                switch( _command )
                {
                    case Command_QueueCodes:
                    {
                        queue< CtiOutMessage * > viable_codes;
                        CtiOutMessage *om;

                        int millis_per_code, slots_available, expired_code_count;

                        if( _first_code_block )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(slog);
                                slog << CtiTime() << " LMI device \"" << _name << "\" removing previous outbound codes" << endl;
                            }

                            _num_codes_loaded = 0;
                        }

                        millis_per_code  = gConfigParms.getValueAsULong("PORTER_LMI_TIME_TRANSMIT", 250);

                        slots_available  = _transmit_window * 1000;
                        slots_available /= millis_per_code;
                        slots_available -= _num_codes_loaded;

                        expired_code_count = 0;

                        while( !_codes.empty() && viable_codes.size() < MaxCodesPerTransaction && viable_codes.size() < slots_available )
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

                                    expired_code_count++;
                                }

                                om = 0;  //  for safekeeping
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - removing null OM from device queue for LMI device \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        if( expired_code_count )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << CtiTime() << " LMI device \"" << _name << "\" pruned " << expired_code_count << " codes this pass" << endl;
                        }

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << CtiTime() << " LMI device \"" << _name << "\" loading " << viable_codes.size() << " (of " << (viable_codes.size() + _codes.size()) << " potential) codes this pass (" << slots_available << " slots available):" << endl;
                        }

                        _outbound_code_count = viable_codes.size();

                        _outbound.length  = (_outbound_code_count * 6) + 2;
                        _outbound.body_header.message_type = Opcode_SendCodes;
                        _outbound.body_header.flush_codes  = _first_code_block;
                        _outbound.data[0] = _outbound_code_count;

                        //  if we have no more codes OR we have no more space OR we have no more time
                        if( _codes.empty()   ||
                            !slots_available ||
                            (viable_codes.size() >= slots_available) ||
                            (_comm_end_time && ((_comm_end_time + 1) <= CtiTime::now().seconds())) )
                        {
                            _final_code_block = true;

                            _outbound.data[0] |= SendCodes_LastCodeGroup;
                        }

                        unsigned offset = 1;

                        while( !viable_codes.empty() )
                        {
                            om = viable_codes.front();
                            viable_codes.pop();

                            char (&codestr)[7] = om->Buffer.SASt._codeSimple;

                            codestr[6] = 0;  //  make sure it's null-terminated, just to be safe...

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

                            if( !findStringIgnoreCase(gConfigParms.getValueAsString("PROTOCOL_LMI_VERIFY") ,"false" ))
                            {
                                //  we don't expire the messages until well after they should've been collected
                                ptime::time_duration_type expiration(seconds((60 + 30) * _tick_time));

                                string golay_codestr;
                                pair< int, int > golay_code = CtiProtocolSA3rdParty::parseGolayAddress(codestr);

                                golay_codestr  = CtiNumStr(golay_code.first).zpad(6);       //  base address
                                golay_codestr += "-";
                                golay_codestr += CtiNumStr(golay_code.second - 1);  //  make the function 0-based so it'll match the RTM's result

                                CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_Golay, *om, CtiProtocolSA3rdParty::asString(om->Buffer.SASt).data(), golay_codestr, expiration);

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
                xfer.setInCountExpected(PacketHeaderLen);
            }
            else
            {
                xfer.setInCountExpected(0);
            }

            xfer.setOutBuffer((unsigned char *)&_outbound);
            xfer.setOutCount(_outbound.length + PacketOverheadLen);

            //  tack on the CRC
            if( xfer.getOutCount() > 0 )
            {
                unsigned short crc = CCITT16CRC(-1, (unsigned char *)&_outbound, _outbound.length + PacketHeaderLen, false);

                _outbound.data[(_outbound.length - 1) + 0] = (crc & 0xff00) >> 8;
                _outbound.data[(_outbound.length - 1) + 1] =  crc & 0x00ff;
            }
        }
        else
        {
            //  what should we do if the generate causes an error?

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - retval = " << retval << " in CtiProtocolLMI::generate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    CtiTime Now;

    if( !status )
    {
        _retries = RetryCount;

        _in_total += xfer.getInCountActual();

        if( _in_total >= PacketHeaderLen && _in_total >= (_inbound.length + PacketOverheadLen) )
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

                        if( _status.s.loadshed_verify_state && _status.s.loadshed_verify_complete )
                        {
                            _codes_ready = true;
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

                        if( _status.s.reset )
                        {
                            _config_sent = 0;  //  force it to be sent again - the RTU's brain has been wiped somehow
                        }
                    }
                    else if( _inbound.body_header.message_type == Opcode_DownloadSystemData )
                    {
                        _config_sent = CtiTime::now().seconds();
                    }
                    else
                    {
                        switch( _command )
                        {
                            case Command_QueueCodes:
                            {
                                _first_code_block = false;

                                _verification_pending = true;

                                //  is this safe?
                                _transaction_complete = _final_code_block;

                                //  _outbound_code_count exists only to keep the code count in a
                                //    known location from generate() to decode() instead of trying
                                //    to pull it out of _outbound.data[0]...  that would be gross, dude
                                _num_codes_loaded += _outbound_code_count;

                                _last_code_download = CtiTime::now().seconds();

                                _transmission_end  = CtiTime::now().seconds();
                                _transmission_end -= _transmission_end % (_tick_time * 60);
                                _transmission_end += _time_offset;

                                while( _transmission_end < CtiTime::now().seconds() )
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
                                        //  GRE says to keep the codes if we don't have any to send...
                                        //_command = Command_ClearQueuedCodes;

                                        //  so we're done
                                        _transaction_complete = true;
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
                                bool final_block = false;

                                offset++;  //  move past the UPA status for the time being

                                if( !(_inbound.data[offset++] & 0x80) )
                                {
                                    final_block = true;
                                }

                                while( offset < (_inbound.length - 1) )
                                {
                                    char codestr[7];

                                    memcpy(codestr, _inbound.data + offset, 6);
                                    codestr[6] = 0;

                                    if( _command == Command_ReadEchoedCodes )
                                    {
                                        if( !findStringIgnoreCase(gConfigParms.getValueAsString("PROTOCOL_LMI_VERIFY"),"false") )
                                        {
                                            string golay_codestr;
                                            pair< int, int > golay_code = CtiProtocolSA3rdParty::parseGolayAddress(codestr);

                                            golay_codestr  = CtiNumStr(golay_code.first).zpad(6);       //  base address
                                            golay_codestr += "-";
                                            golay_codestr += CtiNumStr(golay_code.second - 1);  //  make the function 0-based so it'll match the RTM's result

                                            if( gConfigParms.getValueAsULong("LMI_DEBUGLEVEL", 0) )
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " CtiProtocolLMI::decode() creating report object for code \"" << golay_codestr << "\", receiver_id (" << _transmitter_id << ")" << __FILE__ << " (" << __LINE__ << ")" << endl;
                                            }

                                            CtiVerificationReport *report = CTIDBG_new CtiVerificationReport(CtiVerificationBase::Protocol_Golay, _transmitter_id, golay_codestr, second_clock::universal_time());
                                            _verification_objects.push(report);
                                        }

                                        _verification_pending = false;
                                    }
                                    else
                                    {
                                        _retrieved_codes.push(atoi(codestr));
                                    }

                                    offset += 6;

                                    _num_codes_retrieved++;
                                }

                                //  shouldn't be possible
                                if( _num_codes_retrieved > 0xff )
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint - exceeded maximum codes for device \"" << _name << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    if( _command == Command_ReadEchoedCodes )
                                    {
                                        //  clear them out for safety
                                        _command = Command_ClearEchoedCodes;
                                    }
                                    else
                                    {
                                        _transaction_complete = true;
                                    }
                                }
                                else if( final_block )
                                {
                                    if( _command == Command_ReadEchoedCodes )
                                    {
                                        _command = Command_ClearEchoedCodes;
                                    }
                                    else
                                    {
                                        _transaction_complete = true;
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
                                    memcpy(_seriesv_inbuffer, buf + PacketHeaderLen + 1, _in_total - PacketHeaderLen - 1);

                                    seriesv_xfer.setInBuffer(_seriesv_inbuffer);
                                    seriesv_xfer.setInCountActual(&seriesv_incount_actual);
                                    seriesv_xfer.setInCountActual(_in_total - PacketHeaderLen - 1);

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

                                        _transmitter_power_time = CtiTime::now().seconds();

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
                                    if( _codes_ready )
                                    {
                                        _command = Command_ReadEchoedCodes;
                                    }
                                    else
                                    {
                                        _command = Command_ClearEchoedCodes;

                                        if( isDebugLudicrous() )
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " **** Checkpoint - !_codes_ready in CtiProtocolLMI::decode() for device " << _name << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }
                                    }
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
            _config_sent = CtiTime::now().seconds();
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
                    //  GRE says to keep the codes if we don't have any to send...
                    //_command = Command_ClearQueuedCodes;

                    //  so we're done
                    _transaction_complete = true;
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
        _retries--;

        if( !_retries )
        {
            _transaction_complete = true;
        }

        retval = status;
    }

    return retval;
}


void CtiProtocolLMI::decodeStatuses(lmi_status statuses)
{
    CtiPointDataMsg *pd_template = CTIDBG_new CtiPointDataMsg(), *pd;

    pd_template->setType(StatusPointType);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_LoadShedVerifyState);
    pd->setValue(statuses.loadshed_verify_state);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_LoadShedVerifyComplete);
    pd->setValue(statuses.loadshed_verify_complete);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_QuestionableRequest);
    pd->setValue(statuses.questionable_request);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_LMICommunicationFailure);
    pd->setValue(statuses.comm_failure);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_TransmitterLoadVerified);
    pd->setValue(statuses.transmitter_load_verified);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_LoadShedCodesLocked);
    pd->setValue(statuses.loadshed_codes_locked);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_LMIReset);
    pd->setValue(statuses.reset);
    _lmi_statuses.push_back(pd);

    pd = (CtiPointDataMsg *)pd_template->replicateMessage();
    pd->setId(PointOffset_CodeVerification);
    pd->setValue((statuses.loadshed_verify_complete << 1) | statuses.loadshed_verify_state);
    _lmi_statuses.push_back(pd);

    delete pd_template;
}

