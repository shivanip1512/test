#include "yukon.h"

#include "ccu711.h"

#include "boostutil.h"

#include "emetconwords.h"

#include "cticalls.h"
#include "cti_asmc.h"
#include "color.h"
#include "time.h"
#include "ctidate.h"

#include "simulator.h"

#include <boost/scoped_array.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

Ccu711::Ccu711(unsigned char address, int strategy) :
    _address(address),
    _ccu710(address, strategy),
    _strategy(strategy),
    _expected_sequence(0),
    _ccu711InTag("CCU711(" + CtiNumStr(address) + ")-IN"),
    _ccu711OutTag("CCU711(" + CtiNumStr(address) + ")-OUT")
{
}

bool Ccu711::handleRequest(Comms &comms, Logger &logger)
{
    idlc_request request;
    idlc_reply   reply;

    error_t error;

    {
        ScopedLogger scope = logger.getNewScope(_ccu711InTag);
    
        if( error = readRequest(comms, request) )
        {
            scope.log("Error reading request / " + error, request.message);
            return false;
        }
    
        scope.log(request.description, request.message);
    
        //  get us up to date before we try to process anything new
        processQueue(scope);
    
        if( error = processRequest(request, reply, scope) )
        {
            scope.log("Error processing request / " + error);
            return false;
        }
    
        if( reply.message.empty() )
        {
            scope.log("No reply generated");
            return true;
        }
    }

    {
        ScopedLogger scope = logger.getNewScope(_ccu711OutTag);
            
        if( error = sendReply(comms, reply, scope) )
        {
            scope.log("Error sending reply / " + error);
            return false;
        }
    
        scope.log(describeReply(reply), reply.message);
    }

    return true;
}


error_t Ccu711::readRequest(Comms &comms, idlc_request &request) const
{
    byte_appender request_message_oitr = byte_appender(request.message);

    error_t error;

    if( !comms.read(request_message_oitr, Idlc_HeaderLength) )
    {
        return "Timeout reading IDLC header";
    }
    if( error = extractIdlcHeader(request.message, request.header) )
    {
        return "Invalid IDLC header / " + error;
    }

    if( request.header.control_command == IdlcLink_GeneralRequest ||
        request.header.control_command == IdlcLink_BroadcastRequest )
    {
        static const int info_length_size = 1;

        if( !comms.read(request_message_oitr, info_length_size) )
        {
            return "Timeout reading request info length";
        }
        if( !comms.read(request_message_oitr, request.message.back()) )
        {
            return "Timeout reading request info";
        }
        if( error = extractRequestInfo(request.message, request.info) )
        {
            return "Invalid request info / " + error;
        }
    }

    if( !comms.read(request_message_oitr, Idlc_CrcLength) )
    {
        return "Timeout reading CRC";
    }
    if( error = validateCrc(request.message) )
    {
        return "Invalid CRC";
    }

    request.description = describeRequest(request);

    return error_t::success;
}


error_t Ccu711::extractIdlcHeader(const bytes &message, idlc_header &header) const
{
    const int index_flag    = 0,
              index_address = 1,
              index_control = 2;

    if( message.size() < Idlc_HeaderLength )
    {
        return error_t("Insufficient data for header").lt(message.size(), Idlc_HeaderLength);
    }
    if( message[index_flag] != Hdlc_FramingFlag )
    {
        return error_t("HDLC framing flag not found").neq((int)message[index_flag], Hdlc_FramingFlag);
    }

    header.address = message[index_address] >> 1;

    bytes::value_type const &control_octet = message[index_control];

    header.control_command = IdlcLink_Invalid;
    header.control_sequence          = -1;
    header.control_sequence_expected = -1;

    if( control_octet & 0x01 )
    {
        if( control_octet == HdlcLink_Ud )
        {
            header.control_command = IdlcLink_BroadcastRequest;
        }
        else if( control_octet == HdlcLink_Sarm )
        {
            header.control_command = IdlcLink_ResetRequest;
        }
        else if( control_octet == HdlcLink_Ua )
        {
            header.control_command = IdlcLink_ResetAcknowledge;
        }
        else if( control_octet & HdlcLink_RejectMask == HdlcLink_Rej )
        {
            header.control_command = IdlcLink_RejectWithRestart;
            header.control_sequence_expected = control_octet >> 5;
        }
        else if( control_octet & HdlcLink_RejectMask == HdlcLink_Srej )
        {
            header.control_command = IdlcLink_RetransmitRequest;
            header.control_sequence_expected = control_octet >> 5;
        }
    }
    else
    {
        header.control_command = IdlcLink_GeneralRequest;
        header.control_sequence          = (control_octet >> 1) & 0x07;
        header.control_sequence_expected = (control_octet >> 5);
    }

    if( header.control_command == IdlcLink_Invalid )
    {
        return error_t("Invalid control command", header.control_command);
    }

    return error_t::success;
}


