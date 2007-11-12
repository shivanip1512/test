/*

 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */
#ifndef __FDRSCADASERVER_H__
#define __FDRSCADASERVER_H__

#include <windows.h>

#include "dlldefs.h"
#include "fdrsocketserver.h"

#define SINGLE_SOCKET_NULL               0
#define SINGLE_SOCKET_REGISTRATION       1
#define SINGLE_SOCKET_ACKNOWLEDGEMENT    2
#define SINGLE_SOCKET_VALUE            101
#define SINGLE_SOCKET_STATUS           102
#define SINGLE_SOCKET_VALMET_CONTROL   103 // arrgh, backward compatibility
#define SINGLE_SOCKET_CONTROL          201
#define SINGLE_SOCKET_FORCESCAN        110
#define SINGLE_SOCKET_TIMESYNC         401
#define SINGLE_SOCKET_STRATEGY         501
#define SINGLE_SOCKET_STRATEGYSTOP     503


class IM_EX_FDRBASE CtiFDRScadaServer : public CtiFDRSocketServer
{

    public:
        // constructors and destructors
        CtiFDRScadaServer(string &);
        virtual ~CtiFDRScadaServer();

        virtual int processMessageFromForeignSystem(
          CtiFDRClientServerConnection& connection, char* data, unsigned int size);
        virtual unsigned int getMessageSize(unsigned long header) = 0;
        virtual unsigned long getHeaderBytes(const char* data, unsigned int size);

    protected:

        virtual bool processValueMessage(CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size) {return false;};
        virtual bool processStatusMessage(CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size) {return false;};
        virtual bool processControlMessage(CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size) {return false;};
        virtual bool processRegistrationMessage(CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size) {return false;};
        virtual bool processTimeSyncMessage(CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size) {return false;};

};


#endif  //  #ifndef __FDRSCADASERVER_H__


