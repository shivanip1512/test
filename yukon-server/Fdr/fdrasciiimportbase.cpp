/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrasciiimportbase.cpp
*
*    DATE: 05/31/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrasciiimportbase.cpp-arc  $
*    REVISION     :  $Revision: 1.17.2.1 $
*    DATE         :  $Date: 2008/11/13 17:23:46 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic Interface used to import an ascii file
*
*    DESCRIPTION:
*
*    ---------------------------------------------------
*    History:
      $Log: fdrasciiimportbase.cpp,v $
      Revision 1.17.2.1  2008/11/13 17:23:46  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.17  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.16  2008/09/23 15:14:57  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.15  2008/09/15 21:08:47  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.14  2008/03/20 21:27:14  tspar
      YUK-5541 FDR Textimport and other interfaces incorrectly use the boost tokenizer.

      Changed all uses of the tokenizer to have a local copy of the string being tokenized.

      Revision 1.13  2007/04/10 23:42:08  tspar
      Added even more protection against bad input when tokenizing.

      Doing a ++ operation on an token iterator that is already at the end will also assert.

      Revision 1.12  2007/04/10 23:04:35  tspar
      Added some more protection against bad input when tokenizing.

      Revision 1.11  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.10  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.9  2006/04/24 14:47:32  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.8  2005/12/20 17:17:12  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.7.2.3  2005/08/12 19:53:43  jliu
      Date Time Replaced

      Revision 1.7.2.2  2005/07/14 22:26:55  jliu
      RWCStringRemoved

      Revision 1.7.2.1  2005/07/12 21:08:36  jliu
      rpStringWithoutCmpParser

      Revision 1.7  2005/02/10 23:23:50  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.6  2004/09/24 14:36:52  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.5  2002/10/14 21:10:53  dsutton
      In the database translation routines, if we failed to hit the database
      we called the load routine again just to get the error code.  Whoops
      The error code is now saved from the original call and printed as needed

      Revision 1.4  2002/08/06 22:00:51  dsutton
      Programming around the error that happens if the dataset is empty when it is
      returned from the database and shouldn't be.  If our point list had more than
      two entries in it before, we fail the attempt and try again in 60 seconds

      Revision 1.3  2002/04/16 15:58:31  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:54  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


      Rev 1.0   12 Mar 2002 10:35:50   dsutton
   Initial revision.

*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrasciiimportbase.h"

using std::string;
using std::endl;
using std::vector;

// Constructors, Destructor, and Operators
CtiFDRAsciiImportBase::CtiFDRAsciiImportBase(string &aInterface)
: CtiFDRInterface(aInterface),
    iFileName(string ("dsmdata.txt")),
    iDriveAndPath (string("\\yukon\\server\\import")),
    iImportInterval(900),
    iLinkStatusID(0),
    iDeleteFileAfterImportFlag(true)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

}


CtiFDRAsciiImportBase::~CtiFDRAsciiImportBase()
{
}


long CtiFDRAsciiImportBase::getLinkStatusID( void ) const
{
    return iLinkStatusID;
}

CtiFDRAsciiImportBase & CtiFDRAsciiImportBase::setLinkStatusID(const long aPointID)
{
    iLinkStatusID = aPointID;
    return *this;
}

int CtiFDRAsciiImportBase::getImportInterval() const
{
    return iImportInterval;
}

CtiFDRAsciiImportBase &CtiFDRAsciiImportBase::setImportInterval (int aInterval)
{
    iImportInterval = aInterval;
    return *this;
}

bool CtiFDRAsciiImportBase::shouldDeleteFileAfterImport() const
{
    return iDeleteFileAfterImportFlag;
}

CtiFDRAsciiImportBase &CtiFDRAsciiImportBase::setDeleteFileAfterImport (bool aFlag)
{
    iDeleteFileAfterImportFlag = aFlag;
    return *this;
}

string & CtiFDRAsciiImportBase::getFileName()
{
    return iFileName;
}

string  CtiFDRAsciiImportBase::getFileName() const
{
    return iFileName;
}

CtiFDRAsciiImportBase &CtiFDRAsciiImportBase::setFileName (string aFile)
{
    iFileName = aFile;
    return *this;
}

string & CtiFDRAsciiImportBase::getDriveAndPath()
{
    return iDriveAndPath;
}

string  CtiFDRAsciiImportBase::getDriveAndPath() const
{
    return iDriveAndPath;
}

CtiFDRAsciiImportBase &CtiFDRAsciiImportBase::setDriveAndPath (string aPath)
{
    iDriveAndPath = aPath;
    return *this;
}

