#pragma once

#include <windows.h>

#include "cmdparse.h"
#include "dllbase.h"
#include "dlldefs.h"
#include "mutex.h"

typedef unsigned char uchar;
typedef short int16;
typedef int int32;
typedef unsigned short uint16;
typedef unsigned int uint32;


#define SXNT_ACK            1     // Acknowledge command

#define FILESYS             26     // file system command group
#define    FILESYS_GETALIAS  0     // get access to a file
#define    FILESYS_READ      1     // Read from a file
#define    FILESYS_WRITE     2     // Write to a file
#define    FILESYS_CREATE    3     // Create a file and return an alias
#define    FILESYS_DELETE    4     // Deletes an existing file
#define    FILESYS_DIR       5     // get a list of files in the file system
#define    FILESYS_STAT      6     // get information about a specific file
#define    FILESYS_COMPRESS  7     // Compress the file system
#define    FILESYS_CHKDSK    8     // Check file system integrity
#define    FILESYS_RENAME    9     // Rename an existing file
#define    FILESYS_MEMAVAIL 10     // gets file system available memory

#define DLOG                27     // Datalog command group
#define    DLOG_GET_RECORD_TIME  0
#define    DLOG_FIRST_AFTER_TIME 1
#define    DLOG_GET_RECORDS      2 // DLOG get records subcommand
#define    DLOG_GET_AND_CLEAR    3
#define    DLOG_MOVE_TAIL        4
#define    DLOG_GET_REC_SEGMENT  5
#define    DLOG_NEW_RECORDS     16 // DLOG new records subcommand

// offsets in data area for DLOG_NEW_RECORDS information
#define       DLN_SUBCOMMAND   0   // where DLOG_NEW_RECORDS should be
#define       DLN_FORMAT       1   // command format (offset, not the value)
#define       DLN_LOGID        2   // station ID
#define       DLN_CMD_TIME     4   // when data was sent
#define       DLN_FIRST_REC    8   // first record number in message
#define       DLN_NUM_RECS    12   // number of records in message
#define       DLN_TIME_LEN    13   // number of bytes of time data
#define       DLN_NUM_FLOAT   14   // number of 4-byte float data values
#define       DLN_NUM_LONG    15   // number of 4-byte integer data values
#define       DLN_NUM_SHORT   16   // number of 2-byte integer data values
#define       DLN_NUM_DISC    17   // number of 1-bit discrete data values
#define       DLN_DATA        18   // start of first record data

// offsets in ACK data area for DLOG_NEW_RECORDS ACK
#define       DLNA_FORMAT      0   // command format (offset, not the value)
#define       DLNA_NUM_RECS    1   // number of records acknowledged
#define       DLNA_FIRST_REC   2   // first record number acknowledged
#define       DLNA_NEW_TIME    6   // new time of day
#define       DLNA_NEXT_REPORT 10  // time for next report



class IM_EX_PROT CtiProtocolSixnet : private boost::noncopyable
{
public:
   enum
   {
       GETLEAD,
       GETLENGTH1, GETLENGTH2,
       GETDEST1, GETDEST2,
       GETSRC1, GETSRC2, GETSESS, GETSEQ, GETCMD, GETDATA, GETCRC1, GETCRC2,
       GETCOMPLETE, GETTIMEOUT
   };


private:
    void OutHex(int x, int bytes = 1);
    void OutSpecial(int x);
    void crccitt(uchar data);
    int HexValue(int nHexDigit);

    static CtiMutex seqMux;
    static int nNextSeq;  // message sequence common for all messages

    int _state;//  = GETLEAD;

    /*
     *  Glom from msg classes
     */
    uint32   _alias;        // Remote File alias.

    // Methods which have data.
    std::vector< uchar > _byBuf; // vector of byte data which will be decoded in the process routines

    // These are from GetRecs.
    int      _numRecs;      // number of records received
    uint32   _first;        // first record number received
    uint32   _last;         // last record number received

    // All errors
    int      _error;        // Error on a request here.



    uint16  _txCRC;
    uint16  m_crc;          // Working value of crc

    int     _station;     // my station number
    uchar*  _pData;        // pointer to where processing in m_data

    uint16  _rxCRC;
    std::vector< uchar >  _rxdata;   // message data buffer
    std::vector< uchar >  _txdata;   // message data buffer

    uchar*  pNextRx;        // points to next byte in m_rxdata
    uchar*  pNextTx;        // points to next byte in m_rxdata
    uchar*  pNextRcv;       // where to receive next data in m_rxdata

    uchar*  pTx;            // Original and non-moveable location of the protocol TX buffer.  Allocated elsewhere
    uchar*  pRx;            // Original and non-moveable location of the protocol RX buffer.  Allocated elsewhere

protected:

    enum
    {
        CRC_INIT = 0xFFFF, CRC_MAGIC = 0x1D0F
    };  // CRC constants

    int     _rxFormat;    // Format BIN, HEX or NOCRC
    int     _rxAddrLen;   // 1 or 2 byte length
    int     _rxLength;    // Total message length (not including length bytes)
    int     _rxDest;      // Destination (receiving) station number
    int     _rxSrc;       // Source (sending) station number
    int     _rxSession;   // Session number
    int     _rxSequence;  // Sequence number (0-255 or -1 if not set)
    int     _rxCmd;       // command

    int     _txFormat;    // Format BIN, HEX or NOCRC
    int     _txAddrLen;   // 1 or 2 byte length
    int     _txLength;    // Total message length (not including length bytes)
    int     _txDest;      // Destination (receiving) station number
    int     _txSrc;       // Source (sending) station number
    int     _txSession;   // Session number
    int     _txSequence;        // Transmitted sequence number
    int     _txCmd;       // command
    int     _txSubCommand;
    int     _txFSLoc;       // What memory address in the structure was read.
    int     _txFSLen;       // How many bytes were read?

    bool    _txAcked;     // true iff this message has been acked

private:

    public:

    CtiProtocolSixnet(UCHAR* txBuff = NULL, UCHAR* rxBuff = NULL);
    virtual ~CtiProtocolSixnet();

    int assemble();                 // assemble a message for submissino to serial port
    int disassemble(int nRcv);           // disassemble a message in from serial port

    int  SpecialLen(int n);           // 1 or 2 bytes to send the value?
    int  NextSeq(void);               // get next sequence number
    int  getStationNumber() { return _station;}
    bool setStationNumber(int station); // set my station number
    bool setSource(int station);      // source station
    bool setDestination(int station); // destination station
    bool setFormat(int format);       // HEX, BIN or NOCRC
    bool setFormat(int format, int len); // HEX, BIN or NOCRC, length format
    bool setSession(int session);     // 0-127 or 192-255
    int  getSequence() { return _txSequence;}
    bool setSequence(int sequence);   // 0-255
    bool isAcked() { return _txAcked;} // True iff acked
    bool isTxTimedOut() { return false;} // not implemented
    void setAcked(bool b) {_txAcked = b;}

    void DisplayMsg(void);    // output information about message

    void InitSendData();      // prepare to write data to send
    void send8(int nData);    // write 8-bit value to message data
    void send16(int nData);   // write 16-bit value to message data
    void send32(int nData);   // write 32-bit value to message data

    // get 8, 16 or 32-bit value from message data
    void InitGetData();      // prepare for get8(), get16(), get32()
    int get8(int n)    { return _rxdata[n];}
    int16 get16(int n) { return(_rxdata[n] << 8) + _rxdata[n+1];}
    int32 get32(int n) { return(_rxdata[n] << 24) + (_rxdata[n+1] << 16) + (_rxdata[n+2] << 8) + _rxdata[n+3];}


    // get file data using big-endian byte order
    int getfd8(int n)    { return _byBuf[n];}
    int16 getfd16(int n) { return(_byBuf[n] << 8) + _byBuf[n+1];}
    int32 getfd32(int n) { return(_byBuf[n] << 24) + (_byBuf[n+1] << 16) + (_byBuf[n+2] << 8) + _byBuf[n+3];}

    bool isForMe();     // see if message is addressed to me
    // int  getDataLen() { return _rxdlen;} // length of data after command

    enum
    {
        ADDR_OLD = 1, ADDR_FIXED = 2
    };  // Addressing mode constants

    enum
    {
        HEX = ']', BIN = ')', NOCRC = '}'
    };      // command formats
    enum
    {
        ANY_STATION = 0x603f
    }; // matches any station


    int FsGetAliasGenerate(std::string szName, std::string szOptions = std::string());
    int FsGetAliasProcess();

    int FsREADGenerate(uint32 nPos, int nLen);
    int FsREADProcess();
    int DlMOVETAILGenerate(uint32 tail);
    int DlMoveTailProcess();
    int DlGETRECSGenerate(uint32 first, int n);
    int DlGetRecsProcess();

    bool isGoodAlias() const;
    int ProcessMessage();


    void setBuffers(UCHAR* txBuff = NULL, UCHAR* rxBuff = NULL);

    const std::vector< uchar >& getByteBuffer();

    bool validAlias() const { return _alias != 0; }
    int getNumRecordsRead() const;
    uint32 getFirstRecordRead() const;
    uint32 getLastRecordRead() const;

    uint32 getBytesLeftInRead() const;

};
