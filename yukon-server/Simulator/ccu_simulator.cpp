#include "yukon.h"

#include <iostream>

#include <boost/thread/thread.hpp>
#include <boost/bind.hpp>
#include <boost/function.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/ptr_container/ptr_vector.hpp>

#include "PlcInfrastructure.h"
#include "Ccu710.h"
#include "Ccu711.h"
#include "PortLogger.h"
#include "CommInterface.h"
#include "BehaviorCollection.h"
#include "DelayBehavior.h"
#include "cparms.h"

#include "boostutil.h"

#include "dllbase.h"
#include "ctinexus.h"
#include "ctitime.h"
#include "logger.h"

BOOL WINAPI CtrlHandler(DWORD fdwCtrlType);

PROJECT_ID("CCU Simulator");

bool gQuit = false;

using namespace std;
using namespace boost;

DLLIMPORT extern CtiLogger dout;
namespace Cti {
namespace Simulator {

PlcInfrastructure Grid;

int SimulatorMainFunction(int argc, char **argv);
void CcuPortMaintainer(int portNumber, int strategy);
void CcuPort(int portNumber, int strategy);
void startRequestHandler(CTINEXUS &mySocket, int strategy, PortLogger &logger);
template<class CcuType>
void handleRequests(SocketComms &socket_interface, int strategy, PortLogger &logger);

int SimulatorMainFunction(int argc, char **argv)
{
    srand(time(NULL));

    // Apparently the first random needs to be dumped or else
    // the random number will be some linear function as 
    // opposed to being truly (or rather pseudo-) random.
    rand();

    int strategy = 0,
        port_min = 0,
        port_max = 0;

    switch( argc )
    {
        case 4:  strategy = atoi(argv[3]);
        case 3:  port_max = atoi(argv[2]);
        case 2:  port_min = atoi(argv[1]);  break;

        default:
        {
            cout << "Usage:  ccu_simulator.exe <min_port> [max_port] [strategy #]" << endl;

            exit(-1);
        }
    }

    if( port_max && port_min > port_max )
    {
        cout << "Invalid port range [" << port_min << " - " << port_max << "]" << endl;

        exit(-1);
    }

    //  We need to catch ctrl-c so we can stop
    if( !SetConsoleCtrlHandler(CtrlHandler,  TRUE) )
    {
        cout << "Could not install control handler" << endl;

        exit(-1);
    }

    port_max = max(port_max, port_min);

    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory);
    dout.setOutputFile("ccu_simulator");
    dout.setToStdOut(true);
    dout.setWriteInterval(1000);

    identifyProject(CompileInfo);
    setConsoleTitle(CompileInfo);

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Port range [" << port_min << " - " << port_max << "], strategy " << strategy << endl;
    }

    //  start up Windows Sockets
    WSADATA wsaData;
    WSAStartup(MAKEWORD (1,1), &wsaData);

    thread_group threadGroup;

    for( ; port_min <= port_max; ++port_min )
    {
        threadGroup.add_thread(new thread(bind(Cti::Simulator::CcuPortMaintainer, port_min, strategy)));
    }

    threadGroup.join_all();

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " " << CompileInfo.project << " exiting" << endl;
    }

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    return 0;
}

void CcuPortMaintainer(int portNumber, int strategy)
{
    while( !gQuit )
    {
        thread portThread(bind(CcuPort, portNumber, strategy));
        portThread.join();
    }
}


void CcuPort(int portNumber, int strategy)
{
    CTINEXUS listenSocket, newSocket;

    int loops;

    PortLogger logger(dout, portNumber);

    for( loops = 0; listenSocket.CTINexusCreate(portNumber) && !gQuit; ++loops )
    {
        //  print every 10 seconds
        if( !(loops % 10) )
        {
            logger.log("Cannot create listener");
        }

        //  try once every second
        Sleep(1000);
    }

    for( loops = 0; !newSocket.CTINexusValid() && !gQuit; ++loops )
    {
        //  print every 10 seconds
        if( !(loops % 10) )
        {
            logger.log("Listening for connection");
        }

        //  wait to connect for 1 second
        listenSocket.CTINexusConnect(&newSocket, NULL, 1000);
    }

    //  done with the listener
    listenSocket.CTINexusClose();

    if( newSocket.CTINexusValid() )
    {
        logger.log("Accepted connection");

        startRequestHandler(newSocket, strategy, logger);
    }

    logger.log("Thread exiting");

    newSocket.CTINexusClose();
}


void startRequestHandler(CTINEXUS &mySocket, int strategy, PortLogger &logger)
{
    SocketComms socket_interface(mySocket, 1200);

    if( int chance = gConfigParms.getValueAsInt("SIMULATOR_DELAY_CHANCE_TENTHS_OF_A_PERCENT") )
    {   
        std::auto_ptr<CommsBehavior> d(new DelayBehavior());
        d->setChance(chance);
        socket_interface.setBehavior(d);
    }

    //  both the CCU-710 and CCU-711 have their address info in the first two bytes
    bytes peek_buf;

    //  Peek at the first bytes to determine which CCU type this port will handle.
    while( !gQuit && !socket_interface.peek(byte_appender(peek_buf), 2) )
    {
        Sleep(1000);
    }

    if( !gQuit && !peek_buf.empty() )
    {
        try
        {
            //  This cheat is possible because we know that the 711 has the HDLC flag at the start.
            //
            //  Ideally, before we commit to a type, we should keep looping through the types until
            //    one of them recognizes a valid request on the socket.
            //  That would require anonymous request parsing, which sounds a bit unwieldly compared
            //    to a simple framing flag check.
            //  Life will get more interesting once we have to deal with CCU-721s on the same
            //    port as CCU-711s, though.
            if( peek_buf[0] == Ccu711::Hdlc_FramingFlag )
            {
                handleRequests<Ccu711>(socket_interface, strategy, logger);
            }
            else
            {
                handleRequests<Ccu710>(socket_interface, strategy, logger);
            }
        }
        catch(...)
        {
            logger.log("Uncaught exception");
        }
    }
}


template<class CcuType>
void handleRequests(SocketComms &socket_interface, int strategy, PortLogger &logger)
{
    std::map<int, CcuType *> ccu_list;

    while( !gQuit )
    {
        if( !CcuType::addressAvailable(socket_interface) )
        {
            Sleep(500);
            continue;
        }

        unsigned ccu_address;

        error_t error;

        if( error = CcuType::peekAddress(socket_interface, ccu_address) )
        {
            logger.log("Invalid message received, clearing socket / " + error);

            socket_interface.clear();
        }

        if( ccu_list.find(ccu_address) == ccu_list.end() )
        {
            logger.log("New CCU address received", ccu_address);

            ccu_list.insert(make_pair(ccu_address, new CcuType(ccu_address, strategy)));
        }

        if( !ccu_list[ccu_address]->handleRequest(socket_interface, logger) )
        {
            logger.log("Error while processing message, clearing socket");

            socket_interface.clear();
        }
    }
}


}
}