/*************************************************
* Function Name: CtiFDRAsciiImportBase::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRAsciiImportBase::init( void )
{
    // init the base class
    Inherited::init();
    iThreadReadFromFile = rwMakeThreadFunction(*this,
                                               &CtiFDRAsciiImportBase::threadFunctionReadFromFile);

    return TRUE;
}

/*************************************************
* Function Name: CtiFDRAsciiImportBase::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDRAsciiImportBase::run( void )
{

    // crank up the base class
    Inherited::run();

    // startup our interfaces
    iThreadReadFromFile.start();

    setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
    sendLinkState (FDR_NOT_CONNECTED);
    return TRUE;
}


/*************************************************
* Function Name: CtiFDRAsciiImportBase::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDRAsciiImportBase::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //
    iThreadReadFromFile.requestCancellation();

    // stop the base class
    Inherited::stop();

    return TRUE;
}

void CtiFDRAsciiImportBase::sendLinkState (int aState)
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
* Function Name: CtiFDRAsciiImportBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDRAsciiImportBase::loadTranslationLists()
{
    bool successful = false;
    bool foundPoint = false;
    bool loadPointSuccess = false;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_RECEIVE));

        // keep the status
        loadPointSuccess = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( loadPointSuccess )
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
                pointList = NULL;

                // get iterator on send list

                CtiFDRManager* mgrPtr = getReceiveFromList().getPointList();

                CtiFDRManager::writerLock guard(mgrPtr->getLock());
                CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();

                for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    translateSinglePoint(myIterator->second);
                }

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
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

bool CtiFDRAsciiImportBase::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;
    string           tempString1;
    string           tempString2;
    string           translationName;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
        }
        /********************
        * for our current FTP interfaces, the points are being retrieved only
        * and have specific names already assigned them
        *********************
        */
        const string translation = translationPoint->getDestinationList()[x].getTranslation();
        boost::char_separator<char> sep1(";");
        Boost_char_tokenizer nextTranslate(translation, sep1);
        Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin();

        if ( tok_iter != nextTranslate.end())
        {
            tempString1 = *tok_iter;
            boost::char_separator<char> sep2(":");
            Boost_char_tokenizer nextTempToken(tempString1, sep2);
            Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

            if( tok_iter1 != nextTempToken.end() )
            {
                tok_iter1++;
                if( tok_iter1 != nextTempToken.end() )
                {
                    Boost_char_tokenizer nextTempToken_(tok_iter1.base(), tok_iter1.end(), sep1);

                    tempString2 = *nextTempToken_.begin();
                    tempString2.replace(0,tempString2.length(), tempString2.substr(1,(tempString2.length()-1)));

                    // now we have a point id
                    if ( !tempString2.empty() )
                    {
                        translationPoint->getDestinationList()[x].setTranslation (tempString2);
                        successful = true;
                    }
                }
            }
        }   // first token invalid
    }

    return successful;
}

/**************************************************************************
* Function Name: CtiFDRAsciiImportBase::threadFunctionReadFromFile (void )
*
* Description: thread that waits and then grabs the file for processing
*
***************************************************************************
*/
void CtiFDRAsciiImportBase::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);
    string action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is

    try
    {
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = CtiTime();

            // now is the time to get the file
            if (timeNow >= refreshTime)
            {
                _snprintf (fileName, 200, "%s\\%s",getDriveAndPath().c_str(),getFileName().c_str());
                if( (fptr = fopen( fileName, "r")) == NULL )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getInterfaceName() << "'s file " << string (fileName) << " was either not found or could not be opened" << endl;
                    }
                }
                else
                {
                    vector<string>     valueVector;

                    // load list in the command vector
                    while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
                    {
                        string entry (workBuffer);
                        valueVector.push_back (entry);
                    }

                    fclose(fptr);
                    if( ferror( fptr ) != 0 )
                    {
                        valueVector.erase(valueVector.begin(), valueVector.end());
                    }
                    else
                    {
                        // retrieve each line in order
                        int totalLines = valueVector.size();
                        int lineCnt = 0;
                        CtiMessage      *retMsg=NULL;
                        while (lineCnt < totalLines )
                        {
                            if(validateAndDecodeLine( valueVector[lineCnt], &retMsg ))
                            {
                                queueMessageToDispatch (retMsg);
                            }
                            lineCnt++;
                        }
                        valueVector.erase(valueVector.begin(), valueVector.end());
                    }
                }

                if (shouldDeleteFileAfterImport())
                {
                    DeleteFile (fileName);
                }

                refreshTime = CtiTime() - (CtiTime::now().seconds() % iImportInterval) + iImportInterval;
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRAsciiImportBase::threadFunctionReadFromFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRAsciiImportBase::threadFunctionReadFromFile  " << getInterfaceName() << " is dead! " << endl;
    }
}