error_t Ccu711::extractRequestInfo(const bytes &message, request_info &info) const
{
    const int index_length  = 3,
              index_command = 5;

    if( message.size() < Idlc_HeaderLength + Info_HeaderLength ||
        message.size() < Idlc_HeaderLength + message[index_length] )
    {
        return "Request info too short";
    }

    //  trim off the length and command bytes
    bytes command_data(message.begin() + Idlc_HeaderLength + Info_HeaderLength, message.end());

    //  default to 0 reply length - subsequent decodes may assign a value in here
    info.reply_length = 0;

    switch( message[index_command] )
    {
        case Command_Actin:     info.command = Command_Actin;  return error_t::success; // extractRequestInfo_Actin(command_data, info);
        case Command_WSets:     info.command = Command_WSets;  return error_t::success; // extractRequestInfo_WSets(command_data, info);
        case Command_DTran:     info.command = Command_DTran;  return extractRequestInfo_DTran(command_data, info);
        case Command_RColQ:     info.command = Command_RColQ;  return extractRequestInfo_RColQ(command_data, info);
        case Command_Xtime:     info.command = Command_Xtime;  return extractRequestInfo_XTime(command_data, info);
        case Command_LGrpQ:     info.command = Command_LGrpQ;  return extractRequestInfo_LGrpQ(command_data, info);
        default:
        {
            //  we'll just create an empty generic reply, so this is still a success
            return error_t::success;
        }
    }
}


error_t Ccu711::extractRequestInfo_DTran(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in DTran decode";
    }

    info.reply_length = command_data[0];

    info.dtran.message.assign(command_data.begin() + 1, command_data.end());

    _ccu710.readRequest(BufferCommsIn(info.dtran.message), info.dtran.request);

    return error_t::success;
}


error_t Ccu711::extractRequestInfo_RColQ(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in RColQ decode";
    }
    if( command_data.size() > 1 )
    {
        return error_t("Invalid data length in RColQ decode", command_data.size());
    }

    info.reply_length = command_data[0];

    return error_t::success;
}


error_t Ccu711::extractRequestInfo_XTime(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in XTime decode";
    }
    if( command_data.size() != 8 )
    {
        return error_t("Invalid data length in XTime decode", command_data.size());
    }

    info.xtime.year    = (command_data[0] << 8) | command_data[1];
    info.xtime.day     = (command_data[2] << 8) | command_data[3];
    info.xtime.day_of_week = command_data[4];  //  redundant, but included for completeness
    info.xtime.period  = command_data[5];
    info.xtime.seconds = (command_data[6] << 8) | command_data[7];

    info.xtime.timesync = CtiTime(CtiDate(info.xtime.day, info.xtime.year));
    info.xtime.timesync += info.xtime.period * 28800;
    info.xtime.timesync += info.xtime.seconds;

//  TODO-P3: send an MCT-300 (pre-MCT-400) timesync on all default routes

    return error_t::success;
}


error_t Ccu711::extractRequestInfo_LGrpQ(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in LGrpQ decode";
    }
    if( command_data.size() > 254 )
    {
        return error_t("Invalid data length in LGrpQ decode", command_data.size());
    }

    int index = 0, setl;

    while( setl = command_data[index] )
    {
        queue_entry a_queue_entry;

        if( index + setl >= command_data.size() )
        {
            return error_t("Broken queue entry in LGrpQ decode").geq(index + setl, command_data.size());
        }

        error_t error;

        if( error = extractQueueEntry(command_data, index, setl, a_queue_entry) )
        {
            return "Broken queue entry in LGrpQ decode" + error;
        }

        info.lgrpq.request_group.push_back(a_queue_entry);

        index += setl;
    }

    return error_t::success;
}


error_t Ccu711::extractQueueEntry(const bytes &command_data, int index, int setl, queue_entry &a_queue_entry) const
{
    if( setl < 17 )
    {
        return error_t("Invalid queue entry length").lt(setl, 17);
    }
    if( command_data[index + 1] & 0x80 )
    {
        return "Short form requests not supported in LGrpQ";
    }

    a_queue_entry.entry_id  = command_data[index + 1] << 24;
    a_queue_entry.entry_id |= command_data[index + 2] << 16;
    a_queue_entry.entry_id |= command_data[index + 3] <<  8;
    a_queue_entry.entry_id |= command_data[index + 4];

    a_queue_entry.priority  = command_data[index + 5];

    {
        queue_entry::request_info &request = a_queue_entry.request;

        request.address   = command_data[index + 6] << 16;
        request.address  |= command_data[index + 7] <<  8;
        request.address  |= command_data[index + 8];

        request.bus               =  command_data[index + 9];
        request.repeater_fixed    = (command_data[index + 10] & 0x1f) >> 0;
        request.repeater_variable = (command_data[index + 10] & 0xe0) >> 5;
        request.repeater_count    =  command_data[index + 11];

        if( command_data[index + 12] != 1 )
        {
            return error_t("NFUNC != 1 not supported").neq(command_data[index + 12], 1);
        }

        switch( command_data[index + 13] & 0xc0 )
        {
            default:
            case 0x00:  request.word_type = EmetconWord::WordType_Invalid;  break;
            case 0x40:  request.word_type = EmetconWord::WordType_A;        break;
            case 0x80:  request.word_type = EmetconWord::WordType_B;        break;
            case 0xc0:  request.word_type = EmetconWord::WordType_G;        break;
        }

        request.write      = !(command_data[index + 13] & 0x08 );
        request.function   = (command_data[index + 13] & 0x10 );

        //  index + 14 is not used for B words, but we may need to add back in for G word support

        request.function_code = command_data[index + 15];

        request.length        = command_data[index + 16];

        if( request.write && request.length > (EmetconWordC::PayloadLength * 3) )
        {
            return error_t("Invalid payload length for write").gt(request.length, EmetconWordC::PayloadLength * 3);
        }
        if( !request.write && request.length > (EmetconWordD1::PayloadLength +
                                                EmetconWordD2::PayloadLength +
                                                EmetconWordD3::PayloadLength) )
        {
            return error_t("Invalid payload length for read").gt(request.length, EmetconWordD1::PayloadLength +
                                                                                 EmetconWordD2::PayloadLength +
                                                                                 EmetconWordD3::PayloadLength);
        }

        unsigned word_count = request.write ? EmetconWordC::words_needed(request.length) : EmetconWordD::words_needed(request.length);

        request.b_word = EmetconWordB(request.repeater_fixed,
                                      request.repeater_variable,
                                      request.address,
                                      word_count,
                                      request.function_code,
                                      request.function,
                                      request.write);

        if( request.write && request.length )
        {
            if( index + 17 + request.length > command_data.size() )
            {
                return error_t("Insufficient data for payload").gt(index + 17 + request.length, command_data.size());
            }

            request.data.assign(command_data.begin() + 17,
                                command_data.begin() + 17 + request.length);

            if( !request.data.empty() )
            {
                bytes data(request.data.begin(),
                             request.data.begin() + min(request.data.size(), (bytes::size_type)EmetconWordC::PayloadLength));

                request.c_words.push_back(EmetconWordC(data));
            }
            if( request.data.size() > EmetconWordC::PayloadLength )
            {
                bytes data(request.data.begin() + EmetconWordC::PayloadLength,
                             request.data.begin() + min(request.data.size(), (bytes::size_type)EmetconWordC::PayloadLength * 2));

                request.c_words.push_back(EmetconWordC(data));
            }
            if( request.data.size() > EmetconWordC::PayloadLength * 2 )
            {
                bytes data(request.data.begin() + EmetconWordC::PayloadLength * 2,
                             request.data.begin() + min(request.data.size(), (bytes::size_type)EmetconWordC::PayloadLength * 3));

                request.c_words.push_back(EmetconWordC(data));
            }
        }
    }

    return error_t::success;
}


