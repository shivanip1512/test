#include "yukon.h"


#pragma title ( "Low Level Port Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTSUP.C

    Purpose:
        To handle the actual handshaking and initialization of serial ports

    The following procedures are contained in this module:
        SetLineMode             SetBaudRate
        SetDefaultDCB           SetReadTimeOut
        SetWriteTimeOut         ModemDCD
        ModemDSR                ModemCTS
        RaiseModemRTS           LowerModemRTS
        RaiseModemDTR           LowerModemDTR
        PortInputFlush          PortOutputFlush
        GetPortOutputQueueCount GetPortBaudRate
        MilliTime               ModemReset
        ModemSetup              ModemConnect

    Initial Date:
        9-30-93

    Revision History:
        10-1-93     Moved Millitime here from pport.c       WRO

   -------------------------------------------------------------------- */
#include <windows.h>       // These next few are required for Win32
#include <iostream>
using namespace std;

#include <rw\rwtime.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "queues.h"
#include "dsm2.h"
#include "dllbase.h"
#include "color.h"
#include "dupreq.h"
#include "devicetypes.h"
#include "logger.h"


BOOL ValidModemResponse (PCHAR Response);

#if 1
static INT SetLineMode (HANDLE, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
static INT SetLineModeE71 (HANDLE);
static INT SetLineModeTAPTerm (HANDLE);
static INT SetBaudRate (HANDLE, USHORT);
static INT SetDefaultDCB (HANDLE);
static INT EnableXONXOFF (HANDLE);
static INT DisableXONXOFF (HANDLE);
static INT SetReadTimeOut (HANDLE, USHORT);
static INT SetWriteTimeOut (HANDLE, USHORT);
static INT ModemDCD (HANDLE);
static INT ModemDSR (HANDLE);
static INT ModemCTS (HANDLE);
static INT RaiseModemRTS (HANDLE);
static INT LowerModemRTS (HANDLE);
static INT RaiseModemDTR (HANDLE);
static INT LowerModemDTR (HANDLE);
static INT PortInputFlush (HANDLE);
static INT GetPortInputQueueCount (HANDLE);
static INT PortOutputFlush (HANDLE);
static INT GetPortCommError (HANDLE);
static INT ModemReset (HANDLE, USHORT, USHORT, BOOL dcdTest = TRUE);
static INT ModemHangup (HANDLE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
static INT ModemSetup (HANDLE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
static INT ModemConnect (HANDLE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
static ULONG GetPortOutputQueueCount (HANDLE);
static USHORT GetPortBaudRate (HANDLE);
static USHORT GetPortCommEvent (HANDLE);
#endif



/* Routine to set the line characteristics on a port */
SetLineMode (HANDLE PortHandle, INT bits, INT parity, INT stopbits )
{
   DCB  dcb;
   BOOL fSuccess;

   fSuccess = GetCommState(PortHandle, &dcb);

   if(fSuccess)
   {
      dcb.ByteSize  = bits;
      dcb.Parity    = parity;
      dcb.StopBits  = stopbits;

      fSuccess = SetCommState(PortHandle, &dcb);
   }

   if(!fSuccess)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << __FILE__ << " (" << __LINE__ << "): System Error" << endl;
      return SYSTEM;
   }

   return(NORMAL);
}


SetLineModeE71 (HANDLE PortHandle)
{
   return(SetLineMode(PortHandle, 7, EVENPARITY, ONESTOPBIT));
}


/* set the Line mode stuff for TAP Terminals, 7E1, XON, XOFF */
SetLineModeTAPTerm (HANDLE PortHandle)
{
   ULONG i;

   if((i = SetLineModeE71 (PortHandle)) != NORMAL)
   {
      return(i);
   }

   return(EnableXONXOFF(PortHandle));
}


/* Routine to set the baud rate for a port */
SetBaudRate (HANDLE PortHandle, USHORT BaudRate)
{
   DCB  dcb;
   BOOL fSuccess;

   fSuccess = GetCommState(PortHandle, &dcb);

   if(fSuccess)
   {
      dcb.BaudRate = BaudRate;
      fSuccess = SetCommState(PortHandle, &dcb);
   }

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}


/* Routine to set the default DCB Info for a port */
SetDefaultDCB (HANDLE PortHandle)
{
   DCB  dcb;
   BOOL fSuccess;

   fSuccess = GetCommState(PortHandle, &dcb);

   if(fSuccess)
   {
      dcb.fOutxCtsFlow = FALSE;    // CTS is not monitored for flow control
      dcb.fOutxDsrFlow = FALSE;    // DSR is not monitored for flow control
      dcb.fOutX        = FALSE;
      dcb.fInX         = FALSE;
      dcb.fErrorChar   = FALSE;
      dcb.fNull        = FALSE;
      dcb.fRtsControl  = RTS_CONTROL_DISABLE;

      fSuccess = SetCommState(PortHandle, &dcb);
   }

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}


/* Routine to set the default DCB Info for a port */
EnableXONXOFF(HANDLE PortHandle)
{
   DCB  dcb;
   BOOL fSuccess;

   fSuccess = GetCommState(PortHandle, &dcb);

   if(fSuccess)
   {
      dcb.fOutX  = TRUE;
      dcb.fInX   = TRUE;

      fSuccess = SetCommState(PortHandle, &dcb);
   }

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}
/* Routine to set the default DCB Info for a port */
DisableXONXOFF(HANDLE PortHandle)
{
   DCB  dcb;
   BOOL fSuccess;

   fSuccess = GetCommState(PortHandle, &dcb);

   if(fSuccess)
   {
      dcb.fOutX  = FALSE;
      dcb.fInX   = FALSE;

      fSuccess = SetCommState(PortHandle, &dcb);
   }

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}



/* Routine to set the read timeout for a port */
SetReadTimeOut (HANDLE PortHandle, USHORT TimeOut)
{
   COMMTIMEOUTS  cto;

   GetCommTimeouts(PortHandle, &cto);

   cto.ReadIntervalTimeout = 0;
   cto.ReadTotalTimeoutMultiplier = 0;
   cto.ReadTotalTimeoutConstant = (TimeOut * 1000);

   SetCommTimeouts(PortHandle, &cto);

   return(NORMAL);
}


/* Routine to set the write timeout for a port */
SetWriteTimeOut (HANDLE PortHandle, USHORT TimeOut)
{
   COMMTIMEOUTS  cto;

   GetCommTimeouts(PortHandle, &cto);

   cto.WriteTotalTimeoutMultiplier = 0;
   cto.WriteTotalTimeoutConstant = (TimeOut * 1000);

   SetCommTimeouts(PortHandle, &cto);

   return(NORMAL);
}



/* Routine to raise RTS on a port */
RaiseModemRTS (HANDLE PortHandle)
{
   BOOL fSuccess;

   fSuccess = EscapeCommFunction(PortHandle, SETRTS);

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}


/* Routine to lower RTS on a port */
LowerModemRTS (HANDLE PortHandle)
{
   BOOL fSuccess;

   fSuccess = EscapeCommFunction(PortHandle, CLRRTS);

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}


/* Routine to raise DTR on a port */
RaiseModemDTR (HANDLE PortHandle)
{
   BOOL fSuccess;

   fSuccess = EscapeCommFunction(PortHandle, SETDTR);

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}


/* Routine to lower DTR on a port */
LowerModemDTR (HANDLE PortHandle)
{
   BOOL fSuccess;
   fSuccess = EscapeCommFunction(PortHandle, CLRDTR);

   if(!fSuccess)
   {
      return SYSTEM;
   }

   return(NORMAL);
}


/* Routine to clear the receive buffer on a port */
PortInputFlush (HANDLE PortHandle)
{
   if(!PurgeComm(PortHandle, PURGE_RXCLEAR))
   {
      return SYSTEM;
   }

   return(NORMAL);
}



/* Routine to clear the receive buffer on a port */
GetPortInputQueueCount (HANDLE PortHandle)
{
   DWORD   Err;
   COMSTAT Stat;

   if(ClearCommError(PortHandle, &Err, &Stat))
   {
      return Stat.cbInQue;
   }

   return 0;
}


/* Routine to clear the transmit buffer on a port */
PortOutputFlush (HANDLE PortHandle)
{
   if(!PurgeComm(PortHandle, PURGE_TXCLEAR))
   {
      return SYSTEM;
   }

   return(NORMAL);
}


ULONG GetPortOutputQueueCount (HANDLE PortHandle)
{
   DWORD   Err;
   COMSTAT Stat;

   if(ClearCommError(PortHandle, &Err, &Stat))
   {
      return Stat.cbOutQue;
   }

   return 0;
}

USHORT GetPortBaudRate (HANDLE PortHandle)
{
   DCB  dcb;
   BOOL fSuccess;

   fSuccess = GetCommState(PortHandle, &dcb);

   if(fSuccess)
   {
      return dcb.BaudRate;
   }

   return NORMAL;
}



USHORT GetPortCommEvent (HANDLE PortHandle)
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << __FILE__ << " (" << __LINE__ << "): Function has not been implemented" << endl;
   return NORMAL;
}



INT GetPortCommError (HANDLE PortHandle)
{
   DWORD   Errors;

   BOOL fSuccess;

   fSuccess = ClearCommError(PortHandle, &Errors, NULL);

   if(!fSuccess)
   {
      return SYSTEM;
   }


   return Errors;
}



/* Routine to force the reset of modem */
ModemReset (HANDLE PortHandle, USHORT Port, USHORT Trace, BOOL dcdTest)
{
   CHAR Response[100];
   ULONG ResponseSize;
   ULONG BytesWritten;
   ULONG i = 0;
   static ULONG tCount = 0;

   /* set the timeout on read to 1 second */
   SetReadTimeOut (PortHandle, 1);

   LowerModemRTS (PortHandle);
   LowerModemDTR (PortHandle);
   CTISleep ( 500L );
   RaiseModemDTR (PortHandle);
   RaiseModemRTS (PortHandle);
   CTISleep ( 500L );

   /* If we do not have CTS it is a problem */
   if(!(ModemCTS (PortHandle)))
   {
      if(!(++tCount % 300))
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << "  Port " << Port << " No Modem CTS.  Modem may be off or configured wrong" << endl;
      }

      LowerModemDTR (PortHandle);
      CTISleep ( 500L );
      RaiseModemDTR (PortHandle);
      RaiseModemRTS (PortHandle);
      CTISleep ( 500L );

      if(!(ModemCTS (PortHandle)))
      {
         return READTIMEOUT;
      }
   }

   /* Try five times to intialize modem */
   for(i = 0; i < 5; i++)
   {

      /* Wait for CTS */
      if(!(ModemCTS (PortHandle)))
      {
         if(!(++tCount % 300))
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << Port << " No Modem CTS..." << endl;
            }
         }

         LowerModemDTR (PortHandle);
         CTISleep ( 500L );
         RaiseModemDTR (PortHandle);
         RaiseModemRTS (PortHandle);
         CTISleep ( 500L );

         CTISleep ( 100L );

         if(!(ModemCTS (PortHandle)))
         {
            return READTIMEOUT;
         }
      }

      /* Clear the buffer */
      PortInputFlush (PortHandle);
      ResponseSize = sizeof (Response);

      /* Wait to see if we get the no carrier message */
      if( !(WaitForResponse (PortHandle, &ResponseSize, Response, 1)) )      // See if we get something back from this guy.
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
         }
      }

      ResponseSize = sizeof (Response);
      // Make an attempt to determine if we are in the command mode
      CTIWrite (PortHandle, "AT\r", 3, &BytesWritten);    // Attention to the modem.
      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Sent to Modem:  AT" << endl;
      }

      /* Wait for a response or till we time out */
      if(WaitForResponse (PortHandle, &ResponseSize, Response, 3, "OK"))
      {
         // Just in case we are out to lunch here...
         CTIWrite (PortHandle, "+++", 3, &BytesWritten);    // Attention to the modem.

         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Sent to Modem:  +++" << endl;
         }

         ResponseSize = sizeof (Response);

         /* Wait for a response or till we time out */
         if(WaitForResponse (PortHandle, &ResponseSize, Response, 3, "OK"))
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << Port << " Modem Response Timeout" << endl;
            }

            CTIWrite (PortHandle, "\r", 1, &BytesWritten);    // Get rid of the trash that didn't give a good return.
         }

      }
      else if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
      }


      ResponseSize = sizeof (Response);
      /* Send it a reset message (Ask for an OK) */
      CTIWrite (PortHandle, "ATZ\r", 4, &BytesWritten);

      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Sent to Modem:  ATZ" << endl;
      }

      ResponseSize = sizeof (Response);

      /* Wait for a response or till we time out */
      if(WaitForResponse (PortHandle, &ResponseSize, Response, 4, "OK"))
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Modem Response Timeout" << endl;
         }
         return(READTIMEOUT);
      }
      else if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
      }

      /* Make sure that we got OK */
      if(!(strnicmp (Response, "OK", 2)))
      {
         return(NORMAL);
      }
   }

   return(NOTNORMAL);
}



