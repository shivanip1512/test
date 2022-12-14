#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "serverconnection.h"

#include <windows.h>
#include "string.h"
#include "worker_thread.h"
#include "utility.h"

class CtiFDRScadaServer;
class CtiFDRSocketServer;

/** Cti FDR Client Server Connection for Cti FDR Scada Server
 * This is the connection class for the CtiFDRScadaServer class.
 * It should be noted that this class cannot be used directly with
 * CtiFDRSocketServer (the base of CtiFDRScadaServer) because it
 * relies on specific capabilities of the Scada class. If you need
 * to create a class that extends from CtiFDRSocketServer directly,
 * you should refactor this class so that it has a more generic
 * parent that can server CtiFDRSocketServer more directly.
 */
class IM_EX_FDRBASE CtiFDRClientServerConnection : public Cti::Fdr::ServerConnection
{
    public:
        DEBUG_INSTRUMENTATION;

        CtiFDRClientServerConnection(const std::string& connectionName,
                                     SOCKET theSocket,
                                     CtiFDRSocketServer *aParent);
        ~CtiFDRClientServerConnection();

        typedef std::string Destination;

        Destination getName() const;
        void setName(Destination serverName);
        int getConnectionNumber() const;
        bool isRegistered ();
        void setRegistered(bool registered);
        bool isFailed() const;

        void run();
        void stop();

        bool queueMessage(char *aBuffer, 
                          unsigned int bufferSize, 
                          int aPriority) override;

        int getPortNumber();

    protected:
        void failConnection();
        ULONG getDebugLevel();
        std::string logNow();
        void sendLinkState(bool linkUp);

    private:

        //NOTE: Don't you dare make any of these public/protected!
        //      If you need to modify their behavior, decompose them using
        //      the Template Method pattern or something.
        void threadFunctionSendDataTo( void );
        void threadFunctionGetDataFrom( void );
        void threadFunctionHealth( void );


        //NOTE: Don't you dare make any of these public/protected!
        //      If you need access to the raw socket or wish to directly
        //      read/write on the socket, make a super class of this class that
        //      has the bare representations of a socket, and extend it to suit
        //      your needs.
        int writeSocket(CHAR *aBuffer, ULONG length, ULONG &aBytesWritten);
        int readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead);

        // variables
        Cti::WorkerThread _sendThread;
        Cti::WorkerThread _receiveThread;
        Cti::WorkerThread _healthThread;

        SOCKET _socket;
        HCTIQUEUE _outboundQueue;

        CtiFDRSocketServer* _parentInterface;

        bool _connectionFailed;
        bool _isRegistered;
        std::string _connectionName;
        int _connectionNumber;
        static std::atomic_int _nextConnectionNumber;

        HANDLE _shutdownEvent;
        HANDLE _stillAliveEvent;

        long _linkId;
        std::string _linkName;

    public:
        // exception class
        class StartupException : public std::exception {
        public:
            StartupException() : exception("FdrClientServerConnection Startup Exception") {}
        };

};

typedef boost::shared_ptr<CtiFDRClientServerConnection> CtiFDRClientServerConnectionSPtr;

