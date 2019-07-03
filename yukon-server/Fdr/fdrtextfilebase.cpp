#include "precompiled.h"

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_multi.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"

using std::string;
using std::endl;

// Constructors, Destructor, and Operators
CtiFDRTextFileBase::CtiFDRTextFileBase(string &aInterface)
: CtiFDRInterface(aInterface),
_textFileParts(),
_linkStatusID(0)
{

}


CtiFDRTextFileBase::~CtiFDRTextFileBase()
{
}


long CtiFDRTextFileBase::getLinkStatusID( void ) const
{
    return _linkStatusID;
}

CtiFDRTextFileBase & CtiFDRTextFileBase::setLinkStatusID(const long aPointID)
{
    _linkStatusID = aPointID;
    return *this;
}

int CtiFDRTextFileBase::getInterval() const
{
    return _textFileParts.getInterval();
}

CtiFDRTextFileBase &CtiFDRTextFileBase::setInterval (int aInterval)
{
    _textFileParts.setInterval(aInterval);
    return *this;
}

string & CtiFDRTextFileBase::getFileName()
{
    return _textFileParts.getFileName();
}

string  CtiFDRTextFileBase::getFileName() const
{
    return _textFileParts.getFileName();
}

CtiFDRTextFileBase &CtiFDRTextFileBase::setFileName (string aFile)
{
    _textFileParts.setFileName(aFile);
    return *this;
}

string & CtiFDRTextFileBase::getDriveAndPath()
{
    return _textFileParts.getDriveAndPath();
}

string  CtiFDRTextFileBase::getDriveAndPath() const
{
    return _textFileParts.getDriveAndPath();
}

CtiFDRTextFileBase &CtiFDRTextFileBase::setDriveAndPath (string aPath)
{
    _textFileParts.setDriveAndPath(aPath);
    return *this;
}

/*************************************************
* Function Name: CtiFDRTextFileBase::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRTextFileBase::init( void )
{
    // init the base class
    Inherited::init();
    return TRUE;
}

/*************************************************
* Function Name: CtiFDRTextFileBase::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDRTextFileBase::run( void )
{

    // crank up the base class
    Inherited::run();
    setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
    sendLinkState (FDR_NOT_CONNECTED);
    return TRUE;
}


/*************************************************
* Function Name: CtiFDRTextFileBase::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDRTextFileBase::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //

    // stop the base class
    Inherited::stop();

    return TRUE;
}

void CtiFDRTextFileBase::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
}

INT CtiFDRTextFileBase::processMessageFromForeignSystem (CHAR *data)
{
    return ClientErrors::None;
}


void CtiFDRTextFileBase::sendLinkState (int aState)
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
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*               specified direction
*
*************************************************************************
*/
bool CtiFDRTextFileBase::loadTranslationLists()
{
    CTILOG_ERROR(dout, "Seeing this error is bad, child class should have implemented the function loadTranslationLists()");

    return true;
}


