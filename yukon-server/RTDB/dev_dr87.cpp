#include "yukon.h"
#include <iostream>
#include <vector>
using namespace std ;

#include <rw/rwtime.h>
#include <rw/rwdate.h>


#include "porter.h"
#include "dev_ied.h"
#include "dev_dr87.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "dupreq.h"
#include "dialup.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

//#define PRINT_DEBUG
INT             DR87Retries = 7;

CtiDeviceDR87::CtiDeviceDR87(BYTE *aPtr, BYTE *lpPtr, ULONG cnt) :
    iTotalByteCount (cnt),
    iPorterSide(NULL),
    iDataBuffer(NULL),
    iLoadProfileBuffer(lpPtr)
{
}


CtiDeviceDR87::~CtiDeviceDR87 ()
{
    freeDataBins();
}

CtiDeviceIED::CtiMeterMachineStates_t CtiDeviceDR87::getRequestedState () const
{
  return iRequestedState;
}
CtiDeviceDR87& CtiDeviceDR87::setRequestedState (CtiDeviceIED::CtiMeterMachineStates_t aState)
{
  iRequestedState = aState;
  return *this;
}


ULONG    CtiDeviceDR87::getTotalByteCount() const
{
    return iTotalByteCount;
}

CtiDeviceDR87& CtiDeviceDR87::setTotalByteCount(ULONG c)
{
    iTotalByteCount = c;
    return *this;
}

INT CtiDeviceDR87::getRetryAttempts () const
{
    return iRetryAttempts;
}

CtiDeviceDR87& CtiDeviceDR87::setRetryAttempts (INT aRetry)
{
    iRetryAttempts = aRetry;
    return *this;
}

CtiDR87PorterSide * CtiDeviceDR87::getPorterSide() const
{
    return iPorterSide;
}

CtiDeviceDR87& CtiDeviceDR87::setPorterSide(CtiDR87PorterSide *aPorterSide)
{
    iPorterSide = aPorterSide;
    return *this;
}

BOOL    CtiDeviceDR87::syncByteHasBeenReceived() const
{
    if (iPorterSide != NULL)
        return getPorterSide()->syncByteHasBeenReceived();

    return false;
}

CtiDeviceDR87& CtiDeviceDR87::setSyncByteReceived(BOOL c)
{
    if (iPorterSide != NULL)
        getPorterSide()->setSyncByteReceived( c );
    return *this;
}
BOOL    CtiDeviceDR87::ackNakByteHasBeenReceived() const
{
    if (iPorterSide != NULL)
        return getPorterSide()->ackNakByteHasBeenReceived();

    return false;
}

CtiDeviceDR87& CtiDeviceDR87::setAckNakByteReceived(BOOL c)
{
    if (iPorterSide != NULL)
        getPorterSide()->setAckNakByteReceived( c );
    return *this;
}


BOOL    CtiDeviceDR87::isDataMsgComplete() const
{
    if (iPorterSide != NULL)
        return getPorterSide()->isDataMsgComplete();
    return false;
}

CtiDeviceDR87& CtiDeviceDR87::setDataMsgComplete(BOOL c)
{
    if (iPorterSide != NULL)
        getPorterSide()->setDataMsgComplete(c);
    return *this;
}

const BYTE*    CtiDeviceDR87::getWorkBuffer() const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getWorkBuffer();

    return NULL;
}

BYTE*    CtiDeviceDR87::getWorkBuffer()
{
    if (iPorterSide != NULL)
        return getPorterSide()->getWorkBuffer();

    return NULL;
}

INT CtiDeviceDR87::getTotalBytesExpected () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getTotalBytesExpected();
    return 0;
}

CtiDeviceDR87& CtiDeviceDR87::setTotalBytesExpected (INT aByteCnt)
{
    if (iPorterSide != NULL)
        getPorterSide()->setTotalBytesExpected ( aByteCnt );
    return *this;
}
INT CtiDeviceDR87::getByteNumber () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getByteNumber();
    return 0;
}

CtiDeviceDR87& CtiDeviceDR87::setByteNumber (INT aByteCnt)
{
    if (iPorterSide != NULL)
        getPorterSide()->setByteNumber ( aByteCnt );
    return *this;
}

USHORT CtiDeviceDR87::getOldestIntervalByteOffset () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getOldestIntervalByteOffset();
    return 0;
}

CtiDeviceDR87& CtiDeviceDR87::setOldestIntervalByteOffset (INT aByteOffset)
{
    if (iPorterSide != NULL)
        getPorterSide()->setOldestIntervalByteOffset ( aByteOffset );
    return *this;
}

USHORT CtiDeviceDR87::getCurrentByteOffset () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getCurrentByteOffset();
    return 0;
}

CtiDeviceDR87& CtiDeviceDR87::setCurrentByteOffset (INT aByteOffset)
{
    if (iPorterSide != NULL)
        getPorterSide()->setCurrentByteOffset ( aByteOffset );
    return *this;
}

USHORT CtiDeviceDR87::getMassMemoryStart () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getMassMemoryStart();
    return 0;
}

CtiDeviceDR87& CtiDeviceDR87::setMassMemoryStart (INT aBlockSize)
{
    if (iPorterSide != NULL)
        getPorterSide()->setMassMemoryStart ( aBlockSize );
    return *this;
}

USHORT CtiDeviceDR87::getMassMemoryStop () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getMassMemoryStop();
    return 0;
}

CtiDeviceDR87& CtiDeviceDR87::setMassMemoryStop (INT aBlockSize)
{
    if (iPorterSide != NULL)
        getPorterSide()->setMassMemoryStop ( aBlockSize );
    return *this;
}

INT CtiDeviceDR87::getLogoffFunction () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getLogoffFunction();
    return -1;
}

CtiDeviceDR87& CtiDeviceDR87::setLogoffFunction (INT aFunc)
{
    if (iPorterSide != NULL)
        getPorterSide()->setLogoffFunction ( aFunc );
    return *this;
}

INT CtiDeviceDR87::getNumberOfIncompleteMsgs () const
{
    if (iPorterSide != NULL)
        return getPorterSide()->getNumberOfIncompleteMsgs();
    return -1;
}

CtiDeviceDR87& CtiDeviceDR87::setNumberOfIncompleteMsgs (INT aNumberOfIncompleteMsgs)
{
    if (iPorterSide != NULL)
        getPorterSide()->setNumberOfIncompleteMsgs ( aNumberOfIncompleteMsgs );
    return *this;
}




INT CtiDeviceDR87::generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT retCode = NORMAL;


    switch (getCurrentState())
    {
        case StateHandshakeInitialize:
            {
                // we send the same things each time we send a command so no real handshake
                setRetryAttempts(3);
                setAttemptsRemaining (getRetryAttempts());
                setNumberOfIncompleteMsgs(6);

                // not we drop to security immediately
            }
        case StateHandshakeSendSecurity:
            {
                // retrive the password
                char * stopper;
                BYTEULONG flipper;
                flipper.ul = strtoul (getIED().getPassword().data(),&stopper,10);

                /**************************
                *  Send the login command
                ***************************
                */
                sprintf((CHAR *)Transfer.getOutBuffer(),
                        "%c%c%c%c%c%c",
                        DR87_SYNC,
                        DR87_LOGIN,
                        flipper.ch[3],
                        flipper.ch[2],
                        flipper.ch[1],
                        flipper.ch[0]);

                Transfer.setOutCount (6);
                Transfer.setInCountExpected (200);
                Transfer.setInTimeout (1);

                Transfer.setCRCFlag (0);
                calculateCRC (Transfer.getOutBuffer(), Transfer.getOutCount(), true);
                Transfer.setOutCount (Transfer.getOutCount()+2);

                setPreviousState (StateHandshakeSendSecurity);
                setCurrentState (StateHandshakeDecodeSecurity);
                setTotalBytesExpected (4);

                // this helps track when we have a full message
                setDataMsgComplete (false);
                setAckNakByteReceived(false);
                setSyncByteReceived (false);
                break;
            }

        case StateScanValueSet1FirstScan:
        case StateScanValueSet1:
            {
                // receive only
                Transfer.setInCountExpected (200);
                Transfer.setOutCount (0);
                setCurrentState (StateHandshakeDecodeSecurity);
                break;
            }

        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateHandshakeAbort);
                retCode = !NORMAL;
            }
    }
    return retCode;
}

