#include "yukon.h"


#include "dllbase.h"
#include <fstream>
#include <iostream>
#include "fdrwabash.h"

using namespace std;
using std::string;

FDRWabash * wabashInterface;

const char * FDRWabash::KEY_DB_RELOAD = "FDR_WABASH_DB_RELOAD_RATE";
const char * FDRWabash::KEY_INITIAL_LOAD = "FDR_WABASH_WRITE_INITIAL_LOAD";

FDRWabash::FDRWabash() :
    CtiFDRInterface(string("WABASH")),
    _writeInitialLoad(false)
{}

BOOL FDRWabash::init()
{
    //get FDRManager, pass in the interface name/type call get
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRWabash has started. \n";
    }

    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (recList);
    recList = NULL;

    Inherited::init();

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRWabash has initialized. \n";
    }
    setReloadRate(300);
    readConfig();

    return loadTranslationLists();
}

bool FDRWabash::readConfig()
{
    //FDR_WABASH_DB_RELOAD_RATE     :  360
    //FDR_WABASH_INITAL_LOAD        :  false

    int ok = true;
    string tempString;
    _writeInitialLoad = false;

    tempString = getCparmValueAsString(KEY_DB_RELOAD);
    if (tempString.length() > 0){
        setReloadRate (atoi(tempString.c_str()));
    } else{
        setReloadRate (86400);
    }
    tempString = getCparmValueAsString(KEY_INITIAL_LOAD);
    if (tempString.length() > 0)
    {
        if (string_equal(tempString,"true"))
        {
            _writeInitialLoad = true;
        }
    }

    return true;
}

FDRWabash::~FDRWabash(){
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRWabash has deconstructed. \n";
    }
}

bool FDRWabash::loadTranslationLists()
{
    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(getInterfaceName(),string (FDR_INTERFACE_SEND)); //check into what the second parameter means for the manager

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            if(pointList->entries() > 0)
            {
                CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();
                for ( ; myIterator != pointList->getMap().end(); ++myIterator)
                {
                    translateSinglePoint(myIterator->second);
                }
                // lock the receive list and remove the old one
                {
                    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
                    if (getSendToList().getPointList() != NULL)
                    {
                        getSendToList().deletePointList();
                    }
                    getSendToList().setPointList (pointList);
                }
            }
        }else{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - FDR - Wabash - in Loading Translation**** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

     }catch(...){
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " **** Checkpoint - FDR - Wabash - in Loading Translation**** " << __FILE__ << " (" << __LINE__ << ")" << endl;
     }

     resetForInitialLoad();

     return true;
}

bool FDRWabash::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    string pointID;
    string filename;
    string drivePath;

    for (int x=0; x < translationPoint->getDestinationList().size(); x++)
    {
        //go through destination list?
        pointID = translationPoint->getDestinationList()[x].getTranslationValue("SchedName");
        drivePath = translationPoint->getDestinationList()[x].getTranslationValue("Path");
        filename = translationPoint->getDestinationList()[x].getTranslationValue("Filename");

        if( !pointID.empty() && !drivePath.empty() && !filename.empty() )
        {
            translationPoint->getDestinationList()[x].setTranslation (pointID);

            //This only allows for one file name. In the event we want to use multiple files for different points
            //the filename and path will need to be added into CtiFDRPoint or tracked per point by another method.
            _fileName = filename;
            _path = drivePath;
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " PointId: " << pointID << " at : " << _path << " " << _fileName << std::endl;
            }
            //Also the last point loaded gets the final say in the path and filename
        }
    }
    return true;
}

void FDRWabash::resetForInitialLoad()
{
    CtiFDRPointSPtr point;
    CtiFDRManager* mgrPtr = getSendToList().getPointList();

    CtiFDRManager::writerLock guard(mgrPtr->getLock());

    CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();
    for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator)
    {
        point = (*myIterator).second;
        point->setLastTimeStamp( CtiTime(YUKONEOT) );
    }
}

