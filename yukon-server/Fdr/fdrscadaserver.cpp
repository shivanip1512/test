#include "precompiled.h"

#include <iostream>

using namespace std;

#include <stdio.h>

// this class header
#include "fdrscadaserver.h"


// Constructors, Destructor, and Operators
CtiFDRScadaServer::CtiFDRScadaServer(string &name)
: CtiFDRSocketServer(name)
{
}


CtiFDRScadaServer::~CtiFDRScadaServer()
{

}

/** Process Message From Foreign System
 * The caller is responsible for cleaning up any memory allocated
 * for data when this method returns.
 */
int CtiFDRScadaServer::processMessageFromForeignSystem(Cti::Fdr::ServerConnection& connection, const char* data, unsigned int size)
{
    bool retVal = false;

    USHORT function = getHeaderBytes(data, size);

    switch (function)
    {
        case SINGLE_SOCKET_FORCESCAN:
            {
                CtiFDRClientServerConnection* connection2 = (CtiFDRClientServerConnection*)&connection;
                retVal = processScanMessage(connection2,data);
                break;
            }
        case SINGLE_SOCKET_VALUE:
            {
                retVal = processValueMessage (connection, data, size);
                break;
            }
        case SINGLE_SOCKET_REGISTRATION:
            {
                retVal = processRegistrationMessage (connection, data, size);
                break;
            }
        case SINGLE_SOCKET_STATUS:
            {
                retVal = processStatusMessage (connection, data, size);
                break;
            }
        case SINGLE_SOCKET_CONTROL:
        case SINGLE_SOCKET_VALMET_CONTROL:
            {
                retVal = processControlMessage (connection, data, size);
                break;
            }
        case SINGLE_SOCKET_TIMESYNC:
            {
                if (shouldUpdatePCTime())
                {
                    if (getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Time sync message received"  << endl;
                    }
                    retVal = processTimeSyncMessage (connection, data, size);
                }
                else
                {
                    if (getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Time sync message received, PC not configured to update" << endl;
                    }
                }
                break;
            }
        case SINGLE_SOCKET_NULL:
            {
                if (getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "Heartbeat message received from " << connection <<  endl;
                }
                break;
            }
        default:
            if (getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                logNow() << "Unknown message type " << function <<  " received" << endl;
            }
    }

    return retVal ? NORMAL : !NORMAL;

}

/** Return the message indicator stored in the first 2 or 4 bytes.
 *  This should return a number than can be dirrectly compared
 *  to one of the SINGLE_SOCKET_* constants (i.e. this function
 *  must mask off unnecessary bits and do any necessary
 *  network-to-host conversion).
 */
unsigned long CtiFDRScadaServer::getHeaderBytes(const char* data, unsigned int size)
{
    if (size < sizeof(USHORT))
    {
        return 0;
    }
    USHORT* function = (USHORT*)data;
    return ntohs (*function);
}
