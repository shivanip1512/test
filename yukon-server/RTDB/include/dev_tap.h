/*-----------------------------------------------------------------------------*
*
* File:   dev_tap
*
* Class:  CtiDeviceTapPagingTerminal
* Date:   5/9/2000
*
* Author: Corey G. Plender
*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_tap.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/03/23 20:42:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_TAP_H__
#define __DEV_TAP_H__
#pragma warning( disable : 4786)


#include <rw\cstring.h>
#include <rw\thr\mutex.h>

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"


class IM_EX_DEVDB CtiDeviceTapPagingTerminal  : public CtiDeviceIED
{
protected:

   CtiTableDeviceTapPaging       _tap;

   UINT                          _pageCount;    // Used to count the number of pages sent out (0-n)
   CHAR                          _pagePrefix;   // Used to fake the TAPTERM into thining it is a new message (a-d)
   UINT                          _pageLength;
   CHAR                          *_pageBuffer;

   RWCString                     _inStr;

private:

   UINT                          _idByteCount;  // How many bytes should a guy wait for after the CR (def. to 10)

   bool                          _sendFiller;

   RWTime   _pacingTimeStamp;       // This is a timestamp from which we began the pacing process.
   int      _pagesPerMinute;      // This is a count of pages since the _pacingTimeStamp.         Used with CPARM: PAGING_BATCH_SIZE
   bool     _pacingReport;
   bool     _allowPrefix;

public:

   typedef CtiDeviceIED Inherited;

   CtiDeviceTapPagingTerminal();
   CtiDeviceTapPagingTeminal(const CtiDeviceTapPagingTerminal& aRef);
   virtual ~CtiDeviceTapPagingTerminal();

   CtiDeviceTapPagingTerminal& operator=(const CtiDeviceTapPagingTerminal& aRef);

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
   CtiDeviceTapPagingTerminal& setPageBuffer(const CHAR* copyBuffer, const INT len);

   CtiTableDeviceTapPaging    getTap() const;//       { return _tap; }
   CtiTableDeviceTapPaging&   getTap();//             { return _tap; }

   CtiDeviceTapPagingTerminal& setTap(const CtiTableDeviceTapPaging& aTap);
   CtiDeviceTapPagingTerminal& setSendFiller(bool yesno);
   bool getSendFiller() const;


   virtual ULONG getUniqueIdentifier() const;
   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   RWCString getDescription(const CtiCommandParser & parse) const;

   virtual INT decodeResponseHandshake(CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT generateCommandHandshake(CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT generateCommandDisconnect (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT decodeResponseDisconnect (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT generateCommand(CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT decodeResponse(CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT allocateDataBins (OUTMESS *outMess);
   virtual INT freeDataBins();

   virtual CtiDeviceIED& setInitialState(const LONG oldid);

   INT traceOut(PCHAR Message, ULONG Count, RWTPtrSlist< CtiMessage > &traceList);
   INT traceIn(PCHAR Message, ULONG Count, RWTPtrSlist< CtiMessage > &traceList, BOOL CompletedMessage = FALSE);

   INT printChar( RWCString &Str, CHAR Char );
   bool devicePacingExceeded();
   bool blockedByPageRate() const;
   bool allowPrefix() const;
   CtiDeviceTapPagingTerminal& setAllowPrefix(bool val = false);
   void updatePageCountData(UINT addition);

   virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

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





#endif // #ifndef __DEV_TAP_H__
