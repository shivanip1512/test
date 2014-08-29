
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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceAnsi(const CtiDeviceAnsi&);
    CtiDeviceAnsi& operator=(const CtiDeviceAnsi&);

public:
   CtiDeviceAnsi();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           std::list< CtiMessage* >  &vgList,
                           std::list< CtiMessage* >  &retList,
                           std::list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);

   virtual INT executeLoopback(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           std::list<CtiMessage*>&vgList,
                           std::list<CtiMessage*>&retList,
                           std::list<OUTMESS*>&outList);
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

   virtual void processDispatchReturnMessage( std::list< CtiReturnMsg* > &retList, UINT archiveFlag );
   virtual unsigned long updateLastLpTime();

   virtual Cti::Protocols::Ansi::CtiProtocolANSI& getANSIProtocol( void ) = 0;
   virtual int buildScannerTableRequest (BYTE *ptr, UINT flags) = 0;
   virtual int buildCommanderTableRequest (BYTE *ptr, UINT flags) = 0;
   virtual int buildSingleTableRequest(BYTE *ptr, UINT tableId = 0);

   YukonError_t sendCommResult( INMESS *InMessage);

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


