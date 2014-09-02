#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"

class IM_EX_DEVDB CtiDeviceTCU : public CtiDeviceIDLC
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceTCU(const CtiDeviceTCU&);
    CtiDeviceTCU& operator=(const CtiDeviceTCU&);

    typedef CtiDeviceIDLC Inherited;

    bool _sendFiller;

protected:

    CtiTime  LastPointRefresh;

public:

   CtiDeviceTCU();

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   INT               TCUDecode(const INMESS &InMessage, const CtiTime ScanTime, CtiMessageList &retList);
   CtiReturnMsg*  TCUDecodeStatus(const INMESS &InMessage);

   INT               TCUControl(OUTMESS*, VSTRUCT*);
   INT               TCUScanAll(OUTMESS*);
   INT               TCULoop(OUTMESS*);

   virtual INT IntegrityScan(CtiRequestMsg *pReq,
                             CtiCommandParser &parse,
                             OUTMESS *&OutMessage,
                             CtiMessageList &vgList,
                             CtiMessageList &retList,
                             OutMessageList &outList,
                             INT ScanPriority = MAXPRIORITY - 4);
   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           CtiMessageList &vgList,
                           CtiMessageList &retList,
                           OutMessageList &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
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

   CtiDeviceTCU& setSendFiller(bool yesno);
   bool getSendFiller() const;

   virtual INT getProtocolWrap() const;

};
