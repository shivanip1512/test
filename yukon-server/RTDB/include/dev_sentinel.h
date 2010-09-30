
#pragma warning( disable : 4786)
#ifndef __DEV_SENTINEL_H__
#define __DEV_SENTINEL_H__


#include "dev_meter.h"
#include "dev_ansi.h"
#include "dlldefs.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"

class IM_EX_DEVDB CtiDeviceSentinel : public CtiDeviceAnsi
{

public:

   CtiDeviceSentinel();
   virtual ~CtiDeviceSentinel();

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

   virtual CtiProtocolANSI& getANSIProtocol( void );
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

    //UINT _parseFlags;
    string _result_string;

    unsigned long _lastLPTime;
};


#endif // #ifndef __DEV_SENTINEL_H__
