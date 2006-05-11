/*-----------------------------------------------------------------------------*
*
* File:   prot_lmi
*
* Class:  CtiProtocolLMI
* Date:   2004-jan-14
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2006/05/11 15:35:47 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_LMI_H__
#define __PROT_LMI_H__
#pragma warning( disable : 4786)


#include <map>
#include <vector>
using std::map;
using std::vector;

#include "dllbase.h"
#include "dlldefs.h"
#include "dsm2.h"

#include "prot_seriesv.h"
using namespace Cti;  //  in preparation for moving devices to their own namespace

#include "verification_objects.h"


class IM_EX_PROT CtiProtocolLMI : public Protocol::Interface
{
public:
    enum LMICommand;

private:
    CtiProtocolSeriesV _seriesv;
    unsigned char *_seriesv_inbuffer;

    LMICommand    _command;
    unsigned int  _control_offset,
                  _control_parameter;

    unsigned char _address;
    bool          _deadbands_sent,
                  _transaction_complete;
    int           _retries;

    long          _transmitter_id;

    int           _transmitter_power;
    unsigned long _transmitter_power_time;

    int _tick_time, _transmit_window, _time_offset;
    int _transmitter_power_low_limit, _transmitter_power_high_limit;
    string _start_code, _stop_code;

    //crc_ccitt_type _crc;
    queue< CtiOutMessage * > _codes;
    queue< CtiVerificationBase * > _verification_objects;
    queue< unsigned int > _retrieved_codes;
    queue< unsigned int > _returned_codes;

    pointlist_t _lmi_statuses;

    unsigned int  _num_codes_retrieved, _num_codes_loaded, _outbound_code_count;
    unsigned long _config_sent;
    unsigned long _last_code_download;
    unsigned long _comm_end_time, _transmission_end;
    bool _verification_pending;
    bool _untransmitted_codes;
    bool _preload_sequence;

    enum LMIOpcode
    {
        Opcode_Invalid   = 0,
        Opcode_SendCodes,
        Opcode_GetOriginalCodes,
        Opcode_SetTime,
        Opcode_DownloadSystemData,
        Opcode_UploadSystemData,
        Opcode_Reset,
        Opcode_FlushCodes,
        Opcode_ClearAndReadStatus,
        Opcode_RetransmitCodes,
        Opcode_GetEchoedCodes,
        Opcode_GetTransmitterPower,
        Opcode_SeriesVWrap = 15
    };

    enum
    {
        PacketOverheadLen = 8,
        PacketHeaderLen   = 6,

        MaxCodesPerTransaction =  42,
        MaxCodesDownloaded     = 255,  //  or is it 255 + 42?

        PointOffset_TransmitterPower = 1000,  //  analog

        PointOffset_LoadShedVerifyState     = 1001,  //  statuses
        PointOffset_LoadShedVerifyComplete  = 1002,
        PointOffset_QuestionableRequest     = 1003,
        PointOffset_LMICommunicationFailure = 1004,
        PointOffset_TransmitterLoadVerified = 1005,
        PointOffset_LoadShedCodesLocked     = 1006,
        PointOffset_LMIReset                = 1007,

        PointOffset_CodeVerification        = 1008,  //  a combination of VerifyState and VerifyComplete

        MaxConsecutiveStatusScans = 5,

        SendCodes_LastCodeGroup   = 0x40,
        SendCodes_SendImmediately = 0x80,
        SendCodes_CountBitmask    = 0x3f,

        RetryCount = 3,
    };

#pragma pack(push, 1)

    struct lmi_outmess_struct
    {
        LMICommand command;

        unsigned short control_offset;
        unsigned short control_parameter;  //  for controls, it's duration;  for setpoints, it's value
    };

    struct lmi_pointdata
    {
        unsigned short offset;
        unsigned short value;
        unsigned long  time;
        CtiPointType_t type;
    };

    struct lmi_status
    {
        unsigned char loadshed_verify_state     : 1;
        unsigned char loadshed_verify_complete  : 1;
        unsigned char questionable_request      : 1;
        unsigned char comm_failure              : 1;
        unsigned char transmitter_load_verified : 1;
        unsigned char loadshed_codes_locked     : 1;
        unsigned char unused                    : 1;
        unsigned char reset                     : 1;
    };

    struct lmi_inmess_struct
    {
        unsigned short num_codes;
        unsigned long  transmitter_power;
        unsigned long  transmitter_power_time;
        unsigned short seriesv_inmess_length;
    };

    struct lmi_body_header
    {
        unsigned char message_type : 7;
        unsigned char flush_codes  : 1;
    };

    struct lmi_message
    {
        unsigned char preamble;
        unsigned char length;
        unsigned char dest_sat_id;
        unsigned char dest_node;
        unsigned char src_sat_id;
        unsigned char src_sat_node;
        lmi_body_header body_header;
        unsigned char data[300];
    } _outbound, _inbound;

    union lmi_status_union
    {
        lmi_status s;
        unsigned char c;
    } _status;

    bool _status_read;
    bool _codes_ready;
    int  _status_read_count;
    int  _echoed_error_count;

#pragma pack(pop)

    unsigned long _in_count,
                  _in_total;

    bool _first_code_block,
         _final_code_block;

protected:

public:
    CtiProtocolLMI();
    CtiProtocolLMI(const CtiProtocolLMI &aRef);

    virtual ~CtiProtocolLMI();

    CtiProtocolLMI &operator=(const CtiProtocolLMI &aRef);

    //  these are tokens to kick the protocol layer into doing something fun
    enum LMISequences
    {
        Sequence_Code = 4845,  //  w00t
        Sequence_Preload,
        Sequence_QueueCodes,
        Sequence_ReadEchoedCodes,
        Sequence_ClearQueuedCodes,
        Sequence_TimeSync
    };

    enum LMICommand
    {
        Command_ScanAccumulator,
        Command_ScanIntegrity,
        Command_ScanException,
        Command_Control,
        Command_Loopback,
        Command_AnalogSetpoint,
        Command_Timesync,
        Command_QueueCodes,
        Command_ReadQueuedCodes,
        Command_ReadEchoedCodes,
        Command_ClearEchoedCodes,
        Command_ClearQueuedCodes,
    };

    void setAddress(unsigned char address);
    void setCommand(LMICommand cmd, unsigned control_offset = 0, unsigned control_parameter = 0);
    void setDeadbands(const vector<unsigned> &points, const vector<unsigned> &deadbands);
    void setSystemData(int ticktime, int timeoffset, int transmittime, int transmitterlow, int transmitterhigh, string startcode, string stopcode);

    LMICommand getCommand() const;

    //  client-side (Scanner, PIL) functions
    int sendCommRequest(OUTMESS *&OutMessage, list< OUTMESS* > &outList);
    int recvCommResult (INMESS   *InMessage,  list< OUTMESS* > &outList);

    bool hasInboundData();
    void getInboundData(list< CtiPointDataMsg* > &pointList, string &info);

    //  porter-side (portfield, specificially) functions
    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult (INMESS  *InMessage);

    void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);
    void getStatuses(pointlist_t &points);

    void   queueCode(CtiOutMessage *om);
    bool   hasQueuedCodes() const;
    bool   codeVerificationPending() const;
    bool   canDownloadCodes() const;
    int    getNumCodes() const;
    int    getPreloadDataLength() const;
    CtiTime getTransmissionEnd() const;
    CtiTime getLastCodeDownload() const;

    bool isTransactionComplete();

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    void decodeStatuses(lmi_status statuses);
};

#endif // #ifndef __PROT_LMI_H__
