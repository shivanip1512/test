
#pragma once
#include "dev_meter.h"
#include "dev_ansi.h"
#include "dlldefs.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CtiDeviceSentinel : public CtiDeviceAnsi
{
public:

   CtiDeviceSentinel();

   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority = MAXPRIORITY-4) override;
   virtual YukonError_t DemandReset(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                            INT ScanPriority = MAXPRIORITY-4);


   YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   virtual Cti::Protocols::Ansi::CtiProtocolANSI& getANSIProtocol( void );
   void buildScannerTableRequest  (BYTE *ptr, UINT flags) override;
   void buildCommanderTableRequest(BYTE *ptr, UINT flags) override;
   YukonError_t sendCommResult( INMESS &InMessage) override;

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:
    CtiProtocolANSI_sentinel   _ansiProtocol;

    unsigned long _lastLPTime;
};

}
}

