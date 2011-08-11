#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*****************************************************************************
*
*    FILE NAME: fdrinterface.h
*
*    DATE: 07/15/2000
*
*    AUTHOR: Matt Fisher
*
*    PURPOSE: pure vitural Base Class header for Foreign Data
*
*    DESCRIPTION: Profides an interface for all Foreign Data Interfaces
*                 data exchanges.  The Interfaces implement methods to
*                 exchange data with other systems.
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRINTERFACE_H__
#define __FDRINTERFACE_H__

#include <rw/thr/mutex.h>
#include <rw/thr/barrier.h>
#include <rw/thr/condtion.h>
#include <rw/tvhset.h>
#include <rw/tvdeque.h>

#include "dlldefs.h"
#include "message.h"
#include "msg_dbchg.h"
#include "connection.h"
#include "mgr_fdrpoint.h"
#include "cparms.h"
#include "fdrdebuglevel.h"
#include "fdrpointlist.h"
#include "msg_cmd.h"


class IM_EX_FDRBASE CtiFDRInterface
{
    public:
        // constructors and destructors
        CtiFDRInterface(std::string & interfaceType);

        virtual ~CtiFDRInterface( void );

        // pure virtual function implementent by interface
        virtual bool        sendMessageToForeignSys ( CtiMessage *aMessage ) = 0;
        virtual bool        sendMessageToDispatch   ( CtiMessage *aMessage );
        virtual bool        queueMessageToDispatch   ( CtiMessage *aMessage );
        bool                logEvent( const std::string &logDesc,
                                      const std::string &logMsg,
                                      bool aSendImmediatelyFlag=false );
        CtiCommandMsg* createAnalogOutputMessage(long pointId, std::string translationName, double value);
        CtiCommandMsg* createScanDeviceMessage(long paoId, std::string translationName);

        bool                sendPointRegistration();
        virtual void        buildRegistrationPointList(CtiPointRegistrationMsg **aMsg);

        INT                 reRegisterWithDispatch(void);
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

        BOOL                isInterfaceInDebugMode() const;
        void                setInterfaceDebugMode(const BOOL aChangeFlag = TRUE);

        ULONG               getDebugLevel() const { return iDebugLevel;};

        int                 getOutboundSendRate () const;
        CtiFDRInterface&    setOutboundSendRate (INT aRate);
        int                 getOutboundSendInterval () const;
        CtiFDRInterface&    setOutboundSendInterval (INT aInterval);
        int                 getTimeSyncVariation () const;
        CtiFDRInterface&    setTimeSyncVariation (INT aInterval);
        BOOL                shouldUpdatePCTime() const;
        void                setUpdatePCTimeFlag(const BOOL aChangeFlag = TRUE);


        virtual BOOL        init( void );
        virtual BOOL        run( void );
        virtual BOOL        stop( void );

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

        CtiFDRPointList   getSendToList () const;
        CtiFDRPointList & getSendToList ();
        CtiFDRInterface& setSendToList (CtiFDRPointList & aList);

        CtiFDRPointList   getReceiveFromList () const;
        CtiFDRPointList & getReceiveFromList ();
        CtiFDRInterface& setReceiveFromList (CtiFDRPointList & aList);

        bool findTranslationNameInList(std::string aTranslationName, CtiFDRPointList &aList,CtiFDRPoint &aPoint);
        bool findPointIdInList(long aPointId, CtiFDRPointList &aList,CtiFDRPoint &aPoint);
        bool updatePointByIdInList(CtiFDRPointList &aList, CtiPointDataMsg *aMessage);
        long getClientLinkStatusID(std::string &aClientName);
        virtual void setCurrentClientLinkStates();

        BOOL connectWithDispatch(void);
        BOOL registerWithDispatch(void);

        enum {ConnectionFailed=0,
              ConnectionOkWriteFailed,
              ConnectionOkWriteOk};
        int attemptSend( CtiMessage *aMessage );

        std::ostream& logNow();

    protected:

        CtiMutex            iDispatchMux;
        CtiConnection       *iDispatchConn;
        CtiFDRManager       *iOutBoundPoints;

        RWThreadFunction    iThreadFromDispatch;
        RWThreadFunction    iThreadDbChange;
        RWThreadFunction    iThreadToDispatch;
        RWThreadFunction    iThreadConnectToDispatch;

        void threadFunctionSendToDispatch( void );
        void threadFunctionReceiveFromDispatch( void );
        void threadFunctionReloadDb( void );
        void threadFunctionConnectToDispatch( void );

        void printLists(std::string title, int pid);

        static const CHAR * KEY_DISPATCH_NAME;
        static const CHAR * KEY_DEBUG_LEVEL;

    private:
        std::string           iInterfaceName;
        std::string           iDispatchMachine;

        FDRDbReloadReason   iDbReloadReason;
        ULONG               iDebugLevel;
        int                 iReloadRate;

        BOOL                iDebugMode;
        BOOL                iDispatchOK;
        int                 iQueueFlushRate;

        int                 iOutboundSendRate;
        int                 iOutboundSendInterval;
        int                 iTimeSyncVariation;
        BOOL                iUpdatePCTimeFlag;

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

        int  readConfig( void );
        void disconnect( void );
};


#endif  //  #ifndef __FDRINTERFACE_H__
