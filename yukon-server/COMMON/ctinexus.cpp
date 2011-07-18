#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>
#include <string.h>
#include <process.h>
#include <winbase.h>

#include <rw/thr/cancel.h>

#include "os2_2w32.h"
#include "ctinexus.h"
#include "logger.h"
#include "cticalls.h"
#include "dsm2.h"
#include "millisecond_timer.h"

char     CTINexusErrors[][80] = {
    {"Error unknown"},      // 0
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},      // 10
    {"Error unknown"},
    {"Error unknown"},
    {"Access Denied"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},      // 20
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},      // 30
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Specified socket is invalid\n\tinvalid value specified for socket parameter"},
    {"Error unknown"},
    {"Error unknown"},      // 40
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Address already in use"},
    {"Error unknown"},
    {"Error unknown"},      // 50
    {"Error unknown"},
    {"Error unknown"},
    {"Connection aborted by software\n\t(timeout or other error)"},
    {"Connection reset (closed) by peer"},
    {"Error unknown"},
    {"Error unknown"},
    {"Socket is not connected"},
    {"Socket Shutdown\n\tCannot send after a socket shutdown"},
    {"Error unknown"},
    {"Error unknown"},      // 60
    {"Connection refused by peer"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},      // 70
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"Error unknown"},
    {"80 Error unknown"},   // 80
    {"81 Error unknown"},
    {"82 Error unknown"},
    {"83 Error unknown"},
    {"84 Error unknown"},
    {"85 Error unknown"},
    {"86 Error unknown"},
    {"87 Error unknown"},
    {"88 Error unknown"},
    {"89 Error unknown"},
    {"90 Error unknown"},   // 90
    {"91 Error unknown"},
    {"92 Error unknown"},
    {"Sockets not initialized"},
    {"94 Error unknown"},
    {"95 Error unknown"},
    {"96 Error unknown"},
    {"97 Error unknown"},
    {"98 Error unknown"},
    {"99 Error unknown"}
};

INT CTIGetLastError(void)
{
#ifdef _WIN32
    return(WSAGetLastError());
#elif defined(_OS2_)
    return(sock_errno());
#endif
}


bool CTINEXUS::CTINexusIsSocketError(INT Error)
{
    return (10000 < Error && Error <= 10099);
}

bool CTINEXUS::CTINexusIsFatalSocketError(INT Error)
{
    bool err = false;

    if(CTINexusIsSocketError(Error))
    {
        switch(Error)
        {
        case WSAEWOULDBLOCK:
            {
                break;
            }
        default:
            {
                err = true;
                break;
            }
        }
    }

    return err;
}

INT  CTINEXUS::CTINexusReportError(CHAR *Label, INT Line, INT Error) const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " Socket " << sockt;

    if(CTINexusIsSocketError(Error))
    {
        dout << "   ERROR: " << Label << " (" << Line << "): " << Error << " -> " << CTINexusErrors[Error - 10000] << endl;
    }
    else
    {
        dout << "   ERROR: " << Label << " (" << Line << "): Error " << Error << " occurred" << endl;
    }
    return 0;
}

bool CTINEXUS::CTINexusValid() const
{
    return((NexusState != CTINEXUS_STATE_NULL) && (sockt != INVALID_SOCKET));
}

INT CTINEXUS::CTINexusClose()
{
    INT      nReturnCode;

    if((NexusState != CTINEXUS_STATE_NULL) || (sockt != INVALID_SOCKET))
    {

        nReturnCode = shutdown(sockt, 2);

        if(nReturnCode == SOCKET_ERROR &&
           CTIGetLastError() != 10093 &&            // Not initialized
           CTIGetLastError() != 10057 )             // Not connected
        {
            CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        }

        nReturnCode = closesocket(sockt);     // This may break on non-WIN32 API's

        sockt = INVALID_SOCKET;
        NexusState = CTINEXUS_STATE_NULL;

        if(nReturnCode == SOCKET_ERROR && CTIGetLastError() != 10093)
        {
            CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        }
    }

    return(0);
}

