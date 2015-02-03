/*-----------------------------------------------------------------------------*
*
* File:   dev_sixnet
*
* Date:   7/10/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_sixnet.cpp-arc  $
* REVISION     :  $Revision: 1.23.2.1 $
* DATE         :  $Date: 2008/11/20 16:49:20 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include <limits.h>
#include "dev_sixnet.h"
#include "logger.h"
#include "msg_cmd.h"
#include "porter.h"
#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;
using std::vector;

int CSxlField::processData(uchar *rec, vector< CtiSxlRecord > &_recordData, UINT interval)
{
    int status = ClientErrors::None;
    CtiSxlFieldHistory hist;
    CtiSxlRecord newRec;

    time_t tstamp = CtiDeviceSixnet::get32(rec);
    newRec.setTime( CtiTime(tstamp ) );

    Cti::FormattedTable logTable;
    if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
    {
        logTable.setCell(0, 0) << "Type";
        logTable.setCell(0, 1) << "Offset";
        logTable.setCell(0, 2) << "Value";
    }

    switch (m_eType)
    {
        case FIN:
        case FOUT:
            for (int i=0, row=0; i < m_nNumRegs; ++i)
            {
                // FIXME (offset, value, type?)

                union
                { // use this to get data bytes converted to a float
                    uint32 l;
                    float f;
                } u;
                u.l = CtiDeviceSixnet::get32(rec + m_nOffset + 4*i);

                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    ++row;
                    logTable.setCell(row, 0) << newRec.getType();
                    logTable.setCell(row, 1) << newRec.getOffset();
                    logTable.setCell(row, 2) << newRec.getValue();
                }
            }

            break;

        case LIN:
        case LOUT:
            for (int i=0, row=0; i < m_nNumRegs; ++i)
            {
                // FIXME (offset, value, type?)

                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    ++row;
                    logTable.setCell(row, 0) << newRec.getType();
                    logTable.setCell(row, 1) << newRec.getOffset();
                    logTable.setCell(row, 2) << newRec.getValue();
                }
            }

            break;

        case AIN:
            for (int i=0, row=0; i < m_nNumRegs; ++i)
            {
                hist._offset =  i + 1;
                hist._pulses = CtiDeviceSixnet::get16(rec + m_nOffset + 2*i);
                hist._time = tstamp;

                // Process the analog data as an analog..
                newRec.setType( AnalogPointType );
                newRec.setValue( hist._pulses );
                newRec.setOffset( hist._offset );

                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    ++row;
                    logTable.setCell(row, 0) << newRec.getType();
                    logTable.setCell(row, 1) << newRec.getOffset();
                    logTable.setCell(row, 2) << newRec.getValue();
                }

                _recordData.push_back( newRec );

                // Process the analog data as a demand accumulator..
                auto duece = _history.emplace(hist._offset, hist);

                if (duece.second == false)
                {
                    CtiSxlFieldHistory &aHist = duece.first->second;

                    uint16 prev = aHist._pulses;
                    uint16 curr = hist._pulses;

                    // Figure this out!
                    time_t deltaT = hist._time - aHist._time;
                    uint32 deltaP;
                    uint32 deltaPH;

                    if (prev <= curr)
                    {
                        deltaP = curr - prev;
                    }
                    else
                    {
                        uint16 maximal = 0;

                        maximal -= 1;

                        deltaP = maximal - prev + curr;
                    }

                    if (deltaT < interval)
                    {
                        CTILOG_WARN(dout, "deltaT < interval ("<< deltaT <<" < "<< interval <<")");
                    }
                    else if (deltaT > interval * 2)
                    {
                        Cti::FormattedList itemList;

                        itemList.add("Previous read time") << CtiTime(aHist._time);
                        itemList.add("Current read time")  << CtiTime(hist._time);

                        CTILOG_WARN(dout, "Outage detected.  Current pulse reading consumed for next delta compute."<<
                                itemList
                                );

                        aHist = hist;  // Assign it over ok...
                    }
                    else
                    {
                        deltaPH = deltaP * 3600 / interval;    // Now a per hour reading!

                        if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                        {
                            CTILOG_DEBUG(dout,
                                    endl <<"DeltaP  "<< deltaP <<" = "<< hist._pulses <<" - "<< aHist._pulses <<
                                    endl <<"DeltaPH "<< deltaPH
                                    );
                        }

                        newRec.setType( DemandAccumulatorPointType );
                        newRec.setValue( deltaPH );
                        newRec.setOffset( hist._offset );

                        aHist = hist;  // Assign it over ok...

                        if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                        {
                            ++row;
                            logTable.setCell(row, 0) << newRec.getType();
                            logTable.setCell(row, 1) << newRec.getOffset();
                            logTable.setCell(row, 2) << newRec.getValue();
                        }
                        _recordData.push_back( newRec );
                    }
                }
            }
            break;

        case AOUT:
            for (int i=0, row=0; i < m_nNumRegs; ++i)
            {
                newRec.setType( AnalogOutputPointType );
                newRec.setValue( CtiDeviceSixnet::get16(rec + m_nOffset + 2*i) );
                newRec.setOffset( i + 1 );

                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    ++row;
                    logTable.setCell(row, 0) << newRec.getType();
                    logTable.setCell(row, 1) << newRec.getOffset();
                    logTable.setCell(row, 2) << newRec.getValue();
                }
                _recordData.push_back( newRec );
            }
            break;

        case DIN:
            for (int i=0, row=0; i < m_nNumRegs; ++i)
            {
                uint32 ofs = m_nOffset + i;

                newRec.setType( StatusPointType );
                newRec.setValue( ((rec[ofs / 8] >> (ofs & 0x7)) & 1) );
                newRec.setOffset( i + 1 );

                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    ++row;
                    logTable.setCell(row, 0) << newRec.getType();
                    logTable.setCell(row, 1) << newRec.getOffset();
                    logTable.setCell(row, 2) << newRec.getValue();
                }
                _recordData.push_back( newRec );
            }
            break;

        case DOUT:
            for (int i=0, row=0; i < m_nNumRegs; ++i)
            {
                uint32 ofs = m_nOffset + i;

                newRec.setType( StatusOutputPointType );
                newRec.setValue( ((rec[ofs / 8] >> (ofs & 0x7)) & 1) );
                newRec.setOffset( i + 1 );

                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    ++row;
                    logTable.setCell(row, 0) << newRec.getType();
                    logTable.setCell(row, 1) << newRec.getOffset();
                    logTable.setCell(row, 2) << newRec.getValue();
                }

                _recordData.push_back( newRec );
            }
            break;

        default:
            // should never happen

            CTILOG_ERROR(dout, "Invalid m_eType ("<< m_eType <<")");

            break;
    }

    if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
    {
        CTILOG_DEBUG(dout, "Record Time is "<< newRec.getTime() <<
                logTable
                )
    }

    return status;
}

CtiSxlRecord::CtiSxlRecord() :
_ptType(StatusPointType),
_ptOffset(0),
_ptValue(0)
{
}
CtiSxlRecord::CtiSxlRecord(CtiPointType_t type, int offset, double val, const CtiTime &timestamp) :
_ptTime(timestamp),
_ptType(type),
_ptOffset(offset),
_ptValue(val)
{
}

CtiSxlRecord::~CtiSxlRecord()
{
}

bool CtiSxlRecord::getLPStruct(BYTE *bp, UINT tag)
{
    // CtiSixnetLPData* ptr = CTIDBG_new CtiSixnetLPData;
    CtiSixnetLPData dat;

    dat.offset = _ptOffset;
    dat.time = _ptTime.seconds();
    dat.type = _ptType;
    dat.val = _ptValue;
    dat.tag = tag;

    memcpy(bp, &dat, sizeof(CtiSixnetLPData));

    return true;
}

CtiTime& CtiSxlRecord::getTime()
{
    return _ptTime;
}
int CtiSxlRecord::getOffset() const
{
    return _ptOffset;
}
int CtiSxlRecord::getType() const
{
    return _ptType;
}
double CtiSxlRecord::getValue() const
{
    return _ptValue;
}

CtiSxlRecord& CtiSxlRecord::setTime(const CtiTime& ref)
{
    _ptTime = ref;
    return *this;
}
CtiSxlRecord& CtiSxlRecord::setType(CtiPointType_t type)
{
    _ptType = type;
    return *this;
}
CtiSxlRecord& CtiSxlRecord::setOffset(int offset)
{
    _ptOffset = offset;
    return *this;
}
CtiSxlRecord& CtiSxlRecord::setValue(const double& val)
{
    _ptValue = val;
    return *this;
}


//////////////////////////////////////////////////////////
//
// move the tail (oldest record number). This clears all
// records with lower numbers.
//
int CtiDeviceSixnet::assembleSetTail(uint32 tail)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return getSixnetProtocol().DlMOVETAILGenerate(tail);
}



//////////////////////////////////////////////////////////
//
// get 1 or more records as specified by *pn, starting with *pfirst.
//
// returns true iff get a least one record.
//
// *pfirst is set to actual first record received
// *pn is set to actual number of records received
//
int CtiDeviceSixnet::assembleGetRecords(uint32 first, int n)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return getSixnetProtocol().DlGETRECSGenerate(first, n);
}

int CtiDeviceSixnet::processGetRecords(int &recProcessed)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    // Someone has record data out there...


    recProcessed = getSixnetProtocol().getNumRecordsRead();  // return number read

    bool bOk = recProcessed > 0;

    if (bOk)
    {
        FIELDCOLLECTION::iterator fit;
        const vector<uchar> &vecRef = getSixnetProtocol().getByteBuffer();

        for (int i = 0; i < recProcessed; ++i)
        {
            int j;
            int offset = i * _recSize;
            std::vector<uchar> tBuf;
            tBuf.reserve(_recSize + 1);

            for (j = offset; j < offset + _recSize; j++)
            {
                tBuf[j - offset] = vecRef[j];
            }

            // tBuf now holds one record/row from the file!

            // Each field entry must examine the row, adding values to _recordData.
            for (fit = _fields.begin(); fit != _fields.end(); fit++)
            {
                fit->second.processData( tBuf.data(), _recordData, _logRate );
            }
        }
    }


    return _recordData.size();      // How many data elements have been collected.
}

time_t CtiDeviceSixnet::getFirstRecordTime()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    time_t tstamp = 0;
    // Someone has record data out there...

    int numRead = getSixnetProtocol().getNumRecordsRead();  // return number read

    bool bOk = numRead > 0;

    if (bOk)
    {
        tstamp = (getSixnetProtocol().getByteBuffer()[0] << 24) + (getSixnetProtocol().getByteBuffer()[1] << 16) + (getSixnetProtocol().getByteBuffer()[2] << 8) + getSixnetProtocol().getByteBuffer()[3];
    }

    return tstamp;
}


//////////////////////////////////////////////////////////
//
// get the field descriptions, which specify which information
// was logged and where it is stored in the record.
//
// returns true iff get the fields.
//
// assumes all the field descriptions fit in one message
//
int CtiDeviceSixnet::assembleGetFields()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return getSixnetProtocol().FsREADGenerate(SXL_FIELDS, 8 * _fieldCnt);
}

int CtiDeviceSixnet::processGetFields()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    bool bOk = ( getSixnetProtocol().getByteBuffer().size() == 8 * _fieldCnt );

    _registerCnt = 0;
    _demAccumCnt = 0;

    // copy the field information for each field
    if (bOk)
    {
        for (int i = 0; i < _fieldCnt; ++i)
        {
            CSxlField fld;

            fld.m_eType = (CSxlField::iotypes_t)getSixnetProtocol().getfd8(i * 8);
            fld.m_nNumRegs = getSixnetProtocol().getfd8(i * 8 + 1);
            fld.m_nFirst = getSixnetProtocol().getfd16(i * 8 + 2);
            fld.m_nOffset = getSixnetProtocol().getfd32(i * 8 + 4);

            if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
            {
                Cti::FormattedList itemList;

                itemList.add("Type")   << fld.m_eType;
                itemList.add("Num")    << fld.m_nNumRegs;
                itemList.add("First")  << fld.m_nFirst;
                itemList.add("Offset") << fld.m_nOffset;

                CTILOG_DEBUG(dout, itemList);

            }

            if (fld.m_eType == CSxlField::AIN)
            {
                _demAccumCnt += fld.m_nNumRegs;
            }

            _registerCnt += fld.m_nNumRegs;

            _fields[CSxlField::FIELDKEY(fld.m_eType, fld.m_nOffset)] = fld;
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid size");
    }

    return _fields.size();
}


//////////////////////////////////////////////////////////
//
// Get the data log header information.
// Most is not actually used by this program.
//
// Note - the head and tail record numbers are
//        included in the data, but may not be
//        accurate for some units. They must be
//        read separately, by GetHeadTail().
//
// returns true iff get the information.
//
int CtiDeviceSixnet::assembleGetHeaderInfo()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return getSixnetProtocol().FsREADGenerate(SXL_HDR, SXL_HDR_SIZE);
}


/*
 *  Presumable an assemble was just performed to bring in the correct data.
 */
