/*-----------------------------------------------------------------------------*
*
* File:   dev_lmi
*
* Class:  CtiDeviceLMI
* Date:   2004-feb-02
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/05/24 17:48:38 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LMI_H__
#define __DEV_LMI_H__
#pragma warning( disable : 4786 )

#include "dev_remote.h"
#include "tbl_dv_address.h"
#include "prot_lmi.h"

class IM_EX_DEVDB CtiDeviceLMI : public CtiDeviceRemote
{
private:

    CtiTableDeviceAddress _address;

    CtiProtocolLMI _lmi;
    typedef CtiDeviceRemote Inherited;

protected:

public:

    CtiProtocolBase* getProtocol() const;

    void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);

    void DecodeDatabaseReader(RWDBReader &rdr);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 3);
    INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ErrorDecode (INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
    INT ResultDecode(INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);

    void processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &dnpPoints );

    bool hasQueuedWork() const;
    INT queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    bool getOutMessage(CtiOutMessage *&OutMessage);
    LONG deviceQueueCommunicationTime() const;
    LONG deviceMaxCommunicationTime() const;
};


#endif // #ifndef __DEV_LMI_H__
