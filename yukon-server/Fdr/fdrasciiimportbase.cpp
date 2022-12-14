#include "precompiled.h"

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

#pragma warning(push)
#pragma warning(disable:4355) // disable the warning generated by worker thread using "this" in constructor initializer List

// Constructors, Destructor, and Operators
CtiFDRAsciiImportBase::CtiFDRAsciiImportBase(string &aInterface)
: CtiFDRInterface(aInterface),
    iFileName(string ("dsmdata.txt")),
    iDriveAndPath (string("\\yukon\\server\\import")),
    iImportInterval(900),
    iLinkStatusID(0),
    iDeleteFileAfterImportFlag(true),
    iThreadReadFromFile(Cti::WorkerThread::Function(&CtiFDRAsciiImportBase::threadFunctionReadFromFile, this).name("readFromFile"))
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;
}

#pragma warning(pop)

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
    iThreadReadFromFile.interrupt();
    iThreadReadFromFile.tryJoinOrTerminateFor(Cti::Timing::Chrono::seconds(10));

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
                            CTILOG_DEBUG(dout, "No points defined for use by interface "<< getInterfaceName());
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CTILOG_ERROR(dout, "Could not load (Receive) points for " << getInterfaceName() << " : Empty data set returned");
                successful = false;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to load points from database for "<< getInterfaceName());
            successful = false;
        }

    }
    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to load translation lists for "<< getInterfaceName());
    }

    return successful;
}

bool CtiFDRAsciiImportBase::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool    successful = false;
    string  tempString1;
    string  tempString2;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Parsing Yukon Point ID "<< translationPoint->getPointID() <<
                    " translate: "<< translationPoint->getDestinationList()[x].getTranslation());
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
    CtiTime timeNow;
    CtiTime refreshTime(PASTDATE);
    CHAR    fileName[200];
    FILE*   fptr;
    char    workBuffer[500];  // not real sure how long each line possibly is

    try
    {
        for ( ; ; )
        {
            Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::milliseconds(1000));

            timeNow = CtiTime();

            // now is the time to get the file
            if (timeNow >= refreshTime)
            {
                _snprintf (fileName, 200, "%s\\%s",getDriveAndPath().c_str(),getFileName().c_str());
                if( (fptr = fopen( fileName, "r")) == NULL )
                {
                    CTILOG_ERROR(dout, getInterfaceName() <<"'s file "<< fileName <<" was either not found or could not be opened");
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

    catch ( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionReadFromFile in interface"<< getInterfaceName());
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionReadFromFile in interface "<< getInterfaceName() <<" is dead!");
    }
}
