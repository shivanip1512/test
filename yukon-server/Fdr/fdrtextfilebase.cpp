/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrtextfilebase.cpp
*
*    DATE: 04/14/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextfilebase.cpp-arc  $
*    REVISION     :  $Revision: 1.4.24.1 $
*    DATE         :  $Date: 2008/11/13 17:23:48 $
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
      $Log: fdrtextfilebase.cpp,v $
      Revision 1.4.24.1  2008/11/13 17:23:48  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.4  2005/12/20 17:17:15  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3.2.3  2005/08/12 19:53:46  jliu
      Date Time Replaced

      Revision 1.3.2.2  2005/07/14 22:26:56  jliu
      RWCStringRemoved

      Revision 1.3.2.1  2005/07/12 21:08:37  jliu
      rpStringWithoutCmpParser

      Revision 1.3  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.2  2003/04/22 20:44:45  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work


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
#include "fdrtextfilebase.h"


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

bool CtiFDRTextFileBase::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
    return TRUE;
}

INT CtiFDRTextFileBase::processMessageFromForeignSystem (CHAR *data)
{
    return NORMAL;
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
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Seeing this error is bad, child class should have implemented the function loadTranslationLists()" << endl;
    }
    return true;
}


