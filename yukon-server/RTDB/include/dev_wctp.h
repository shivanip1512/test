#pragma once

#include <xercesc/sax2/DefaultHandler.hpp>
#include <xercesc/sax2/Attributes.hpp>
#include <xercesc/sax2/SAX2XMLReader.hpp>

#include "dev_paging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"
#include "encryption_oneway_message.h"

#define WCTP_TIMEOUT    3

class SAXWctpHandler;

class IM_EX_DEVDB CtiDeviceWctpTerminal  : public Cti::Devices::DevicePaging,
                                           protected OneWayMsgEncryption
{
    typedef Cti::Devices::DevicePaging Inherited;

    CHAR            *readLinePtr;

    XERCES_CPP_NAMESPACE::SAX2XMLReader   *parser;            // Parser to parse the WCTP response message
    SAXWctpHandler  *handler;           // WCTP response message handler

    BOOL            statusParsed;       // Whether the HTTP response status line has been parsed
    BOOL            headerParsed;       // Whether the HTTP response headers has been parsed
    INT             timeEllapsed;       // Time ellapsed before WCTP response message has been completely received

    bool _sendFiller;

    CtiTime  _pacingTimeStamp;       // This is a timestamp from which we began the pacing process.  Used with CPARM: PAGING_BATCH_WINDOW
    int      _pagesPerMinute;        // This is a count of pages since the _pacingTimeStamp.         Used with CPARM: PAGING_BATCH_SIZE
    bool     _pacingReport;

    bool     _allowPrefix;

protected:

   std::queue< CtiVerificationBase * >  _verification_objects;
   UINT                         _pageCount;    // Used to count the number of pages sent out (0-n)
   CHAR                         _pagePrefix;   // Used to fake the WCTPTERM into thining it is a new message (a-d)
   UINT                         _pageLength;
   CHAR*                        _pageBuffer;
   OUTMESS                      *_outMessage;

   std::string                  _inStr;

   CHAR*                        _outBuffer;     // Use our own buffer because WCTP message could be as long as 1024 bytes
   CHAR*                        _inBuffer;
   CHAR*                        _xmlBuffer;     // Buffer for XML message

public:

   CtiDeviceWctpTerminal();
   virtual ~CtiDeviceWctpTerminal();

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

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   std::string getDescription(const CtiCommandParser & parse) const;

   YukonError_t generateCommand(CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t decodeResponse(CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;

   INT allocateDataBins (OUTMESS *outMess) override;
   INT freeDataBins() override;

   virtual CtiDeviceIED& setInitialState(const LONG oldid);

   INT traceOut(PCHAR Message, ULONG Count, CtiMessageList &traceList);
   INT traceIn(PCHAR Message, ULONG Count, CtiMessageList &traceList, BOOL CompletedMessage = FALSE);

   CtiDeviceWctpTerminal& setSendFiller(bool yesno);
   bool getSendFiller() const;
   bool devicePacingExceeded();
   bool allowPrefix() const;
   void setAllowPrefix(bool val);
   void updatePageCountData(UINT addition);

   virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

   void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);

private:

   CHAR* replaceChars(const CHAR *src, CHAR *dst);
   CHAR* buildXMLMessage(const CHAR* recipientId, const CHAR* senderId, const CHAR* msgPayload, const CHAR* timeStamp, const CHAR *prefix, const CHAR *securityCode=0);

   INT   readLine(CHAR* str, CHAR* buf, INT bufLen);
   CHAR* getReadLinePtr()
   {
       return readLinePtr;
   }

   XERCES_CPP_NAMESPACE::SAX2XMLReader*  getSAXParser();
   SAXWctpHandler* getWctpHandler();

   CHAR* removeDocType(const CHAR *src, CHAR *dst);

   CHAR* trimMessage(CHAR *message);

};



class SAXWctpHandler : public XERCES_CPP_NAMESPACE::DefaultHandler
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
    void startElement (const XMLCh* const uri, const XMLCh* const localname, const XMLCh* const qname, const XERCES_CPP_NAMESPACE::Attributes &attrs);
    void endElement (const XMLCh* const uri, const XMLCh* const localname, const XMLCh* const qname);
    void characters(const XMLCh* const chars, const unsigned int length);

    // -----------------------------------------------------------------------
    //  Handlers for the SAX2 ErrorHandler interface
    // -----------------------------------------------------------------------
    void  error (const XERCES_CPP_NAMESPACE::SAXParseException &exception);
    void  fatalError (const XERCES_CPP_NAMESPACE::SAXParseException &exception);
    void  warning (const XERCES_CPP_NAMESPACE::SAXParseException &exception);


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
