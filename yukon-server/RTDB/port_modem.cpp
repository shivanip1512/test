
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   port_modem
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/port_modem.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:16 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
using namespace std;

#include "logger.h"
#include "port_modem.h"

BOOL CtiPortModem::connectedTo(const LONG devID)
{
   return (devID == getDialedUpDevice());
}

BOOL CtiPortModem::connectedTo(const ULONG crc)
{
   return (crc == getDialedUpDeviceCRC());
}

CtiPortModem::CtiPortModem(LONG id) :
   _shouldDisconnect(FALSE),
   _dialedUpCRC(0),
   _dialedUpDevice(id)
{}

CtiPortModem::CtiPortModem(const CtiPortModem& aRef)
{
   *this = aRef;
}

CtiPortModem::~CtiPortModem() {}

CtiPortModem& CtiPortModem::operator=(const CtiPortModem& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _dialedUpDevice = aRef.getDialedUpDevice();
      _dialedUpNumber = aRef.getDialedUpNumber();
      _dialedUpCRC    = aRef.getDialedUpDeviceCRC();
   }
   return *this;
}

BOOL&                CtiPortModem::getShouldDisconnect()                      { return _shouldDisconnect;}
BOOL                 CtiPortModem::shouldDisconnect() const                   { return _shouldDisconnect;}
CtiPortModem&        CtiPortModem::setShouldDisconnect(BOOL b)
{
   _shouldDisconnect = b;
   return *this;
}

LONG                 CtiPortModem::getDialedUpDevice() const              { return _dialedUpDevice;}
LONG&                CtiPortModem::getDialedUpDevice()                    { return _dialedUpDevice;}
CtiPortModem&        CtiPortModem::setDialedUpDevice(const LONG &i)
{
   _dialedUpDevice = i;
   return *this;
}

ULONG                CtiPortModem::getDialedUpDeviceCRC() const             { return _dialedUpCRC;}
ULONG&               CtiPortModem::getDialedUpDeviceCRC()                    { return _dialedUpCRC;}
CtiPortModem&        CtiPortModem::setDialedUpDeviceCRC(const ULONG &i)
{
   _dialedUpCRC = i;
   return *this;
}


RWCString            CtiPortModem::getDialedUpNumber() const              { return _dialedUpNumber;}
RWCString&           CtiPortModem::getDialedUpNumber()                    { return _dialedUpNumber;}
CtiPortModem&        CtiPortModem::setDialedUpNumber(const RWCString &str)
{
   _dialedUpNumber = str;
   return *this;
}



