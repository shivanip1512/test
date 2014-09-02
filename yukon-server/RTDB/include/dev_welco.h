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
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceWelco(const CtiDeviceWelco&);
    CtiDeviceWelco& operator=(const CtiDeviceWelco&);

   typedef CtiDeviceIDLC Inherited;

   int     _deadbandsSent;

public:

   CtiDeviceWelco();

   INT WelCoContinue    (OUTMESS *OutMessage, INT Priority);
   INT WelCoGetError    (OUTMESS *OutMessage, INT Priority);
   INT WelCoPoll        (OUTMESS *OutMessage, INT Priority);
   INT WelCoReset       (OUTMESS *OutMessage, INT Priority);
   INT WelCoTimeSync    (OUTMESS *OutMessage, INT Priority);

   INT WelCoDeadBands   (OUTMESS *OutMessage, OutMessageList &outList, INT Priority);

   INT WelCoDeadBands   (const INMESS  &InMessage,  OutMessageList &outList, INT Priority);
   INT WelCoTimeSync    (const INMESS  &InMessage,  OutMessageList &outList, INT Priority);

   bool getDeadbandsSent() const;
   void incDeadbandsSent();
   CtiDeviceWelco& setDeadbandsSent(const bool b);

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           CtiMessageList &vgList,
                           CtiMessageList &retList,
                           OutMessageList &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT AccumulatorScan(CtiRequestMsg *pReq,
                               CtiCommandParser &parse,
                               OUTMESS *&OutMessage,
                               CtiMessageList &vgList,
                               CtiMessageList &retList,
                               OutMessageList &outList,
                               INT ScanPriority = MAXPRIORITY - 3);
   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             CtiMessageList &vgList,
                             CtiMessageList &retList,
                             OutMessageList &outList,
                             INT ScanPriority = MAXPRIORITY - 4);

   virtual INT ErrorDecode(const INMESS      &InMessage,
                           const CtiTime      TimeNow,
                           CtiMessageList &retList);

   virtual INT ResultDecode(const INMESS&,
                            const CtiTime,
                            CtiMessageList   &vgList,
                            CtiMessageList &retList,
                            OutMessageList &outList);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              CtiMessageList      &vgList,
                              CtiMessageList      &retList,
                              OutMessageList         &outList);

   virtual INT executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

   virtual bool clearedForScan(int scantype);
   virtual void resetForScan(int scantype);

};
