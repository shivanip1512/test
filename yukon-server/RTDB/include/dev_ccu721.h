/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu721
*
* Namespace:  Cannon::Device
* Class:      CCU721
* Date:       2006-aug-08
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/09/04 17:10:16 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_CCU721_H__
#define __DEV_CCU721_H__
#pragma warning( disable : 4786 )

#include "dev_remote.h"
#include "tbl_dv_address.h"
#include "prot_klondike.h"

namespace Cti       {
namespace Device    {

class IM_EX_DEVDB CCU721 : public CtiDeviceRemote
{
private:

    typedef CtiDeviceRemote Inherited;

    CtiTableDeviceAddress _address;

protected:

    Protocol::Klondike _klondike;
    Protocol::Interface *getProtocol();

public:

    CCU721();
    virtual ~CCU721();

    virtual void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);

    void DecodeDatabaseReader(RWDBReader &rdr);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ErrorDecode (INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);
    INT ResultDecode(INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    INT  queuedWorkCount() const;
    bool hasQueuedWork()   const;

    INT  queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);
    bool getOutMessage(CtiOutMessage *&OutMessage);

    virtual int recvCommRequest(OUTMESS *OutMessage);
    virtual int sendCommResult (INMESS  *InMessage);

    virtual int sendCommRequest(OUTMESS *&OutMessage, list<OUTMESS *> &outList);
    virtual int recvCommResult (INMESS   *InMessage,  list<OUTMESS *> &outList);
};

}
}

#endif // #ifndef __DEV_LMI_H__
