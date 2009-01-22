
/*
 *    FILE NAME: fdracs.h
 *
 *    DATE: 03/07/2001
 *
 *    AUTHOR: David Sutton
 *
 *    PURPOSE: Interface to the ACS scada systemm (class header)
 *
 *    DESCRIPTION: This class implements an interface that exchanges point data
 *                 from an ACS scada system.  The data is both status and Analog data.
 *                 Information is exchanged using sockets opened on a predefined socket
 *                 number and also pre-defined messages between the systems.  See the
 *                 design document for more information
 *    History:
 *      $Log$
 * *
 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */

#ifndef __FDRDNPSLAVE_H__
#define __FDRDNPSLAVE_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include <map>

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "fdrdnphelper.h"
#include "dnp_object_analoginput.h"
#include "prot_dnp.h"

// global defines
#define DNPSLAVE_PORTNUMBER      2085
#define FDR_DNP_HEADER_SIZE      12
#define FDR_DNP_HEADER_BYTE1     0x05
#define FDR_DNP_HEADER_BYTE2     0x64


#define SINGLE_SOCKET_DNP_CONFIRM      0
#define SINGLE_SOCKET_DNP_READ         1
#define SINGLE_SOCKET_DNP_WRITE        2
#define SINGLE_SOCKET_DNP_DIRECT_OP    5

//namespace Cti
//{

using namespace Cti::Protocol;

struct IM_EX_FDRBASE CtiDnpId
{
    USHORT MasterId;
    USHORT SlaveId;
    UINT   PointType;
    USHORT Offset;
    BOOL valid;
    

    CtiFDRClientServerConnection::Destination MasterServerName;
    bool operator<(const CtiDnpId& other) const
    {
        if (MasterServerName == other.MasterServerName)
        {
            if (MasterId == other.MasterId)
            {
                if (SlaveId == other.SlaveId)
                {
                    if (PointType == other.PointType)
                    {
                        return Offset < other.Offset;
                    }
                    else
                    {
                        return PointType < other.PointType;
                    }
                }
                else
                {
                    return SlaveId < other.SlaveId;
                }
            }
            else
            {
                return MasterId < other.MasterId;
            }
        }
        else
        {
            return MasterServerName < other.MasterServerName;
        }
    }

};
inline std::ostream& operator<< (std::ostream& os, const CtiDnpId& id)
{ 
    return os << "[DNP: Master= "<< id.MasterServerName <<", M=" << id.MasterId << ", S="
        << id.SlaveId << ", P=" << id.PointType
        << ", O=" << id.Offset << "]";
}


class IM_EX_FDRDNPSLAVE CtiFDRDnpSlave : public CtiFDRSocketServer
{
    public:
        // helper structs
        CtiFDRDNPHelper<CtiDnpId>* _helper;


        // constructors and destructors
        CtiFDRDnpSlave();

        virtual ~CtiFDRDnpSlave();

        virtual unsigned int getMessageSize(const char* data);

        virtual int readConfig( void );

        void startup();

    protected:
        virtual CtiFDRClientServerConnection* createNewConnection(SOCKET newConnection);

        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool send);
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

        /*virtual bool buildForeignSystemHeartbeatMsg(char** buffer,
                                                    unsigned int& bufferSize);
        */
        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize);

        virtual int processMessageFromForeignSystem(CtiFDRClientServerConnection& connection,
                                           char* data, unsigned int size);
        virtual unsigned long getHeaderBytes(const char* data, unsigned int size);

        virtual unsigned int getMagicInitialMsgSize(){return FDR_DNP_HEADER_SIZE;};


    private:
        CtiDnpId    ForeignToYukonId(CtiFDRDestination pointDestination);
        bool        YukonToForeignQuality (USHORT aQuality);
        int processScanSlaveRequest (CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size);

        DNPSlaveInterface  _dnpData;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;

        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_FDR_DNPSLAVE_SERVER_NAMES;
        static const CHAR * KEY_LINK_TIMEOUT;

        static const string dnpMasterId;    
        static const string dnpSlaveId;     
        static const string dnpPointType;   
        static const string dnpPointOffset; 
        static const string dnpPointStatusString;
        static const string dnpPointAnalogString;
        static const string dnpPointCounterString;

    public:

};


#endif  //  #ifndef __FDRDNPSLAVE_H__

