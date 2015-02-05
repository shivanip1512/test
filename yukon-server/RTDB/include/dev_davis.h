#pragma once

#include <windows.h>
#include "dev_ied.h"

class IM_EX_DEVDB CtiDeviceDavis : public CtiDeviceIED
{
    typedef CtiDeviceRemote Inherited;

    YukonError_t generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = 14);

public:

   CtiDeviceDavis();

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
   YukonError_t ErrorDecode (const INMESS & InMessage, const CtiTime TimeNow, CtiMessageList &retList) override;
   YukonError_t ResultDecode(const INMESS & InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   static std::string getPointOffsetName(int offset);
};