YukonError_t CtiDeviceSixnet::processGetHeaderInfo()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    YukonError_t status = ClientErrors::None;


    if (getSixnetProtocol().getByteBuffer().size() >= SXL_HDR_SIZE)     // Did we get that which we asked for??
    {
        bool bOk = true;

        // first make sure the header is for file format I recognize
        if (bOk)
        {
            if (getSixnetProtocol().getfd8(SXL_MAJOR_VERS) != 1
                || getSixnetProtocol().getfd8(SXL_MINOR_VERS) != 0
                || getSixnetProtocol().getfd8(SXL_RELEASE) != 0
                || getSixnetProtocol().getfd8(SXL_REVISION) != 1)
                bOk = false;      // don't recognize header, give up
        }

        // make sure configuration loaded OK
        if (bOk)
        {
            if (getSixnetProtocol().getfd8(SXL_ERROR) != 0)
            {
                status = ClientErrors::Abnormal;
                bOk = false;      // configuration has errors, give up
            }
        }

        if (bOk)
        {
            // process data
            _fieldCnt = getSixnetProtocol().getfd16(SXL_NFIELDS);         // number of I/O fields in record
            _records = getSixnetProtocol().getfd32(SXL_NRECORDS);         // maximum records in file
            _recSize = getSixnetProtocol().getfd32(SXL_RECSIZE);          // bytes in each record
            _timeFormat = getSixnetProtocol().getfd8(SXL_MISC) & 0x7;     // time format
            _logRate =  (getSixnetProtocol().getfd32(SXL_MSINTERVAL) / 1000);      // seconds per log
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid size");
    }

    return status;
}