INT CtiDeviceDR87::generateCommand (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    USHORT retCode=NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
//            retCode = generateCommandSelectMeter (Transfer);
                break;
            }

        case CmdScanData:
            {
                retCode = generateCommandScan (Transfer, traceList);
                break;
            }

        case CmdLoadProfileData:
            {
                retCode = generateCommandLoadProfile (Transfer, traceList);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = !NORMAL;
                break;
            }

    }
    return retCode;
}


INT CtiDeviceDR87::generateCommandScan (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode = NORMAL;

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        {
            setRetryAttempts(4);
            setNumberOfIncompleteMsgs(6);
        }
        case StateScanValueSet1FirstScan:
        {
            setAttemptsRemaining(getRetryAttempts());
        }

        case StateScanValueSet1:
        {
            fillUploadTransferObject (Transfer, DR87_DUMP_MEMORY, 0x200, 4, ((25-4)+1));
            setTotalByteCount (0);

            // need this to get back here in case of a CRC problem
            setRequestedState (StateScanValueSet1);
            setPreviousState (StateScanValueSet1);
            setCurrentState (StateScanDecode1);
            break;
        }
        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
        {
            fillUploadTransferObject (Transfer, DR87_DUMP_MEMORY, 0x200, 212, ((340-212)+1));
            setTotalByteCount (0);
            // need this to get back here in case of a CRC problem
            setRequestedState (StateScanValueSet2);
            setPreviousState (StateScanValueSet2);
            setCurrentState (StateScanDecode2);
            break;
        }
        case StateScanValueSet8:
        {
            // receive only
//            Transfer.setInCountExpected (200);
            Transfer.setOutCount (0);
            setCurrentState (getPreviousState());
            setPreviousState (StateScanValueSet8);
            break;
        }

        case StateScanSendTerminate:
        {
            generateCommandTerminate (Transfer, traceList);
            break;
        }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            setRequestedState (StateScanAbort);
            generateCommandTerminate (Transfer, traceList);
            retCode = StateScanAbort;
    }

    return retCode;
}


INT CtiDeviceDR87::generateCommandTerminate (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    BYTEUSHORT worker;
    INT retVal = NORMAL;

    worker.sh = getLogoffFunction();

    // if this is negative, we don't have it yet
    if (worker.sh == -1)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to generate a terminate, letting " << getName() << " hang up on its own " << endl;
        }

        Transfer.setOutCount (0);
        Transfer.setInCountExpected (0);
        Transfer.setInTimeout (1);
        setCurrentState (StateScanDecode7);
        retVal = !NORMAL;
    }
    else
    {
        sprintf((CHAR *)Transfer.getOutBuffer(),
                "%c%c%c%c%c%c",
                DR87_SYNC,
                DR87_EXECUTE_FUNCTION,
                worker.ch[0],
                worker.ch[1],
                0,
                0);

        Transfer.setOutCount (6);
        Transfer.setInCountExpected (6);
        CTISleep(10);
//        Transfer.setInCountExpected (10);
        Transfer.setInTimeout (1);

        Transfer.setCRCFlag (0);
        calculateCRC (Transfer.getOutBuffer(), Transfer.getOutCount(), true);
        Transfer.setOutCount (Transfer.getOutCount()+2);

        setTotalBytesExpected (4);

        // this helps track when we have a full message
        setDataMsgComplete (false);
        setAckNakByteReceived(false);
        setSyncByteReceived (false);

        setPreviousState (StateScanSendTerminate);
        setCurrentState (StateScanDecodeTerminate);

        /***************************
        * because we are reading smaller buffers trying to catch the
        * terminate message, I'm going to give myself more incomplete messages
        ****************************
        */
        setNumberOfIncompleteMsgs(20);
    }
    return retVal;
}

INT CtiDeviceDR87::generateCommandLoadProfile (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode = NORMAL;
    DR87LoadProfile_t * localLP = (DR87LoadProfile_t*)iLoadProfileBuffer;

    /*
     *  This is the load profile request
     */

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanComplete:
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
        {
            setAttemptsRemaining(getRetryAttempts());
            setNumberOfIncompleteMsgs(6);
        }

        case StateScanValueSet1:
        {
            fillUploadTransferObject (Transfer, DR87_DUMP_MEMORY, 0x200, 4, ((25-4)+1));
            setTotalByteCount (0);
            // need this to get back here in case of a CRC problem
            setRequestedState (StateScanValueSet1);
            setPreviousState (StateScanValueSet1);
            setCurrentState (StateScanDecode1);
            break;
        }
        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
        {
            fillUploadTransferObject (Transfer, DR87_DUMP_MEMORY, 0x200, 212, ((340-212)+1));
            setTotalByteCount (0);
            // need this to get back here in case of a CRC problem
            setRequestedState (StateScanValueSet2);
            setPreviousState (StateScanValueSet2);
            setCurrentState (StateScanDecode2);
            break;
        }
        case StateScanValueSet3FirstScan:
        case StateScanValueSet3:
        {
            bool validMeterClock = true;

            if (shouldRetrieveLoadProfile (localLP->porterLPTime, localLP->config.intervalLength))
            {
                /**********************
                * we're going to back up to at least the appropriate time
                * and decode from the last interval retrieved backwards
                ***********************
                */
                ULONG   intervalsToCollect = (localLP->config.lastIntervalTime - localLP->porterLPTime) /
                                                (localLP->config.intervalLength * 60) + 2;
                // intervals * number of channels * 2 bytes per channel = total bytes to collect
                ULONG   bytesToCollect = intervalsToCollect * localLP->config.numberOfChannels * 2;
                ULONG   bytesSoFar = 0;

                /*************************
                 * check and make sure its reasonable, no more than 32 days of data
                 * the DR87 doesn't like invalid commands so we need to be very sure
                 *************************
                 */
                if (intervalsToCollect < (32*(24*(60/localLP->config.intervalLength))))
                {
                    /************************
                    *  our current memory pointer will be the oldest pointer - the 0x1ff offset from
                    *  the document so we are dealing with real RAM addresses
                    *************************
                    */
                    setCurrentByteOffset (getOldestIntervalByteOffset() - 0x01ff);

                    for (bytesSoFar =0; bytesSoFar < bytesToCollect; bytesSoFar+= localLP->config.numberOfChannels*2)
                    {
                        // loop the channels separately in case we don't have exact byte boundary matches
                        for (int channel=0; channel < localLP->config.numberOfChannels; channel++)
                        {
                            /*************************
                            *  we need to walk back in the memory until we hit the start address listed
                            *  in bytes 3-4.   The address there is actual RAM address so we must first
                            *  subtract 0x01ff (511) from the address to get rid of our document offset of 0x200
                            **************************
                            */
                            if ((getCurrentByteOffset()-2) >= (getMassMemoryStart() - 0x1ff))
                            {
                                setCurrentByteOffset(getCurrentByteOffset()-2);
                            }
                            else
                            {
                                // must go from the stop address now
                                setCurrentByteOffset(getMassMemoryStop() - 0x1ff);
                            }
                        }
                    }

                    if (bytesToCollect > 256)
                    {
                        localLP->config.blockSize = 256;
                    }
                    else
                    {
                        localLP->config.blockSize = bytesToCollect;
                    }


                    // we now know where to start, we just need to decide how much to ask for
                    if ((getCurrentByteOffset() + localLP->config.blockSize) > (getMassMemoryStop()-0x1ff))
                    {
                        localLP->config.blockSize = ((getMassMemoryStop()-0x1ff) - getCurrentByteOffset());
                    }

                    fillUploadTransferObject (Transfer, DR87_DUMP_MEMORY, 0x200, getCurrentByteOffset(), localLP->config.blockSize);

    #if 0
                    /*************************
                    * Check the validity of the time received
                    **************************
                    */
                    if ((RWTime(LPTime+rwEpoch) < (RWTime()-(2*86400))) ||
                        (RWTime(LPTime+rwEpoch) > (RWTime()+(2*86400))))
                    {
                        /***********************
                        * if meter time is not within a 2 day window, its
                        * invalid
                        ************************
                        */
                        validMeterClock = false;
                    }
                    else
                    {
                        localMMLoadProfile[localMMInputs->MMIndex].RecordTime    = LPTime;

                        // And the next INDEX
                        localMMInputs->MMIndex += 1;
                    }
    #endif

                    setTotalByteCount (0);
                    // need this to get back here in case of a CRC problem
                    setRequestedState (StateScanValueSet3);
                    setPreviousState (StateScanValueSet3);
                    setCurrentState (StateScanDecode3);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error calculating intervals to collect for " << getName() << " no data will be collected this scan" << endl;
                    }
                    setRequestedState (StateScanAbort);
                    generateCommandTerminate(Transfer, traceList);
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Load profile for " << getName() << " will not be collected this scan" << endl;
                }
                setRequestedState (StateScanComplete);
                generateCommandTerminate(Transfer, traceList);
            }
            break;
        }

        case StateScanValueSet4FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet4:
        {
            /************************
            *  our current memory pointer will be the oldest pointer - the 0x1ff offset from
            *  the document so we are dealing with real RAM addresses
            *************************
            */
            setCurrentByteOffset (getCurrentByteOffset () + localLP->config.blockSize);
            localLP->config.blockSize = calculateBlockSize();

            /************************
            * because the DR-87 doesn't recover at all, make sure we haven't
            * messed up the calc and are requesting more than 256 bytes
            *************************
            */
            if (localLP->config.blockSize > 256)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error calculating bytes to collect for " << getName() << " no data will be collected this scan" << endl;
                }
                setRequestedState (StateScanAbort);
                generateCommandTerminate(Transfer, traceList);
            }
            else
            {
                fillUploadTransferObject (Transfer, DR87_DUMP_MEMORY, 0x200, getCurrentByteOffset(), localLP->config.blockSize);

                setTotalByteCount (0);
                // need this to get back here in case of a CRC problem
                setRequestedState (StateScanValueSet4);
                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode3);
            }
            break;
        }


        case StateScanValueSet8:
        {
            // we don't need to reset in count, it goes with whatever we used before
//            Transfer.setInCountExpected (200);
            Transfer.setOutCount (0);
            setCurrentState (getPreviousState());
            setPreviousState (StateScanValueSet8);
            break;
        }

        case StateScanSendTerminate:
        {
            generateCommandTerminate (Transfer, traceList);
            break;
        }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            setRequestedState (StateScanAbort);
            generateCommandTerminate (Transfer, traceList);
            retCode = StateScanAbort;
    }

    return retCode;
}




