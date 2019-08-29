#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"

class IM_EX_FDRCYGNET CtiFDRCygnet : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDRCygnet();

        virtual ~CtiFDRCygnet();

        void sendMessageToForeignSys ( CtiMessage *aMessage ) override;
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
        static const CHAR * KEY_DB_RELOAD_RATE;

    protected:
        Cti::WorkerThread   iThreadGetCygnetData;

        void    threadFunctionGetDataFromCygnet( void );

        bool    connectToAnalogService(void);
        bool    connectToStatusService(void);

        bool    retrieveAnalogPoints(void);
        bool    retrieveStatusPoints(void);

        bool    loadTranslationLists(void);
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);
        bool    loadLists(CtiFDRPointList &aList);

        ULONG   calculateNextSendTime();

        bool    readConfig() override;

        enum {Cygnet_Open = 0, Cygnet_Closed = 1};

        // this should be from the base Yukon but there are problems...
        enum {Yukon_Open = STATE_OPENED,  Yukon_Closed = STATE_CLOSED};

    private:
        ULONG               iScanRateSeconds;
        std::string           iAnalogServiceName;
        std::string           iStatusServiceName;
        ConnectState        iAnalogServiceState;
        ConnectState        iStatusServiceState;
        double              iHiReasonabilityFilter;
};
