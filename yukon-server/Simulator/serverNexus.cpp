/*****************************************************************************
*
*    FILE NAME: serverNexus.cpp
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Simulate the behavior of CCUs
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#ifndef __RWNET_WINSOCK_H__
#define __RWNET_WINSOCK_H__
#include "yukon.h"
#include <windows.h>
#include <iostream>
#include "cticalls.h"
#include "ctinexus.h"
#include "dsm2.h"
#include "CCU710.h"
#include "CCU711.h"
#include "color.h"
#include "ctiTime.h"


int main(int argc, char *argv[])
{	
	CCU711 aCCU711;
    CCU710 aCCU710;

    WSADATA wsaData;

    int portNumber = 0;
    int ccuNumber = 0;
    int mctNumber = 0;

    if(argc>1)
    {   // Specify port number
        cout<<"Port set to "<<argv[1]<<endl;
        portNumber = atoi(argv[1]);
    }
    else
    {
            cout<<"No command line argument found specifying port!"<<endl;
            return 0;
    }



    WSAStartup(MAKEWORD (1,1), &wsaData);

	CTINEXUS * listenSocket; 
	listenSocket = new CTINEXUS();
	listenSocket->CTINexusCreate(portNumber);   //12345 or 11234 for example

	CTINEXUS * newSocket;
	newSocket = new CTINEXUS();

	for(int i = 0; i<1000000 && !(newSocket->CTINexusValid()); i++) {
		listenSocket->CTINexusConnect(newSocket, NULL, 1000, CTINEXUS_FLAG_READEXACTLY);
		CtiTime Listening;
		std::cout<<Listening.asString()<<" Listening..."<<std::endl;
	}


	while(newSocket->CTINexusValid()) {
        unsigned char TempBuffer[1];
        unsigned long bytesRead=0;
                
        //  Peek at first byte
        newSocket->CTINexusPeek(TempBuffer,1, &bytesRead);

        if(TempBuffer[0]==0x7e)
            {   //  It's a 711 IDLC message
            CtiTime AboutToRead;
            unsigned char ReadBuffer[300];
            int BytesToFollow;
            int counter = 0;
            bytesRead=0;
            BytesToFollow = 4;
            //  Read first few bytes
            while(bytesRead < BytesToFollow) {
                newSocket->CTINexusRead(ReadBuffer + counter  ,1, &bytesRead, 15);
                counter ++;
            }

            if(ReadBuffer[0]==0x7e )
                {
                SET_FOREGROUND_BRIGHT_YELLOW;
                cout <<'\n'<< AboutToRead.asString();
                SET_FOREGROUND_BRIGHT_CYAN;
                cout << " IN:" << endl;
            }
            SET_FOREGROUND_BRIGHT_GREEN;
            for( int byteitr = 0; byteitr < (bytesRead); byteitr++ )
                {
                cout << string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2)) << ' ';
            }


            BytesToFollow = aCCU711.ReceiveMsg(ReadBuffer, ccuNumber);

            if(BytesToFollow>0)
            {
                bytesRead=0;
                //  Read any additional bytes
                while(bytesRead < BytesToFollow) {
                    newSocket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
                    counter++;
                }
                for( byteitr = 0; byteitr < BytesToFollow; byteitr++ )
                    {
                    if(byteitr == 1)
                        {
                        SET_FOREGROUND_BRIGHT_RED;
                        cout << string(CtiNumStr(ReadBuffer[byteitr+4]).hex().zpad(2)) << ' ';
                        SET_FOREGROUND_BRIGHT_GREEN;
                    }
                    else
                        cout<<string(CtiNumStr(ReadBuffer[byteitr+4]).hex().zpad(2))<<' ';
                }

                aCCU711.ReceiveMore(ReadBuffer, counter);
                aCCU711.PrintInput();
            }

            aCCU711.CreateMsg(ccuNumber);

            unsigned char SendData[300];

            int MsgSize = aCCU711.SendMsg(SendData);

            if(MsgSize>0)
             {
                unsigned long bytesWritten = 0;
                newSocket->CTINexusWrite(&SendData, MsgSize, &bytesWritten, 15); 

                CtiTime DateSent;
                SET_FOREGROUND_BRIGHT_YELLOW;
                cout<<DateSent.asString();
                SET_FOREGROUND_BRIGHT_CYAN;
                cout<<" OUT:"<<endl;
                SET_FOREGROUND_BRIGHT_MAGNETA;

                for(byteitr = 0; byteitr < bytesWritten; byteitr++ )
                    {
                    cout <<string(CtiNumStr(SendData[byteitr]).hex().zpad(2))<<' ';
                }

                aCCU711.PrintMessage();
            }
            else
                {
                //SET_FOREGROUND_BRIGHT_RED;
                //cout<<"Error: Outgoing message is null"<<endl;
                //SET_FOREGROUND_WHITE;
            }
        }
        else if(TempBuffer[0] & 0x04)
        {   //  It's a 710 message
            CtiTime AboutToRead;
            unsigned char ReadBuffer[300];
            int BytesToFollow;
            int counter = 0;
            bytesRead=0;
            BytesToFollow = 3;
            //  Read first few bytes
            while(bytesRead < BytesToFollow) {
                newSocket->CTINexusRead(ReadBuffer + counter  ,1, &bytesRead, 15);
                counter ++;
            }

            SET_FOREGROUND_BRIGHT_YELLOW;
            cout << AboutToRead.asString();
            SET_FOREGROUND_BRIGHT_CYAN;
            cout << " IN:" << endl;
            SET_FOREGROUND_BRIGHT_GREEN;
            for( int byteitr = 0; byteitr < (bytesRead); byteitr++ )
            {
                cout << string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2)) << ' ';
            }


            BytesToFollow = aCCU710.ReceiveMsg(ReadBuffer, ccuNumber);

            if(BytesToFollow>0)
            {
                bytesRead=0;
                //  Read any additional bytes
                while(bytesRead < BytesToFollow) {
                    newSocket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
                    counter++;
                }
                for( byteitr = 0; byteitr < BytesToFollow; byteitr++ )
                    {
                    cout<<string(CtiNumStr(ReadBuffer[byteitr+3]).hex().zpad(2))<<' ';
                }


                aCCU710.ReceiveMore(ReadBuffer, mctNumber,counter);
                aCCU710.PrintInput();
            }
            

            if(BytesToFollow>0)
                {
                aCCU710.CreateMsg(ccuNumber, mctNumber);

                unsigned char SendData[300];

                int MsgSize = aCCU710.SendMsg(SendData);

                if(MsgSize>0)
                    {
                    unsigned long bytesWritten = 0;
                    newSocket->CTINexusWrite(&SendData, MsgSize, &bytesWritten, 15); 

                    CtiTime DateSent;
                    SET_FOREGROUND_BRIGHT_YELLOW;
                    cout<<DateSent.asString();
                    SET_FOREGROUND_BRIGHT_CYAN;
                    cout<<" OUT:"<<endl;
                    SET_FOREGROUND_BRIGHT_MAGNETA;

                    for(byteitr = 0; byteitr < bytesWritten; byteitr++ )
                        {
                        cout <<string(CtiNumStr(SendData[byteitr]).hex().zpad(2))<<' ';
                    }

                    cout<<endl;
                    aCCU710.PrintMessage();
                }
            }
        }

    }

    CTISleep(5000);

    return 0;
}

#endif





////////////////////////////////////////
// 
//       CODE BELOW ALLOWS FOR PORT CONTROL
//       USING SELECT()

/*

#include <winsock2.h>
#include <windows.h>
#include <stdio.h>
#include <iostream>
#include <numstr.h>
#include "CCU710.h"

#define PORT 11234
#define PORT2 12345

#define BUFFERSIZE 10

typedef struct _MYSOCKET_INFORMATION {
   CHAR Buffer[BUFFERSIZE];
   WSABUF DataBuf;
   SOCKET Socket;
   OVERLAPPED Overlapped;
   DWORD SendBytes;
   DWORD RecvBytes;
} SOCKET_INFORMATION, * LPSOCKET_INFORMATION;

BOOL CreateSocketInformation(SOCKET s);
void FreeSocketInformation(DWORD Index);

DWORD TotalSockets = 0;
LPSOCKET_INFORMATION SocketList[FD_SETSIZE];

void main(void)
{
   SOCKET ListenSocket;
   SOCKET AcceptSocket;
   SOCKET ListenSocket2;
   SOCKET AcceptSocket2;
   SOCKADDR_IN InternetAddr;
   WSADATA wsaData;
   INT Ret;
   FD_SET Writer;
   FD_SET Reader;
   DWORD i;
   DWORD Total;
   ULONG NonBlock;
   DWORD Flags;
   DWORD SendBytes;
   DWORD RecvBytes;


   if ((Ret = WSAStartup(MAKEWORD(2,0),&wsaData)) != 0)
   {
      printf("WSAStartup() failed with error %d\n", Ret);
      WSACleanup();
      return;
   }

   // Create a socket.

   if ((ListenSocket = WSASocket(AF_INET, SOCK_STREAM, 0, NULL, 0,
      WSA_FLAG_OVERLAPPED)) == INVALID_SOCKET) 
   {
      printf("WSASocket() failed with error %d\n", WSAGetLastError());
      return;
   }

   InternetAddr.sin_family = AF_INET;
   InternetAddr.sin_addr.s_addr = htonl(INADDR_ANY);
   InternetAddr.sin_port = htons(PORT);

   if (bind(ListenSocket, (SOCKADDR *) &InternetAddr, sizeof(InternetAddr))
      == SOCKET_ERROR)
   {
      printf("Binding failed with error %d\n", WSAGetLastError());
      return;
   }

   if (listen(ListenSocket, 5))
   {
      printf("listen failed with error %d\n", WSAGetLastError());
      return;
   }

   // Change the socket mode on the listening socket from blocking to non-block 

   NonBlock = 1;
   if (ioctlsocket(ListenSocket, FIONBIO, &NonBlock) == SOCKET_ERROR)
   {
      printf("ioctlsocket() failed \n");
      return;
   }


   // Create another socket.

   if ((ListenSocket2 = WSASocket(AF_INET, SOCK_STREAM, 0, NULL, 0,
	  WSA_FLAG_OVERLAPPED)) == INVALID_SOCKET) 
   {
	  printf("WSASocket() failed with error %d\n", WSAGetLastError());
	  return;
   }

   InternetAddr.sin_family = AF_INET;
   InternetAddr.sin_addr.s_addr = htonl(INADDR_ANY);
   InternetAddr.sin_port = htons(PORT2);

   if (bind(ListenSocket2, (SOCKADDR *) &InternetAddr, sizeof(InternetAddr))
	  == SOCKET_ERROR)
   {
	  printf("Binding failed with error %d\n", WSAGetLastError());
	  return;
   }

   if (listen(ListenSocket2, 5))
   {
	  printf("listen failed with error %d\n", WSAGetLastError());
	  return;
   }

   // Change the socket mode on the listening socket from blocking to non-block 

   NonBlock = 1;
   if (ioctlsocket(ListenSocket2, FIONBIO, &NonBlock) == SOCKET_ERROR)
   {
	  printf("ioctlsocket() failed \n");
	  return;
   }

   //  begin looping with the select() function
   while(TRUE)
   {
      // Initialize the Read and Write socket set.
      FD_ZERO(&Reader);
      FD_ZERO(&Writer);

      // Check for connection attempts.

      FD_SET(ListenSocket, &Reader);
	  FD_SET(ListenSocket2, &Reader);

      // Set Read and Write notification for each socket based on the
      // current state the buffer.  

      for (i = 0; i < TotalSockets; i++)
         if (SocketList[i]->RecvBytes > SocketList[i]->SendBytes)
            FD_SET(SocketList[i]->Socket, &Writer);
         else
            FD_SET(SocketList[i]->Socket, &Reader);

      if ((Total = select(FD_SETSIZE, &Reader, &Writer, NULL, NULL)) == SOCKET_ERROR)
      {
         printf("select function returned with error %d\n", WSAGetLastError());
         return;
      }
	  std::cout<<"Total Sockets = "<<TotalSockets<<std::endl;

      // Check for arriving connections on the listening socket.
      if (FD_ISSET(ListenSocket, &Reader))
      {
         Total--;
         if ((AcceptSocket = accept(ListenSocket, NULL, NULL)) != INVALID_SOCKET)
         {

            // Set the accepted socket to non-blocking mode so the server will
            // not get caught in a blocked condition on WSASends

            NonBlock = 1;
            if (ioctlsocket(AcceptSocket, FIONBIO, &NonBlock) == SOCKET_ERROR)
            {
               printf("ioctlsocket() failed with error %d\n", WSAGetLastError());
               return;
            }

            if (CreateSocketInformation(AcceptSocket) == FALSE)
               return;

         }
         else
         {		
            if (WSAGetLastError() != WSAEWOULDBLOCK)
            {
               printf("accept() failed with error %d\n", WSAGetLastError());
               return;
            }
         }
      }

	  // Check for arriving connections on listening socket2.
      if (FD_ISSET(ListenSocket2, &Reader))
      {
         Total--;
         if ((AcceptSocket = accept(ListenSocket2, NULL, NULL)) != INVALID_SOCKET)
         {

            // Set the accepted socket to non-blocking mode so the server will
            // not get caught in a blocked condition on WSASends

            NonBlock = 1;
            if (ioctlsocket(AcceptSocket, FIONBIO, &NonBlock) == SOCKET_ERROR)
            {
               printf("ioctlsocket() failed with error %d\n", WSAGetLastError());
               return;
            }

            if (CreateSocketInformation(AcceptSocket) == FALSE)
               return;

         }
         else
         {		
            if (WSAGetLastError() != WSAEWOULDBLOCK)
            {
               printf("accept() failed with error %d\n", WSAGetLastError());
               return;
            }
         }
      }

      // Check each socket for Read and Write notification for Total number of sockets

      for (i = 0; Total > 0 && i < TotalSockets; i++)
      {
         LPSOCKET_INFORMATION SocketInfo = SocketList[i];

         // If the Reader is marked for this socket then this means data
         // is available to be read on the socket.

         if (FD_ISSET(SocketInfo->Socket, &Reader))
         {
            Total--;

            SocketInfo->DataBuf.buf = SocketInfo->Buffer;
            SocketInfo->DataBuf.len = BUFFERSIZE;

            Flags = 0;
            if (WSARecv(SocketInfo->Socket, &(SocketInfo->DataBuf), 1, &RecvBytes,
               &Flags, NULL, NULL) == SOCKET_ERROR)
            {
               if (WSAGetLastError() != WSAEWOULDBLOCK)
               {
                  printf("Receive failed with error\n");

                  FreeSocketInformation(i);
               }

               continue;
            } 
            else
            {
               SocketInfo->RecvBytes = RecvBytes;
			   ////////////////////////////////add something here to allow 
			   //   the output of hex!
			   //printf("%s\n",SocketInfo->DataBuf.buf);
			   for( int byteitr = 0; byteitr < RecvBytes; byteitr++ ){
				   std::cout<<std::string(CtiNumStr(SocketInfo->DataBuf.buf[byteitr]).hex().zpad(2))<<std::endl;
			   }

			   //std::cout<<"Creating CCU710..."<<std::endl;

			   //CCU710 new710;
			   //CTINEXUS * scktPtr = & SocketInfo->Socket;
			   //new710.setSocket(scktPtr);

			   //new710.ReceiveMsg();
			   //new710.CreateMsg();
			   //new710.SendMsg();


			   // If zero bytes are received, this indicates connection is closed.
			   if (RecvBytes == 0)
				   {
				   FreeSocketInformation(i);
                  continue;
               }
            }
         }


         // If the Writer is marked on this socket then this means the internal
         // data buffers are available for more data.

         if (FD_ISSET(SocketInfo->Socket, &Writer))
         {
            Total--;

            SocketInfo->DataBuf.buf = SocketInfo->Buffer + SocketInfo->SendBytes;
            SocketInfo->DataBuf.len = SocketInfo->RecvBytes - SocketInfo->SendBytes;

            if (WSASend(SocketInfo->Socket, &(SocketInfo->DataBuf), 1, &SendBytes, 0,
               NULL, NULL) == SOCKET_ERROR)
            {
               if (WSAGetLastError() != WSAEWOULDBLOCK)
               {
                  printf("Send failed with error\n");

                  FreeSocketInformation(i);
               }

               continue;
            }
            else
            {
               SocketInfo->SendBytes += SendBytes;

               if (SocketInfo->SendBytes == SocketInfo->RecvBytes)
               {
                  SocketInfo->SendBytes = 0;
                  SocketInfo->RecvBytes = 0;
               }
            }
         }
      }
   }
}

BOOL CreateSocketInformation(SOCKET s)
{
   LPSOCKET_INFORMATION SI;
      
   printf("Accepted socket\n");

   if ((SI = (LPSOCKET_INFORMATION) GlobalAlloc(GPTR,
      sizeof(SOCKET_INFORMATION))) == NULL)
   {
      printf("GlobalAlloc() failed\n");
      return FALSE;
   }

   // Prepare SocketInfo structure for use.

   SI->Socket = s;
   SI->SendBytes = 0;
   SI->RecvBytes = 0;

   SocketList[TotalSockets] = SI;

   TotalSockets++;

   return(TRUE);
}

void FreeSocketInformation(DWORD Index)
{
   LPSOCKET_INFORMATION SI = SocketList[Index];
   DWORD i;

   closesocket(SI->Socket);

   printf("Closing socket\n");

   GlobalFree(SI);

   // Remove from the socket array
   for (i = Index; i < TotalSockets; i++)
   {
      SocketList[i] = SocketList[i + 1];
   }

   TotalSockets--;
}

*/



