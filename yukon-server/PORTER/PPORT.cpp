#pragma title ( "Porter Low Level Port Routines" )
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PPORT
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PPORT.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
p
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PPORT.C

    Purpose:
        To handle the actual handshaking and initialization of serial ports
        for porter.

    The following procedures are contained in this module:
        OutMess                     InMess
        InitPort                    RaiseRTS
        LowerRTS                    RaiseDTR
        LowerDTR                    InClear
        OutClear                    DCDTest

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        8-19-93     Fixed timing bug from hell in OutMess           WRO
        9-7-93      Converted to 32 bit                             WRO
        9-30-93     Converted to use portsup subroutines            WRO
        10-1-93     Moved MilliTime to portsup.c                    WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include <iostream>
using namespace std;

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/* TCP/IP Includes */
#include <winsock.h>


#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portsup.h"
#include "tcpsup.h"

#include "portglob.h"
#include "port_base.h"
#include "dev_base.h"

#include "logger.h"
#include "guard.h"

extern UINT getIPInitFlags(CtiPort *PortRecord);

/* routine to output a message to the serial port */
OutMess (PBYTE       Message,        /* message to be sent out */
         ULONG        Length,         /* Length of message */
         CtiPort     *PortRecord,    /* Port Info */
         CtiDeviceBase   *RemoteRecord)  /* CCU Info */
{
   ULONG Written, i;
   ULONG MSecs, ByteCount;
   ULONG StartWrite, ReturnWrite;

   if(Length > 4096)
   {
      cerr << "Danger Will Robinson" << endl;
      cerr << " About to attempt an OutMess of " << Length << " bytes" << endl;

      Length = 100;
      Sleep(10000);
   }

   /* Check if we need to do a trace */
   if(
     (TraceFlag || TracePort == PortRecord->getPortID() ||
      (TraceRemote == RemoteRecord->getAddress()))      &&
     !TraceErrorsOnly
     )
   {
      TraceOut (Message,
                (USHORT)Length,
                PortRecord->getPortID(),
                RemoteRecord->getAddress());
   }

   /* If terminal server make sure we are hooked up */
   if(PortRecord->isTCPIPPort())
   {
      /* Make sure memory is allocated for this */
      if(NetCXPortInfo[PortRecord->getPortID()] == NULL)
      {
         if((NetCXPortInfo[PortRecord->getPortID()] = (NETCXPORTINFO*)malloc (sizeof (NETCXPORTINFO))) == NULL)
         {
            return(MEMORY);
         }

         // Sanity by CGP
         memset(NetCXPortInfo[PortRecord->getPortID()], 0, sizeof(NETCXPORTINFO));

         /* We can't be open yet so flag accordingly */
         NetCXPortInfo[PortRecord->getPortID()]->Open = FALSE;
         NetCXPortInfo[PortRecord->getPortID()]->Connected = FALSE;
         NetCXPortInfo[PortRecord->getPortID()]->MyThreadID = -1;
         NetCXPortInfo[PortRecord->getPortID()]->NetCXAltPin = NetCXAltPin;

         /* Create the mutex semaphore to protect the resources */
         CTICreateMutexSem (NULL,
                            &NetCXPortInfo[PortRecord->getPortID()]->PortLockSem,
                            0,
                            0);
      }

      if(NetCXPortInfo[PortRecord->getPortID()]->Connected == FALSE)
      {
         /* We not connected so try to make the connection */

         if((i = TCPInit (NetCXPortInfo[PortRecord->getPortID()],
                          PortRecord,
                          getIPInitFlags(PortRecord))) != NORMAL)
         {
            //CtiLockGuard<CtiLogger> doubt_guard(dout);
            // dout << __FILE__ << " (" << __LINE__ << ")" << endl;
            return(i);
         }
      }
   }

   /* Wait for DCD to dissapear */
#ifdef OLD_WAY
   if(RemoteRecord->useRadioDelays())
   {
      i = 0;
      while(i++ < PortRecord->getDCDSense() && DCDTest (PortRecord) )
      {
         /* We have DCD... Wait 1/20 second and try again */
         CTISleep (50L);
      }
   }
#else
   i = 0;
   while(i++ < PortRecord->getDCDSense() && DCDTest (PortRecord) )
   {
      /* We have DCD... Wait 1/20 second and try again */
      // CtiLockGuard<CtiLogger> doubt_guard(dout);
      // dout << __FILE__ << " (" << __LINE__ << ")" << endl;
      CTISleep (50L);
   }
#endif
   /* Check if we need to key */

   /* Pre Key Delay */
   if(PortRecord->getDelay(PRE_RTS_DELAY))
   {
      CTISleep ((ULONG) PortRecord->getDelay(PRE_RTS_DELAY));
   }

   /* Key the radio */
   RaiseRTS (PortRecord);
   /* get the present time */
   MilliTime (&MSecs);

   if(PortRecord->getDelay(RTS_TO_DATA_OUT_DELAY))
   {
      CTISleep ((ULONG) PortRecord->getDelay(RTS_TO_DATA_OUT_DELAY));
   }


   /* Clear the Buffers */
   OutClear (PortRecord);
   InClear (PortRecord);

   /* Remember when we started writing */
   MilliTime (&StartWrite);

   if(PortRecord->isTCPIPPort())
   {
      /* Terminal server so send that route */
      /* A NET C/X terminal server will not return till all bytes are delivered */
      /* Regular TS returns immediatly which should be fine... */
      if((i = TCPSend (NetCXPortInfo[PortRecord->getPortID()],
                       Message,
                       Length)) != NORMAL)
      {
         // CtiLockGuard<CtiLogger> doubt_guard(dout);
         // dout << __FILE__ << " (" << __LINE__ << ")" << endl;
         return(i);
      }
   }
   else
   {
      if(CTIWrite (PortHandle[PortRecord->getPortID()],
                   Message,
                   Length,
                   &Written) || Written != Length)
      {
         CTIClose (PortHandle[PortRecord->getPortID()]);
         PortHandle[PortRecord->getPortID()] = (HFILE) NULL;
         return(PORTWRITE);
      }


      /* if software queue is not empty wait for it to be */
      while((ByteCount = GetPortOutputQueueCount (PortHandle[PortRecord->getPortID()])) != 0)
      {
         CTISleep ((10000L * ByteCount) / (ULONG) PortRecord->getBaudRate());
      }

      /* Now outwait the hardware queue if neccessary */
      if(Length > 2)
      {
         MilliTime (&ReturnWrite);
         if(ReturnWrite < (StartWrite + (((ULONG) (Length - 2) * 10000L) / (ULONG) PortRecord->getBaudRate())))
         {
            CTISleep (StartWrite + (((ULONG) (Length - 2) * 10000L) / (ULONG) PortRecord->getBaudRate()) - ReturnWrite);
         }
      }
   }

   /* Time to do the RTS thing */
#ifdef OLD_WAY
   if(RemoteRecord->useRadioDelays())
   {
      /* I don't want to give up timeslice if I don't have to... 12/9 CGP */
      if(PortRecord->getDelay(DATA_OUT_TO_RTS_DOWN_DELAY))
         CTISleep ((ULONG) PortRecord->getDelay(DATA_OUT_TO_RTS_DOWN_DELAY));

      LowerRTS (PortRecord);
   }
   else if(PortRecord->getDCDSense())
   {
      InClear (PortRecord);
   }
#else
   /* I don't want to give up timeslice if I don't have to... 12/9 CGP */
   if(PortRecord->getDelay(DATA_OUT_TO_RTS_DOWN_DELAY))
      CTISleep ((ULONG) PortRecord->getDelay(DATA_OUT_TO_RTS_DOWN_DELAY));

   LowerRTS (PortRecord);
#endif

   MilliTime (&MilliLast[PortRecord->getPortID()]);

   if(RemoteRecord->getAddress() == RTUGLOBAL ||
      RemoteRecord->getAddress() == CCUGLOBAL)
   {
      CTISleep ((ULONG) RemoteRecord->getPostDelay());
   }

   return(NORMAL);
}


