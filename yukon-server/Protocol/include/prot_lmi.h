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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/07/27 16:52:51 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_LMI_H__
#define __PROT_LMI_H__
#pragma warning( disable : 4786)


#include <map>
#include <vector>
using namespace std;

#include "dlldefs.h"
#include "dsm2.h"

#include "prot_seriesv.h"
#include "verification_objects.h"


class IM_EX_PROT CtiProtocolLMI : public CtiProtocolBase
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
    bool          _deadbandsSent,
                  _transactionComplete;

    int           _transmitter_power;
    unsigned long _transmitter_power_time;

    //crc_ccitt_type _crc;
    queue< CtiOutMessage * > _codes;
    queue< CtiVerificationWork * > _work_objects;
    queue< unsigned int > _retrieved_codes;
    queue< unsigned int > _returned_codes;

    unsigned int  _num_codes_retrieved;

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
        LMIPacketOverheadLen = 8,
        LMIPacketHeaderLen   = 6,

        LMITransmitterPowerPointOffset = 1000
    };

#pragma pack(push, 1)

    struct lmi_outmess_struct
    {
        LMICommand command;

        unsigned short control_offset;
        unsigned short control_parameter;  //  for controls, it's duration;  for setpoints, it's value
    };

    struct lmi_inmess_struct
    {
        unsigned short num_codes;
        unsigned short seriesv_inmess_length;
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

#pragma pack(pop)

    unsigned long _in_count,
                  _in_total;

    RWTime _completion_time,
           _transmitting_until;

    bool _first_comm;

    RWCString _name;

protected:

public:
    CtiProtocolLMI();
    CtiProtocolLMI(const CtiProtocolLMI &aRef);

    virtual ~CtiProtocolLMI();

    CtiProtocolLMI &operator=(const CtiProtocolLMI &aRef);

    enum
    {
        QueuedWorkToken = 4845  //  w00t
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
        Command_QueueCode,
        Command_TransmitCodes,
        Command_SendQueuedCodes,
        Command_ReadQueuedCodes,
        Command_ReadEchoedCodes
    };

    void setAddress(unsigned char address);
    void setName(const RWCString &name);
    void setCommand(LMICommand cmd, unsigned control_offset = 0, unsigned control_parameter = 0);
    void setDeadbands(const vector<unsigned> &points, const vector<unsigned> &deadbands);

    //  client-side (Scanner, PIL) functions
    int sendCommRequest(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList);
    int recvCommResult (INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList);

    bool hasInboundData();
    void getInboundData(RWTPtrSlist< CtiPointDataMsg > &pointList, RWCString &info);

    //  porter-side (portfield, specificially) functions
    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult (INMESS  *InMessage);

    void getVerificationWorkObjects(queue< CtiVerificationBase * > &work_queue);

    void   queueCode(CtiOutMessage *om);
    bool   hasCodes() const;
    bool   canTransmit(const RWTime &allowed_time) const;
    int    getNumCodes() const;
    RWTime getTransmittingUntil() const;

    bool isTransactionComplete();

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);
};

#endif // #ifndef __PROT_LMI_H__
