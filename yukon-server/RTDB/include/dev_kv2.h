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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceKV2(const CtiDeviceKV2&);
    CtiDeviceKV2& operator=(const CtiDeviceKV2&);

public:

    CtiDeviceKV2();

   virtual INT DemandReset( CtiRequestMsg *pReq,
                    CtiCommandParser &parse,
                    OUTMESS *&OutMessage,
                    std::list< CtiMessage* > &vgList,
                    std::list< CtiMessage* > &retList,
                    std::list< OUTMESS* > &outList,
                    INT ScanPriority = MAXPRIORITY-4);

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           std::list< CtiMessage* >  &vgList,
                           std::list< CtiMessage* >  &retList,
                           std::list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT ResultDecode(const INMESS               *InMessage,
                            CtiTime                    &TimeNow,
                            std::list< CtiMessage* > &vgList,
                            std::list< CtiMessage* > &retList,
                            std::list< OUTMESS* >    &outList);

   virtual INT ExecuteRequest( CtiRequestMsg         *pReq,
                       CtiCommandParser           &parse,
                       OUTMESS                   *&OutMessage,
                       std::list< CtiMessage* >  &vgList,
                       std::list< CtiMessage* >  &retList,
                       std::list< OUTMESS* >     &outList );


   Cti::Protocols::Ansi::CtiProtocolANSI & getKV2Protocol( void );
   CtiProtocolANSI_kv2 & getANSIProtocol( void );
   void processDispatchReturnMessage( std::list< CtiReturnMsg* >  &retList, UINT archiveFlag );
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
    std::string _result_string;
    unsigned long _lastLPTime;

};



