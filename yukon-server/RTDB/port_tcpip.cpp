
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   port_tcpip
*
* Date:   5/9/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/port_tcpip.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/06/06 19:55:11 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
using namespace std;

#include "logger.h"
#include "port_tcpip.h"
#include "portsup.h"
#include "yukon.h"

CtiPortTCPIPDirect::CtiPortTCPIPDirect() :
   _socket(INVALID_SOCKET),
   _open(false),
   _connected(false),
   _failed(false),
   _busy(false),
   _baud(0)
{}


CtiPortTCPIPDirect::CtiPortTCPIPDirect(const CtiPortTCPIPDirect& aRef)
{
   *this = aRef;
}

CtiPortTCPIPDirect::~CtiPortTCPIPDirect() {}

CtiPortTCPIPDirect& CtiPortTCPIPDirect::operator=(const CtiPortTCPIPDirect& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _tcpIpInfo = aRef.getTcpIpInfo();
   }
   return *this;
}


CtiTablePortTCPIP CtiPortTCPIPDirect::getTcpIpInfo() const
{
   return _tcpIpInfo;
}

CtiTablePortTCPIP& CtiPortTCPIPDirect::getTcpIpInfo()
{
   return _tcpIpInfo;
}

CtiPortTCPIPDirect& CtiPortTCPIPDirect::setTcpIpInfo(const CtiTablePortTCPIP& tcpipinfo)
{
   _tcpIpInfo = tcpipinfo;
   return *this;
}


INT CtiPortTCPIPDirect::getIPPort() const
{
   return _tcpIpInfo.getIPPort();
}

INT& CtiPortTCPIPDirect::getIPPort()
{
   return _tcpIpInfo.getIPPort();
}

RWCString CtiPortTCPIPDirect::getIPAddress() const
{
   return _tcpIpInfo.getIPAddress();
}

RWCString& CtiPortTCPIPDirect::getIPAddress()
{
   return _tcpIpInfo.getIPAddress();
}

INT CtiPortTCPIPDirect::init()
{
   INT      status = NORMAL;

   LockGuard grd( monitor() );

   if(isInhibited())
   {
      status = PORTINHIBITED;
   }
   else if(_socket == INVALID_SOCKET)
   {
      ULONG    i, j;

      int      OptVal;
      USHORT   ipport = getIPPort();

      /* Take a crack at hooking up */
      /* set up client for stuff we will send */
      memset (&_server, 0, sizeof (_server));

      _server.sin_family = AF_INET;
      _server.sin_addr.s_addr = inet_addr ( getIPAddress().data() );
      _server.sin_port = htons( ipport );

      /* get a stream socket. */
      if((_socket = socket (AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error getting Socket for Terminal Server:  " << WSAGetLastError() << " " << getName() << endl;
         }
         shutdownClose(__FILE__, __LINE__);
         status = TCPCONNECTERROR;
      }
      else
      {
         if( connect(_socket, (const struct sockaddr*)&_server, sizeof(_server)) == SOCKET_ERROR)
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Error Connecting to Terminal Server:  " << WSAGetLastError() << " " << getName() << endl;
            }
            shutdownClose(__FILE__, __LINE__);
            return(TCPCONNECTERROR);
         }

         _open = true;

         /* Turn on the keepalive timer */
         OptVal = 1;
         if(setsockopt (_socket, SOL_SOCKET, SO_KEEPALIVE, (char *) &OptVal, sizeof (OptVal)))
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error setting KeepAlive Mode for Terminal Server Socket:  " << WSAGetLastError() << " " << getName() << endl;
         }

         _connected   = true;
         _baud        = _tblPortSettings.getBaudRate();

#if 0
         /* Not digi stuff so set to non blocking */
         ULONG ulTemp = 1L;
         if(ioctlsocket (_socket, FIONBIO, &ulTemp))
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error setting Non Blocking Mode for Terminal Server Socket:  " << WSAGetLastError() << " " << getName() << endl;
         }
#endif
      }
   }


   return status;
}


INT CtiPortTCPIPDirect::close(INT trace)
{
   return shutdownClose();
}

