#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrftpinterface.h
*
*    DATE: 05/30/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic class for any interface retrieving a file using ftp
*
*    DESCRIPTION: This class implements an interface that retrieves data using
*                                               ftp
*
*    Copyright (C) 2001 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRFTPINTERFACE_H__
#define __FDRFTPINTERFACE_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "dlldefs.h"
#include <wininet.h>
#include "fdrinterface.h"

class IM_EX_FDRBASE CtiFDRFtpInterface : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

    public:
        // constructors and destructors
        CtiFDRFtpInterface(std::string &interfaceType);

        virtual ~CtiFDRFtpInterface();

        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage )=0;
        virtual int processMessageFromForeignSystem (CHAR *data)=0;

        virtual BOOL    init( void );
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

    protected:

        RWThreadFunction    iThreadRetrieveFrom;
        void threadFunctionRetrieveFrom( void );

        RWThreadFunction    iThreadFTPGetFile;
        RWCompletionState threadFunctionWorkerFTPGetFile( void );

        RWThreadFunction    iThreadInternetConnect;
        RWCompletionState threadFunctionWorkerInternetConnection(void);

        HINTERNET iSessionHandle;
        HINTERNET iInitialHandle;

        std::string & getIPAddress();
        std::string  getIPAddress() const;
        CtiFDRFtpInterface &setIPAddress (std::string aIP);

        std::string & getPassword();
        std::string  getPassword() const;
        CtiFDRFtpInterface &setPassword (std::string aPassword);

        std::string & getLogin();
        std::string  getLogin() const;
        CtiFDRFtpInterface &setLogin (std::string aLogin);

        std::string & getServerFileName();
        std::string  getServerFileName() const;
        CtiFDRFtpInterface &setServerFileName (std::string aFile);

        std::string & getFTPDirectory();
        std::string  getFTPDirectory() const;
        CtiFDRFtpInterface &setFTPDirectory (std::string aDir);

        std::string & getLocalFileName();
        std::string  getLocalFileName() const;
        CtiFDRFtpInterface &setLocalFileName (std::string aFile);

        int getPort() const;
        CtiFDRFtpInterface &setPort (int aPort);

        int getDownloadInterval() const;
        CtiFDRFtpInterface &setDownloadInterval (int aInterval);

        int getTries() const;
        CtiFDRFtpInterface &setTries (int aTry);

        virtual int fail() = 0;
        virtual int decodeFile() = 0;
        virtual bool loadTranslationLists(void);
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

        long                getLinkStatusID( void ) const;
        CtiFDRFtpInterface  &  setLinkStatusID(const long aPointID);
        void sendLinkState (int aState);



    private:
        int                iPort;
        int                iTries;
        int                iDownloadInterval;
        long               iLinkStatusID;

        std::string      iIPAddress;
        std::string      iPassword;
        std::string      iLogin;
        std::string      iServerFileName;
        std::string      iLocalFileName;
        std::string      iFTPDirectory;
};

#endif  //  #ifndef __FDRFTPINTERFACE_H__