/* routine to read a the input from a port */
INT InMess  (BYTE          *Message,         /* receives the result of the read */
             ULONG         Length,           /* number of characters to read */
             CtiPort       *PortRecord,      /* port information */
             CtiDeviceBase *RemoteRecord,    /* ccu information */
             UINT          TimeOut,          /* time after which message is considered tardy */
             PULONG        Count,            /* count of number of characters read */
             INT           SecondRead,       /* Flag to indicate this is continuation */
             INT           Trace)            /* Flag to indicate if we should do trace here */

{
   ULONG i = NORMAL;
   ULONG DCDCount = 0;
   ULONG SomeRead = 0;
   BYTE SomeMessage[300];


   /* If Length is 0 just return */
   if(Length == 0)
   {
      *Count = 0;
      return(NORMAL);
   }

   /* Check to see if this is through a terminal server */
   if((PortRecord->isTCPIPPort()))
   {
      if(NetCXPortInfo[PortRecord->getPortID()]->Connected == FALSE)
      {
         *Count = 0;
         i = TCPREADERROR;
      }
      else
      {
         if(SecondRead == 0 || SecondRead == 2)
         {
            if(PortRecord->getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY)) // CGP 12/9
            {
               /*
                *  Done in DE to keep the thing from clearing bytes
                */
               CTISleep ((ULONG) PortRecord->getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));
               InClear (PortRecord);
            }

            if(PortRecord->getDCDSense())
            {
               /* Check if we have DCD */
               while(!(DCDTest(PortRecord)) && DCDCount++ < PortRecord->getDCDSense())
               {
                  /* We do not have DCD... Wait 1/20 second and try again */
                  CTISleep (50L);
               }

               if(!(DCDTest(PortRecord)))
               {
                  *Count = 0;
                  i = NODCD;
               }
            }
         }

         if(!i)
         {
            /* If neccesary wait for IDLC flag character */
            if(PortRecord->getProtocol() == ProtocolWrapIDLC && (SecondRead == 0 || SecondRead == 2))
            {
               do
               {
                  /* Read in the number of bytes requested */
                  i = TCPReceive (NetCXPortInfo[PortRecord->getPortID()],
                                  Message,
                                  1,
                                  TimeOut + ExtraTimeOut,
                                  (PLONG) Count);
                  /* Note we really can't kill the socket on receive but if it is failed next write will get it */
                  if(i)
                  {
                     if((LONG) (*Count) < 0)
                     {
                        *Count = 0;
                        break;
                     }
                  }

                  if(Message[0] == 0x7e)
                  {
                     break;
                  }
               } while(!i);

               if(!i)
               {
                  i = TCPReceive (NetCXPortInfo[PortRecord->getPortID()],
                                  (&Message[1]),
                                  Length - 1,
                                  TimeOut + ExtraTimeOut,
                                  (PLONG) Count);
                  /* Note we really can't kill the socket on receive but if it is failed next write will get it */
                  if(i)
                  {
                     if((LONG) (*Count) < 0)
                     {
                        *Count = 0;
                     }
                     else
                     {
                        *Count += 1;
                     }
                  }
                  else
                  {
                     *Count += 1;
                  }
               }
            }
            else
            {
               /* Read in the number of bytes requested */
               i = TCPReceive (NetCXPortInfo[PortRecord->getPortID()],
                               Message,
                               Length,
                               TimeOut + ExtraTimeOut,
                               (PLONG) Count);
               /* Note we really can't kill the socket on receive but if it is failed next write will get it */
               if(i)
               {
                  if((LONG) (*Count) < 0)
                  {
                     *Count = 0;
                  }
                  fprintf(stderr,"%s: (%d): \n",__FILE__, __LINE__);
               }
            }
         }
      }
   }
   else
   {
      if(SecondRead == 0 || SecondRead == 2)
      {
         if(PortRecord->getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY))
         {
            CTISleep ((ULONG) PortRecord->getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));
            InClear (PortRecord);
         }

         if(PortRecord->getDCDSense())
         {
            /* Check if we have DCD */
            while(!(DCDTest(PortRecord)) && DCDCount++ < PortRecord->getDCDSense())
            {
               /* We do not have DCD... Wait 1/20 second and try again */
               CTISleep (50L);
            }

            if(!(DCDTest(PortRecord)))
            {
               *Count = 0;
               i = NODCD;
            }
         }
      }


      /* Make sure that any errors on the port are cleared */
