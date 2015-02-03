#pragma once

#include "dev_paging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"
#include "encryption_oneway_message.h"


class IM_EX_DEVDB CtiDeviceTapPagingTerminal  : public Cti::Devices::DevicePaging,
                                                protected OneWayMsgEncryption
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceTapPagingTerminal(const CtiDeviceTapPagingTerminal&);
    CtiDeviceTapPagingTerminal& operator=(const CtiDeviceTapPagingTerminal&);

    typedef Cti::Devices::DevicePaging Inherited;

   UINT                          _idByteCount;  // How many bytes should a guy wait for after the CR (def. to 10)

   bool                          _sendFiller;

   CtiTime   _pacingTimeStamp;       // This is a timestamp from which we began the pacing process.
   int      _pagesPerMinute;      // This is a count of pages since the _pacingTimeStamp.         Used with CPARM: PAGING_BATCH_SIZE
   bool     _pacingReport;
   bool     _allowPrefix;

protected:

   std::queue< CtiVerificationBase * >  _verification_objects;
   UINT                          _pageCount;    // Used to count the number of pages sent out (0-n)
   CHAR                          _pagePrefix;   // Used to fake the TAPTERM into thining it is a new message (a-d)
   UINT                          _pageLength;
   CHAR                          *_pageBuffer;
   OUTMESS                       *_outMessage;

   std::string                     _inStr;

public:

   CtiDeviceTapPagingTerminal();
   virtual ~CtiDeviceTapPagingTerminal();

   CHAR  incrementPagePrefix();

   CHAR  getPagePrefix() const;
   CHAR& getPagePrefix();
   CtiDeviceTapPagingTerminal& setPagePrefix(const CHAR aCh);

   UINT  getPageCount() const;
   UINT& getPageCount();
   CtiDeviceTapPagingTerminal& setPageCount(const UINT aInt);

   UINT getPageLength() const;
   CtiDeviceTapPagingTerminal& setPageLength(const UINT aInt);

   BOOL  isValidPageBuffer() const;
   CHAR* getPageBuffer();
   CHAR  getPageBuffer(const INT i) const;

  // CtiDeviceTapPagingTerminal& setPageBuffer(const CHAR* copyBuffer, const INT len);

   CtiDeviceTapPagingTerminal& setSendFiller(bool yesno);
   bool getSendFiller() const;

   virtual ULONG getUniqueIdentifier() const;
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

   virtual CtiDeviceIED& setInitialState(const LONG oldid);

   INT traceOut(PCHAR Message, ULONG Count, CtiMessageList &traceList);
   INT traceIn(PCHAR Message, ULONG Count, CtiMessageList &traceList, BOOL CompletedMessage = FALSE);

   INT printChar( std::string &Str, CHAR Char );
   bool devicePacingExceeded();
   bool blockedByPageRate() const;
   bool allowPrefix() const;
   CtiDeviceTapPagingTerminal& setAllowPrefix(bool val = false);
   void updatePageCountData(UINT addition);

   virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

   void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);

};


#define TAPTIME_T1      2
#define TAPTIME_T2      1
#define TAPTIME_T3      10
#define TAPTIME_T4      4
#define TAPTIME_T5      8

#define TAPCOUNT_N1     3
#define TAPCOUNT_N2     3
#define TAPCOUNT_N3     3

#define CHAR_CR         0x0D
#define CHAR_LF         0x0A
#define CHAR_ESC        0x1B
#define CHAR_STX        0x02
#define CHAR_ETX        0x03
#define CHAR_US         0x1F
#define CHAR_ETB        0x17
#define CHAR_EOT        0x04
#define CHAR_SUB        0x1A
#define CHAR_ACK        0x06
#define CHAR_NAK        0x15
#define CHAR_RS         0x1e