/* Routine to force the reset of modem */
INT CtiPortModem::modemReset(USHORT Trace, BOOL dcdTest)
{
   CHAR Response[100];
   ULONG ResponseSize;
   ULONG BytesWritten;
   ULONG i = 0;
   static ULONG tCount = 0;

   /* set the timeout on read to 1 second */
   SetReadTimeOut (getHandle(), 1);

   LowerModemRTS (getHandle());
   LowerModemDTR (getHandle());
   CTISleep ( 500L );
   RaiseModemDTR (getHandle());
   RaiseModemRTS (getHandle());
   CTISleep ( 500L );

   /* If we do not have CTS it is a problem */
   if(!(isCTS()))
   {
      if(!(++tCount % 300))
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << "  Port " << getName() << " No Modem CTS.  Modem may be off or configured wrong" << endl;
      }

      LowerModemDTR (getHandle());
      CTISleep ( 500L );
      RaiseModemDTR (getHandle());
      RaiseModemRTS (getHandle());
      CTISleep ( 500L );

      if(!(isCTS()))
      {
         return TIMEOUT;
      }
   }

   /* Try five times to intialize modem */
   for(i = 0; i < 5; i++)
   {

      /* Wait for CTS */
      if(!(isCTS()))
      {
         if(!(++tCount % 300))
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << getName() << " No Modem CTS..." << endl;
            }
         }

         LowerModemDTR (getHandle());
         CTISleep ( 500L );
         RaiseModemDTR (getHandle());
         RaiseModemRTS (getHandle());
         CTISleep ( 500L );

         CTISleep ( 100L );

         if(!(isCTS()))
         {
            return TIMEOUT;
         }
      }

      /* Clear the buffer */
      PortInputFlush (getHandle());
      ResponseSize = sizeof (Response);

      /* Wait to see if we get the no carrier message */
      if( !(WaitForResponse (getHandle(), &ResponseSize, Response, 1)) )      // See if we get something back from this guy.
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << getName() << " Received from Modem:  " << Response <<  endl;
         }
      }

      ResponseSize = sizeof (Response);
      // Make an attempt to determine if we are in the command mode
      CTIWrite (getHandle(), "AT\r", 3, &BytesWritten);    // Attention to the modem.
      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Sent to Modem:  AT" << endl;
      }

      /* Wait for a response or till we time out */
      if(WaitForResponse (getHandle(), &ResponseSize, Response, 3, "OK"))
      {
         // Just in case we are out to lunch here...
         CTIWrite (getHandle(), "+++", 3, &BytesWritten);    // Attention to the modem.

         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << getName() << " Sent to Modem:  +++" << endl;
         }

         ResponseSize = sizeof (Response);

         /* Wait for a response or till we time out */
         if(WaitForResponse (getHandle(), &ResponseSize, Response, 3, "OK"))
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Port " << getName() << " Modem Response Timeout" << endl;
            }

            CTIWrite (getHandle(), "\r", 1, &BytesWritten);    // Get rid of the trash that didn't give a good return.
         }

      }
      else if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Received from Modem:  " << Response <<  endl;
      }


      ResponseSize = sizeof (Response);
      /* Send it a reset message (Ask for an OK) */
      CTIWrite (getHandle(), "ATZ\r", 4, &BytesWritten);

      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Sent to Modem:  ATZ" << endl;
      }

      ResponseSize = sizeof (Response);

      /* Wait for a response or till we time out */
      if(WaitForResponse (getHandle(), &ResponseSize, Response, 4, "OK"))
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << getName() << " Modem Response Timeout" << endl;
         }
         return(TIMEOUT);
      }
      else if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Received from Modem:  " << Response <<  endl;
      }

      if(gLogPorts)
      {
         CtiLockGuard<CtiLogger> portlog_guard(_portLog);
         _portLog << RWTime() << " " << getName() << " modem reset" << endl;
      }

      /* Make sure that we got OK */
      if(!(strnicmp (Response, "OK", 2)))
      {
         return(NORMAL);
      }
   }

   return(!NORMAL);
}



/* Routine to send setup string(s) to the modem */
INT CtiPortModem::modemSetup(USHORT Trace, BOOL dcdTest)
{
   ULONG BytesWritten;
   CHAR Response[100];
   ULONG ResponseSize;
   ULONG i, j;

   // Try up to five times
   for(j = 0; j < 5; j++)
   {
      CTISleep (250L);

      /* Flush the input buffer */
      PortInputFlush (getHandle());

      if(CTIWrite (getHandle(), (char*)getModemInit().data(), strlen((char*)getModemInit().data()), &BytesWritten) || BytesWritten != strlen ((char*)getModemInit().data()))
      {
         return(!NORMAL);
      }

      /* Send the CR */
      CTIWrite (getHandle(), "\r", 1, &BytesWritten);

      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Sent to Modem:  " << getModemInit() << endl;
      }

      ResponseSize = sizeof (Response);

      /* Wait for a response or till we time out */
      if(WaitForResponse (getHandle(), &ResponseSize, Response, 3, "OK"))
      {
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << getName() << " Modem Response Timeout" << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         continue;
      }

      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Received from Modem:  " << Response << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }

      /* Make sure that we got OK */
      if(!(strnicmp (Response, "OK", 2)))
      {
         break;
      }
      else if(!(strnicmp (Response, "ERROR", 5)))
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << getName() << " Received from Modem:  " << Response << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  The modem init string \"" << getModemInit() << "\" was rejected." << endl;
         }
         return(DIALUPERROR);
      }
   }

   if(gLogPorts)
   {
      CtiLockGuard<CtiLogger> portlog_guard(_portLog);
      _portLog << RWTime() << " " << getName() << " modem setup " << (j >= 5 ? "failed" : "successful") << endl;
   }

   if(j >= 5)
   {
      return(!NORMAL);
   }

   return(NORMAL);
}