#ifdef OLD_WAY    // CGP 091099 - I don't have a GetPortCommEvent replicat, so just clear them.
      if(GetPortCommEvent (PortHandle[PortRecord->getPortID()]) & ERROR_OCCURRED)
      {
         GetPortCommError (PortHandle[PortRecord->getPortID()]);
      }
#else
      GetPortCommError (PortHandle[PortRecord->getPortID()]);
#endif
      if(!i)
      {
         /* set the read timeout */
         #if 0
         if((TimeOut + ExtraTimeOut) <= 655)
         {
            SetReadTimeOut (PortHandle[PortRecord->getPortID()], (USHORT)(TimeOut + ExtraTimeOut));
         }
         else
         {
            SetReadTimeOut (PortHandle[PortRecord->getPortID()], 65535);
         }
         #else
         SetReadTimeOut (PortHandle[PortRecord->getPortID()], (USHORT)(TimeOut + ExtraTimeOut));
         #endif

         /* If neccesary wait for IDLC flag character */
         if(PortRecord->getProtocol() == ProtocolWrapIDLC &&
            (SecondRead == 0 || SecondRead == 2))
         {
            do
            {
               if((i = CTIRead (PortHandle[PortRecord->getPortID()],
                                Message,
                                1,
                                Count)) || *Count != 1)
               {
                  if(i == ERROR_INVALID_HANDLE)
                  {
                     CTIClose (PortHandle[PortRecord->getPortID()]);
                     PortHandle[PortRecord->getPortID()] = (HFILE) NULL;
                     i = PORTREAD;
                  }
                  else if(i)
                  {
                     i = READERR;
                  }
                  else
                  {
                     if(SomeRead)
                     {
                        i = FRAMEERR;
                     }
                     else
                     {
                        i = READTIMEOUT;
                     }
                  }
                  break;
               }

               /* Make sure that any errors on the port are cleared */
#ifdef OLD_WAY    // CGP 091099 - I don't have a GetPortCommEvent replicat, so just clear them.

               if(GetPortCommEvent (PortHandle[PortRecord->getPortID()]) & ERROR_OCCURRED)
               {
                  GetPortCommError (PortHandle[PortRecord->getPortID()]);
               }
#else
               GetPortCommError (PortHandle[PortRecord->getPortID()]);
#endif

               SomeMessage[SomeRead] = Message[0];
               SomeRead++;
            }  while(Message[0] != 0x7e && Message[0] != 0xfc && SomeRead < sizeof (SomeMessage));

            if(i && SomeRead)
            {
               memcpy (Message, SomeMessage, SomeRead);
               *Count = SomeRead;
            }

            if(!i)
            {
               if(PortRecord->getProtocol() == ProtocolWrapIDLC && Message[0] == 0xfc)
               {
                  Message[0] = 0x7e;
#ifdef DEBUG3
                  printf ("0xfc replaced with 0x7e\n");
#endif
               }
               if((i = CTIRead (PortHandle[PortRecord->getPortID()],
                                Message + 1,
                                Length - 1,
                                Count)) != NORMAL)
               {

                  if(i == ERROR_INVALID_HANDLE)
                  {
                     CTIClose (PortHandle[PortRecord->getPortID()]);
                     PortHandle[PortRecord->getPortID()] = (HFILE) NULL;
                     i = PORTREAD;
                  }
                  else if(i)
                  {
                     i = READERR;
                  }
                  else
                  {
                     i = READTIMEOUT;
                  }
               }

               (*Count)++;
            }
         }
         else
         {
            if((i = CTIRead (PortHandle[PortRecord->getPortID()],
                             Message,
                             Length,
                             Count)) != NORMAL)
            {

               LPVOID lpMsgBuf;
               FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
                              NULL,
                              GetLastError(),
                              MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                              (LPTSTR) &lpMsgBuf,
                              0,
                              NULL );// Process any inserts in lpMsgBuf.

               // ...// Display the string.
               MessageBox( NULL, (LPCTSTR)lpMsgBuf, "Error", MB_OK | MB_ICONINFORMATION );
               // Free the buffer.
               LocalFree( lpMsgBuf );

               if(i == ERROR_INVALID_HANDLE)
               {
                  CTIClose (PortHandle[PortRecord->getPortID()]);
                  PortHandle[PortRecord->getPortID()] = (HFILE) NULL;
               }

               i = PORTREAD;
            }
         }
      }

      if(!i)
      {
         if(*Count != Length)
         {
            if((i = CTIRead (PortHandle[PortRecord->getPortID()],
                             Message + *Count,
                             Length - *Count,
                             Count)) != NORMAL)
            {

               LPVOID lpMsgBuf;
               FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
                              NULL,
                              GetLastError(),
                              MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                              (LPTSTR) &lpMsgBuf,
                              0,
                              NULL );// Process any inserts in lpMsgBuf.

               // ...// Display the string.
               MessageBox( NULL, (LPCTSTR)lpMsgBuf, "Error", MB_OK | MB_ICONINFORMATION );
               // Free the buffer.
               LocalFree( lpMsgBuf );

               if(i == ERROR_INVALID_HANDLE)
               {
                  CTIClose (PortHandle[PortRecord->getPortID()]);
                  PortHandle[PortRecord->getPortID()] = (HFILE) NULL;
               }

               i = PORTREAD;
            }

            //i = READTIMEOUT;
         }
      }
   }

   if(Trace && (TraceFlag || TracePort == PortRecord->getPortID() || TraceRemote == RemoteRecord->getAddress()))
   {
      TraceIn (Message,
               (USHORT)*Count,
               PortRecord->getPortID(),
               RemoteRecord->getAddress(),
               (USHORT)i);
   }

   /* Do the Extraordinay delay irreguardless of radio */
   if(SecondRead == 1 || SecondRead == 2)
   {
      CTISleep ((ULONG) RemoteRecord->getPostDelay());
   }

   MilliTime (&MilliLast[PortRecord->getPortID()]);

   return(i);
}



