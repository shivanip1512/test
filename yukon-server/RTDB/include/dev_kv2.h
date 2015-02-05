#pragma once
#include <string>

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi_kv2.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"

class IM_EX_DEVDB CtiDeviceKV2 : public CtiDeviceMeter
{
public:

    CtiDeviceKV2();

   virtual YukonError_t DemandReset(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                            INT ScanPriority = MAXPRIORITY-4);

   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                    INT ScanPriority=MAXPRIORITY-4) override;

   YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

   Cti::Protocols::Ansi::CtiProtocolANSI & getKV2Protocol( void );
   CtiProtocolANSI_kv2 & getANSIProtocol( void );
   void processDispatchReturnMessage( std::list< CtiReturnMsg* >  &retList, UINT archiveFlag );
   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);
   YukonError_t sendCommResult( INMESS &InMessage) override;

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:
    CtiProtocolANSI_kv2   _ansiProtocol;

    UINT _parseFlags;
    std::string _result_string;
    unsigned long _lastLPTime;

};



