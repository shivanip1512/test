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
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <vector>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <rw/db/datetime.h>

#include "cticalls.h"
#include "ctinexus.h"
#include "dsm2.h"
#include "color.h"
#include "ctiTime.h"
#include "dbaccess.h"
#include "mctStruct.h"
#include "CCU710.h"
#include "CCU711.h"


bool globalCtrlCFlag = false;

using namespace std;

boost::mutex io_mutex;


void CCUThread(const int& s, const int& strtgy);

/* CtrlHandler handles is used to catch ctrl-c when run in a console */
BOOL CtrlHandler(DWORD fdwCtrlType)
{
    switch( fdwCtrlType )
    {
    case CTRL_C_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:
    case CTRL_LOGOFF_EVENT:
        {
            globalCtrlCFlag = true;
            Sleep(50000);
            return TRUE;
        }

    default:
        {
            return FALSE;
        }
    }
}

typedef void (*CCUThreadFunPtr)(const int&, const int&);


template<typename FunT,
typename ParamT,
typename ParamU>
struct Adapter
{
    Adapter(FunT f, ParamT& p, ParamU& q) :
    f_(f), p_(&p) , q_(&q)
    {
    }

    void operator()()
    {
        f_(*p_,*q_);
    }
    private:
    FunT f_;
    ParamT* p_;
    ParamU* q_;
};


int main(int argc, char *argv[])
{
    vector<boost::thread *> threadVector;

    if( argc==4 )
    {   // Specify port number
        cout << "Port range " << argv[1] << " - " << argv[2] << endl;
        cout << "Strategy selected: " << argv[3] << endl;
    }
    else if( argc==3 )
    {
        cout << "Port range " << argv[1] << " - " << argv[2] << endl;
    }
    else
    {
        cout << "Invalid port range entry.  Format is:  ccu_simulator 00001 99999" << endl;
        return 0;
    }


    int portNum = atoi(argv[1]);
    int portMax = atoi(argv[2]);
    int strategy = 0;
    if( argc==4 )
    {
        strategy = atoi(argv[3]);
    }
    boost::thread *thr1;
    while( portNum != (portMax+1) )
    {
        thr1 = new boost::thread(Adapter<CCUThreadFunPtr, int, int>(CCUThread, portNum, strategy));
        threadVector.push_back(thr1);
        CTISleep(750);
        portNum++;
    }


    vector<boost::thread *>::iterator itr = threadVector.begin();

    //  We need to catch ctrl-c so we can stop
    if( !SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE) )
    {
        cerr << "Could not install control handler" << endl;
    }

    (*itr)->join();
    /* if(argv[1]!=argv[2]) { //  Check to see if there was >1 port specified
         itr++;
         (*itr)->join();
         cout<<"Threads have joined!!!"<<endl;
     }*/



    if( globalCtrlCFlag )
    {
        boost::mutex::scoped_lock lock(io_mutex);
        cout<<"Main function closing..."<<endl;
        exit(0);
    }

    cout<<"Returning from main function..."<<endl;
    CTISleep(10000);
    return 0;
}