error_t Ccu711::validateCrc(const bytes &message) const
{
    //  this array is for the interface with NCrcCalc_C, which wants a raw buffer
    boost::scoped_array<unsigned char> buf(new unsigned char[message.size() - 3]);

    std::copy(message.begin() + 1, message.end() - 2, buf.get());

    unsigned short message_crc = MAKEUSHORT(*(message.end() - 1), *(message.end() - 2));

    unsigned short crc = NCrcCalc_C(buf.get(), message.size() - 3);

    if( message_crc != crc )
    {
        return error_t("CRCs do not match").neq(message_crc, crc);
    }

    return error_t::success;
}


string Ccu711::describeRequest(const idlc_request &request) const
{
    ostringstream request_description;

    request_description << "Request / address " << request.header.address;

    request_description << " / ";

    switch( request.header.control_command )
    {
        case IdlcLink_GeneralRequest:
        {
            request_description << "general request";
            request_description << " / sequence " << request.header.control_sequence;
            request_description << " / expected " << request.header.control_sequence_expected;
            request_description << endl;
            request_description << describeGeneralRequest(request.info);
            break;

        }
        case IdlcLink_BroadcastRequest:
        {
            request_description << "broadcast request";
            break;
        }
        case IdlcLink_RetransmitRequest:
        {
            request_description << "retransmit request";
            request_description << " / expected " << request.header.control_sequence_expected;
            break;
        }
        case IdlcLink_ResetRequest:
        {
            request_description << "reset request";
            break;
        }

        default:
        case IdlcLink_GeneralReply:
        case IdlcLink_RejectWithRestart:
        case IdlcLink_ResetAcknowledge:
        {
            request_description << "invalid request (" << hex << setw(2) << setfill('0') << request.header.control_command << ")";
            break;
        }
    }

    return request_description.str();
}


string Ccu711::describeGeneralRequest(const request_info &info) const
{
    ostringstream info_description;

    info_description << "command ";

    switch( info.command )
    {
        case Command_DTran:
        {
            info_description << " DTran / length " << info.dtran.message.size() << endl;

            info_description << info.dtran.request->description;

            break;
        }
        case Command_RColQ:
        {
            info_description << " RColQ / reply length " << info.reply_length;
            break;
        }
        case Command_Xtime:
        {
            info_description << " XTime / " << info.xtime.timesync;
            break;
        }
        case Command_LGrpQ:
        {
            info_description << " LGrpQ / " << info.lgrpq.request_group.size() << " requests";

            vector<queue_entry>::const_iterator entry_itr = info.lgrpq.request_group.begin(),
                                                entry_end = info.lgrpq.request_group.end();

            for( ; entry_itr != entry_end; ++entry_itr )
            {
                info_description << endl;

                info_description << "qenid "    << setw(10) << setfill('0') << entry_itr->entry_id << ", ";
                info_description << "priority " << setw(2)                  << entry_itr->priority << ", ";

                const queue_entry::request_info &request = entry_itr->request;

                info_description << "addr " << setw(7) << request.address << ", ";

                switch( request.word_type )
                {
                    case EmetconWord::WordType_A:
                    {
//  TODO-P2: add A word description
                        info_description << "A word";
                        break;
                    }
                    case EmetconWord::WordType_B:
                    {
                        info_description << "B word";
                        info_description << ", ";

                        info_description << (request.function?'f':' ');
                        info_description << (request.write   ?'w':'r');
                        info_description << hex << setw(2) << setfill('0') << request.function_code << dec << ", ";

                        info_description << "repeater fixed bits "    << request.repeater_fixed    << ", ";
                        info_description << "repeater variable bits " << request.repeater_variable << ", ";
                        info_description << "repeater count "         << request.repeater_count    << ", ";
                        info_description << "bus "                    << request.bus;

                        if( request.write )
                        {
                            info_description << ", data: " << hex;

                            bytes::const_iterator request_itr = request.data.begin();
                            bytes::const_iterator request_end = request.data.end();

                            int dFill = info_description.fill('0');

                            while( request_itr != request_end )
                            {
                                info_description << setw(2) << static_cast<int>(*request_itr++) << " ";
                            }

                            info_description.fill(dFill);

                            info_description << dec;
                        }

                        break;
                    }
                    case EmetconWord::WordType_G:
                    {
                        info_description << "G word, ignored";
                        break;
                    }
                    default:
                    {
                        info_description << "unhandled word (" << request.word_type << ")";
                    }
                }
            }

            break;
        }
        case Command_Actin:
        {
            info_description << " Actin ";
            break;
        }
        case Command_WSets:
        {
            info_description << " WSets ";
            break;
        }
        default:
        {
            info_description << " Unhandled command " << info.command;
            break;
        }
    }

    return info_description.str();
}