INT CtiDeviceDR87::decodeResponseHandshake (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
//   BYTE  Command;
    int cnt=0;
    int retVal;


    switch (getCurrentState())
    {
        case StateHandshakeDecodeSecurity:
            {
                // retrieve the message from teh data stream
                retVal= getMessageInDataStream (Transfer,commReturnValue);

                if (retVal == DR87_BAD_CRC)
                {
                    setCurrentState (getPreviousState());
                }
                else if (retVal == DR87_MSG_INCOMPLETE)
                {
                    // if we get more than 6 incompletes, assume bad data
                    if (getNumberOfIncompleteMsgs() <= 0)
                        setCurrentState (StateHandshakeAbort);
                    else
                        setCurrentState (StateScanValueSet1);
                }
                else if (retVal == DR87_NAK)
                {
                    setAttemptsRemaining(getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        setCurrentState (getPreviousState());
                    }
                    else
                    {
                        setCurrentState (StateHandshakeAbort);
                    }
                }
                else if (retVal == DR87_MSG_COMPLETE)
                {
                    setCurrentState (StateHandshakeComplete);
                }
                else
                {
                    setAttemptsRemaining(getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        setCurrentState (getPreviousState());
                    }
                    else
                    {
                        setCurrentState (StateHandshakeAbort);
                    }
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                setCurrentState (StateHandshakeAbort);
                break;
            }
    }
    return NORMAL;
}



INT CtiDeviceDR87::decodeResponse (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT retCode = NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
                // decode the return message
//            retCode = decodeResponseSelectMeter (Transfer, commReturnValue);
                break;
            }

        case CmdScanData:
            {
                // decode the return message
                retCode = decodeResponseScan (Transfer, commReturnValue, traceList);
                break;
            }

        case CmdLoadProfileData:
            {
                // decode the return message
                retCode = decodeResponseLoadProfile (Transfer, commReturnValue, traceList);
                break;
            }

        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = !NORMAL;
                break;
            }
    }
    return retCode;
}

