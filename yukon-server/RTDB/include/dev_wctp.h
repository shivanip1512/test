/*-----------------------------------------------------------------------------*
*
* File:   dev_wctp
*
* Class:  CtiDeviceWctpTerminal
* Date:   5/29/2002
*
* Author: Zhihong Yao
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_WCTP_H__
#define __DEV_WCTP_H__
#pragma warning( disable : 4786)


#include <rw\thr\mutex.h>

#include <xercesc/sax2/DefaultHandler.hpp>
#include <xercesc/sax2/Attributes.hpp>
#include <xercesc/sax2/SAX2XMLReader.hpp>

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"

#define WCTP_TIMEOUT    3

class SAXWctpHandler;

class IM_EX_DEVDB CtiDeviceWctpTerminal  : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;

    CHAR            *readLinePtr;

    SAX2XMLReader   *parser;            // Parser to parse the WCTP response message
    SAXWctpHandler  *handler;           // WCTP response message handler

    BOOL            statusParsed;       // Whether the HTTP response status line has been parsed
    BOOL            headerParsed;       // Whether the HTTP response headers has been parsed
    INT             timeEllapsed;       // Time ellapsed before WCTP response message has been completely received

    bool _sendFiller;

    CtiTime   _pacingTimeStamp;       // This is a timestamp from which we began the pacing process.  Used with CPARM: PAGING_BATCH_WINDOW
    int      _pagesPerMinute;        // This is a count of pages since the _pacingTimeStamp.         Used with CPARM: PAGING_BATCH_SIZE
    bool     _pacingReport;

    bool     _allowPrefix;

protected:

   CtiTableDeviceTapPaging      _wctp;          // Use the same class as a TapPagingTerminal for now

   queue< CtiVerificationBase * >  _verification_objects;
   UINT                         _pageCount;    // Used to count the number of pages sent out (0-n)
   CHAR                         _pagePrefix;   // Used to fake the WCTPTERM into thining it is a new message (a-d)
   UINT                         _pageLength;
   CHAR*                        _pageBuffer;
   OUTMESS                      *_outMessage;

   string                    _inStr;

   CHAR*                        _outBuffer;     // Use our own buffer because WCTP message could be as long as 1024 bytes
   CHAR*                        _inBuffer;
   CHAR*                        _xmlBuffer;     // Buffer for XML message

public:

   CtiDeviceWctpTerminal();
   CtiDeviceWctpTeminal(const CtiDeviceWctpTerminal& aRef);
   virtual ~CtiDeviceWctpTerminal();

   CtiDeviceWctpTerminal& operator=(const CtiDeviceWctpTerminal& aRef);

   CHAR  incrementPagePrefix();

   CHAR  getPagePrefix() const;
   CHAR& getPagePrefix();
   CtiDeviceWctpTerminal& setPagePrefix(const CHAR aCh);

   UINT  getPageCount() const;
   UINT& getPageCount();
   CtiDeviceWctpTerminal& setPageCount(const UINT aInt);

   UINT getPageLength() const;
   CtiDeviceWctpTerminal& setPageLength(const UINT aInt);

   BOOL  isValidPageBuffer() const;
   CHAR* getPageBuffer();
   CHAR  getPageBuffer(const INT i) const;
   //CtiDeviceWctpTerminal& setPageBuffer(const CHAR* copyBuffer, const INT len);

   CHAR* getOutBuffer();
   CHAR* getInBuffer();
   CHAR* getXMLBuffer();
   void  destroyBuffers();

   CtiTableDeviceTapPaging    getWctp() const;//       { return _wctp; }
   CtiTableDeviceTapPaging&   getWctp();//             { return _wctp; }

   CtiDeviceWctpTerminal& setWctp(const CtiTableDeviceTapPaging& aWctp);


   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList);

   string getDescription(const CtiCommandParser & parse) const;

   virtual INT generateCommand(CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT decodeResponse(CtiXfer &Transfer, INT commReturnValue, list< CtiMessage* > &traceList);

   virtual INT allocateDataBins (OUTMESS *outMess);
   virtual INT freeDataBins();

   virtual CtiDeviceIED& setInitialState(const LONG oldid);

   INT traceOut(PCHAR Message, ULONG Count, list< CtiMessage* > &traceList);
   INT traceIn(PCHAR Message, ULONG Count, list< CtiMessage* > &traceList, BOOL CompletedMessage = FALSE);

   CtiDeviceWctpTerminal& setSendFiller(bool yesno);
   bool getSendFiller() const;
   bool devicePacingExceeded();
   bool allowPrefix() const;
   CtiDeviceWctpTerminal& setAllowPrefix(bool val = false);
   void updatePageCountData(UINT addition);

   virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

   void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);

private:

   CHAR* replaceChars(const CHAR *src, CHAR *dst);
   CHAR* buildXMLMessage(const CHAR* recipientId, const CHAR* senderId, const CHAR* msgPayload, const CHAR* timeStamp, const CHAR *prefix, const CHAR *securityCode=0);

   INT   readLine(CHAR* str, CHAR* buf, INT bufLen);
   CHAR* getReadLinePtr()
   {
       return readLinePtr;
   }

   SAX2XMLReader*  getSAXParser();
   SAXWctpHandler* getWctpHandler();

   CHAR* removeDocType(const CHAR *src, CHAR *dst);

   CHAR* trimMessage(CHAR *message);

};



class SAXWctpHandler : public DefaultHandler
{
public:
    // -----------------------------------------------------------------------
    //  Constructors and Destructor
    // -----------------------------------------------------------------------
    SAXWctpHandler();
    ~SAXWctpHandler();


    // -----------------------------------------------------------------------
    //  Getter methods
    // -----------------------------------------------------------------------
    unsigned int getResponseCode() const
    {
        return respCode;
    }

    char* getResponseText() const
    {
        return respText;
    }

    char* getResponseMessage() const
    {
        return respMessage;
    }

    bool isSuccessful() const
    {
        return succ;
    }

    bool hasError() const
    {
        return err;
    }

    void resetHandler();


    // -----------------------------------------------------------------------
    //  Handlers for the SAX2 ContentHandler interface
    // -----------------------------------------------------------------------
    void startElement (const XMLCh* const uri, const XMLCh* const localname, const XMLCh* const qname, const Attributes &attrs);
    void endElement (const XMLCh* const uri, const XMLCh* const localname, const XMLCh* const qname);
    void characters(const XMLCh* const chars, const unsigned int length);

    // -----------------------------------------------------------------------
    //  Handlers for the SAX2 ErrorHandler interface
    // -----------------------------------------------------------------------
    void  error (const SAXParseException &exception);
    void  fatalError (const SAXParseException &exception);
    void  warning (const SAXParseException &exception);


private:
    unsigned int    respCode;
    char            *respText;
    char            *respMessage;
    bool            succ;
    bool            err;

    bool    isRoot;
    bool    inOperation;
    bool    inSubmitClientResponse;
    bool    inClientSuccess;
    bool    inFailure;
    bool    inConfirmation;
    bool    inSuccess;
};




#endif // #ifndef __DEV_WCTP_H__
