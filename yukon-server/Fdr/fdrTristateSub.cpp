
#include "precompiled.h"
#include <wininet.h>
#include <fcntl.h>
#include <io.h>

#include "dllbase.h"
#include "fdrTriStateSub.h"

#include "ctitime.h"
#include "ctidate.h"

using namespace std;
using std::string;

FDRTriStateSub * Interface;

const CHAR * FDRTriStateSub::KEY_PORT_NUMBER = "FDR_TRISTATESUB_PORT_NUMBER";
const CHAR * FDRTriStateSub::KEY_TRIES = "FDR_TRISTATESUB_NUMBER_OF_TRIES";
const CHAR * FDRTriStateSub::KEY_INTERVAL = "FDR_TRISTATESUB_DOWNLOAD_INTERVAL";
const CHAR * FDRTriStateSub::KEY_IP_ADDRESS = "FDR_TRISTATESUB_IP_ADDRESS";
const CHAR * FDRTriStateSub::KEY_PASSWORD = "FDR_TRISTATESUB_PASSWORD";
const CHAR * FDRTriStateSub::KEY_LOGIN = "FDR_TRISTATESUB_LOGIN";
const CHAR * FDRTriStateSub::KEY_SERVER_FILENAME = "FDR_TRISTATESUB_SERVER_FILE";
const CHAR * FDRTriStateSub::KEY_DB_RELOAD_RATE = "FDR_TRISTATESUB_DB_RELOAD_RATE";
const CHAR * FDRTriStateSub::KEY_QUEUE_FLUSH_RATE = "FDR_TRISTATESUB_QUEUE_FLUSH_RATE";
const CHAR * FDRTriStateSub::KEY_FTP_DIRECTORY = "FDR_TRISTATESUB_FTP_DIRECTORY";

FDRTriStateSub::FDRTriStateSub()
: CtiFDRFtpInterface(string("TRISTATESUB"))
{}

FDRTriStateSub::~FDRTriStateSub(){
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, "FDRTristateSub has deconstructed");
    }
}

BOOL FDRTriStateSub::init()
{
    //get FDRManager, pass in the interface name/type call get
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, "FDRTristateSub has started");
    }

    Inherited::init();

    readConfig();
    loadTranslationLists();

    return true;
}

bool FDRTriStateSub::readConfig()
{
    bool successful = true;
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPort(atoi(tempStr.c_str()));
    }
    else
    {
        setPort (INTERNET_DEFAULT_FTP_PORT);
    }

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        setDownloadInterval(atoi(tempStr.c_str()));
    }
    else
    {
        setDownloadInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_SERVER_FILENAME);
    if (tempStr.length() > 0)
    {
        setServerFileName(tempStr);
    }
    else
    {
        setServerFileName(string ("tsload\\load.dat"));
    }

    tempStr = getCparmValueAsString(KEY_FTP_DIRECTORY);
    if (tempStr.length() > 0)
    {
        setFTPDirectory(tempStr);
    }
    else
    {
        setFTPDirectory(string ("\\yukon\\server\\import"));
    }


    tempStr = getCparmValueAsString(KEY_LOGIN);
    if (tempStr.length() > 0)
    {
        setLogin(tempStr);
    }
    else
    {
        setLogin(string ("cannon"));
    }

    tempStr = getCparmValueAsString(KEY_PASSWORD);
    if (tempStr.length() > 0)
    {
        setPassword(tempStr);
    }
    else
    {
        setPassword(string ("jesse"));
    }

    tempStr = getCparmValueAsString(KEY_IP_ADDRESS);
    if (tempStr.length() > 0)
    {
        setIPAddress(tempStr);
    }
    else
    {
        setIPAddress(string());
                  successful = false;
    }

    tempStr = getCparmValueAsString(KEY_TRIES);
    if (tempStr.length() > 0)
    {
        setTries(atoi(tempStr.c_str()));
    }
    else
    {
        setTries (3);
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
        // default to one second for tristate, its 2 only points
        setQueueFlushRate (1);
    }

    // default local file name, it changes everytime
    setLocalFileName(string ("rtdata.dat"));

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("FDRTristateSub server file name")  << getServerFileName();
        loglist.add("FDRTristateSub FTP directory")     << getFTPDirectory();
        loglist.add("FDRTristateSub download interval") << getDownloadInterval();
        loglist.add("FDRTristateSub number of tries")   << getTries();
        loglist.add("FDRTristateSub login")             << getLogin();
        loglist.add("FDRTristateSub IP")                << getIPAddress();
        loglist.add("FDRTristateSub db reload rate")    << getReloadRate();

        CTILOG_DEBUG(dout, loglist);
    }

    return successful;
}

void FDRTriStateSub::sendMessageToForeignSys( CtiMessage *msg )
{
}

int FDRTriStateSub::processMessageFromForeignSystem( char* )
{
    return 0;
}

BOOL FDRTriStateSub::run( void )
{
    return Inherited::run();
}

BOOL FDRTriStateSub::stop( void )
{
    Inherited::stop();
    return true;
}

int FDRTriStateSub::fail()
{
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, "FDRTristateSub failed decoding the File");
    }
    return ClientErrors::None;
}