INT CtiPortTCPIPDirect::inClear() const
{
   INT status = NORMAL;
   PBYTE Buffer;
   ULONG ulTemp;

   if(_socket != INVALID_SOCKET)
   {
      // How many are available ??
      if( ioctlsocket(_socket, FIONREAD, &ulTemp) == SOCKET_ERROR )
      {
          return SOCKET_ERROR;
      }
      else
      {
          if(ulTemp == 0)
          {
             return(NORMAL);
          }

          if((Buffer = (PBYTE)malloc(ulTemp)) == NULL)
          {
             return(MEMORY);
          }

          if(recv (_socket, (PCHAR)Buffer, (int)ulTemp, 0) == SOCKET_ERROR )
          {
              status = TCPREADERROR;
          }

          free (Buffer);
      }
   }

   return status;
}

INT CtiPortTCPIPDirect::outClear() const
{
   INT status = NORMAL;

   return NORMAL;
}


INT CtiPortTCPIPDirect::inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList)
{
   INT      status      = NORMAL;

   BYTE     SomeMessage[300];
   ULONG    DCDCount    = 0;
   ULONG    SomeRead    = 0;
   ULONG    Told, Tnew, Tmot;
   LONG     byteCount   = 0;

   LockGuard gd(monitor());

   BYTE     *Message    = Xfer.getInBuffer();      // Local alias for ease of use!

   Xfer.setInCountActual( (ULONG)0 );     // Mark it as zero to prevent any "lies"

   if(Xfer.getNonBlockingReads())         // We need to get all that are out there.
   {
       ULONG bytesavail = 0;
       INT   lpcnt = 0;
       ULONG expected = Xfer.getInCountExpected();

       while( Xfer.getInTimeout() * 4 >= lpcnt++ )  // Must do this at least once.
       {
           Sleep(250);

           bytesavail = 0;
           if(_socket != INVALID_SOCKET)
           {
               ioctlsocket (_socket, FIONREAD, &bytesavail);
           }

           if(0)
           {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               dout << "   There are " << bytesavail << " on the port..  I wanted " << expected << "  I waited " << lpcnt << " 1/4 seconds " << endl;
           }

           if( (expected > 0 && bytesavail >= expected) ||  (expected == 0 && bytesavail > 0) )
           {
               /*
                *   If we specified a byte count, we will wait one timeout amount of time before returning (and
                *   return whatever is available). If not we will wait for any bytes to become available and
                *   return them.
                */
               break; // the while loop
           }
       }

       Xfer.setInCountExpected( bytesavail );
   }

   /* If getInCountExpected() is 0 just return */
   if(Xfer.getInCountExpected() == 0)  // Don't ask me for it then!
   {
      return(NORMAL);
   }

   /* set the read timeout */
   Told = (Xfer.getInTimeout() + getDelay(EXTRA_DELAY));
   Tnew = (byteTime(Xfer.getInCountExpected()) + getDelay(EXTRA_DELAY) );
   Tmot = (Told > Tnew) ? Told : Tnew;

   if(Xfer.isMessageStart())           // Are we the initial request?
   {
      if(getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY))
      {
         CTISleep ((ULONG) getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));
         inClear();
      }

      if(_tblPortSettings.getCDWait() != 0)
      {
         status = NODCD;
         /* Check if we have DCD */
         while(!(dcdTest()) && DCDCount++ < _tblPortSettings.getCDWait())
         {
            /* We do not have DCD... Wait 1/20 second and try again */
            CTISleep (50L);
         }

         if(DCDCount < _tblPortSettings.getCDWait())
         {
            status = NORMAL;
         }
      }
   }

   if(status == NORMAL)
   {
      /* If neccesary wait for IDLC flag character */
      if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Xfer.isMessageStart())
      {
         do
         {
            if((status = receiveData(Message, 1, Tmot, &byteCount)) || byteCount != 1)
            {
               break;
            }

            SomeMessage[SomeRead] = Message[0];
            SomeRead++;

            if(SomeRead == sizeof(SomeMessage))
            {
               // oh no we stomped memory
               status = FRAMEERR;
               break;               // the while loop
            }
         }  while(Message[0] != 0x7e && Message[0] != 0xfc);

         if(status != NORMAL && SomeRead)
         {
            memcpy (Message, SomeMessage, SomeRead);
            byteCount = SomeRead;
         }

         if(status == NORMAL)
         {
            if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Message[0] == 0xfc)
            {
               Message[0] = 0x7e;
            }

            if((status = receiveData(&Message[1], Xfer.getInCountExpected() - 1, Tmot, &byteCount)) != NORMAL)
            {
               if(status == BADSOCK)
               {
                  shutdownClose();
               }
            }

            if(status == NORMAL)
            {
               byteCount += 1;  // Add the 7e byte into the count
            }
         }
      }
      else
      {
         if((status = receiveData(Message, Xfer.getInCountExpected(), Tmot, &byteCount)) != NORMAL)
         {
            if(status == BADSOCK)
            {
               shutdownClose();
            }
         }
      }
   }

   if(status == NORMAL)
   {
      if(byteCount != Xfer.getInCountExpected())
      {
         INT oldcount = byteCount;

         if((status = receiveData(Message + byteCount, Xfer.getInCountExpected() - byteCount, Tmot, &byteCount)) != NORMAL)
         {
            if(status == BADSOCK)
            {
               shutdownClose();
            }
         }

         if(byteCount != Xfer.getInCountExpected())
         {
            byteCount += oldcount;
            status = READTIMEOUT;
         }
      }

      Xfer.setInCountActual((ULONG)byteCount);      // This is the number of bytes filled into the buffer!

      /* Do the extra delay if the message is a completing type */
      if( Xfer.isMessageComplete() )
      {
         if(Dev->getPostDelay()) CTISleep((ULONG)Dev->getPostDelay());
      }

      if(Xfer.verifyCRC() && CheckCCITT16CRC(Dev->getType(), Xfer.getInBuffer(), *Xfer.getInCountActual()))    // CRC check failed.
      {
         status = BADCRC;
      }
   }

   return status;
}