INT CtiDeviceDR87::decodeResponseScan (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    int retCode = NORMAL,x;
    int msgDecodeRetVal;
    DR87ScanData_t * localData = (DR87ScanData_t*)iDataBuffer;

    msgDecodeRetVal = getMessageInDataStream (Transfer, commReturnValue);

    switch (msgDecodeRetVal)
    {
        case DR87_MSG_INCOMPLETE:
            {
                if (getNumberOfIncompleteMsgs() <= 0)
                {
                    // if we were decoding a terminate, check the last lp flag
                    if (getCurrentState() == StateScanDecodeTerminate)
                    {
                        setCurrentState (getRequestedState());
                    }
                    else
                    {
                        setRequestedState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                }
                else
                {
                    setPreviousState (getCurrentState());
                    setCurrentState (StateScanValueSet8);
                }
                break;
            }
        case DR87_MSG_COMPLETE:
            {
                // get appropriate data
                switch (getCurrentState())
                {
                    case StateScanDecode1:
                    {
                        BYTEUSHORT worker;
                        worker.ch[0] = getWorkBuffer()[25-getByteNumber()+3];
                        worker.ch[1] = getWorkBuffer()[24-getByteNumber()+3];
                        setLogoffFunction (worker.sh);

                        setCurrentState (StateScanValueSet2FirstScan);
                        break;
                    }
                    case StateScanDecode2:
                    {
                        /*********************
                        *  note that all of these offsets need a plus 3 to jump the sync,ack and echo bytes
                        * inside of the read buffer
                         **********************
                        */
                        localData->numberOfChannels = (USHORT)getWorkBuffer()[221-getByteNumber()+3];

                        typedef union
                        {
                            UCHAR    ch[8];
                            ULONG    db;
                        } BYTEDOUBLE;

                        BYTEDOUBLE usage;
                        usage.db = 0.0;

                        for (x=0; x < localData->numberOfChannels; x++)
                        {
                            // initialize then fill
                            localData->scaleFactor[x] = 0;
                            localData->scaleFactor[x] =
                                (USHORT)getWorkBuffer()[(225+x)-getByteNumber()+3];

                            // initialize then fill
                            localData->kvalue[x] = 0;
                            localData->kvalue[x] =
                                (DOUBLE)BCDtoBase10 (&getWorkBuffer()[(229+x*3)-getByteNumber()+3], 3) / 1000.0;

                            // initialize then fill
                            localData->totalPulses[x] = 0;
                            for (int y=4; y >= 0; y--)
                            {
                                /**************************
                                *  high byte is 320, each usage is 5 bytes long
                                ***************************
                                */
                                usage.ch[y]  = getWorkBuffer()[((320+x*5)+4-y)-getByteNumber()+3];
                            }

                            localData->totalPulses[x] = usage.db;
                        }

                        if( DebugLevel & 0x0001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() <<"  ----- DR-87 Configuration ------ " << endl;
                            dout << "Channels " << localData->numberOfChannels << endl;
                            dout << "Channel    scale factor  kvalue (w/p)    usage" << endl;
                            for (x=0; x < localData->numberOfChannels; x++)
                            {
                                dout << "     " << x <<  "            " << localData->scaleFactor[x] << "        "  << localData->kvalue[x];
                                dout << "       "  << localData->totalPulses[x] << endl;
                            }
                        }

                        setCurrentState (StateScanComplete);
                        setCurrentCommand(CmdLoadProfileTransition);
                        break;
                    }

                    case StateScanDecodeTerminate:
                    {
                        // set current state to whatever we had before the terminate abort or complete
                        setCurrentState (getRequestedState());
                        break;
                    }
                    case StateScanDecode7:
                    {
                        // problem generating the terminate command is the only way we get here
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                        break;
                    }

                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                        }
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                }
                break;
            }
        case DR87_COMM_ERROR:
        case DR87_BAD_CRC:
        case DR87_NAK:
        default:
            {
                setAttemptsRemaining(getAttemptsRemaining() - 1);
                if (getAttemptsRemaining() > 0)
                {
                    setCurrentState (getRequestedState());
                }
                else
                {
                    if (getCurrentState() == StateScanDecodeTerminate)
                    {
                        setCurrentState (getRequestedState());
                    }
                    else
                    {
                        setRequestedState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                }
                break;
            }
    }
    return retCode;
}

INT CtiDeviceDR87::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    int retCode = NORMAL,x;
    int msgDecodeRetVal;
    DR87LoadProfile_t * localLP = (DR87LoadProfile_t*)iLoadProfileBuffer;

    msgDecodeRetVal = getMessageInDataStream (Transfer, commReturnValue);

    switch (msgDecodeRetVal)
    {
        case DR87_MSG_INCOMPLETE:
            {
                if (getNumberOfIncompleteMsgs() <= 0)
                {
                    // if we were decoding a terminate, check the last lp flag
                    if (getCurrentState() == StateScanDecodeTerminate)
                    {
                        setCurrentState (getRequestedState());

//                        if (localLP->lastLPMessage)
//                            setCurrentState (StateScanComplete);
//                        else
//                            setCurrentState (StateScanAbort);

                    }
                    else
                    {
                        setRequestedState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                }
                else
                {
                    setPreviousState (getCurrentState());
                    setCurrentState (StateScanValueSet8);
                }
                break;
            }
        case DR87_MSG_COMPLETE:
            {
                // get appropriate data
                switch (getCurrentState())
                {
                    case StateScanDecode1:
                    {

                        /*********************
                        *  note that all of these offsets need a plus 3 to jump the sync,ack and echo bytes
                        * inside of the read buffer
                        ***********************
                        */
                        BYTEUSHORT worker;
                        worker.ch[0] = getWorkBuffer()[5 - getByteNumber() +3];
                        worker.ch[1] = getWorkBuffer()[4 - getByteNumber() +3];
                        setMassMemoryStart (worker.sh);

                        worker.ch[0] = getWorkBuffer()[7-getByteNumber()+3];
                        worker.ch[1] = getWorkBuffer()[6-getByteNumber()+3];
                        setMassMemoryStop (worker.sh);

                        worker.ch[0] = getWorkBuffer()[25-getByteNumber()+3];
                        worker.ch[1] = getWorkBuffer()[24-getByteNumber()+3];
                        setLogoffFunction (worker.sh);


                        if( DebugLevel & 0x0001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << "  ----- DR-87 Configuration ------ " << endl;
                            dout << "Mass Memory Range" << endl;
                            dout << "Start   : " << getMassMemoryStart() << endl;
                            dout << "Stop    : " << getMassMemoryStop() << endl;
                            dout << "Logoff  : " << getLogoffFunction() << endl;
                        }
                        setCurrentState (StateScanValueSet2FirstScan);
                        break;
                    }
                    case StateScanDecode2:
                    {
                        /*********************
                        *  note that all of these offsets need a plus 3 to jump the sync,ack and echo bytes
                        * inside of the read buffer
                         **********************
                        */
                        localLP->config.numberOfChannels =
                        (USHORT)getWorkBuffer()[221-getByteNumber()+3];

                        localLP->config.intervalLength =
                        (USHORT)getWorkBuffer()[222-getByteNumber()+3];

                        localLP->config.lastIntervalTime = RWTime(RWDate(getWorkBuffer()[311-getByteNumber()+3],
                                                                         getWorkBuffer()[310-getByteNumber()+3],
                                                                         getWorkBuffer()[312-getByteNumber()+3]+2000),
                                                                  getWorkBuffer()[307-getByteNumber()+3],
                                                                  getWorkBuffer()[308-getByteNumber()+3]).seconds();
                        BYTEUSHORT worker;
                        worker.ch[0] =  getWorkBuffer()[305-getByteNumber()+3];
                        worker.ch[1] =  getWorkBuffer()[304-getByteNumber()+3];
                        setOldestIntervalByteOffset (worker.sh);

                        typedef union
                        {
                            UCHAR    ch[8];
                            ULONG    db;
                        } BYTEDOUBLE;

                        BYTEDOUBLE usage;
                        usage.db = 0.0;


                        for (x=0; x < localLP->config.numberOfChannels; x++)
                        {
                            // initialize then fill
                            localLP->config.scaleFactor[x] = 0;
                            localLP->config.scaleFactor[x] =
                                (USHORT)getWorkBuffer()[(225+x)-getByteNumber()+3];

                            // initialize then fill
                            localLP->config.kvalue[x] = 0;
                            localLP->config.kvalue[x] =
                                (DOUBLE)BCDtoBase10 (&getWorkBuffer()[(229+x*3)-getByteNumber()+3], 3) / 1000.0;

                            // initialize then fill
                            localLP->channelUsage[x] = 0;
                            for (int y=4; y >= 0; y--)
                            {
                                /**************************
                                *  high byte is 320, each usage is 5 bytes long
                                ***************************
                                */
                                usage.ch[y]  = getWorkBuffer()[((320+x*5)+4-y)-getByteNumber()+3];
                            }

                            localLP->channelUsage[x] = usage.db;
                        }

                        if( DebugLevel & 0x0001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "  ----- DR-87 Configuration ------ " << endl;
                            dout << "Channels " << localLP->config.numberOfChannels << endl;
                            dout << "Interval " << localLP->config.intervalLength << endl;
                            dout << "Last Interval " << RWTime(localLP->config.lastIntervalTime).asString() << endl;
                            dout << "Old Mem Ptr " << getOldestIntervalByteOffset() << endl;

                            dout << "Channel    scale factor  kvalue (w/p)    usage" << endl;
                            for (x=0; x < localLP->config.numberOfChannels; x++)
                            {
                                dout << "     " << x <<  "            " << localLP->config.scaleFactor[x] << "        "  << localLP->config.kvalue[x];
                                dout << "       "  << localLP->channelUsage[x] << endl;
                            }
                        }
                        setCurrentState (StateScanValueSet3FirstScan);
                        break;
                    }

                    case StateScanDecode3:
                        {
                            // return the load profile data
                            memcpy (&localLP->loadProfileData, &getWorkBuffer()[3], localLP->config.blockSize);

                            // see if we are done
                            if ((getCurrentByteOffset() + localLP->config.blockSize) >= (getOldestIntervalByteOffset()-0x1ff))
                            {
                                // check if we may have wrapped around
                                if (getCurrentByteOffset () > (getOldestIntervalByteOffset ()-0x1ff))
                                {
                                    if( DebugLevel & 0x0001 )
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "  ----- DR-87 wrapped around going to get more lp data ------ " << endl;
                                        dout << " Current Byte Offset: " << getCurrentByteOffset() << endl;
                                        dout << " Block size: " << localLP->config.blockSize << endl;
                                        dout << " Oldest byte offset:  " << (getOldestIntervalByteOffset()-0x1ff) << endl;
                                    }

                                    // we're not done yet
                                    localLP->lastLPMessage = false;

                                    // decode and continue
                                    setPreviousState(StateScanValueSet4FirstScan);
                                }
                                else
                                {
                                    // set this to last message
                                    localLP->lastLPMessage = true;

                                    // if we are done, send the hangup command
                                    setRequestedState (StateScanComplete);
                                    setPreviousState (StateScanSendTerminate);
                                }
                            }
                            else
                            {
                                if( DebugLevel & 0x0001 )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "  ----- DR-87 Going to get more lp data ------ " << endl;
                                    dout << " Current Byte Offset: " << getCurrentByteOffset() << endl;
                                    dout << " Block size: " << localLP->config.blockSize << endl;
                                    dout << " Oldest byte offset:  " << (getOldestIntervalByteOffset()-0x1ff) << endl;
                                }

                                // we're not done yet
                                localLP->lastLPMessage = false;

                                // decode and continue
                                setPreviousState(StateScanValueSet4FirstScan);
                            }

                            setCurrentState (StateScanReturnLoadProfile);
                            break;
                        }
                    case StateScanDecodeTerminate:
                        {
                            /*******************
                            * don't really worry about the CRC on the return message
                            * if we aren't nak'ed, we're ok and the DR-87 will hang up
                            ********************
                            */
                            setCurrentState (getRequestedState());
                            break;
                        }
                    case StateScanDecode7:
                        {
                            // problem generating the terminate command is the only way we get here
                            setCurrentState (StateScanAbort);
                            retCode = StateScanAbort;
                            break;
                        }

                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                        }
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                }
                break;
            }
        case DR87_BAD_CRC:
            {
                setAttemptsRemaining(getAttemptsRemaining() - 1);
                if (getAttemptsRemaining() > 0)
                {
                    setCurrentState (getRequestedState());
                }
                else
                {
                    if (getCurrentState() == StateScanDecodeTerminate)
                    {
                        setCurrentState (getRequestedState());
                        if (getRequestedState() == StateScanValueSet4)
                        {
                            // set this to zero so the mechanics ask for the same data again
                            localLP->config.blockSize = 0;
                        }

                    }
                    else
                    {
                        setRequestedState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                }
                break;
            }

        case DR87_COMM_ERROR:
        case DR87_NAK:
        default:
            {
                setAttemptsRemaining(getAttemptsRemaining() - 1);
                if (getAttemptsRemaining() > 0)
                {
                    setCurrentState (getRequestedState());
                }
                else
                {
                    if (getCurrentState() == StateScanDecodeTerminate)
                    {
                        setCurrentState (getRequestedState());
                    }
                    else
                    {
                        setRequestedState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                }
                break;
            }
    }
    return retCode;
}