///////  MOVE THIS INTO IT'S OWN FILE/////////////////////////////
void CCUThread(const int& s, const int& strtgy)
{
    int portNumber = s;
    {
        boost::mutex::scoped_lock lock(io_mutex);
        cout<<"Port: "<<portNumber<<endl;
    }
    int strategy=strtgy;


//////////////////////////////////////////////////////////
//   ADDING CODE TO READ AND STORE GETKWH VALUES
// ///////////////////////////////////////////////////////
//CHANGE THIS TO getConnection(0) !!!
/////////////////////////////////////////////

    //InitYukonBaseGlobals();                            // Load up the config file.

    // Set default database connection params
    setDatabaseParams(0, "msq15d.dll", "mn1db02\\server2005", "erooney", "erooney");   // *** THIS NEEDS TO BE CHANGED FOR ALL USERS !!!!!!!!

    /*
    RWDBConnection conn = getConnection();
    RWDBDatabase   db   = conn.database();
    if( !db.isValid() )
    {
        cout << "Cannot connect to database.  Skipping database..."<<endl;//myDbase.status().message() << endl;
    }
    else
    {

        cout << "Connected to database."<<endl;
    }
    RWDBTable    table     = db.table("METERDATA");
    if( table.exists() )
    {
        cout<<"Table "<<table.name()<<" exists"<<endl;
        //cout<<"DB status: "<<(db.status()).errorCode()<<endl;
    }
    else
    {
        RWDBSchema schema;
        RWDBValue value(20);
        RWDBValue value2(20.02);
        RWDBDateTime timestamp;
        RWDBValue value3(timestamp);
        schema.appendColumn("MCTADDRESS", value.type());
        schema.appendColumn("KWHVALUE", value2.type());
        schema.appendColumn("TIMESTAMP", value3.type());
        RWCString name = "METERDATA";
        cout<<"Table does not exist.  Attempting to create table..."<<endl;
        cout<<"Creating table: "<<(db.createTable(name, schema, conn)).errorCode()<<endl;
        if( table.exists() )
        {
            cout<<"Table created successfully"<<endl;
        }
        else
        {
            cout<<"Failed to create table: "<<table.name()<<endl;
            db.createTable(table);
        }
    }

    cout<<"Num cols: "<<table.numberOfColumns()<<endl;

    if( strategy==2 )
    {
        if( !db.isValid() )
        {
            cout << "Still cannot connect to database.  Skipping database..."<<endl;//myDbase.status().message() << endl;
        }
        else
        {
            cout << "Connected to database."<<endl;
        }
    }

    RWDBDeleter deleter = table.deleter();
    //deleter.where(autoParts["name"] == "hubcap" &&
    //              autoParts["color"] == "red");
    deleter.execute();
*/
//**************************************DONE CREATING TABLE***************************************


    WSADATA wsaData;
    int ccuNumber = 0;
    int mctNumber = 0;

    std::map <int, CCU711 *> ccuList;


    WSAStartup(MAKEWORD (1,1), &wsaData);

    CTINEXUS * listenSocket;
    listenSocket = new CTINEXUS();
    listenSocket->CTINexusCreate(portNumber);   //12345 or 11234 for example

    CTINEXUS * newSocket;
    newSocket = new CTINEXUS();

    for( int i = 0; i<1000000 && !(newSocket->CTINexusValid()); i++ )
    {
        listenSocket->CTINexusConnect(newSocket, NULL, 10000, CTINEXUS_FLAG_READEXACTLY);
        CtiTime Listening;
        {
            boost::mutex::scoped_lock lock(io_mutex);
            std::cout<<Listening.asString()<<" Listening on " << portNumber << std::endl;
            if( globalCtrlCFlag )
            {
                boost::mutex::scoped_lock lock(io_mutex);
                cout<<"Listening thread closing..."<<endl;
                exit(0);
            }
        }
    }



    CCU710 aCCU710;
    aCCU710.setStrategy(strategy);


    while( !globalCtrlCFlag )
    {
        unsigned char TempBuffer[2];
        TempBuffer[0] = 0x00;
        unsigned long bytesRead=0;
        unsigned long addressFound = 0x00;
        int totalBytesRead = 0;

        //  Peek at first byte
        Sleep(50);
        newSocket->CTINexusPeek(TempBuffer,2, &bytesRead);
        addressFound = TempBuffer[1];

        if( TempBuffer[0]==0x7e )
        {

            CCU711 *aCCU711;
            if( ccuList.find(addressFound) == ccuList.end() )
            {
                boost::mutex::scoped_lock lock(io_mutex);
                std::cout<<'\n'<<addressFound<<" is not in the map!";
                aCCU711 = new CCU711(addressFound);
                ccuList[addressFound] = aCCU711;
                aCCU711->setStrategy(strategy);
            }
            else
            {
                boost::mutex::scoped_lock lock(io_mutex);
                std::cout<<addressFound<<" is in the map";
                aCCU711 = ccuList[addressFound];
            }


            //  It's a 711 IDLC message
            CtiTime AboutToRead;
            unsigned char ReadBuffer[300];
            int BytesToFollow;
            int counter = 0;
            bytesRead=0;
            BytesToFollow = 4;
            //  Read first few bytes
            while( bytesRead < BytesToFollow )
            {
                newSocket->CTINexusRead(ReadBuffer + counter  ,1, &bytesRead, 15);
                counter ++;
            }

            totalBytesRead = bytesRead;

            if( ReadBuffer[0]==0x7e )
            {
                boost::mutex::scoped_lock lock(io_mutex);
                SET_FOREGROUND_BRIGHT_YELLOW;
                cout <<'\n'<< AboutToRead.asString();
                SET_FOREGROUND_BRIGHT_CYAN;
                cout << " IN:  Port: "<<portNumber<< endl;
            }
            SET_FOREGROUND_BRIGHT_GREEN;
            for( int byteitr = 0; byteitr < (bytesRead); byteitr++ )
            {
                CTISleep((8.0/1200.0)*1000.0); //  Delay at 1200 baud
                boost::mutex::scoped_lock lock(io_mutex);
                cout << string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2)) << ' ';
            }

            if( ReadBuffer[1] != 0x7e )  //  Make sure it didn't try to send two messages at once and overlap the two HDLC flags
            {

                BytesToFollow = aCCU711->ReceiveMsg(ReadBuffer, ccuNumber);

                if( BytesToFollow>0 )
                {
                    bytesRead=0;
                    //  Read any additional bytes
                    while( bytesRead < BytesToFollow )
                    {
                        newSocket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
                        counter++;
                    }

                    for( byteitr = 0; byteitr < BytesToFollow; byteitr++ )
                    {
                        CTISleep((8.0/1200.0)*1000.0);  //  Delay at 1200 baud
                        if( byteitr == 1 )
                        {
                            boost::mutex::scoped_lock lock(io_mutex);
                            SET_FOREGROUND_BRIGHT_RED;
                            cout << string(CtiNumStr(ReadBuffer[byteitr+4]).hex().zpad(2)) << ' ';
                            SET_FOREGROUND_BRIGHT_GREEN;
                        }
                        else
                            cout<<string(CtiNumStr(ReadBuffer[byteitr+4]).hex().zpad(2))<<' ';
                    }


                    int mctAddressArray[50];
                    memset(mctAddressArray, 0, 50);
                    aCCU711->getNeededAddresses(mctAddressArray);  // ask the CCU711 which mct addresses it needs values from the db for

                    int i = 0;
                    while( (mctAddressArray[i] != 0) )
                    {
                        cout<<"\nMct: "<<mctAddressArray[i]<<endl;
                        i++;
                    }

                    /*RWDBSelector selector  = db.selector();

                    selector << table["MCTADDRESS"] << table["KWHVALUE"]<< table["TIMESTAMP"];

                    selector.from( table );

                    selector.where( selector["KWHVALUE"] != -1);

                    RWDBReader  rdr = selector.reader( conn );

                    int readMCTaddress = 0;
                    int readKWHvalue = 0;
                    RWDBDateTime readTimestamp;

                    if( !rdr.isValid() )
                    {
                        cout<<"RDR(): No such entry!"<<endl;
                    }
                    else
                    {
                        //  iterate through the components
                        while( rdr() )
                        {
                            rdr>>readMCTaddress>>readKWHvalue>>readTimestamp;
                            cout<<"MCTADDRESS "<<readMCTaddress<<endl;
                            cout<<"KWHVALUE "<<readKWHvalue<<endl;
                            cout<<"TIMESTAMP"<<readTimestamp.asString()<<endl;
                            i = 0;
                            int matchAtIndex = -1;
                            while( (mctAddressArray[i] != 0) )
                            {
                                if( mctAddressArray[i]==readMCTaddress )
                                {
                                    matchAtIndex=i;
                                }
                                i++;
                            }
                            if( matchAtIndex!=-1 )
                            {
                                //cout<<"\nCrossing off match: "<<mctAddressArray[matchAtIndex]<<endl;
                                mctAddressArray[matchAtIndex]=-1;
                            }
                        }

                    }

                    RWDBUpdater updater = table.updater();

                    RWDBColumn kwhvalue = table["KWHVALUE"];
                    updater << kwhvalue.assign(kwhvalue + 7);
                    //updater.where(table["MCTADDRESS"] == 2);
                    updater.execute(conn);

                    //RWDBDeleter deleter = table.deleter();
                    //deleter.where(autoParts["name"] == "hubcap" &&
                    //              autoParts["color"] == "red");
                    //deleter.execute();

                    RWDBInserter inserter  = table.inserter();

                    RWDBStatus::ErrorCode err;
*/
                 /*   i = 0;
                    while( (mctAddressArray[i] != 0) )
                    {
                        if( mctAddressArray[i]!=-1 )
                        {
                            // initialize random seed:
                            CtiTime seedValue;
                            srand ( seedValue.second() );
                            // generate secret number:
                            int inKWHvalue = rand() % 100 + 1 ;
                            RWDBDateTime inTimestamp;
                            inserter<<mctAddressArray[i]<<inKWHvalue<<inTimestamp;
                            if( err =  ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() )
                            {
                                cout << " **** Checkpoint - error \"" << err << "\" while inserting in METERDATA **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            else
                                cout<<"No error inserting "<<mctAddressArray[i]<<endl;
                        }
                        i++;
                    }
*/
                    mctStruct testStruct;
                    mctStruct structArray[100];
                    structArray[0]=testStruct;
                    aCCU711->ReceiveMore(ReadBuffer, counter, structArray);
                    aCCU711->PrintInput();
                }

                aCCU711->CreateMsg(ccuNumber);

                unsigned char SendData[300];

                int MsgSize = aCCU711->SendMsg(SendData);

                if( MsgSize>0 )
                {
                    int napTime = (((MsgSize)*8.0)/1200.0)*1000.0;  //  Delay at 1200 baud
                    CTISleep(napTime);
                    unsigned long bytesWritten = 0;
                    newSocket->CTINexusWrite(&SendData, MsgSize, &bytesWritten, 15);

                    CtiTime DateSent;
                    {
                        boost::mutex::scoped_lock lock(io_mutex);
                        SET_FOREGROUND_BRIGHT_YELLOW;
                        cout<<DateSent.asString();
                        SET_FOREGROUND_BRIGHT_CYAN;
                        cout<<" OUT:"<<endl;
                    }
                    SET_FOREGROUND_BRIGHT_MAGNETA;

                    for( int byteitr = 0; byteitr < bytesWritten; byteitr++ )
                    {
                        boost::mutex::scoped_lock lock(io_mutex);
                        cout <<string(CtiNumStr(SendData[byteitr]).hex().zpad(2))<<' ';
                    }

                    aCCU711->PrintMessage();
                }
                else
                {
                    //SET_FOREGROUND_BRIGHT_RED;
                    //cout<<"Error: Outgoing message is null"<<endl;
                    //SET_FOREGROUND_WHITE;
                }
            }
            else
            {
                SET_FOREGROUND_BRIGHT_RED;
                cout<<"Error: Two IDLC messages overlapped since bytes 0 and 1 are both 0x7e"<<endl;
                SET_FOREGROUND_WHITE;
            }

        }
        else if( TempBuffer[0] & 0x04 )
        {   //  It's a 710 message
            CtiTime AboutToRead;
            unsigned char ReadBuffer[300];
            int BytesToFollow;
            int counter = 0;
            bytesRead=0;
            BytesToFollow = 3;
            //  Read first few bytes
            while( bytesRead < BytesToFollow )
            {
                newSocket->CTINexusRead(ReadBuffer + counter  ,1, &bytesRead, 15);
                counter ++;
            }

            {
                boost::mutex::scoped_lock lock(io_mutex);
                SET_FOREGROUND_BRIGHT_YELLOW;
                cout << AboutToRead.asString();
                SET_FOREGROUND_BRIGHT_CYAN;
                cout << " IN:" << endl;
                SET_FOREGROUND_BRIGHT_GREEN;
                for( int byteitr = 0; byteitr < (bytesRead); byteitr++ )
                {
                    cout << string(CtiNumStr(ReadBuffer[byteitr]).hex().zpad(2)) << ' ';
                }
            }


            BytesToFollow = aCCU710.ReceiveMsg(ReadBuffer, ccuNumber);

            if( BytesToFollow>0 )
            {
                bytesRead=0;
                //  Read any additional bytes
                while( bytesRead < BytesToFollow )
                {
                    newSocket->CTINexusRead(ReadBuffer + counter, 1, &bytesRead, 15);
                    counter++;
                }
                for( int byteitr = 0; byteitr < BytesToFollow; byteitr++ )
                {
                    boost::mutex::scoped_lock lock(io_mutex);
                    cout<<string(CtiNumStr(ReadBuffer[byteitr+3]).hex().zpad(2))<<' ';
                }


                aCCU710.ReceiveMore(ReadBuffer, mctNumber,counter);
                aCCU710.PrintInput();
            }


            if( BytesToFollow>0 )
            {
                aCCU710.CreateMsg(ccuNumber, mctNumber);

                unsigned char SendData[300];

                int MsgSize = aCCU710.SendMsg(SendData);

                if( MsgSize>0 )
                {
                    int napTime = ((MsgSize*8)/1200)*2*1000;  //  Delay at 1200 baud in both directions
                    CTISleep(napTime);
                    unsigned long bytesWritten = 0;
                    newSocket->CTINexusWrite(&SendData, MsgSize, &bytesWritten, 15);

                    CtiTime DateSent;
                    {
                        boost::mutex::scoped_lock lock(io_mutex);
                        SET_FOREGROUND_BRIGHT_YELLOW;
                        cout<<DateSent.asString();
                        SET_FOREGROUND_BRIGHT_CYAN;
                        cout<<" OUT:"<<endl;
                    }
                    SET_FOREGROUND_BRIGHT_MAGNETA;

                    for( int byteitr = 0; byteitr < bytesWritten; byteitr++ )
                    {
                        boost::mutex::scoped_lock lock(io_mutex);
                        cout <<string(CtiNumStr(SendData[byteitr]).hex().zpad(2))<<' ';
                    }

                    cout<<endl;
                    aCCU710.PrintMessage();
                }
            }
        }
        CTISleep(250);

    }
    {
        boost::mutex::scoped_lock lock(io_mutex);
        cout<<"Active thread closing..."<<endl;
    }

    listenSocket->CTINexusClose();
    newSocket->CTINexusClose();
    return;
}





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//       CODE BELOW ALLOWS FOR PORT CONTROL
//       USING SELECT()
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*

