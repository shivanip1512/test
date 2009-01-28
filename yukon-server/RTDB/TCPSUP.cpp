/*---------------------------------------------------------------------
    Copyright (c) 1990-1996 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        TCPSUP.C

    Purpose:
        To handle initialization, handshaking and control of TCP/IP terminal server

    The following procedures are contained in this module:
        TCPInit                         TCPSend
        TCPReceive                      TCPInputFlush
        TCPClose

    Initial Date:
       12-20-96

    Revision History:

   -------------------------------------------------------------------- */
#include "yukon.h"


// These next few are required for Win32
#include <winsock.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include "queues.h"
#include "dsm2.h"
#include "porter.h"

#include "dlldefs.h"
#include "tcpsup.h"

#include "c_port_interface.h"
#include "rtdb.h"
#include "port_base.h"
#include "utility.h"


ULONG DoRequestMutexSem(HMTX MyMutex, ULONG MyTimeOut, INT iLine, CHAR *szFunction);

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    WORD    wVersionRequested = MAKEWORD(1,1);
    WSADATA wsaData;

    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);

            // Initialize WinSock and check version
            int err = WSAStartup(wVersionRequested, &wsaData);

            if(wsaData.wVersion != wVersionRequested)
            {
                fprintf(stderr,"\nWrong WINSOCK version\n");
                return FALSE;
            }
            else if(err)
            {
                fprintf(stderr,"\nFatal WINSOCK error %d in TCPSUP.DLL\n", err);
                return FALSE;
            }

            break;
        }
        case DLL_THREAD_ATTACH:
        {
            break;
        }
        case DLL_THREAD_DETACH:
        {
            break;
        }
        case DLL_PROCESS_DETACH:
        {
            WSACleanup();

            break;
        }
    }

    return TRUE;
}

/* Routine to send a message to a TCP/IP Terminal Server port */
IM_EX_TCPSUP INT TCPSend (NETCXPORTINFO *MyPortInfo, PBYTE Message, ULONG Length)
{
   int i;
   USHORT CharsToSend;
   ULONG TimeToSend;

   if((send (MyPortInfo->TerminalSocket, (CHAR*)Message, Length, 0)) != (INT)Length)
   {
      TCPClose (MyPortInfo);
      printf ("Error Sending Message to Terminal Server:  ", WSAGetLastError ());
      return(TCPWRITEERROR);
   }

   /* On normal terminal server it does not matter if we sit */
   return(NORMAL);
}

/* Routine to receive a message from a TCP/IP Terminal Server Port */
IM_EX_TCPSUP INT TCPReceive (NETCXPORTINFO   *MyPortInfo,
                             PBYTE           Message,
                             LONG            Length,
                             ULONG           TimeOut,
                             PLONG           ReceiveLength)
{
   int WaitCount = 0;
   ULONG ulTemp;

   /* Wait up to timeout for characters to be available */
   *ReceiveLength = 0;
   while((ULONG)WaitCount++ <= ((TimeOut * 1000L) / 50L))
   {
      /* Find out if we have any bytes */
      ioctlsocket ((ULONG)MyPortInfo->TerminalSocket, FIONREAD, &ulTemp);

      /* if a specific length specified wait for at least that much */
      if(Length > 0)
      {
         if((LONG)ulTemp < Length)
         {
            CTISleep (50L);
         }
         else
         {
            break;                     // the while loop ends now.
         }
      }
      else                             // Otherwise any length will do
      {
         if(ulTemp == 0)               // Wait for something, or the timeout.
         {
            CTISleep (50L);
         }
         else
         {
            break;                     // the while loop ends now.
         }
      }
   }

   if(ulTemp == 0)
   {
      return(READTIMEOUT);
   }

   if(Length > 0)
   {
      /* Go ahead and actually read some bytes */
      if((*ReceiveLength = recv (MyPortInfo->TerminalSocket, (CHAR*)Message, Length, 0)) <= 0)
      {
         TCPClose (MyPortInfo);
         printf ("Read from Terminal Server failed:  %ld\n", WSAGetLastError ());
         return(TCPREADERROR);
      }

      if(*ReceiveLength < Length)
      {
         return(READTIMEOUT);
      }
   }
   else
   {
      /* Go ahead and actually read some bytes */
      if((*ReceiveLength = recv (MyPortInfo->TerminalSocket, (CHAR*)Message, -Length, 0)) <= 0)
      {
         TCPClose (MyPortInfo);
         printf ("Read from Terminal Server failed:  %ld\n", WSAGetLastError ());
         return(TCPREADERROR);
      }
   }

   return(NORMAL);
}