INT CtiDeviceDR87::GeneralScan(CtiRequestMsg *pReq,
                               CtiCommandParser &parse,
                               OUTMESS *&OutMessage,
                               RWTPtrSlist< CtiMessage > &vgList,
                               RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList,
                               INT ScanPriority)
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " General Scan of device " << getName() << " in progress " << endl;
    }

    ULONG BytesWritten;

    // load profile information
    time_t         RequestedTime = 0L;
    time_t         DeltaTime;
    time_t         NowTime;

    if (OutMessage != NULL)
    {
        OutMessage->Buffer.DUPReq.Identity = IDENT_DR87;

        /*************************
         *
         *   setting the current command in hopes that someday we don't have to use the command
         *   bits in the in message to decide what we're doing DLS
         *
         *************************
         */
        setCurrentCommand(CmdScanData);
//        OutMessage->Buffer.DUPReq.Command[0] = CmdLoadProfileData;       // One call does it all...
        OutMessage->Buffer.DUPReq.Command[0] = CmdScanData;       // One call does it all...

        // whether this is needed is decided later
        OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
        OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();

        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();

        /************************
        * a standalone master has a slave address of -1
        * a master with slaves has a slave address of 0
        *************************
        */
        EstablishOutMessagePriority( OutMessage, ScanPriority );

        if (!isMaster())
        {
            OverrideOutMessagePriority( OutMessage, OutMessage->Priority - 1 );
        }

        OutMessage->TimeOut   = 2;
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Sequence  = 0;
        OutMessage->Retry     = 3;

        outList.insert(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        status = MEMORY;
    }
    return status;
}

INT CtiDeviceDR87::allocateDataBins(OUTMESS *outMess)
{
    if (iPorterSide == NULL)
    {
        iPorterSide = CTIDBG_new CtiDR87PorterSide;
    }
    if (iDataBuffer == NULL)
    {
        iDataBuffer = CTIDBG_new BYTE[sizeof (DR87ScanData_t)];
    }
    if (iLoadProfileBuffer == NULL)
    {
        iLoadProfileBuffer = CTIDBG_new BYTE[sizeof (DR87LoadProfile_t)];

        if (iLoadProfileBuffer != NULL)
        {
            ((DR87LoadProfile_t *)iLoadProfileBuffer)->porterLPTime = outMess->Buffer.DUPReq.LP_Time;
        }
    }

    setTotalByteCount (0);
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setCurrentState (StateHandshakeInitialize);
    return NORMAL;
}


INT CtiDeviceDR87::freeDataBins()
{
    if (iPorterSide != NULL)
    {
        delete iPorterSide;
        iPorterSide = NULL;
    }

    if (iDataBuffer != NULL)
    {
        delete []iDataBuffer;
        iDataBuffer=NULL;
    }

    if (iLoadProfileBuffer != NULL)
    {
        delete []iLoadProfileBuffer;
        iLoadProfileBuffer = NULL;
    }

    // re-init everything for next time through
    setTotalByteCount (0);
    setCurrentState (StateHandshakeInitialize);

    iLPPulseVector.erase(iLPPulseVector.begin(), iLPPulseVector.end());

    return NORMAL;
}

INT CtiDeviceDR87::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)

{
    /****************************
    *  data is decoded on the fly with this device so nothing is done here
    *  except copying it into the return buffer
    *****************************
    */
    memcpy (aInMessBuffer, iDataBuffer, sizeof (DR87ScanData_t));
    aTotalBytes = sizeof (DR87ScanData_t);
    return NORMAL;
}


INT CtiDeviceDR87::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{

    memcpy(aInMessBuffer, iLoadProfileBuffer, sizeof (DR87LoadProfile_t));
    aTotalBytes = sizeof (DR87LoadProfile_t);
    return NORMAL;
}


INT  CtiDeviceDR87::ResultDecode(INMESS *InMessage,
                                 RWTime &TimeNow,
                                 RWTPtrSlist< CtiMessage >   &vgList,
                                 RWTPtrSlist< CtiMessage > &retList,
                                 RWTPtrSlist< OUTMESS > &outList)
{

    /****************************
    *
    *  intialize the command based on the in message
    *  someday the command will be pulled from the device object directly
    *
    *****************************
    */
    char tmpCurrentCommand = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[0],
         tmpCurrentState   = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    if( !_dstFlagValid )
    {
        _dstFlag = readDSTFile( getName() );
    }

    switch (tmpCurrentCommand)
    {
        case CmdScanData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Scan decode for device " << getName() << " in progress " << endl;
                }

                decodeResultScan (InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        case CmdLoadProfileData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " LP decode for device " << getName() << " in progress " << endl;
                }


                // just in case we're getting an empty message
                if (tmpCurrentState == StateScanReturnLoadProfile)
                {
                    decodeResultLoadProfile (InMessage, TimeNow, vgList, retList, outList);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " LP decode failed device " << getName() << " invalid state " << getCurrentState() << endl;
                    }
                }

                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "(" << __LINE__ << ") *** ERROR *** Invalid decode for " << getName() << endl;
                }
            }
    }
    return NORMAL;
}

INT CtiDeviceDR87::ErrorDecode (INMESS *InMessage,
                                RWTime &TimeNow,
                                RWTPtrSlist< CtiMessage >   &vgList,
                                RWTPtrSlist< CtiMessage > &retList,
                                RWTPtrSlist< OUTMESS > &outList)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    INT retCode = NORMAL;
    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);
    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            RWCString(),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);


    if (pMsg != NULL)
    {
        pMsg->insert( -1 );          // This is the dispatch token and is unimplemented at this time
        pMsg->insert(OP_DEVICEID);   // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());         // The id (device or point which failed)
        pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
        pMsg->insert(InMessage->EventCode);
    }

    insertPointIntoReturnMsg (pMsg, pPIL);

    // send the whole mess to dispatch
    if (pPIL->PointData().entries() > 0)
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;
    return retCode;
}



