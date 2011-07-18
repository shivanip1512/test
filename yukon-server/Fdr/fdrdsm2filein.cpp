#include "precompiled.h"

#include <wininet.h>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrdsm2filein.h"
#include "ctitokenizer.h"
#include "utility.h"

using std::string;
using std::endl;
using std::vector;

CtiFDR_Dsm2Filein * textImportInterface;

const CHAR * CtiFDR_Dsm2Filein::KEY_INTERVAL = "FDR_DSM2FILEIN_INTERVAL";
const CHAR * CtiFDR_Dsm2Filein::KEY_FILENAME = "FDR_DSM2FILEIN_FILENAME";
const CHAR * CtiFDR_Dsm2Filein::KEY_DRIVE_AND_PATH = "FDR_DSM2FILEIN_DRIVE_AND_PATH";
const CHAR * CtiFDR_Dsm2Filein::KEY_DB_RELOAD_RATE = "FDR_DSM2FILEIN_DB_RELOAD_RATE";
const CHAR * CtiFDR_Dsm2Filein::KEY_QUEUE_FLUSH_RATE = "FDR_DSM2FILEIN_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Dsm2Filein::KEY_DELETE_FILE = "FDR_DSM2FILEIN_DELETE_FILE";
const CHAR * CtiFDR_Dsm2Filein::KEY_USE_SYSTEM_TIME = "FDR_DSM2FILEIN_USE_SYSTEM_TIME";


// Constructors, Destructor, and Operators
CtiFDR_Dsm2Filein::CtiFDR_Dsm2Filein()
: CtiFDRTextFileBase(string("DSM2FILEIN"))
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;
    init();

}

CtiFDR_Dsm2Filein::~CtiFDR_Dsm2Filein()
{
}


bool CtiFDR_Dsm2Filein::shouldDeleteFileAfterImport() const
{
    return _deleteFileAfterImportFlag;
}

CtiFDR_Dsm2Filein &CtiFDR_Dsm2Filein::setDeleteFileAfterImport (bool aFlag)
{
    _deleteFileAfterImportFlag = aFlag;
    return *this;
}


bool CtiFDR_Dsm2Filein::useSystemTime() const
{
    return _useSystemTime;
}

