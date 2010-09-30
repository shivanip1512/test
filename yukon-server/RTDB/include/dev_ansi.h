
#pragma warning( disable : 4786)
#ifndef __DEV_ANSI_H__
#define __DEV_ANSI_H__


#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_ansi.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"
using namespace Ansi;


class IM_EX_DEVDB CtiDeviceAnsi : public CtiDeviceMeter
{

public:
   CtiDeviceAnsi();
   virtual ~CtiDeviceAnsi();

   virtual INT GeneralScan(CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                    *&OutMessage,
                           list< CtiMessage* >  &vgList,
                           list< CtiMessage* >  &retList,
                           list< OUTMESS* >     &outList,
                           INT                        ScanPriority=MAXPRIORITY-4);
   virtual INT DemandReset( CtiRequestMsg *pReq,
                    CtiCommandParser &parse,
                    OUTMESS *&OutMessage,
                    list< CtiMessage* > &vgList,
                    list< CtiMessage* > &retList,
                    list< OUTMESS* > &outList,
                    INT ScanPriority = MAXPRIORITY-4);


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

   
   virtual CtiProtocolANSI& getANSIProtocol( void ) = 0;
   virtual void processDispatchReturnMessage( list< CtiReturnMsg* > &retList, UINT archiveFlag );
   virtual int buildScannerTableRequest (BYTE *ptr, UINT flags) = 0;
   virtual int buildCommanderTableRequest (BYTE *ptr, UINT flags) = 0;
   INT sendCommResult( INMESS *InMessage);

   struct WANTS_HEADER
   {
      unsigned long  lastLoadProfileTime;
      int            numTablesRequested;
      int            command;
   };


private:

    //UINT _parseFlags;
    string _result_string;

    unsigned long _lastLPTime;
};


#endif // #ifndef 