/* Routine to send setup string(s) to the modem */
ModemSetup (HANDLE PortHandle, PCHAR Message, USHORT Port, USHORT Trace, BOOL dcdTest)
{
   ULONG BytesWritten;
   CHAR MyMessage[100];
   CHAR Response[100];
   ULONG ResponseSize;
   ULONG i, j;

   // Remove any quotes
   if(Message[0] == '"')
   {
      strcpy (MyMessage, Message + 1);
      if(MyMessage[strlen (MyMessage) - 1] == '"')
      {
         MyMessage[strlen (MyMessage) - 1] = '\0';
      }
   }
   else
   {
      strcpy (MyMessage, Message);
   }

   // Try up to five times
   for(j = 0; j < 5; j++)
   {
      CTISleep ( 250L);

      /* Flush the input buffer */
      PortInputFlush (PortHandle);

      if(CTIWrite (PortHandle, MyMessage, strlen (MyMessage), &BytesWritten) ||
         BytesWritten != strlen (MyMessage))
      {
         return(NOTNORMAL);
      }

      /* Send the CR */
      CTIWrite (PortHandle, "\r", 1, &BytesWritten);

      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Sent to Modem:  " << MyMessage << endl;
      }

      ResponseSize = sizeof (Response);

      /* Wait for a response or till we time out */
      if(WaitForResponse (PortHandle, &ResponseSize, Response, 3, "OK"))
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Modem Response Timeout" << endl;
         }
         continue;
      }

      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
      }

      /* Make sure that we got OK */
      if(!(strnicmp (Response, "OK", 2)))
      {
         break;
      }
      else if(!(strnicmp (Response, "ERROR", 5)))
      {
         return(NOTNORMAL);
      }
   }


   if(j >= 5)
   {
      return(NOTNORMAL);
   }

   return(NORMAL);
}