CtiFDR_Dsm2Filein &CtiFDR_Dsm2Filein::setUseSystemTime (bool aFlag)
{
    _useSystemTime = aFlag;
    return *this;
}
BOOL CtiFDR_Dsm2Filein::init( void )
{
    // init the base class
    Inherited::init();
    _threadReadFromFile = rwMakeThreadFunction(*this,
                                               &CtiFDR_Dsm2Filein::threadFunctionReadFromFile);

    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_Dsm2Filein::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_Dsm2Filein::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadReadFromFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_Dsm2Filein::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_Dsm2Filein::stop( void )
{
    _threadReadFromFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


CtiTime CtiFDR_Dsm2Filein::ForeignToYukonTime (string aTime)
{
    struct tm ts;
    CtiTime retVal;

    if (aTime.length() == 19)
    {
        try
        {
            if (sscanf (aTime.c_str(),
                        "%2ld/%2ld/%4ld %2ld:%2ld:%2ld",
                        &ts.tm_mon,
                        &ts.tm_mday,
                        &ts.tm_year,
                        &ts.tm_hour,
                        &ts.tm_min,
                        &ts.tm_sec) != 6)
            {
                retVal = PASTDATE;
            }
            else
            {
                ts.tm_year -= 1900;
                ts.tm_mon--;
                ts.tm_isdst = CtiTime().isDST();

                try
                {
                    retVal = CtiTime(&ts);

                    // if CtiTime can't make a time ???
                    if (!retVal.isValid())
                    {
                        retVal = PASTDATE;
                    }
                }
                catch (...)
                {
                    retVal = PASTDATE;
                }
            }
        }
        catch (...)
        {
            retVal = PASTDATE;
        }
    }
    else
        retVal = PASTDATE;
    return retVal;
}

USHORT CtiFDR_Dsm2Filein::ForeignToYukonQuality (string aQuality)
{
    USHORT Quality = NonUpdatedQuality;

    if (ciStringEqual(aQuality,"N"))
        Quality = NormalQuality;
    if (ciStringEqual(aQuality,"M"))
        Quality = ManualQuality;

    return(Quality);
}


bool CtiFDR_Dsm2Filein::processFunctionOne (string &aLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool pointValidFlag=true;
    string tempString1;                // Will receive each token

    boost::char_separator<char> sep(",\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    CtiFDRPoint         point;
    int fieldNumber = 1, quality = 0;
    double value = 0.0;
    CHAR   action[60];
    string linetimestamp,translationName,desc;
    CtiTime pointtimestamp;


    /****************************
    * function 1 is of the following format
    * function,id,value,quality,timestamp,daylight savings flag
    *****************************
    */
    while ( tok_iter != cmdLine.end() && pointValidFlag)
    {
        tempString1 = *tok_iter; tok_iter++;
        switch (fieldNumber)
        {
            case 1:
                {
                    // this the function number so we do nothing with this
                    break;
                }
            case 2:
                {
                    // throw on the function header for the translation name
                    translationName=tempString1;
                    tempString1="1-----" + tempString1;

                    // lock the list while we're returning the point
                    {
                        CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                        pointValidFlag = findTranslationNameInList (tempString1, getReceiveFromList(), point);
                    }

                    if (pointValidFlag != true)
                    {
                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Translation for point " << translationName;
                                dout << " from " << getFileName() << " was not found" << endl;
                            }
                            desc = getFileName() + string ("'s point ") + translationName + string( " is not listed in the translation table");
                            _snprintf(action,60,"%s", translationName.c_str());
                            logEvent (desc,string (action));
                        }
                    }

                    break;
                }
            case 3:
                {
                    value = atof(tempString1.c_str());
                    break;
                }
            case 4:
                {
                    quality = ForeignToYukonQuality(tempString1);
                    break;
                }

            case 5:
                {
                    linetimestamp=tempString1;
                    break;
                }
            case 6:
                {
                    linetimestamp=linetimestamp + " " + tempString1;

                    if (useSystemTime())
                    {
                        pointtimestamp=CtiTime();
                    }
                    else
                    {
                        pointtimestamp = ForeignToYukonTime (linetimestamp);
                        if (pointtimestamp == PASTDATE)
                        {
                            pointValidFlag = false;
                        }
                    }
                    break;
                }
            default:
                break;
        }
        fieldNumber++;
    }

    if (pointValidFlag)
    {
        retCode = buildAndAddPoint (point,value,pointtimestamp,quality,translationName,aRetMsg);
    }
    return retCode;
}


bool CtiFDR_Dsm2Filein::processFunctionTwo (string &aLine, CtiMessage **aRetMsg)
{
        bool retCode = false;
    bool pointValidFlag=true;
    string tempString1;                // Will receive each token
    CtiTokenizer cmdLine(aLine);           // Tokenize the string a
    CtiFDRPoint         point;
    int fieldNumber = 1, quality = 0;
    double value = 0.0;
    CHAR   action[60];
    string linetimestamp,translationName,desc,lookupName;
    CtiTime pointtimestamp;


    /****************************
    * function 1 is of the following format
    * function,id,value,quality,timestamp,daylight savings flag
    *****************************
    */
    while (!(tempString1 = cmdLine(",\r\n")).empty() && pointValidFlag)
    {
        switch (fieldNumber)
        {
            case 1:
                {
                    // this the function number so we do nothing with this
                    break;
                }
            case 2:
                {
                    // throw on the function header for the translation name
                    translationName=tempString1 + ":";
                    lookupName="2-----" + tempString1 + ":";
                    break;
                }
            case 3:
                {
                    translationName+=tempString1;
                    lookupName+=tempString1;

                    // lock the list while we're returning the point
                    {
                        CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                        pointValidFlag = findTranslationNameInList (lookupName.c_str(), getReceiveFromList(), point);
                    }

                    if (pointValidFlag != true)
                    {
                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Translation for point " << translationName;
                                dout << " from " << getFileName() << " was not found" << endl;
                            }

                            desc = getFileName().c_str() + string ("'s point ") + translationName + string( " is not listed in the translation table");
                            _snprintf(action,60,"%s", translationName.c_str());
                            logEvent ( string(desc.c_str()),
                                       string(action));
                        }
                    }

                    break;
                }
            case 4:
                {
                    value = atof(tempString1.c_str());
                    break;
                }
            case 5:
                {
                    quality = ForeignToYukonQuality(tempString1.c_str());
                    break;
                }

            case 6:
                {
                    linetimestamp=tempString1;
                    break;
                }
            case 7:
                {
                    linetimestamp=linetimestamp + " " + tempString1;

                    if (useSystemTime())
                    {
                        pointtimestamp=CtiTime();
                    }
                    else
                    {
                        pointtimestamp = ForeignToYukonTime (linetimestamp.c_str());
                        if (pointtimestamp == PASTDATE)
                        {
                            pointValidFlag = false;
                        }
                    }
                    break;
                }
            default:
                break;
        }
        fieldNumber++;
    }

    if (pointValidFlag)
    {
        retCode = buildAndAddPoint (point,value,pointtimestamp,quality,translationName.c_str(),aRetMsg);
    }
    return retCode;
}