/* routine to inititialize port for use with emetcon */
InitPort (CtiPort *PortRecord)
{
   ULONG Result, i;
   CHAR Name[sizeof (PortRecord->getPhysicalPort()) + 1];

   /* Check to see if this is through a terminal server */
   if(PortRecord->isTCPIPPort())
   {
      /* This is where we do the TCP/IP Initialization stuff... */

      // FIX FIX FIX

   }
   else
   {
      /* check to be sure port is in proper range */
      if(PortRecord->getPortID() > MAXPORT)
         return(BADPARAM);


#ifdef OLD_WAY
      memcpy (Name, PortRecord->getPhysicalPort(), sizeof (PortRecord->getPhysicalPort()));

      /* Assume port name is less than 20 characters */
      for(i = sizeof (PortRecord->getPhysicalPort()) - 1; i > 0; i--)
      {
         if(PortRecord->getPhysicalPort()(i - 1) != ' ')
         {
            Name[i] = '\0';
            break;
         }
      }
#endif
      strcpy(Name, PortRecord->getPhysicalPort());

      /* open the port saving the handle for future reference */
      if(CTIOpen (Name,
                  &PortHandle[PortRecord->getPortID()],
                  &Result,
                  0L,
                  FILE_NORMAL,
                  FILE_OPEN,
                  OPEN_ACCESS_READWRITE | OPEN_SHARE_DENYREADWRITE
                  | OPEN_FLAGS_WRITE_THROUGH,
                  0L) || Result != 1)
      {

         return(SYSTEM);
      }

      /* set the baud rate on the port */
      if((i = BaudRate (PortRecord)) != NORMAL)
      {
         return(i);
      }

      /* set the line characteristics for this port */
      if((i = SetLineMode (PortHandle[PortRecord->getPortID()])) != NORMAL)
      {
         return(i);
      }

      /* set the default DCB info for the port */
      if((i = SetDefaultDCB (PortHandle[PortRecord->getPortID()])) != NORMAL)
      {
         return(i);
      }

      /* set the Read Timeout for the port */
      if((i = SetReadTimeOut (PortHandle[PortRecord->getPortID()],
                              TIMEOUT)) != NORMAL)
      {
         return(i);
      }

      /* set the write timeout for the port */
      if((i = SetWriteTimeOut (PortHandle[PortRecord->getPortID()],
                               TIMEOUT)) != NORMAL)
      {
         return(i);
      }

      /* Lower RTS */
      LowerRTS (PortRecord);

      /* Raise DTR */
      RaiseDTR (PortRecord);
   }

   /* that should do it for this port */
   return(NORMAL);
}


