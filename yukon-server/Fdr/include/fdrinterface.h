#pragma once

#include "dlldefs.h"
#include "message.h"
#include "msg_dbchg.h"
#include "connection_client.h"
#include "mgr_fdrpoint.h"
#include "cparms.h"
#include "fdrdebuglevel.h"
#include "fdrpointlist.h"
#include "msg_cmd.h"
#include "worker_thread.h"


class IM_EX_FDRBASE CtiFDRInterface
{
    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDRInterface(std::string & interfaceType);

        virtual ~CtiFDRInterface( void );

        // pure virtual function implementent by interface
        virtual void        sendMessageToForeignSys    ( CtiMessage *aMessage ) = 0;
        virtual bool        sendMessageToDispatch      ( CtiMessage *aMessage );
        virtual bool        queueMessageToDispatch     ( CtiMessage *aMessage );
        virtual void        processCommandFromDispatch ( CtiCommandMsg *cmd ){delete cmd;};

        bool                logEvent( const std::string &logDesc,
                                      const std::string &logMsg,
                                      bool aSendImmediatelyFlag=false );
        CtiCommandMsg* createAnalogOutputMessage(long pointId, std::string translationName, double value);
        CtiCommandMsg* createScanDeviceMessage(long paoId, std::string translationName);

        bool                sendPointRegistration();
        virtual std::unique_ptr<CtiPointRegistrationMsg> buildRegistrationPointList();


        std::string              getCparmValueAsString(std::string key);

        CtiFDRInterface &   setInterfaceName(std::string & aInterfaceName);
        std::string       &   getInterfaceName(void);

        int                 getReloadRate () const;
        CtiFDRInterface&    setReloadRate (INT aRate);

        int                 getQueueFlushRate () const;
        CtiFDRInterface&    setQueueFlushRate (INT aTime);

        FDRDbReloadReason   getDbReloadReason() const;
        CtiFDRInterface&    setDbReloadReason(FDRDbReloadReason aLevel=DbChange);
        void                processFDRPointChange(int pointId, bool deleteType);

        ULONG               getDebugLevel();

        int                 getOutboundSendRate () const;
        CtiFDRInterface&    setOutboundSendRate (INT aRate);
        int                 getOutboundSendInterval () const;
        CtiFDRInterface&    setOutboundSendInterval (INT aInterval);
        int                 getTimeSyncVariation () const;
        CtiFDRInterface&    setTimeSyncVariation (INT aInterval);
        bool                shouldIgnoreOldData() const;
        void                setIgnoreOldData(bool ignore);
        bool                shouldUpdatePCTime() const;
        void                setUpdatePCTimeFlag(const bool aChangeFlag);


        virtual BOOL        init( void );
        virtual BOOL        run( void );
        virtual BOOL        stop( void );

        virtual bool readConfig();

        virtual int processMessageFromForeignSystem (CHAR *data) = 0;

        bool reloadTranslationLists(void);
        virtual bool loadTranslationLists(void)=0;
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false)=0;

        //Load single point, maintaining current lists
        virtual bool loadTranslationPoint(long pointId);
        //remove single point maintaining current lists
        void removeTranslationPoint(long pointId);
        //This is the interfaces hook to know when a point is removed. Most do not care so a default was written.
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

        CtiFDRPointList & getSendToList ();
        CtiFDRInterface& setSendToList (CtiFDRPointList & aList);

        CtiFDRPointList & getReceiveFromList ();
        CtiFDRInterface& setReceiveFromList (CtiFDRPointList & aList);

        bool findTranslationNameInList(std::string aTranslationName, CtiFDRPointList &aList,CtiFDRPoint &aPoint);
        bool findPointIdInList(long aPointId, CtiFDRPointList &aList,CtiFDRPoint &aPoint);
        bool updatePointByIdInList(CtiFDRPointList &aList, CtiPointDataMsg *aMessage);
        long getClientLinkStatusID(const std::string &aClientName);
        virtual void setCurrentClientLinkStates();

        std::string logNow();

    protected:

        CtiMutex            iCparmMutex;

        Cti::WorkerThread   iThreadFromDispatch;
        Cti::WorkerThread   iThreadDbChange;
        Cti::WorkerThread   iThreadToDispatch;
        Cti::WorkerThread   iThreadReloadCparm;

        void threadFunctionSendToDispatch( void );
        void threadFunctionReceiveFromDispatch( void );
        void threadFunctionReloadDb( void );
        void threadFunctionReloadCparm( void );

        void printLists(std::string title, int pid);

        bool verifyDispatchConnection ();

        CtiConnection::Que_t iDispatchInQueue;

    private:

        std::string           iInterfaceName;

        int                 iCparmReloadSeconds;
        FDRDbReloadReason   iDbReloadReason;
        ULONG               iDebugLevel;
        int                 iReloadRate;

        int                 iQueueFlushRate;

        int                 iOutboundSendRate;
        int                 iOutboundSendInterval;
        int                 iTimeSyncVariation;
        bool                iIgnoreOldData;
        bool                iUpdatePCTimeFlag;

        /***********************
        * not sure if these should be here since not every interface has both
        * a send and a receive list
        * the other option is to define the lists in each individual
        * type of interface which also seems redundant
        * for now, an empty unused list is easier to deal with
        * than repeating code everywhere throughout fdr
        ************************
        */
        CtiFDRPointList    iSendToList;
        CtiFDRPointList    iReceiveFromList;

        // add things here and then send dispatch a multi point msg
        CtiFIFOQueue<CtiMessage> iDispatchQueue;

        bool reloadConfigs();

        void disconnect( void );

        typedef Cti::readers_writer_lock_t Lock;
        typedef Lock::reader_lock_guard_t  ReaderGuard;
        typedef Lock::writer_lock_guard_t  WriterGuard;

        mutable Lock iDispatchLock;

        boost::scoped_ptr<CtiFDRManager>       iOutBoundPoints;
        boost::scoped_ptr<CtiClientConnection> iDispatchConn;
        boost::optional<int>                   iDispatchRegisterId;
        bool                                   iDispatchConnected;

        bool connectWithDispatch ();
        bool reRegisterWithDispatch(void);
};