INT CTINEXUS::CTINexusCreate(SHORT nPort)
{
    int nRet;
    int nLen;
    char szBuf[256];

    //
    // Create a TCP/IP stream socket to "listen" with
    //
    sockt = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if(sockt == INVALID_SOCKET)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        return -1;
    }

    //
    // Fill in the address structure
    //

    saServer.sin_family = AF_INET;
    saServer.sin_addr.s_addr = INADDR_ANY; // Let WinNexus supply address
    saServer.sin_port = htons(nPort);      // Use port from command line

    //
    // bind the name to the socket
    //

    nRet = bind(sockt, (LPSOCKADDR)&(saServer), sizeof(struct sockaddr));
    if(nRet == SOCKET_ERROR)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        CTINexusClose();
        return -1;
    }

    //
    // This isn't normally done or required, but in this
    // example we're printing out where the server is waiting
    // so that you can connect the example client.
    //
    nLen = sizeof(SOCKADDR);

    nRet = gethostname(szBuf, sizeof(szBuf));
    if(nRet == SOCKET_ERROR)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        CTINexusClose();
        return -1;
    }

    //
    // set the socket to listen.  This initializes the wsock queues
    //

    nRet = listen(sockt, SOMAXCONN);                 // Number of connection request queue
    if(nRet == SOCKET_ERROR)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        CTINexusClose();
        return -1;
    }

    return 0;
}

/*****************************************************************************
 * Connect Waits on a socket for an incoming request
 *
 ****************************************************************************/
INT CTINEXUS::CTINexusConnect(CTINEXUS *RemoteNexus, HANDLE *hAbort, LONG timeout, int flags)
{
    INT      nReturnCode = 0;
    ULONG    param = 1;
    DWORD    dwWait = WAIT_TIMEOUT;

    LONG     loops = timeout > 0 ? (timeout + 249) / 250 : LONG_MAX;

    //
    // Wait for an incoming request
    //

    // fprintf(stderr,"Waiting for a connection at accept()\n");
    /*
     * accept returns a NEW socket descriptor...!!!!
     */

    RemoteNexus->sockt = INVALID_SOCKET;

    if(hAbort != NULL || loops != LONG_MAX)
    {
        // Set to non-blocking mode...
        ioctlsocket(sockt, FIONBIO, &param);

        while( loops-- > 0 && (RemoteNexus->sockt = accept(sockt, NULL, NULL)) == INVALID_SOCKET )
        {
            if( hAbort != NULL && WAIT_OBJECT_0 == (dwWait = WaitForSingleObject(*hAbort, 250L)) )
            {
                break;      // hAbort has been posted!
            }
            else
            {
                CTISleep( 250L );
            }
        }
    }
    else
    {
        RemoteNexus->sockt = accept(sockt, NULL, NULL);
    }

    if(WAIT_OBJECT_0 == dwWait)
    {
        nReturnCode = -1;
    }
    else if(loops <= 0 && (RemoteNexus->sockt == INVALID_SOCKET))
    {
        // We timed out here...
    }
    else if(RemoteNexus->sockt == INVALID_SOCKET)
    {
        CTINexusReportError(__FILE__, __LINE__, ERR_CTINEXUS_INVALID_HANDLE );
        nReturnCode = ERR_CTINEXUS_INVALID_HANDLE;
    }
    else
    {
        BOOL ka = TRUE;
        setsockopt(RemoteNexus->sockt, SOL_SOCKET, SO_KEEPALIVE, (char*)&ka, sizeof(BOOL));

        RemoteNexus->NexusState |= CTINEXUS_STATE_CONNECTED;
        RemoteNexus->NexusType  |= CTINEXUS_TYPE_SOCKTYPE;
        RemoteNexus->NexusFlags  = flags;
    }

    return nReturnCode;
}

/*****************************************************************************
 * Analgous to the good old DosOpen OS/2 call.
 ****************************************************************************/
INT CTINEXUS::CTINexusOpen(CHAR *szServer, SHORT nPort, ULONG Flags)
{
    INT   nReturnCode = 0;
    int   nRet;

    //
    // Look for my server provided socket out there!
    //
    NexusFlags = Flags;

    //
    // Find the server
    //

    lpHostEntry = gethostbyname(szServer);
    if(lpHostEntry == NULL)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        return !0;
    }

    if(sockt != INVALID_SOCKET)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "    Socket is being re-opened without being closed first.  Closing now.." << endl;
        }

        CTINexusClose();
    }

    //
    // Create a TCP/IP stream socket
    //
    sockt = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if(sockt == INVALID_SOCKET)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        return !0;
    }

    //
    // Fill in the address structure
    //
    saServer.sin_family = AF_INET;
    saServer.sin_addr = *((LPIN_ADDR)*lpHostEntry->h_addr_list);
    saServer.sin_port = htons(nPort);   // Port number

    //
    // connect to the server
    //

    nRet = connect(sockt, (LPSOCKADDR)&saServer, sizeof(struct sockaddr));
    if(nRet == SOCKET_ERROR)
    {
        // CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
        CTINexusClose();
        return !0;
    }

    BOOL ka = TRUE;
    setsockopt(sockt, SOL_SOCKET, SO_KEEPALIVE, (char*)&ka, sizeof(BOOL));

    NexusState |= CTINEXUS_STATE_CONNECTED;

    return nReturnCode;
}