/* Routine to establish modem connection */
ModemConnect (HANDLE PortHandle, PCHAR Message, USHORT Port, USHORT Trace, BOOL dcdTest)
{
   ULONG BytesWritten, i;
   CHAR MyMessage[100];
   CHAR Response[100];
   ULONG ResponseSize;

   /* set the timeout on read to 1 second */
   SetReadTimeOut (PortHandle, 1);

   if(Message[0] == 'A' || Message[0] == 'a')
   {
      strcpy (MyMessage, Message);
   }
   else
   {
      strcpy (MyMessage, "ATDT");
      strcat (MyMessage, Message);
   }

   CTISleep ( 250L);

   /* Flush the input buffer */
   PortInputFlush (PortHandle);

   /* Perform the dial */
   if(CTIWrite (PortHandle, MyMessage, strlen (MyMessage),&BytesWritten) ||
      BytesWritten != strlen (MyMessage))
   {
      return(NOTNORMAL);
   }

   /* Send the CR */
   CTIWrite (PortHandle, "\r", 1, &BytesWritten);

   if(Trace)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Port " << Port << " Sent to Modem:  " << MyMessage << endl;
   }

   ResponseSize = sizeof (Response);

   /* Wait for a response from the modem */
   if(WaitForResponse(PortHandle, &ResponseSize, Response, ModemConnectionTimeout, "CONNECT"))
   {
      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Modem Response Timeout" << endl;
      }
      return(READTIMEOUT);
   }

   if(Trace)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
   }

   /* We have a response so see what it is */
   if(!(strnicmp (Response, "CONNECT", 7)))
   {
      /* Wait up to 2 seconds for CD */
      if(dcdTest == TRUE)
      {
         for(i = 0; i < 20; i++)
         {
            if(ModemDCD (PortHandle))
            {
               /* Raise RTS */
               RaiseModemRTS (PortHandle);

               CTISleep ( 200L);       // CTISleep ( 2000L );

               PortInputFlush (PortHandle);

               return(NORMAL);
            }

            CTISleep ( 100L);
         }
      }
      else     // Proceed, ignoring DCD
      {
         /* Raise RTS */
         RaiseModemRTS (PortHandle);
         CTISleep ( 200L);       // CTISleep ( 2000L );
         PortInputFlush (PortHandle);
         return(NORMAL);
      }
   }
   else if(!(strnicmp (Response, "NO CARRIER", 10)))
   {
   }
   else if(!(strnicmp (Response, "ERROR", 5)))
   {
   }
   else if(!(strnicmp (Response, "NO DIAL TONE", 12)))
   {
   }
   else if(!(strnicmp (Response, "NO ANSWER", 9)))
   {
   }
   else if(!(strnicmp (Response, "BUSY", 4)))
   {
   }
   else if(!(strnicmp (Response, "NO DIALTONE", 12)))
   {
   }
   else
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return(NOTNORMAL);
}


