

// includes /////////////////////////////////////////////////////

#include <windows.h>
#include <iostream>
using namespace std;

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
    RWCString sql("SELECT MAX(LMCTRLHISTID) FROM LMCONTROLHISTORY WHERE PAOBJECTID = ");
    INT id = 0;

    sql += RWCString(CtiNumStr(pao));

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

    /* Lotsa stuff requires the InMessage to be loaded so load it */
    memcpy(&(Out->Request), &(In->Return), sizeof(PIL_ECHO));

    return bRet;
}
BOOL OutEchoToIN(const OUTMESS *Out, INMESS *In)
{
    BOOL bRet = FALSE;

    /* Lotsa stuff requires the InMessage to be loaded so load it */
    memcpy(&(In->Return), &(Out->Request), sizeof(PIL_ECHO));
    //  save this for the macro routes
    In->Priority = Out->Priority;

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
/*
   static struct {
      char *proj;
      int major;
      int minor;
      int build;
   } CompileInfo = {
      PROJECT,
      MAJORREVISION,
      MINORREVISION,
      BUILDNUMBER
   };
*/

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(Info.date)
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
/*
   static struct {
      char *fname;
      double rev;
      char *date;
   } VersionInfo[];

*/

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
    BOOL        foundFirst = FALSE;
    CHAR        temp[20];
    RWCString   rStr;
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

