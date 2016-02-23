#pragma once

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
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDRScadaServer(std::string &);
        virtual ~CtiFDRScadaServer();

        virtual int processMessageFromForeignSystem(
          Cti::Fdr::ServerConnection& connection, const char* data, unsigned int size);
        virtual unsigned int getMessageSize(const char* data) = 0;
        unsigned long getScadaFunction(const char* data, unsigned int size);
        bool readConfig();
        //  virtual unsigned int getHeaderLength()=0;  //  implied

    protected:

        virtual int processScanMessage(CtiFDRClientServerConnection* connection, const char *data) {return false;};

        virtual bool processValueMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size) {return false;};
        virtual bool processStatusMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size) {return false;};
        virtual bool processControlMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size) {return false;};
        virtual bool processRegistrationMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size) {return false;};
        virtual bool processTimeSyncMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size) {return false;};

};
