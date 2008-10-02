/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrftpinterface.cpp
*
*    DATE: 05/31/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrftpinterface.cpp-arc  $
*    REVISION     :  $Revision: 1.20 $
*    DATE         :  $Date: 2008/10/02 23:57:15 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic Interface used to download a file using ftp
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrftpinterface.cpp,v $
      Revision 1.20  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.19  2008/09/23 15:14:58  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.18  2008/09/15 21:08:48  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.17  2008/03/20 21:27:14  tspar
      YUK-5541 FDR Textimport and other interfaces incorrectly use the boost tokenizer.

      Changed all uses of the tokenizer to have a local copy of the string being tokenized.

      Revision 1.16  2007/04/10 23:42:09  tspar
      Added even more protection against bad input when tokenizing.

      Doing a ++ operation on an token iterator that is already at the end will also assert.

      Revision 1.15  2007/04/10 23:04:35  tspar
      Added some more protection against bad input when tokenizing.

      Revision 1.14  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.13  2006/05/23 21:01:42  tspar
      Bug fix: .c_str() is volatile in a threaded environment. Making a local copy before calling  .c_str()

      Revision 1.12  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.11  2006/04/24 14:47:32  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.10  2005/12/20 17:17:13  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.9.2.3  2005/08/12 19:53:43  jliu
      Date Time Replaced

      Revision 1.9.2.2  2005/07/14 22:26:55  jliu
      RWCStringRemoved

      Revision 1.9.2.1  2005/07/12 21:08:36  jliu
      rpStringWithoutCmpParser

      Revision 1.9  2005/02/10 23:23:50  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.8  2004/09/24 14:36:52  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.7  2004/02/13 20:36:42  dsutton
      Number of tries were not being decremented correctly on a failure.  This may
      have caused the problem where the STEC or TRISTATE interface would
      fail a few times due to network struggles and then stop trying to retrieve
      the files

      Revision 1.5.12.1  2003/10/31 18:30:55  dsutton
      After a network failure, the STEC and TRISTATE interfaces reported an error
      connecting to their sites.  After the network came back up, the app stopped
      retrieving data altogether (some threads were still running).  Updated the locking
      mechanism in an attempt to minimize how long the data lists were locked

      Revision 1.5  2002/10/14 21:10:53  dsutton
      In the database translation routines, if we failed to hit the database
      we called the load routine again just to get the error code.  Whoops
      The error code is now saved from the original call and printed as needed

      Revision 1.4  2002/08/06 22:01:02  dsutton
      Programming around the error that happens if the dataset is empty when it is
      returned from the database and shouldn't be.  If our point list had more than
      two entries in it before, we fail the attempt and try again in 60 seconds

      Revision 1.3  2002/04/16 15:58:32  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:55  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.6   01 Mar 2002 13:15:40   dsutton
   added a link state getters,setters and a send current state function, updated run function to invalidate link status on startup adn update to valid everytime we decode a file correcty
   
      Rev 2.5   18 Feb 2002 16:19:20   dsutton
   added getters/setters and entry for new cparm that holds the local location of the downloaded file and changed the download code to put the file in the defined location
   
      Rev 2.4   15 Feb 2002 14:26:08   dsutton
   removed a bunch of debug code
   
      Rev 2.3   11 Feb 2002 15:02:18   dsutton
   Found a couple of bugs that were probably the stability problem.  Ran STEC and TRISATE simultaneously for 4 days with no problems and very little memory creep (still some though)
   
      Rev 2.2   14 Dec 2001 17:19:16   dsutton
   functions for routing data walk destination list to decide where to send data
   
      Rev 2.1   15 Nov 2001 16:16:38   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin
   
      Rev 2.0   06 Sep 2001 13:21:34   cplender
   Promote revision
   
      Rev 1.2   14 Aug 2001 17:27:18   dsutton
   the function to retrieve an ftp file had a 60 minute timeout assigned
   when there was a problem, it kept trying for 60 minutes with no feedback.  
   workaround from microsoft said to start a new thread to do the retrieval
   and kill it after the configured amount of time.  that is what we now do
   
      Rev 1.1   20 Jul 2001 10:00:26   dsutton
   trying to find a bug that the download failes and timeouts after an hour
   
      Rev 1.0   04 Jun 2001 09:33:00   dsutton
   Initial revision.
   
      Rev 1.1   10 May 2001 11:12:12   dsutton
   updated with new socket classes
   
      Rev 1.0   23 Apr 2001 11:17:58   dsutton
   Initial revision.
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"