//////////////////////////////////////////////////////////
//
// Get the head and tail record numbers.
//
// Note - For some units the head and tail record numbers
//        must be read by reading exactly 4 or 8 bytes
//        at the proper address.
//
// returns true iff get the information.
//
int CtiDeviceSixnet::assembleGetHeadTail()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return getSixnetProtocol().FsREADGenerate(SXL_HEAD, 8);
}

int CtiDeviceSixnet::processGetHeadTail()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    bool bOk = getSixnetProtocol().getByteBuffer().size() == 8;

    // first make sure the header is for file format I recognize
    /*
     *  Ok, this is nice.. I guess we do the first (SXL_HEAD-SXL_HEAD) just for fun,
     *  and the second (SXL_TAIL-SXL_HEAD) only to get the offset right.  Yeah, and
     *  all this after we hard coded the length at eight.  Which, by the way is
     *  required for the SiteTrak product.  This info must be read in a seperate
     *  and singular read.
     */
    if (bOk)
    {
        _head = getSixnetProtocol().getfd32(SXL_HEAD-SXL_HEAD);   // get head record number
        _tail = getSixnetProtocol().getfd32(SXL_TAIL-SXL_HEAD);   // get tail record number
    }

    int cnt;

    if ( _head > _tail )
    {
        cnt = _head - _tail;
    }
    else
    {
        cnt = UINT_MAX - _tail + _head;
    }

    return cnt;
}


int CtiDeviceSixnet::fetchRecords(bool resetFetch)
{
    return ClientErrors::None;
}

//////////////////////////////////////////////////////////
//
// Get an alias (roughly speaking a handle) for the data log file.
// It is needed to access the logged data.
//
// returns true iff get an alias for the file.
//
int CtiDeviceSixnet::assembleGetAlias()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    if (_recordData.size()) _recordData.clear();

    // Fill out the request message and return the number of bytes to be sent into the world.
    return getSixnetProtocol().FsGetAliasGenerate(_logfileName.c_str());
}

bool CtiDeviceSixnet::processGetAlias()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    // Return value is zero for success.

    return getSixnetProtocol().validAlias();
}

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CtiDeviceSixnet::CtiDeviceSixnet() :
    _demAccumCnt(0),
    _head(0),
    _logRate(0),
    _records(0),
    _recSize(0),
    _registerCnt(0),
    _tail(0),
    _tailTime(0),
    _timeFormat(0)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    _msStationNum = CtiProtocolSixnet::ANY_STATION;
    _targetStationNum = CtiProtocolSixnet::ANY_STATION;
    _fieldCnt = 0;
    _dataFormat = CtiProtocolSixnet::BIN;        // Binary message Format is for me/for now anyway
    _lengthFormat = CtiProtocolSixnet::ADDR_OLD; // 1 byte length and station numbers

    _protocol = NULL;
    _txBuffer = NULL;
    _rxBuffer = NULL;

    _completedState = SXNT_START;
    _executionState = SXNT_START;
}

CtiDeviceSixnet::~CtiDeviceSixnet()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    flushVectors();
    destroyBuffers();
}

CtiProtocolSixnet& CtiDeviceSixnet::getSixnetProtocol()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    if (_protocol == NULL)
    {
        /*
         *  Make sure we have a protocol object to operate upon.
         */
        _protocol = CTIDBG_new CtiProtocolSixnet(getTxBuffer(), getRxBuffer());

        _protocol->setStationNumber(_msStationNum);
        _protocol->setDestination(_targetStationNum);
        _protocol->setFormat(_dataFormat, _lengthFormat);
    }

    return *_protocol;
}

UCHAR* CtiDeviceSixnet::getTxBuffer()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if (_txBuffer == NULL)
    {
        /*
         *  Make sure we have a protocol object to operate upon.
         */
        _txBuffer = CTIDBG_new UCHAR[512];
    }

    return _txBuffer;
}

