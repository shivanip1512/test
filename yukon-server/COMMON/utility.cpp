


// includes /////////////////////////////////////////////////////

#include <windows.h>
#include <iomanip>
#include <iostream>
using namespace std;

#if _MSC_VER > 1000
    #pragma once
#endif // _MSC_VER > 1000



#pragma warning( disable: 4786 )

#include <rw\re.h>

#include <windows.h>
#include <string>
#include <vector>
#include <stdio.h>
#include <stdlib.h>

// imagehlp.h must be compiled with packing to eight-byte-boundaries,
// but does nothing to enforce that. I am grateful to Jeff Shanholtz
// <JShanholtz@premia.com> for finding this problem.
#pragma pack( push, before_imagehlp, 8 )
#include <imagehlp.h>
#pragma pack( pop, before_imagehlp )



#include <winsock.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/types.h>
#include <sys/timeb.h>



#include <rw\thr\mutex.h>
#include <rw/db/db.h>


#include "ctinexus.h"
#include "dbaccess.h"
#include "porter.h"
#include "utility.h"
#include "logger.h"
#include "dllbase.h"
#include "guard.h"
#include "numstr.h"
#include "yukon.h"

LONG GetMaxLMControl(long pao)
{
    RWCString sql;
    INT id = 0;

    sql = RWCString(("SELECT MAX(LMCTRLHISTID) FROM LMCONTROLHISTORY WHERE PAOBJECTID = ")) + CtiNumStr(pao) + RWCString(" AND ACTIVERESTORE != 'N'");

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBReader  rdr = ExecuteQuery( conn, sql );

    if(rdr() && rdr.isValid())
    {
        rdr >> id;
    }
    else
    {
        RWMutexLock::LockGuard  guard(coutMux);
        cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return id;
}


LONG LMControlHistoryIdGen(bool force)
{
    static RWMutexLock   mux;
    RWMutexLock::LockGuard guard(mux);

    LONG tempid = 0;
    static BOOL init_id = FALSE;
    static LONG id = 0;
    static const CHAR sql[] = "SELECT MAX(LMCTRLHISTID) FROM LMCONTROLHISTORY";

    if(!init_id || force)
    {   // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        // are out of scope when the release is called
        RWDBReader  rdr = ExecuteQuery( conn, sql );

        if(rdr() && rdr.isValid())
        {
            rdr >> tempid;
        }
        else
        {
            RWMutexLock::LockGuard  guard(coutMux);
            cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(tempid >= id)
        {
            id = tempid;
        }

        init_id = TRUE;
    }   // Temporary results are destroyed to free the connection

    return(++id);
}

LONG CommErrorHistoryIdGen(bool force)
{
    static RWMutexLock   mux;
    RWMutexLock::LockGuard guard(mux);

    LONG tempid = 0;
    static BOOL init_id = FALSE;
    static LONG id = 0;
    static const CHAR sql[] = "SELECT MAX(COMMERRORID) FROM COMMERRORHISTORY";

    if(!init_id || force)
    {   // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        // are out of scope when the release is called
        RWDBReader  rdr = ExecuteQuery( conn, sql );

        if(rdr() && rdr.isValid())
        {
            rdr >> tempid;
        }
        else
        {
            RWMutexLock::LockGuard  guard(coutMux);
            cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(tempid >= id)
        {
            id = tempid;
        }

        init_id = TRUE;
    }   // Temporary results are destroyed to free the connection

    return(++id);
}

INT ChangeIdGen(bool force)
{
    static RWMutexLock   mux;
    RWMutexLock::LockGuard guard(mux);

    INT tempid = 0;
    static BOOL init_id = FALSE;
    static INT id = 0;
    static const CHAR sql[] = "SELECT MAX(CHANGEID) FROM RAWPOINTHISTORY";

    if(!init_id || force)
    {   // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        // are out of scope when the release is called
        RWDBReader  rdr = ExecuteQuery( conn, sql );

        if(rdr() && rdr.isValid())
        {
            rdr >> tempid;
        }
        else
        {
            RWMutexLock::LockGuard  guard(coutMux);
            cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if(tempid >= id)
        {
            id = tempid;
        }

        init_id = TRUE;
    }   // Temporary results are destroyed to free the connection

    return(++id);
}

INT SystemLogIdGen()
{
    static BOOL init_id = FALSE;
    static INT id = 0;
    static RWMutexLock   mux;
    static const CHAR sql[] = "SELECT MAX(LOGID) FROM SYSTEMLOG";


    if(!init_id)
    {   // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        // are out of scope when the release is called
        RWDBReader  rdr = ExecuteQuery( conn, sql );

        if(rdr() && rdr.isValid())
        {
            rdr >> id;
        }
        else
        {
            RWMutexLock::LockGuard  guard(coutMux);
            cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        init_id = TRUE;
    }   // Temporary results are destroyed to free the connection

    RWMutexLock::LockGuard guard(mux);
    return(++id);
}

INT PAOIdGen()
{
    static RWMutexLock mux;
    static const CHAR sql[] = "SELECT MAX(PAOBJECTID) FROM YUKONPAOBJECT";
    INT id = 0;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();


    RWDBReader  rdr = ExecuteQuery( conn, sql );

    if(rdr() && rdr.isValid())
    {
        rdr >> id;
    }
    else
    {
        RWMutexLock::LockGuard  guard(coutMux);
        cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return ++id;
}

/* Routine to calculate time a VHF shed will take */
INT VCUTime (OUTMESS *OutMessage, PULONG Seconds)
{
    SHORT Count;
    SHORT Repeats;
    PSZ RepEnv;

    /* Check for number of repeats on message transmit */
    if(CTIScanEnv ("VCOMREPEATS", &RepEnv))
    {
        Repeats = 3;
    }
    else
    {
        if(!(Repeats = atoi (RepEnv)))
        {
            Repeats = 3;
        }
    }

    /* first see how many bytes are involved */
    Count = OutMessage->Buffer.OutMessage[PREIDLEN + 3] + 1;

    /* Calculate the number of bits involved */
    Count *= 8;
    Count += 27; /* Preamble, Postamble, BCH and trailing bits */;

    /* Now Calculate the time */
    *Seconds = (((Count * 20L) + 200L) * Repeats) + 80L;
    *Seconds /= 1000L;
    *Seconds += 2;

    return(NORMAL);
}

BOOL isFileTooBig(RWCString fileName, DWORD thisBig)
{
    BOOL big = FALSE;

    HANDLE hFile = CreateFile(fileName.data(),
                              GENERIC_READ,
                              FILE_SHARE_READ,
                              NULL,
                              OPEN_EXISTING,
                              FILE_ATTRIBUTE_NORMAL,
                              NULL);

    if(hFile != INVALID_HANDLE_VALUE)
    {
        DWORD fSize = GetFileSize(hFile, NULL);

        if(fSize < 0xFFFFFFFF && fSize > thisBig)
        {
            // Valid and bigger than 5 MB in size
            big = TRUE;
        }
    }

    CloseHandle(hFile);

    return big;
}

BOOL InEchoToOut(const INMESS *In, OUTMESS *Out)
{
    BOOL bRet = FALSE;

    Out->TargetID = In->TargetID;
    Out->ReturnNexus = In->ReturnNexus;
    Out->Sequence = In->Sequence;
    Out->Priority = In->Priority;
    // Out->MessageFlags = In->MessageFlags;

    /* Lotsa stuff requires the InMessage to be loaded so load it */
    memcpy(&(Out->Request), &(In->Return), sizeof(PIL_ECHO));

    return bRet;
}
BOOL OutEchoToIN(const OUTMESS *Out, INMESS *In)
{
    BOOL bRet = FALSE;

    // Clear it...
    memset(In, 0, sizeof(INMESS));

    /* Lotsa stuff requires the InMessage to be loaded so load it */
    memcpy(&(In->Return), &(Out->Request), sizeof(PIL_ECHO));
    //  save this for the macro routes
    In->Priority = Out->Priority;

    In->DeviceID            = Out->DeviceID;
    In->TargetID            = Out->TargetID;

    In->Remote              = Out->Remote;
    In->Port                = Out->Port;
    In->Sequence            = Out->Sequence;
    In->ReturnNexus         = Out->ReturnNexus;
    In->SaveNexus           = Out->SaveNexus;
    In->Priority            = Out->Priority;
    In->MessageFlags        = Out->MessageFlags;

    In->DeviceIDofLMGroup   = Out->DeviceIDofLMGroup;
    In->TrxID               = Out->TrxID;

    return bRet;
}

/*
 *  This method will set the outmessage's priority if it has not been already set.  In general all the device execute
 *  routines should use this function or the next.
 */
INT EstablishOutMessagePriority(OUTMESS *Out, INT priority)
{
    INT pri = 0;

    if(Out)
    {
        if(Out->Priority <= 0)
        {
            Out->Priority = priority;
        }

        pri = Out->Priority;
    }

    return pri;
}

/*
 *  This method will set the outmessage's priority.  It is provided only as a contrast to EstablishOutMessagePriority
 */
INT OverrideOutMessagePriority(OUTMESS *Out, INT priority)
{
    INT pri = 0;

    if(Out)
    {
        pri = Out->Priority = priority;
    }
    return pri;
}



// defines //////////////////////////////////////////////////////

//#define  WIN32_LEAN_AND_MEAN
//#define  NOGDI
//#define  NOIME

#define  VERIFY_RET_VAL( arg )  { int nRet = arg; if( nRet ) return nRet; }

// private functions - used internally //////////////////////////

int SendMailMessage( SENDMAIL *pMail );
int Send( SOCKET s, const char *lpszBuff, int nLen, int nFlags );
int Receive( SOCKET s, LPTSTR lpszBuff, int nLenMax, int nFlags, LPCTSTR lpszReplyCode );
void LogMessage( LPCTSTR lpszMsg );

// Static variables /////////////////////////////////////////////

static BOOL   gbLog  = FALSE;
static HANDLE ghFile = INVALID_HANDLE_VALUE;
static char   gszMailerID[] = "X-Mailer: CtiMail DLL V1.0\r\n";
static char   gszLogFile[]  = "ctimail.log";


// SendMail - sends an SMTP mail message to specified host. This
// is the only exported function from this DLL.
/////////////////////////////////////////////////////////////////
BOOL SendMail( SENDMAIL *pMail, int *pResult )
{
    WORD    wVersion = MAKEWORD( 1, 1 );
    WSADATA wsaData;
    int     nRet;

// check for required parameters
    if( pMail == NULL || pResult == NULL ||   pMail->lpszHost == NULL ||
        pMail->lpszRecipient == NULL || pMail->lpszSubject == NULL  ||
        pMail->lpszMessage == NULL )
    {
        if( pResult )
            *pResult = WSAEINVAL;
        return FALSE;
    }

    if( (*pResult = WSAStartup(wVersion, &wsaData)) )
        return FALSE;

// open log file if specified
    if( pMail->bLog )
    {
        char szLogPath[ MAX_PATH + 1 ];

        *szLogPath = '\0';
        GetTempPath( MAX_PATH, szLogPath );
        strcat( szLogPath, gszLogFile );

        ghFile = CreateFile( szLogPath, GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL );

        if( ghFile != INVALID_HANDLE_VALUE )
            gbLog = TRUE;
    }

// try to send the message
    nRet = SendMailMessage( pMail );

    if( nRet != 0 )
    {
        if( gbLog )
        {
            char szMsg[ MAX_LINE_SIZE ];

            if( nRet < 0 )
                _snprintf( szMsg, sizeof(szMsg), "SMTP error %d in SendMail.\n", -nRet );
            else
                _snprintf( szMsg, sizeof(szMsg), "Socket error %d in SendMail.\n", nRet );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << szMsg << endl;
            }
            LogMessage( szMsg );
        }
    }

// cleanup socket lib and log file
    WSACleanup();
    if( ghFile != INVALID_HANDLE_VALUE )
    {
        CloseHandle( ghFile );
        ghFile = INVALID_HANDLE_VALUE;
        gbLog  = FALSE;
    }

    *pResult = nRet;
    return( *pResult == 0 );
}


// SendMailMessage - actually sends the message.This is a private
// function.
/////////////////////////////////////////////////////////////////
int SendMailMessage( SENDMAIL *pMail )
{
    char   szBuff[ MAX_LINE_SIZE + 1 ]; // transmit/receive buffer
    char   szUser[ MAX_NAME_SIZE + 1 ]; // user name buffer
    char   szName[ MAX_NAME_SIZE + 1 ]; // host name buffer
    DWORD  dwSize = MAX_NAME_SIZE;
    SOCKET s;

    struct hostent    *ph;
    struct sockaddr_in addr;

    char         szTime[ MAX_NAME_SIZE + 1 ]; // time related vars
    time_t       tTime;
    struct tm   *ptm;
    struct timeb tbTime;

// connect to the SMTP port on remote host
    if( (s = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET )
        return WSAGetLastError();

    if( isdigit(*pMail->lpszHost) && strchr(pMail->lpszHost, '.') )
    {
        unsigned long iaddr = inet_addr( pMail->lpszHost );
        ph = gethostbyaddr( (const char *)&iaddr, 4, PF_INET );
    }
    else
        ph = gethostbyname( pMail->lpszHost );

    if( ph == NULL )
        return WSAGetLastError();

    addr.sin_family = AF_INET;
    addr.sin_port   = htons( SMTP_PORT );
    memcpy( &addr.sin_addr, ph->h_addr_list[0],
            sizeof(struct in_addr) );

    if( connect(s, (struct sockaddr *)&addr, sizeof(struct sockaddr)) )
        return WSAGetLastError();

// receive signon message
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "220" ); );

// get user name and local host name
    GetUserName( szUser, &dwSize );
    gethostname( szName, MAX_NAME_SIZE );

// send HELO message
    _snprintf(szBuff, sizeof(szBuff),"HELO %s\r\n", szName );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "250" ); )

// send MAIL message
    if( pMail->lpszSender )
    {
        if( strchr( pMail->lpszSender, '@' ) )
            _snprintf(szBuff, sizeof(szBuff),"MAIL FROM: <%s>\r\n", pMail->lpszSender );
        else
            _snprintf(szBuff, sizeof(szBuff),"MAIL FROM: <%s%c%s>\r\n", pMail->lpszSender, '@', szName );
    }
    else
        _snprintf(szBuff, sizeof(szBuff),"MAIL FROM:<%s%c%s>\r\n", szUser, '@', szName );

    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "250" ); )

// send RCPT message
    _snprintf(szBuff, sizeof(szBuff),"RCPT TO: <%s>\r\n", pMail->lpszRecipient );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "25" ); )

