#include "precompiled.h"

#include "Ccu710.h"
#include "Simulator.h"

using namespace std;

namespace Cti {
namespace Simulator {

Ccu710::Ccu710(int address, int strategy) :
    _address(address),
    _strategy(strategy),
    _ccu710InTag("CCU710(" + CtiNumStr(address) + ")-IN"),
    _ccu710OutTag("CCU710(" + CtiNumStr(address) + ")-OUT")
{
}

bool Ccu710::addressAvailable(Comms &comms)
{
    //  technically, the address could be in just the first byte, but all legal requests
    //    are at least 2 bytes long...  so this is a bit of a shortcut
    return comms.available(2);
}

error_t Ccu710::peekAddress(Comms &comms, unsigned &address)
{
    bytes address_buf;

    if( !comms.peek(back_inserter(address_buf), 3) )
    {
        return false;
    }

    return extractAddress(address_buf, address);
}

bool Ccu710::isExtendedAddress(unsigned char address_byte)
{
    return address_byte & 0x40;
}

bool parity(unsigned char x)
{
    x = x ^ (x >> 4);
    x = x ^ (x >> 2);
    x = x ^ (x >> 1);

    return (x & 1);
}

bool isEvenParity(unsigned char x)
{
    return !parity(x);
}

bool isEvenParity(const bytes &x)
{
    return find_if(x.begin(), x.end(), parity) == x.end();
}

unsigned char makeEvenParity(unsigned char x)
{
    unsigned char cropped = x & 0x7f;

    return parity(cropped)?(cropped | 0x80):(cropped);
}

error_t Ccu710::extractAddress(const bytes &address_buf, unsigned &address)
{
    if( address_buf.size() < 2)
    {
        return "Insufficient data for address extraction";
    }
    if( !isEvenParity(address_buf[0]) || !isEvenParity(address_buf[1]) )
    {
        return "Invalid parity in address extraction";
    }

    if( isExtendedAddress(address_buf[0]) )
    {
        address = address_buf[0] & 0x03;
        address |= (address_buf[1] >> 1) & 0x1c;
    }
    else
    {
        // These bits can never be set if the address isn't extended.
        if( address_buf[1] & 0x38 )
        {
            return "Invalid non-extended address data received.";
        }

        address = address_buf[0] & 0x03;
    }

    return error_t::success;
}

bool Ccu710::handleRequest(Comms &comms, Logger &logger)
{
    request_t ccu_request;
    reply_t   ccu_reply;

    error_t error;

    {
        ScopedLogger scope = logger.getNewScope(_ccu710InTag);

        if( error = readRequest(comms, ccu_request) )
        {
            scope.log("Error reading request / " + error, ccu_request.message);
            return false;
        }

        scope.log(ccu_request.description, ccu_request.message);

        if( error = processRequest(ccu_request, ccu_reply, scope) )
        {
            scope.log("Error processing request / " + error);
            return false;
        }

        if( ccu_reply.message.empty() )
        {
            scope.log("No reply");
            return true;
        }
    }

    {
        ScopedLogger scope = logger.getNewScope(_ccu710OutTag);

        if( error = sendReply(comms, ccu_reply, scope) )
        {
            scope.log("Error sending reply / " + error + "\n" + ccu_reply.description, ccu_reply.message);
            return false;
        }

        scope.log(ccu_reply.description, ccu_reply.message);
    }

    return true;
}


error_t Ccu710::readRequest(CommsIn &comms_in, request_holder &external_request_holder) const
{
    request_t new_request;

    error_t error = readRequest(comms_in, new_request);

    external_request_holder = request_holder(new_request);

    return error;
}


error_t Ccu710::readRequest(CommsIn &comms_in, request_t &request) const
{
    byte_appender request_data_oitr = byte_appender(request.message);

    error_t error;

    if( !comms_in.read(back_inserter(request.message), 2) )
    {
        return "Timeout reading address/operation";
    }
    if( error = extractAddress(request.message, request.address) )
    {
        return "Error extracting address / " + error;
    }

    if( request.message[0] & FunctionBit_FeederOperation )
    {
        request.function = Function_FeederOperation;

        if( request.message.size() < 3 && !comms_in.read(request_data_oitr, 3 - request.message.size()) )
        {
            return "Timeout reading address/operation";
        }
        if( !isEvenParity(request.message) )
        {
            return "Invalid parity in address/operation";
        }
        if( error = extractFeederOperation(request.message, request.feeder_operation) )
        {
            return "Error extracting feeder operation / " + error;
        }

        bytes feeder_word_buf;
        byte_appender feeder_word_oitr = byte_appender(feeder_word_buf);

        if( !comms_in.read(feeder_word_oitr, request.feeder_operation.length) )
        {
            return "Timeout reading feeder operation words";
        }
        if( !EmetconWord::restoreWords(feeder_word_buf, request.feeder_operation.words) )
        {
            return "Error extracting feeder operation words";
        }
    }
    else
    {
        const int operation = request.message[0] & FunctionBits_Mask;

        switch( operation )
        {
            default:
            {
                return error_t("Invalid operation", operation);
            }
            case FunctionBits_RetransmitToMaster:
            case FunctionBits_DownloadTime:
            case FunctionBits_ReadCcuIdentification:
            case FunctionBits_LineMonitor:
            case FunctionBits_RetransmitToRemote:
            {
                return error_t("Unsupported operation", operation);
            }

            case FunctionBits_Loopback:
            {
                request.function = Function_Loopback;

                if( request.message.size() < 3 && !comms_in.read(request_data_oitr, 3 - request.message.size()) )
                {
                    return "Timeout reading loopback operation";
                }
                if( !isEvenParity(request.message) )
                {
                    return "Invalid parity in loopback operation";
                }

                request.loopback_response.assign(request.message.begin() + 1, request.message.end());

                break;
            }
        }
    }

    request.description = describeRequest(request);

    return error_t::success;
}


error_t Ccu710::extractFeederOperation(const bytes &feeder_op_buf, feeder_operation_t &feeder_operation) const
{
    feeder_operation.bus            = feeder_op_buf[0] & 0x38 >> 3;
    feeder_operation.amp            = feeder_op_buf[0] & 0x40 >> 6;
    feeder_operation.repeater_count = feeder_op_buf[1] & 0x07;
    feeder_operation.length         = feeder_op_buf[2] & 0x3f;

    return error_t::success;
}


string Ccu710::describeRequest(const request_t &request) const
{
    ostringstream request_description;

    request_description << "Request / address " << request.address;

    request_description << " / ";

    switch( request.function )
    {
        case Function_FeederOperation:
        {
            request_description << "feeder operation";

            request_description << ", amp " << request.feeder_operation.amp;
            request_description << ", bus " << request.feeder_operation.bus;
            request_description << ", " << request.feeder_operation.repeater_count << " repeaters";

            words_t::const_iterator words_itr = request.feeder_operation.words.begin(),
                                    words_end = request.feeder_operation.words.end();

            if( words_itr == words_end )
            {
                request_description << ", ERROR - no words to send";
                break;
            }

            request_description << " / ";

            switch( (*words_itr)->type )
            {
                case EmetconWord::WordType_A:
                {
//  TODO-P2: add A word description
                    request_description << "A word";

                    break;
                }
                case EmetconWord::WordType_B:
                {
                    const EmetconWordB &b_word = *(boost::static_pointer_cast<const EmetconWordB>(*words_itr));

                    request_description << "B word";
                    request_description << ", ";

                    request_description << "addr " << setw(7) << b_word.dlc_address << ", ";

                    request_description << (b_word.function?'f':' ');
                    request_description << (b_word.write   ?'w':'r');
                    request_description << hex << setw(2) << setfill('0') << b_word.function_code << dec << ", ";

                    request_description << "fix " << b_word.repeater_fixed << ", ";
                    request_description << "var " << b_word.repeater_variable << ", ";
                    request_description << "words " << b_word.words_to_follow;

                    if( b_word.write )
                    {
                        while( ++words_itr != words_end )
                        {
                            if( (*words_itr)->type != EmetconWord::WordType_C )
                            {
                                request_description << "unhandled word (" << (*words_itr)->type << ")";

                                break;
                            }

                            const EmetconWordC &c_word = *(boost::static_pointer_cast<const EmetconWordC>(*words_itr));

                            int width = request_description.width(2);

                            request_description << "C word";

                            if( c_word.data.size() < EmetconWordC::PayloadLength )
                            {
                                request_description << " (partial)";
                            }

                            request_description << ", data: " << hex;

                            bytes::const_iterator data_itr = c_word.data.begin();
                            bytes::const_iterator data_end = c_word.data.end();

                            int dFill = request_description.fill('0');

                            while( data_itr != data_end )
                            {
                                request_description << setw(2) << static_cast<int>(*data_itr++) << " ";
                            }

                            request_description.fill(dFill);

                            request_description << dec;
                        }
                    }

                    break;
                }
                case EmetconWord::WordType_G:
                {
                    request_description << "G word";
                    break;
                }
                default:
                {
                    request_description << "unhandled word (" << (*words_itr)->type << ")";
                }
            }

            break;
        }
        case Function_Loopback:
        {
            request_description << "loopback";
            break;
        }
        default:
        {
            request_description << "unhandled function " << request.function;
            break;
        }
    }

    return request_description.str();
}


error_t Ccu710::processRequest(const request_holder &external_request_holder, reply_holder &external_reply_holder, Logger &logger)
{
    reply_t new_reply;

    error_t error = processRequest(*external_request_holder, new_reply, logger);

    external_reply_holder = reply_holder(new_reply);

    return error;
}


error_t Ccu710::processRequest(const request_t &request, reply_t &reply, Logger &logger)
{
    byte_appender reply_oitr(reply.message);

    switch( request.function )
    {
        case Function_FeederOperation:
        {
            unsigned words_expected;

            error_t error;

            //  kick everything back with a NAK-W
            if( false )
            {
                *reply_oitr++ = makeReplyControl(_address, ReplyControl_NoAcknowledgeWait);
                *reply_oitr++ = makeReplyControl(_address, ReplyControl_NoAcknowledgeWait);

                return error_t::success;
            }

            if( error = validateFeederOperation(request.feeder_operation, words_expected) )
            {
                //  not sure this is right, but it looks like what Porter is looking for (NackTst(), words.cpp)
                *reply_oitr++ = makeReplyControl(_address, ReplyControl_NoAcknowledgeSignal);
                *reply_oitr++ = makeReplyControl(_address, ReplyControl_NoAcknowledgeSignal);

                return error_t::success;
            }

            *reply_oitr++ = makeReplyControl(_address, ReplyControl_Acknowledge);
            *reply_oitr++ = makeReplyControl(_address, ReplyControl_Acknowledge);

            bytes request_buf;
            byte_appender request_oitr(request_buf);

            copy(request.feeder_operation.words.begin(),
                 request.feeder_operation.words.end(),
                 EmetconWord::serializer(request_oitr));

            Sleep(dlc_time(request_buf.size() * 8) * (request.feeder_operation.repeater_count + 1));

            if( !words_expected )
            {
                //  eventually pass bus and feeder information here as well - Grid needs to know!
                Grid.oneWayCommand(request_buf, logger);
            }
            else
            {
                bytes reply_buf;

                //  eventually pass bus and feeder information here as well - Grid needs to know!
                Grid.twoWayCommand(request_buf, reply_buf, logger);

//  TODO-P4: We will actually be able to use a common transmit portion, and this "listen" portion will be optional

//  TODO-P2: Add check to verify we got back the correct number of words
//  TODO-P2: Change to words-expected to determine the delay, not the size of the reply from Grid.

                Sleep(dlc_time(reply_buf.size() * 8) * (request.feeder_operation.repeater_count + 1));

                *reply_oitr++ = makeReplyControl(_address, ReplyControl_StartOfDlcReply);

//  TODO-P2: Add NACK processing for partial returns (and crosstalk)
//          Basically, we'd need to try to decode the words to see where they failed...
//          The NACK padding is a CCU function, so it will need to be applied by the CCU,
//          not the Emetcon words

                words_t reply_words;

                //  For now, this assumes that we're getting whole words (no errors) back from Grid
                if( !EmetconWord::restoreWords(reply_buf, reply_words) )
                {
                    return "Error restoring reply words";
                }

                words_t::const_iterator words_itr = reply_words.begin(),
                                        words_end = reply_words.end();

                for( ; words_itr != words_end; ++words_itr )
                {
                    (*words_itr)->serialize(reply_oitr);

                    *reply_oitr++ = makeReplyControl(_address, ReplyControl_Acknowledge);
                }
            }

            return error_t::success;
        }
        case Function_Loopback:
        {
            //  first, the ack
            reply.message.push_back(makeReplyControl(_address, ReplyControl_Acknowledge));
            reply.message.push_back(makeReplyControl(_address, ReplyControl_Acknowledge));

            //  then the echo characters
            reply.message.insert(reply.message.end(),
                                 request.loopback_response.begin(),
                                 request.loopback_response.end());

            return error_t::success;
        }
        default:
        {
            return error_t("Unsupported command", request.function);
        }
    }
}


error_t Ccu710::validateFeederOperation(const feeder_operation_t &feeder_operation, unsigned &words_expected) const
{
    words_expected = 0;

    if( feeder_operation.words.empty() )
    {
        return "No words for feeder operation";
    }

    //  from Section 2 EMETCON Protocols, page 3-4
    switch( feeder_operation.length )
    {
        default:
        {
            return error_t("Invalid request length in feeder operation", feeder_operation.length);
        }

        case 1:
        case 8:
        case 16:
        case 24:
        case 32:
        {
            return "G words not supported";
        }
        case 4:   //  A word
        {
            if( !feeder_operation.words[0] )
            {
                return error_t("First word not valid");
            }
            if( feeder_operation.words[0]->type != EmetconWord::WordType_A )
            {
                return error_t("First word not A word").neq(feeder_operation.words[0]->type, EmetconWord::WordType_A);
            }
            if( feeder_operation.words.size() != 1 )
            {
                return error_t("Incorrect word count for A word shed").neq(feeder_operation.words.size(), 1);
            }

            return error_t::success;
        }
        case 7:   //  B word
        case 14:  //  B + 1C words
        case 21:  //  B + 2C words
        case 28:  //  B + 3C words
        {
//  TODO-P2: Combine this code with similar code in Mct410::read()/write()?
//             Does it belong in EmetconWord, or do we need an Emetcon class that knows how words go together (including D1-D3 words)?
//             Seems similar logic can/should be owned by both CCUs and MCTs.

            if( !feeder_operation.words[0] )
            {
                return error_t("First word not valid");
            }
            if( feeder_operation.words[0]->type != EmetconWord::WordType_B )
            {
                return error_t("First word not B word").neq(feeder_operation.words[0]->type, EmetconWord::WordType_B);
            }

            boost::shared_ptr<const EmetconWordB> b_word = boost::static_pointer_cast<const EmetconWordB>(feeder_operation.words[0]);

            if( b_word->write )
            {
                if( (b_word->words_to_follow + 1) != feeder_operation.words.size() )
                {
                    return error_t("Incorrect word count for write").neq(b_word->words_to_follow + 1, feeder_operation.words.size());
                }

                for( unsigned index = 1; index <= b_word->words_to_follow; ++index )
                {
                    if( !feeder_operation.words[index] || feeder_operation.words[index]->type != EmetconWord::WordType_C )
                    {
                        return error_t("Invalid data word at index", index);
                    }
                }

                if( feeder_operation.length != (EmetconWordB::Length + EmetconWordC::Length * b_word->words_to_follow) )
                {
                    return error_t("Incorrect length for write").neq(feeder_operation.length, EmetconWordB::Length + EmetconWordC::Length * b_word->words_to_follow);
                }
            }
            else
            {
                words_expected = b_word->words_to_follow;

                if( feeder_operation.length != EmetconWordB::Length )
                {
                    return error_t("Incorrect length for read").neq(feeder_operation.length, EmetconWordB::Length);
                }
            }

            return error_t::success;
        }
    }
}


string Ccu710::describeReply(const reply_t &reply) const
{
//  TODO-P2: add CCU-710 reply description
    return "CCU710 reply";
}


error_t Ccu710::sendReply(CommsOut &comms_out, const reply_t &reply, Logger &logger) const
{
    if( !comms_out.write(reply.message, logger) )
    {
        return error_t("Error writing reply to comms");
    }

    return error_t::success;
}


unsigned char Ccu710::makeReplyControl(unsigned address, ReplyControls reply_control)
{
    switch( reply_control )
    {
        case ReplyControl_Acknowledge:             return makeEvenParity(ReplyCharacter_Ack  | (address & 0x03));
        case ReplyControl_NoAcknowledgeSignal:     return makeEvenParity(ReplyCharacter_NakS | (address & 0x03));
        case ReplyControl_NoAcknowledgeBch:        return makeEvenParity(ReplyCharacter_NakB | (address & 0x03));
        case ReplyControl_NoAcknowledgeDropout:    return makeEvenParity(ReplyCharacter_NakD | (address & 0x03));
        case ReplyControl_NoAcknowledgeWait:       return makeEvenParity(ReplyCharacter_NakW | (address & 0x03));
        case ReplyControl_StartOfDlcReply:         return makeEvenParity(ReplyCharacter_Stx);
        default:  return 0xff;
    }
}

}
}