UCHAR* CtiDeviceSixnet::getRxBuffer()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if (_rxBuffer == NULL)
    {
        /*
         *  Make sure we have a protocol object to operate upon.
         */
        _rxBuffer = CTIDBG_new UCHAR[512];
    }

    return _rxBuffer;
}


INT CtiDeviceSixnet::allocateDataBins (OUTMESS *OutMessage)
{
    INT status = ClientErrors::None;

    CtiLockGuard<CtiMutex> guard(_classMutex);
    // They are self allocating... But we want to clear these out.
    flushVectors();

    _executionState = SXNT_START;
    _completedState = SXNT_START;

    _lpTime = CtiTime((ULONG)OutMessage->Buffer.DUPReq.LP_Time);

    return status;
}

void CtiDeviceSixnet::flushVectors()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    try
    {
        if (_recordData.size()) _recordData.clear();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiDeviceSixnet::destroyBuffers()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    try
    {
        if (_protocol != NULL)
        {
            delete _protocol;
            _protocol = NULL;
        }

        if (_txBuffer != NULL)
        {
            delete [] _txBuffer;
            _txBuffer = NULL;
        }

        if (_rxBuffer != NULL)
        {
            delete [] _rxBuffer;
            _rxBuffer = NULL;
        }
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

INT CtiDeviceSixnet::freeDataBins()
{
    return ClientErrors::None;
}

YukonError_t CtiDeviceSixnet::generateCommandHandshake(CtiXfer  &Transfer, CtiMessageList &traceList)
{

    CtiLockGuard<CtiMutex> guard(_classMutex);
    setCurrentState( CtiDeviceIED::StateHandshakeInitialize );
    YukonError_t status = ClientErrors::None;
    INT bytesread = (INT) (Transfer.getInCountActual());
    INT protocolreturn;

    bool breakcase = true;

    switch ( _executionState )
    {
        case SXNT_START:
        case SXNT_GETALIAS:
            {
                _completedState = SXNT_GETALIAS;           // We just completed this!
                _executionState = SXNT_DECODEALIAS;          // We are working on this..

                Transfer.setOutCount( assembleGetAlias() );  // Asseble the Alias request message and return the byte count.
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied

                Transfer.setNonBlockingReads(true);          // We want the port to only load up the data that is out there in the buffer.
                Transfer.setInCountExpected(10);             // Minimum of 10 bytes required.
                Transfer.setInTimeout(1);                    // Will wait a bit though.

                Transfer.setCRCFlag(0);                      // Device manages all CRC computes.

                break;
            }
        case SXNT_GETHEADERINFO:
            {
                _completedState = _executionState;
                _executionState = SXNT_DECODEHEADERINFO;          // We are working on this..

                Transfer.setOutCount( assembleGetHeaderInfo() );  // Asseble the request message and return the byte count.
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied

                Transfer.setNonBlockingReads(true);          // OK, Make sure we get at least this many!
                Transfer.setInCountExpected(SXL_HDR_SIZE);   // Minimum of SXL_HDR_SIZE bytes required.
                Transfer.setInTimeout(10);                    // Will wait a bit though.

                Transfer.setCRCFlag(0);                      // Device manages all CRC computes.

                break;
            }
        case SXNT_MOREDATANEEDED:
            {
                // set it back to the previous completed state
                _executionState=_completedState ;
                break;
            }

        default:
            {
                CTILOG_ERROR(dout, "Invalid execution state ("<< _executionState <<")");

                break;
            }
    }

    return status;
}
YukonError_t CtiDeviceSixnet::decodeResponseHandshake(CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    YukonError_t status = ClientErrors::None;
    INT bytesread = (INT) (Transfer.getInCountActual());
    INT protocolreturn;

    bool breakcase = true;

    switch ( _executionState )
    {
        case SXNT_DECODEALIAS:
            {
                // If we enter this, we know that we've sent out the alias request.
                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    _completedState = _executionState;
                    _executionState = SXNT_GETHEADERINFO;
                    // breakcase = false;                        // Fall through to next case..
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                if (breakcase) break;
            }
        case SXNT_DECODEHEADERINFO:
            {
                // If we enter this, we know that we've sent out the headerinfo request.
                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    status = processGetHeaderInfo();

                    setCurrentState( CtiDeviceIED::StateHandshakeComplete );    // Exit the loop in the portfield file.

                    _completedState = _executionState;
                    _executionState = SXNT_GETFIELDDESC;
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Invalid execution state ("<< _executionState <<")");

                _executionState = SXNT_START;

                break;
            }
    }

    return status;
}

YukonError_t CtiDeviceSixnet::generateCommandDisconnect(CtiXfer  &Transfer, CtiMessageList &traceList)
{
    YukonError_t status = ClientErrors::None;

    setCurrentState(CtiDeviceIED::StateComplete);

    Transfer.setOutCount(0);
    Transfer.setNonBlockingReads(false);   // Make it ignore the port!
    Transfer.setInCountExpected(0);

    CTILOG_INFO(dout, "Sixnet communication completed to " << getName());

    return status;
}
YukonError_t CtiDeviceSixnet::decodeResponseDisconnect(CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t status = ClientErrors::None;

    CTILOG_INFO(dout, "Sixnet disconnecting " << getName());

    return status;
}

YukonError_t CtiDeviceSixnet::generateCommand(CtiXfer  &Transfer, CtiMessageList &traceList)
{
    YukonError_t status = ClientErrors::None;
    INT bytesread = (INT) (Transfer.getInCountActual());
    INT protocolreturn;

    setPreviousState( CtiDeviceIED::StateScanValueSet1 );
    setCurrentState( CtiDeviceIED::StateScanValueSet1 );

    switch ( _executionState )
    {
        case SXNT_GETFIELDDESC:
            {
                _completedState = _executionState;
                _executionState = SXNT_DECODEFIELDDESC;          // We are working on this..

                // This MAY be a new Transfer reference!
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied

                Transfer.setOutCount( assembleGetFields() );  // Asseble the request message and return the byte count.

                Transfer.setNonBlockingReads(true);          // We want the port to only load up the data that is out there in the buffer.
                Transfer.setInCountExpected(8 * _fieldCnt);  // Minimum bytes required.
                Transfer.setInTimeout(3);                    // Will wait a bit though.

                Transfer.setCRCFlag(0);                      // Device manages all CRC computes.

                break;
            }
        case SXNT_GETHEADTAIL:
            {
                _completedState = _executionState;
                _executionState = SXNT_DECODEHEADTAIL;       // We are working on this..
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied

                Transfer.setOutCount( assembleGetHeadTail() ); // Asseble the request message and return the byte count.

                Transfer.setNonBlockingReads(true);          // We want the port to only load up the data that is out there in the buffer.
                Transfer.setInCountExpected(8);              // Minimum bytes required.
                Transfer.setInTimeout(1);                    // Will wait a bit though.

                Transfer.setCRCFlag(0);                      // Device manages all CRC computes.

                break;
            }
        case SXNT_GETTAILRECORD:
            {
                // We need to find the record of choice here...
                _completedState = _executionState;
                _executionState = SXNT_DECODETAILRECORD;    // We are working on this..
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied

                Transfer.setOutCount( assembleGetRecords(_tail, 1) ); // Assemble the request message and return the byte count.

                Transfer.setNonBlockingReads(true);          // We want the port to only load up the data that is out there in the buffer.
                Transfer.setInCountExpected(0);              // Unknown!
                Transfer.setInTimeout(2);                    // Will wait a bit though.
                Transfer.setCRCFlag(0);                      // Device manages all CRC computes.

                break;
            }
        case SXNT_GETHEADRECORD:
            {
                // We need to find the record of choice here...
                _completedState = _executionState;
                _executionState = SXNT_DECODEHEADRECORD;    // We are working on this..
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied

                Transfer.setOutCount( assembleGetRecords(_head - 1, 1) ); // Asseble the request message and return the byte count.

                Transfer.setNonBlockingReads(true);          // We want the port to only load up the data that is out there in the buffer.
                Transfer.setInCountExpected(0);              // Unknown!
                Transfer.setInTimeout(2);                    // Will wait a bit though.
                Transfer.setCRCFlag(0);                      // Device manages all CRC computes.

                break;
            }
        case SXNT_GETRECORDS:
            {
                _completedState = _executionState;
                Transfer.setOutBuffer( getTxBuffer() );      // Don't use any buffer which may have been supplied
                Transfer.setInBuffer( getRxBuffer() );       // Don't use any buffer which may have been supplied
                setupGetRecord(Transfer);
                break;
            }
        case SXNT_RETURNDATA:
            {
                _completedState = _executionState;

                // A null transfer I expect here.
                Transfer.setOutCount(0);
                Transfer.setNonBlockingReads(false);   // Make it ignore the port!
                Transfer.setInCountExpected(0);

                if (_recordData.size() > 0)
                {
                    _executionState = SXNT_COMPLETE;
                    setCurrentState( CtiDeviceIED::StateScanReturnLoadProfile );

                    Sleep(100);  // Don't pinch the socket too hard!    Unclear why I need this!  Socket blows without it.
                }
                else
                {
                    _executionState = SXNT_BAIL;        // Done...

                    CTILOG_INFO(dout, "Sixnet "<< getName() <<" all collected records have been returned");

                    setCurrentState( CtiDeviceIED::StateScanComplete );
                }

                break;
            }
        case SXNT_BAIL:
            {
                break;
            }
        case SXNT_DECODERECORDS:
            {
                break;
            }

        case SXNT_MOREDATANEEDED:
            {
                // set it back to the previous completed state
                _executionState=_completedState ;
                break;
            }

        default:
            {
                CTILOG_ERROR(dout, "Invalid execution state ("<< _executionState <<") - completed state ("<< _completedState <<")");

                setCurrentState( CtiDeviceIED::StateScanAbort );
                break;
            }
    }

    return status;
}

YukonError_t CtiDeviceSixnet::decodeResponse(CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t status = ClientErrors::None;
    INT bytesread = (INT) (Transfer.getInCountActual());
    INT protocolreturn;

    switch ( _executionState )
    {
        case SXNT_DECODEFIELDDESC:
            {
                // If we enter this, we know that we've sent out the headerinfo request.
                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    _completedState = _executionState;
                    _executionState = SXNT_GETHEADTAIL;

                    if ( processGetFields() == 0 )
                    {
                        status = ClientErrors::Abnormal;
                    }
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                break;
            }
        case SXNT_DECODEHEADTAIL:
            {
                // If we enter this, we know that we've sent out the headerinfo request.
                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    _completedState = _executionState;
                    _executionState = SXNT_GETTAILRECORD;

                    if ( processGetHeadTail() == 0 )     // No records on the device.
                    {
                        status = ClientErrors::Abnormal;
                    }
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                break;
            }
        case SXNT_DECODETAILRECORD:
            {
                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    _tailTime = getFirstRecordTime();

                    CtiTime recordtime( _tailTime );

                    if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                    {
                        CTILOG_DEBUG(dout, "tail record was recorded at "<< recordtime);
                    }

                    try
                    {
                        if (_lpTime < recordtime)
                        {
                            int recProcessed=0;

                            // Oh, get them all then... Start with _tail...
                            _completedState = _executionState;
                            _executionState = SXNT_GETRECORDS;    // We are working on this..

                            processGetRecords(recProcessed); // We are consuming the tail request here!
                            _tail += recProcessed;
                        }
                        else // Greater or equal
                        {
                            // We have to go out and get head record.
                            // breakcase = false;
                            _executionState = SXNT_GETHEADRECORD;
                        }
                    }
                    catch (...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                break;
            }
        case SXNT_DECODEHEADRECORD:
            {
                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    time_t recordtime = getFirstRecordTime();
                    CtiTime headTime( recordtime );
                    CtiTime tailtime( _tailTime );

                    if ( _lpTime >= headTime )
                    {
                        // there is no new data..
                        CTILOG_INFO(dout, getName() <<" has no new data (_lpTime >= headTime) ("<< _lpTime <<" >= "<< headTime <<")");

                        _executionState = SXNT_BAIL;

                        setCurrentState( CtiDeviceIED::StateScanComplete );
                    }
                    else if (tailtime <= _lpTime && _lpTime < headTime)
                    {
                        // The start point is in there somewhere!
                        ULONG newtail = _tail;
                        ULONG deltaT = headTime.seconds() - tailtime.seconds();                    // What is the timespan in the box.
                        ULONG deltaR = (_head > _tail ? _head - _tail: UINT_MAX - _tail + _head);  // How many records in the box.
                        ULONG recordCnt = (deltaT / _logRate) + 1;                                 // Number of records I would expect based solely upon timed logs.  Event triggered logs would be in addition to these!.
                        ULONG skiprecords = ((_lpTime.seconds() - tailtime.seconds()) / _logRate) - 5; // How many records do I need to go forward from the tail time? Insulation of 5 repeats is ok.

                        if (recordCnt > deltaR)
                        {
                            recordCnt = deltaR;
                        }

                        if (skiprecords > deltaR)
                        {
                            CTILOG_WARN(dout, "skiprecords > deltaR ("<< skiprecords <<" > "<< deltaR <<")");

                            skiprecords = deltaR - 5;     // Just get a few already..
                        }

                        if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                        {
                            Cti::FormattedList itemList;

                            itemList.add("tail")   << _tail;
                            itemList.add("head")   << _head;
                            itemList.add("tail")   << tailtime;
                            itemList.add("lptime") << _lpTime;
                            itemList.add("head")   << headTime;
                            itemList.add("deltaT") << deltaT;
                            itemList.add("deltaR") << deltaR;
                            itemList.add("recCnt") << recordCnt;
                            itemList.add("shift")  << (deltaR - recordCnt);
                            itemList.add("skip")   << skiprecords;

                            CTILOG_DEBUG(dout, itemList);
                        }

                        // OK, we will skip records, but we MUST assume that any events occur at the very begining
                        //  of the data.  This means we only skip part of he the records...

                        if (isRecLT(recordCnt, deltaR))
                        {
                            skiprecords = skiprecords - (deltaR - recordCnt);  // The final paren set MAY be zero if there are no evnet triggered events.
                        }

                        if (skiprecords > 0)
                        {
                            // Move/skip the "_tail" forward to our new try..
                            newtail = _tail + skiprecords;      // Add the number of records to skip to the tail record number.  Assume the rollover happens correctly on a uint32.

                            if (!(isRecLT(_tail, newtail) && isRecLT(newtail, _head + 1)))
                            {
                                // Whoops the logic sucks.. This better not happen
                                CTILOG_ERROR(dout, "_tail = "<< _tail <<"  newtail = "<< newtail <<"  _head = "<< _head);

                                newtail = _tail;     // Just be a slop monger.
                            }
                        }

                        _tail = newtail;

                        // We must collect the records now..
                        _executionState = SXNT_GETRECORDS;
                    }
                    else
                    {
                        // The fan has been hit!
                        CTILOG_ERROR(dout, "unexpected _lpTime < tailtime ("<< _lpTime <<" < "<< tailtime <<")");

                        // We must collect ALL the records now..
                        _executionState = SXNT_GETRECORDS;
                    }

                    _completedState = _executionState;
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                break;
            }
        case SXNT_DECODERECORDS:
            {
                INT recProcessed;

                protocolreturn = analyzeReadBytes(bytesread);

                if (CtiProtocolSixnet::GETCOMPLETE == protocolreturn )
                {
                    CtiTime recordtime( getFirstRecordTime() );

                    int recCnt = processGetRecords(recProcessed);

                    if (recordtime < _lpTime)
                    {
                        if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                        {
                            CTILOG_DEBUG(dout, "Record "<< _tail <<" is older ("<< recordtime <<") than the last LP time collected ("<< _lpTime <<")");
                        }
                    }

                    if (recCnt > 50)
                    {
                        setCurrentState( CtiDeviceIED::StateScanReturnLoadProfile );
                    }

                    // Next pass gets us a new record!
                    _tail += recProcessed;

                    _executionState = SXNT_GETRECORDS;
                }
                else
                {
                    checkStreamForTimeout(protocolreturn, Transfer);
                }

                break;
            }
        case SXNT_COMPLETE:
            {
                // Process the records in this state!
                int i;
                int recCnt = _recordData.size();

                if (recCnt > 0)
                {
                    _completedState = _executionState;
                    _executionState = SXNT_RETURNDATA;    // We are working on this..
                    setPreviousState( CtiDeviceIED::StateScanValueSet1 );
                    setCurrentState( CtiDeviceIED::StateScanReturnLoadProfile );
                }
                else
                {
                    _executionState = SXNT_COMPLETE;
                    setCurrentState( CtiDeviceIED::StateScanComplete );

                    CTILOG_INFO(dout, getName() <<" has no new records");
                }
                break;
            }
        case SXNT_BAIL:
            {
                setCurrentState( CtiDeviceIED::StateScanComplete );
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Invalid execution state ("<< _executionState <<") - completed state ("<< _completedState <<")");

                setCurrentState( CtiDeviceIED::StateScanAbort );
                break;
            }
    }

    return status;
}

INT CtiDeviceSixnet::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aBytesReceived)
{
    INT status = ClientErrors::None;

    return status;
}

INT CtiDeviceSixnet::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aBytesReceived)
{
    INT status = ClientErrors::None;
    INT remainder = _registerCnt + _demAccumCnt;

    CtiLockGuard<CtiMutex> guard(_classMutex);
    UINT numLeft = _recordData.size();


    if (numLeft > 0)
    {
        BYTE *bpcurr = aInMessBuffer;
        int *ptrcnt = (int *)aInMessBuffer;

        bpcurr += sizeof(int);        // Integer count is sent first.

        do
        {
            DATACOLLECTION::iterator itr = _recordData.begin();
            CtiSxlRecord &Record = *itr;
            UINT tag = 0;

            if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
            {
                CTILOG_DEBUG(dout,
                        endl << Record.getTime() <<"  Type "<< Record.getType() <<"  Offset "<< Record.getOffset() <<"  Value "<< Record.getValue());
            }

            _recordData.erase(itr);

            numLeft = _recordData.size();


            if ( _executionState == SXNT_RETURNDATA || _executionState == SXNT_COMPLETE )
            {
                // Down to the last few records
                tag |= ( numLeft == 0 ) ? SXNT_TAG_FINAL_MESSAGE : 0x00000000;

                remainder = 0; // Clean them out.
            }

            if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
            {
                CTILOG_DEBUG(dout,
                        endl << "NumLeft = "<< numLeft <<
                        endl << "TAG     = "<< tag
                        );
            }

            Record.getLPStruct(bpcurr, tag);
            bpcurr += sizeof(CtiSixnetLPData);

            (*ptrcnt)++;

        } while (bpcurr - aInMessBuffer < 2048 - sizeof(CtiSixnetLPData) && numLeft > remainder);
    }

    return status;
}

int CtiDeviceSixnet::analyzeReadBytes(int bytesread)
{
    return getSixnetProtocol().disassemble( bytesread );
}


CtiDeviceIED::CtiMeterMachineStates_t CtiDeviceSixnet::determineHandshakeNextState(int protocolstate)
{
    CtiDeviceIED::CtiMeterMachineStates_t nextstate = CtiDeviceIED::StateHandshakeAbort;

    switch (protocolstate)
    {
        case (CtiProtocolSixnet::GETTIMEOUT):
            {
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "unexpected protocol state ("<< protocolstate << ")");

                nextstate = CtiDeviceIED::StateAbsorb;

                break;
            }
    }

    return nextstate;
}

