
/*-----------------------------------------------------------------------------*
*
* File:   prot_fmu
*
* Class:  CtiProtocolFMU
* Date:   9/26/2006
*
* Author: Julie Richter
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/26 19:56:14 $
* HISTORY      :
*
* $Log: prot_fmu.h,v $
* Revision 1.1  2007/01/26 19:56:14  jrichter
* FMU stuff for jess....
*
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PROT_FMU_H__
#define __PROT_FMU_H__

#include "cmdparse.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"
#include "fmu_application.h"

//using namespace Cti;  //  in preparation for moving devices to their own namespace

using std::pair;
namespace Cti       {
namespace Protocol  {
namespace fmuProtocol {


class IM_EX_PROT CtiProtocolFMU : public Protocol::Interface
{
protected:

    
    CtiFMUApplication _fmuAppLayer;

private:

    BYTE                 *_currentPacket;
    INT                   _packetBytesReceived;
    BYTE                 *_fmuMsgs;
    int _currentDataOffset;
    BOOL _expectMore;

    UINT32 _address;
    BYTE _seq;
    BYTE _cmd;
    BYTE _dataLen;


    typedef struct
    {
        UINT32 utcTime;
        UINT8 codeSet;
        UINT8 rssi;   
        UINT8 msgLength;
    }FMU_MSG_HEADER_STRUCT;

    typedef struct
    {
        FMU_MSG_HEADER_STRUCT header;
        BYTE *data;
    }
    FMU_MSG;

public:

    CtiProtocolFMU();

    virtual ~CtiProtocolFMU();

    void init(void);
    CtiProtocolFMU& operator=(const CtiProtocolFMU& aRef);
    int parseCommand(CtiCommandParser &parse);

    bool messageReady() const;

    void getBuffer(BYTE *dest, ULONG &len) const;

    BYTE getFMUData() const;
    CtiProtocolFMU& setFMUData(const BYTE data);

    CtiFMUApplication &getApplicationLayer();
    int decodeHeader( CtiXfer &xfer, int status );
    int decodeData( CtiXfer &xfer, int status );
    int generate(UCHAR *abuf, INT& buflen, UINT32 address, BYTE sequence, USHORT cmd, BYTE *optionalData);

    bool isCRCvalid( void );
    unsigned short crc16( unsigned char octet, unsigned short crc );
    unsigned short crc( int size, unsigned char *packet );

    static string asString(const CtiSAData &sa);
    bool getStartOfMessageFlag(BYTE sequence);
    bool getEndOfMessageFlag(BYTE sequence);

    UINT32 getAddress();
    BYTE getSequence();
    BYTE getCmd();
    BYTE getDataLen();

    void setAddress(UINT32 add);
    void setSequence(BYTE seq);
    void setCmd(BYTE cmd);
    void setDataLen(BYTE len);

    BYTE* getFMUMsgs(int index);
    
};

typedef enum
    {
        ack         = 0x00,
        nak,
        commSync,
        unSolMsg,
        loCom,
        timeSyncReq,
        timeRead,
        timeRsp,
        dataReqCmd,
        dataReqRsp,
        resetMsgDataLog,
        extDevDirCmd,
        extDevDirRsp
    } FMU_cmd_type;

}
}
}

#endif 