#include <winsock2.h>#include <windows.h>#include <stdio.h>#include <iostream>#include <numstr.h>#include "CCU710.h"#define PORT 11234#define PORT2 12345#define BUFFERSIZE 10

typedef struct _MYSOCKET_INFORMATION {
   CHAR Buffer[BUFFERSIZE];WSABUF DataBuf;SOCKET Socket;OVERLAPPED Overlapped;DWORD SendBytes;DWORD RecvBytes;
} SOCKET_INFORMATION, * LPSOCKET_INFORMATION;

BOOL CreateSocketInformation(SOCKET s);void FreeSocketInformation(DWORD Index);DWORD TotalSockets = 0;LPSOCKET_INFORMATION SocketList[FD_SETSIZE];

void main(void)
{
   SOCKET ListenSocket;SOCKET AcceptSocket;SOCKET ListenSocket2;SOCKET AcceptSocket2;SOCKADDR_IN InternetAddr;WSADATA wsaData;
   INT Ret;FD_SET Writer;FD_SET Reader;DWORD i;DWORD Total;ULONG NonBlock;DWORD Flags;DWORD SendBytes;DWORD RecvBytes;


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

   InternetAddr.sin_family = AF_INET;InternetAddr.sin_addr.s_addr = htonl(INADDR_ANY);InternetAddr.sin_port = htons(PORT);

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
               }}}}}}

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