void Ccu711::processQueue(Logger &logger)
{
    CtiTime now;

    queue_info::pending_set::const_iterator pending_itr = _queue.pending.begin(),
                                            pending_end = _queue.pending.end();

    while( pending_itr != pending_end && _queue.last_transmit < now )
    {
        queue_entry entry = *pending_itr;

        _queue.pending.erase(pending_itr++);

        _queue.last_transmit += queue_request_dlc_time(entry.request);

        bytes request_buf;

        entry.request.b_word.serialize(byte_appender(request_buf));

        if( entry.request.write )
        {
            copy(entry.request.c_words.begin(),
                 entry.request.c_words.end(),
                 EmetconWord::serializer(byte_appender(request_buf)));

            Grid.oneWayCommand(request_buf, logger);

            entry.result.completion_status = queue_entry::result_info::CompletionStatus_Successful;
        }
        else
        {
            bytes reply_buf;

            Grid.twoWayCommand(request_buf, reply_buf, logger);

            words_t reply_words;

//  TODO-P3: return more interesting errors if we get crosstalk, etc
//  TODO-P2: validate that we got the expected data back, i.e.:
//            if( b_word->words_to_follow == reply_words.size() )

            EmetconWord::restoreWords(reply_buf, reply_words);

            error_t error;

            if( error = extractInformation(reply_words, entry))
            {
                logger.log("Error extracting information from reply words / " + error);

                entry.result.completion_status = queue_entry::result_info::CompletionStatus_TransponderFailure;
            }
            else
            {
                entry.result.completion_status = queue_entry::result_info::CompletionStatus_Successful;
            }
        }

        entry.result.completion_time = _queue.last_transmit;

        _queue.completed.insert(entry);
    }

    if( _queue.pending.empty() )
    {
        _queue.last_transmit = now;
    }

    _status.statd.ncsets = _queue.completed.size();
    _status.statd.readyn = 0x20 - _queue.pending.size() - _queue.completed.size();

    queue_info::completed_set::const_iterator completed_itr = _queue.completed.begin(),
                                              completed_end = _queue.completed.end();

    _status.statd.ncocts = 0;

    for( ; completed_itr != completed_end; ++completed_itr )
    {
        _status.statd.ncocts += (completed_itr->request.write)?(15):(16 + completed_itr->result.data.size());
    }
}

unsigned Ccu711::queue_request_dlc_time(const queue_entry::request_info &request)
{
    unsigned bits_out = 0,
             bits_in  = 0;

    //  a little bit of pragmatic code duplication from extractQueueEntry - otherwise there
    //    would be some messy type checking going on here

    bits_out += EmetconWordB::BitLength;

    if( request.write )
    {
        bits_out += EmetconWordC::BitLength * EmetconWordC::words_needed(request.length);
    }
    else
    {
        bits_in  += EmetconWordD::BitLength * EmetconWordD::words_needed(request.length);
    }

    return ( dlc_time(bits_out, bits_in) * (request.repeater_count + 1) + 999 )/ 1000;
}


template<class DataWord>
void extractWordData(const DataWord &dw, byte_appender &output)
{
    copy(dw.data, dw.data + DataWord::PayloadLength, output);
}

error_t Ccu711::extractInformation(const words_t &reply_words, queue_entry &entry)
{
    error_t error;

    if( error = extractData(reply_words, byte_appender(entry.result.data)) )
    {
        return error_t::error;
    }
    if( error = extractTS_Values(reply_words, entry) )
    {
        return error_t::error;
    }

    return error_t::success;
}

error_t Ccu711::extractTS_Values(const words_t &reply_words, queue_entry &entry)
{
    if( reply_words.empty() )
    {
        return error_t::success;
    }
    if( !reply_words[0] )
    {
        return "reply_words[0] is null";
    }
    if( reply_words[0]->type != EmetconWord::WordType_D1 )
    {
        return error_t("reply_words[0] is not a D1 word").neq(reply_words[0]->type, EmetconWord::WordType_D1);
    }

    boost::shared_ptr<const EmetconWordD1> word = boost::static_pointer_cast<const EmetconWordD1>(reply_words[0]);
    if( word->alarm == true )
    {
        // If the D-word's alarm bit is set to true, then our response data needs to 
        // reflect this. Bit 0 of the TS values is the general alarm bit. Using a 
        // bitwise OR with 1 correctly sets this bit.
        entry.result.ts_values |= TS_AlarmEnabled;
    }
    
    return error_t::success;
}

