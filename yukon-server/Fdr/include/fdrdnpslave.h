
/*
 *    FILE NAME: fdrdnplave.h
 *
 *    DATE: 01/20/2009
 *
 *    AUTHOR: Julie Richter
 *
 *    PURPOSE: Interface implenting DNP Slave Response (class header)
 *
 *    DESCRIPTION: This class implements an interface that exchanges point data
 *                 from an DNP slave device.  The data is status, Analog and counter data.

 *    History:
 *      $Log$
 * *
 *
 *    Copyright (C) 2009 Cooper Power Systems.  All rights reserved.
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
#define FDR_DNP_HEADER_SIZE        10
#define FDR_DNP_REQ_FUNC_LOCATION  12
#define FDR_DNP_DATA_CRC_MARKER    16
#define FDR_DNP_HEADER_BYTE1     0x05
#define FDR_DNP_HEADER_BYTE2     0x64


#define SINGLE_SOCKET_DNP_CONFIRM      0
#define SINGLE_SOCKET_DNP_READ         1
#define SINGLE_SOCKET_DNP_WRITE        2
#define SINGLE_SOCKET_DNP_DIRECT_OP    5
#define SINGLE_SOCKET_DNP_DATALINK_REQ 100

//namespace Cti
//{

using namespace Cti::Protocol;

struct IM_EX_FDRBASE CtiDnpId
{
    USHORT MasterId;
    USHORT SlaveId;
    UINT   PointType;
    USHORT Offset;
    FLOAT  Multiplier;
    BOOL valid;


    CtiFDRClientServerConnection::Destination MasterServerName;
    bool operator<(const CtiDnpId& other) const
    {
        if( MasterServerName < other.MasterServerName )  return true;
        if( MasterServerName > other.MasterServerName )  return false;

        if( MasterId < other.MasterId )  return true;
        if( MasterId > other.MasterId )  return false;

        if( SlaveId < other.SlaveId )  return true;
        if( SlaveId > other.SlaveId )  return false;

        if( PointType < other.PointType )  return true;
        if( PointType > other.PointType )  return false;

        return Offset < other.Offset;
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

        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList);
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize);

        virtual int processMessageFromForeignSystem(CtiFDRClientServerConnection& connection,
                                           char* data, unsigned int size);
        virtual unsigned long getHeaderBytes(const char* data, unsigned int size);

        virtual unsigned int getMagicInitialMsgSize();


    private:
        CtiDnpId    ForeignToYukonId(CtiFDRDestination pointDestination);
        bool        YukonToForeignQuality (USHORT aQuality, CtiTime lastTimeStamp);
        int processScanSlaveRequest (CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size, bool includeTime);
        int processDataLinkConfirmationRequest(CtiFDRClientServerConnection& connection, char* data);

        bool isScanIntegrityRequest(const char* data, unsigned int size);
        void dumpDNPMessage(const string direction, const char* data, unsigned int size);

        DNPSlaveInterface  _dnpData;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;
        int _staleDataTimeOut;

        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_FDR_DNPSLAVE_SERVER_NAMES;
        static const CHAR * KEY_LINK_TIMEOUT;
        static const CHAR * KEY_STALEDATA_TIMEOUT;

        static const string dnpMasterId;
        static const string dnpSlaveId;
        static const string dnpPointType;
        static const string dnpPointOffset;
        static const string dnpPointStatusString;
        static const string dnpPointAnalogString;
        static const string dnpPointCalcAnalogString;
        static const string dnpPointCounterString;
        static const string dnpPointMultiplier;

        static const string CtiFdrDNPInMessageString;
        static const string CtiFdrDNPOutMessageString;

    public:

};


#endif  //  #ifndef __FDRDNPSLAVE_H__

