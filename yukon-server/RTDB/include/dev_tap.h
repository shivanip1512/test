#pragma once

#include "dev_paging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"
#include "encryption_oneway_message.h"


namespace Cti {
namespace Devices {

class IM_EX_DEVDB TapPagingTerminal : public DevicePaging, protected OneWayMsgEncryption
{
   typedef DevicePaging Inherited;

   UINT     _idByteCount;  // How many bytes should a guy wait for after the CR (def. to 10)

   CtiTime  _pacingTimeStamp;     // This is a timestamp from which we began the pacing process.
   int      _pagesPerMinute;      // This is a count of pages since the _pacingTimeStamp.         Used with CPARM: PAGING_BATCH_SIZE
   bool     _pacingReport;
   bool     _allowPrefix;

   std::queue< CtiVerificationBase * >  _verification_objects;
   UINT      _pageCount;    // Used to count the number of pages sent out (0-n)
   CHAR      _pagePrefix;   // Used to fake the TAPTERM into thining it is a new message (a-d)
   UINT      _pageLength;
   CHAR     *_pageBuffer;
   OUTMESS  *_outMessage;

   std::string _inStr;

protected:

   CHAR  incrementPagePrefix();
   CHAR  getPageBuffer(const INT i) const;

   INT traceOut(PCHAR Message, ULONG Count, CtiMessageList &traceList);
   INT traceIn(PCHAR Message, ULONG Count, CtiMessageList &traceList, BOOL CompletedMessage = FALSE);

public:

   TapPagingTerminal();
   virtual ~TapPagingTerminal();

   bool getSendFiller() const;

   ULONG getUniqueIdentifier() const override;
   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   std::string getDescription(const CtiCommandParser & parse) const;

   YukonError_t decodeResponseHandshake(CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t generateCommandHandshake(CtiXfer  &Transfer, CtiMessageList &traceList) override;

   YukonError_t generateCommandDisconnect (CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t decodeResponseDisconnect (CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;

   YukonError_t generateCommand(CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t decodeResponse(CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;

   INT allocateDataBins (OUTMESS *outMess) override;
   INT freeDataBins() override;

   CtiDeviceIED& setInitialState(const LONG oldid) override;

   INT printChar( std::string &Str, CHAR Char );
   bool devicePacingExceeded();
   bool blockedByPageRate() const;
   void setAllowPrefix(bool val);
   void updatePageCountData(UINT addition);

   CtiMessage* rsvpToDispatch(bool clearMessage = true) override;

   void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);

};

}
}
