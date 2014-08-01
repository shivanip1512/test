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

    INT generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = 14);

protected:

public:

   CtiDeviceDavis();
   virtual ~CtiDeviceDavis();

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ErrorDecode(const INMESS & InMessage, const CtiTime TimeNow, std::list<CtiMessage*>& retList);
   virtual INT ResultDecode(const INMESS*, CtiTime&, std::list< CtiMessage* >   &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   static std::string getPointOffsetName(int offset);
};