// send DATA message
    _snprintf(szBuff, sizeof(szBuff),"DATA\r\n" );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "354" ); )

// construct date string
    tTime = time( NULL );
    ptm   = localtime( &tTime );

    strftime( szTime, MAX_NAME_SIZE, "%a, %d %b %Y %H:%M:%S %Z", ptm );

// find time zone offset and correct for DST
    ftime( &tbTime );
    if( tbTime.dstflag )
        tbTime.timezone -= 60;

    sprintf( szTime + strlen(szTime), " %2.2d%2.2d", -tbTime.timezone / 60, tbTime.timezone % 60 );

    /*
     *  Do not send a date and time.. It seems that some SMTP servers cannot account for the timezone properly.
     */

// send mail headers
// Date:
    // 030901 CGP // _snprintf(szBuff, sizeof(szBuff),"Date: %s\r\n", szTime );
    // 030901 CGP // VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )

// X-Mailer:
    VERIFY_RET_VAL( Send( s, gszMailerID, strlen(gszMailerID), 0 ); )

// Message-ID:
    if( pMail->lpszMessageID )
    {
        _snprintf(szBuff, sizeof(szBuff),"Message-ID: %s\r\n", pMail->lpszMessageID );
        VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    }

// To:
    _snprintf(szBuff, sizeof(szBuff),"To: %s", pMail->lpszRecipient );
    if( pMail->lpszRecipientName )
        sprintf( szBuff + strlen(szBuff), " (%s)\r\n", pMail->lpszRecipientName );
    else
        strcat( szBuff, "\r\n" );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )

