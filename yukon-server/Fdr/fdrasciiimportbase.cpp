/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrasciiimportbase.cpp
*
*    DATE: 05/31/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrasciiimportbase.cpp-arc  $
*    REVISION     :  $Revision: 1.7 $
*    DATE         :  $Date: 2005/02/10 23:23:50 $
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


#include <windows.h>

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrasciiimportbase.h"


// Constructors, Destructor, and Operators
CtiFDRAsciiImportBase::CtiFDRAsciiImportBase(RWCString &aInterface)
: CtiFDRInterface(aInterface),
    iFileName(RWCString ("dsmdata.txt")),
    iDriveAndPath (RWCString("\\yukon\\server\\import")),
    iImportInterval(900),
    iLinkStatusID(0),
    iDeleteFileAfterImportFlag(true)
{ 
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
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

RWCString & CtiFDRAsciiImportBase::getFileName()
{
    return iFileName;
}

RWCString  CtiFDRAsciiImportBase::getFileName() const
{
    return iFileName;
}

CtiFDRAsciiImportBase &CtiFDRAsciiImportBase::setFileName (RWCString aFile)
{
    iFileName = aFile;
    return *this;
}

RWCString & CtiFDRAsciiImportBase::getDriveAndPath()
{
    return iDriveAndPath;
}

RWCString  CtiFDRAsciiImportBase::getDriveAndPath() const
{
    return iDriveAndPath;
}

CtiFDRAsciiImportBase &CtiFDRAsciiImportBase::setDriveAndPath (RWCString aPath)
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
*				specified direction
* 
*************************************************************************
*/
bool CtiFDRAsciiImportBase::loadTranslationLists()
{
    bool                successful(FALSE);
    CtiFDRPoint *       translationPoint = NULL;
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false;
    RWDBStatus          listStatus;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(), 
                                                       RWCString (FDR_INTERFACE_RECEIVE));

        // keep the status
        listStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( listStatus.errorCode() == (RWDBStatus::ok))
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
                CtiFDRManager::CTIFdrPointIterator  myIterator(getReceiveFromList().getPointList()->getMap());
                int x;

                for ( ; myIterator(); )
                {
                    foundPoint = true;
                    translationPoint = myIterator.value();

                    for (x=0; x < translationPoint->getDestinationList().size(); x++)
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
                        RWCTokenizer nextTranslate(translationPoint->getDestinationList()[x].getTranslation());

                        if (!(tempString1 = nextTranslate(";")).isNull())
                        {
                            RWCTokenizer nextTempToken(tempString1);

                            // do not care about the first part
                            nextTempToken(":");

                            tempString2 = nextTempToken(";");
                            tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));

                            // now we have a point id
                            if ( !tempString2.isNull() )
                            {
                                translationPoint->getDestinationList()[x].setTranslation (tempString2);
                                successful = true;
                            }
                        }   // first token invalid
                    }
                }   // end for interator

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
                            dout << RWTime() << " No points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
            successful = false;
        }

    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error loading translation lists for " << getInterfaceName() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error loading translation lists for " << getInterfaceName() << endl;
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
    RWTime         timeNow;
    RWTime         refreshTime(rwEpoch);
    RWCString action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is

    try
    {
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = RWTime();

            // now is the time to get the file
            if (timeNow >= refreshTime)
            {
                _snprintf (fileName, 200, "%s\\%s",getDriveAndPath(),getFileName());
                if( (fptr = fopen( fileName, "r")) == NULL )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << getInterfaceName() << "'s file " << RWCString (fileName) << " was either not found or could not be opened" << endl;
                    }
                }
                else
                {
                    vector<RWCString>     valueVector;

                    // load list in the command vector
                    while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
                    {
                        RWCString entry (workBuffer);
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

                refreshTime = RWTime() - (RWTime::now().seconds() % iImportInterval) + iImportInterval;
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
        dout << RWTime() << " Fatal Error:  CtiFDRAsciiImportBase::threadFunctionReadFromFile  " << getInterfaceName() << " is dead! " << endl;
    }
}
