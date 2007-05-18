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
*						ftp
*
*    Copyright (C) 2001 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRFTPINTERFACE_H__
#define __FDRFTPINTERFACE_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include "dlldefs.h"
#include <wininet.h>
#include "fdrinterface.h"

class IM_EX_FDRBASE CtiFDRFtpInterface : public CtiFDRInterface
{
    typedef CtiFDRInterface Inherited;

    public:
        // constructors and destructors
        CtiFDRFtpInterface(string &interfaceType); 
    
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

        string & getIPAddress();
        string  getIPAddress() const;
        CtiFDRFtpInterface &setIPAddress (string aIP);
    
        string & getPassword();
        string  getPassword() const;
        CtiFDRFtpInterface &setPassword (string aPassword);
    
        string & getLogin();
        string  getLogin() const;
        CtiFDRFtpInterface &setLogin (string aLogin);
    
        string & getServerFileName();
        string  getServerFileName() const;
        CtiFDRFtpInterface &setServerFileName (string aFile);
    
        string & getFTPDirectory();
        string  getFTPDirectory() const;
        CtiFDRFtpInterface &setFTPDirectory (string aDir);

        string & getLocalFileName();
        string  getLocalFileName() const;
        CtiFDRFtpInterface &setLocalFileName (string aFile);
    
        int getPort() const;
        CtiFDRFtpInterface &setPort (int aPort);
    
        int getDownloadInterval() const;
        CtiFDRFtpInterface &setDownloadInterval (int aInterval);
    
        int getTries() const;
        CtiFDRFtpInterface &setTries (int aTry);

        virtual int fail() = 0;
        virtual int decodeFile() = 0;
        virtual bool loadTranslationLists(void);

        long                getLinkStatusID( void ) const;
        CtiFDRFtpInterface  &  setLinkStatusID(const long aPointID);
        void sendLinkState (int aState);


    
    private:
        int                iPort;
        int                iTries;
        int                iDownloadInterval;
        long               iLinkStatusID;
    
        string      iIPAddress;
        string      iPassword;
        string      iLogin;
        string      iServerFileName;
        string      iLocalFileName;
        string      iFTPDirectory;
};

#endif  //  #ifndef __FDRFTPINTERFACE_H__