/* Routine to establish modem connection */
INT CtiPortModem::modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest)
{
   INT status = NORMAL;
   ULONG BytesWritten, i;
   CHAR MyMessage[100];
   CHAR Response[100];
   ULONG ResponseSize;

   /* set the timeout on read to 1 second */
   SetReadTimeOut (getHandle(), 1);

   if(Message[0] == 'A' || Message[0] == 'a')
   {
      strcpy (MyMessage, Message);
   }
   else
   {
      strcpy (MyMessage, "ATDT");
      strcat (MyMessage, Message);
   }

   /* Send the CR */
   strcat (MyMessage, "\r");

   CTISleep ( 250L);

   /* Flush the input buffer */
   PortInputFlush (getHandle());

   /* Perform the dial */
   if(CTIWrite (getHandle(), MyMessage, strlen(MyMessage), &BytesWritten) || BytesWritten != strlen(MyMessage))
   {
      return(!NORMAL);
   }

   if(Trace)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Port " << getName() << " Sent to Modem:  " << MyMessage << endl;
   }

   ResponseSize = sizeof (Response);

   /* Wait for a response from the modem */
   if(WaitForResponse(getHandle(), &ResponseSize, Response, ModemConnectionTimeout, "CONNECT"))
   {
      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Modem Response Timeout" << endl;
      }
      status = TIMEOUT;
   }
   else
   {
      if(Trace)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port " << getName() << " Received from Modem:  " << Response <<  endl;
      }

      /* We have a response so see what it is */
      if(!(strnicmp (Response, "CONNECT", 7)))
      {
         /* Wait up to 2 seconds for CD */
         if(dcdTest == TRUE)
         {
            for(i = 0; i < 20; i++)
            {
               if(isDCD())
               {
                  /* Raise RTS */
                  RaiseModemRTS (getHandle());
                  CTISleep ( 200L);       // CTISleep ( 2000L );
                  PortInputFlush (getHandle());

                  status = NORMAL;
               }
               else
               {
                  CTISleep (100L);
               }
            }
         }
         else     // Proceed, ignoring DCD
         {
            /* Raise RTS */
            RaiseModemRTS (getHandle());
            CTISleep ( 200L);       // CTISleep ( 2000L );
            PortInputFlush (getHandle());
            status = NORMAL;
         }
      }
      else if(!(strnicmp (Response, "NO CARRIER", 10)))
      {
         status = !NORMAL;
      }
      else if(!(strnicmp (Response, "ERROR", 5)))
      {
         status = !NORMAL;
      }
      else if(!(strnicmp (Response, "NO DIAL TONE", 12)))
      {
         status = !NORMAL;
      }
      else if(!(strnicmp (Response, "NO ANSWER", 9)))
      {
         status = !NORMAL;
      }
      else if(!(strnicmp (Response, "BUSY", 4)))
      {
         status = !NORMAL;
      }
      else if(!(strnicmp (Response, "NO DIALTONE", 12)))
      {
         status = !NORMAL;
      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Modem responded " << Response << endl;
      }
   }

   if(gLogPorts)
   {
      CtiLockGuard<CtiLogger> portlog_guard(_portLog);
      _portLog << RWTime() << " " << getName() << " modem connect to " << Message << ".  Modem responded with " << Response << endl;
   }

   return(status);
}