error_t Ccu711::extractData(const words_t &reply_words, byte_appender &output)
{
    if( reply_words.empty() )
    {
        return error_t::success;
    }
    if( !reply_words[0] )
    {
        return "reply_words[0] is null";
    }
    if( reply_words[0]->type != EmetconWord::WordType_D1 )
    {
        return error_t("reply_words[0] is not a D1 word").neq(reply_words[0]->type, EmetconWord::WordType_D1);
    }

    extractWordData(*(boost::static_pointer_cast<const EmetconWordD1>(reply_words[0])), output);

    if( reply_words.size() < 2 )
    {
        return error_t::success;
    }
    if( !reply_words[1] )
    {
        return "reply_words[1] is null";
    }
    if( reply_words[1]->type != EmetconWord::WordType_D2 )
    {
        return error_t("reply_words[1] is not a D2 word").neq(reply_words[1]->type, EmetconWord::WordType_D2);
    }

    extractWordData(*(boost::static_pointer_cast<const EmetconWordD2>(reply_words[1])), output);

    if( reply_words.size() < 3 )
    {
        return error_t::success;
    }
    if( !reply_words[2] )
    {
        return "reply_words[2] is null";
    }
    if( reply_words[2]->type != EmetconWord::WordType_D3 )
    {
        return error_t("reply_words[2] is not a D3 word").neq(reply_words[2]->type, EmetconWord::WordType_D3);
    }

    extractWordData(*(boost::static_pointer_cast<const EmetconWordD3>(reply_words[2])), output);

    return error_t::success;
}


error_t Ccu711::processRequest(const idlc_request &request, idlc_reply &reply, Logger &logger)
{
    error_t error;

    back_insert_iterator<bytes> reply_inserter(reply.message);

    reply.header.address = request.header.address;
    reply.info.status = _status;

    //  Right now, the only IdlcLinkCommands implemented are:
    //    * GeneralRequest
    //    * ResetRequest
    //  Ignored:
    //    * BroadcastRequest
    //  May want to add:
    //    * RetransmitRequest (timesyncs may be broadcast this way...?)
    //    * BroadcastRequest

    switch( request.header.control_command )
    {
        case IdlcLink_GeneralRequest:
        {
            //  I think this is the right place... ?
            if( request.header.control_sequence != _expected_sequence )
            {
                reply.header.control_command = IdlcLink_RejectWithRestart;
                reply.header.control_sequence_expected = _expected_sequence;

                //  sequence mismatch, we need to re-send any queue entries we just sent
                _queue.completed.insert(_queue.returned.begin(),
                                        _queue.returned.end());

//  TODO-P3: We might not want to erase the returned entries if we implement frame retransmit...
//             In that case, we'd probably keep a set of completed_set::const_iterators that we could
//             erase out of _queue.completed instead of swapping the actual elements like we're doing now.
                _queue.returned.clear();

                if( error = writeIdlcHeader(reply.header, reply_inserter) )
                {
                    return "Error writing IDLC reply header / " + error;
                }
                if( error = writeIdlcCrc(reply.message, reply_inserter) )
                {
                    return "Error writing IDLC CRC / " + error;
                }

                return error_t::success;
            }

            _expected_sequence = (request.header.control_sequence + 1) % 8;

            //  the sequence matches, so they must've gotten our reply - empty out the queued entries we sent back
            _queue.returned.clear();

            reply.header.control_command = IdlcLink_GeneralReply;
            reply.header.control_sequence = request.header.control_sequence_expected;
            reply.header.control_sequence_expected = _expected_sequence;

            if( error = processGeneralRequest(request, reply, logger) )
            {
                return "Error processing general request / " + error;
            }

            if( error = writeIdlcHeader(reply.header, reply_inserter) )
            {
                return "Error writing IDLC reply header / " + error;
            }

            bytes info_buf;
            byte_appender info_inserter(info_buf);

            if( error = writeReplyInfo(reply.info, info_inserter) )
            {
                return "Error writing IDLC reply info / " + error;
            }

            *reply_inserter++ = info_buf.size();
            copy(info_buf.begin(), info_buf.end(), reply_inserter);

            if( error = writeIdlcCrc(reply.message, reply_inserter) )
            {
                return "Error writing IDLC CRC / " + error;
            }

            return error_t::success;
        }

        case IdlcLink_ResetRequest:
        {
            reply.header.control_command = IdlcLink_ResetAcknowledge;

            _expected_sequence = 0;

            if( error = writeIdlcHeader(reply.header, reply_inserter) )
            {
                return "Error writing IDLC reply header / " + error;
            }
            if( error = writeIdlcCrc(reply.message, reply_inserter) )
            {
                return "Error writing IDLC CRC / " + error;
            }

            return error_t::success;
        }

        //  ignored, we won't send back a response
        case IdlcLink_BroadcastRequest:
        {
            return error_t::success;
        }

        case IdlcLink_RetransmitRequest:
        {
            //  we don't currently handle these, so we need to error out
            return "IDLC Link retransmit requests not implemented";
        }

        //  invalid for a slave to receive
        case IdlcLink_ResetAcknowledge:
        case IdlcLink_RejectWithRestart:
        default:
        {
            return "Invalid IDLC Link control command received";
        }
    }

    reply.description = describeGeneralReply(reply.info);
}