INT CtiDeviceDR87::decodeResultScan (INMESS *InMessage,
                                     RWTime &TimeNow,
                                     RWTPtrSlist< CtiMessage >   &vgList,
                                     RWTPtrSlist< CtiMessage > &retList,
                                     RWTPtrSlist< OUTMESS > &outList)
{
    char tmpCurrentState = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    CHAR           temp[100], buffer[60];

    BOOL     bDoStatusCheck = FALSE;
    BOOL     bDoResetStatus = FALSE;
    BOOL     isValidPoint = TRUE;

    /* Misc. definitions */
    ULONG i, j;
    ULONG Pointer;

    /* Variables for decoding Messages */
    SHORT    Value;
    USHORT   UValue;
    FLOAT    PartHour;
    DOUBLE   PValue;
    RWTime    peakTime;

    DIALUPREQUEST      *DupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY        *DUPRep = &InMessage->Buffer.DUPSt.DUPRep;


    CtiPointDataMsg   *pData    = NULL;
    CtiPointNumeric   *pNumericPoint = NULL;

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            RWCString(),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);

    DR87ScanData_t  *scanData = (DR87ScanData_t*) DUPRep->Message;

    if (isScanPending())
    {
        // if we bombed, we need an error condition and to plug values
        if ((tmpCurrentState == StateScanAbort)  ||
            (tmpCurrentState == StateHandshakeAbort) ||
            (InMessage->EventCode != 0))
        {

            CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

            if (pMsg != NULL)
            {
                pMsg->insert( -1 );           // This is the dispatch token and is unimplemented at this time
                pMsg->insert(OP_DEVICEID);    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pMsg->insert(getID());             // The id (device or point which failed)
                pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

                if (InMessage->EventCode != 0)
                {
                    pMsg->insert(InMessage->EventCode);
                }
                else
                {
                    pMsg->insert(GeneralScanAborted);
                }
            }

            insertPointIntoReturnMsg (pMsg, pPIL);
        }
        else
        {

            bDoStatusCheck = TRUE;

            // loop through my three offsets
            for (i  = 1;
                i <= OFFSET_HIGHEST_CURRENT_OFFSET;
                i ++)
            {
                // grab the point based on device and offset
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(i, AnalogPointType)) != NULL)
                {
                    // only build one of these if the point was found and configured correctly
                    if (getMeterDataFromScanStruct (i, PValue, peakTime, scanData))
                    {
                        if( DebugLevel & 0x0001 )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() <<" " << __LINE__ << " Point " << pNumericPoint->getPointID();
                            dout << " Value " << PValue << " mulitplier " << PValue * pNumericPoint->getMultiplier() << endl;
                        }

                        verifyAndAddPointToReturnMsg (pNumericPoint->getPointID(),
                                                      PValue * pNumericPoint->getMultiplier(),
                                                      NormalQuality,
                                                      peakTime,
                                                      pPIL);

                    }
                }
            }
        }
    }

    // reset this flag so device makes it on the queue later
    resetScanPending();

    if (pPIL->PointData().entries() > 0)
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    if( DebugLevel & 0x0001 )
        ResultDisplay(InMessage);
    return NORMAL;
}


INT CtiDeviceDR87::decodeResultLoadProfile (INMESS *InMessage,
                                            RWTime &TimeNow,
                                            RWTPtrSlist< CtiMessage >   &vgList,
                                            RWTPtrSlist< CtiMessage > &retList,
                                            RWTPtrSlist< OUTMESS > &outList)
{
    int retCode = NORMAL;
    DIALUPREPLY        *DUPRep       = &InMessage->Buffer.DUPSt.DUPRep;
    DR87LoadProfile_t  *ptr = (DR87LoadProfile_t *)DUPRep->Message;

    BYTEUSHORT        flip;
    CtiPointDataMsg   *pData    = NULL;
    CtiPointNumeric   *pNumericPoint = NULL;
    int               cnt,scratch;
    DOUBLE            intervalData;

    ULONG             currentIntervalTS;
    ULONG             lastLPTime = getLastLPTime().seconds();

    int               dataQuality = NormalQuality;

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            RWCString(),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);

    CtiReturnMsg   *pLastLPIntervals = NULL;

    // alpha only supports 4 channels
    DR87LPPointInfo_t   validLPPointInfo[4] = { {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1}};

    /********************
    * the DR-87 stamps the last interval and then we read back in memory
    * nice thing is, it marks each reading with a power outage flag, ect
    * we will keep all readings until we get the time stamp for the last one
    * and then go backwards
    *********************
    */
    for (int x=0; x < ptr->config.blockSize; x+=2)
    {
        flip.ch[0] = ptr->loadProfileData[x+1];
        flip.ch[1] = ptr->loadProfileData[x];
        iLPPulseVector.push_back (flip.sh);
    }

    // if true
    if (ptr->lastLPMessage)
    {
        /*************************
        * Check the validity of the time received
        **************************
        */
        if (RWTime (ptr->config.lastIntervalTime) < (RWTime()-(2*86400)) ||
            (RWTime (ptr->config.lastIntervalTime) > (RWTime()+(2*86400))))
        {
            /***********************
            * if meter time is not within a 2 day window, its
            * invalid
            ************************
            */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Aborting scan: Invalid last interval timestamp " << getName()  << " " << RWTime (ptr->config.lastIntervalTime).asString() << endl;
            }
        }
        else
        {
            // we're walking backwards here
            if (ptr->config.lastIntervalTime > lastLPTime)
            {
                // this is our current last lp time
                setLastLPTime (ptr->config.lastIntervalTime);

                // working timestamp
                currentIntervalTS = ptr->config.lastIntervalTime;

                int     offset = iLPPulseVector.size() - 1;


                // now, loop through the channels and get the point offsets and multipliers
                for (int pointCnt=0; pointCnt < ptr->config.numberOfChannels; pointCnt++)
                {
                    findLPDataPoint (validLPPointInfo[pointCnt], pointCnt);
                }

                // walk until either we get to the last time or we get to the end of our vector
                while (currentIntervalTS > lastLPTime && offset > -1)
                {
                    // delete the last set and start over
                    if (pLastLPIntervals != NULL)
                    {
                        delete pLastLPIntervals;
                        pLastLPIntervals = NULL;
                    }

                    pLastLPIntervals = CTIDBG_new CtiReturnMsg(getID(),
                                                        RWCString(InMessage->Return.CommandStr),
                                                        RWCString(),
                                                        InMessage->EventCode & 0x7fff,
                                                        InMessage->Return.RouteID,
                                                        InMessage->Return.MacroOffset,
                                                        InMessage->Return.Attempt,
                                                        InMessage->Return.TrxID,
                                                        InMessage->Return.UserID);

                    for (int currentChannel = ptr->config.numberOfChannels-1; currentChannel > -1 ; currentChannel--)
                    {
                        // perform parity checking on reading
                        cnt = 0;
                        for (scratch = 0x0001; scratch <= 0x8000; scratch <<=1)
                        {
                            if (iLPPulseVector[offset] & scratch)
                            {
                                cnt++;
                            }
                        }

                        // check for odd parity for each reading
                        if ((cnt % 2) == 1)
                        {
                            if (iLPPulseVector[offset] & 0x4000)
                            {
                                dataQuality = PartialIntervalQuality;
                            }

                            // data is contained in bits 0 - 13
                            iLPPulseVector[offset] &= 0x3FFF;

                            intervalData = ((DOUBLE)iLPPulseVector[offset] * ptr->config.scaleFactor[currentChannel] *
                                            ptr->config.kvalue[currentChannel] *
                                            validLPPointInfo[currentChannel].multiplier) *
                                            ((DOUBLE)(60.0 /ptr->config.intervalLength) / 1000.0);

                            verifyAndAddPointToReturnMsg (validLPPointInfo[currentChannel].pointId,
                                                          intervalData,
                                                          dataQuality,
                                                          currentIntervalTS,
                                                          pPIL,
                                                          TAG_POINT_LOAD_PROFILE_DATA);

                            verifyAndAddPointToReturnMsg (validLPPointInfo[currentChannel].pointId,
                                                          intervalData,
                                                          dataQuality,
                                                          currentIntervalTS,
                                                          pLastLPIntervals);
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Parity calculation failed " << getName() << endl;
                            }
                        }

                        offset--;
                    }

                    // switch the time
                    currentIntervalTS -= ptr->config.intervalLength * 60;
                }
            }
        }
        // must erase the entire vector
        iLPPulseVector.erase(iLPPulseVector.begin(), iLPPulseVector.end());
    }

    // send the whole mess to dispatch
    if (pPIL->PointData().entries() > 0)
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    /***************************
    *  this list of point changes will be sent without the load profile flag set
    *  allowing us to display the last interals in a report
    *  currently dispatch does not route load profile data
    ****************************
    */
    if (pLastLPIntervals != NULL)
    {
        if (pLastLPIntervals->PointData().entries() > 0)
        {
            retList.insert(pLastLPIntervals);
        }
        else
        {
            delete pLastLPIntervals;
        }
        pLastLPIntervals = NULL;
    }

    return retCode;
}