bool CtiFDR_Dsm2Filein::buildAndAddPoint (CtiFDRPoint &aPoint,
                                          DOUBLE aValue,
                                          CtiTime aTimestamp,
                                          int aQuality,
                                          string aTranslationName,
                                          CtiMessage **aRetMsg)
{
    string desc;
    CHAR   action[60];
    bool retCode = false;

    // figure out what it should be now
    switch (aPoint.getPointType())
    {
        case AnalogPointType:
        case PulseAccumulatorPointType:
        case DemandAccumulatorPointType:
        case CalculatedPointType:
        {
            DOUBLE value = aValue;

            value *= aPoint.getMultiplier();
            value += aPoint.getOffset();

            *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                           value,
                                           aQuality,
                                        AnalogPointType);
                                    ((CtiPointDataMsg *)*aRetMsg)->setTime(aTimestamp);

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Analog point " << aTranslationName;
                dout << " value " << value << " from " << getFileName() << " assigned to point " << aPoint.getPointID() << endl;;
            }
            retCode = true;
            break;
        }
        case StatusPointType:
        {
            // check for control functions
            if (aPoint.isControllable())
            {
                // make sure the value is valid
                if ((aValue == OPENED) || (aValue == CLOSED))
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Control point " << aTranslationName;
                        if (aValue == OPENED)
                        {
                            dout << " control: Open " ;
                        }
                        else
                        {
                            dout << " control: Closed " ;
                        }

                        dout <<" from " << getFileName() << " and processed for point " << aPoint.getPointID() << endl;;
                    }

                    // build the command message and send the control
                    *aRetMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                    ((CtiCommandMsg *)*aRetMsg)->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                    ((CtiCommandMsg *)*aRetMsg)->insert(0);                   // device id, unknown at this point, dispatch will find it
                    ((CtiCommandMsg *)*aRetMsg)->insert(aPoint.getPointID());  // point for control
                    ((CtiCommandMsg *)*aRetMsg)->insert(aValue);
                    retCode = true;

                }
                else
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Invalid control state " << aValue;
                            dout << " for " << aTranslationName << " received from " << getFileName() << endl;
                        }

                        CHAR state[20];
                        _snprintf (state,20,"%.0f",aValue);
                        desc = getFileName() + string (" control point received with an invalid state ") + string (state);
                        _snprintf(action,60,"%s for pointID %d",
                                  aTranslationName.c_str(),
                                  aPoint.getPointID());
                        logEvent (desc,string (action));
                    }
                }
            }
            else
            {
                if ((aValue == OPENED) || (aValue == CLOSED))
                {
                    string tracestate;
                    if (aValue == OPENED)
                    {
                        tracestate = string ("Open");
                    }
                    else
                    {
                        tracestate = string ("Closed");
                    }
                    *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                                aValue,
                                                aQuality,
                                                StatusPointType);
                                            ((CtiPointDataMsg*)*aRetMsg)->setTime(aTimestamp);
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Status point " << aTranslationName;
                        dout << " new state: " << tracestate;
                        dout <<" from " << getFileName() << " assigned to point " << aPoint.getPointID() << endl;;
                    }
                    retCode = true;
                }
                else
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                   {
                       {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Status point " << aTranslationName;
                            dout << " received an invalid state " << (int)aValue;
                            dout <<" from " << getFileName() << " for point " << aPoint.getPointID() << endl;;
                       }
                        CHAR state[20];
                        _snprintf (state,20,"%.0f",aValue);
                        desc = getFileName() + string (" status point received with an invalid state ") + string (state);
                        _snprintf(action,60,"%s for pointID %d",
                                aTranslationName.c_str(),
                                aPoint.getPointID());
                        logEvent (desc,string (action));
                   }
                }
            }
            break;
        }
    }
    return retCode;
}


bool CtiFDR_Dsm2Filein::validateAndDecodeLine (string &aLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool flag;
    std::transform(aLine.begin(), aLine.end(), aLine.begin(), tolower);
    string tempString1;                // Will receive each token

    boost::char_separator<char> sep(",\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    CtiFDRPoint         point;
    int function;

    // grab the function number
    if ( tok_iter != cmdLine.end() )
    {
        tempString1 = *tok_iter; tok_iter++;
        function = atoi (tempString1.c_str());

        // check the function number
        switch (function)
        {
            case 1:
            {
                /****************************
                * function 1 is of the following format
                * id,value,quality,timestamp,daylight savings flag
                *****************************
                */
                retCode=processFunctionOne (aLine,aRetMsg);
                break;
            }
            case 2:
            {
                /****************************
                * function 2 is of the following format
                * devicename,pointname,value,quality,timestamp,daylight savings flag
                *****************************
                */
                retCode=processFunctionTwo (aLine,aRetMsg);
                break;
            }
            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unknown function number from import file" << endl;
                break;
            }
        }
    }

    return retCode;
}