/* Routine to set the baud rate on a port */
BaudRate (CtiPort *PortRecord)
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPsetBaudRate (NetCXPortInfo[PortRecord->getPortID()],
                             PortRecord->getBaudRate()));
   }
   else
   {
      return(SetBaudRate (PortHandle[PortRecord->getPortID()],
                          PortRecord->getBaudRate()));
   }
}


/* Port interface to modem reset routine */
PortModemReset (CtiPort *PortRecord,
                REMOTE *RemoteRecord,
                DEVICE *DeviceRecord)
{
   return(ModemReset (PortHandle[PortRecord->getPortID()],
                      PortRecord->getPortID(),
                      TraceFlag));
}


/* Port interface to modem hangup routine */
PortModemHangup (CtiPort *PortRecord,
                 REMOTE *RemoteRecord,
                 DEVICE *DeviceRecord,
                 PCHAR InitString)
{
   return(ModemHangup (PortHandle[PortRecord->getPortID()],
                       InitString,
                       PortRecord->getPortID(),
                       TraceFlag));
}

/* Port interface to modem init routine */
int PortModemSetup (CtiPort    *PortRecord,
                CtiDeviceBase  *RemoteRecord,
                DEVICE     *DeviceRecord,
                PCHAR      InitString)
{
   return(ModemSetup (PortHandle[PortRecord->getPortID()],
                      InitString,
                      RemoteRecord->getPortID(),
                      TraceFlag));
}