void CtiDeviceSixnet::setupGetRecord(CtiXfer &Transfer)
{
    if (isRecLT(_tail, _head))
    {
        int rtg = getRecordsToGet();

        _executionState = SXNT_DECODERECORDS;

        Transfer.setOutCount( assembleGetRecords(_tail, rtg) ); // Assemble the request message and return the byte count.

        Transfer.setNonBlockingReads(true);          // We want the port to only load up the data that is out there in the buffer.
        Transfer.setInCountExpected(9 + rtg * _recSize); // Unknown, but at least this much!
        Transfer.setInTimeout(3);
        Transfer.setCRCFlag(0);                      // Device manages all CRC computes.
    }
    else
    {
        // We are done with this looping effort. all records have been gathered!
        _executionState = SXNT_COMPLETE;

        // A null transfer I expect here.
        Transfer.setOutCount(0);
        Transfer.setNonBlockingReads(false);   // Make it ignore the port!
        Transfer.setInCountExpected(0);
    }
}

void CtiDeviceSixnet::checkStreamForTimeout(INT protocolreturn, CtiXfer &Transfer)
{
    static int infloopprevention = 0;

    if (CtiProtocolSixnet::GETTIMEOUT == protocolreturn )
    {
        // We've timed out and should retry, or abort
        if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
        {
            CTILOG_DEBUG(dout,
                    endl <<"Execution State = "<< _executionState <<
                    endl <<"Completed State = "<< _completedState
                    );
        }

        if ( getCurrentState() == CtiDeviceIED::StateHandshakeInitialize )
        {
            setCurrentState( CtiDeviceIED::StateHandshakeAbort );
        }
        else
        {
            setCurrentState( CtiDeviceIED::StateAbort );
        }

        _executionState = SXNT_COMPLETE;
        infloopprevention = 0;
    }
    else if (CtiProtocolSixnet::GETLEAD == protocolreturn && Transfer.getInCountActual() == 0)
    {

        if (infloopprevention++ < 10)
        {
            // seems like completed state would be where I wanted to go
            _executionState = SXNT_GETALIAS;
        }
        else
        {
            setCurrentState( CtiDeviceIED::StateAbort );

            CTILOG_WARN(dout, "SXNT_BAIL set to abort");

            _executionState = SXNT_BAIL;
            infloopprevention = 0;
        }
    }
    else
    {
        UINT bytesleft = getSixnetProtocol().getBytesLeftInRead();

        // We are not timed out, but are in an intermediate state and need MORE data from the port
        CTILOG_INFO(dout, "Going after some more bytes - "<< bytesleft <<"-byte(s) left");

        if (infloopprevention++ > 5)
        {
            setCurrentState( CtiDeviceIED::StateAbort );

            CTILOG_WARN(dout, "SXNT_BAIL set to abort");

            _executionState = SXNT_BAIL;
            infloopprevention = 0;
        }
        if (bytesleft == 0)
        {
            CTILOG_INFO(dout, "Protocol object returned "<< protocolreturn);

            _executionState = SXNT_RETURNDATA;
        }
        else
        {
            Transfer.setInBuffer(getRxBuffer() + Transfer.getInCountActual());
            Transfer.setNonBlockingReads(false);          // OK, Make sure we get at least this many!
            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( bytesleft );
            Transfer.setInTimeout( 1 );

            /**********************************
            * execution stat must be something that will get it thru the generation
            * state since all we are doing here is trying to retrieve more data
            ***********************************
            */
            _completedState = _executionState;
            _executionState = SXNT_MOREDATANEEDED;

            // reset this because if we don't get exactly the bytes needed, we'll timeout and
            // start over anyway
            infloopprevention = 0;
        }
    }

    return;
}

