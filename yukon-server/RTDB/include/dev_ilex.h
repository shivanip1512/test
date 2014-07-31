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
private:

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
    virtual ~CtiDeviceILEX();

    /*
     *  These guys initiate a scan based upon the type requested.
     */
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3);
    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual INT ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, std::list<CtiMessage*>& retList);
    virtual INT ResultDecode(const INMESS*, CtiTime&, std::list< CtiMessage* >   &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT exceptionScan(OUTMESS *&OutMessage, INT ScanPriority, std::list< OUTMESS* > &outList);

};