/* Port interface to modem connect routine */
int PortModemConnect (CtiPort     *PortRecord,
                      CtiDeviceBase   *RemoteRecord,
                      DEVICE      *DeviceRecord,
                      PCHAR       ConnectString)
{
   return(ModemConnect (PortHandle[PortRecord->getPortID()],
                        ConnectString,
                        PortRecord->getPortID(),
                        TraceFlag));
}


/* routine to raise rts */
RaiseRTS (CtiPort *PortRecord)      /* port number 0 - ... */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPRaiseRTS (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(RaiseModemRTS (PortHandle[PortRecord->getPortID()]));
   }
}


/* routine to lower RTS */
LowerRTS (CtiPort* PortRecord)      /* port number 0 - ... */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPLowerRTS (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(LowerModemRTS (PortHandle[PortRecord->getPortID()]));
   }
}


/* routine to raise DTR */
RaiseDTR (CtiPort *PortRecord)      /* port number 0 - ... */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPRaiseDTR (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(RaiseModemDTR (PortHandle[PortRecord->getPortID()]));
   }
}


/* routine to lower DTR */



LowerDTR (CtiPort *PortRecord)      /* port number 0 - ... */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPLowerDTR (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(LowerModemDTR (PortHandle[PortRecord->getPortID()]));
   }
}


