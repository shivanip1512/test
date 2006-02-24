/*-----------------------------------------------------------------------------*
*
* File:   dev_seriesv
*
* Class:  CtiDeviceSeriesV
* Date:   2004-jan-12
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2006/02/24 00:19:14 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SERIESV_H__
#define __DEV_SERIESV_H__
#pragma warning( disable : 4786 )

#include "dev_ied.h"
#include "prot_seriesv.h"

class IM_EX_DEVDB CtiDeviceSeriesV : public CtiDeviceIED
{
private:

    CtiProtocolSeriesV _seriesv;

protected:

public:

    Protocol::Interface *getProtocol();

    virtual INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList);

    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual INT ErrorDecode (INMESS *InMessage, CtiTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList);
    virtual INT ResultDecode(INMESS *InMessage, CtiTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList);
};


#endif // #ifndef __DEV_SERIESV_H__