int CtiFDR_Dsm2Filein::readConfig( void )
{
    int         successful = TRUE;
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr.c_str()) <=1)
        {
            setInterval(1);
        }
        else
        {
            setInterval(atoi(tempStr.c_str()));
        }
    }
    else
    {
        setInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }
    else
    {
        setFileName(string ("yukon.txt"));
    }

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(string ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    }
    else
    {
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    setDeleteFileAfterImport(true);
    tempStr = getCparmValueAsString(KEY_DELETE_FILE);
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"false"))
        {
            setDeleteFileAfterImport (false);
        }
    }

    setUseSystemTime(false);
    tempStr = getCparmValueAsString(KEY_USE_SYSTEM_TIME);
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr, "true"))
        {
            setUseSystemTime (true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " DSM2 filein file name " << getFileName() << endl;
        dout << CtiTime() << " DSM2 filein directory " << getDriveAndPath() << endl;
        dout << CtiTime() << " DSM2 filein interval " << getInterval() << endl;
        dout << CtiTime() << " DSM2 filein dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << CtiTime() << " DSM2 filein db reload rate " << getReloadRate() << endl;

        if (shouldDeleteFileAfterImport())
            dout << CtiTime() << " DSM2 filein file will be deleted after import" << endl;
        else
            dout << CtiTime() << " DSM2 filein file will NOT be deleted after import" << endl;
        if (useSystemTime())
            dout << CtiTime() << " DSM2 filein will stamp data with time at import" << endl;
        else
            dout << CtiTime() << " DSM2 filein file stamp data according to time included in the import" << endl;


    }



    return successful;
}


/************************************************************************
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*               specified direction
*
*************************************************************************
*/
bool CtiFDR_Dsm2Filein::loadTranslationLists()
{
    bool successful = false;
    bool foundPoint = false;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_RECEIVE));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
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

                // get iterator on send list
                CtiFDRManager::spiterator myIterator = pointList->getMap().begin();

                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    translateSinglePoint(myIterator->second);
                }   // end for interator

                // lock the receive list and remove the old one
                {
                    CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                    if (getReceiveFromList().getPointList() != NULL)
                    {
                        getReceiveFromList().deletePointList();
                    }
                    getReceiveFromList().setPointList (pointList);
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

bool CtiFDR_Dsm2Filein::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;
    string tempString2;
    string translation_name;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
            //dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
        }
        tempString2 = translationPoint->getDestinationList()[x].getTranslationValue("Option Number");
        if (!tempString2.empty())
        {
            translation_name = tempString2 + "-----";
            successful = true;
        }

        tempString2 = translationPoint->getDestinationList()[x].getTranslationValue("Point ID");
        if (!tempString2.empty() && successful == true)
        {
            translation_name+=tempString2;
            translationPoint->getDestinationList()[x].setTranslation (translation_name);
            // we end up with a translation of function #-----id
        }
    }
    return successful;
}

/**************************************************************************
* Function Name: CtiFDRTextFileBase::threadFunctionReadFromFile (void )
*
* Description: thread that waits and then grabs the file for processing
*
***************************************************************************
*/
void CtiFDR_Dsm2Filein::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);
    string action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    int attemptCounter=0;

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

                fptr = fopen( fileName, "r");
                while ((fptr == NULL) && (attemptCounter < 10))
                {
                    attemptCounter++;
                    pSelf.sleep(1000);
                    pSelf.serviceCancellation( );
                }

                if( fptr == NULL )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getInterfaceName() << "'s file " << string (fileName) << " was either not found or could not be opened" << endl;
                    }
                }
                else
                {
                    vector<string>     pointVector;

                    // load list in the command vector
                    while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
                    {
                        string entry (workBuffer);
                        pointVector.push_back (entry);
                    }

                    fclose(fptr);
                    if( ferror( fptr ) != 0 )
                    {
                        pointVector.erase(pointVector.begin(), pointVector.end());
                    }
                    else
                    {
                        // retrieve each line in order
                        int totalLines = pointVector.size();
                        int lineCnt = 0;
                        CtiMessage      *retMsg=NULL;
                        while (lineCnt < totalLines )
                        {
                            if(validateAndDecodeLine( pointVector[lineCnt], &retMsg ))
                            {
                                queueMessageToDispatch (retMsg);
                            }
                            lineCnt++;
                        }
                        pointVector.erase(pointVector.begin(), pointVector.end());
                    }
                }

                if (shouldDeleteFileAfterImport())
                {
                    DeleteFile (fileName);
                }

                refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRDsm2Filein::threadFunctionReadFromFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRTextIMport::threadFunctionReadFromFile  " << getInterfaceName() << " is dead! " << endl;
    }
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
        textImportInterface = new CtiFDR_Dsm2Filein();

        // now start it up
        return textImportInterface->run();
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

        textImportInterface->stop();
        delete textImportInterface;
        textImportInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


