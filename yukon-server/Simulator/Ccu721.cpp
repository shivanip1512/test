#include "precompiled.h"

#include "Ccu721.h"

#include "Grid.h"

#include "cti_asmc.h"

using namespace std;

namespace Cti {
namespace Simulator {

Ccu721::Ccu721(unsigned char address, int strategy) :
    _address(address),
    _strategy(strategy),
    _expected_sequence(0),
    _next_seq(1),
    _bufferFrozen(false),
    _ccu721InTag("CCU721(" + CtiNumStr(address) + ")-IN"),
    _ccu721OutTag("CCU721(" + CtiNumStr(address) + ")-OUT")
{
}

bool Ccu721::handleRequest(Comms &comms, Logger &logger)
{
    idlc_request request;
    idlc_reply   reply;

    error_t error;

    {
        ScopedLogger scope = logger.getNewScope(_ccu721InTag);

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
        ScopedLogger scope = logger.getNewScope(_ccu721OutTag);

        scope.log(describeReply(reply), reply.message);

        if( error = sendReply(comms, reply, scope) )
        {
            scope.log("Error sending reply / " + error);
            return false;
        }
    }

    return true;
}

error_t Ccu721::readRequest(Comms &comms, idlc_request &request) const
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

error_t Ccu721::extractRequestInfo(const bytes &message, request_info &info) const
{
    const int index_length  = 3,
              index_command = 4;

    if( message.size() < Idlc_HeaderLength + Info_HeaderLength ||
        message.size() < Idlc_HeaderLength + message[index_length] )
    {
        return "Request info too short";
    }

    //  trim off the length and command bytes
    bytes command_data(message.begin() + Idlc_HeaderLength + Info_HeaderLength, message.end());

    switch( message[index_command] )
    {
        case Klondike_Dtran:
        {
            info.command = Klondike_Dtran;
            return extractRequestInfo_Dtran(command_data, info);
        }
        case Klondike_CheckStatus:
        {
            info.command = Klondike_CheckStatus;
            return error_t::success;
        }
        case Klondike_ClearBuffers:
        {
            info.command = Klondike_ClearBuffers;
            return extractRequestInfo_ClrBuffers(command_data, info);
        }
        case Klondike_LoadBuffer:
        {
            info.command = Klondike_LoadBuffer;
            return extractRequestInfo_LoadBuffer(command_data, info);
        }
        case Klondike_FreezeBuffer:
        {
            info.command = Klondike_FreezeBuffer;
            return error_t::success;
        }
        case Klondike_ThawBuffer:
        {
            info.command = Klondike_ThawBuffer;
            return error_t::success;
        }
        case Klondike_SpyBuffer:
        {
            info.command = Klondike_SpyBuffer;
            return error_t::success;
        }
        case Klondike_ReadBuffer:
        {
            info.command = Klondike_ReadBuffer;
            return extractRequestInfo_ReadBuffer(command_data, info);
        }
        case Klondike_TimeSync:
        {
            info.command = Klondike_TimeSync;
            return extractRequestInfo_TimeSync(command_data, info);
        }
        case Klondike_WriteRouteTable:
        {
            info.command = Klondike_WriteRouteTable;
            return extractRequestInfo_WriteRtTable(command_data, info);
        }
        case Klondike_ReadRouteTable:
        {
            info.command = Klondike_ReadRouteTable;
            return error_t::success;
        }
        case Klondike_ReadRouteTableOpen:
        {
            info.command = Klondike_ReadRouteTableOpen;
            return error_t::success;
        }
        case Klondike_ClearRouteTable:
        {
            info.command = Klondike_ClearRouteTable;
            return extractRequestInfo_ClrRtTable(command_data, info);
        }
        case Klondike_ReadMemory:
        {
            info.command = Klondike_ReadMemory;
            return extractRequestInfo_ReadMem(command_data, info);
        }
        case Klondike_WriteMemory:
        {
            info.command = Klondike_WriteMemory;
            return extractRequestInfo_WriteMem(command_data, info);
        }
        default:
        {
            //  we'll just create an empty generic reply, so this is still a success
            return error_t::success;
        }
    }
}

error_t Ccu721::extractRequestInfo_Dtran(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in Dtran command!";
    }

    int index = 0;

    info.dtran.message.assign(command_data.begin(), command_data.end());

    queue_entry::request_info &request = info.dtran.bookkeeperEntry.request;

    info.dtran.bookkeeperEntry.sequence = command_data[index] | command_data[index + 1] << 8;
    info.dtran.bookkeeperEntry.request.bus = command_data[index + 2] & 0x07;
    info.dtran.bookkeeperEntry.request.broadcast = command_data[index + 2] & 0x10;
    info.dtran.bookkeeperEntry.request.dlcType = command_data[index + 2] & 0xe0;
    info.dtran.bookkeeperEntry.request.stagesToFollow = command_data[index + 3];
    info.dtran.bookkeeperEntry.request.length = command_data[index + 4];

    switch( command_data[index + 5] & 0xf0 )
    {
        default:
            request.word_type = EmetconWord::WordType_Invalid;
            break;
        case 0x80:
        case 0x90:
            request.word_type = EmetconWord::WordType_A;
            // Process the command_data to get the information
            // to put into the A word.
            // There is nothing in the data structures that contains
            // a location for the A word. What to do here?
            break;
        case 0xa0:
        case 0xb0:
            request.word_type = EmetconWord::WordType_B;

            words_t b_word_holder;  // Something to hold the B Word

            // Grab the 7 bytes of data we need to create the B Word
            bytes b_word_buf = bytes(command_data.begin() + (index + 5), command_data.begin() + (index + 12));

            // Create it and store it in the holder.
            EmetconWord::restoreWords(b_word_buf,b_word_holder);

            const EmetconWordB &bWord = *boost::static_pointer_cast<const EmetconWordB>(b_word_holder[0]);

            request.function =   command_data[index + 10] & 0x08;
            request.write    = !(command_data[index + 10] & 0x04);

            request.b_word = bWord;

            break;
    }