LONG CtiDeviceDR87::findLPDataPoint (DR87LPPointInfo_t &point, USHORT aChannel)
{
    LONG retCode = NORMAL;
    CtiPointNumeric   *pNumericPoint = NULL;

    switch (aChannel)
    {
        case 0 :
            {
                // looking for channel one point
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_CHANNEL_1, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case 1 :
            {
                // looking for channel one point
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_CHANNEL_2, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case 2 :
            {
                // looking for channel one point
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_CHANNEL_3, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;

            }
        case 3 :
            {
                // looking for channel one point
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_CHANNEL_4, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        default:
            {
                point.pointId = 0;
                point.multiplier = 1.0;
                retCode = !NORMAL;
            }
    }
    return retCode;
}

BOOL CtiDeviceDR87::getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak, DR87ScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;
    int channel=-1;

    // this is initial value
    peak = rwEpoch;

    // assuming all values are in w hours, so we divide by 1000
    switch (aOffset)
    {
        case OFFSET_TOTAL_CHANNEL_1:
            {
                channel = 0;
                aValue = aScanData->totalPulses[channel] * aScanData->scaleFactor[channel] * aScanData->kvalue[channel] / 1000.0;
                if( DebugLevel & 0x0001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() <<" ----- Channel 1 ------ " << endl;
                    dout << "Pulses " << aScanData->totalPulses[channel] << " Scale factor " << aScanData->scaleFactor[channel] << " K Factor " << aScanData->kvalue[channel] << endl;
                }

                // check how many channels it was configured for, this may not be valid data
                if (aScanData->numberOfChannels > 0)
                {
                    isValidPoint = TRUE;
                }
                else
                {
                    isValidPoint = FALSE;
                }
                break;
            }
        case OFFSET_TOTAL_CHANNEL_2:
            {
                channel = 1;
                aValue = aScanData->totalPulses[channel] * aScanData->scaleFactor[channel] * aScanData->kvalue[channel] / 1000.0;

                if( DebugLevel & 0x0001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() <<" ----- Channel 2 ------ " << endl;
                    dout << "Pulses " << aScanData->totalPulses[channel] << " Scale factor " << aScanData->scaleFactor[channel] << " K Factor " << aScanData->kvalue[channel] << endl;
                }

                // check how many channels it was configured for, this may not be valid data
                if (aScanData->numberOfChannels > 1)
                {
                    isValidPoint = TRUE;
                }
                else
                {
                    isValidPoint = FALSE;
                }
                break;
            }
        case OFFSET_TOTAL_CHANNEL_3:
            {
                channel = 2;
                aValue = aScanData->totalPulses[channel] * aScanData->scaleFactor[channel] * aScanData->kvalue[channel] / 1000.0;

                if( DebugLevel & 0x0001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() <<" ----- Channel 3 ------ " << endl;
                    dout << "Pulses " << aScanData->totalPulses[channel] << " Scale factor " << aScanData->scaleFactor[channel] << " K Factor " << aScanData->kvalue[channel] << endl;
                }

                // check how many channels it was configured for, this may not be valid data
                if (aScanData->numberOfChannels > 2)
                {
                    isValidPoint = TRUE;
                }
                else
                {
                    isValidPoint = FALSE;
                }
                break;
            }
        case OFFSET_TOTAL_CHANNEL_4:
            {
                channel = 3;
                aValue = aScanData->totalPulses[channel] * aScanData->scaleFactor[channel] * aScanData->kvalue[channel] / 1000.0;
                if( DebugLevel & 0x0001 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() <<" ----- Channel 4 ------ " << endl;
                    dout << "Pulses " << aScanData->totalPulses[channel] << " Scale factor " << aScanData->scaleFactor[channel] << " K Factor " << aScanData->kvalue[channel] << endl;
                }

                // check how many channels it was configured for, this may not be valid data
                if (aScanData->numberOfChannels > 3)
                {
                    isValidPoint = TRUE;
                }
                else
                {
                    isValidPoint = FALSE;
                }
                break;
            }
        default:
            isValidPoint = FALSE;
            break;
    }
    return isValidPoint;
}

// Routine to display or print the message
INT CtiDeviceDR87::ResultDisplay (INMESS *InMessage)

{
    return NORMAL;
}

BOOL CtiDeviceDR87::verifyAndAddPointToReturnMsg (LONG   aPointId,
                                                  DOUBLE aValue,
                                                  USHORT aQuality,
                                                  RWTime aTime,
                                                  CtiReturnMsg *aReturnMsg,
                                                  USHORT aIntervalType,
                                                  RWCString aValReport)

{
    BOOL validPointFound = FALSE;
    CtiPointDataMsg   *pData    = NULL;

    // if our offset if valid, add the point
    if (aPointId)
    {
        //create a CTIDBG_new message
        pData = CTIDBG_new CtiPointDataMsg(aPointId,
                                    aValue,
                                    aQuality,
                                    AnalogPointType,
                                    aValReport);


        if (pData != NULL)
        {
            // set to load profile if needed
            if (aIntervalType == TAG_POINT_LOAD_PROFILE_DATA)
            {
                pData->setTags(TAG_POINT_LOAD_PROFILE_DATA);
            }

            if (aTime != rwEpoch)
            {
                //
                //  hack fix for non-DST compliant meters - someday to be absorbed by a big, global timekeeper
                //
                if( _dstFlag && aTime.isDST( ) )
                {
                    aTime += 60 * 60;
                }

                pData->setTime (aTime);
            }

            if( DebugLevel & 0x0001 )
            {
                pData->dump();
            }

            // aData is deleted in this function
            validPointFound = insertPointIntoReturnMsg (pData, aReturnMsg);
        }
    }
    return validPointFound;
}


BOOL CtiDeviceDR87::insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
                                              CtiReturnMsg   *aReturnMsg)
{
    BOOL retCode = TRUE;

    if (aReturnMsg != NULL)
    {
        aReturnMsg->PointData().insert(aDataPoint);
        aDataPoint = NULL;   // We just put it on the list...
    }
    else
    {
        delete aDataPoint;
        aDataPoint = NULL;
        retCode = FALSE;
    }
    return retCode;
}

USHORT  CtiDeviceDR87::calculateCRC(UCHAR* buffer, LONG length, BOOL bAdd)
{
    // octal tables used in calc of crc
    USHORT lowNib[16] = {0,
        0140301,
        0140601,
        0500,
        0141401,
        01700,
        01200,
        0141101,
        0143001,
        03300,
        03600,
        0143501,
        02400,
        0142701,
        0142201,
        02100} ;
    USHORT highNib[16] = {0,
        0146001,
        0154001,
        012000,
        0170001,
        036000,
        024000,
        0162001,
        0120001,
        066000,
        074000,
        0132001,
        050000,
        0116001,
        0104001,
        042000};

    BYTEUSHORT newCRC;
    USHORT j0,j1,j2;

    newCRC.sh = 0;

    for (int x=1; x < length; x++)
    {
        j0 = newCRC.sh ^ (buffer[x] & 0377);
        j1 = j0 & 017;
        j2 = (j0 >> 4) & 017;
        j0 = lowNib[j1] ^ highNib[j2];

        newCRC.sh = (newCRC.sh >> 8) ^ j0;
    }

    /*******************
    * swap the positions so they go out correctly
    ********************
    */

    if (bAdd)
    {
        // low byte first
        buffer[length]     = newCRC.ch[0];
        buffer[length + 1] = newCRC.ch[1];
    }

    return newCRC.sh;
}

INT CtiDeviceDR87::checkCRC(BYTE *InBuffer,ULONG InCount)
{
    BYTEUSHORT  CRC;
    INT         retVal = !NORMAL;


    CRC.sh = calculateCRC (InBuffer, InCount - 2, FALSE);

    if (CRC.ch[0] == InBuffer[InCount - 2] && CRC.ch[1] == InBuffer[InCount - 1])
    {
        retVal = NORMAL;
    }

    return retVal;
}

INT CtiDeviceDR87::getMessageInDataStream (CtiXfer  &Transfer, int aCommValue)
{
    int retVal = DR87_MSG_INCOMPLETE;
    int cnt;

    if (aCommValue)
    {
        retVal = DR87_COMM_ERROR;
    }
    else
    {
        /**************************
        * this device streams 0xff to us when its not sending anything usefull
        *
        *  we must look for a 0x16 in the stream and that is the beginning of our msg
        **************************
        */
        cnt=0;

        // make sure we don't go off looking for the sync bit forever
        setNumberOfIncompleteMsgs (getNumberOfIncompleteMsgs()-1);

        while (cnt < Transfer.getInCountActual() && retVal != DR87_NAK && !isDataMsgComplete())
        {
            // check if we've gotten a sync byte already
            if (syncByteHasBeenReceived())
            {
                /***********************
                * nak or ack is always the second byte in the message
                * so check to decide how many bytes to retrieve
                ************************
                */
                if (!ackNakByteHasBeenReceived())
                {
                    // check for a nak
                    if (Transfer.getInBuffer()[cnt] == DR87_NAK)
                    {
                        setNumberOfIncompleteMsgs(6);
                        retVal = DR87_NAK;
                    }
                    else
                    {
                        setAckNakByteReceived(true);
                        getWorkBuffer()[getTotalByteCount()] = Transfer.getInBuffer()[cnt];
                        setTotalByteCount(getTotalByteCount()+1);
                    }
                }
                else
                {
                    // copy data into the work buffer until we get an pad byte
                    if (getTotalByteCount() < getTotalBytesExpected())
                    {
                        getWorkBuffer()[getTotalByteCount()] = Transfer.getInBuffer()[cnt];
                        setTotalByteCount(getTotalByteCount()+1);
                    }
                    else
                    {
                        setSyncByteReceived(false);
                        setAckNakByteReceived(false);
                        setDataMsgComplete (true);
                        setNumberOfIncompleteMsgs(6);

                        /******************************
                        * because the recorder spits 0xff at you when its saying nothing else
                        * we need to handle the CRC ourselves for every message
                        *******************************
                        */
                        if (checkCRC (getWorkBuffer(), getTotalByteCount()))
                        {
                            retVal = DR87_BAD_CRC;

                            /********************
                            * this thing seems to sometimes hang up before it finishes
                            * generating the CRC for the logoff function
                            * therefore, if we get this far, we know we were Ack'ed
                            * and the CRC really isn't important anymore if we had
                            * sent a terminate command
                            *********************
                            */
                            if (getCurrentState() == StateScanDecodeTerminate)
                            {
                                retVal = DR87_MSG_COMPLETE;
                            }
                        }
                        else
                        {
                            retVal = DR87_MSG_COMPLETE;

                        }

                        setTotalByteCount(0);
                    }
                }
            }
            else if (Transfer.getInBuffer()[cnt] == DR87_SYNC)
            {
                setSyncByteReceived(true);
                getWorkBuffer()[getTotalByteCount()] = Transfer.getInBuffer()[cnt];
                setTotalByteCount(getTotalByteCount()+1);
                setNumberOfIncompleteMsgs(6);
            }
            // increment our counter
            cnt++;
        }

        // if we haven't been nak'd already
        if (retVal != DR87_NAK)
        {
            // is the message is not complete, do this again
            if (!isDataMsgComplete())
            {
                retVal = DR87_MSG_INCOMPLETE;
            }
        }
    }
    return retVal;
}


INT CtiDeviceDR87::fillUploadTransferObject (CtiXfer  &aTransfer, USHORT aCmd, USHORT aStartAddress, USHORT aByteNumber, USHORT aBytesToRead)
{
    BYTEUSHORT startAddress,byteCount;
    INT retVal = NORMAL;

    startAddress.sh = aStartAddress;
    byteCount.sh = (~aBytesToRead)+0x01;

    /********************
    *  dump memory commands are asking for a specific byte and
    *  the document starts with byte 0x200 = byte 1, not zero so everything is off by 1
    *********************
    */
    if ( aCmd == DR87_DUMP_MEMORY)
    {
        startAddress.sh += aByteNumber - 1;
    }

    sprintf((CHAR *)aTransfer.getOutBuffer(),
            "%c%c%c%c%c%c",
            DR87_SYNC,
            aCmd,
            startAddress.ch[0],
            startAddress.ch[1],
            byteCount.ch[0],
            byteCount.ch[1]);

    aTransfer.setOutCount (6);
    aTransfer.setInCountExpected (200);
    aTransfer.setInTimeout (1);

    /******************************
    * because the recorder spits 0xff at you when its saying nothing else
    * we need to handle the CRC ourselves for every message
    *******************************
    */
    aTransfer.setCRCFlag (0);
    calculateCRC (aTransfer.getOutBuffer(), aTransfer.getOutCount(), true);
    aTransfer.setOutCount (aTransfer.getOutCount()+2);

    // this helps track when we have a full message
    setDataMsgComplete (false);
    setAckNakByteReceived(false);
    setSyncByteReceived (false);
    setByteNumber(aByteNumber);

    // bytes expected plus the sync, ack, echo and 2 byte crc
    setTotalBytesExpected (aBytesToRead+5);
    return retVal;
}

INT CtiDeviceDR87::calculateBlockSize()
{
    INT blockSize= 256;

    /***********************
    * check if grabbing the default size takes us beyond the current interval
    ************************
    */
    if ((getCurrentByteOffset() + 256) > (getOldestIntervalByteOffset ()-0x1ff))
    {
        /**********************
        * now we must check whether we should wrap around to the start
        * offset of the memory map
        ***********************
        */
        if (getCurrentByteOffset() > (getOldestIntervalByteOffset ()-0x1ff))
        {
            /*********************
            * we will need to wrap to the front of the memory map eventually,
            * check if grabbing the next block of 256 will cause us to go
            * beyond the stop address
            **********************
            */
            if ((getCurrentByteOffset () + 256) > (getMassMemoryStop()-0x1ff))
            {
                /************************
                * since at some point we are reading up to the stop address of the
                * memory map, check and see if we stopped at the end of the map
                *************************
                */
                if (getCurrentByteOffset() == (getMassMemoryStop()-0x1ff+2))
                {
                    /**********************
                    * we were at the stop address, move to the start address and
                    * calculate whether the default block of 256 will take us
                    * beyond the current interval
                    ***********************
                    */
                    setCurrentByteOffset (getMassMemoryStart() - 0x1ff);

                    if ((getCurrentByteOffset () + 256) > (getOldestIntervalByteOffset ()-0x1ff))
                    {
                        blockSize = getOldestIntervalByteOffset() - getCurrentByteOffset() - 0x1ff;
                    }
                    else
                    {
                        blockSize = 256;
                    }
                }
                else
                {
                    // stop address is the 1st byte in last interval of 2 bytes
                    blockSize = ( getMassMemoryStop() - getCurrentByteOffset() - 0x1ff + 2);
                }
            }
            else
            {
                /**********************
                * default block size is ok
                ***********************
                */
                blockSize = 256;
            }
        }
        else
        {
            /**********************
            * the is less than 256 bytes still needed to collect
            ***********************
            */
            blockSize = getOldestIntervalByteOffset() - getCurrentByteOffset() - 0x1ff;
        }
    } // default is ok
    return blockSize;
}


