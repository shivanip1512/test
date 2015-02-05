#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "prot_welco.h"

class IM_EX_DEVDB CtiDeviceWelco : public CtiDeviceIDLC
{
   typedef CtiDeviceIDLC Inherited;

   int     _deadbandsSent;

public:

   CtiDeviceWelco();

   YukonError_t WelCoContinue    (OUTMESS *OutMessage, INT Priority);
   void         WelCoGetError    (OUTMESS *OutMessage, INT Priority);
   void         WelCoPoll        (OUTMESS *OutMessage, INT Priority);
   YukonError_t WelCoReset       (OUTMESS *OutMessage, INT Priority);
   YukonError_t WelCoTimeSync    (OUTMESS *OutMessage, INT Priority);

   YukonError_t WelCoDeadBands   (OUTMESS *OutMessage, OutMessageList &outList, INT Priority);

   YukonError_t WelCoDeadBands   (const INMESS  &InMessage,  OutMessageList &outList, INT Priority);
   YukonError_t WelCoTimeSync    (const INMESS  &InMessage,  OutMessageList &outList, INT Priority);

   bool getDeadbandsSent() const;
   void incDeadbandsSent();
   CtiDeviceWelco& setDeadbandsSent(const bool b);

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                        INT ScanPriority = MAXPRIORITY - 4) override;
   YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                        INT ScanPriority = MAXPRIORITY - 3) override;
   YukonError_t IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                        INT ScanPriority = MAXPRIORITY - 4) override;

   YukonError_t ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList) override;
   YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   virtual YukonError_t executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

   virtual bool clearedForScan(int scantype);
   virtual void resetForScan(int scantype);

};