YukonError_t CtiDeviceSixnet::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if (!isScanFlagSet(ScanRateGeneral))
    {
        CTILOG_INFO(dout, "General Scan of device "<< getName() <<" in progress. Collect from "<< getLastLPTime());

        ULONG BytesWritten;

        // load profile information
        time_t         RequestedTime = 0L;
        time_t         DeltaTime;
        time_t         NowTime;

        if (OutMessage != NULL)
        {
            /*************************
             *   setting the current command in hopes that someday we don't have to use the command
             *   bits in the in message to decide what we're doing DLS
             **************************/
            setCurrentCommand(CmdLoadProfileData);
            OutMessage->Buffer.DUPReq.Command[0] = CmdLoadProfileData;

            // whether this is needed is decided later
            OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
            OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();

            // Load all the other stuff that is needed
            populateRemoteOutMessage(*OutMessage);
            OutMessage->Retry = 3;  //  override

            EstablishOutMessagePriority( OutMessage, ScanPriority );

            outList.push_back(OutMessage);
            OutMessage = NULL;
        }
        else
        {
            status = ClientErrors::MemoryAccess;
        }
    }

    return status;
}

YukonError_t CtiDeviceSixnet::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    /****************************
    *
    *  intialize these parameters to match the porter side's last settings.
    *
    *****************************/
    char tmpCurrentCommand = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0],
                             tmpCurrentState   = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    switch (tmpCurrentCommand)
    {
        case CmdScanData:
            {
                CTILOG_INFO(dout, "Scan decode for device "<< getName() <<" in progress");

                decodeResultScan (InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        case CmdLoadProfileData:
            {
                CTILOG_INFO(dout, "LP decode for device "<< getName() <<" in progress");

                // just in case we're getting an empty message
                if (tmpCurrentState == StateScanReturnLoadProfile)
                {
                    status = decodeResultLoadProfile (InMessage, TimeNow, vgList, retList, outList);
                }
                else
                {
                    CTILOG_ERROR(dout, "LP decode failed device "<< getName() <<" invalid state "<< getCurrentState());
                }

                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Invalid decode for "<< getName() <<"(command: "<< tmpCurrentCommand <<")");
            }
    }

    return status;
}

YukonError_t CtiDeviceSixnet::ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    CTILOG_INFO(dout, "ErrorDecode for device "<< getName() <<" in progress");

    YukonError_t retCode = ClientErrors::None;
    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            string(InMessage.Return.CommandStr),
                                            GetErrorString(InMessage.ErrorCode),
                                            InMessage.ErrorCode,
                                            InMessage.Return.RouteID,
                                            InMessage.Return.RetryMacroOffset,
                                            InMessage.Return.Attempt,
                                            InMessage.Return.GrpMsgID,
                                            InMessage.Return.UserID);


    if (pPIL != NULL)
    {
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if (pMsg != NULL)
        {
            pMsg->insert( -1 );          // This is the dispatch token and is unimplemented at this time
            pMsg->insert(CtiCommandMsg::OP_DEVICEID);   // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());         // The id (device or point which failed)
            pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
            pMsg->insert(InMessage.ErrorCode);


            pPIL->PointData().push_back(pMsg);
            pMsg = NULL;   // We just put it on the list...
        }

        // send the whole mess to dispatch
        if (pPIL->PointData().size() > 0)
        {
            retList.push_back( pPIL );
        }
        else
        {
            delete pPIL;
        }
    }

    return retCode;
}