INT CTINEXUS::CTINexusWrite(void *buf, ULONG len, PULONG BytesWritten, LONG TimeOut)
{
    ULONG    BytesSent   = 0;
    CHAR     *bptr       = (CHAR*)buf;
    INT      nReason = 0;

    *BytesWritten = 0;

    CtiTime  now, then(CtiTime::now() + TimeOut);
    int      wbLoops = 0;        //  number of "would block" loops
    unsigned long ulTemp;

    if( ioctlsocket(sockt, FIONREAD, &ulTemp) == SOCKET_ERROR )
    {
        CTINexusClose();  //  will set sockt to INVALID_SOCKET
    }

    try
    {
        if(sockt != INVALID_SOCKET)
        {
            do
            {
                nReason = 0;

                BytesSent = send(sockt, bptr, len, 0);

                if(BytesSent == SOCKET_ERROR)
                {
                    nReason = CTIGetLastError();

                    if(nReason == WSAEWOULDBLOCK)
                    {
                        int sec_remaining = then.seconds() - now.seconds();

                        if( wbLoops++ )  //  gripe every 5 seconds
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Outbound nexus to " << Name << " is full, will wait up to " << sec_remaining << " seconds to retry. " << endl;
                        }

                        struct timeval tv;

                        tv.tv_sec  = std::min(5, sec_remaining);
                        tv.tv_usec = 0;

                        fd_set writefds;

                        //  initialize the FD set
                        FD_ZERO(&writefds);
                        FD_SET(sockt, &writefds);

                        //  block for tv until there's something to read
                        select(0, NULL, &writefds, NULL, &tv);
                    }
                    else
                    {
                        CTINexusReportError(__FILE__, __LINE__, nReason);
                        CTINexusClose();

                        break; // the do - while(len > 0)
                    }

                    BytesSent = 0;   // Keep from screwing up the buffers!
                }

                *BytesWritten  += BytesSent;
                bptr           += BytesSent;
                len            -= BytesSent;


                if(len > 10000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Nexus Write > 10000 bytes. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                now = CtiTime::now();

            } while( (nReason == WSAEWOULDBLOCK && now < then) && len > 0);

            if(len > 0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Outbound nexus to " << Name << " could not be written. " << len << " bytes unwritten." << endl;
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   A send was attempted to an invalid socket handle.  " << endl;
            }

            nReason = BADSOCK;
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return( nReason );
}

INT CTINEXUS::CTINexusRead(void *buf, ULONG len, PULONG BRead, LONG TimeOut)
{
    INT retval = NoError;

    if( TimeOut > 0 )
    {
        //  convert to milliseconds
        TimeOut *= 1000;
    }

    try
    {
        //  READANY waits until the timeout occurs or until any data is read
        //  READEXACTLY waits until an exact amount of data is read or until the timeout occurs

        Cti::Timing::MillisecondTimer timer;

        while( !retval && (read_buffer.size() < len) && (NexusState != CTINEXUS_STATE_NULL) && (sockt != INVALID_SOCKET) )
        {
            int socket_status;
            unsigned long bytes_read = 0, bytes_available = 0;
            struct timeval tv = {0, 0};
            fd_set readfds;

            if( TimeOut )
            {
                if( TimeOut > 0 )
                {
                    //  convert milliseconds to microseconds
                    tv.tv_usec = std::min(TimeOut, 500L) * 1000;
                }
                else
                {
                    //  convert milliseconds to microseconds
                    tv.tv_usec = 500 * 1000;
                }

                timer.reset();
            }

            //  initialize the FD set
            FD_ZERO(&readfds);
            FD_SET(sockt, &readfds);

            //  block for tv until there's something to read
            int data_available = select(0, &readfds, NULL, NULL, &tv);

            if( data_available == SOCKET_ERROR )
            {
                INT Error = CTIGetLastError();
                CTINexusReportError(__FILE__, __LINE__, Error );

                retval = -Error;
            }
            else if( data_available > 0 )
            {
                if( ioctlsocket(sockt, FIONREAD, &bytes_available) == SOCKET_ERROR )
                {
                    INT Error = CTIGetLastError();
                    CTINexusReportError(__FILE__, __LINE__, Error );

                    retval = -Error;
                }
                else if( bytes_available == 0 )
                {
                    len = read_buffer.size();  //  socket's been closed - exit quietly
                }
                else if( bytes_available > 0 )
                {
                    if( bytes_available > 4096 )  bytes_available = 4096;

                    //  ensure current buffer is large enough to fit the bytes available
                    unsigned insert_position = read_buffer.size();
                    read_buffer.insert(read_buffer.end(), bytes_available, 0);

                    bytes_read = recv(sockt, (char *) ( &* (read_buffer.begin() + insert_position)), bytes_available, 0);

                    if( bytes_read <= 0 )
                    {
                        if( bytes_read == SOCKET_ERROR)
                        {
                            bytes_read = 0;

                            CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
                        }

                        retval = ErrorNexusRead;
                    }
                    else if( bytes_read < bytes_available )
                    {
                        read_buffer.erase(read_buffer.begin() + insert_position + bytes_read, read_buffer.end());
                    }
                }
            }

            if( TimeOut )
            {
                if( TimeOut > 0 )
                {
                    TimeOut -= min((LONG)timer.elapsed(), TimeOut);
                }

                try
                {
                    rwServiceCancellation();
                }
                catch(RWCancellation &c)
                {
                    // dout << "Caught a cancellation in the nexus read" << endl;
                    CTINexusClose();  //  this will cause us to exit the main loop
                    retval = -ERR_CTINEXUS_CANCELLATION;
                }
            }
            else if( read_buffer.size() < len )
            {
                if( NexusFlags & CTINEXUS_FLAG_READEXACTLY )
                {
                    retval = -ERR_CTINEXUS_READTIMEOUT;
                }
                else // if( NexusFlags & CTINEXUS_FLAG_READANY )
                {
                    //  READANY - exit without setting an error code
                    len = read_buffer.size();
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( !read_buffer.empty() && ((read_buffer.size() >= len) || (NexusFlags & CTINEXUS_FLAG_READANY)) )
    {
        unsigned long to_copy = std::min(len, static_cast<unsigned long>(read_buffer.size()));

        memcpy(buf, reinterpret_cast<const void *>(&*read_buffer.begin()), to_copy);
        read_buffer.erase(read_buffer.begin(), read_buffer.begin() + to_copy   );

        *BRead += to_copy;
    }

    return retval;
}

INT CTINEXUS::CTINexusPeek(void *buf, ULONG len, PULONG BRead)
{
    INT      BytesRead   = 0;
    ULONG    BytesAvail  = 0;
    CHAR     *bptr       = (CHAR*)buf;
    INT      nReturn;
    LONG    nLoops      = 0;

    nReturn = ioctlsocket(sockt, FIONREAD, &BytesAvail);

    if(nReturn == SOCKET_ERROR)
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError() );
        return(-CTIGetLastError());
    }

    /*
     *  data is there, or we don't care about the any TimeOut value!
     */

    if(BytesAvail)
    {
        if(len > BytesAvail)
            len = BytesAvail;

        do
        {
            BytesRead = recv(sockt, bptr, len, MSG_PEEK);

            if(BytesRead <= 0)
            {
                if(BytesRead == SOCKET_ERROR)
                {
                    CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
                }

                return(ErrorNexusRead);
            }

            *BRead += BytesRead;

            if((NexusFlags & CTINEXUS_FLAG_READANY))
            {
                break;
            }

            bptr += BytesRead;
            len -= BytesRead;
        } while(len > 0);
    }

    return(0);
}

INT CTINEXUS::CTINexusFlushInput()
{
    CHAR *flushBuf;
    ULONG numBytesToFlush = 0;
    INT status = NORMAL;

    if(sockt != INVALID_SOCKET)
    {
        // How many are available ??
        if(ioctlsocket (sockt, FIONREAD, &numBytesToFlush) != SOCKET_ERROR)
        {
            if(numBytesToFlush != 0)
            {
                if((flushBuf = new CHAR[numBytesToFlush]) == NULL)
                {
                    status = (MEMORY);
                }
                else
                {
                    if(recv(sockt, flushBuf, (int)numBytesToFlush, 0) <= 0)
                    {
                        status = TCPREADERROR;
                        CTINexusClose();
                    }

                    delete [] flushBuf;
                }
            }
        }
        else
        {
            status = TCPREADERROR;
            CTINexusClose();
        }
    }

    return(status);
}

ULONG CTINEXUS::CtiGetNexusState()
{
    return NexusState;
}