// From:
    if( pMail->lpszSender )
    {
        _snprintf(szBuff, sizeof(szBuff),"From: %s", pMail->lpszSender );
        if( pMail->lpszSenderName )
            sprintf( szBuff + strlen(szBuff), " (%s)\r\n", pMail->lpszSenderName );
        else
            strcat( szBuff, "\r\n" );
    }
    else
        _snprintf(szBuff, sizeof(szBuff),"From: %s@%s\r\n", szUser, szName );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )

// Reply-To:
    if( pMail->lpszReplyTo )
    {
        _snprintf(szBuff, sizeof(szBuff),"Reply-To: %s", pMail->lpszReplyTo );
        if( pMail->lpszReplyToName )
            sprintf( szBuff + strlen(szBuff), " (%s)\r\n",
                     pMail->lpszReplyToName );
        else
            strcat( szBuff, "\r\n" );
        VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    }

// Subject:
    _snprintf(szBuff, sizeof(szBuff),"Subject: %s\r\n", pMail->lpszSubject );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )

// send message text
    VERIFY_RET_VAL( Send( s, pMail->lpszMessage, strlen(pMail->lpszMessage), 0 ); )

// send message terminator and receive reply
    VERIFY_RET_VAL( Send( s, "\r\n.\r\n", 5, 0 ); )
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "250" ); )

// send QUIT message
    _snprintf(szBuff, sizeof(szBuff),"QUIT\r\n" );
    VERIFY_RET_VAL( Send( s, szBuff, strlen(szBuff), 0 ); )
    VERIFY_RET_VAL( Receive( s, szBuff, MAX_LINE_SIZE, 0, "221" ); )

    closesocket( s );
    return 0;
}


// Send - send the request to the SMTP server, and handle errors.
/////////////////////////////////////////////////////////////////
int Send( SOCKET s, const char *lpszBuff, int nLen, int nFlags )
{
    int nCnt = 0;

    while( nCnt < nLen )
    {
        int nRes = send( s, lpszBuff + nCnt, nLen - nCnt, nFlags );
        if( nRes == SOCKET_ERROR )
            return WSAGetLastError();
        else
            nCnt += nRes;
    }

    if( gbLog )
        LogMessage( lpszBuff );

    return 0;
}


// Receive - receive a reply from the SMTP server, and verify that
// the request has succeeded by checking against the specified
// reply code.
/////////////////////////////////////////////////////////////////
int Receive( SOCKET s, LPTSTR lpszBuff, int nLenMax, int nFlags, LPCTSTR lpszReplyCode )
{
    LPTSTR p;
    int    nRes = recv( s, lpszBuff, nLenMax, nFlags );

    if( nRes == SOCKET_ERROR )
        return WSAGetLastError();
    else
        *( lpszBuff + nRes ) = '\0';

    if( gbLog )
        LogMessage( lpszBuff );

// check reply code for success/failure
    p = strtok( lpszBuff, "\n" );
    while( p )
    {
        if( *(p + 3) == ' ' )
        {
            if( !strncmp(p, lpszReplyCode, strlen(lpszReplyCode)) )
                return 0;
            else
            {
                int nErr = 1;

                sscanf( p, "%d", &nErr );
                return -nErr;
            }
        }
        else
            p = strtok( NULL, "\n" );
    }

    return -1;
}


// LogMessage - log messages to log file.
/////////////////////////////////////////////////////////////////
void LogMessage( LPCTSTR lpszMsg )
{
    DWORD dwRet;
    if( ghFile != INVALID_HANDLE_VALUE ) WriteFile( ghFile, lpszMsg, strlen(lpszMsg), &dwRet, NULL );
}



void identifyProject(const CTICOMPILEINFO &Info)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if((DebugLevel & DEBUGLEVEL_LUDICROUS) && Info.date)      // DEBUGLEVEL added 012903 CGP
        {
            dout << RWTime() << " (Build " <<
            Info.major << "." <<
            Info.minor << "." <<
            Info.build << ", " <<
            Info.date  << ") " <<
            Info.proj << endl;
        }
        else
        {
            dout << RWTime() << " (Build " <<
            Info.major << "." <<
            Info.minor << "." <<
            Info.build << ") " <<
            Info.proj << endl;
        }
    }

    return;
}

void identifyProjectComponents(const CTICOMPONENTINFO *pInfo)
{
    for(int i = 0 ; pInfo[i].fname != NULL ; i++)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " <<
            pInfo[i].fname << " Rev. " <<
            pInfo[i].rev << "  " <<
            pInfo[i].date << endl;
        }
    }

    return;
}

CtiMutex gOutMessageMux;
ULONG gOutMessageCounter = 0;

void incrementCount()
{
    CtiLockGuard<CtiMutex> guard(gOutMessageMux);
    gOutMessageCounter++;
}
void decrementCount()
{
    CtiLockGuard<CtiMutex> guard(gOutMessageMux);
    gOutMessageCounter--;
}
ULONG OutMessageCount()
{
    CtiLockGuard<CtiMutex> guard(gOutMessageMux);
    return gOutMessageCounter;
}


/*****************************************************************************
 * This method takes a USHORT (as an int) and converst it into it's constituent
 * bits.  The bits are numbered one to 16 with the zero (LSB) bit as 1.
 *****************************************************************************/
RWCString convertVersacomAddressToHumanForm(INT address)
{
    BOOL        foundFirst = FALSE;
    CHAR        temp[32];
    RWCString   rStr;

    for(int i = 0; i < 16; i++)
    {
        if((address >> i) & 0x0001)
        {
            if(foundFirst)
            {
                sprintf(temp, ",%d", i+1);
            }
            else
            {
                sprintf(temp, "%d", i+1);

                foundFirst = TRUE;
            }

            rStr += RWCString(temp);
        }
    }

    return rStr;
}


INT convertHumanFormAddressToVersacom(INT address)
{
    INT num = 0;

    for(int i = 0; i < 16; i++)
    {
        num |= (((address >> i) & 0x0001) << (15 - i));
    }

    return num;
}