/* routine to flush the input buffer on the port */
InClear (CtiPort *PortRecord)   /* port number 0 - ... */
{
   /* Check to see if this is through a terminal server */
   if(PortRecord->isTCPIPPort())
   {
      return(TCPInputFlush (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(PortInputFlush (PortHandle[PortRecord->getPortID()]));
   }
}


/* routine to flush the output buffer on the port */
OutClear (CtiPort *PortRecord)          /* port number */
{
   /* Check to see if this is through a terminal server */
   if(PortRecord->isTCPIPPort())
   {
      return(TCPOutputFlush (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(PortOutputFlush (PortHandle[PortRecord->getPortID()]));
   }
}


/* Routine to check status of DCD */
/* Returns 0 if DCD clear for port, !0 if it is set */
DCDTest (CtiPort *PortRecord)        /* Port to check */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPDCDTest (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(ModemDCD (PortHandle[PortRecord->getPortID()]));
   }
}


/* Routine to check status of CTS */
/* Returns 0 if CTS clear for port, !0 if it is set */
CTSTest (CtiPort *PortRecord)        /* Port to check */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPCTSTest (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(ModemCTS (PortHandle[PortRecord->getPortID()]));
   }
}


/* Routine to check status of DSR */
/* Returns 0 if DSR clear for port, !0 if it is set */
DSRTest (CtiPort *PortRecord)        /* Port to check */
{
   if(PortRecord->isTCPIPPort())
   {
      return(TCPDSRTest (NetCXPortInfo[PortRecord->getPortID()]));
   }
   else
   {
      return(ModemDSR (PortHandle[PortRecord->getPortID()]));
   }
}


/* Routine to do analog loopback */
AnalogLoop (OUTMESS     *OutMessage,
            CtiPort     *PortRecord,
            CtiDeviceBase   *RemoteRecord)
{
   INMESS InMessage;
   ULONG Count;
   ULONG BytesWritten;

   /* Make the call to outmess */
   if(OutMess (OutMessage->Buffer.OutMessage,
               (USHORT)OutMessage->OutLength,
               PortRecord,
               RemoteRecord))
   {
      /* We had an error */
      // CtiLockGuard<CtiLogger> doubt_guard(dout);
      // dout << __FILE__ << " " << __LINE__ << endl;
      SendError (OutMessage, PORTWRITE);
   }

   /* Load up the appropriate parts of InMessage */
   InMessage.Remote        = OutMessage->Remote;
   InMessage.Port          = OutMessage->Port;
   InMessage.Sequence      = OutMessage->Sequence;
   InMessage.ReturnNexus   = OutMessage->ReturnNexus;
   InMessage.SaveNexus     = OutMessage->SaveNexus;
   InMessage.Priority      = OutMessage->Priority;

   /* Go get what we can */

   InMessage.EventCode = InMess  (InMessage.Buffer.InMessage,
                                  (USHORT)OutMessage->OutLength,
                                  PortRecord,
                                  RemoteRecord,
                                  OutMessage->TimeOut,
                                  &Count,
                                  3,
                                  FALSE);

   InMessage.InLength = Count;

   /* And the Trace Please */
   if(TraceFlag)
   {
      TraceIn (InMessage.Buffer.InMessage,
               (USHORT)Count,
               PortRecord->getPortID(),
               RemoteRecord->getAddress(),
               InMessage.EventCode);
   }

   /* Ship back to calling process if neccessary */
   if(OutMessage->EventCode & RESULT)
   {
      /* send message back to originating process */
      if(OutMessage->ReturnNexus.NexusState != CTINEXUS_STATE_NULL)
      {
         if(CTINexusWrite (&OutMessage->ReturnNexus,
                           &InMessage,
                           sizeof (InMessage),
                           &BytesWritten,
                           0L))
         {
            printf ("Error Writing to Return Pipe\n");
            delete (OutMessage);
            return(PIPEWRITE);
         }
      }
   }

   delete (OutMessage);
   return(PIPEWRITE);
}
