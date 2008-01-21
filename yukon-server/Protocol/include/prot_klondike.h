/*-----------------------------------------------------------------------------*
*
* File:   prot_klondike
*
* Namespace: Cti::Protocol
* Class:     Klondike
* Date:      2006-aug-08
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/01/21 20:54:00 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_KLONDIKE_H__
#define __PROT_KLONDIKE_H__
#pragma warning( disable : 4786)


#include <list>

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_wrap.h"

#include "prot_idlc.h"
//#include "dnp_datalink.h"  //  DNP should be reimplemented as a wrapper protocol


namespace Cti       {
namespace Protocol  {


class IM_EX_PROT Klondike : public Interface
{
    enum Command;
    //enum ProtocolWrap;
    enum Errors;

private:

    struct command_mapping_t : public map<int, Command>
    {
        command_mapping_t();
    };

    struct error_mapping_t : public map<Errors, int>
    {
        error_mapping_t();
    };

    unsigned short _masterAddress,
                   _slaveAddress;

    Command _command;
    static const command_mapping_t _command_mapping;

    BSTRUCT _dtran_bstruct;  //  the outbound information for a direct transmission
    unsigned char _d_words[DWORDLEN * 3];  //  the response (if any) from a direct transmission
    unsigned char _response_length;

    int _protocol_errors;
    int _sequence;

    void processResponse_DirectMessage(unsigned char *inbound, unsigned char inbound_length);

    enum NAK_Errors
    {
        NAK_DTran_DTranBusy = 0x00,
        NAK_DTran_NoRoutes,
        NAK_DTran_InvalidSEQ,
        NAK_DTran_InvalidBUS,
        NAK_DTran_BUSDisabled,
        NAK_DTran_InvalidDLCType,
    };

    int _error_code;

    static const error_mapping_t _error_mapping;

    enum MiscNumeric
    {
        ProtocolErrorsMaximum =   3,
        WrapLengthMaximum     = 255,
    };

    enum IO_State
    {
        IO_Invalid,

        IO_Output,
        IO_Input,

        IO_Complete,

    } _io_state;

    void doOutput(void);
    static bool response_expected(Command command);

    Klondike(const Klondike &aRef);
    Klondike &operator=(const Klondike &aRef);

    Wrap *_wrap;

    IDLC          _idlc_wrap;
    //DNP::Datalink _dnp_wrap;

protected:

public:

    Klondike();
    virtual ~Klondike();

    void setAddresses(unsigned short slaveAddress, unsigned short masterAddress);
    //void setWrap(ProtocolWrap w);

    bool setCommand(int command);
    bool setCommandDirectTransmission(const BSTRUCT &BSt);

    void getResultDirectTransmission(unsigned char *buf, int buf_length, unsigned char &input_length);

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    bool isTransactionComplete(void) const;

    bool errorCondition() const;
    int  errorCode();

    static int decodeDWords(unsigned char *input, unsigned input_length, unsigned Remote, DSTRUCT *DSt);

    /*enum ProtocolWrap
    {
        ProtocolWrap_IDLC,
        ProtocolWrap_DNP,
    };*/

    enum Command
    {
        Command_Invalid = 0,

        Command_DirectMessageRequest,

        Command_WaitingQueueWrite,
        Command_WaitingQueueFreeze,
        Command_WaitingQueueRead,
        Command_WaitingQueueClear,
        Command_ReplyQueueRead,

        Command_TimeSyncCCU,
        Command_TimeSyncTransmit,

        Command_RoutingTableWrite,
        Command_RoutingTableRead,
        Command_RoutingTableRequestAvailableSlots,
        Command_RoutingTableClear,

        Command_ConfigurationMemoryRead,
        Command_ConfigurationMemoryWrite,

        Command_Failed,
        Command_Complete,
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