#include <windows.h>
#include <wininet.h>


/** include files **/
#include <rw/ctoken.h>
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrftpinterface.h"



// Constructors, Destructor, and Operators
CtiFDRFtpInterface::CtiFDRFtpInterface(string &aInterface)
: CtiFDRInterface(aInterface),
iPort (INTERNET_DEFAULT_FTP_PORT),
iTries (3),
iDownloadInterval (3600),
iLogin (string()),
iPassword (string()),
iServerFileName (string()),
iLocalFileName (string()),
iFTPDirectory (string()),
iIPAddress (string())
{ 
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE)); 
    getReceiveFromList().setPointList (recList);
    recList = NULL;
}


CtiFDRFtpInterface::~CtiFDRFtpInterface()
{
}


int CtiFDRFtpInterface::getPort() const
{
    return iPort;
}

long CtiFDRFtpInterface::getLinkStatusID( void ) const
{   
    return iLinkStatusID;
}
        
CtiFDRFtpInterface & CtiFDRFtpInterface::setLinkStatusID(const long aPointID)
{   
    iLinkStatusID = aPointID;
    return *this;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setPort (int aPort)
{
    iPort = aPort;
    return *this;
}
int CtiFDRFtpInterface::getTries() const
{
    return iTries;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setTries(int aTries)
{
    iTries = aTries;
    return *this;
}


int CtiFDRFtpInterface::getDownloadInterval() const
{
    return iDownloadInterval;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setDownloadInterval (int aInterval)
{
    iDownloadInterval = aInterval;
    return *this;
}

string & CtiFDRFtpInterface::getIPAddress()
{
    return iIPAddress;
}

string  CtiFDRFtpInterface::getIPAddress() const
{
    return iIPAddress;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setIPAddress (string aAddress)
{
    iIPAddress = aAddress;
    return *this;
}

string & CtiFDRFtpInterface::getLogin()
{
    return iLogin;
}

string  CtiFDRFtpInterface::getLogin() const
{
    return iLogin;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setLogin (string aLogin)
{
    iLogin = aLogin;
    return *this;
}

string & CtiFDRFtpInterface::getPassword()
{
    return iPassword;
}

string  CtiFDRFtpInterface::getPassword() const
{
    return iPassword;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setPassword (string aPassword)
{
    iPassword = aPassword;
    return *this;
}

string & CtiFDRFtpInterface::getServerFileName()
{
    return iServerFileName;
}

string  CtiFDRFtpInterface::getServerFileName() const
{
    return iServerFileName;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setServerFileName (string aServerFileName)
{
    iServerFileName = aServerFileName;
    return *this;
}

string & CtiFDRFtpInterface::getFTPDirectory()
{
    return iFTPDirectory;
}

string  CtiFDRFtpInterface::getFTPDirectory() const
{
    return iFTPDirectory;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setFTPDirectory (string aDir)
{
    iFTPDirectory = aDir;
    return *this;
}

string & CtiFDRFtpInterface::getLocalFileName()
{
    return iLocalFileName;
}

string  CtiFDRFtpInterface::getLocalFileName() const
{
    return iLocalFileName;
}

CtiFDRFtpInterface &CtiFDRFtpInterface::setLocalFileName (string aLocalFileName)
{
    iLocalFileName = aLocalFileName;
    return *this;
}

/*************************************************
* Function Name: CtiFDRFtpInterface::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRFtpInterface::init( void )
{
    // init the base class
    Inherited::init();    

    iThreadRetrieveFrom = rwMakeThreadFunction(*this, 
                                               &CtiFDRFtpInterface::threadFunctionRetrieveFrom);
    iThreadFTPGetFile = rwMakeThreadFunction(*this, 
                                             &CtiFDRFtpInterface::threadFunctionWorkerFTPGetFile);
    iThreadInternetConnect = rwMakeThreadFunction(*this, 
                                                  &CtiFDRFtpInterface::threadFunctionWorkerInternetConnection);

    return TRUE;
}

/*************************************************
* Function Name: CtiFDRFtpInterface::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDRFtpInterface::run( void )
{

    // crank up the base class
    Inherited::run();

    // startup our interfaces
    iThreadRetrieveFrom.start();

    setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
    sendLinkState (FDR_NOT_CONNECTED);
    return TRUE;
}


/*************************************************
* Function Name: CtiFDRFtpInterface::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDRFtpInterface::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //
    iThreadRetrieveFrom.requestCancellation();

    // stop the base class
    Inherited::stop();

    return TRUE;
}

void CtiFDRFtpInterface::sendLinkState (int aState)
{
    if (getLinkStatusID() != 0)
    {
        CtiPointDataMsg     *pData;
        pData = new CtiPointDataMsg(getLinkStatusID(), 
                                    aState, 
                                    NormalQuality, 
                                    StatusPointType);
        sendMessageToDispatch (pData);
    }
}

/************************************************************************
* Function Name: CtiFDRFtpInterface::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the 
*				specified direction
* 
*************************************************************************
*/
bool CtiFDRFtpInterface::loadTranslationLists()
{
    bool successful = false;
    bool foundPoint = false;
    RWDBStatus listStatus;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(), string (FDR_INTERFACE_RECEIVE));
        listStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if (listStatus.errorCode() == (RWDBStatus::ok))
        {
            /**************************************
            * seeing occasional problems where we get empty data sets back
            * and there should be info in them,  we're checking this to see if
            * is reasonable if the list may now be empty
            * the 2 entry thing is completly arbitrary
            ***************************************
            */
            if (((pointList->entries() == 0) && (getReceiveFromList().getPointList()->entries() <= 2)) ||
                (pointList->entries() > 0))
            {
                // lock the receive list and remove the old one
                CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());  
                if (getReceiveFromList().getPointList() != NULL)
                {
                    getReceiveFromList().deletePointList();
                }
                getReceiveFromList().setPointList (pointList);

                // get iterator on send list
                CtiFDRManager* mgrPtr = getReceiveFromList().getPointList();

                CtiFDRManager::writerLock guard(mgrPtr->getLock());
                CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();

                for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    translateSinglePoint(myIterator->second);
                }

                pointList=NULL;
                if (!successful)
                {
                    if (!foundPoint)
                    {
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
            successful = false;
        }

    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error loading translation lists for " << getInterfaceName() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error loading translation lists for " << getInterfaceName() << endl;
    }

    return successful;
}

bool CtiFDRFtpInterface::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool send)
{
    string           tempString1;
    string           tempString2;
    bool successful = false;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
        }

        /********************
        * for the our current FTP interfaces, the points are being retrieved only
        * and have specific names already assigned them
        *********************
        */
        const string translation = translationPoint->getDestinationList()[x].getTranslation();
        boost::char_separator<char> sep(";");
        Boost_char_tokenizer nextTranslate(translation, sep);
        Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin(); 

        if ( tok_iter != nextTranslate.end() )
        {
            tempString1 = *tok_iter;
            boost::char_separator<char> sep1(":");
            Boost_char_tokenizer nextTempToken(tempString1, sep1);
            Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin(); 

            if( tok_iter1 != nextTempToken.end() )
            {
                tok_iter1++;
                if( tok_iter1 != nextTempToken.end() )
                {
                    tempString2 = *tok_iter1;
                }else
                {
                    tempString2 = "";
                }

                // now we have a point name
                if ( !tempString2.empty() )
                {
                    translationPoint->getDestinationList()[x].setTranslation (tempString2);
                    successful = true;
                }
            }
        }   // first token invalid
    }

    return successful;
}


/**************************************************************************
* Function Name: CtiFDRFtpInterface::threadFunctionRetrieveFrom (void )
*
* Description: thread that waits and then grabs the file for processing
* 
***************************************************************************
*/
/********************************************************
This function retrieves a file from the specified ftp site

Unfortunately, certain timeout values are defaulted to times that are unreasonable.  The
get file and connect to server timeouts default to 1 hour.  The functions used to
change these timeouts to something more reasonable do not work (verified by Microsoft
knowledge base Q224318)  The workaround suggested
uses separate threads to do the connections and retrievals.  This allows us to set a 
semaphore and kill the thread if it hasn't returned in a realistic amount of 
time (2 minutes in our case).  We then can return control to our program instead
of waiting the entire timeout
*********************************************************
*/
void CtiFDRFtpInterface::threadFunctionRetrieveFrom( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);
    ULONG  errorNum, errorLength=500;
    CHAR errorBuffer[500];
    int fileNumber=0;
    CHAR fileName[200];
    ULONG timeout=120000;  // 2 minutes in milli seconds
    ULONG length = sizeof (ULONG);

    HANDLE   workerThread; 
    DWORD    threadID;
    ULONG    exitCode = 0;
    string action,desc;
    RWWaitStatus workerThreadReturnStatus;
    RWCompletionState workerThreadCompletionState;

    try
    {
        while (getTries() == 0)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);
        }

        tries = getTries();

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);


            timeNow = CtiTime();

            // now is the time to get the file
            if (timeNow >= refreshTime)
            {
                iInitialHandle = InternetOpen (getInterfaceName().c_str(),
                                               INTERNET_OPEN_TYPE_DIRECT,  // valid if direct connection to internet used
                                               NULL,
                                               NULL,
                                               0);

                if (fileNumber > 30)
                {
                    fileNumber = 0;
                }
                else
                {
                    fileNumber++;
                }

                if (iInitialHandle == NULL)
                {
                    errorNum=GetLastError();
                    InternetGetLastResponseInfo (&errorNum,errorBuffer,&errorLength);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Error connecting to " <<getInterfaceName() << " " << string (errorBuffer) << endl;
                    }

                    action = getInterfaceName() + ": Failed to connect";
                    desc = "Error connecting to " + getIPAddress();
                    logEvent (desc, action, true);
                    InternetCloseHandle (iInitialHandle);
                    iInitialHandle = NULL;

                    tries--;
                    if (tries <= 0)
                    {
                        tries = getTries();
                        refreshTime = CtiTime() - (CtiTime::now().seconds() % iDownloadInterval) + iDownloadInterval;
                        fail();
                    }
                }
                else
                {
                    string tempTS = getInterfaceName();
                    _snprintf (fileName, 200, "%s\\%s%d.txt",getFTPDirectory().c_str(),tempTS.c_str(),fileNumber);
                    iLocalFileName = string (fileName);
                    iThreadInternetConnect.start();
//                    workerThreadReturnStatus = iThreadInternetConnect.join( 5000);
                    workerThreadReturnStatus = iThreadInternetConnect.join( 120000);

                    // wait at most 2 minutes 
                    if ( workerThreadReturnStatus == RW_THR_TIMEOUT )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Timeout error connecting to " << getInterfaceName() << endl;
                        }

                        action = getInterfaceName() + ": Timeout error";
                        desc = "Timeout error connecting to " + getIPAddress();
                        logEvent (desc, action, true);

                        // if it was something, shut it down
//                        if (iInitialHandle)
                        InternetCloseHandle (iInitialHandle);
                        iInitialHandle = NULL;

                        iThreadInternetConnect.join();

                        tries--;
                        if (tries <= 0)
                        {
                            tries = getTries();
                            refreshTime = CtiTime() - (CtiTime::now().seconds() % iDownloadInterval) + iDownloadInterval;
                            fail();
                        }
                    }
                    else
                    {
                        // The state of the specified object (thread) is signaled
                        workerThreadCompletionState = iThreadInternetConnect.getCompletionState();
                        if ( workerThreadCompletionState != RW_THR_NORMAL)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Error connecting to " <<getInterfaceName() << endl;
                            }

                            action = getInterfaceName() + ": Failed to connect";
                            desc = "Error connecting to " + getIPAddress();
                            logEvent (desc, action, true);

                            InternetCloseHandle (iInitialHandle);
                            iInitialHandle = NULL;

                            tries--;
                            if (tries <= 0)
                            {
                                tries = getTries();
                                refreshTime = CtiTime() - (CtiTime::now().seconds() % iDownloadInterval) + iDownloadInterval;
                                fail();
                            }
                        }
                        else
                        {
                            iThreadFTPGetFile.start();
//                            workerThreadReturnStatus = iThreadFTPGetFile.join( 5000);
                                workerThreadReturnStatus = iThreadFTPGetFile.join( 120000);

                            // wait at most 2 minutes 
                            if ( workerThreadReturnStatus == RW_THR_TIMEOUT )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Timeout error retrieving file " << iServerFileName << " for " << getInterfaceName() << endl;
                                }
                                action = getInterfaceName() + ": Timeout error";
                                desc = "Timeout retrieving file " + iServerFileName ;
                                logEvent (desc, action, true);

                                // if it was something, shut it down