/* Routine to wait for a response or a timeout */
WaitForResponse (HANDLE PortHandle, PULONG ResponseSize, PCHAR Response, ULONG Timeout)
{
   ULONG i, j;
   ULONG BytesRead;

   i = 0;         // Tracks the Message offset.
   j = 0;         // Counts the read operations (seconds)

   /* set the timeout on read to 1 second */
// 071100 CGP  SetReadTimeOut (PortHandle, 1);

   while(j <= Timeout)
   {
      if(CTIRead (PortHandle, &Response[i], 1, &BytesRead) || BytesRead == 0)
      {
         j++;
         continue;
      }

      if(i == 0)
      {
         if(Response[i] == '\n' || Response[i] == '\r')
         {
            continue;
         }
      }

      /* Check what this is */
      if(Response[i] == '\r')
      {
         Response[i + 1] = '\0';
         *ResponseSize = i + 2;

         return(NORMAL);
      }

      i++;

      if(i + 2 > *ResponseSize)
      {
         return(NOTNORMAL);
      }
   }

   return(READTIMEOUT);
}



/* Routine to check DCD on a Port */
ModemDCD (HANDLE PortHandle)
{
   DWORD   eMask = 0;

   GetCommModemStatus(PortHandle, &eMask);

   return(eMask & MS_RLSD_ON);     // Yes, that is DCD or receive-line-signal detect.
}


