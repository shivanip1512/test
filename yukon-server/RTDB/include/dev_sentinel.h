
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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceSentinel(const CtiDeviceSentinel&);
    CtiDeviceSentinel& operator=(const CtiDeviceSentinel&);

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
   YukonError_t sendCommResult( INMESS &InMessage);

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

