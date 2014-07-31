#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "prot_welco.h"

class IM_EX_DEVDB CtiDeviceWelco : public CtiDeviceIDLC
{
private:

   typedef CtiDeviceIDLC Inherited;

   int     _deadbandsSent;

protected:

public:

   CtiDeviceWelco();
   virtual ~CtiDeviceWelco();

   INT WelCoContinue    (OUTMESS *OutMessage, INT Priority);
   INT WelCoGetError    (OUTMESS *OutMessage, INT Priority);
   INT WelCoPoll        (OUTMESS *OutMessage, INT Priority);
   INT WelCoReset       (OUTMESS *OutMessage, INT Priority);
   INT WelCoTimeSync    (OUTMESS *OutMessage, INT Priority);

   INT WelCoDeadBands   (OUTMESS *OutMessage, std::list< OUTMESS* > &outList, INT Priority);

   INT WelCoDeadBands   (const INMESS  *InMessage,  std::list< OUTMESS* > &outList, INT Priority);
   INT WelCoTimeSync    (const INMESS  *InMessage,  std::list< OUTMESS* > &outList, INT Priority);

   bool getDeadbandsSent() const;
   void incDeadbandsSent();
   CtiDeviceWelco& setDeadbandsSent(const bool b);

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           std::list< CtiMessage* > &vgList,
                           std::list< CtiMessage* > &retList,
                           std::list< OUTMESS* > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT AccumulatorScan(CtiRequestMsg *pReq,
                               CtiCommandParser &parse,
                               OUTMESS *&OutMessage,
                               std::list< CtiMessage* > &vgList,
                               std::list< CtiMessage* > &retList,
                               std::list< OUTMESS* > &outList,
                               INT ScanPriority = MAXPRIORITY - 3);
   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             std::list< CtiMessage* > &vgList,
                             std::list< CtiMessage* > &retList,
                             std::list< OUTMESS* > &outList,
                             INT ScanPriority = MAXPRIORITY - 4);

   virtual INT ErrorDecode(const INMESS      &InMessage,
                           const CtiTime      TimeNow,
                           std::list<CtiMessage*> &retList);

   virtual INT ResultDecode(const INMESS*,
                            CtiTime&,
                            std::list< CtiMessage* >   &vgList,
                            std::list< CtiMessage* > &retList,
                            std::list< OUTMESS* > &outList);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   virtual bool clearedForScan(int scantype);
   virtual void resetForScan(int scantype);

};