/* Routine to check DSR on a port */
ModemDSR (HANDLE PortHandle)
{
   DWORD   eMask = 0;

   GetCommModemStatus(PortHandle, &eMask);

   return(eMask & MS_DSR_ON);
}


/* Routine to check CTS on a port */
ModemCTS (HANDLE PortHandle)
{
   DWORD   eMask = 0;

   GetCommModemStatus(PortHandle, &eMask);

   return(eMask & MS_CTS_ON);
}

void    DumpASCIIString(UCHAR* buffer, ULONG len)
{
   int i;
   printf("ASCII= <");
   for(i = 0; i < (INT)len; i++)
   {
      if(isgraph(buffer[i]))
         printf("%c ",buffer[i]);
      else
         printf("%02X ",buffer[i]);
   }
   printf(">\n");
}

void    ClearBuffer(UCHAR* buffer, ULONG len)
{
   memset(buffer, 0x00, len);
}


// known valid modem returns -- Note 0 is the same as OK when not in verbal mode
static PCHAR ModemResponseText[] = {"OK", "ERROR", "BUSY", "NO CARRIER",
   "NO DIALTONE", "NO ANSWER", "NO DIAL TONE", NULL};

/* returns TRUE if a valid modem return is found */
BOOL ValidModemResponse (PCHAR Response)
{
   BOOL isValid = FALSE;
   int count;

   for(count = 0; ModemResponseText[count] != NULL; count++)
   {
      if( strstr(Response, ModemResponseText[count]) != NULL )
      {
         // Valid modem return
         strcpy(Response, ModemResponseText[count]);
         isValid = TRUE;
         break;
      }
   }

   if( isValid != TRUE )
   {
      if(!strcmp(Response, "0"))      // Zero is a special case of OK
      {
         // Valid modem return
         strcpy(Response, "OK");
         isValid = TRUE;
      }
   }

   return(isValid);
}

