#pragma warning( disable : 4786)
#ifndef __DEV_DNP_H__
#define __DEV_DNP_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Class:  CtiDeviceDNP
* Date:   8/05/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/10/09 19:46:59 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_remote.h"
#include "prot_dnp.h"
#include "tbl_dv_dnp.h"

class IM_EX_DEVDB CtiDeviceDNP : public CtiDeviceRemote
{
protected:

    CtiProtocolDNP    _dnp;
    CtiTableDeviceDNP _dnpAddress;

private:

public:

    typedef CtiDeviceRemote Inherited;

    CtiDeviceDNP();
    CtiDeviceDNP(const CtiDeviceDNP& aRef);
    virtual ~CtiDeviceDNP();

    CtiDeviceDNP& operator=(const CtiDeviceDNP& aRef);

    virtual RWCString getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    CtiProtocolBase *getProtocol() const;

    //  virtual in case devices need to form up different DNP requests for the same command ("control open", for example)
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT ErrorDecode (INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual void processInboundPoints(RWTPtrSlist<CtiPointDataMsg> &dnpPoints, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};


#endif // #ifndef __DEV_CBC_H__