/* Routine to receive a message from a TCP/IP Terminal Server Port */
IM_EX_TCPSUP INT TCPReceiveChars (TCPIPSTRUCT *TCPIPStruct,
                                  PBYTE Message,
                                  ULONG Length,
                                  ULONG TimeOut,
                                  PULONG ReceiveLength)
{
   int WaitCount = 0;
   int Result;

   /* Wait up to timeout for characters to be available */
   *ReceiveLength = 0;
   while((ULONG)WaitCount++ <= ((TimeOut * 1000L) / 50L))
   {
      /* Find out if we have any bytes */
      if(ioctlsocket (TCPIPStruct->TCPIPSocket, FIONREAD, ReceiveLength))
      {
         printf ("TCPIP Connection Failed on ioctlsocket:  %ld\n", WSAGetLastError ());
         /* Check if someone else failed it */
         if(TCPIPStruct->TCPIPFailed != TRUE)
         {
            TCPIPStruct->TCPIPFailed = TRUE;
            SocketShutdownClose(__FILE__, __LINE__, TCPIPStruct->TCPIPSocket);
            TCPIPStruct->TCPIPSocket = (int) NULL;
            TCPIPStruct->MyPortInfo.TerminalSocket = 0;
            CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
            TCPIPStruct->Busy = 0;
         }
         return(TCPREADERROR);
      }

      /* if a specific length specified wait for at least that much */
      if(*ReceiveLength < Length)
      {
         CTISleep (50L);
      }
      else
      {
         break;
      }
   }

   if(*ReceiveLength < Length)
   {
      return(READTIMEOUT);
   }

   if(Length > 0)
   {
      /* Go ahead and actually read some bytes */
      if((*((int *) ReceiveLength) = recv (TCPIPStruct->TCPIPSocket, (CHAR*)Message, Length, 0)) <= 0)
      {
         printf ("Read from Terminal Server failed on read:  %ld\n", WSAGetLastError ());
         /* Check if someone else failed it */
         if(TCPIPStruct->TCPIPFailed != TRUE)
         {
            TCPIPStruct->TCPIPFailed = TRUE;
            SocketShutdownClose(__FILE__, __LINE__, TCPIPStruct->TCPIPSocket);
            TCPIPStruct->TCPIPSocket = (int) NULL;
            TCPIPStruct->MyPortInfo.TerminalSocket = 0;
            CTIPostEventSem (TCPIPStruct->TCPIPFailedSem);
            TCPIPStruct->Busy = 0;
         }
         return(TCPREADERROR);
      }

      if(*ReceiveLength < Length)
      {
         return(READTIMEOUT);
      }
   }

   return(NORMAL);
}


/* Routine to flush the output buffer */
IM_EX_TCPSUP INT TCPOutputFlush (NETCXPORTINFO *MyPortInfo)
{
   return(NORMAL);
}


/* Routine to flush the receive buffer */
IM_EX_TCPSUP INT TCPInputFlush (NETCXPORTINFO *MyPortInfo)
{
   PBYTE Buffer;
   ULONG ulTemp;

   // How many are available ??
   ioctlsocket (MyPortInfo->TerminalSocket, FIONREAD, &ulTemp);

   if(ulTemp == 0)
   {
      return(NORMAL);
   }

   if((Buffer = (PBYTE)malloc (ulTemp)) == NULL)
   {
      return(MEMORY);
   }

   if(recv (MyPortInfo->TerminalSocket, (PCHAR)Buffer, (int)ulTemp, 0) <= 0)
   {
      TCPClose (MyPortInfo);
      free (Buffer);
      return(TCPREADERROR);
   }

   free (Buffer);
   return(NORMAL);
}

/* Routine to flush the receive buffer on the Terminal server! */
IM_EX_TCPSUP INT TCPInputFlushTS (NETCXPORTINFO *MyPortInfo)
{
   return TCPInputFlush( MyPortInfo );
}


