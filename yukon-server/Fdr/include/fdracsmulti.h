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
 *      Revision 1.7  2008/09/23 15:15:22  tspar
 *      YUK-5013 Full FDR reload should not happen with every point db change
 *
 *      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.
 *
 *      Revision 1.6  2008/09/15 21:09:16  tspar
 *      YUK-5013 Full FDR reload should not happen with every point db change
 *
 *      Changed interfaces to handle points on an individual basis so they can be added
 *      and removed by point id.
 *
 *      Changed the fdr point manager to use smart pointers to help make this transition possible.
 *
 *      Revision 1.5  2007/11/12 16:46:55  mfisher
 *      Removed some Rogue Wave includes
 *
 *      Revision 1.4  2007/05/11 19:20:27  tspar
 *      missed file
 *
 *      Revision 1.3  2005/12/20 17:17:15  tspar
 *      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
 *
 *      Revision 1.2  2005/10/28 19:27:01  tmack
 *      Added a configuration parameter to set the link timeout value.
 *
 *      Revision 1.1  2005/09/13 20:37:27  tmack
 *      New file for the ACS(MULTI) implementation.
 *
 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */

#ifndef __FDRACS_H__
#define __FDRACS_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include <map>

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "device.h"             // get the raw states
#include "fdrscadaserver.h"
#include "fdrscadahelper.h"

// global defines
#define ACS_PORTNUMBER      1668


/* Definitions and structures used to share data with ACS */

// ACS quality codes
#define ACS_NORMAL              0x0001      // channel up good reading
#define ACS_MANUALENTRY         0x0002      // operator entered
#define ACS_PLUGGED             0x0004      // channel down
/*

NOTE:  All data limit violations will be handled by the receiving system
*/

/*
NOTE:  Decision was made to use function 201 (ACS_CONTROL) as a way of starting and
        stopping control by strategy and also by group (as opposed to functions 503/501).
        Differentation between these and regular control are to be implemented on the
        DSM2 side by using the naming convention @C_ in the device name

        ACS_OPEN    =   stop strategy   =   restore load group
        ACS_CLOSED  =   start strategy  =   Shed load group
*/

/*
Naming conventions for points are as follows:

    RrrrrCcPxxxx        where
                            rrrr = remote number
                            c     = category
                            pppp = point number

    Current valid categories are:

      R    remote
        P    pseudo
        C    calculated
        D    diagnostic

        A    accumulator
*/

// error codes for ACS
// NOTE:  Currently, they treat all errors as no replies DLS 8 Apr 99
#define ACS_ERRROR_NOREPLY          31
#define ACS_ERRROR_SEQUENCE         32
#define ACS_ERRROR_FRAME            33
#define ACS_ERRROR_BADCRC           34
#define ACS_ERRROR_BADLENGTH        35
#define ACS_ERRROR_UNKNOWN          37
#define ACS_ERRROR_DSM2_DATABASE    53
#define ACS_ERRROR_RTU_DISABLED     78
#define ACS_ERRROR_PORT_DISABLED    83

#pragma pack(push, acs_packing, 1)

struct IM_EX_FDRBASE CtiAcsId
{
    USHORT RemoteNumber;
    USHORT PointNumber;
    CHAR CategoryCode;
    CtiFDRClientServerConnection::Destination ServerName;
    bool operator<(const CtiAcsId& other) const
    {
        if (ServerName == other.ServerName)
        {
            if (CategoryCode == other.CategoryCode)
            {
                if (RemoteNumber == other.RemoteNumber)
                {
                    return PointNumber < other.PointNumber;
                }
                else
                {
                    return RemoteNumber < other.RemoteNumber;
                }
            }
            else
            {
                return CategoryCode < other.CategoryCode;
            }
        }
        else
        {
            return ServerName < other.ServerName;
        }
    }

};

inline std::ostream& operator<< (std::ostream& os, const CtiAcsId& id)
{
    return os << "[ACS: R=" << id.RemoteNumber << ", P="
        << id.PointNumber << ", C=" << id.CategoryCode
        << ", S=" << id.ServerName << "]";
}


// Structure for messages to and from ACS
typedef struct {
    USHORT Function;
    CHAR TimeStamp[16];
    union {
        /* Null Message     Function = 0 */

        /* Value Message   Function = 101 */
        struct {
            USHORT RemoteNumber;
            USHORT PointNumber;
            CHAR CategoryCode;
            CHAR Spare;
            union {
                FLOAT Value;
                ULONG LongValue;
            };
            USHORT Quality;
        } Value;

        /* Status Message   Function = 102 */
        struct {
            USHORT RemoteNumber;
            USHORT PointNumber;
            CHAR CategoryCode;
            CHAR Spare;
            USHORT Value;
            USHORT Quality;
        } Status;

        /* Control Message   Function = 103 */
        struct {
            USHORT RemoteNumber;
            USHORT PointNumber;
            CHAR CategoryCode;
            CHAR Spare;
            USHORT Value;
        } Control;
    };

    /* Force long alignment */
//    CHAR Spare;

} ACSInterface_t;

#pragma pack(pop, acs_packing)     // Restore the prior packing alignment..

class CtiTime;

class IM_EX_FDRACSMULTI CtiFDRAcsMulti : public CtiFDRScadaServer
{
    public:
        // helper structs


        // constructors and destructors
        CtiFDRAcsMulti();

        virtual ~CtiFDRAcsMulti();

        virtual unsigned int getMessageSize(unsigned long header);

        virtual int readConfig( void );

        enum {ACS_Open = 0, ACS_Closed = 1, ACS_Invalid=99};

        void startup();

    protected:
        virtual CtiFDRClientServerConnection* createNewConnection(SOCKET newConnection);

        virtual void begineNewPoints();
        virtual bool translateSinglePoint(CtiFDRPointSPtr translationPoint, bool send);
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr translationPoint, bool recvList);

        virtual bool buildForeignSystemHeartbeatMsg(char** buffer,
                                                    unsigned int& bufferSize);
        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize);

        virtual bool processValueMessage(CtiFDRClientServerConnection& connection,
                                         char* data, unsigned int size);
        virtual bool processStatusMessage(CtiFDRClientServerConnection& connection,
                                          char* data, unsigned int size);
        virtual bool processControlMessage(CtiFDRClientServerConnection& connection,
                                           char* data, unsigned int size);
        virtual bool processTimeSyncMessage(CtiFDRClientServerConnection& connection,
                                            char* data, unsigned int size);

    private:
        CtiAcsId    ForeignToYukonId(USHORT remote, CHAR category, USHORT point,
                                     CtiFDRClientServerConnection::Destination serverName);
        USHORT      ForeignToYukonQuality (USHORT aQuality);
        int         ForeignToYukonStatus (USHORT aStatus);
        CtiTime     ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag = false);

        string      YukonToForeignTime (CtiTime aTimeStamp);
        int         YukonToForeignId (string aPointName, USHORT &remoteNumber, CHAR &category, USHORT &pointNumber);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        USHORT      YukonToForeignStatus (int aStatus);

        CtiFDRScadaHelper<CtiAcsId>* _helper;
        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;

        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_OUTBOUND_SEND_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_INTERVAL;
        static const CHAR * KEY_TIMESYNC_UPDATE;
        static const CHAR * KEY_TIMESYNC_VARIATION;
        static const CHAR * KEY_POINT_TIME_VARIATION;
        static const CHAR * KEY_FDR_ACS_SERVER_NAMES;
        static const CHAR * KEY_LINK_TIMEOUT;

    public:

};


#endif  //  #ifndef __FDRACS_H__

