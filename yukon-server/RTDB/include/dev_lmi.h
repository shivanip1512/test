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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/05/11 18:31:25 $
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

    virtual CtiProtocolBase* getProtocol() const;

    virtual void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 3);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual INT ErrorDecode (INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
    virtual INT ResultDecode(INMESS *InMessage, RWTime &Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
};


#endif // #ifndef __DEV_LMI_H__