INT CtiPortTCPIPDirect::outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList)
{
   INT      status = NORMAL;
   INT      i = 0;

   ULONG    Written;
   ULONG    MSecs;
   ULONG    ByteCount;
   ULONG    StartWrite;
   ULONG    ReturnWrite;

   LockGuard gd(monitor());

   if(_socket == INVALID_SOCKET)
   {
      status = BADSOCK;        // Invalid Handle really
   }
   else if(Xfer.getOutCount() > 0)
   {
      if(Xfer.getOutCount() > 4096)
      {
         cerr << " *** ERROR *** to attempt an OutMess of " << Xfer.getOutCount() << " bytes" << endl;
         Xfer.setOutCount(100);     // Only allow 100 or so...
      }

      if(Xfer.addCRC())
      {
         BYTEUSHORT  CRC;
         CRC.sh = CCITT16CRC(Dev->getType(), Xfer.getOutBuffer(), Xfer.getOutCount(), TRUE); // CRC func appends the CRC data
         Xfer.setOutCount( Xfer.getOutCount() + 2 );
      }

      /* Wait for DCD to dissapear */
      if(_tblPortSettings.getCDWait() != 0)
      {
         i = 0;
         while(i++ < _tblPortSettings.getCDWait() && dcdTest() )
         {
            CTISleep (50L);
         }
      }

      /* Check if we need to key ... Pre Key Delay */
      if(getDelay(PRE_RTS_DELAY))
      {
         CTISleep (getDelay(PRE_RTS_DELAY));
      }

      /* Clear the Buffers */
      outClear();
      if( inClear() != NORMAL )
      {
          shutdownClose( __FILE__, __LINE__);
      }

      /* Key the radio */
      raiseRTS();
      /* get the present time */
      MilliTime (&MSecs);

      if(getDelay(RTS_TO_DATA_OUT_DELAY))
      {
         CTISleep (getDelay(RTS_TO_DATA_OUT_DELAY));
      }

      /* Remember when we started writing */
      MilliTime (&StartWrite);

      if( sendData(Xfer.getOutBuffer(), Xfer.getOutCount(), &Written) || Written != Xfer.getOutCount())
      {
         shutdownClose();
         status = PORTWRITE;
      }

      if(status == NORMAL)
      {
         /* Now outwait the hardware queue if neccessary */
         if(Xfer.getOutCount() > 2)
         {
            MilliTime (&ReturnWrite);
            if(ReturnWrite < (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / _tblPortSettings.getBaudRate())))
            {
               CTISleep (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / _tblPortSettings.getBaudRate()) - ReturnWrite);
            }
         }
         /* Time to do the RTS thing */
         if(getDelay(DATA_OUT_TO_RTS_DOWN_DELAY))
         {
             CTISleep (getDelay(DATA_OUT_TO_RTS_DOWN_DELAY));

             if(!isDialup())
             {
                lowerRTS();
             }
         }

         if(Dev->getAddress() == RTUGLOBAL || Dev->getAddress() == CCUGLOBAL)
         {
            CTISleep (Dev->getPostDelay());
         }
      }

      /* Check if we need to do a trace */
      traceOut(Xfer, traceList, Dev, status);
   }

   return status;
}