//                                if (iSessionHandle)
                                InternetCloseHandle (iSessionHandle);
                                iSessionHandle = NULL;

                                // put the smack on it to make sure it dies
                                iThreadFTPGetFile.join();

                                tries--;
                                if (tries <= 0)
                                {
                                    tries = getTries();
                                    refreshTime = CtiTime() - (CtiTime::now().seconds() % iDownloadInterval) + iDownloadInterval;
                                    fail();
                                }
                            }
                            else
                            {
                                // The state of the specified object (thread) is signaled
                                workerThreadCompletionState = iThreadFTPGetFile.getCompletionState();
                                if ( workerThreadCompletionState != RW_THR_NORMAL)
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " Error connecting to " <<getInterfaceName() << " " << string (errorBuffer) << endl;
                                    }
                                    action = getInterfaceName() + ": Timeout error";
                                    desc = "Error retrieving file " + iServerFileName ;
                                    logEvent (desc, action, true);
                                    InternetCloseHandle (iSessionHandle);
                                    iSessionHandle = NULL;

                                    tries--;
                                    if (tries <= 0)
                                    {
                                        tries = getTries();
                                        refreshTime = CtiTime() - (CtiTime::now().seconds() % iDownloadInterval) + iDownloadInterval;
                                        fail();
                                    }
                                }
                                else
                                {
                                    if (decodeFile())
                                        fail();

                                    refreshTime = CtiTime() - (CtiTime::now().seconds() % iDownloadInterval) + iDownloadInterval;
                                    tries = getTries();
                                }

                                InternetCloseHandle (iSessionHandle);
                                iSessionHandle = NULL;
                            }
                            InternetCloseHandle (iInitialHandle);
                            iInitialHandle = NULL;
                        }            
                    }
                }
            }                          
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        InternetCloseHandle (iInitialHandle);
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRFtpInterface::threadFunctionRetrieveFrom in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        InternetCloseHandle (iInitialHandle);
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRFtpInterface::threadFunctionRetrieveFrom " << getInterfaceName() << " is dead! " << endl;
    }
}