error_t Ccu711::processGeneralRequest(const idlc_request &request, idlc_reply &reply, Logger &logger)
{
    reply.info.command      = request.info.command;
    reply.info.reply_length = request.info.reply_length;

    switch( request.info.command )
    {
        default:
        {
            return error_t("Unhandled IDLC command", request.info.command);
        }
        case Command_Actin:
        case Command_WSets:
        {
            return error_t::success;
        }

        case Command_Xtime:
        {
            _timesynced_at = CtiTime::now();
            _timesynced_to = request.info.xtime.timesync;

            return error_t::success;
        }

        case Command_DTran:
        {
//  TODO-P3: if we're currently executing a queued entry, delay DTRAN until it's done

            //  Ccu710::processRequest() will add the DLC delays
            _ccu710.processRequest(request.info.dtran.request, reply.info.dtran.reply, logger);

            reply.info.dtran.message = reply.info.dtran.reply->message;

            return error_t::success;
        }

        case Command_LGrpQ:
        {
//  TODO-P2: return a REQACK if all 32 slots are full

            _queue.pending.insert(request.info.lgrpq.request_group.begin(),
                                  request.info.lgrpq.request_group.end());

            return error_t::success;
        }

        case Command_RColQ:
        {
            bool full = false;
            unsigned length_used = CCU_ReplyLength;

            while( !_queue.completed.empty() && !full )
            {
                const queue_entry &entry = *(_queue.completed.begin());

                //  Refer to Section 2 EMETCON Protocols, 4-86, pdf page 123
                unsigned entry_length = (entry.request.write)?(15):(16 + entry.result.data.size());

                if( (length_used + entry_length) <= (request.info.reply_length + CCU_ReplyLength) )
                {
                    _queue.returned.insert(entry);

                    reply.info.collected_queue_entries.push_back(entry);

                    _queue.completed.erase(_queue.completed.begin());

                    length_used += entry_length;
                }
                else
                {
                    full = true;
                }
            }

            return error_t::success;
        }
    }
}


string Ccu711::describeReply(const idlc_reply &reply) const
{
    ostringstream reply_description;

    reply_description << "Reply / address " << reply.header.address;

    reply_description << " / ";

    switch( reply.header.control_command )
    {
        case IdlcLink_GeneralReply:
        {
            reply_description << "general reply";
            reply_description << " / sequence " << reply.header.control_sequence;
            reply_description << " / expected " << reply.header.control_sequence_expected;
            //reply_description << " / " << describeGeneralReply(reply.info);
            reply_description << endl << describeGeneralReply(reply.info);
            break;
        }
        case IdlcLink_RejectWithRestart:
        {
            reply_description << "reject with restart";
            reply_description << " / expected " << reply.header.control_sequence_expected;
            break;
        }
        case IdlcLink_ResetAcknowledge:
        {
            reply_description << "reset acknowledge";
            break;
        }

        default:
        case IdlcLink_GeneralRequest:
        case IdlcLink_BroadcastRequest:
        case IdlcLink_RetransmitRequest:
        case IdlcLink_ResetRequest:
        {
            reply_description << "invalid reply (" << hex << setw(2) << setfill('0') << reply.header.control_command << ")";
            break;
        }
    }

    return reply_description.str();
}

string Ccu711::describeGeneralReply(const reply_info &info) const
{
    ostringstream info_description;

//  TODO-P3: Do we care about RCONT capability?

    switch( info.command )
    {
        case Command_Actin:  info_description << "actin";  break;
        case Command_WSets:  info_description << "wsets";  break;
        case Command_Xtime:  info_description << "xtime";  break;
        case Command_LGrpQ:  info_description << "lgrpq";  break;
        case Command_DTran:  info_description << "dtran";  break;
        case Command_RColQ:  info_description << "rcolq";  break;
    }

    info_description << describeStatuses(info.status);

    switch( info.command )
    {
        case Command_DTran:
        {
            bytes::const_iterator message_itr = info.dtran.message.begin();
            bytes::const_iterator message_end = info.dtran.message.end();

            info_description << endl << hex;

            int dFill = info_description.fill('0');

            while( message_itr != message_end )
            {
                info_description<< setw(2) << static_cast<int>(*message_itr++) << " ";
            }

            info_description.fill(dFill);

            info_description << dec << endl << info.dtran.reply->description;

            break;
        }
        case Command_RColQ:
        {
            info_description << "; " << info.collected_queue_entries.size() << " entries" << endl;

            vector<queue_entry>::const_iterator completed_itr = info.collected_queue_entries.begin(),
                                                  completed_end = info.collected_queue_entries.begin();

            for( ; completed_itr != completed_end; ++completed_itr )
            {
                int dFill = info_description.fill('0');

//  TODO-P2:  Change completion_status to output text instead of an integer
                info_description << setw(9) << completed_itr->entry_id << " ";
                info_description << completed_itr->result.completion_status << " at ";
                info_description << completed_itr->result.completion_time << ", ";

                if( !completed_itr->request.write )
                {
                    info_description << ", data: " << hex;

                    bytes::const_iterator data_itr = completed_itr->result.data.begin();
                    bytes::const_iterator data_end = completed_itr->result.data.end();

                    while( data_itr != data_end )
                    {
                        info_description << setw(2) << static_cast<int>(*data_itr++) << " ";
                    }

                    info_description << dec;
                }

                info_description.fill(dFill);
            }

            break;
        }
    }

    return info_description.str();
}