int FDRTriStateSub::decodeFile()
{
    //Open file
    ifstream file(getLocalFileName().c_str(), ios::in);
    CtiFDRPoint         point;

    if( file.is_open() ){

        //if ok call readInFile
        std::list<string> strs = readInFile(file);
        file.close();
        remove(getLocalFileName().c_str());
        //pass returned std::list to processData if not empty
        if( !(strs.size() > 0) )
            return 1;

        std::list<StringMessageContainer> msgs = processData( strs );
        //Check Size of both  Lists, they should be the same, if messages is smaller,
        //there were errors with some input.

        //Find Point(s) to send to *see tristate*
        //send messages returned from process data.
        std::list<StringMessageContainer>::iterator itr = msgs.begin();
        for( ;itr != msgs.end(); itr++ ){
            bool flag = findTranslationNameInList ( itr->getName(), getReceiveFromList(), point);
            if ((flag == true) &&
                ((point.getPointType() == AnalogPointType) ||
                 (point.getPointType() == PulseAccumulatorPointType) ||
                 (point.getPointType() == DemandAccumulatorPointType) ||
                 (point.getPointType() == CalculatedPointType)))
            {
                CtiMessage* m = itr->getMessage().get();
                CtiPointDataMsg *pData = (CtiPointDataMsg*)((CtiPointDataMsg*)m)->replicateMessage();
                pData->setId( point.getPointID());
                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, itr->getName() <<" "<< ((CtiPointDataMsg*)m)->getValue());
                }
            }
            else
            {
                if (flag == false)
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Translation for analog point "<< itr->getName() <<" from "<< getInterfaceName() <<" was not found");
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Analog point "<< itr->getName() <<" from "<< getInterfaceName() <<
                            " was mapped incorrectly to non-analog point "<< point.getPointID());
                }
            }
        }


        return 0;
    }else
        return 1; //fail() is called if we return bad
}
std::list<string> FDRTriStateSub::readInFile( istream & io )
{
    std::list<string> stringList;
    io.seekg(0,ios::end);
    int length = io.tellg();
    io.seekg(0,ios::beg);

    if( length == -1 )
        return stringList;

    char* data = new char [length+1];

    io.read(data,length);

    //process data into strings
    for( int i = 0; i < length; i++){
        //remove all new lines so we can create a string with this array.
        if( data[i] == '\n' )
            data[i] = '|';
    }
    //end the string, this is not done by the read call.
    data[length] = '\0';
    string dataString(data);
    delete data;

    //tokenize based off '|' and split to individual strings.
    boost::char_separator<char> delim("|");
    Boost_char_tokenizer tokens(dataString, delim);
    Boost_char_tokenizer::iterator tokIter = tokens.begin();

    for( ; tokIter != tokens.end(); tokIter++ ){
        stringList.push_back(string(*tokIter));
    }
    //return the std::list for processing.
    return stringList;
}

std::list<StringMessageContainer> FDRTriStateSub::processData( std::list<string>& stringList )
{
    std::list<StringMessageContainer> msgs;
    StringMessageContainer msg;

    std::list<string>::iterator itr = stringList.begin();

    for( ; itr != stringList.end() ; itr++ ){
        string str = *itr;
        int pos = 0;

        //remove '"'
        string::iterator itr2 = str.begin();
        for( ; itr2 != str.end() ; ){
            if( *itr2 == '"' )
                itr2 = str.erase(itr2);
            else
                itr2++;
        }

        //tokenize based off ',' and split to individual strings.
        boost::char_separator<char> delim(",");
        Boost_char_tokenizer tokens(str, delim);

        msg = generateMessage( tokens );
        if( msg.getMessage().get() != NULL )
            msgs.push_back(msg);

    }
    //return std::list
    return msgs;
}

//return list if we handle multiple points
StringMessageContainer FDRTriStateSub::generateMessage( Boost_char_tokenizer& tokens )
{
    StringMessageContainer msg;
    bool error = false;
    //basic validity checks,  like # of tokens, size of first tokens (ones that are a constant size)
    Boost_char_tokenizer::iterator itr = tokens.begin();
    string time;
    string name;
    string uom;
    string val;

    if ( itr != tokens.end() ){
        time = *itr;
        itr++;
    }else
        error = true;

    if ( error == false && itr != tokens.end()  ){
        name = *itr;
        itr++;
    }else
        error = true;

    if ( error == false && itr != tokens.end()  ){
        uom = *itr;
        itr++;
    }else
        error = true;

    if ( error == false && itr != tokens.end()  ){
        val = *itr;
        itr++;
    }else
        error = true;

    if( error == true )
        return msg;

    //Translate Date to CtiTime
    //20070516101500  2007-05-16-10:15:00
    if(time.size() != 14 )
        error = true;

    if( error == false ){

        CtiTime t( CtiDate( atoi(string(time,6,2).c_str()),atoi(string(time,4,2).c_str()),atoi(string(time,0,4).c_str()) ),
                   atoi(string(time,8,2).c_str()), atoi(string(time,10,2).c_str()), atoi(string(time,12,2).c_str())  );
        if( !t.isValid() )
            error = true;

        double d = atof( val.c_str() );

        if( d == 0.0)
            error = true;

        if( error == false){
            //generate new message to send to dispatch
            //We will set the ID later when we find points.
            CtiPointDataMsg *pData = new CtiPointDataMsg(1,d,NormalQuality, AnalogPointType);
            pData->setTime(t);
            msg.setName(name);
            msg.setMessage( pData );
        }
    }
    return msg;
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
        Interface = new FDRTriStateSub();
        Interface->init();

        // now start it up
        return Interface->run();
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

        Interface->stop();
        delete Interface;
        Interface = 0;

        return 0;
    }
#ifdef __cplusplus
}
#endif


StringMessageContainer::StringMessageContainer()
{
    name = "default";
}

StringMessageContainer::~StringMessageContainer()
{}

void StringMessageContainer::setMessage( CtiMessage* m )
{
    boost::shared_ptr<CtiMessage> ptr(m);
    msg = ptr;
}
void StringMessageContainer::setName( string n )
{
    name = string(n);
}
string StringMessageContainer::getName()
{
    return name;
}
boost::shared_ptr<CtiMessage> StringMessageContainer::getMessage()
{
    return msg;
}