void CtiDeviceSixnet::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    // really only have this data.
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    // Now pervert the data to our needs.
    _msStationNum = 0;
    _targetStationNum = getIED().getSlaveAddress();    // Lie to me baby
    _logfileName = getIED().getPassword();             // Lie to me baby

    getSixnetProtocol().setStationNumber(_msStationNum);        // Update the nonsense
    getSixnetProtocol().setDestination(_targetStationNum);      // Update the nonsense
}



YukonError_t CtiDeviceSixnet::decodeResultLoadProfile (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;


    int recCnt = *((const int*)&InMessage.Buffer.DUPSt.DUPRep.Message);
    const BYTE *bpcurr = (const BYTE *)&InMessage.Buffer.DUPSt.DUPRep.Message;

    bpcurr += sizeof(int);  // get past the count.

    CtiPointSPtr pPoint ;
    ULONG lastLPTime = getLastLPTime().seconds();  // OK I think, this IS a scanner side value..

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            string(InMessage.Return.CommandStr),
                                            string(),
                                            InMessage.ErrorCode,
                                            InMessage.Return.RouteID,
                                            InMessage.Return.RetryMacroOffset,
                                            InMessage.Return.Attempt,
                                            InMessage.Return.GrpMsgID,
                                            InMessage.Return.UserID);

    CtiReturnMsg *pRetMsg = NULL;


    for (int i = 0; i < recCnt; i++)
    {
        const CtiSixnetLPData *pSxnt = (const CtiSixnetLPData *)bpcurr;
        CtiPointDataMsg *pData    = NULL;

        bpcurr += sizeof(CtiSixnetLPData);

        CtiTime logTime(pSxnt->time);    // When was this log taken?

        if (logTime > getLastLPTime())
        {
            if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
            {
                CTILOG_DEBUG(dout, "Time = "<< logTime <<"  Offset "<< pSxnt->offset <<" = "<< pSxnt->val);
            }

            if ((pPoint = getDevicePointOffsetTypeEqual(pSxnt->offset, pSxnt->type)))
            {
                // We have a point match here...

                string resString;   // Add description to this here please!
                double val;

                if (pPoint->isNumeric())
                {
                    val = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pSxnt->val);
                }
                else
                {
                    val = pSxnt->val;
                }

                pRetMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                  string(InMessage.Return.CommandStr),
                                                  resString,
                                                  InMessage.ErrorCode,
                                                  InMessage.Return.RouteID,
                                                  InMessage.Return.RetryMacroOffset,
                                                  InMessage.Return.Attempt,
                                                  InMessage.Return.GrpMsgID,
                                                  InMessage.Return.UserID);

                //create a new data message
                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), resString, TAG_POINT_LOAD_PROFILE_DATA);

                if (pData != NULL)
                {
                    pData->setTime( pSxnt->time );

                    if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                    {
                        CTILOG_DEBUG(dout, pData);
                    }

                    pPIL->PointData().push_back(pData);

                    pData = 0;
                }
            }
            else
            {
                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    CTILOG_DEBUG(dout, "No point of type "<< pSxnt->type <<"  offset "<< pSxnt->offset);
                }
            }

            if (pSxnt->tag & SXNT_TAG_FINAL_MESSAGE)
            {
                if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
                {
                    CTILOG_DEBUG(dout, "pSxnt->tag final message");
                }

                setLastLPTime (logTime);

                resetScanFlag(ScanRateGeneral);
                resetScanFlag(ScanException);
            }
        }
        else if (getDebugLevel() & DEBUGLEVEL_SIXNET_DEVICE)
        {
            CTILOG_DEBUG(dout, "OLD DATA: " << logTime <<" Type "<< pSxnt->type <<" Offset "<< pSxnt->offset << " = " << pSxnt->val);
        }
    }




    // send the whole mess to dispatch
    if (pPIL->PointData().size() > 0)
    {
        retList.push_back( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    return status;
}



YukonError_t CtiDeviceSixnet::decodeResultScan(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    resetScanFlag(ScanForced);
    resetScanFlag(ScanRateGeneral);

    return ClientErrors::None;
}


uint32 CtiDeviceSixnet::getRecordsToGet() const
{
    uint32 getthismany;

    INT maxget = 239 / _recSize;

    // OK, we can get 239 bytes.  We know _recSize, _head, _tail,...

    if (isRecLT(_tail + maxget, _head))
    {
        getthismany = maxget;
    }
    else // Switch over to 1 at a time...
    {
        getthismany = 1;
    }

    return getthismany;
}