/* Routine to wait for a response or a timeout */
WaitForResponse (HANDLE PortHandle, PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{

   ULONG   i , j;
   ULONG   BytesRead;
   INT     status = READTIMEOUT;

   i = 0;        // i represents the count of bytes in response.
   j = 0;

   if(PortHandle == (HANDLE)NULL)
   {
      return(NOTNORMAL);
   }

   /* Set the timeout on read to 1 second */
   // 012201 CGP This clears the buffer on WIN32.//  SetReadTimeOut (PortHandle, 1);

   while(j <= Timeout)
   {
      if(CTIRead (PortHandle, &Response[i], 1, &BytesRead) || BytesRead == 0) // Reads one byte into the buffer
      {
         j++;        // Fails once per second if no bytes read
         continue;
      }

      if(i == 0)                                            // Special case for the zeroeth byte
      {
         if(Response[i] == '\n' || Response[i] == '\r')    // Make sure it is not a CR or LF
         {
            continue;
         }
      }

      /* Check what this is */
      if(Response[i] == '\r')
      {
         // null out any CR LF
         Response[i] = '\0';

         // handle none verbal OK
         if(!(strcmp(ExpectedResponse, "OK")) && !(strcmp(Response, "0")))
         {
             // 0 is the same as OK for none verbal
             strcpy(Response,"OK");
         }

         // check for expected return
         if(ExpectedResponse == NULL || strstr(Response, ExpectedResponse) != NULL)
         {
            // it was the command we wanted or we did not want a specific response
            *ResponseSize = i;
            status = NORMAL;
            break; // the while
         }
         else if(ValidModemResponse(Response))
         {
            // this is valid (unexpected response), or we did not specify.
            *ResponseSize = strlen(Response);
            status = NORMAL;
            break; // the while
         }

         // look for new message
         Response[0] = '\0';
         i = 0;
      }
      else
      {
         i++;

         if(i + 2 > *ResponseSize)     // are we still within the size limit.
         {
            status = NOTNORMAL;
            break; // the while
         }
      }
   }

   #ifdef DEBUG3

   if(status != NORMAL)
   {
      if(i > 0)
      {
         printf("CheckPoint: %s (%d) ", __FILE__, __LINE__);
         for(j = 0; j < i+2; j++)
         {
            printf("%02x ", Response[i]);
         }
         printf("\n");
      }
   }
   #endif

   return status;
}




ModemHangup (HANDLE PortHandle, PCHAR Message, USHORT Port, USHORT Trace, BOOL dcdTest)
{
   INT status = NORMAL;
   CHAR Response[100];
   ULONG ResponseSize;
   ULONG BytesWritten;
   ULONG i;


   /* set the timeout on read to 1 second */
   SetReadTimeOut (PortHandle, 1);

   LowerModemRTS (PortHandle);
   LowerModemDTR (PortHandle);
   CTISleep ( 250L );
   RaiseModemDTR (PortHandle);
   RaiseModemRTS (PortHandle);

   /* Try five times to verify the modem's attention is ours. */
   for(i = 0; i < 5; i++)
   {
      // Kick it good!
      LowerModemDTR (PortHandle);
      CTISleep ( 500L );
      RaiseModemDTR (PortHandle);
      RaiseModemRTS (PortHandle);
      CTISleep ( 500L );

      /* Clear the buffer */
      PortInputFlush (PortHandle);
      ResponseSize = sizeof (Response);

      /* Wait to see if we get the no carrier message */
      if( !(WaitForResponse (PortHandle, &ResponseSize, Response, 1, NULL)) )      // See if we get something back from this guy.
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
         }
      }

      // Check for command mode by pegging the modem with an AT.
      CTIWrite (PortHandle, "AT\r", 3, &BytesWritten);
      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << Port << " Sent to Modem:  AT" << endl;
      }

      if(WaitForResponse (PortHandle, &ResponseSize, Response, 3, "OK"))
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Modem Response Timeout (this may be ok)" << endl;
         }

         // Maybe we are not in command mode...
         CTIWrite (PortHandle, "+++", 3, &BytesWritten);    // Escape to command mode please
         CTISleep ( 1500L );
         CTIWrite (PortHandle, "AT\r", 3, &BytesWritten);   // escape does not give response do AT

         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Sent to Modem:  +++" << endl;
         }
         if(WaitForResponse (PortHandle, &ResponseSize, Response, 3, "OK"))
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << Port << " Modem Response Timeout (will re-try)" << endl;
            }

            status = READTIMEOUT;
            continue;   // We are not getting anything here!
         }
         else if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;

            status = NORMAL;
            break;
         }
      }
      else
      {
         break;   //  We got an OK back on the "AT"
      }
   }

   PortInputFlush (PortHandle);

   if(status == NORMAL)
   {
      /* Attempt to hangup the modem (on hook) */
      for(i = 0; i < 5; i++)
      {
         CTISleep ( 250L);

         /* Send it the off hook message */
         CTIWrite (PortHandle, "ATH0\r", 5, &BytesWritten);

         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port << " Sent to Modem:  ATH0" << endl;
         }

         ResponseSize = sizeof (Response);

         if(WaitForResponse (PortHandle, &ResponseSize, Response, 3, "OK"))
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << Port << " Modem Response Timeout" << endl;
            }

            status = READTIMEOUT;     // Hangup Failed
            continue;
         }
         else
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << Port << " Received from Modem:  " << Response <<  endl;
            }

            status = NORMAL;
            break;   // Wow, we just went on hook!
         }
      }
   }

   if(status == NORMAL && dcdTest == TRUE)
   {
      /* Wait for carrier to drop */
      i = 0;
      while(i < 20)
      {
         if(!(ModemDCD (PortHandle)))
         {
            CTISleep ( 1500L );
            status = NORMAL;
            break;
         }

         CTISleep ( 100 );
         i++;
      }
   }


   if(status != NORMAL)
   {
      /* something is wrong so reset and setup the modem */
      ModemReset (PortHandle, Port, Trace, dcdTest);
      status = ModemSetup (PortHandle, Message, Port, Trace, dcdTest);
   }


   return status;
}
