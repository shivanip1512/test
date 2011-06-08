#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   fdrcygnet
*
*    DATE: 10/01/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.9.2.2 $
* DATE         :  $Date: 2008/11/18 20:11:30 $
*    DESCRIPTION: This class implements an interface that retrieves point data
*                 from a Foreign System.  The data is status and Analog data.
*                 This interface only receives at this time.  It links with
*                 a Cygnet library provided by Visual Systems.  The library
*                 and headers provide access to their API.
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __FDRCYGNET_H__
#define __FDRCYGNET_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"

class IM_EX_FDRCYGNET CtiFDRCygnet : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

    public:
        // constructors and destructors
        CtiFDRCygnet();

        virtual ~CtiFDRCygnet();

        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );
        virtual int processMessageFromForeignSystem (CHAR *data);

        virtual BOOL    init( void );
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        enum ConnectState
        {
            CSTATE_NOT_INSTALLED = 0,
            CSTATE_NORMAL,
            CSTATE_FAILED
        };

        // start getters and setters
        ULONG           getScanRateSeconds() const;
        void            setScanRateSeconds(const ULONG mySeconds);

        std::string       getAnalogServiceName() const;
        std::string       getStatusServiceName() const;

        ConnectState    getAnalogServiceState() const;
        CtiFDRCygnet &  setAnalogServiceState(const ConnectState myCState);

        ConnectState    getStatusServiceState() const;
        CtiFDRCygnet &  setStatusServiceState(const ConnectState myCState);

        double          getHiReasonabilityFilter() const;
        CtiFDRCygnet &  setHiReasonabilityFilter(const double myValue);

        // end getters and setters
        static const CHAR * KEY_SCAN_RATE_SECONDS;
        static const CHAR * KEY_ANALOG_SERVICE_NAME;
        static const CHAR * KEY_STATUS_SERVICE_NAME;
        static const CHAR * KEY_HI_REASONABILITY_FILTER;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_DB_RELOAD_RATE;

    protected:
        RWThreadFunction    iThreadGetCygnetData;

        void    threadFunctionGetDataFromCygnet( void );

        bool    connectToAnalogService(void);
        bool    connectToStatusService(void);

        bool    retreiveAnalogPoints(void);
        bool    retreiveStatusPoints(void);

        bool    loadTranslationLists(void);
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);
        bool    loadLists(CtiFDRPointList &aList);

        ULONG   calculateNextSendTime();

        int     readConfig( void );

        enum {Cygnet_Open = 0, Cygnet_Closed = 1};

        // this should be from the base Yukon but there are problems...
        enum {Yukon_Open = OPENED,  Yukon_Closed = CLOSED};

    private:
        ULONG               iScanRateSeconds;
        std::string           iAnalogServiceName;
        std::string           iStatusServiceName;
        ConnectState        iAnalogServiceState;
        ConnectState        iStatusServiceState;
        double              iHiReasonabilityFilter;


};


#endif  //  #ifndef __FDRCYGNET_H__
