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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/06/06 20:28:44 $
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

    Cti::Protocol::Interface *getProtocol();

    virtual INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual INT ResultDecode(INMESS *InMessage, CtiTime &Now, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};


#endif // #ifndef __DEV_SERIESV_H__
