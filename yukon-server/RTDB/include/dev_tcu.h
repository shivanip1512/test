#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"

class IM_EX_DEVDB CtiDeviceTCU : public CtiDeviceIDLC
{
    typedef CtiDeviceIDLC Inherited;

    bool _sendFiller;

protected:

    CtiTime  LastPointRefresh;

public:

   CtiDeviceTCU();

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   YukonError_t TCUDecode(const INMESS &InMessage, const CtiTime ScanTime, CtiMessageList &retList);
   CtiReturnMsg* TCUDecodeStatus(const INMESS &InMessage);

   YukonError_t TCUControl(OUTMESS*, VSTRUCT*);
   YukonError_t TCUScanAll(OUTMESS*);
   YukonError_t TCULoop(OUTMESS*);

   YukonError_t IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority = MAXPRIORITY - 4) override;
   YukonError_t GeneralScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority = MAXPRIORITY - 4) override;

   YukonError_t ResultDecode(const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   CtiDeviceTCU& setSendFiller(bool yesno);
   bool getSendFiller() const;

   virtual INT getProtocolWrap() const;

};
