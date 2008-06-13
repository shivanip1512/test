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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/06/13 13:39:49 $
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

    OUTMESS *om;

protected:

    Protocol::Klondike _klondike;
    Protocol::Interface *getProtocol();

public:

    CCU721();
    virtual ~CCU721();

    enum Commands
    {
        Command_Loopback,
        Command_LoadQueue,
        Command_ReadQueue,
        Command_LoadRoutes,
    };

    virtual void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);

    void DecodeDatabaseReader(RWDBReader &rdr);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ErrorDecode (INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, bool &overrideExpectMore);
    INT ResultDecode(INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    //  these commands just indicate that messages are queued into the Yukon-side CCU device;
    //    it does not indicate whether the messages are currently loaded into the field device
    INT  queuedWorkCount() const;
    bool hasQueuedWork()   const;
    bool hasWaitingWork()  const;
    bool hasRemoteWork()   const;
    INT  queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);

    virtual LONG getAddress() const;

    bool buildCommand(CtiOutMessage *&OutMessage, Commands command);

    virtual int recvCommRequest(OUTMESS *OutMessage);
    virtual int sendCommResult (INMESS  *InMessage);

    void processInbound(const OUTMESS *om, INMESS *im);
};

typedef boost::shared_ptr<CCU721> CCU721SPtr;

}
}

#endif // #ifndef __DEV_LMI_H__