bool pokeDigiPortserver(CHAR *server, INT port)
{
    bool Success = false;

    CTINEXUS telnetNexus;

    if(telnetNexus.CTINexusOpen(server, port, CTINEXUS_FLAG_READANY) == NORMAL)
    {
        char buffer[120];
        ULONG cnt = 0;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Connected to " << server << " on port " << port << endl;
        }

        memset(buffer, '\0', 120);

        for(int i = 0; i < 5; i++)
        {
            telnetNexus.CTINexusWrite("\r", 1, &cnt, 1);

            memset(buffer, '\0', 120);
            cnt = 0;
            Sleep(1000);

            telnetNexus.CTINexusRead( buffer, 75, &cnt, 2);

            if( cnt > 0)
            {

                // We got some bytes back!
                CHAR *chptr = NULL;

                if((chptr = strstr(buffer, "login:" )) != NULL)
                {
                    break;
                }
            }

            Sleep(1000);

        }

        if( cnt > 0)
        {
            // We got some bytes back!
            CHAR *chptr = NULL;

            if((chptr = strstr(buffer, "login:" )) != NULL)
            {
                // we found the login prompt.
                cnt = 0;
                memset(buffer, '\0', 120);

                telnetNexus.CTINexusWrite("root\r", 5, &cnt, 0);

                Sleep(5000);

                telnetNexus.CTINexusRead( buffer, 50, &cnt, 10);
                if( cnt > 0)
                {
                    if((chptr = strstr(buffer, "passwd:" )) != NULL)
                    {
                        // we found the password prompt.
                        telnetNexus.CTINexusWrite("dbps\r", 5, &cnt, 0);

                        cnt = 0;
                        memset(buffer, '\0', 120);
                        telnetNexus.CTINexusRead( buffer, 5, &cnt, 2);
                        if( cnt > 0)
                        {
                            CHAR reboot[] = {"boot action=reset\r"};
                            // Assume we just read out the prompt... sent the reboot sequence!
                            telnetNexus.CTINexusWrite(reboot, strlen(reboot) , &cnt, 0);

                            Success = true;

                            /*
                             *  User should sleep awhile before re-initing the device!
                             */
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << buffer << endl;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << buffer << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << buffer << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << buffer << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        telnetNexus.CTINexusClose();
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return Success;
}

bool isION(INT type)
{
    bool isit = false;

    switch(type)
    {
        case TYPE_ION7330:
        case TYPE_ION7700:
        case TYPE_ION8300:
        {
            isit = true;
        }
    }

    return isit;
}

bool isLCU(INT type)
{
    bool isit = false;

    switch(type)
    {
    case TYPE_LCU415:
    case TYPE_LCU415LG:
    case TYPE_LCU415ER:
    case TYPE_LCUT3026:
        {
            isit = true;
        }
    }

    return isit;
}

int generateTransmissionID()
{
    static int id = 0;
    static RWMutexLock   mux;
    RWMutexLock::LockGuard guard(mux);
    return ++id;
}

LONG GetPAOIdOfPoint(long pid)
{
    RWCString sql("SELECT PAOBJECTID FROM POINT WHERE POINTID = ");
    INT id = 0;

    sql += CtiNumStr(pid);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBReader  rdr = ExecuteQuery( conn, sql );

    if(rdr() && rdr.isValid())
    {
        rdr >> id;
    }
    else if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " " << sql << endl;
        }
    }

    return id;
}

/*
 *  Checks if sockets are available in the system.
 */
bool CheckSocketSubsystem()
{
    bool status = true;

    SOCKET s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if(s == INVALID_SOCKET)
    {
        status = false;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Socket subsystem is questionable.  Error " << WSAGetLastError() << endl;
        }
    }
    else
    {
        closesocket(s);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Socket subsystem is OK." << endl;
        }
    }

    return status;
}


RWCString& traceBuffer(RWCString &str, BYTE *Message, ULONG Length)
{
    INT status = NORMAL;
    ULONG i;
    ULONG offset = 0;

    /* loop through all of the characters */
    for(i = 0; i < Length; i++)
    {
        str += CtiNumStr((UINT)Message[i]).zpad(2).hex() + " ";
    }

    return str;
}

RWTime nextScheduledTimeAlignedOnRate( const RWTime &origin, LONG rate )
{
    RWTime first(YUKONEOT);

    if( rate > 3600 )
    {
        RWTime hourstart = RWTime(origin.seconds() - (origin.seconds() % 3600));            // align to the current hour.
        first = RWTime(hourstart.seconds() - ((hourstart.hour() * 3600) % rate) + rate);
    }
    else if(rate > 0 )    // Prevent a divide by zero with this check...
    {
        first = RWTime(origin.seconds() - (origin.seconds() % rate) + rate);
    }
    else if(rate == 0)
    {
        first = origin.now();
    }

    while(first <= origin)
    {
        first += rate;
    }

    return first;
}

// #define _PRINT_SYMBOLS TRUE

#define gle (GetLastError())
#define lenof(a) (sizeof(a) / sizeof((a)[0]))
#define MAXNAMELEN 1024 // max name length for found symbols
#define IMGSYMLEN ( sizeof IMAGEHLP_SYMBOL )
#define TTBUFLEN 65536 // for a temp buffer



// SymCleanup()
typedef BOOL (__stdcall *tSC)( IN HANDLE hProcess );
tSC pSC = NULL;

// SymFunctionTableAccess()
typedef PVOID (__stdcall *tSFTA)( HANDLE hProcess, DWORD AddrBase );
tSFTA pSFTA = NULL;

// SymGetLineFromAddr()
typedef BOOL (__stdcall *tSGLFA)( IN HANDLE hProcess, IN DWORD dwAddr,
                                  OUT PDWORD pdwDisplacement, OUT PIMAGEHLP_LINE Line );
tSGLFA pSGLFA = NULL;

// SymGetModuleBase()
typedef DWORD (__stdcall *tSGMB)( IN HANDLE hProcess, IN DWORD dwAddr );
tSGMB pSGMB = NULL;

// SymGetModuleInfo()
typedef BOOL (__stdcall *tSGMI)( IN HANDLE hProcess, IN DWORD dwAddr, OUT PIMAGEHLP_MODULE ModuleInfo );
tSGMI pSGMI = NULL;

// SymGetOptions()
typedef DWORD (__stdcall *tSGO)( VOID );
tSGO pSGO = NULL;

// SymGetSymFromAddr()
typedef BOOL (__stdcall *tSGSFA)( IN HANDLE hProcess, IN DWORD dwAddr,
                                  OUT PDWORD pdwDisplacement, OUT PIMAGEHLP_SYMBOL Symbol );
tSGSFA pSGSFA = NULL;

// SymInitialize()
typedef BOOL (__stdcall *tSI)( IN HANDLE hProcess, IN PSTR UserSearchPath, IN BOOL fInvadeProcess );
tSI pSI = NULL;

// SymLoadModule()
typedef DWORD (__stdcall *tSLM)( IN HANDLE hProcess, IN HANDLE hFile,
                                 IN PSTR ImageName, IN PSTR ModuleName, IN DWORD BaseOfDll, IN DWORD SizeOfDll );
tSLM pSLM = NULL;

// SymSetOptions()
typedef DWORD (__stdcall *tSSO)( IN DWORD SymOptions );
tSSO pSSO = NULL;

// StackWalk()
typedef BOOL (__stdcall *tSW)( DWORD MachineType, HANDLE hProcess,
                               HANDLE hThread, LPSTACKFRAME StackFrame, PVOID ContextRecord,
                               PREAD_PROCESS_MEMORY_ROUTINE ReadMemoryRoutine,
                               PFUNCTION_TABLE_ACCESS_ROUTINE FunctionTableAccessRoutine,
                               PGET_MODULE_BASE_ROUTINE GetModuleBaseRoutine,
                               PTRANSLATE_ADDRESS_ROUTINE TranslateAddress );
tSW pSW = NULL;

// UnDecorateSymbolName()
typedef DWORD (__stdcall WINAPI *tUDSN)( PCSTR DecoratedName, PSTR UnDecoratedName,
                                         DWORD UndecoratedLength, DWORD Flags );
tUDSN pUDSN = NULL;



struct ModuleEntry
{
    std::string imageName;
    std::string moduleName;
    DWORD baseAddress;
    DWORD size;
};
typedef std::vector< ModuleEntry > ModuleList;
typedef ModuleList::iterator ModuleListIter;



int threadAbortFlag = 0;
HANDLE hTapTapTap = NULL;



void ShowStack( HANDLE hThread, CONTEXT& c ); // dump a stack
DWORD __stdcall TargetThread( void *arg );
void ThreadFunc1();
void ThreadFunc2();
DWORD Filter( EXCEPTION_POINTERS *ep );
void enumAndLoadModuleSymbols( HANDLE hProcess, DWORD pid );
bool fillModuleList( ModuleList& modules, DWORD pid, HANDLE hProcess );
bool fillModuleListTH32( ModuleList& modules, DWORD pid );
bool fillModuleListPSAPI( ModuleList& modules, DWORD pid, HANDLE hProcess );

static bool stinit = false;


// miscellaneous psapi declarations; we cannot #include the header
// because not all systems may have it
typedef struct _MODULEINFO
{
    LPVOID lpBaseOfDll;
    DWORD SizeOfImage;
    LPVOID EntryPoint;
} MODULEINFO, *LPMODULEINFO;


void autopsy(char *calleefile, int calleeline)
{
    HANDLE hThread;
    CONTEXT c;

    try
    {
        DuplicateHandle( GetCurrentProcess(), GetCurrentThread(), GetCurrentProcess(), &hThread, 0, false, DUPLICATE_SAME_ACCESS );
        memset( &c, '\0', sizeof c );
        c.ContextFlags = CONTEXT_FULL;

        // init CONTEXT record so we know where to start the stackwalk

        if( ! GetThreadContext( hThread, &c ) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "GetThreadContext(): gle = " << gle << endl;
            }
            return;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout, 10000);

            if(doubt_guard.isAcquired())
            {
                dout << endl << RWTime() << " **** STACK TRACE **** called from " << calleefile << " line: " << calleeline << endl;
                dout << endl << "Thread 0x" << hex << GetCurrentThreadId() << dec << "  " << GetCurrentThreadId() << endl;
                ShowStack( hThread, c );
                dout << RWTime() << " **** STACK TRACE ENDS ****" << endl << endl ;
            }
            else    // This is a bit rough!
            {
                dout << endl << RWTime() << " **** STACK TRACE **** called from " << calleefile << " line: " << calleeline << endl;
                dout << endl << "Thread 0x" << hex << GetCurrentThreadId() << dec << "  " << GetCurrentThreadId() << endl;
                ShowStack( hThread, c );
                dout << RWTime() << " **** STACK TRACE ENDS ****" << endl << endl ;
                dout.flush();
            }

        }

        CloseHandle( hThread );
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}