string Ccu711::describeStatuses(const status_info &statuses) const
{
//  TODO-P2: Add status description

    return "(status description)";
}


error_t Ccu711::sendReply(Comms &comms, const idlc_reply &reply, Logger &logger) const
{
    if( !comms.write(reply.message, logger) )
    {
        return "Error writing reply to comms";
    }

    return error_t::success;
}


error_t Ccu711::writeIdlcHeader(const idlc_header &header, byte_appender &out_itr) const
{
    *out_itr++ = Hdlc_FramingFlag;
    *out_itr++ = header.address << 1;

    switch( header.control_command )
    {
        case IdlcLink_GeneralReply:
        {
            //  assume final message
            unsigned char control = 0x10;  //  final ? 0x10 : 0x00;

            control |= (header.control_sequence          << 1) & 0x0e;
            control |= (header.control_sequence_expected << 5) & 0xe0;

            *out_itr++ = control;

            break;
        }

        case IdlcLink_RejectWithRestart:  *out_itr++ = HdlcLink_Rej | _expected_sequence << 5;  break;
        case IdlcLink_ResetAcknowledge:   *out_itr++ = HdlcLink_Ua;                             break;

        default:  return error_t("Invalid control command", header.control_command);
    }

    return error_t::success;
}


error_t Ccu711::writeReplyInfo(const reply_info &info, byte_appender &out_itr) const
{
    bytes status_buf;
    bytes data_buf;

    byte_appender status_out_itr(status_buf);

    error_t error;

    if( error = writeReplyStatus(info.status, status_out_itr) )
    {
        return "Error writing reply statuses / " + error;
    }

    switch( info.command )
    {
        case Command_DTran:
        {
            data_buf.assign(info.dtran.message.begin(),
                            info.dtran.message.end());

            break;
        }
        case Command_RColQ:
        {
            vector<queue_entry>::const_iterator completed_itr = info.collected_queue_entries.begin(),
                                                completed_end = info.collected_queue_entries.end();

            for( ; completed_itr != completed_end; ++completed_itr )
            {
                //  see Section 2 EMETCON Protocols, 4-86, pdf page 123

                bytes completed_entry_buf;

                //  QENID
                completed_entry_buf.push_back(completed_itr->entry_id >> 24);
                completed_entry_buf.push_back(completed_itr->entry_id >> 16);
                completed_entry_buf.push_back(completed_itr->entry_id >>  8);
                completed_entry_buf.push_back(completed_itr->entry_id >>  0);

                //  ENSTA
                /* Refer to Section 2 EMETCON Protocols, 4-86, pdf page 123 -
                   ENSTA field is 1 nibble in length, so the first 4 bits 
                   are the significant bits.                                */
                completed_entry_buf.push_back(0xF0);

                unsigned period = completed_itr->result.completion_time.date().weekDay() * 3 +
                                  completed_itr->result.completion_time.hour() / 8;

                unsigned within_period = completed_itr->result.completion_time.hour() * 3600 +
                                         completed_itr->result.completion_time.minute() * 60 +
                                         completed_itr->result.completion_time.second();

                //  STIME
                completed_entry_buf.push_back(period);
                completed_entry_buf.push_back(within_period >> 8);
                completed_entry_buf.push_back(within_period >> 0);

                //  ROUTE
                //  The cart CCU has a route of 255, so set the simulator
                //  route to 255 also to match.
                completed_entry_buf.push_back(0xFF);

                //  NFUNC
                completed_entry_buf.push_back(1);

                //  S1 - see Section 2 EMETCON Protocols, 4-71 to 4-72, pdf pages 106-107
                completed_entry_buf.push_back(completed_itr->result.completion_status << 6);
                completed_entry_buf.push_back(1);

                if( !completed_itr->request.write )
                {

                    //  L1
                    
                    completed_entry_buf.push_back(completed_itr->result.data.size());
                    
                    //  TS

                    //  b0 = D1.alarm
                    //  b1 = D1.powerfail
                    //  b2 = D2.spare
                    //  b3 = D2.timesync
                    //  b4 = D3.spare2
                    //  b5 = D3.spare1
                    //  b6 = E word occurred
                    //  b7 = last request timed out

                    completed_entry_buf.push_back(completed_itr->result.ts_values >> 8);
                    completed_entry_buf.push_back(completed_itr->result.ts_values);

                    //  D1
                    completed_entry_buf.insert(completed_entry_buf.end(),
                                               completed_itr->result.data.begin(),
                                               completed_itr->result.data.end());
                }

                data_buf.push_back(completed_entry_buf.size() + 1);
                data_buf.insert(data_buf.end(),
                                completed_entry_buf.begin(),
                                completed_entry_buf.end());
            }

            data_buf.push_back('\0');

            break;
        }
    }

    *out_itr++ = 0x00;  //  src/des
    *out_itr++ = 0x80 | info.command;

    copy(status_buf.begin(), status_buf.end(), out_itr);
    copy(data_buf.begin(),   data_buf.end(),   out_itr);

    unsigned base_len = data_buf.size();
    unsigned char badfood[4] = { 0xba, 0xad, 0xf0, 0x0d };

    while( base_len++ < info.reply_length )
    {
        *out_itr++ = badfood[base_len % 4];
    }

    return error_t::success;
}


