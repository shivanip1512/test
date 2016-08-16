#include "precompiled.h"

#include "SimulatorUtils.h"
#include "Grid.h"
#include "Ccu711.h"
#include "Ccu721.h"
#include "DelayBehavior.h"
#include "BchBehavior.h"
#include "NackBehavior.h"
#include "cparms.h"
#include "StreamSocketListener.h"
#include "module_util.h"
#include "database_reader.h"

#include "E2eSimulator.h"

#include <boost/filesystem.hpp>
#include <boost/thread.hpp>

BOOL WINAPI CtrlHandler(DWORD fdwCtrlType);

PROJECT_ID("CCU Simulator");

bool gQuit = false;
HANDLE gQuitEvent = NULL;

using namespace std;
using namespace boost;

using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;
using Cti::Timing::Chrono;

namespace Cti {
namespace Simulator {

int SimulatorMainFunction(int argc, char **argv);
void loadGloabalSimulatorBehaviors(Logger &logger);
bool confirmCcu710(int ccu_address, int portNumber, Logger &logger);
bool getPorts(vector<int> &ports);
void CcuPortMaintainer(int portNumber, int strategy);
void CcuPort(int portNumber, int strategy);
void startRequestHandler(StreamSocketConnection &mySocket, int strategy, int portNumber, Logger &logger);
void handleRequests(SocketComms &socket_interface, int strategy, int portNumber, Logger &logger);
template<class CcuType>
bool validRequest(SocketComms &socket_interface);

int SimulatorMainFunction(int argc, char **argv)
{
    int strategy = 0,
        port_min = 0,
        port_max = 0;

    vector<int> portList;

    switch( argc )
    {
        case 4:  strategy = atoi(argv[3]);
        case 3:  port_max = atoi(argv[2]);
        case 2:  port_min = atoi(argv[1]);  break;

        default:
        {
            port_min = gConfigParms.getValueAsInt("SIMULATOR_INIT_PORT_MIN");
            port_max = gConfigParms.getValueAsInt("SIMULATOR_INIT_PORT_MAX");

            if( port_min && port_max )
            {
                cout << "Loading ports from master.cfg file: Port range [" << port_min << " - " << port_max << "]" << endl;
                break;
            }
            else if (getPorts(portList))
            {
                cout << "Loaded " << portList.size() << " ports from the database." << endl;
                break;
            }
            else
            {
                cout << "Unable to retrieve port values.\n";
                cout << "Command-line usage:  ccu_simulator.exe <min_port> [max_port] [strategy #]" << endl;
                cout << "master.cfg file usage: SIMULATOR_INIT_PORT_MIN :[min_port]" << endl;
                cout << "                       SIMULATOR_INIT_PORT_MAX :[max_port]" << endl;
                exit(-1);
            }
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

    SimulatorLogger logger(dout);

    Cti::identifyProject(CompileInfo);

    if( port_min && port_max )
    {
        logger.log("Port range [" + CtiNumStr(port_min) + " - " + CtiNumStr(port_max) + "], strategy " + CtiNumStr(strategy));
    }

    //  start up Windows Sockets
    WSADATA wsaData;
    WSAStartup(MAKEWORD (1,1), &wsaData);

    loadGloabalSimulatorBehaviors(logger);

    // Load up the MCT behaviors here as well.
    Mct410Sim::initBehaviors(logger);

    const wstring wideMemoryMapDirectory(
        DeviceMemoryManager::memoryMapDirectory.begin(),
        DeviceMemoryManager::memoryMapDirectory.end());

    filesystem::path mctFilePath( wideMemoryMapDirectory );

    // Create the directory if necessary.
    if( filesystem::create_directory( mctFilePath ) )
    {
        logger.log("Directory " + DeviceMemoryManager::memoryMapDirectory + " has been created for simulator MCT memory maps.");
    }
    else
    {
        logger.log("Directory " + DeviceMemoryManager::memoryMapDirectory + " already exists.");
    }

    thread_group threadGroup;

    if( portList.empty() )
    {
        for( ; port_min <= port_max; ++port_min )
        {
            threadGroup.add_thread(new thread(bind(Cti::Simulator::CcuPortMaintainer, port_min, strategy)));
        }
    }
    else
    {
        for each (int port in portList )
        {
            threadGroup.add_thread(new thread(bind(Cti::Simulator::CcuPortMaintainer, port, strategy)));
        }
    }

    std::unique_ptr<E2eSimulator> e2eSimulator;

    if( gConfigParms.isTrue("SIMULATOR_RFN_E2E", false) )
    {
        e2eSimulator = std::make_unique<E2eSimulator>();

        logger.log("RFN E2E simulator started.");
    }

    threadGroup.join_all();

    if( e2eSimulator )
    {
        e2eSimulator->stop();
    }

    logger.log(string(CompileInfo.project) + " exiting");

    return 0;
}

void loadGloabalSimulatorBehaviors(Logger &logger)
{
    if( double chance = gConfigParms.getValueAsDouble("SIMULATOR_PLC_BEHAVIOR_BCH_ERROR_PROBABILITY") )
    {
        logger.log("BCH Behavior Enabled - Probability " + CtiNumStr(chance, 2) + "%");
        Grid.setBehavior(std::make_unique<BchBehavior>(chance));
    }
    if( double chance = gConfigParms.getValueAsDouble("SIMULATOR_PLC_BEHAVIOR_NACK_ERROR_PROBABILITY") )
    {
        logger.log("NACK Behavior Enabled - Probability " + CtiNumStr(chance, 2) + "%");
        Grid.setBehavior(std::make_unique<NackBehavior>(chance));
    }
}

bool confirmCcu710(int ccu_address, int portNumber, Logger &logger)
{
    static const string Ccu710AType = "CCU-710A";
    stringstream ss_addr, ss_port;
    ss_addr << ccu_address;
    ss_port << portNumber;
    const string sql = "SELECT PAO.Type "
                       "FROM YukonPAObject PAO "
                         "JOIN DeviceIdlcRemote DIR ON PAO.PAObjectId = DIR.DeviceId "
                         "JOIN DeviceDirectCommSettings DDCS ON PAO.PAObjectID = DDCS.DEVICEID "
                         "JOIN PORTTERMINALSERVER PTS ON DDCS.PORTID = PTS.PORTID "
                       "WHERE DIR.Address = " + ss_addr.str() + " " +
                         "AND PTS.SocketPortNumber = " + ss_port.str();

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr.execute();

    if( rdr() )
    {
        string type;
        rdr["Type"] >> type;
        return (strcmp(type.c_str(), Ccu710AType.c_str()) == 0);
    }
    else
    {
        // If we have a DB connection, no results came from the query, so return false.
        // If we don't have a DB connection, just return true.
        return !connection.isValid();
    }
}

bool getPorts(vector<int> &ports)
{
    static const string sql =
        "SELECT"
            " Distinct P.SOCKETPORTNUMBER"
        " FROM"
            " YukonPAObject Ccu"
            " join DeviceDirectCommSettings D on Ccu.PAObjectID = D.DEVICEID"
            " join YukonPAObject CcuPort on CcuPort.PAObjectID = D.PORTID"
            " join PORTTERMINALSERVER P on CcuPort.PAObjectID = P.PORTID"
        " WHERE"
            " CCU.PAOClass = 'TRANSMITTER'"
            " AND (P.IPADDRESS = '127.0.0.1' OR P.IPADDRESS = 'localhost')"
            " AND Ccu.Type like 'CCU%'"
            " AND CcuPort.DisableFlag <> 'Y'"
            " AND Ccu.DisableFlag <> 'Y'";

    unsigned port;

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();

    while( rdr() )
    {
        rdr["SOCKETPORTNUMBER"] >> port;
        ports.push_back(port);
    }

    return !ports.empty();
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
    StreamSocketListener listenSocket;

    PortLogger logger(dout, portNumber);

    for( int loops = 0; ! listenSocket.create(portNumber) && ! gQuit; loops++ )
    {
        //  print every 10 seconds
        if( !(loops % 10) )
        {
            logger.log("Cannot create listener");
        }

        //  try once every second
        Sleep(1000);
    }

    std::auto_ptr<StreamSocketConnection> newSocket;

    for( int loops = 0; ! newSocket.get() && ! gQuit; loops++ )
    {
        //  print every 10 seconds
        logger.log("Listening for connection");

        //  wait to connect for 10 second
        newSocket = listenSocket.accept(StreamSocketConnection::ReadExactly, Chrono::seconds(10), &gQuitEvent);
    }

    //  done with the listener
    listenSocket.close();

    if( newSocket.get() && ! gQuit )
    {
        logger.log("Accepted connection");

        startRequestHandler(*newSocket, strategy, portNumber, logger);
    }

    logger.log("Thread exiting");
}

void startRequestHandler(StreamSocketConnection &mySocket, int strategy, int portNumber, Logger &logger)
{
    try
    {
        CtiTime now;

        srand(now.seconds());

        // First rand is apparently linear. To avoid undesired predictability while running
        // the simulator, we should dump the first call here.
        rand();

        SocketComms socket_interface(mySocket, 1200);

        // Check for behaviors that may be used during the simulator runtime.
        if( double chance = gConfigParms.getValueAsDouble("SIMULATOR_COMMS_DELAY_PROBABILITY") )
        {
            logger.log("Delay Behavior Enabled - Probability " + CtiNumStr(chance, 2) + "%");

            socket_interface.setBehavior(
                    std::make_unique<DelayBehavior>(chance));
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
            handleRequests(socket_interface, strategy, portNumber, logger);
        }
    }
    catch( const StreamConnectionException &ex )
    {
        logger.log( string("Caught StreamConnectionException: ") + ex.what() );
    }
    catch(...)
    {
        logger.log("Caught unhandled exception:\n" + Cti::Logging::getUnknownExceptionCause());
    }
}

void handleRequests(SocketComms &socket_interface, int strategy, int portNumber, Logger &logger)
{
    std::map<int, PlcTransmitter *> ccu_list;
    bytes peek_buf;
    error_t error;
    unsigned ccu_address;

    while( !gQuit )
    {
        // What do we do here?
        ScopedLogger scope = logger.getNewScope("");

        peek_buf.clear();
        socket_interface.peek(byte_appender(peek_buf), 2);

        if( !peek_buf.empty() )
        {
            if( isDnpHeader(peek_buf) )
            {
                scope.log("DNP message obtained, clearing the socket.");
                scope.log("Please remove the CBC/RTU from this comm channel to eliminate this message.");
                socket_interface.clear();
            }
            else
            {
                if( peek_buf[0] == CcuIDLC::Hdlc_FramingFlag )
                {
                    // Device is either a 721 or a 711.
                    if( !CcuIDLC::addressAvailable(socket_interface) )
                    {
                        Sleep(500);
                        continue;
                    }

                    if( error = CcuIDLC::peekAddress(socket_interface, ccu_address) )
                    {
                        scope.log("Invalid message received, clearing socket / " + error);

                        socket_interface.clear();
                    }

                    if( ccu_list.find(ccu_address) == ccu_list.end() )
                    {
                        stringstream ss_address, ss_port;
                        ss_address << ccu_address;
                        ss_port    << portNumber;
                        // Device is either a 711 or 721, but isn't yet in the ccu_list.
                        // We need to find from the database (if possible) what type of device
                        // this is and create it, then add it to the map.
                        const string sql_Ccu721 =   "SELECT DISTINCT Y.TYPE "
                                                    "FROM YukonPAObject Y, DEVICE V, DeviceAddress A, PORTTERMINALSERVER P, "
                                                        "DeviceDirectCommSettings D "
                                                    "WHERE A.DeviceID = V.DEVICEID AND V.DEVICEID = Y.PAObjectID AND "
                                                        "Y.TYPE = 'CCU-721' AND A.SlaveAddress = " + ss_address.str() + " AND "
                                                        "D.PORTID = P.PORTID AND Y.PAObjectID = D.DEVICEID AND "
                                                        "P.SOCKETPORTNUMBER = " + ss_port.str();

                        const string sql_Ccu711 =   "SELECT DISTINCT Y.TYPE "
                                                    "FROM YukonPAObject Y, DEVICEIDLCREMOTE D, Device V, PORTTERMINALSERVER P, "
                                                        "DeviceDirectCommSettings S "
                                                    "WHERE D.DEVICEID = V.DEVICEID AND V.DEVICEID = Y.PAObjectID AND "
                                                        "Y.TYPE = 'CCU-711' AND D.ADDRESS = " + ss_address.str() + " AND "
                                                        "S.PORTID = P.PORTID AND Y.PAObjectID = S.DEVICEID AND "
                                                        "P.SOCKETPORTNUMBER = " + ss_port.str();

                        Cti::Database::DatabaseConnection connection;
                        Cti::Database::DatabaseReader rdr(connection, sql_Ccu721);

                        rdr.execute();

                        if( rdr() )
                        {
                            // The database query result wasn't empty, so the device SHOULD BE a 721. Check this.
                            string str;
                            rdr["TYPE"] >> str;
                            if( strcmp(str.c_str(), "CCU-721") == 0 )
                            {
                                ccu_list.insert(make_pair(ccu_address, new Ccu721(ccu_address, strategy)));
                            }
                        }
                        else
                        {
                            rdr.setCommandText(sql_Ccu711);
                            rdr.execute();

                            if( rdr() )
                            {
                                string str;
                                rdr["TYPE"] >> str;
                                if( strcmp(str.c_str(), "CCU-711") == 0 )
                                {
                                    ccu_list.insert(make_pair(ccu_address, new Ccu711(ccu_address, strategy)));
                                }
                            }
                            else
                            {
                                // There was a problem in the connection to the database, or the device isn't
                                // in the database. Determine which device to use based on the validateCommand
                                // function.
                                if( validRequest<Ccu721>(socket_interface) )
                                {
                                    ccu_list.insert(make_pair(ccu_address, new Ccu721(ccu_address, strategy)));
                                }
                                else
                                {
                                    ccu_list.insert(make_pair(ccu_address, new Ccu711(ccu_address, strategy)));
                                }
                            }
                        }
                    }
                }
                else
                {
                    // Device is a 710.
                    if( !Ccu710::addressAvailable(socket_interface) )
                    {
                        Sleep(500);
                        continue;
                    }

                    if( error = Ccu710::peekAddress(socket_interface, ccu_address) )
                    {
                        scope.log("Invalid message received, clearing socket / " + error);

                        socket_interface.clear();
                    }

                    if( ccu_list.find(ccu_address) == ccu_list.end() && confirmCcu710(ccu_address, portNumber, scope))
                    {
                        scope.log("New CCU address received", ccu_address);

                        ccu_list.insert(make_pair(ccu_address, new Ccu710(ccu_address, strategy)));
                    }
                }

                if( ccu_list.find(ccu_address) != ccu_list.end() )
                {
                    if( !ccu_list[ccu_address]->handleRequest(socket_interface, scope) )
                    {
                        scope.log("Error while processing message, clearing socket");

                        socket_interface.clear();
                    }
                }
                else
                {
                    // The request was probably garbage. We shouldn't see this often, if ever.
                    scope.log("Invalid message received, clearing socket.");
                    socket_interface.clear();
                }
            }
        }
        else
        {
            Sleep(500);
        }
    }
}

template<class CcuType>
bool validRequest(SocketComms &socket_interface)
{
    bytes peek_buf;

    socket_interface.peek(byte_appender(peek_buf), 5);

    if( peek_buf[2] & 0x01 )
    {
        // Could be valid for either type. Since it's not a general request,
        // it's hard to say whether or not it is for the 711 or 721 decisively.
        return false;
    }
    else
    {
        // It's a general request. We will need to see whether or not the command
        // fits this specific device.
        return (CcuType::validateCommand(socket_interface));
    }
}

}
}