void ShowStack( HANDLE hThread, CONTEXT& c )
{
    HINSTANCE hImagehlpDll = NULL;

    {

        // we load imagehlp.dll dynamically because the NT4-version does not
        // offer all the functions that are in the NT5 lib
        hImagehlpDll = LoadLibrary( "imagehlp.dll" );
        if( hImagehlpDll == NULL )
        {
            {
                //CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "LoadLibrary( \"imagehlp.dll\" ): gle = " << gle << endl;;
            }
            return;
        }

        pSC = (tSC) GetProcAddress( hImagehlpDll, "SymCleanup" );
        pSFTA = (tSFTA) GetProcAddress( hImagehlpDll, "SymFunctionTableAccess" );
        pSGLFA = (tSGLFA) GetProcAddress( hImagehlpDll, "SymGetLineFromAddr" );
        pSGMB = (tSGMB) GetProcAddress( hImagehlpDll, "SymGetModuleBase" );
        pSGMI = (tSGMI) GetProcAddress( hImagehlpDll, "SymGetModuleInfo" );
        pSGO = (tSGO) GetProcAddress( hImagehlpDll, "SymGetOptions" );
        pSGSFA = (tSGSFA) GetProcAddress( hImagehlpDll, "SymGetSymFromAddr" );
        pSI = (tSI) GetProcAddress( hImagehlpDll, "SymInitialize" );
        pSSO = (tSSO) GetProcAddress( hImagehlpDll, "SymSetOptions" );
        pSW = (tSW) GetProcAddress( hImagehlpDll, "StackWalk" );
        pUDSN = (tUDSN) GetProcAddress( hImagehlpDll, "UnDecorateSymbolName" );
        pSLM = (tSLM) GetProcAddress( hImagehlpDll, "SymLoadModule" );

        if( pSC == NULL || pSFTA == NULL || pSGMB == NULL || pSGMI == NULL ||
            pSGO == NULL || pSGSFA == NULL || pSI == NULL || pSSO == NULL ||
            pSW == NULL || pUDSN == NULL || pSLM == NULL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "GetProcAddress(): some required function not found." << endl;
            }

            FreeLibrary( hImagehlpDll );
            return;
        }

        stinit = true;
    }
    // normally, call ImageNtHeader() and use machine info from PE header
    DWORD imageType = IMAGE_FILE_MACHINE_I386;
    HANDLE hProcess = GetCurrentProcess(); // hProcess normally comes from outside
    int frameNum; // counts walked frames
    DWORD offsetFromSymbol; // tells us how far from the symbol we were
    DWORD symOptions; // symbol handler settings
    IMAGEHLP_SYMBOL *pSym = (IMAGEHLP_SYMBOL *) malloc( IMGSYMLEN + MAXNAMELEN );
    char undName[MAXNAMELEN]; // undecorated name
    char undFullName[MAXNAMELEN]; // undecorated name with all shenanigans
    IMAGEHLP_MODULE Module;
    IMAGEHLP_LINE Line;
    std::string symSearchPath;
    char *tt = 0, *p;

    STACKFRAME s; // in/out stackframe
    memset( &s, '\0', sizeof s );

    // NOTE: normally, the exe directory and the current directory should be taken
    // from the target process. The current dir would be gotten through injection
    // of a remote thread; the exe fir through either ToolHelp32 or PSAPI.

    tt = new char[TTBUFLEN]; // this is a _sample_. you can do the error checking yourself.

    // build symbol search path from:
    symSearchPath = "";
    // current directory
    if( GetCurrentDirectory( TTBUFLEN, tt ) )
        symSearchPath += tt + std::string( ";" );
    // dir with executable
    if( GetModuleFileName( 0, tt, TTBUFLEN ) )
    {
        for( p = tt + strlen( tt ) - 1; p >= tt; -- p )
        {
            // locate the rightmost path separator
            if( *p == '\\' || *p == '/' || *p == ':' )
                break;
        }
        // if we found one, p is pointing at it; if not, tt only contains
        // an exe name (no path), and p points before its first byte
        if( p != tt ) // path sep found?
        {
            if( *p == ':' ) // we leave colons in place
                ++ p;
            *p = '\0'; // eliminate the exe name and last path sep
            symSearchPath += tt + std::string( ";" );
        }
    }
    // environment variable _NT_SYMBOL_PATH
    if( GetEnvironmentVariable( "_NT_SYMBOL_PATH", tt, TTBUFLEN ) )
        symSearchPath += tt + std::string( ";" );
    // environment variable _NT_ALTERNATE_SYMBOL_PATH
    if( GetEnvironmentVariable( "_NT_ALTERNATE_SYMBOL_PATH", tt, TTBUFLEN ) )
        symSearchPath += tt + std::string( ";" );
    // environment variable SYSTEMROOT
    if( GetEnvironmentVariable( "SYSTEMROOT", tt, TTBUFLEN ) )
        symSearchPath += tt + std::string( ";" );

    if( symSearchPath.size() > 0 ) // if we added anything, we have a trailing semicolon
        symSearchPath = symSearchPath.substr( 0, symSearchPath.size() - 1 );

#ifdef _PRINT_SYMBOLS
    {
        //CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "symbols path: " << symSearchPath.c_str() << endl;
    }
#endif

    // why oh why does SymInitialize() want a writeable string?
    strncpy( tt, symSearchPath.c_str(), TTBUFLEN );
    tt[TTBUFLEN - 1] = '\0'; // if strncpy() overruns, it doesn't add the null terminator

    // init symbol handler stuff (SymInitialize())
    if( ! pSI( hProcess, tt, false ) )
    {
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "SymInitialize(): gle = " <<  gle << endl;
        }
        goto cleanup;
    }

    // SymGetOptions()
    symOptions = pSGO();
    symOptions |= SYMOPT_LOAD_LINES;
    symOptions &= ~SYMOPT_UNDNAME;
    pSSO( symOptions ); // SymSetOptions()

    // Enumerate modules and tell imagehlp.dll about them.
    // On NT, this is not necessary, but it won't hurt.
    enumAndLoadModuleSymbols( hProcess, GetCurrentProcessId() );

    // init STACKFRAME for first call
    // Notes: AddrModeFlat is just an assumption. I hate VDM debugging.
    // Notes: will have to be #ifdef-ed for Alphas; MIPSes are dead anyway,
    // and good riddance.
    s.AddrPC.Offset = c.Eip;
    s.AddrPC.Mode = AddrModeFlat;
    s.AddrFrame.Offset = c.Ebp;
    s.AddrFrame.Mode = AddrModeFlat;

    memset( pSym, '\0', IMGSYMLEN + MAXNAMELEN );
    pSym->SizeOfStruct = IMGSYMLEN;
    pSym->MaxNameLength = MAXNAMELEN;

    memset( &Line, '\0', sizeof Line );
    Line.SizeOfStruct = sizeof Line;

    memset( &Module, '\0', sizeof Module );
    Module.SizeOfStruct = sizeof Module;

    offsetFromSymbol = 0;

    for( frameNum = 0; ; ++ frameNum )
    {
        // get next stack frame (StackWalk(), SymFunctionTableAccess(), SymGetModuleBase())
        // if this returns ERROR_INVALID_ADDRESS (487) or ERROR_NOACCESS (998), you can
        // assume that either you are done, or that the stack is so hosed that the next
        // deeper frame could not be found.
        if( ! pSW( imageType, hProcess, hThread, &s, &c, NULL, pSFTA, pSGMB, NULL ) )
            break;

        // display its contents
#ifdef _PRINT_SYMBOLS
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            char of = dout.fill('0');
            dout << setw(3) << frameNum <<
                (s.Far ? 'F': '.') <<
                (s.Virtual? 'V': '.') <<
                " " << hex << setw(8) << s.AddrPC.Offset <<
                " " << hex << setw(8) << s.AddrReturn.Offset <<
                " " << hex << setw(8) << s.AddrFrame.Offset <<
                " " << hex << setw(8) << s.AddrStack.Offset << dec << endl;

            dout.fill(of);
        }