/* Routine to queury characters available */
IM_EX_TCPSUP INT TCPQuery (NETCXPORTINFO *MyPortInfo)
{
   ULONG ReceiveLength = 0;

   if(ioctlsocket (MyPortInfo->TerminalSocket, FIONREAD, &ReceiveLength))
   {
      TCPClose (MyPortInfo);
      return(0);
   }

   return((INT)ReceiveLength);
}



/* Routine to change the baud rate */
IM_EX_TCPSUP INT TCPSetBaudRate (NETCXPORTINFO *MyPortInfo, USHORT BaudRate)
{
   MyPortInfo->Baud = BaudRate;

   return(NORMAL);
}


/* Routine to Raise RTS on a Terminal Server */
IM_EX_TCPSUP INT TCPRaiseRTS (NETCXPORTINFO *MyPortInfo)
{
   return(NORMAL);
}


/* Routine to Raise RTS on a Terminal Server */
IM_EX_TCPSUP INT TCPLowerRTS (NETCXPORTINFO *MyPortInfo)
{
   return(NORMAL);
}


/* Routine to Raise DTR on a Terminal Server */
IM_EX_TCPSUP INT TCPRaiseDTR (NETCXPORTINFO *MyPortInfo)
{
   return(NORMAL);
}


/* Routine to Lower DTR on a Terminal Server */
IM_EX_TCPSUP INT TCPLowerDTR (NETCXPORTINFO *MyPortInfo)
{
   return(NORMAL);
}


/* Routine to Check DCD on a Terminal Server */
IM_EX_TCPSUP INT TCPDCDTest (NETCXPORTINFO *MyPortInfo)
{
   return(TRUE);
}


/* Routine to Check CTS on a Terminal Server */
IM_EX_TCPSUP INT TCPCTSTest (NETCXPORTINFO *MyPortInfo)
{
   return(TRUE);
}


/* Routine to Check DSR on a Terminal Server */
IM_EX_TCPSUP INT TCPDSRTest (NETCXPORTINFO *MyPortInfo)
{
   return(TRUE);
}


/* Routine to Close a TCP/IP Terminal Server */
IM_EX_TCPSUP INT TCPClose (NETCXPORTINFO *MyPortInfo)
{
   return(TRUE);
}


ULONG  DoRequestMutexSem(HMTX MyMutex, ULONG MyTimeOut, INT iLine, CHAR *szFunction)
{
   ULONG  ulRet = 0;
   ULONG  i = 0;

   if(MyTimeOut == SEM_INDEFINITE_WAIT)
   {
      ulRet = ERROR_TIMEOUT;
      while(ulRet == ERROR_TIMEOUT)
      {
         ulRet = CTIRequestMutexSem(MyMutex, 10000L);
         if(ulRet == ERROR_TIMEOUT)
         {
            printf("Waiting on Mutex at %d, %s\n",iLine, szFunction);
         }
      }
   }
   else
   {
      ulRet = CTIRequestMutexSem(MyMutex, MyTimeOut);
   }

   return(ulRet);

}

IM_EX_TCPSUP INT SocketShutdownClose(PCHAR Label, ULONG Line, SOCKET &Socket)
{
   INT   iRet = 0;

   if(Socket != INVALID_SOCKET)
   {
      fprintf(stderr,"Closing socket %d from %s (%d) ... \n", Socket ,Label, Line);
      shutdown(Socket, 2);
      if(closesocket(Socket))
      {
         iRet = WSAGetLastError();
         fprintf(stderr,"\nSocket #%d Close Failed.  Error = %d\n",Socket, iRet);
      }
      Socket = INVALID_SOCKET;
   }

   return(iRet);
}

/* Routine to flush the receive buffer */
IM_EX_TCPSUP INT SocketInputFlush(SOCKET &sock)
{
   PBYTE Buffer;
   ULONG ulTemp = 0;
   INT status = NORMAL;

   if(sock != INVALID_SOCKET)
   {
      // How many are available ??
      ioctlsocket (sock, FIONREAD, &ulTemp);

      if(ulTemp != 0)
      {
         if((Buffer = (PBYTE)malloc (ulTemp)) == NULL)
         {
            status = (MEMORY);
         }
         else
         {
            if(recv (sock, (PCHAR)Buffer, (int)ulTemp, 0) <= 0)
            {
               status = TCPREADERROR;
               SocketShutdownClose(__FILE__, __LINE__, sock);
            }

            free (Buffer);
         }
      }
   }

   return(status);
}