bool FDRWabash::sendMessageToForeignSys( CtiMessage *msg )
{
    bool               ok = true;
    bool               initialmsg = false;
    bool               equivalentValue = false;
    CtiFDRPointSPtr point;
    CtiTime aTime;
    string dateStr,schedName,action;

    CtiPointDataMsg* aMessage = (CtiPointDataMsg*)msg;
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDR has sent a message to wabash. \n";
    }
    ////
    // Do the formating we can before locking the mux to get the schedName;

    //get the status/command to string START || STOP from pointdata
    if ( aMessage->getValue() == 0 )
    {
        action = string("STOP");
    }
    else if ( aMessage->getValue() == 1 )
    {
        action = string("START");
    }
    else
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Warning: Data will not be sent for Id: " << aMessage->getId() << endl;
            dout << CtiTime() << "          Expected a 0 or 1, got: " << aMessage->getValue() << endl;
        }

        return false;
    }

    //get the time from pdata and format to match "11/08/06 14:00:19,AEP-AC-COOL-75%,STOP"
    aTime = aMessage->getTime();
    dateStr = aTime.asString();
    //drop the first two digits of the year
    dateStr.erase(6,2);

    //get the schedName from the pointData
    {
        CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
        CtiFDRManager* mgrPtr = getSendToList().getPointList();
        CtiFDRManager::readerLock guard(mgrPtr->getLock());

        CtiFDRManager::spiterator  itr;
        itr = mgrPtr->getMap().find(aMessage->getId());
        if( itr != mgrPtr->getMap().end() )
        {
            point = (*itr).second;
        }

        if (mgrPtr->getMap().size() == 0 || !point)
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Warning: FDRWabash will not write data to the file. Point is not in the watch list." << std::endl;
            }

            return false;
        }
        else
        {
            //no change, so do not print
            //or
            //dont print, this is the initial message received from dispatch.

            if( (point->getValue() == aMessage->getValue()) )
            {
                equivalentValue = true;
            }
            if( point->getLastTimeStamp() == CtiTime(YUKONEOT) )
            {
                initialmsg = true;
            }

            // store away the latest values.
            schedName = point->getDestinationList()[0].getTranslation();
            point->setValue (aMessage->getValue());
            point->setLastTimeStamp(aMessage->getTime());
        }
    }

    //form string with these pieces and call writeDataToFile( string )
    //write out if initialmsg && _writeInitialLoad regardless of value's being equal
    //write out if the value has changed always
    //do not write initial load at all if _writeInitialLoad == false
    //do not write if same values
    if( initialmsg )
    {
        if (_writeInitialLoad){
            writeDataToFile( dateStr + "," + schedName + "," + action );
        }
        else
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " FDRWabash: Initial Data will not be written to the file. FDR_WABASH_WRITE_INITIAL_LOAD is false. \n";
            }
        }
    }
    else
    {
        if ( !equivalentValue )
        {
            writeDataToFile( dateStr + "," + schedName + "," + action );
        }
        else
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " FDRWabash: Data will not be written to file. Duplicate data.\n";
            }
        }
    }


    return ok;
}

string FDRWabash::getFilename()
{
    return _fileName;
}
string FDRWabash::getPath()
{
    return _path;
}
void FDRWabash::setFilename( string fileName )
{
    _fileName = fileName;
}
void FDRWabash::setPath( string path )
{
    _path = path;
}
bool FDRWabash::writeDataToFile( string cmd )
{
    ofstream file;
    string pathandfile = _path;// + _fileName );
    pathandfile.append( _fileName );

    //open file, append mode if it exists.
    do{
        file.open( pathandfile.c_str(), ios::app );
        //if file in use, wait and try again.
        if ( file.is_open() )
            break;
        else
            Sleep(200);
    }while( true );//retry limit?

    //print strings ( and new line )
    file << cmd << std::endl;

    // close file
    file.close();

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRWabash: New Data written to the file.\n";

        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            dout << CtiTime() << " Data:" << cmd << endl;
        }
    }

    return true;
}

int FDRWabash::processMessageFromForeignSystem( char* )
{
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRWabash is trying to process a message from FS. Not supposed to happen!\n";
        return 0;
}

/****************************************************************************************
*
*      Here Starts some C functions that are used to Start the
*      Interface and Stop it from the Main() of FDR.EXE.
*
*/

#ifdef __cplusplus
extern "C" {
#endif


/************************************************************************
* Function Name: Extern C int RunInterface(void)
*
* Description: This is used to Start the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function creates a global FDRCygnet Object and then
*              calls its run method to cank it up.
*
*************************************************************************
*/

    DLLEXPORT int RunInterface(void)
    {

        // make a point to the interface
        wabashInterface = new FDRWabash();
        wabashInterface->init();
        // now start it up
        return wabashInterface->run();
    }

/************************************************************************
* Function Name: Extern C int StopInterface(void)
*
* Description: This is used to Stop the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function stops a global FDRCygnet Object and then
*              deletes it.
*
*************************************************************************
*/
    DLLEXPORT int StopInterface( void )
    {

        wabashInterface->stop();
        delete wabashInterface;
        wabashInterface = 0;

        return 0;
    }
#ifdef __cplusplus
}
#endif

