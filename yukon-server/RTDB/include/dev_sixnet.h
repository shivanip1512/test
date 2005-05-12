/*-----------------------------------------------------------------------------*
*
* File:   dev_sixnet
*
* Class:  CtiDeviceSixnet
* Date:   7/10/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_sixnet.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/05/12 19:57:48 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SIXNET_H__
#define __DEV_SIXNET_H__
#pragma warning( disable : 4786)


#include <set>
#include <vector>

#include "dev_meter.h"
#include "dlldefs.h"
#include "prot_sixnet.h"

typedef struct
{
  ULONG time;
  int type;
  int offset;
  double val;
  UINT tag;

} CtiSixnetLPData;

class CtiSxlRecord
{
protected:

    RWTime      _ptTime;        // When this all went and happened.
    int         _ptType;        // Corresponds to the types in pointtypes.h
    int         _ptOffset;      // 1 based offset on this device for this point type.
    double      _ptValue;       // Go figure.

public:

    CtiSxlRecord();
    CtiSxlRecord(int type, int offset, double val, const RWTime &time);
    virtual ~CtiSxlRecord();

    RWTime& getTime();
    int getOffset() const;
    int getType() const;
    double getValue() const;

    CtiSxlRecord& setTime(const RWTime& ref);
    CtiSxlRecord& setType(int type);
    CtiSxlRecord& setOffset(int offset);
    CtiSxlRecord& setValue(const double& val);

    bool getLPStruct(BYTE *bp, UINT tag = 0x00000000);
};

class CtiSxlFieldHistory
{
public:
   // int         _type;      // Type is catually defined byt the field we are found in.
   time_t      _time;         // When this all went and happened.
   int         _offset;       // 1 based offset on this device for this point type.
   short       _pulses;       // Go figure.

   bool operator==(const CtiSxlFieldHistory& aRef) const
   {
      return _offset == aRef._offset;
   }

   bool operator<(const CtiSxlFieldHistory& aRef) const
   {
      return _offset < aRef._offset;
   }

   bool operator()(const CtiSxlFieldHistory& y) const
   {
      return operator<(y);    // *this < y
   }

};

class CSxlField
{
public:

   typedef set< CtiSxlFieldHistory > FIELDHISTORY;
   typedef pair< FIELDHISTORY::iterator, bool > FIELDHISTORYPAIR;

   CSxlField()
   {
      m_eType = NOTSET;
   }
   virtual ~CSxlField()
   {
      _history.clear();
   }

   int processData(uchar *rec, vector< CtiSxlRecord > & _recordData, UINT interval);

   bool operator==(const CSxlField& aRef) const
   {
      bool bOffset = m_nOffset == aRef.m_nOffset;
      bool bSame = (m_eType == aRef.m_eType);

      return (bOffset && bSame);
   }

   bool operator<(const CSxlField& aRef) const
   {
      bool bType = (m_eType < aRef.m_eType);
      bool bOffset = m_nOffset < aRef.m_nOffset;
      bool bSame = (m_eType == aRef.m_eType);



      return ( bType || (bSame && bOffset) );
   }

   bool operator()(const CSxlField& y) const
   {
      return operator<(y);    // *this < y
   }

   // Sixnet I/O types:
   typedef enum
   {
      NOTSET = -1,
      AIN = 0,        // analog inputs (16 bits each)
      AOUT = 1,       // analog outputs (16 bits each)
      DIN = 10,       // discrete inputs (1 bit each)
      DOUT = 11,      // discrete outputs (1 bit each)
      LIN = 20,       // long integer inputs (32 bits each)
      LOUT = 21,      // long integer outputs (32 bits each)
      FIN = 22,       // float inputs (32 bits each)
      FOUT = 23,      // float outputs (32 bits each)
   } iotypes_t;

   iotypes_t m_eType;
   int m_nNumRegs;
   int m_nFirst;
   int m_nOffset;

   FIELDHISTORY _history;
};


class IM_EX_DEVDB CtiDeviceSixnet : public CtiDeviceIED
{
public:

   typedef vector< CtiSxlRecord >   DATACOLLECTION;
   typedef set< CSxlField >         FIELDCOLLECTION;
   typedef pair< FIELDCOLLECTION::iterator, bool > FIELDCOLLECTIONPAIR;

   enum
   {
      SXNT_TAG_FINAL_MESSAGE = 0x00000001
   };

   enum
   {
      SXNT_START,
      SXNT_GETALIAS,
      SXNT_DECODEALIAS,
      SXNT_GETHEADERINFO,
      SXNT_DECODEHEADERINFO,
      SXNT_GETFIELDDESC,
      SXNT_DECODEFIELDDESC,
      SXNT_GETHEADTAIL,
      SXNT_DECODEHEADTAIL,
      SXNT_GETTAILRECORD,
      SXNT_DECODETAILRECORD,
      SXNT_GETHEADRECORD,
      SXNT_DECODEHEADRECORD,
      SXNT_GETRECORDS,
      SXNT_DECODERECORDS,
      SXNT_COMPLETE,
      SXNT_RETURNDATA,
      SXNT_BAIL,
      SXNT_MOREDATANEEDED
   };

protected:

   RWCString _logfileName;
   UCHAR* _txBuffer;
   UCHAR* _rxBuffer;


   DATACOLLECTION _recordData;    // This is why we are here!


   // uint32 m_nAlias;     // Protocol object keeps the alias.
   int _msStationNum;      // MasterStation Station Number
   int _targetStationNum;  // Target Sixnet IO device station number
   int _dataFormat;        // Format of the data.  I intend to support bin or hex only.
   int _lengthFormat;      // Always will be type 2 (extended lengths)
   int _fieldCnt;          // number of I/O fields in record

   FIELDCOLLECTION _fields;

   int _records;            // maximum records in file
   int _recSize;            // bytes in each record
   int _timeFormat;         // time format (1 means seconds since 1970)
   uint32 _head;            // head record number
   uint32 _tail;            // tail record number
   uint32 _logRate;         // milleseconds between each log!
   time_t _tailTime;        // tail record (or tail we care about) timestamp.

   RWTime _lpTime;


   CtiProtocolSixnet *_protocol;

   CtiProtocolSixnet& getSixnetProtocol();

private:

   UINT _registerCnt;
   UINT _demAccumCnt;

   enum
   {
      SXL_HDR = 0,          // file location of header information
      SXL_MAJOR_VERS = 0,   // 1 byte major version
      SXL_MINOR_VERS = 1,   // 1 byte minor version
      SXL_RELEASE = 2,      // 1 byte release number
      SXL_REVISION = 3,     // 1 byte release number
      SXL_MISC = 25,        // 1 byte miscellaneous settings
      SXL_ERROR = 26,       // 1 byte error code
      SXL_NFIELDS = 30,     // 2 byte number of record field descriptors
      SXL_NRECORDS = 60,    // 4 byte number of records
      SXL_MSINTERVAL = 68,  // 4 byte millesecond log interval
      SXL_HEAD = 72,        // 4 byte head record number
      SXL_TAIL = 76,        // 4 byte tail record number
      SXL_RECSIZE = 84,     // 4 byte record size
      SXL_HDR_SIZE = 88,    // 88 bytes in fixed header information
      SXL_FIELDS = 88,      // the field descriptions start here
   };

   int _completedState;
   int _executionState;

public:

   typedef CtiDeviceIED Inherited;

   CtiDeviceSixnet();

   virtual ~CtiDeviceSixnet();

   CtiDeviceSixnet& operator=(const CtiDeviceSixnet& aRef);

   // get 8, 16 or 32-bit value from buffer
   static int get8(uchar* p)    { return p[0];}
   static int16 get16(uchar* p) { return(p[0] << 8) + p[1];}
   static int32 get32(uchar* p) { return(p[0] << 24) + (p[1] << 16) + (p[2] << 8) + p[3];}

   int assembleGetAlias();
   int assembleGetHeaderInfo();
   int assembleGetHeadTail();
   int assembleGetRecords(uint32 pfirst, int pn); // get n records
   int assembleGetFields();
   int assembleSetTail(uint32 tail);

   bool processGetAlias();
   int processGetHeaderInfo();
   int processGetHeadTail();
   int processGetRecords(int &recProcessed);
   int processGetFields();
   int processSetTail(uint32 tail);

   time_t getFirstRecordTime();

   // Note - record number compares must deal properly with
   // record numbers that wrap back to 0. isRecLT() does this..
   // Answer: Is a less than b?
   bool isRecLT(uint32 a, uint32 b) const { return(int32)(a - b) < 0;}
   UCHAR* getTxBuffer();
   UCHAR* getRxBuffer();

   uint32 CtiDeviceSixnet::getRecordsToGet() const;

   int analyzeReadBytes(int bytesread);
   int fetchRecords(bool resetFetch);
   void flushVectors();
   void destroyBuffers();


   CtiMeterMachineStates_t determineHandshakeNextState(int state);
   /*
    *  A paired set which implements a state machine (before/do port work/after) in conjunction with
    *  the port's function out/inMess pair.
    */
   virtual INT generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT decodeResponseHandshake (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT generateCommandDisconnect (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT decodeResponseDisconnect (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT generateCommand    (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT decodeResponse (CtiXfer &Transfer,INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT allocateDataBins (OUTMESS *);
   virtual INT freeDataBins ();
   virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived);
   virtual INT copyLoadProfileData (BYTE *aInMessBuffer, ULONG &aBytesReceived);

   void setupGetRecord(CtiXfer &Transfer);
   void checkStreamForTimeout(INT protocolreturn, CtiXfer &Transfer);

   virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority);
   virtual INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList,  RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   virtual INT ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);


   INT decodeResultLoadProfile(INMESS *InMessage,RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   INT decodeResultScan(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

};
#endif // #ifndef __DEV_SIXNET_H__