#endif

        if( s.AddrPC.Offset == 0 )
        {
            {
                //CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "(-nosymbols- PC == 0)" << endl;
            }
        }
        else
        { // we seem to have a valid PC
            // show procedure info (SymGetSymFromAddr())
            if( ! pSGSFA( hProcess, s.AddrPC.Offset, &offsetFromSymbol, pSym ) )
            {
                if( gle != 487 )
                {
                    //CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "SymGetSymFromAddr(): gle = " << gle << endl;
                }
            }
            else
            {
                // UnDecorateSymbolName()
                pUDSN( pSym->Name, undName, MAXNAMELEN, UNDNAME_NAME_ONLY );
                pUDSN( pSym->Name, undFullName, MAXNAMELEN, UNDNAME_COMPLETE );
                {
                    //CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << undName;

                    #ifdef _PRINT_SYMBOLS
                    if( offsetFromSymbol != 0 )
                        dout << " " << (long) offsetFromSymbol << " bytes " << endl;
                    else
                        dout << endl;

                    dout << "    Sig:  " << pSym->Name << endl;
                    dout << "    Decl: " << undFullName << endl;

                    #endif

                    // show line number info, NT5.0-method (SymGetLineFromAddr())
                    if( pSGLFA != NULL )
                    { // yes, we have SymGetLineFromAddr()
                        if( ! pSGLFA( hProcess, s.AddrPC.Offset, &offsetFromSymbol, &Line ) )
                        {
                            if( gle != 487 )
                            {
                                dout << "SymGetLineFromAddr(): gle = " << gle << endl;
                            }
                        }
                        else
                        {
                            dout << " " << Line.FileName << " (" << Line.LineNumber << ") " << offsetFromSymbol << " bytes ";
                        }
                    } // yes, we have SymGetLineFromAddr()

                    dout << endl;
                }
            }

            // show module info (SymGetModuleInfo())
            if( ! pSGMI( hProcess, s.AddrPC.Offset, &Module ) )
            {
                {
                    //CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "SymGetModuleInfo): gle = " << gle << endl;
                }
            }
            else
            { // got module info OK
                char ty[80];
                switch( Module.SymType )
                {
                case SymNone:
                    strcpy( ty, "-nosymbols-" );
                    break;
                case SymCoff:
                    strcpy( ty, "COFF" );
                    break;
                case SymCv:
                    strcpy( ty, "CV" );
                    break;
                case SymPdb:
                    strcpy( ty, "PDB" );
                    break;
                case SymExport:
                    strcpy( ty, "-exported-" );
                    break;
                case SymDeferred:
                    strcpy( ty, "-deferred-" );
                    break;
                case SymSym:
                    strcpy( ty, "SYM" );
                    break;
                default:
                    _snprintf( ty, sizeof ty, "symtype=%ld", (long) Module.SymType );
                    break;
                }

#ifdef _PRINT_SYMBOLS
                {
                   //CtiLockGuard<CtiLogger> doubt_guard(dout);
                   char of = dout.fill('0');
                   dout << "    Mod:  " << Module.ModuleName << "[" << Module.ImageName << "], base: " << hex << setw(8) << Module.BaseOfImage << "h" << dec << endl;
                   dout << "    Sym:  type: " << ty << ", file: " << Module.LoadedImageName << endl;
                   dout.fill(of);
                }
#endif
            } // got module info OK
        } // we seem to have a valid PC

        // no return address means no deeper stackframe
        if( s.AddrReturn.Offset == 0 )
        {
            // avoid misunderstandings in the printf() following the loop
            SetLastError( 0 );
            break;
        }

    } // for ( frameNum )

    if( gle != 0 )
    {
        //CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "StackWalk(): gle = " << gle << endl;
    }

cleanup:
    ResumeThread( hThread );
    // de-init symbol handler etc. (SymCleanup())
    pSC( hProcess );
    free( pSym );
    delete [] tt;

    FreeLibrary( hImagehlpDll );
}


void enumAndLoadModuleSymbols( HANDLE hProcess, DWORD pid )
{
    ModuleList modules;
    ModuleListIter it;
    char *img, *mod;

    // fill in module list
    fillModuleList( modules, pid, hProcess );

    for( it = modules.begin(); it != modules.end(); ++ it )
    {
        // unfortunately, SymLoadModule() wants writeable strings
        img = new char[(*it).imageName.size() + 1];
        strcpy( img, (*it).imageName.c_str() );
        mod = new char[(*it).moduleName.size() + 1];
        strcpy( mod, (*it).moduleName.c_str() );

        if( pSLM( hProcess, 0, img, mod, (*it).baseAddress, (*it).size ) == 0 )
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Error " << gle << " loading symbols for \"" << (*it).moduleName.c_str() << "\"" << endl;
        }
#ifdef _PRINT_SYMBOLS
        else
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Symbols loaded: \"" << (*it).moduleName.c_str() << "\"" << endl;
        }
#endif

        delete [] img;
        delete [] mod;
    }
}



bool fillModuleList( ModuleList& modules, DWORD pid, HANDLE hProcess )
{
    // try toolhelp32 first
    if( fillModuleListTH32( modules, pid ) )
        return true;
    // nope? try psapi, then
    return fillModuleListPSAPI( modules, pid, hProcess );
}


bool fillModuleListPSAPI( ModuleList& modules, DWORD pid, HANDLE hProcess )
{
    // EnumProcessModules()
    typedef BOOL (__stdcall *tEPM)( HANDLE hProcess, HMODULE *lphModule, DWORD cb, LPDWORD lpcbNeeded );
    // GetModuleFileNameEx()
    typedef DWORD (__stdcall *tGMFNE)( HANDLE hProcess, HMODULE hModule, LPSTR lpFilename, DWORD nSize );
    // GetModuleBaseName() -- redundant, as GMFNE() has the same prototype, but who cares?
    typedef DWORD (__stdcall *tGMBN)( HANDLE hProcess, HMODULE hModule, LPSTR lpFilename, DWORD nSize );
    // GetModuleInformation()
    typedef BOOL (__stdcall *tGMI)( HANDLE hProcess, HMODULE hModule, LPMODULEINFO pmi, DWORD nSize );

    HINSTANCE hPsapi;
    tEPM pEPM;
    tGMFNE pGMFNE;
    tGMBN pGMBN;
    tGMI pGMI;

    int i;
    ModuleEntry e;
    DWORD cbNeeded;
    MODULEINFO mi;
    HMODULE *hMods = 0;
    char *tt = 0;

    hPsapi = LoadLibrary( "psapi.dll" );
    if( hPsapi == 0 )
        return false;

    modules.clear();

    pEPM = (tEPM) GetProcAddress( hPsapi, "EnumProcessModules" );
    pGMFNE = (tGMFNE) GetProcAddress( hPsapi, "GetModuleFileNameExA" );
    pGMBN = (tGMFNE) GetProcAddress( hPsapi, "GetModuleBaseNameA" );
    pGMI = (tGMI) GetProcAddress( hPsapi, "GetModuleInformation" );
    if( pEPM == 0 || pGMFNE == 0 || pGMBN == 0 || pGMI == 0 )
    {
        // yuck. Some API is missing.
        FreeLibrary( hPsapi );
        return false;
    }

    hMods = new HMODULE[TTBUFLEN / sizeof HMODULE];
    tt = new char[TTBUFLEN];
    // not that this is a sample. Which means I can get away with
    // not checking for errors, but you cannot. :)

    if( ! pEPM( hProcess, hMods, TTBUFLEN, &cbNeeded ) )
    {
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "EPM failed, gle = " << gle << endl;
        }
        goto cleanup;
    }

    if( cbNeeded > TTBUFLEN )
    {
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "More than " << lenof( hMods ) << " module handles. Huh?" << endl;
        }
        goto cleanup;
    }

    for( i = 0; i < cbNeeded / sizeof hMods[0]; ++ i )
    {
        // for each module, get:
        // base address, size
        pGMI( hProcess, hMods[i], &mi, sizeof mi );
        e.baseAddress = (DWORD) mi.lpBaseOfDll;
        e.size = mi.SizeOfImage;
        // image file name
        tt[0] = '\0';
        pGMFNE( hProcess, hMods[i], tt, TTBUFLEN );
        e.imageName = tt;
        // module name
        tt[0] = '\0';
        pGMBN( hProcess, hMods[i], tt, TTBUFLEN );
        e.moduleName = tt;
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            char of = dout.fill('0');
            dout << hex << setw(8) << e.baseAddress << " " <<
                dec << setw(6) << e.size << " " << setw(15) <<
                e.moduleName.c_str() << " " <<
                e.imageName.c_str() << endl;

            dout.fill(of);
        }

        modules.push_back( e );
    }

    cleanup:
    if( hPsapi )
        FreeLibrary( hPsapi );
    delete [] tt;
    delete [] hMods;

    return modules.size() != 0;
}