Ccu711::status_info::status_info()
{
    stats.power  = false;
    stats.faultc = false;
    stats.deadmn = false;
    stats.coldst = false;
    stats.nsadj  = false;
    stats.algflt = false;
    stats.reqack = false;
    stats.broadc = false;

    stats.battry = false;
    stats.jouren = false;
    stats.jourov = false;
    stats.badtim = false;

    stats.alg_st[0] = stats_info::AlgSt_Inactive;
    stats.alg_st[1] = stats_info::AlgSt_EnabledAndHalted;
    stats.alg_st[2] = stats_info::AlgSt_Running;
    stats.alg_st[3] = stats_info::AlgSt_Inactive;

    stats.alg_st[4] = stats_info::AlgSt_EnabledAndHalted;
    stats.alg_st[5] = stats_info::AlgSt_EnabledAndHalted;
    stats.alg_st[6] = stats_info::AlgSt_Inactive;
    stats.alg_st[7] = stats_info::AlgSt_Inactive;

    statd.hdrmck = false;
    statd.battst = false;
    statd.dlcflt = false;

    statd.cplpw  = false;

    statd.readyn = 0;
    statd.ncsets = 0;
    statd.ncocts = 0;

    statp.dummy = false;

}


error_t Ccu711::writeReplyStatus(const status_info &status, byte_appender &out_itr) const
{
    {
        unsigned char byte = 0;

        if( status.stats.power  )  byte |= 1 << 0;
        if( status.stats.faultc )  byte |= 1 << 1;
        if( status.stats.deadmn )  byte |= 1 << 2;
        if( status.stats.coldst )  byte |= 1 << 3;
        if( status.stats.nsadj  )  byte |= 1 << 4;
        if( status.stats.algflt )  byte |= 1 << 5;
        if( status.stats.reqack )  byte |= 1 << 6;
        if( status.stats.broadc )  byte |= 1 << 7;

        *out_itr++ = byte;
    }

    {
        unsigned char byte = 0;

        if( status.stats.battry )  byte |= 1 << 0;
        if( status.stats.jouren )  byte |= 1 << 1;
        if( status.stats.jourov )  byte |= 1 << 2;
        if( status.stats.badtim )  byte |= 1 << 3;

        *out_itr++ = byte;
    }

    {
        unsigned char byte = 0;

        byte |= (status.stats.alg_st[0] & 3) << 6;
        byte |= (status.stats.alg_st[1] & 3) << 4;
        byte |= (status.stats.alg_st[2] & 3) << 2;
        byte |= (status.stats.alg_st[3] & 3) << 0;

        *out_itr++ = byte;
    }

    {
        unsigned char byte = 0;

        byte |= (status.stats.alg_st[4] & 3) << 6;
        byte |= (status.stats.alg_st[5] & 3) << 4;
        byte |= (status.stats.alg_st[6] & 3) << 2;
        byte |= (status.stats.alg_st[7] & 3) << 0;

        *out_itr++ = byte;
    }

    {
        unsigned char byte = 0;

        if( status.statd.hdrmck )  byte |= 1 << 4;
        if( status.statd.battst )  byte |= 1 << 6;
        if( status.statd.dlcflt )  byte |= 1 << 7;

        *out_itr++ = byte;
    }

    *out_itr++ = status.statd.cplpw ? 1 : 0;
    *out_itr++ = status.statd.readyn;
    *out_itr++ = status.statd.ncsets;

    *out_itr++ = (status.statd.ncocts >> 8) & 0xff;
    *out_itr++ = (status.statd.ncocts >> 0) & 0xff;

//  TODO-P3: do we want statp for the individual ddddd algorithms?
    *out_itr++ = 0x00;  //  StatP
    *out_itr++ = 0x00;  //  "  "

    return error_t::success;
}


error_t Ccu711::writeIdlcCrc(const bytes &message, byte_appender &out_itr) const
{
    //  this array is for the interface with NCrcCalc_C, which wants a raw buffer
    boost::scoped_array<unsigned char> buf(new unsigned char[message.size() - 1]);

    std::copy(message.begin() + 1, message.end(), buf.get());

    unsigned short crc = NCrcCalc_C(buf.get(), message.size() - 1);

    *out_itr++ = HIBYTE(crc);
    *out_itr++ = LOBYTE(crc);

    return error_t::success;
}

bool Ccu711::validateCommand(SocketComms &socket_interface)
{
    bytes peek_buf;
    socket_interface.peek(byte_appender(peek_buf), 6);
    unsigned char command = peek_buf[5];
    unsigned numEntries = 0, numBytes = 0;

    switch( command )
    {
        case Command_DTran:
            {
                return ( peek_buf[3] >= 3 );
            }
        case Command_Actin:
            {
                return ( peek_buf[3] >= 3 );
            }
        case Command_WSets:
            {
                return ( peek_buf[3] >= 3 );
            }
        case Command_LGrpQ:
            {
                return ( peek_buf[3] >= 3 );
            }
        case Command_RColQ:
            {
                return ( peek_buf[3] >= 3 );
            }
        case Command_Xtime:
            {
                return ( peek_buf[3] >= 3 );
            }
        default:
        {
            // Command is either non-existent or doesn't make sense.
            return false;
        }
    }
}


}
}