RWCompletionState CtiFDRFtpInterface::threadFunctionWorkerInternetConnection( void )
{
    RWCompletionState retCode;
    try
    {
        if (iInitialHandle != NULL)
        {
            try
            {

                iSessionHandle = InternetConnect (iInitialHandle,
                                                  iIPAddress.c_str(),
                                                  iPort,
                                                  iLogin.c_str(),
                                                  iPassword.c_str(),
                                                  INTERNET_SERVICE_FTP,
                                                  0,
                                                  0);

                if (iSessionHandle == NULL)
                {
                    retCode = RW_THR_TERMINATED;
                }
                else
                {
                    retCode = RW_THR_NORMAL;
                }
            }
            catch ( RWCancellation &cancellationMsg )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << " InternetConnect blew " <<getInterfaceName()<< endl;
                retCode = RW_THR_TERMINATED;
            }

            // try and catch the thread death
            catch ( ... )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << " Fatal Error:InternetConnect blew " <<getInterfaceName()<< endl;
                retCode = RW_THR_TERMINATED;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Internet connection iInitialHandle set to null " << getInterfaceName() << endl;
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRFtpInterface::threadFunctionWorkerInternetConnection in interface " <<getInterfaceName()<< endl;
        retCode = RW_THR_TERMINATED;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRFtpInterface::threadFunctionWorkerInternetConnection " << getInterfaceName() << " is dead! " << endl;
        retCode = RW_THR_TERMINATED;
    }
    return retCode;
}

RWCompletionState CtiFDRFtpInterface::threadFunctionWorkerFTPGetFile()
{
    RWCompletionState retCode;

    try
    {
        /*************************
        * use flag INTERNET_FLAG_RELOAD to force the 
        * application to get the file from the server
        * always instead of looking in the cache
        **************************
        */
        if (iSessionHandle != NULL)
        {
            if (!FtpGetFile (iSessionHandle,
                             iServerFileName.c_str(),
                             iLocalFileName.c_str(),
                             FALSE,
                             FILE_ATTRIBUTE_NORMAL,
                             FTP_TRANSFER_TYPE_BINARY | INTERNET_FLAG_RELOAD,
                             0))
            {
                retCode = RW_THR_TERMINATED;
            }
            else
            {
                retCode = RW_THR_NORMAL;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " FTP Get File iSessionHandle set to null " << getInterfaceName() << endl;
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRFtpInterface::threadFunctionWorkerInternetConnection in interface " <<getInterfaceName()<< endl;
        retCode = RW_THR_TERMINATED;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRFtpInterface::threadFunctionWorkerInternetConnection " << getInterfaceName() << " is dead! " << endl;
        retCode = RW_THR_TERMINATED;
    }

    return retCode;
}