// miscellaneous toolhelp32 declarations; we cannot #include the header
// because not all systems may have it
#define MAX_MODULE_NAME32 255
#define TH32CS_SNAPMODULE   0x00000008
#pragma pack( push, 8 )
typedef struct tagMODULEENTRY32
{
    DWORD   dwSize;
    DWORD   th32ModuleID;       // This module
    DWORD   th32ProcessID;      // owning process
    DWORD   GlblcntUsage;       // Global usage count on the module
    DWORD   ProccntUsage;       // Module usage count in th32ProcessID's context
    BYTE  * modBaseAddr;        // Base address of module in th32ProcessID's context
    DWORD   modBaseSize;        // Size in bytes of module starting at modBaseAddr
    HMODULE hModule;            // The hModule of this module in th32ProcessID's context
    char    szModule[MAX_MODULE_NAME32 + 1];
    char    szExePath[MAX_PATH];
} MODULEENTRY32;
typedef MODULEENTRY32 *  PMODULEENTRY32;
typedef MODULEENTRY32 *  LPMODULEENTRY32;
#pragma pack( pop )

bool fillModuleListTH32( ModuleList& modules, DWORD pid )
{
    // CreateToolhelp32Snapshot()
    typedef HANDLE (__stdcall *tCT32S)( DWORD dwFlags, DWORD th32ProcessID );
    // Module32First()
    typedef BOOL (__stdcall *tM32F)( HANDLE hSnapshot, LPMODULEENTRY32 lpme );
    // Module32Next()
    typedef BOOL (__stdcall *tM32N)( HANDLE hSnapshot, LPMODULEENTRY32 lpme );

    // I think the DLL is called tlhelp32.dll on Win9X, so we try both
    const char *dllname[] = { "kernel32.dll", "tlhelp32.dll"};
    HINSTANCE hToolhelp;
    tCT32S pCT32S;
    tM32F pM32F;
    tM32N pM32N;

    HANDLE hSnap;
    MODULEENTRY32 me = { sizeof me};
    bool keepGoing;
    ModuleEntry e;
    int i;

    for( i = 0; i < lenof( dllname ); ++ i )
    {
        hToolhelp = LoadLibrary( dllname[i] );
        if( hToolhelp == 0 )
            continue;
        pCT32S = (tCT32S) GetProcAddress( hToolhelp, "CreateToolhelp32Snapshot" );
        pM32F = (tM32F) GetProcAddress( hToolhelp, "Module32First" );
        pM32N = (tM32N) GetProcAddress( hToolhelp, "Module32Next" );
        if( pCT32S != 0 && pM32F != 0 && pM32N != 0 )
            break; // found the functions!
        FreeLibrary( hToolhelp );
        hToolhelp = 0;
    }

    if( hToolhelp == 0 ) // nothing found?
        return false;

    hSnap = pCT32S( TH32CS_SNAPMODULE, pid );
    if( hSnap == (HANDLE) -1 )
        return false;

    keepGoing = !!pM32F( hSnap, &me );
    while( keepGoing )
    {
        // here, we have a filled-in MODULEENTRY32
#ifdef _PRINT_SYMBOLS
        {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            char of = dout.fill('0');
            dout << hex << setw(8) << me.modBaseAddr << "h " <<
                dec << setw(6) << me.modBaseSize << " " << setw(15) <<
                me.szModule << " " <<
                me.szExePath << endl;

            dout.fill(of);
        }
#endif
        e.imageName = me.szExePath;
        e.moduleName = me.szModule;
        e.baseAddress = (DWORD) me.modBaseAddr;
        e.size = me.modBaseSize;
        modules.push_back( e );
        keepGoing = !!pM32N( hSnap, &me );
    }

    CloseHandle( hSnap );

    FreeLibrary( hToolhelp );

    return modules.size() != 0;
}



ULONG StrToUlong(UCHAR* buffer, ULONG len)
{
   int i;
   ULONG temp=0;

   for(i = 0; i < (INT)len; i++)
   {
      temp <<= 8;
      temp += buffer[i];
   }

   return temp;
}


ULONG BCDtoBase10(UCHAR* buffer, ULONG len)
{

   int i, j;
   ULONG temp;
   ULONG scratch = 0;

   for(i = (INT)len, j = 0; i > 0; j++, i--)
   {
      temp = 0;

      /* The high nibble */
      temp += (((buffer[j] & 0xf0) >> 4)  * 10);

      /* The Low nibble */
      temp += (buffer[j] & 0x0f);

      scratch = scratch * 100 + temp ;
   }

   return scratch;
}

USHORT  CCITT16CRC(INT Id, UCHAR* buffer, LONG length, BOOL bAdd)
{
   ULONG       i,j;
   BYTEUSHORT   CRC;

   BYTE CRCMSB = 0xff;
   BYTE CRCLSB = 0xff;
   BYTE Temp;
   BYTE Acc;

   CRC.sh = 0;

   if(length > 0)
   {
      switch(Id)
      {
      default:
      case TYPE_ALPHA_A1:
      case TYPE_ALPHA_PPLUS:
         {
            // printf("Alpha Computation\n");
            for(i = 0; i < (ULONG)length; i++)
            {
               CRC.ch[1] ^= buffer[i];

               for(j = 0; j < 8; j++)
               {
                  if(CRC.sh & 0x8000)
                  {
                     CRC.sh = CRC.sh << 1;
                     CRC.sh ^= 0x1021;
                  }
                  else
                  {
                     CRC.sh = CRC.sh << 1;
                  }
               }

            }

            if(bAdd)
            {
               buffer[length]     = CRC.ch[1];
               buffer[length + 1] = CRC.ch[0];
            }

            break;
         }
      case TYPE_VECTRON:
      case TYPE_FULCRUM:
      case TYPE_QUANTUM:
         {
            // printf("Schlumberger Computation\n");
            for(i = 0; i < (ULONG)length; i++)
            {
               Acc = buffer[i];     // a
               Acc ^= CRCMSB;       // b
               Temp = Acc;          // c
               Acc >>= 4;           // d
               Acc ^= Temp;         // e
               Temp = Acc;          // f
               Acc <<= 4;           // g
               Acc ^= CRCLSB;       // h
               CRCMSB = Acc;        // i
               Acc = Temp;          // j
               Acc >>= 3;           // k
               Acc ^= CRCMSB;       // l
               CRCMSB = Acc;        // m
               Acc = Temp;          // n
               Acc <<= 5;           // o
               Acc ^= Temp;         // p
               CRCLSB = Acc;        // q
            }

            CRC.ch[0] = CRCLSB;
            CRC.ch[1] = CRCMSB;

            if(bAdd)
            {
               buffer[length]     = CRC.ch[1];
               buffer[length + 1] = CRC.ch[0];
            }

            break;
         }
      case TYPE_LGS4:
         {
            // check sum is addition of messages 9 bytes
            for(i=0; i < length; i++)
               CRC.sh = CRC.sh + buffer[i];

            if(bAdd)
            {
               // add the checksum here for now
               buffer[9]=CRC.ch[0];
               buffer[10]=CRC.ch[1];
            }
            break;
         }
      case TYPE_KV2:
          {

              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << __FILE__ << " (" << __LINE__ << "): May need CRC code for kv2 implemented here" << endl;
              break;
          }
      }

   }

   return CRC.sh;
}

