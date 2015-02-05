
#pragma once
#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"
#include "pt_analog.h"
#include "pt_status.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CtiDeviceAnsi : public CtiDeviceMeter
{
public:
   CtiDeviceAnsi();

   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority=MAXPRIORITY-4) override;

   YukonError_t executeLoopback(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

   virtual YukonError_t DemandReset(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                            INT ScanPriority=MAXPRIORITY-4);

   YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

   virtual void processDispatchReturnMessage( std::list< CtiReturnMsg* > &retList, UINT archiveFlag );
   virtual unsigned long updateLastLpTime();

   virtual Cti::Protocols::Ansi::CtiProtocolANSI& getANSIProtocol( void ) = 0;
   virtual void buildScannerTableRequest  (BYTE *ptr, UINT flags) = 0;
   virtual void buildCommanderTableRequest(BYTE *ptr, UINT flags) = 0;
   virtual void buildSingleTableRequest(BYTE *ptr, UINT tableId = 0);

   YukonError_t sendCommResult( INMESS &InMessage) override;

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };

protected:

   std::string _result_string;

private:

    void createLoadProfilePointData(const CtiPointAnalog &analogPoint, std::list< CtiReturnMsg* > &retList);
    void createPointData(const CtiPointAnalog &analogPoint, double value, double timestamp,unsigned int archiveFlag, std::list< CtiReturnMsg* > &retList);

    unsigned long _lastLPTime;
};

}}