INT CtiPortModem::modemHangup(USHORT Trace, BOOL dcdTest)
{
   INT status = NORMAL;
   CHAR Response[100];
   ULONG ResponseSize;
   ULONG BytesWritten;
   ULONG i;

   if(getHandle() != NULL)
   {
      /* set the timeout on read to 1 second */
      SetReadTimeOut (getHandle(), 1);

      LowerModemRTS (getHandle());
      LowerModemDTR (getHandle());
      CTISleep ( 250L );
      RaiseModemDTR (getHandle());
      RaiseModemRTS (getHandle());

      /* Try five times to verify the modem's attention is ours. */
      for(i = 0; i < 5; i++)
      {
         // Kick it good!
         LowerModemDTR (getHandle());
         CTISleep ( 500L );
         RaiseModemDTR (getHandle());
         RaiseModemRTS (getHandle());
         CTISleep ( 500L );

         /* Clear the buffer */
         PortInputFlush (getHandle());
         ResponseSize = sizeof (Response);

         /* Wait to see if we get the no carrier message */
         if( !(WaitForResponse (getHandle(), &ResponseSize, Response, 1, NULL)) )      // See if we get something back from this guy.
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " " << getName()  << " Received from Modem:  " << Response <<  endl;
            }
         }

         // Check for command mode by pegging the modem with an AT.
         CTIWrite (getHandle(), "AT\r", 3, &BytesWritten);
         if(Trace)
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getName()  << " Sent to Modem:  AT" << endl;
         }

         if(WaitForResponse (getHandle(), &ResponseSize, Response, 3, "OK"))
         {
            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " " << getName()  << " Modem Response Timeout (this may be ok)" << endl;
            }

            // Maybe we are not in command mode...
            CTIWrite (getHandle(), "+++", 3, &BytesWritten);    // Escape to command mode please
            CTISleep ( 1500L );
            CTIWrite (getHandle(), "AT\r", 3, &BytesWritten);   // escape does not give response do AT

            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " " << getName()  << " Sent to Modem:  +++" << endl;
            }
            if(WaitForResponse (getHandle(), &ResponseSize, Response, 3, "OK"))
            {
               if(Trace)
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " " << getName()  << " Modem Response Timeout (will re-try)" << endl;
               }

               status = TIMEOUT;
               continue;   // We are not getting anything here!
            }
            else if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " " << getName()  << " Received from Modem:  " << Response <<  endl;

               status = NORMAL;
               break;
            }
         }
         else
         {
            break;   //  We got an OK back on the "AT"
         }
      }

      PortInputFlush (getHandle());

      if(status == NORMAL)
      {
         /* Attempt to hangup the modem (on hook) */
         for(i = 0; i < 5; i++)
         {
            CTISleep ( 250L);

            /* Send it the off hook message */
            CTIWrite (getHandle(), "ATH0\r", 5, &BytesWritten);

            if(Trace)
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " " << getName()  << " Sent to Modem:  ATH0" << endl;
            }

            ResponseSize = sizeof (Response);

            if(WaitForResponse (getHandle(), &ResponseSize, Response, 3, "OK"))
            {
               if(Trace)
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " " << getName()  << " Modem Response Timeout" << endl;
               }

               status = TIMEOUT;     // Hangup Failed
               continue;
            }
            else
            {
               if(Trace)
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " " << getName()  << " Received from Modem:  " << Response <<  endl;
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
            if(!(ModemDCD (getHandle())))
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
         modemReset(Trace, dcdTest);
         status = modemSetup(Trace, dcdTest);
      }
   }
   else
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }

   if(gLogPorts)
   {
      CtiLockGuard<CtiLogger> portlog_guard(_portLog);
      _portLog << RWTime() << " " << getName() << " modem hangup on " << (getDialedUpNumber().isNull ? "unknown phone number" : getDialedUpNumber()) << endl;
   }

   setDialedUpDeviceCRC(-1);
   setDialedUpDevice(0);
   setDialedUpNumber(RWCString());

   return status;
}