INT     CheckCCITT16CRC(INT Id,BYTE *InBuffer,ULONG InCount)
{
   BYTEUSHORT  CRC;
   INT         retval = NOTNORMAL;


   switch(Id)
   {
   case TYPE_LGS4:
      {
         CRC.sh = CCITT16CRC(Id, InBuffer, InCount - 2, FALSE);

         if(
           CRC.ch[0] == InBuffer[InCount - 2] &&
           CRC.ch[1] == InBuffer[InCount - 1]
           )
         {
            retval = NORMAL;
         }
         break;
      }
   default:

      if(InCount < 3)
      {
         retval = NORMAL;
      }
      else
      {
         CRC.sh = CCITT16CRC(Id, InBuffer, InCount - 2, FALSE);

         if(
           CRC.ch[0] == InBuffer[InCount - 1] &&
           CRC.ch[1] == InBuffer[InCount - 2]
           )
         {
            retval = NORMAL;
         }
      }
   }

   return retval;
}

USHORT ShortLittleEndian(USHORT *ShortEndianFloat)
{
   /* This guy is in charge of doing the shuffle royale */

   union
   {
      USHORT sh;
      CHAR   ch[2];
   } Flipper;

   USHORT *sptr = ShortEndianFloat;    // We point at the first byte of the destination.
   CHAR  *cptr = (CHAR *) ShortEndianFloat;

   Flipper.ch[1] = *cptr++;
   Flipper.ch[0] = *cptr++;

   *sptr = Flipper.sh;

   return *sptr;

}


FLOAT FltLittleEndian(FLOAT *BigEndianFloat)
{
   /* This guy is in charge of doing the shuffle royale */

   union
   {
      FLOAT fl;
      CHAR  ch[4];
   } Flipper;

   FLOAT *fptr = (FLOAT*) BigEndianFloat;    // We point at the first byte of the destination.

   CHAR  *cptr = (CHAR *) BigEndianFloat;

   Flipper.ch[3] = *cptr++;
   Flipper.ch[2] = *cptr++;
   Flipper.ch[1] = *cptr++;
   Flipper.ch[0] = *cptr++;

   *fptr = Flipper.fl;

   return *fptr;

}

DOUBLE DblLittleEndian(DOUBLE *BigEndianDouble)
{
   /* This guy is in charge of doing the shuffle royale */

   union
   {
      DOUBLE   db;
      CHAR     ch[8];
   } Flipper;

   DOUBLE *dptr = (DOUBLE*) BigEndianDouble;    // We point at the first byte of the destination.

   CHAR   *cptr = (CHAR *) BigEndianDouble;

   Flipper.ch[7] = *cptr++;
   Flipper.ch[6] = *cptr++;
   Flipper.ch[5] = *cptr++;
   Flipper.ch[4] = *cptr++;
   Flipper.ch[3] = *cptr++;
   Flipper.ch[2] = *cptr++;
   Flipper.ch[1] = *cptr++;
   Flipper.ch[0] = *cptr++;

   *dptr = Flipper.db;

   return *dptr;
}

VOID BDblLittleEndian(CHAR *BigEndianBDouble)
{
   /* This guy is in charge of doing the shuffle royale */

   CHAR     Flipper[9];
   CHAR   *cptr = BigEndianBDouble;

   Flipper[8] = *cptr++;
   Flipper[7] = *cptr++;
   Flipper[6] = *cptr++;
   Flipper[5] = *cptr++;
   Flipper[4] = *cptr++;
   Flipper[3] = *cptr++;
   Flipper[2] = *cptr++;
   Flipper[1] = *cptr++;
   Flipper[0] = *cptr++;

   memcpy(BigEndianBDouble, Flipper, 9);
}

/* Function to return system time in milliseconds */
ULONG MilliTime (PULONG MilliSeconds)
{
   struct timeb TimeB;

   UCTFTime (&TimeB);

   if(MilliSeconds != NULL)
   {
      *MilliSeconds = TimeB.time * 1000L + TimeB.millitm;
   }

   return(TimeB.time * 1000L + TimeB.millitm);
}

BOOL searchFuncForOutMessageDevID(void *pId, void* d)
{
    LONG Id = (LONG)pId;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->DeviceID == Id);
}

BOOL searchFuncForOutMessageRteID(void *pId, void* d)
{
    LONG Id = (LONG)pId;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->Request.RouteID == Id);
}

BOOL searchFuncForOutMessageUniqueID(void *pId, void* d)
{
    LONG Id = (LONG)pId;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->Request.CheckSum == Id);
}

LONG ResetBreakAlloc()
{
    long allocReqNum = 0;

#ifdef _DEBUG
    long *my_pointer = (long*)new long;
    _CrtIsMemoryBlock(my_pointer, sizeof(long), &allocReqNum, NULL, NULL);
    /*
     * Set a breakpoint on the allocation request number for "my_pointer"
     * this should keep us from breaking for at least 2^31 more allocations.
     */
    _CrtSetBreakAlloc(allocReqNum);
    _crtBreakAlloc = allocReqNum;
    delete my_pointer;
#endif

    return allocReqNum;
}

#define LOADPROFILESEQUENCE 4  //  CtiProtocolEmetcon::Scan_LoadProfile


bool findLPRequestEntries(void *om, void* d)
{
    OUTMESS *NewGuy = (OUTMESS *)om;
    OUTMESS *OutMessage = (OUTMESS *)d;
    bool bRet = false;

    if(NewGuy && OutMessage)
    {
        if( (OutMessage->Sequence == LOADPROFILESEQUENCE) &&
            (NewGuy->Sequence     == LOADPROFILESEQUENCE) &&
            (OutMessage->TargetID == NewGuy->TargetID) )
        {
            RWCString oldstr(OutMessage->Request.CommandStr);
            RWCString newstr(NewGuy->Request.CommandStr);

            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Comparing Outmessage->CommandStr" << endl;
                dout << "NewGuy     = \"" << NewGuy->Request.CommandStr << "\" and" << endl;
                dout << "OutMessage = \"" << OutMessage->Request.CommandStr << "\"" << endl;
            }

            bRet = oldstr.contains("scan loadprofile") && newstr.contains("scan loadprofile");

            if( bRet && oldstr.contains(" channel", RWCString::ignoreCase) )
            {
                RWCRExpr re_channel(" channel +[0-9]");

                if( !(newstr.match(re_channel) == oldstr.match(re_channel)) )
                {
                    bRet = false;
                }
            }
        }
    }

    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " bRet = " << bRet << endl;
    }

    return(bRet);
}

void cleanupOutMessages(void *unusedptr, void* d)
{
    OUTMESS *OutMessage = (OUTMESS *)d;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Removing Outmessage for TargetID " << OutMessage->TargetID << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    delete OutMessage;
    return;
}

void applyPortQueueOutMessageReport(void *ptr, void* d)
{
    CtiQueueAnalysis_t *pQA = (CtiQueueAnalysis_t*)ptr;
    OUTMESS *OutMessage = (OUTMESS *)d;

    if(pQA != 0 &&
       OutMessage->HeadFrame[0] == 0x02 && OutMessage->HeadFrame[1] == 0xe0 &&
       OutMessage->TailFrame[0] == 0xea && OutMessage->TailFrame[1] == 0x03)
    {
        // This is indeed an OutMessage I like
        int i = OutMessage->Priority;

        if(0 < i && i < 16)
        {
            pQA->priority_count[i] = pQA->priority_count[i] + 1;    // Accumulate on into this priority bin!
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        // Now I need to look for some of the interesting metrics in the system!  These will be the first 16.
        // #define NOWAIT          0x0000
        // #define NORESULT        0x0000
        // #define WAIT            0x0001
        // #define RESULT          0x0002
        // #define QUEUED          0x0004
        // #define ACTIN           0x0008
        // #define AWORD           0x0010
        // #define BWORD           0x0020
        // #define DTRAN           0x0040
        // #define RCONT           0x0080
        // #define RIPPLE          0x0100
        // #define STAGE           0x0200
        // #define VERSACOM        0x0400
        // #define TSYNC           0x0800
        // #define REMS            0x1000   // This can never be used now.... CGP Corey.
        // #define FISHERPIERCE    0x1000
        // #define ENCODED         0x4000
        // #define DECODED         0x4000
        // #define COMMANDCODE     0x8000

        UINT ec = OutMessage->EventCode;

        for(i = 0; i < 16; i++)
        {
            if( ec >> i & 0x00001 )
            {
                pQA->metrics[i] = pQA->metrics[i] + 1;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Malformed OutMessage.  Header and Footer are not valid. **** " << endl;
    }

    return;
}


