#pragma warning( disable : 4786 )

#ifndef __DEV_ILEX_H__
#define __DEV_ILEX_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_ilex
*
* Class:  CtiDeviceILEX
* Date:   12/18/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_welco.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/05/21 18:52:53 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"
#include "connection.h"


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
protected:

    BYTE _freezeNumber;
    BYTE _sequence;

private:

    INT header(PBYTE  Header, USHORT Function, USHORT SubFunction1, USHORT SubFunction2);

    BYTE CtiDeviceILEX::getFreezeNumber() const;
    CtiDeviceILEX& CtiDeviceILEX::setFreezeNumber(BYTE number);
    BYTE getIlexSequenceNumber() const;
    CtiDeviceILEX& setIlexSequenceNumber(BYTE number);

public:

    typedef CtiDeviceIDLC Inherited;

    CtiDeviceILEX();
    CtiDeviceILEX(const CtiDeviceILEX& aRef);
    virtual ~CtiDeviceILEX();

    CtiDeviceILEX& operator=(const CtiDeviceILEX& aRef);
    /*
     *  These guys initiate a scan based upon the type requested.
     */
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 3);
    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual INT ErrorDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
    virtual INT ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT exceptionScan(OUTMESS *&OutMessage, INT ScanPriority);

};
#endif // #ifndef __DEV_ILEX_H__