    if( request.write && request.b_word.words_to_follow )
    {
        // There are C words following the B word.
        request.data.assign(command_data.begin() + 12 + index, command_data.end() );

        if( request.data.size() % CWordBytesLength != 0 )
        {
            return error_t("Invalid amount of data following B word", request.data.size());
        }

        // This is guaranteed to happen since the words_to_follow value wasn't 0.
        bytes data(request.data.begin(), request.data.begin() + CWordBytesLength);
        words_t words;
        EmetconWord::restoreWords(data,words);
        const EmetconWordC &cword = *boost::static_pointer_cast<const EmetconWordC>(words[0]);
        request.c_words.push_back(cword);

        if( request.b_word.words_to_follow > 1 )
        {
            // There is a second C word.
            bytes data(request.data.begin() + CWordBytesLength, request.data.begin() + (2 * CWordBytesLength));
            words_t words;
            EmetconWord::restoreWords(data,words);
            const EmetconWordC &cword = *boost::static_pointer_cast<const EmetconWordC>(words[0]);
            request.c_words.push_back(cword);
        }
        if( request.b_word.words_to_follow > 2 )
        {
            bytes data(request.data.begin() + (2 * CWordBytesLength), request.data.begin() + (3 * CWordBytesLength));
            words_t words;
            EmetconWord::restoreWords(data,words);
            const EmetconWordC &cword = *boost::static_pointer_cast<const EmetconWordC>(words[0]);
            request.c_words.push_back(cword);
        }
    }

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_ReadBuffer(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in Read Buffer command!";
    }
    info.readBuffer.flags = command_data[0];

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_ClrBuffers(const bytes &command_data, request_info &info) const
{
    if( !_bufferFrozen )
    {
        return "Buffer is not frozen! Send the FreezeBuffer request before attempting to clear buffers!";
    }
    if( command_data.empty() )
    {
        return error_t("No data in clear buffer request!", command_data.size());
    }

    int index = 0, blocksToErase, seq;

    if( command_data.size() == 1 )
    {
        // Only one byte of data signifies that the request was intended to clear
        // the entire buffer. If command_data[0] is 0x00, this is the case, check
        // to be sure.
        if( command_data[index] == 0x00 )
        {
            // clear all the blocks!
            info.clearBuffer.clearAll = true;
        }
        else
        {
            // The data sent wasn't 0x00. Should we still delete all data?
            // What to do here? For now, return an error.
            return error_t("Data in clear buffer request is inconsistent with command's format.", command_data[index]);
        }
    }
    else
    {
        // The request has size greater than one. command_data contains the number
        // of blocks to erase and the sequence number of each block to be erased.
        if( command_data.size() % 2 != 1 )
        {
            /*  There should be an odd number of bytes in the command_data, one for the
                number of blocks to erase and two each for the sequences to be erased.
                If there is an even number of bytes, this pattern doesn't hold and the
                request data is corrupt or incorrect: return an error.                  */
            return error_t("Invalid number of bytes in clear buffer request data!", command_data.size());
        }
        blocksToErase = command_data[index];
        if( blocksToErase != ((command_data.size() - 1) / 2))
        {
            /*  First byte of command_data contains the number of blocks the request
                has specified to erase. The data remaining is in two-byte pairs
                specifying each sequence needing to be erased. If the number of blocks
                to erase doesn't match this number of two-byte sequence number pairs,
                there is something wrong with the data, and we need to return an error! */
            return error_t("Blocks to erase is not consistant with clear buffer request data!", command_data.size());
        }
        index++;
        while ( blocksToErase > 0 )
        {
            seq = int(command_data[index]) | int(command_data[index + 1] << 8);

            // seq contains the sequence number of the queue block to be erased.
            info.clearBuffer.sequences.push_back(seq);

            // index needs to be incremented by two!
            index = index + 2;

            // Decrement how many blocks remain to be erased.
            blocksToErase--;
        }
    }

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_TimeSync(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in time sync request.";
    }

    int index = 0;

    // What happens with this...?
    unsigned utcSeconds = command_data[index] << 24 | command_data[index + 1] << 16
                        | command_data[index + 2] << 8 | command_data[index + 3];

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_LoadBuffer(const bytes &command_data, request_info &info) const
{
    if ( command_data.empty() )
    {
        return error_t("No data in load buffer request!", command_data.size());
    }
    int index = 0, startingSequence, numQueueEntries;
    if( command_data.size() >= 3 )
    {
        startingSequence = int(command_data[index]) | int(command_data[index + 1] << 8);
        numQueueEntries = command_data[index + 2];
        index = index + 3;
    }
    else
    {
        return error_t("Missing data in the Load Buffer request!", command_data.size());
    }
    int nextSequence = startingSequence;

    error_t error;

    while( numQueueEntries > 0 )
    {
        queue_entry entry;
        if( error = extractQueueEntry(command_data, index, nextSequence, entry))
        {
            return "Broken queue entry in load buffer decode " + error;
        }

        info.loadBuffer.request_group.push_back(entry);

        // One queue entry has been used up.
        numQueueEntries--;

        // Is this what we want to do?
        // CCU 721 SSPEC Page 22 indicates that all additional commands will increment
        // up from the starting sequence number.
        nextSequence++;

        index += entry.request.length;  // This too?
    }

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_WriteRtTable(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in Write Route Table request.";
    }

    int index = 0;

    unsigned int numEntries = command_data[index++];

    if( numEntries > 32 )
    {
        return error_t("There are only 32 available entries in the route table.", numEntries);
    }

    routeTable_entry entry;

    while( numEntries > 0 )
    {
        // First byte contains the route number
        entry.routeNumber    = command_data[index++] & 0x1f;

        if( entry.routeNumber > 31 )
        {
            return error_t("Route number value is out of the range of valid route numbers.", entry.routeNumber);
        }

        // Second byte contains the fixed and variable bits of repeater code.
        entry.fixedBits      = command_data[index] & 0xf8;
        entry.variableBits   = command_data[index++] & 0x07;

        // Third byte contains the stages to follow and bus coupling.
        entry.stagesToFollow = command_data[index] & 0x38;
        entry.busCoupling    = command_data[index++] & 0x07;

        // Put the entry somewhere. Into a vector in the request_info struct?
        info.routeTable.write_requests.push_back(entry);

        numEntries--;
    }

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_ClrRtTable(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in the clear routing table command.";
    }

    int index = 0, numRoutes;

    numRoutes = command_data[index];

    if( numRoutes > 32 )
    {
        return error_t("There are only 32 entries in the route table.", numRoutes);
    }

    if( !numRoutes )
    {
        // All routes will be cleared if routesToClear is zero.
        info.routeTable.clearAllRoutes = true;
    }
    else
    {
        int routeNumber;

        while( numRoutes > 0 )
        {
            routeNumber = command_data[index++] & 0x1f;
            info.routeTable.routesToClear.push_back(routeNumber);
            numRoutes--;
        }
    }

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_ReadMem(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in the read memory request.";
    }

    int index = 0, memType, numBytes;

    info.memoryAccess.memoryRead = true;
    memType = command_data[index];

    if( memType > 1 )
    {
        return error_t("Invalid memory type for read memory command.", memType);
    }
    info.memoryAccess.type = memType;
    info.memoryAccess.startAddress = (command_data[index + 1] << 8) | command_data[index + 2];

    numBytes = command_data[index + 3];

    if( numBytes > 246 )
    {
        // To fit into the 256 byte response, the amount of memory read can be a maximum of 246 bytes.
        return error_t("Requested amount of memory to read is too large.", numBytes);
    }
    info.memoryAccess.numBytes = command_data[index + 3];

    return error_t::success;
}

error_t Ccu721::extractRequestInfo_WriteMem(const bytes &command_data, request_info &info) const
{
    if( command_data.empty() )
    {
        return "No data in write memory request";
    }

    int index = 0, memType;

    memType = command_data[index];
    if( memType )
    {
        return error_t("Invalid memory type specified in write memory request.", memType);
    }

    info.memoryAccess.memoryRead = false;
    info.memoryAccess.type = memType;
    info.memoryAccess.startAddress = (command_data[index + 1] << 8) | command_data[index + 2];
    info.memoryAccess.numBytes = command_data[index + 3];

    info.memoryAccess.data.assign(command_data.begin() + 4, command_data.end());

    if( info.memoryAccess.data.size() != info.memoryAccess.numBytes )
    {
        return "Amount of data to write does not match specified size.";
    }

    return error_t::success;
}

error_t Ccu721::extractQueueEntry(const bytes &command_data, int index, int nextSequence, queue_entry &entry) const
{
    entry.sequence = nextSequence;
    entry.priority = command_data[index];

    {
        queue_entry::request_info &request = entry.request;

        request.bus = command_data[index + 1] & 0x07;
        request.broadcast = command_data[index + 1] & 0x10;
        request.dlcType = command_data[index + 1] & 0xe0;

        request.stagesToFollow  = command_data[index + 2];
        entry.dlc_length        = command_data[index + 3];

        request.length = entry.dlc_length+4;        // Request length is the length of the dlc header and data.

        switch( command_data[index + 4] & 0xf0 )
        {
            default:
                request.word_type = EmetconWord::WordType_Invalid;
                break;
            case 0x80:
            case 0x90:
                request.word_type = EmetconWord::WordType_A;
                // Process the command_data to get the information
                // to put into the A word.
                // There is nothing in the data structures that contains
                // a location for the A word. What to do here?
                break;
            case 0xa0:
            case 0xb0:
                request.word_type = EmetconWord::WordType_B;

                words_t b_word_holder;  // Something to hold the B Word

                // Grab the 7 bytes of data we need to create the B Word
                bytes b_word_buf = bytes(command_data.begin() + (index + 4), command_data.begin() + (index + 11));

                // Create it and store it in the holder.
                EmetconWord::restoreWords( b_word_buf, b_word_holder );

                const EmetconWordB &bWord = *boost::static_pointer_cast<const EmetconWordB>(b_word_holder[0]);

                request.function =   command_data[index + 9] & 0x08;
                request.write    = !(command_data[index + 9] & 0x04);

                request.b_word = bWord;

                break;
        }

        if( request.b_word.write && request.b_word.words_to_follow )
        {
            // There are C words following the B word.
            request.data.assign(command_data.begin() + 11 + index, command_data.end() );

            if( request.data.size() % CWordBytesLength != 0 )
            {
                return error_t("Invalid amount of data following B word", request.data.size());
            }

            // This is guaranteed to happen since the words_to_follow value wasn't 0.
            bytes data(request.data.begin(), request.data.begin() + CWordBytesLength);
            words_t words;
            EmetconWord::restoreWords(data,words);
            const EmetconWordC &cword = *boost::static_pointer_cast<const EmetconWordC>(words[0]);
            request.c_words.push_back(cword);

            if( request.b_word.words_to_follow > 1 )
            {
                // There is a second C word.
                bytes data(request.data.begin() + CWordBytesLength, request.data.begin() + (2 * CWordBytesLength));
                words_t words;
                EmetconWord::restoreWords(data,words);
                const EmetconWordC &cword = *boost::static_pointer_cast<const EmetconWordC>(words[0]);
                request.c_words.push_back(cword);
            }
            if( request.b_word.words_to_follow > 2 )
            {
                bytes data(request.data.begin() + (2 * CWordBytesLength), request.data.begin() + (3 * CWordBytesLength));
                words_t words;
                EmetconWord::restoreWords(data,words);
                const EmetconWordC &cword = *boost::static_pointer_cast<const EmetconWordC>(words[0]);
                request.c_words.push_back(cword);
            }
        }
    }

    return error_t::success;
}

error_t Ccu721::extractIdlcHeader(const bytes &message, idlc_header &header) const
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
        else if( (control_octet & HdlcLink_RejectMask) == HdlcLink_Rej )
        {
            header.control_command = IdlcLink_RejectWithRestart;
            header.control_sequence_expected = control_octet >> 5;
        }
        else if( (control_octet & HdlcLink_RejectMask) == HdlcLink_Srej )
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

error_t Ccu721::processRequest(const idlc_request &request, idlc_reply &reply, Logger &logger)
{
    error_t error;

    back_insert_iterator<bytes> reply_inserter(reply.message);

    reply.header.address = request.header.address;

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

            if( error = updateStatusBytes() )
            {
                return "Error updating status bytes data / " + error;
            }

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

error_t Ccu721::processGeneralRequest(const idlc_request &request, idlc_reply &reply, Logger &logger)
{
    reply.info.command = request.info.command;

    switch( request.info.command )
    {
        default:
        {
            return error_t("Unhandled Klondike command", request.info.command);
        }
        case Klondike_Dtran:
        {
            _next_seq++;
            return processDtranRequest(request, reply, logger);
        }
        case Klondike_SpyBuffer:
        case Klondike_CheckStatus:
        {
            return error_t::success;
        }
        case Klondike_ClearBuffers:
        {
            if( request.info.clearBuffer.clearAll )
            {
                // Clear the request group queue as well?
                //      request.loadBuffer.request_group.clear();
                _queue.pending.clear();
            }
            else
            {
                queue_info::pending_set::const_iterator pending_itr, pending_end;

                std::vector<int>::const_iterator sequence_itr = request.info.clearBuffer.sequences.begin(),
                                                 sequence_end = request.info.clearBuffer.sequences.end();

                unsigned int sequence, target;

                while( sequence_itr != sequence_end )
                {
                    // Put the iterator at the beginning of the pending queue.
                    pending_itr = _queue.pending.begin();
                    pending_end = _queue.pending.end();

                    // sequence holds the first sequence number to be removed.
                    // target holds the sequence number of the entry in the pending queue we are currently looking at.
                    sequence = *sequence_itr, target = (*pending_itr).sequence;

                    // Check to see if we have the one we're looking for.
                    // If not, check the next entry in the pending queue until we find it.
                    while( pending_itr != pending_end && target != sequence )
                    {
                        pending_itr++;
                    }

                    if( pending_itr == pending_end )
                    {
                        // We didn't find it, print a message and move on.
                        CTILOG_WARN(dout, "Message with sequence "<< sequence <<" not found in pending queue.");
                    }
                    else
                    {
                        // We found it, delete it.
                        _queue.pending.erase(pending_itr);
                    }

                    sequence_itr++;
                }
            }
        }
        case Klondike_LoadBuffer:
        {
            // The pending queue can only hold 8 messages at a time. Ensure that
            // it doesn't exceed this threshold.

            //unsigned available = LoadBufferMaxSlots - _queue.pending.size();
            unsigned pre_pending = _queue.pending.size();

            _queue.pending.insert(request.info.loadBuffer.request_group.begin(),
                                  request.info.loadBuffer.request_group.begin()
                                  + min(request.info.loadBuffer.request_group.size(), (LoadBufferMaxSlots - pre_pending)));

            reply.info.loadBuffer.available = LoadBufferMaxSlots - _queue.pending.size();
            //reply.info.loadBuffer.accepted  = _queue.pending.size() + LoadBufferMaxSlots - available;
            reply.info.loadBuffer.accepted  = _queue.pending.size() - pre_pending;

            _next_seq += reply.info.loadBuffer.accepted;

            return error_t::success;
        }
        case Klondike_FreezeBuffer:
        {
            _bufferFrozen = true;

            return error_t::success;
        }
        case Klondike_ThawBuffer:
        {
            _bufferFrozen = false;

            return error_t::success;
        }
        case Klondike_ReadBuffer:
        {
            // Read the entries from the completed queue and put them into the collected queue.
            reply.info.readBuffer.flags = request.info.readBuffer.flags;

            int bufferCount = LoadBufferMaxSlots;

            while( !_queue.completed.empty() && bufferCount )
            {
                const queue_entry &entry = *(_queue.completed.begin());

                _queue.returned.insert(entry);

                reply.info.collected_queue_entries.push_back(entry);

                _queue.completed.erase(_queue.completed.begin());

                bufferCount--;
            }

            return error_t::success;
        }
        case Klondike_TimeSync:
        {
            // Is this what we want to happen here?
            _timesynced_at = CtiTime::now();
            _timesynced_to = request.info.xtime.timesync;

            return error_t::success;
        }
        case Klondike_WriteRouteTable:
        {
            // Pretend to write the requested data to the routing table.
            // Ack without data is all that is required for now.
            return error_t::success;
        }
        case Klondike_ReadRouteTable:
        {
            // No routing table right now. What to return?
            return error_t::success;
        }
        case Klondike_ReadRouteTableOpen:
        {
            // This will go over the routing table and determine which routes are open.
            // For now there is no routing table.
            return error_t::success;
        }
        case Klondike_ClearRouteTable:
        {
            return error_t::success;
        }
        case Klondike_ReadMemory:
        {
            // Pretend to read the specified data from the memory.
            // The data to be returned will just be packed with 0s.
            reply.info.memoryAccess.type = request.info.memoryAccess.type;
            reply.info.memoryAccess.startAddress = request.info.memoryAccess.startAddress;

            for( int i = 0; i < request.info.memoryAccess.numBytes; i++ )
            {
                reply.info.memoryAccess.data.push_back(0);
            }

            return error_t::success;
        }
        case Klondike_WriteMemory:
        {
            // Pretend to write to memory.
            return error_t::success;
        }
    }
}

error_t Ccu721::processDtranRequest(const idlc_request &request, idlc_reply &reply, Logger &logger)
{
    error_t error;

    bytes request_buf;
    byte_appender request_oitr(request_buf);

    request.info.dtran.bookkeeperEntry.request.b_word.serialize(request_oitr);
    for each ( const EmetconWordC &cword in request.info.dtran.bookkeeperEntry.request.c_words )
    {
        cword.serialize(request_oitr);
    }

    reply.info.dtran.sequence = request.info.dtran.bookkeeperEntry.sequence;

    Sleep(dlc_time(request_buf.size() * 8) * (request.info.dtran.bookkeeperEntry.request.stagesToFollow + 1));

    if( request.info.dtran.bookkeeperEntry.request.write )
    {
        Grid.oneWayCommand(request_buf, logger);
    }
    else
    {
        bytes reply_buf;
        byte_appender reply_oitr(reply_buf);

        Grid.twoWayCommand(request_buf, reply_buf, logger);

        size_t wordsExpected = request.info.dtran.bookkeeperEntry.request.b_word.words_to_follow;

        Sleep(dlc_time(wordsExpected * 8) * (request.info.dtran.bookkeeperEntry.request.stagesToFollow + 1));

        words_t reply_words;

        //  For now, this assumes that we're getting whole words (no errors) back from Grid
        EmetconWord::restoreWords(reply_buf, reply_words);

        reply_buf.clear();

        for( const auto &word : reply_words )
        {
            word->serialize(reply_oitr);
            --wordsExpected;
        }

        if( wordsExpected )
        {
            EmetconWordE eWord;

            eWord.errors.incoming_no_response = true;

            while( wordsExpected-- > 0 )
            {
                eWord.serialize(reply_oitr);
            }
        }

        reply.info.dtran.message.assign(reply_buf.begin(), reply_buf.end());
    }

    return error_t::success;
}

void Ccu721::processQueue(Logger &logger)
{
    CtiTime now;

    queue_info::pending_set::const_iterator pending_itr = _queue.pending.begin(),
                                            pending_end = _queue.pending.end();

    while( pending_itr != pending_end && _queue.last_transmit < now && _queue.completed.size() < LoadBufferMaxSlots )
    {
        queue_entry entry = *pending_itr;

        _queue.pending.erase(pending_itr++);

        _queue.last_transmit += queue_request_dlc_time(entry.request);

        bytes request_buf;

        entry.request.b_word.serialize(byte_appender(request_buf));

        if( entry.request.write )
        {
            byte_appender request_appender { request_buf };

            for( const auto& word : entry.request.c_words )
            {
                word.serialize(request_appender);
            }

            Grid.oneWayCommand(request_buf, logger);
        }
        else
        {
            Grid.twoWayCommand(request_buf, entry.result.data, logger);

//  TODO-P3: return more interesting errors if we get crosstalk, etc
//           also, this expects full words back from Grid - it won't handle partial responses yet

            EmetconWord::restoreWords(entry.result.data, entry.result.as_words);

            size_t wordsExpected = entry.request.b_word.words_to_follow;

            //  Append E words for any missing reply words
            if( wordsExpected > entry.result.as_words.size() )
            {
                wordsExpected -= entry.result.as_words.size();

                byte_appender reply_oitr(entry.result.data);

                EmetconWordE eWord;

                eWord.errors.incoming_no_response = true;

                while( wordsExpected-- )
                {
                    eWord.serialize(reply_oitr);
                }
            }
        }

        entry.result.completion_time = _queue.last_transmit;

        _queue.completed.insert(entry);
    }
}

unsigned Ccu721::queue_request_dlc_time(const queue_entry::request_info &request)
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

    return ( dlc_time(bits_out, bits_in) * (request.stagesToFollow + 1) + 999 )/ 1000;
}

string Ccu721::describeRequest(const idlc_request &request) const
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

string Ccu721::describeGeneralRequest(const request_info &info) const
{
    ostringstream info_description;

    info_description << "Command";

    switch( info.command )
    {
        case Klondike_Dtran:
        {
            // Describe this correctly!
            info_description << " DTran / length " << info.dtran.message.size();
            break;
        }
        case Klondike_CheckStatus:
        {
            info_description << " Check Status / ";
            break;
        }
        case Klondike_ClearBuffers:
        {
            info_description << " Clear Buffers / ";
            if ( info.clearBuffer.clearAll )
            {
                info_description << "clear all buffers ";
            }
            else
            {
                info_description << "sequences to clear: ";
                for each (int seq in info.clearBuffer.sequences )
                {
                    info_description << seq << " ";
                }
                info_description << " / ";
            }
            break;
        }
        case Klondike_LoadBuffer:
        {
            info_description << " Load Buffer / " << info.loadBuffer.request_group.size() << " requests";

            vector<queue_entry>::const_iterator entry_itr = info.loadBuffer.request_group.begin(),
                                                entry_end = info.loadBuffer.request_group.end();

            for( ; entry_itr != entry_end; ++entry_itr )
            {
                info_description << endl;

                info_description << "sequence "; //<< setw(10) << setfill('0')
                info_description << entry_itr->sequence << ", ";
                info_description << "priority " << setw(2)                  << entry_itr->priority << ", ";

                const queue_entry::request_info &request = entry_itr->request;

                info_description << "addr " << setw(7) << request.b_word.dlc_address << ", ";

                switch( request.word_type )
                {
                    case EmetconWord::WordType_A:
                    {
                        info_description << "A word";
                        break;
                    }
                    case EmetconWord::WordType_B:
                    {
                        info_description << "B word";
                        info_description << ", ";

                        info_description << (request.function?'f':' ');
                        info_description << (request.write   ?'w':'r');
                        info_description << hex << setw(2) << setfill('0') << request.b_word.function_code << dec << ", ";

                        info_description << "repeater fixed bits "    << request.b_word.repeater_fixed    << ", ";
                        info_description << "repeater variable bits " << request.b_word.repeater_variable << ", ";
                        info_description << "repeater count "         << request.stagesToFollow    << ", ";
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
        case Klondike_FreezeBuffer:
        {
            info_description << " Freeze Buffer / ";
            break;
        }
        case Klondike_ThawBuffer:
        {
            info_description << " Thaw Buffer / ";
            break;
        }
        case Klondike_SpyBuffer:
        {
            info_description << " Spy Buffer / ";
            break;
        }
        case Klondike_ReadBuffer:
        {
            info_description << " Read Buffer / Block Read Flags " << CtiNumStr(info.readBuffer.flags).xhex();
            break;
        }
        case Klondike_TimeSync:
        {
            info_description << " Time Sync / " << info.xtime.timesync;
            break;
        }
        case Klondike_WriteRouteTable:
        {
            info_description << " Write Route Table / entries " << info.routeTable.write_requests.size();
            break;
        }
        case Klondike_ReadRouteTable:
        {
            info_description << " Read Route Table / ";
            break;
        }
        case Klondike_ReadRouteTableOpen:
        {
            info_description << " Read Route Table Open Slots / ";
            break;
        }
        case Klondike_ClearRouteTable:
        {
            info_description << " Clear Route Table / ";
            if( info.routeTable.clearAllRoutes )
            {
                info_description << " clear all routes / ";
            }
            else
            {
                info_description << " routes to clear: ";
                for each (int route in info.routeTable.routesToClear)
                {
                    info_description << route << " ";
                }
                info_description << " / ";
            }
            break;
        }
        case Klondike_ReadMemory:
        {
            info_description << " Read Memory / start address " << info.memoryAccess.startAddress;
            info_description << " number of bytes " << info.memoryAccess.numBytes;
            break;
        }
        case Klondike_WriteMemory:
        {
            info_description << " Write Memory / start address " << info.memoryAccess.startAddress;
            info_description << " number of bytes " << info.memoryAccess.numBytes;
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

string Ccu721::describeReply(const idlc_reply &reply) const
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

string Ccu721::describeGeneralReply(const reply_info &info) const
{
    ostringstream info_description;

    info_description << describeStatuses(_status.status_bytes);

    info_description << "Responding to ";

    switch( info.command )
    {
        case Klondike_Dtran:
        {
            info_description << "Dtran / response data ";

            bytes::const_iterator message_itr = info.dtran.message.begin();
            bytes::const_iterator message_end = info.dtran.message.end();

            info_description << hex;

            int dFill = info_description.fill('0');

            while( message_itr != message_end )
            {
                info_description<< setw(2) << static_cast<int>(*message_itr++) << " ";
            }

            info_description.fill(dFill);

            break;
        }
        case Klondike_CheckStatus:
        {
            info_description << "Check Status / Expected Sequence " << _next_seq << ", slots available ";
            info_description << LoadBufferMaxSlots - _queue.pending.size() << ", status ";
            info_description << std::hex << _status.status_bytes;
            break;
        }
        case Klondike_LoadBuffer:
        {
            info_description << "Load Buffer / accepted " << info.loadBuffer.accepted;
            info_description << " / slots available " << info.loadBuffer.available;
            break;
        }
        case Klondike_SpyBuffer:
        {
            info_description << "Spy Buffer / accepted " << info.loadBuffer.accepted;
            info_description << " / slots available " << info.loadBuffer.available;
            break;
        }
        case Klondike_ReadBuffer:
        {
            info_description << "Read Buffer / block read flags " << CtiNumStr(info.readBuffer.flags).xhex() << " /";
            info_description << " buffer slots read " << info.collected_queue_entries.size();
            if( !info.collected_queue_entries.empty() )
            {
                vector<queue_entry>::const_iterator buffer_itr = info.collected_queue_entries.begin(),
                                                    buffer_end = info.collected_queue_entries.end();

                for( ; buffer_itr != buffer_end; buffer_itr++ )
                {
                    info_description << endl;
                    info_description << "sequence " << buffer_itr->sequence << ", ";
                    info_description << "completion time " << buffer_itr->result.completion_time << ", ";
                    info_description << "message length " << buffer_itr->result.data.size();
                }
            }

            break;
        }
        case Klondike_ReadRouteTable:
        {
            // Once the route table is actually implemented this will describe
            // the entries that are in the route table. Until then this does nothing.
            info_description << " Read Route Table / ";
            break;
        }
        case Klondike_ReadRouteTableOpen:
        {
            // Same as read route table description.
            info_description << "Read Route Table Open Slots / ";
            break;
        }
        case Klondike_ReadMemory:
        {
            info_description << "Read Memory / start address " << info.memoryAccess.startAddress;
            info_description << " / number of bytes " << info.memoryAccess.numBytes << " / ";

            bytes::const_iterator message_itr = info.memoryAccess.data.begin();
            bytes::const_iterator message_end = info.memoryAccess.data.end();

            info_description << hex;

            int dFill = info_description.fill('0');

            while( message_itr != message_end )
            {
                info_description<< setw(2) << static_cast<int>(*message_itr++) << " ";
            }

            info_description.fill(dFill);

            break;
        }
    }
    return info_description.str();
}

string Ccu721::describeStatuses(const unsigned short &status) const
{
    ostringstream info_description;

    if( status & Status_TimeSyncRequired )
    {
        info_description << "Time Sync Required / ";
    }
    if( status & Status_RespBufHasData )
    {
        info_description << "Response Buffer has Data / ";
    }
    if( status & Status_RespBufMarkedData )
    {
        info_description << "Response Buffer has Marked Data / ";
    }
    if( status & Status_RespBufFull )
    {
        info_description << "Response Buffer Full / ";
    }
    if( status & Status_TransBufHasData )
    {
        info_description << "Transmit Buffer has Data / ";
    }
    if( status & Status_TransBufFull )
    {
        info_description << "Transmit Buffer full / ";
    }
    if( status & Status_TransBufFrozen )
    {
        info_description << "Transmit Buffer Frozen / ";
    }
    if( status & Status_PLCTransDtran )
    {
        info_description << "PLC Transmitting DTran Message / ";
    }
    if( status & Status_PLCTransBuf )
    {
        info_description << "PLC Transmitting Buffer Message / ";
    }

    return info_description.str();
}

error_t Ccu721::sendReply(Comms &comms, const idlc_reply &reply, Logger &logger) const
{
    if( !comms.write(reply.message, logger) )
    {
        return "Error writing reply to comms";
    }

    return error_t::success;
}

error_t Ccu721::validateCrc(const bytes &message) const
{
    //  this array is for the interface with NCrcCalc_C, which wants a raw buffer
    bytes buf { message.begin() + 1, message.end() - 2 };

    unsigned short message_crc = MAKEUSHORT(*(message.end() - 1), *(message.end() - 2));

    unsigned short crc = NCrcCalc_C(buf.data(), buf.size());

    if( message_crc != crc )
    {
        return error_t("CRCs do not match").neq(message_crc, crc);
    }

    return error_t::success;
}

error_t Ccu721::writeIdlcHeader(const idlc_header &header, byte_appender &out_itr) const
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

error_t Ccu721::writeReplyInfo(const reply_info &info, byte_appender &out_itr) const
{
    bytes status_buf;
    bytes data_buf;
    bytes ack_buf;

    byte_appender status_out_itr(status_buf);
    byte_appender ack_out_itr(ack_buf);

    error_t error;

    if( error = writeReplyAckInfo(info.command, ack_out_itr) )
    {
        return "Error writing ack/nak info / " + error;
    }

    if( error = writeReplyStatus(_status.status_bytes, status_out_itr) )
    {
        return "Error writing reply statuses / " + error;
    }

    // ACK with Data section. This needs to be updated when and if NAK responses become available.
    // For now, assume all responses are ACKs and that a NAK doesn't occur.
    switch( info.command )
    {
        case Klondike_Dtran:
        {
            // Sequence number
            data_buf.push_back(info.dtran.sequence);
            data_buf.push_back(info.dtran.sequence >> 8);

            // Signal Strength
            data_buf.push_back(0xff);
            data_buf.push_back(0xff);

            // Message Length
            data_buf.push_back(info.dtran.message.size());

            // DLC Data
            data_buf.insert(data_buf.end(),
                            info.dtran.message.begin(),
                            info.dtran.message.end());

            break;
        }
        case Klondike_CheckStatus:
        {
            data_buf.push_back(LoadBufferMaxSlots - _queue.pending.size());
            data_buf.push_back(_next_seq);
            data_buf.push_back(_next_seq >> 8);

            break;
        }
        case Klondike_SpyBuffer:
        case Klondike_LoadBuffer:
        {
            data_buf.push_back(info.loadBuffer.accepted & 0x0f);
            data_buf.push_back(info.loadBuffer.available);

            break;
        }
        case Klondike_ReadBuffer:
        {
            data_buf.push_back(info.readBuffer.flags);
            data_buf.push_back(info.collected_queue_entries.size());

            unsigned entries;

            vector<queue_entry>::const_iterator completed_itr = info.collected_queue_entries.begin(),
                                                completed_end = info.collected_queue_entries.end();

            for( ; completed_itr != completed_end; ++completed_itr )
            {
                bytes completed_entry_buf;

                completed_entry_buf.push_back(completed_itr->sequence);
                completed_entry_buf.push_back(completed_itr->sequence >> 8);

                unsigned utc_seconds = completed_itr->result.completion_time.seconds();

                completed_entry_buf.push_back(utc_seconds);
                completed_entry_buf.push_back(utc_seconds >> 8);
                completed_entry_buf.push_back(utc_seconds >> 16);
                completed_entry_buf.push_back(utc_seconds >> 24);

                // Signal Strength.
                completed_entry_buf.push_back(0xff);
                completed_entry_buf.push_back(0xff);

                // Result
                //  00 - Valid Reply
                //  01 - Invalid Reply (No Data)
                //  02 - Error Message
                completed_entry_buf.push_back(0x00);

                unsigned data_size = completed_itr->result.data.size();

                // Message Length
                completed_entry_buf.push_back(completed_itr->result.data.size());

                completed_entry_buf.insert(completed_entry_buf.end(),
                                           completed_itr->result.data.begin(),
                                           completed_itr->result.data.end());

                data_buf.insert(data_buf.end(),
                                completed_entry_buf.begin(),
                                completed_entry_buf.end());
            }
            break;
        }
        case Klondike_ReadRouteTable:
        {
            // This needs to read all route table data for routes in use.
            // No routing table exists yet, just pad it with 0s for now.
            data_buf.push_back(0);
            data_buf.push_back(0);
            data_buf.push_back(0);

            break;
        }
        case Klondike_ReadRouteTableOpen:
        {
            // This responds with the route number of all the open routes in the
            // routing table. Contrary to what the Read Route Table command responds with,
            // and to make this response a lot simpler, assume all routes are being used
            // and that there are no route numbers open.

            // Number of routes available
            data_buf.push_back(0);

            // No data to follow if routes available is zero. Done.
            break;
        }
        case Klondike_ReadMemory:
        {
            // Memory type
            data_buf.push_back(info.memoryAccess.type);

            // Starting address
            data_buf.push_back(info.memoryAccess.startAddress);
            data_buf.push_back(info.memoryAccess.startAddress >> 8);

            // Number of bytes
            data_buf.push_back(info.memoryAccess.data.size());

            // Read memory data
            data_buf.insert(data_buf.end(),
                            info.memoryAccess.data.begin(),
                            info.memoryAccess.data.end());

            break;
        }
    }

    copy(ack_buf.begin(),    ack_buf.end(),    out_itr);
    copy(status_buf.begin(), status_buf.end(), out_itr);
    copy(data_buf.begin(),   data_buf.end(),   out_itr);

    return error_t::success;
}

error_t Ccu721::writeReplyAckInfo (const KlondikeCommandCodes &command, byte_appender &out_itr) const
{
    switch( command )
    {
        case Klondike_Dtran:
        {
            // Dtran's valid responses:
            //      ACK / Data
            //      ACK / No Data
            //      NAK
            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_CheckStatus:
        {
            // Check Status valid responses:
            //      ACK / Data
            //      NAK

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_ClearBuffers:
        {
            // Clear Buffers valid responses:
            //      ACK / No Data
            //      NAK

            out_itr++ = Klondike_AckNoData;
            break;
        }
        case Klondike_LoadBuffer:
        {
            // Load Buffer valid responses:
            //      ACK / Data
            //      NAK

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_FreezeBuffer:
        {
            // Freeze Buffer valid responses:
            //      ACK / No Data
            //      NAK

            out_itr++ = Klondike_AckNoData;
            break;
        }
        case Klondike_ThawBuffer:
        {
            // Thaw Buffer valid responses:
            //      ACK / No Data
            //      NAK

            out_itr++ = Klondike_AckNoData;
            break;
        }
        case Klondike_SpyBuffer:
        {
            // Spy Buffer valid responses:
            //      ACK / Data
            //      NAK

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_ReadBuffer:
        {
            // Read/ACK Reply Buffer valid responses:
            //      ACK / Data
            //      NAK

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_TimeSync:
        {
            // Time Sync CCU valid responses:
            //      ACK / No Data
            //      NAK

            out_itr++ = Klondike_AckNoData;
            break;
        }
        case Klondike_WriteRouteTable:
        {
            // Write Route Table valid responses:
            //      ACK / No Data

            out_itr++ = Klondike_AckNoData;
            break;
        }
        case Klondike_ReadRouteTable:
        {
            // Read Route Table valid responses:
            //      ACK / Data
            //      NAK

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_ReadRouteTableOpen:
        {
            // Read Route Table Open Slots valid responses:
            //      ACK / Data
            //      NAK

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_ClearRouteTable:
        {
            // Clear Route Table valid responses:
            //      ACK / No Data
            //      NAK

            out_itr++ = Klondike_AckNoData;
            break;
        }
        case Klondike_ReadMemory:
        {
            // Read Memory valid responses:
            //      ACK / Data

            out_itr++ = Klondike_AckData;
            break;
        }
        case Klondike_WriteMemory:
        {
            // Write Memory valid responses:
            //      ACK / No Data
            //      NAK

            out_itr++ = Klondike_AckNoData;
            break;
        }
        default:
        {
            // Incoming command wasn't a valid request command.
            return error_t("Incoming request contained invalid command type.", command);
        }
    }

    out_itr++ = command;

    return error_t::success;
}

error_t Ccu721::writeReplyStatus(const unsigned short &status, byte_appender &out_itr) const
{
    {
        unsigned char byte = 0;

        if( status & Status_RespBufHasData )    byte |= 1 << 0;
        if( status & Status_RespBufMarkedData ) byte |= 1 << 1;
        if( status & Status_RespBufFull )       byte |= 1 << 2;
        if( status & Status_TransBufHasData )   byte |= 1 << 3;
        if( status & Status_TransBufFull )      byte |= 1 << 4;
        if( status & Status_TransBufFrozen )    byte |= 1 << 5;
        if( status & Status_PLCTransDtran )     byte |= 1 << 6;
        if( status & Status_PLCTransBuf )       byte |= 1 << 7;

        out_itr++ = byte;
    }

    {
        unsigned char byte = 0;

        if( status & Status_TimeSyncRequired )  byte |= 1 << 0;

        out_itr++ = byte;
    }

    return error_t::success;
}

error_t Ccu721::writeIdlcCrc(const bytes &message, byte_appender &out_itr) const
{
    //  this array is for the interface with NCrcCalc_C, which wants a raw buffer
    bytes buf { message.begin() + 1, message.end() };

    unsigned short crc = NCrcCalc_C(buf.data(), buf.size());

    *out_itr++ = HIBYTE(crc);
    *out_itr++ = LOBYTE(crc);

    return error_t::success;
}

bool Ccu721::validateCommand(SocketComms &socket_interface)
{
    bytes peek_buf;
    socket_interface.peek(byte_appender(peek_buf), 5);
    unsigned char command = peek_buf[4];
    unsigned numEntries = 0, numBytes = 0;

    if( (command & 0x80) || (command & 0x08) )
    {
        // Bits 3 and 7 of the command byte for the 721 are NEVER set in any of
        // the valid request commands. If either of them are, we know for sure
        // that this is a 711 command.
        return false;
    }

    switch( command )
    {
        case Klondike_Dtran:
            // Not definitive.
            // This command could be of varying size. Hard to distinguish.
            // Return false for now until a definitive way can be determined.
            return false;
        case Klondike_CheckStatus:
            // There should only be 7 total bytes in the request if this is the case.
            return (peek_buf[3] == 1);
        case Klondike_ClearBuffers:
            // Could be a lengthy command. If the length is 1 we know for sure it's
            // a 721 command, otherwise it's hard to say.
            return (peek_buf[3] == 1);
        case Klondike_LoadBuffer:
            // This is another lengthy command. We know for sure that the number of
            // entries must be 8 or fewer, so if this isn't the case we know it's not
            // a 721 command.

            // Since 711 general requests are at least 9 bytes in length, and the load
            // buffer request is even longer, we can safely grab the value for the number
            // of queue entries to check.
            if( peek_buf[8] > 8 )
            {
                return false;
            }

            // Return false anyway until we can distiguish for sure.
            return false;
        case Klondike_FreezeBuffer:
            return (peek_buf[3] == 1);
        case Klondike_ThawBuffer:
            return (peek_buf[3] == 1);
        case Klondike_SpyBuffer:
            return (peek_buf[3] == 1);
        case Klondike_ReadBuffer:
            // Should only contain 8 bytes, with data length of 2.
            return (peek_buf[3] == 2);
        case Klondike_TimeSync:
            // Should be exactly 11 bytes, data length of 5.
            if ( peek_buf[3] != 5 )
            {
                return false;
            }
            // It is possible that a 711 command could be 11 bytes as well. Until we can
            // tell for sure we should return false.
            return false;
        case Klondike_WriteRouteTable:
            // Could be lengthy. Hard to say conclusively.
            // Entries are 3 bytes each, and this command specifies how many of these entries there
            // should be. Do the math and if it matches length, this should be a Write Route table command.
            socket_interface.peek(byte_appender(peek_buf), 6);

            numEntries = peek_buf[5];
            if( peek_buf[3] == (3 * numEntries) + 2 )
            {
                return true;
            }
            return false;
        case Klondike_ReadRouteTable:
            return (peek_buf[3] == 1);
        case Klondike_ReadRouteTableOpen:
            return (peek_buf[3] == 1);
        case Klondike_ClearRouteTable:
            // Specifies number of routes to clear. If the length matches this number, then
            // this request is probably a clear route table command.
            if( peek_buf[3] == 2 )
            {
                // This isn't necessarily true. If this is the case, the second byte is probably
                // a 0x00 indended to clear all the routes. However, this 0x00 matches with the
                // 711's ACTIN command which will be virtually indistinguishable.
                return false;
            }

            socket_interface.peek(byte_appender(peek_buf), 6);
            numEntries = peek_buf[5];
            if( peek_buf[3] == numEntries + 2 )
            {
                return true;
            }

            // This may not be entirely correct. There is no guarantee that these values matching
            // ensures that this is the correct command. Also, none of the route numbers can be
            // higher than 31. This doesn't check each of these values.
            return false;
        case Klondike_ReadMemory:
            // This command must be exactly 11 bytes long, data length of 5.
            if( peek_buf[3] != 5 )
            {
                return false;
            }

            return false;
        case Klondike_WriteMemory:
            if( peek_buf[3] < 3 )
            {
                return false;
            }
            socket_interface.peek(byte_appender(peek_buf), 9);
            numBytes = peek_buf[8];
            if( peek_buf[3] == numBytes + 5 )
            {
                return true;
            }
            return false;
        default:
        {
            // Command is either non-existent or doesn't make sense.
            return false;
        }
    }
}

error_t Ccu721::setStatusBit(const StatusDescriptions &statusPos)
{
    _status.status_bytes |= statusPos;

    return error_t::success;
}

error_t Ccu721::resetStatusBit(const StatusDescriptions &statusPos)
{
    _status.status_bytes &= ~statusPos;

    return error_t::success;
}

error_t Ccu721::updateStatusBytes()
{
    // Bit 0 of Byte 1 of the Status Bytes.
    if( !_queue.completed.empty() )
    {
        setStatusBit(Status_RespBufHasData);
    }
    else
    {
        resetStatusBit(Status_RespBufHasData);
    }

    // Bit 1 of Byte 1 of the Status Bytes.
    // "Marked Data"

    // Bit 3 of Byte 1 of the Status Bytes.
    if( !_queue.pending.empty() )
    {
        setStatusBit(Status_TransBufHasData);
    }
    else
    {
        resetStatusBit(Status_TransBufHasData);
    }

    // Bit 5 of Byte 1 of the Status Bytes.
    if( _bufferFrozen )
    {
        setStatusBit(Status_TransBufFrozen);
    }
    else
    {
        resetStatusBit(Status_TransBufFrozen);
    }

    // Bit 6 of Byte 1 of the Status Bytes.
    // "Transmitting Dtran Message" - Necessary?

    // Bit 7 of Byte 1 of the Status Bytes.
    if ( (_status.status_bytes & Status_TransBufHasData) && (_queue.completed.size() < LoadBufferMaxSlots) )
    {
        setStatusBit(Status_PLCTransBuf);
    }
    else
    {
        resetStatusBit(Status_PLCTransBuf);
    }

    return error_t::success;
}

}
}

