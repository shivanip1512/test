
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
   virtual ~CtiDeviceSentinel();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           std::list< CtiMessage* >  &vgList,
                           std::list< CtiMessage* >  &retList,
                           std::list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);
   virtual INT DemandReset( CtiRequestMsg *pReq,
                    CtiCommandParser &parse,
                    OUTMESS *&OutMessage,
                    std::list< CtiMessage* > &vgList,
                    std::list< CtiMessage* > &retList,
                    std::list< OUTMESS* > &outList,
                    INT ScanPriority = MAXPRIORITY-4);


   virtual INT ResultDecode(const INMESS                    *InMessage,
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

   virtual Cti::Protocols::Ansi::CtiProtocolANSI& getANSIProtocol( void );
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
    CtiProtocolANSI_sentinel   _ansiProtocol;

    unsigned long _lastLPTime;
};

}
}

