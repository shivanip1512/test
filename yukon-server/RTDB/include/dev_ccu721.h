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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/03/31 21:17:35 $
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

    OUTMESS _current_outmessage;

protected:

    Protocol::Klondike _klondike;
    Protocol::Interface *getProtocol();

public:

    CCU721();
    virtual ~CCU721();

    enum Commands
    {
        Command_Loopback,
        Command_WriteQueue,
        Command_ReadQueue,
    };

    virtual void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);

    void DecodeDatabaseReader(RWDBReader &rdr);

    INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ErrorDecode (INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);
    INT ResultDecode(INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList);

    //  these commands just indicate that the device hasn't sent back an INMESS yet;
    //    it says nothing about whether the messages are currently loaded into the CCU-721
    INT  queuedWorkCount() const;
    bool hasQueuedWork()   const;
    INT  queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt);

    virtual LONG getAddress() const;

    bool buildCommand(CtiOutMessage *&OutMessage, Commands command);

    virtual int recvCommRequest(OUTMESS *OutMessage);
    virtual int sendCommResult (INMESS  *InMessage);

    virtual int sendCommRequest(OUTMESS *&OutMessage, list<OUTMESS *> &outList);
    virtual int recvCommResult (INMESS   *InMessage,  list<OUTMESS *> &outList);
};

}
}

#endif // #ifndef __DEV_LMI_H__
