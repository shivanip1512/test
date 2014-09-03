#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "dev_ied.h"

class IM_EX_DEVDB CtiDeviceDavis : public CtiDeviceIED
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceDavis(const CtiDeviceDavis&);
    CtiDeviceDavis& operator=(const CtiDeviceDavis&);

    typedef CtiDeviceRemote Inherited;

    INT generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = 14);

public:

   CtiDeviceDavis();

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
   INT ErrorDecode (const INMESS & InMessage, const CtiTime TimeNow, CtiMessageList &retList) override;
   INT ResultDecode(const INMESS & InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
   INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   static std::string getPointOffsetName(int offset);
};
