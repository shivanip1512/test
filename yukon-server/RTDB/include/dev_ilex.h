#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"


/* ILEX Message definitions */
#define ILEXGLOBAL  0


/* ILEX command definitions */
#define ILEXNODATA          0x00
#define ILEXSCAN            0x01
#define ILEXFREEZE          0x02
#define ILEXSBOSELECT       0x02
#define ILEXSBOEXECUTE      0x03
#define ILEXSCANPARTIAL     0x05
#define FORCED_SCAN         0X01
#define EXCEPTION_SCAN      0x00
#define ILEXTIMESYNC        0x06
#define TIMESYNC1           0x01
#define TIMESYNC2           0x00
#define ILEXGETTIME         0x01
#define ILEXSETTIME         0x02
#define ILEXCORRECTTIME     0x03


/* Lengths */
#define ILEXHEADERLEN        2
#define ILEXTIMELENGTH      10


class IM_EX_DEVDB CtiDeviceILEX : public CtiDeviceIDLC
{
    typedef CtiDeviceIDLC Inherited;

    INT header(PBYTE  Header, USHORT Function, USHORT SubFunction1, USHORT SubFunction2);

    BYTE CtiDeviceILEX::getFreezeNumber() const;
    CtiDeviceILEX& CtiDeviceILEX::setFreezeNumber(BYTE number);
    BYTE getIlexSequenceNumber() const;
    CtiDeviceILEX& setIlexSequenceNumber(BYTE number);

protected:

    BYTE _freezeNumber;
    BYTE _sequence;

public:

    CtiDeviceILEX();

    /*
     *  These guys initiate a scan based upon the type requested.
     */
    YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 3) override;
    YukonError_t IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

    YukonError_t ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList& retList) override;
    YukonError_t ResultDecode(const INMESS&, const CtiTime, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    YukonError_t executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t exceptionScan(OUTMESS *&OutMessage, INT ScanPriority, OutMessageList &outList);

};
