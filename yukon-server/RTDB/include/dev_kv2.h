#pragma once
#include <string>

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi_kv2.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"

using std::string;

class IM_EX_DEVDB CtiDeviceKV2 : public CtiDeviceMeter
{

public:

   CtiDeviceKV2();
   virtual ~CtiDeviceKV2();

   virtual INT DemandReset( CtiRequestMsg *pReq,
                    CtiCommandParser &parse,
                    OUTMESS *&OutMessage,
                    list< CtiMessage* > &vgList,
                    list< CtiMessage* > &retList,
                    list< OUTMESS* > &outList,
                    INT ScanPriority = MAXPRIORITY-4);

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           list< CtiMessage* >  &vgList,
                           list< CtiMessage* >  &retList,
                           list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT ResultDecode(INMESS                    *InMessage,
                            CtiTime                    &TimeNow,
                            list< CtiMessage* > &vgList,
                            list< CtiMessage* > &retList,
                            list< OUTMESS* >    &outList);

   virtual INT ExecuteRequest( CtiRequestMsg         *pReq,
                       CtiCommandParser           &parse,
                       OUTMESS                   *&OutMessage,
                       list< CtiMessage* >  &vgList,
                       list< CtiMessage* >  &retList,
                       list< OUTMESS* >     &outList );


   Cti::Protocols::Ansi::CtiProtocolANSI & getKV2Protocol( void );
   CtiProtocolANSI_kv2 & getANSIProtocol( void );
   void processDispatchReturnMessage( list< CtiReturnMsg* >  &retList, UINT archiveFlag );
   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);
   INT sendCommResult( INMESS *InMessage);

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:
    CtiProtocolANSI_kv2   _ansiProtocol;

    UINT _parseFlags;
    string _result_string;
    unsigned long _lastLPTime;

};