INT CtiPortTCPIPDirect::shutdownClose(PCHAR Label, ULONG Line)
{
   INT   iRet = 0;

   if(_socket != INVALID_SOCKET)
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Closing socket " << _socket;

         if(Label != NULL)
         {
            dout << " from " << Label << " (" << Line << ") ";
         }

         dout << endl;
      }

      shutdown(_socket, 2);
      _open = false;

      if(closesocket(_socket))
      {
         iRet = WSAGetLastError();

         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Socket close failed. Error = " << iRet << endl;
         }
      }
      else
      {
         _connected = false;
      }

   }

   _socket = INVALID_SOCKET;
   _lastBaudRate = 0;


   return(iRet);
}

INT CtiPortTCPIPDirect::queryBytesAvailable()
{
   ULONG ReceiveLength = 0;

   if(ioctlsocket(_socket, FIONREAD, &ReceiveLength))
   {
      return(-1);
   }

   return((INT)ReceiveLength);
}


INT CtiPortTCPIPDirect::receiveData(PBYTE Message, LONG Length, ULONG TimeOut, PLONG ReceiveLength)
{
   INT status = NORMAL;
   int WaitCount = 0;
   ULONG ulTemp = 0;


   *ReceiveLength = 0;  // no lies here

   if(_socket != INVALID_SOCKET)
   {
      /* Wait up to timeout for characters to be available */
      while((ULONG)WaitCount++ <= ((TimeOut * 1000L) / 50L))
      {
         /* Find out if we have any bytes */
         ioctlsocket (_socket, FIONREAD, &ulTemp);

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
         if((*ReceiveLength = recv(_socket, (CHAR*)Message, Length, 0)) <= 0)
         {
            shutdownClose(__FILE__, __LINE__);
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Read from Terminal Server failed:  " << WSAGetLastError() << endl;
            }
            return(TCPREADERROR);
         }

         if(*ReceiveLength < Length)
         {
            if(ulTemp >= Length)     // The stupid thing told us the bytes were there!
            {
               int bytesrecv = 0;

               while(*ReceiveLength < Length)
               {
                  bytesrecv = recv(_socket, (CHAR*)&Message[*ReceiveLength], Length - *ReceiveLength, 0);

                  if(bytesrecv > 0)
                  {
                     *ReceiveLength += bytesrecv;
                  }
               }
            }
            else
            {
               status = READTIMEOUT;
            }
         }
      }
      else
      {
         /* Go ahead and actually read some bytes */
         if((*ReceiveLength = recv(_socket, (CHAR*)Message, -Length, 0)) <= 0)
         {
            shutdownClose(__FILE__, __LINE__);
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Read from Terminal Server failed:  " << WSAGetLastError() << endl;
            }
            status = TCPREADERROR;
         }
      }
   }
   else
   {
      status = BADSOCK;
   }

   return status;
}



/* Routine to send a message to a TCP/IP Terminal Server port */
INT CtiPortTCPIPDirect::sendData(PBYTE Message, ULONG Length, PULONG Written)
{
   int i;
   INT status = NORMAL;
   USHORT CharsToSend;
   ULONG TimeToSend;
   INT retval;

   init();      // OK, just do this every time in case!

   if( (retval = send (_socket, (CHAR*)Message, Length, 0)) == SOCKET_ERROR )
   {
      shutdownClose(__FILE__, __LINE__);
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Error Sending Message to Terminal Server:  " << WSAGetLastError() << endl;
      }
      status = TCPWRITEERROR;
   }
   else
   {
      *Written = retval;
   }

   /* On normal terminal server it does not matter if we sit */
   return status;
}

void CtiPortTCPIPDirect::Dump() const
{
   Inherited::Dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " IP Address                        = " << getIPAddress() << endl;
   dout << " IP Port                           = " << getIPPort() << endl;

   return;
}

void CtiPortTCPIPDirect::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);
   _tcpIpInfo.DecodeDatabaseReader(rdr);       // get the base class handled
}

bool CtiPortTCPIPDirect::needsReinit() const
{
   return (_socket == INVALID_SOCKET);
}

void CtiPortTCPIPDirect::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTablePortTCPIP::getSQL(db, keyTable, selector);
}

