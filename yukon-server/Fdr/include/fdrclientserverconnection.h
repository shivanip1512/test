/*
 *
 *    FILE NAME: fdrclientserverconnection.h
 *
 *    History: 
 *     $Log$
 *     Revision 1.4  2007/08/07 21:04:40  mfisher
 *     removed "using namespace std;" from header files
 *
 *     Revision 1.3  2005/12/20 17:17:15  tspar
 *     Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
 *
 *     Revision 1.2  2005/10/28 19:30:55  tmack
 *     Added a health thread to ensure that something is received from the remote system at least once every [link timeout] seconds.
 *
 *     Added a connection number to make it easy to differentiate separate connections from the same remote.
 *
 *     Changed formatting of some debug messages.
 *
 *     Revision 1.1  2005/09/13 20:37:27  tmack
 *     New file for the ACS(MULTI) implementation.
 *
 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */
#ifndef __FDRCLIENTSERVERCONNECTION_H__
#define __FDRCLIENTSERVERCONNECTION_H__

#include <windows.h>
#include "string.h"
#include <rw/thr/thrfunc.h>

#include "dlldefs.h"
#include "queues.h"

class CtiFDRScadaServer;

/** Cti FDR Client Server Connection for Cti FDR Scada Server
 * This is the connection class for the CtiFDRScadaServer class.
 * It should be noted that this class cannot be used directly with
 * CtiFDRSocketServer (the base of CtiFDRScadaServer) because it 
 * relies on specific capabilities of the Scada class. If you need 
 * to create a class that extends from CtiFDRSocketServer directly, 
 * you should refactor this class so that it has a more generic 
 * parent that can server CtiFDRSocketServer more directly.
 */
class IM_EX_FDRBASE CtiFDRClientServerConnection
{                                    
    public:
        CtiFDRClientServerConnection(const string& connectionName, 
                                     SOCKET theSocket,
                                     CtiFDRScadaServer *aParent);
        ~CtiFDRClientServerConnection();
    
        typedef string Destination;
        
        Destination getName() const;
        void setName(Destination serverName);
        int getConnectionNumber() const;
        bool isRegistered ();
        void setRegistered(bool registered);
        bool isFailed() const;
        
        void run();
        void stop();
    
        bool queueMessage(CHAR *aBuffer, 
                          unsigned int bufferSize, 
                          int aPriority);
                          
        
    protected:
        void failConnection();
        ULONG getDebugLevel();
        std::ostream logNow();
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
        SOCKET getRawSocket();
        int writeSocket(CHAR *aBuffer, ULONG length, ULONG &aBytesWritten);
        int readSocket (CHAR *aBuffer, ULONG length, ULONG &aBytesRead);
        
        // variables
        RWThreadFunction _sendThread;
        RWThreadFunction _receiveThread;
        RWThreadFunction _healthThread;
        
        SOCKET _socket;
        HCTIQUEUE _outboundQueue;
        
        CtiFDRScadaServer* _parentInterface;
        
        bool _connectionFailed;
        bool _isRegistered;
        string _connectionName;
        int _connectionNumber;
        static int _nextConnectionNumber;
        
        HANDLE _shutdownEvent;
        HANDLE _stillAliveEvent;
        
        long _linkId;
        string _linkName;
            
    public:
        // exception class
        class StartupException : public exception {
        public:
            StartupException() : exception("FdrClientServerConnection Startup Exception") {}
        };
        
};

inline std::ostream& operator<< (std::ostream& os, const CtiFDRClientServerConnection& conn)
{
    return os << "[connection " << conn.getName() << "#" << conn.getConnectionNumber() << "]";
}



#endif  //  #ifndef __FDRCLIENTSERVERCONNECTION_H__

