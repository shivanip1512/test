/*
 *
 *    FILE NAME: fdrclientserverconnection.h
 *
 *    History: 
 *     $Log$
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
#include <rw/cstring.h>
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
        CtiFDRClientServerConnection(const RWCString& connectionName, 
                                     SOCKET theSocket,
                                     CtiFDRScadaServer *aParent);
        ~CtiFDRClientServerConnection();
    
        typedef RWCString Destination;
        
        Destination getName(void) const;
        void setName(Destination serverName);
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
        ostream logNow();
        void sendLinkState(bool linkUp);
    
    private:
        
        //NOTE: Don't you dare make any of these public/protected!
        //      If you need to modify their behavior, decompose them using 
        //      the Template Method pattern or something.
        void threadFunctionSendDataTo( void );
        void threadFunctionGetDataFrom( void );
    
        
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
        
        SOCKET _socket;
        HCTIQUEUE _outboundQueue;
        
        CtiFDRScadaServer* _parentInterface;
        
        bool _connectionFailed;
        bool _isRegistered;
        RWCString _connectionName;
        
        HANDLE _shutdownEvent;
        
        long _linkId;
        RWCString _linkName;
            
    public:
        // exception class
        class StartupException : public exception {
        public:
            StartupException() : exception("FdrClientServerConnection Startup Exception") {}
        };
        
};

inline std::ostream& operator<< (std::ostream& os, const CtiFDRClientServerConnection& conn)
{
    return os << "[connection " << conn.getName() << "]";
}



#endif  //  #ifndef __FDRCLIENTSERVERCONNECTION_H__

