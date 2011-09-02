#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "dev_ied.h"

class IM_EX_DEVDB CtiDeviceDavis : public CtiDeviceIED
{
private:

    typedef CtiDeviceRemote Inherited;

    INT generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = 14);

protected:

public:

   CtiDeviceDavis();
   CtiDeviceDavis(const CtiDeviceDavis& aRef);
   virtual ~CtiDeviceDavis();

   CtiDeviceDavis& operator=(const CtiDeviceDavis& aRef);
   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ErrorDecode(const INMESS & InMessage, const CtiTime TimeNow, std::list<CtiMessage*>& retList);
   virtual INT ResultDecode(INMESS*, CtiTime&, std::list< CtiMessage* >   &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

   static std::string getPointOffsetName(int offset);
};
