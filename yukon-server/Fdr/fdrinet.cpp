/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrinet.cpp
*
*    DATE: 04/27/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrinet.cpp-arc  $
*    REVISION     :  $Revision: 1.26 $
*    DATE         :  $Date: 2008/10/31 15:36:33 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic Interface to the Foreign System
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 with a generic system such as Inet on DSM2.  The data is both status and Analog data.
*                                 Information is exchanged using sockets opened on a predefined socket
*                                 number and also pre-defined messages between the systems.
*
*    ---------------------------------------------------
*    History:
      $Log: fdrinet.cpp,v $
      Revision 1.26  2008/10/31 15:36:33  tspar
      YUK-6649 - Yukon is throwing away point updates from RCCS

      String resize call was putting in nulls when expanding the string. RW strings used a space. String compares were failing because of this.

      Changed resize calls to use space as the padding.

      Revision 1.25  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.24  2008/09/23 15:14:58  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.23  2008/09/15 21:08:48  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.22  2008/06/09 15:48:08  tspar
      YUK-6032 FDR Inet Will not send data

      std::transform() was used incorrectly, after attempting to convert to upper case letters it FDR Inet erased the destination name. When we send a point, we look it up by name. We could not find the connection since the name was erased.

      Revision 1.21  2008/03/27 21:04:17  tspar
      Minor bug fix for fdrinet

      Revision 1.20  2008/03/20 21:27:14  tspar
      YUK-5541 FDR Textimport and other interfaces incorrectly use the boost tokenizer.

      Changed all uses of the tokenizer to have a local copy of the string being tokenized.

      Revision 1.19  2007/04/10 23:42:09  tspar
      Added even more protection against bad input when tokenizing.

      Doing a ++ operation on an token iterator that is already at the end will also assert.

      Revision 1.18  2007/04/10 23:04:35  tspar
      Added some more protection against bad input when tokenizing.

      Revision 1.17  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.16  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.15  2006/04/24 14:47:32  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.14  2006/01/03 20:23:37  tspar
      Moved non RW string utilities from rwutil.h to utility.h

      Revision 1.13  2005/12/20 17:17:13  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.12  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.11  2004/12/21 17:40:56  dsutton
      Bug in the connect code when Yukon acts as the client.  INET was trying to
      connect to port 0 instead of port 1000.  Bug was introduced when updates
      were made to RCCS for Progress and they needed separate listen and
      connect port numbers

      Revision 1.9.2.1  2004/09/28 21:05:02  dsutton
      Finally updated the default reload rate to 86400

      Revision 1.9  2003/12/12 21:58:32  dsutton
      Sending of status points was following Yukon values 0,1 instead of DSM2
      values of 1,2 for open and close.   Caused things to fail for powerlink
      application used to talking to DSM2.  Intercepted the status points now and
      correct the value accordingly

      Revision 1.6.12.2  2003/12/05 03:30:34  dsutton
      Sending of status points was following Yukon values 0,1 instead of DSM2
      values of 1,2 for open and close.   Caused things to fail for powerlink
      application used to talking to DSM2.  Intercepted the status points now and
      correct the value accordingly

      Revision 1.6.12.1  2003/10/31 18:31:53  dsutton
      Updated to allow us to send and receive accumlator points to other systems.
      Oversite from the original implementation

      Revision 1.6  2002/10/14 21:10:54  dsutton
      In the database translation routines, if we failed to hit the database
      we called the load routine again just to get the error code.  Whoops
      The error code is now saved from the original call and printed as needed

      Revision 1.5  2002/09/06 18:54:17  dsutton
      The database load and list swap used to occur before we updated
      the translation names.  I now update the entire temporary list and then
      lock the real list and swap it.  Much faster and doesn't lock the list
      down as long.

      Revision 1.4  2002/08/06 22:02:21  dsutton
      Programming around the error that happens if the dataset is empty when it is
      returned from the database and shouldn't be.  If our point list had more than
      two entries in it before, we fail the attempt and try again in 60 seconds

      Revision 1.3  2002/04/16 15:58:33  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:55  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


      Rev 2.11   05 Apr 2002 11:43:08   dsutton
   fixed (again) a bug where if two connections went down and the second in the list came up, we never tried to attach to the first again.  I think the logic is right this time

      Rev 2.10   27 Mar 2002 17:43:06   dsutton
   fixed a bug where if two connections went away and one was restored, we never tried to restore the other.  Had to do with the order in the list of connections

      Rev 2.9   12 Mar 2002 10:28:52   dsutton
   the listener has been moved into the socketinterface base class so calls to iListener had to be changed to the getter

      Rev 2.8   01 Mar 2002 13:20:14   dsutton
   changed the client list and connection list to vectors, updated the server thread to use a listener object so shutdown is more peacefule,function to walk connection list and update link status points, memory leak in client list loading

      Rev 2.7   20 Feb 2002 08:40:54   dsutton
   moved some debug lines around and changed the level that they show under

      Rev 2.6   15 Feb 2002 14:11:46   dsutton
   changed the default queue flush rate to 1 second

      Rev 2.5   15 Feb 2002 11:22:08   dsutton
    changed the debug settings for a few of the transactions to make them more uniform throughout fdr

      Rev 2.4   11 Feb 2002 15:03:24   dsutton
   added event logs when the connection is established or failed, unknown points, invalid states, etc

      Rev 2.3   14 Dec 2001 17:17:56   dsutton
   the functions that load the lists of points noware updating point managers instead of creating separate lists of their own.  Hopefully this is easier to follow

      Rev 2.2   15 Nov 2001 16:16:38   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin

      Rev 2.1   26 Oct 2001 15:20:20   dsutton
   moving revision 1 to 2.x

      Rev 1.5.1.0   26 Oct 2001 14:21:50   dsutton
   client connection bug, point sendable,

      Rev 1.5   23 Aug 2001 13:59:32   dsutton
   updated to intercept control points from the yukon side. also a few changes
   so it could be the base class of the rccs interface

      Rev 1.4   20 Jul 2001 10:00:50   dsutton
   client connection problem

      Rev 1.3   04 Jun 2001 14:22:24   dsutton
   updated logging and removed debug messages

      Rev 1.2   04 Jun 2001 09:31:56   dsutton
   lists had moved to the interface layer

      Rev 1.1   30 May 2001 16:47:28   dsutton
   moved the listener to the main class

      Rev 1.0   10 May 2001 11:15:30   dsutton
   Initial revision.

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

#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrserverconnection.h"
#include "fdrclientconnection.h"
#include "fdrsocketlayer.h"

// this class header
#include "fdrinet.h"

using std::string;
using std::endl;

/** global used to start the interface by c functions **/
CtiFDR_Inet * inetInterface;

const CHAR * CtiFDR_Inet::KEY_LISTEN_PORT_NUMBER = "FDR_INET_PORT_NUMBER";
const CHAR * CtiFDR_Inet::KEY_IP_MASK = "FDR_INET_IP_MASK";
const CHAR * CtiFDR_Inet::KEY_TIMESTAMP_WINDOW = "FDR_INET_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_Inet::KEY_DB_RELOAD_RATE = "FDR_INET_DB_RELOAD_RATE";
const CHAR * CtiFDR_Inet::KEY_SOURCE_NAME = "FDR_INET_SOURCE_NAME";
const CHAR * CtiFDR_Inet::KEY_SERVER_LIST = "FDR_INET_SERVER_LIST";
const CHAR * CtiFDR_Inet::KEY_DEBUG_MODE = "FDR_INET_DEBUG_MODE";
const CHAR * CtiFDR_Inet::KEY_QUEUE_FLUSH_RATE = "FDR_INET_QUEUE_FLUSH_RATE";

// Constructors, Destructor, and Operators
CtiFDR_Inet::CtiFDR_Inet(string aName) :
    CtiFDRSocketInterface(aName),
    iClientConnectionSemaphore(NULL)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    CtiFDRManager   *sendList = new CtiFDRManager(getInterfaceName(), string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (sendList);
    sendList = NULL;

}


CtiFDR_Inet::~CtiFDR_Inet()
{
    {
        CtiLockGuard<CtiMutex> guard(iConnectionListMux);

        // delete all the layers
        for (int x=0; x < iConnectionList.size(); x++)
        {
            delete iConnectionList[x];
        }
        // erase the pointers
        iConnectionList.erase (iConnectionList.begin(),iConnectionList.end());
    }
    // kill the connection list and then send the statuses
    setCurrentClientLinkStates();
    {
        CtiLockGuard<CtiMutex> guard(iClientListMux);
        iClientList.erase (iClientList.begin(),iClientList.end());
    }
}

CtiFDR_Inet& CtiFDR_Inet::setSourceName(string &aName)
{
    iSourceName = aName;
    return *this;
}
string & CtiFDR_Inet::getSourceName()
{
        return iSourceName;
}

string  CtiFDR_Inet::getSourceName() const
{
        return iSourceName;
}

vector< CtiFDRSocketLayer *> &CtiFDR_Inet::getConnectionList ()
{
    return iConnectionList;
}
vector< CtiFDRSocketLayer *> CtiFDR_Inet::getConnectionList () const
{
    return iConnectionList;
}

CtiMutex & CtiFDR_Inet::getConnectionMux ()
{
    return iConnectionListMux;
}

vector< string >  &CtiFDR_Inet::getClientList ()
{
    return iClientList;
}
vector< string >  CtiFDR_Inet::getClientList () const
{
    return iClientList;
}

CtiMutex & CtiFDR_Inet::getClientListMux ()
{
    return iClientListMux;
}

/*************************************************
* Function Name: CtiFDR_INET::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_Inet::init( void )
{
    // init the base class
    Inherited::init();

    if ( !readConfig( ) )
    {
        return FALSE;
    }

    // start up the socket layer
    loadClientList ();
    loadTranslationLists();

    iThreadMonitor = rwMakeThreadFunction(*this,
                                            &CtiFDR_Inet::threadFunctionMonitor);

    iThreadServer = rwMakeThreadFunction(*this,
                                            &CtiFDR_Inet::threadFunctionServerConnection);

    iThreadClient = rwMakeThreadFunction(*this,
                                            &CtiFDR_Inet::threadFunctionClientConnection);

    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData = rwMakeThreadFunction(*this,
                                                &CtiFDR_Inet::threadFunctionSendDebugData);
    }

    return TRUE;
}

/*************************************************
* Function Name: CtiFDR_Inet::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_Inet::run( void )
{
    // this is here because I want the readconfig up the tree called in RCCS
    init();

    // crank up the base class
    Inherited::run();

    // startup our interfaces
    iThreadMonitor.start();
    iThreadServer.start();
    iThreadClient.start();
    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData.start();
    }

    setCurrentClientLinkStates();
    return TRUE;
}


void CtiFDR_Inet::setCurrentClientLinkStates()
{
    // try and load the status ids
    int destEntries = iClientList.size();
    int connEntries = iConnectionList.size();
    long linkID;
    bool foundClient;

    for (int x=0; x < destEntries; x++)
    {
        foundClient = false;
        linkID = getClientLinkStatusID (iClientList[x]);

        // look in our list for our name
        for (int y = 0; y < connEntries; y++)
        {
            // if the names match
            if(ciStringEqual(iConnectionList[y]->getName(),iClientList[x]))
            {
                iConnectionList[y]->setLinkStatusID(linkID);
                iConnectionList[y]->sendLinkState (FDR_CONNECTED);
                foundClient = true;
            }
        }

        // if we didn't find the client, send not connected
        if ((!foundClient) && (linkID))
        {
            CtiPointDataMsg     *pData;
            pData = new CtiPointDataMsg(linkID,
                                        FDR_NOT_CONNECTED,
                                        NormalQuality,
                                        StatusPointType);
            sendMessageToDispatch (pData);
        }

    }
}
/*************************************************
* Function Name: CtiFDR_Inet::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_Inet::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //
    shutdownListener();
    iThreadClient.requestCancellation();
    iThreadMonitor.requestCancellation();
    iThreadServer.requestCancellation();

    if (isInterfaceInDebugMode())
    {
        iThreadSendDebugData.requestCancellation();
    }

    // stop the base class
    Inherited::stop();

    return TRUE;
}

/************************************************************************
* Function Name: CtiFDR_Inet::loadTranslationList()
*
* Description: Creates a seperate collection of Status and Analog Point
*              IDs and Inet id for translation.
*
*************************************************************************
*/
bool CtiFDR_Inet::loadTranslationLists()
{
    bool retCode = true;

    retCode = Inherited::loadTranslationLists();

    if (retCode)
    {
        retCode = loadClientList ();
    }
    return retCode;
}

/************************************************************************
* Function Name: CtiFDR_Inet::loadList()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDR_Inet::loadList(string &aDirection,  CtiFDRPointList &aList)
{
    bool successful = false;
    bool foundPoint = false;

    int entries;

    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(getInterfaceName(), aDirection);

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
            if (((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) || (pointList->entries() > 0))
            {
                // get iterator on send list
                CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();

                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    successful = translateSinglePoint(myIterator->second);
                }

                // lock the list I'm swapping
                CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
                if (aList.getPointList() != NULL)
                {
                    aList.deletePointList();
                }
                aList.setPointList (pointList);

                // set this to null, the memory is now assigned to the other point
                pointList=NULL;

                if (!successful)
                {
                    if (!foundPoint)
                    {
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No " << aDirection << " points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error loading (" << aDirection << ") points for " << getInterfaceName() << " : Empty data set returned " << endl;
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
        dout << "loadTranslationList():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "loadTranslationList():   (...)" << endl;
    }

    return successful;
}


bool CtiFDR_Inet::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;
    string tempString1;
    string tempString2;
    string translationName;

    for (int x=0; x < translationPoint->getDestinationList().size(); x++)
    {
        const string translation = translationPoint->getDestinationList()[x].getTranslation();
        boost::char_separator<char> sep1(";");
        Boost_char_tokenizer nextTranslate(translation, sep1);
        Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin();

        if ( tok_iter != nextTranslate.end() )
        {
            tempString1 = *tok_iter;tok_iter++;
            boost::char_separator<char> sep2(":");
            Boost_char_tokenizer nextTempToken(tempString1, sep2);
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

                // now we have a device name
                if ( !tempString2.empty() )
                {
                    // blank pad device
                    tempString2.resize(20,' ');
                    translationName = tempString2;

                    // next token is the point name
                    if ( tok_iter != nextTranslate.end())
                    {
                        tempString1 = *tok_iter;
                        boost::char_separator<char> sep2(":");
                        Boost_char_tokenizer nextTempToken(tempString1, sep2);
                        Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

                        tok_iter1++;
                        if( tok_iter1 != nextTempToken.end() )
                        {
                            tempString2 = *tok_iter1;

                            // now we have a point name:
                            if ( !tempString2.empty() )
                            {
                                tempString2.resize(20,' ');
                                translationName += tempString2;
                                std::transform(translationName.begin(), translationName.end(), translationName.begin(), ::toupper);

                                translationPoint->getDestinationList()[x].setTranslation(translationName);
                                /***/
                                string s = translationPoint->getDestinationList()[x].getDestination();
                                if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << " Destination: " << translationPoint->getDestinationList()[x].getDestination() << endl;
                                }
                                std::transform(s.begin(), s.end(), s.begin(), ::toupper);
                                translationPoint->getDestinationList()[x].setDestination(s);
                                if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << " Destination: " << translationPoint->getDestinationList()[x].getDestination() << endl;
                                }
                                /***/
                                successful = true;

                                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Point ID " << translationPoint->getPointID();
                                    dout << " translated: " << translationName << " for " << translationPoint->getDestinationList()[x].getDestination() << endl;
                                }

                            }   // point id invalid
                        }
                    }   // second token invalid

                }   // category invalid
            }
        }   // first token invalid

    }   // end of while entries

    return successful;
}

/************************************************************************
* Function Name: CtiFDR_Inet::loadDestinationList()
*
* Description: Creates a collection of destinations
*
*************************************************************************
*/
bool CtiFDR_Inet::loadClientList()
{
    bool                successful(FALSE);
    CtiFDRPointSPtr     translationPoint;
    string              receiveConnections;
    int                 entries;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_SEND));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            CtiLockGuard<CtiMutex> destGuard(iClientListMux);
            iClientList.erase (iClientList.begin(),iClientList.end());

            // get iterator on send list
            CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();

            if (pointList->getMap().size())
            {
                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    translationPoint = (*myIterator).second;

                    for (int x=0; x < translationPoint->getDestinationList().size(); x++)
                    {
                        bool foundDestination = false;

                        for (int y=0; y < iClientList.size(); y++)
                        {
                            if(ciStringEqual(iClientList[y],translationPoint->getDestinationList()[x].getDestination()))
                                foundDestination = true;
                        }

                        if (!foundDestination)
                        {
                            iClientList.push_back (translationPoint->getDestinationList()[x].getDestination());
                            successful = true;
                        }
                    }
                }
            }
            else
            {
                successful = true;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            successful = false;
        }
        // we're always newing this so delete it everytime through
        delete pointList;

        /******************************
        * note:  for the RCCS interface we are always sending data
        * to the machine we may receive data from
        * because of this, the server list is always empty
        *******************************
        */
        receiveConnections = getCparmValueAsString(KEY_SERVER_LIST);
        if(receiveConnections.length() != 0)
        {
            boost::char_separator<char> sep(",");
            Boost_char_tokenizer next(receiveConnections, sep);
            Boost_char_tokenizer::iterator tok_iter = next.begin();

            string       myInterfaceName;
            string       tempString;

            // parse the interfaces
            while ( tok_iter != next.end() )
            {
                myInterfaceName = *tok_iter;tok_iter++;
                bool foundDestination = false;

                for (int y=0; y < iClientList.size(); y++)
                {
                    if(ciStringEqual(iClientList[y],myInterfaceName))
                        foundDestination = true;
                }

                if (!foundDestination)
                {
                    iClientList.push_back (myInterfaceName);
                    successful = true;
                }
            } // end while (!(myInterfaceName=next
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "loadDestinationList():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "loadDestinationList():   (...)" << endl;
    }

    return successful;
}

/*************************************************
* Function Name: CtiFDR_Inet::config()
*
* Description: loads cparm config values
*
**************************************************
*/
int CtiFDR_Inet::readConfig( void )
{
    int         successful = TRUE;
    string   tempStr;


    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setPortNumber (INET_PORTNUMBER);
    }
    // since inet and rccs are the only interfaces that intiate the connection, we must make sure we set the
    // connect port number also
    setConnectPortNumber (getPortNumber());

    tempStr = getCparmValueAsString(KEY_IP_MASK);
    if (tempStr.length() > 0)
    {
        setIpMask(tempStr);
    }
    else
    {
        setIpMask("");
    }

    tempStr = getCparmValueAsString(KEY_TIMESTAMP_WINDOW);
    if (tempStr.length() > 0)
    {
        setTimestampReasonabilityWindow (atoi (tempStr.c_str()));
    }
    else
    {
        setTimestampReasonabilityWindow (120);
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

    tempStr = getCparmValueAsString(KEY_SOURCE_NAME);
    if (tempStr.length() > 0)
    {
        iSourceName = tempStr;
    }
    else
    {
        iSourceName = string("YUKON");
    }


    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    }
    else
    {
        // default to 5 seconds for inet
        setQueueFlushRate (1);
    }

    tempStr = getCparmValueAsString(KEY_SERVER_LIST);

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " INET port number " << getPortNumber() << endl;
        dout << CtiTime() << " INET timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << CtiTime() << " INET db reload rate " << getReloadRate() << endl;
        dout << CtiTime() << " INET source name " << iSourceName << endl;

        if(tempStr.length() != 0)
            dout << CtiTime() << " INET receive only connections will be initialized " << endl;

        if (isInterfaceInDebugMode())
            dout << CtiTime() << " INET running in debug mode " << endl;
        else
            dout << CtiTime() << " INET running in normal mode "<< endl;

    }

    return successful;
}


/**************************************************************************
* Function Name: CtiFDR_Inet::sendMessageToForeignSys ()
*
* Description: We must find the appropriate destination first and then do our write
*
***************************************************************************
*/

bool CtiFDR_Inet::buildAndWriteToForeignSystem (CtiFDRPoint &aPoint )
{
    bool retVal = true;
    int  connectionIndex;;
    CHAR *foreignSys=NULL;

    if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " DEBUG: Building message to send to INET. " << endl;
    }

    // now loop thru the many possible destinations for the point
    for (int x=0; x < aPoint.getDestinationList().size(); x++)
    {
        CtiLockGuard<CtiMutex> guard(iConnectionListMux);
        connectionIndex = findConnectionByNameInList (aPoint.getDestinationList()[x].getDestination());

        if (connectionIndex != -1)
        {
            /**************************
            * we allocate an inet message here and it will be deleted
            * inside of the write function on the connection
            ***************************
            */
            foreignSys = new CHAR[sizeof (InetInterface_t)];
            InetInterface_t *ptr = (InetInterface_t *)foreignSys;

            // make sure we have all the pieces
            if (foreignSys != NULL)
            {
                // put everything in the message
                ptr->Type = INETTYPEVALUE;
                iSourceName.resize(INETDESTSIZE,' ');
                strncpy (ptr->SourceName, iSourceName.c_str(), INETDESTSIZE);

                CtiTime timestamp(aPoint.getLastTimeStamp());
                if (timestamp < CtiTime(CtiDate(1,1,2001)))
                {
                    timestamp = CtiTime();
                }

                ptr->msgUnion.value.TimeStamp = (timestamp.seconds());
                strncpy (ptr->msgUnion.value.DeviceName, aPoint.getDestinationList()[x].getTranslation().c_str(),20);
                strncpy (ptr->msgUnion.value.PointName, &aPoint.getDestinationList()[x].getTranslation()[20],20);

                /***********************
                * for exchanging with DSM2 systems
                * the daylight savings flag is the most significant bit
                * in the quality, if we are in daylight savings, I need to set the quality
                * so the receiving side knows the time
                ************************
                */
                ptr->msgUnion.value.Quality = YukonToForeignQuality (aPoint.getQuality());
                if (timestamp.isDST())
                {
                    ptr->msgUnion.value.Quality |= DSTACTIVE;
                }
                 // need to intercept sending a status point to make it DSM2 like
                switch (aPoint.getPointType())
                {
                    case StatusPointType:
                        {
                            ptr->msgUnion.value.Value = aPoint.getValue()+1;
                            break;
                        }
                    default:
                        ptr->msgUnion.value.Value = aPoint.getValue();
                        break;
                }
                ptr->msgUnion.value.AlarmState = NORMAL;

                /**************************
                * if we get this far, the connection list must exist so no null check
                * required (memory is consumed no matter what if we get this far)
                ***************************
                */
                string clientName(ptr->SourceName);
                string deviceName(ptr->msgUnion.value.DeviceName);
                string pointName(ptr->msgUnion.value.PointName);

                // memory is consumed no matter what
                if (!iConnectionList[connectionIndex]->write (foreignSys))
                {
                    clientName.resize(INETDESTSIZE,' ');
                    deviceName.resize(20,' ');
                    pointName.resize(20,' ');


                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << string (trim(deviceName)) << " " << string(trim(pointName)) << " sent to " << iConnectionList[connectionIndex]->getName() << endl;
                    }

                    // successfully sent message
                    retVal = true;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Error: " << string (trim(deviceName)) << " " << string(trim(pointName)) << " sending to " << iConnectionList[connectionIndex]->getName() << endl;
                    }
                }
            }
        }
        else
        {
            if (getDebugLevel () & MAJOR_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " DEBUG: Could not find the destination connection. <"<< aPoint.getDestinationList()[x].getDestination() << ">\n";
            }
        }
    }
    return retVal;
}


int CtiFDR_Inet::findConnectionByNameInList(string aName)
{
    bool                    foundFlag= false;
    int                     index = 0, foundIndex=-1;
    int                     entries;

        // list must be protected outside of this call
    entries = iConnectionList.size();

    while ((index < entries) && !foundFlag)
    {
        // find the point id
        if(ciStringEqual(iConnectionList[index]->getName(),aName))
        {
            foundFlag = true;
            foundIndex = index;
        }
        // increment our counter
        index++;
    }
    return foundIndex;
}

int CtiFDR_Inet::findClientInList(SOCKADDR_IN aAddr)
{
    bool                    foundFlag= false;
    int                     index = 0, foundIndex=-1;
    int                     entries;

    // list must be protected outside of this call
    entries = iConnectionList.size();

    while ((index < entries) && !foundFlag)
    {
        // make sure this isn't still null and trying to initialize
        if (iConnectionList[index]->getOutBoundConnection() != NULL)
        {
            // find the point id
            if (aAddr.sin_addr.S_un.S_addr == iConnectionList[index]->getOutBoundConnection()->getAddr().sin_addr.S_un.S_addr)
            {
                foundFlag = true;
                foundIndex = index;
            }
        }
        index++;
    }
    return foundIndex;
}

/**************************************************************************
* Function Name: CtiFDR_Inet::threadFunctionConnection
*
* Description: thread that watches connection status and re-establishes it as needed
*
***************************************************************************
*/
void CtiFDR_Inet::threadFunctionMonitor( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDR_Inet::threadFunctionMonitor " << endl;
        }
        // don't try to do anything while the list is null
        while (iConnectionList.size() == 0)
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (500);
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (250);

            CtiLockGuard<CtiMutex> guard(iConnectionListMux);

            vector<CtiFDRSocketLayer*>::iterator connectionIt = iConnectionList.begin() ;

            // iterate list or until we find our entry
            /************************
            * we have a vector of pointers so we have to de-reference
            * the iterator before we can access the pointer
            *************************
            */
            while (connectionIt != iConnectionList.end())
            {
                // see if we failed
                if ((*connectionIt)->getInBoundConnectionStatus() == CtiFDRSocketConnection::Failed ||
                    (*connectionIt)->getOutBoundConnectionStatus() == CtiFDRSocketConnection::Failed)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << (*connectionIt)->getName() << "'s link has failed" << endl;
                    }

                    // log it before we kill it
                    string action, desc;
                    desc = (*connectionIt)->getName() + "'s link has failed";
                    logEvent (desc,action, true);

                    CtiFDRSocketLayer::FDRConnectionType connectionType = (*connectionIt)->getConnectionType();

                    delete (*connectionIt);
                    connectionIt = iConnectionList.erase(connectionIt);

                    // if its a server connection, the client will re-connect
                    if (connectionType == CtiFDRSocketLayer::Client_Multiple)
                    {
                        // this signals the client thread to spit out another
                        SetEvent (iClientConnectionSemaphore);
                    }

                    break;
                }
                else
                {
                    connectionIt++;
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        // destroy the connection list here
        {
            CtiLockGuard<CtiMutex> guard(iConnectionListMux);

            // delete all the layers
            for (int x=0; x < iConnectionList.size(); x++)
            {
                delete iConnectionList[x];
            }
            // erase the pointers
            iConnectionList.erase (iConnectionList.begin(),iConnectionList.end());
        }
        setCurrentClientLinkStates();


        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CANCELLATION of CtiFDR_Inet::threadFunctionMonitor " << endl;
        return;
    }

    // try and catch the thread death
    catch ( ... )
    {
        {
            CtiLockGuard<CtiMutex> guard(iConnectionListMux);

            // delete all the layers
            for (int x=0; x < iConnectionList.size(); x++)
            {
                delete iConnectionList[x];
            }
            // erase the pointers
            iConnectionList.erase (iConnectionList.begin(),iConnectionList.end());
        }

        setCurrentClientLinkStates();

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDR_Inet::threadFunctionMonitor is dead! " << endl;
    }
}


void CtiFDR_Inet::threadFunctionClientConnection( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    ULONG postCount;
    int destEntries=0, connEntries=0, index=0;
    bool foundFlag=false, allFound=false;
    CtiFDRSocketLayer   *layer;
    DWORD semRet;
    int retVal;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDR_Inet::threadFunctionClientConnection " << endl;
        }

        iClientConnectionSemaphore = CreateEvent ((LPSECURITY_ATTRIBUTES)NULL,TRUE,false,NULL);
        if (iClientConnectionSemaphore != NULL)
        {
            // load inital clients
            while (!allFound)
            {
                allFound = findAndInitializeClients();

                pSelf.sleep (500);
                pSelf.serviceCancellation( );
            }

            for ( ; ; )
            {
                pSelf.serviceCancellation( );
                pSelf.sleep (500);

                // returns an error 1 for a timeout, error or otherwise
                semRet = WaitForSingleObject(iClientConnectionSemaphore, 5000L);

                // returns an error 1 for a timeout, error or otherwise
                if(semRet == WAIT_TIMEOUT)
                {
                    pSelf.serviceCancellation( );

                    // reset here because of processing elsewhere
                    ResetEvent (iClientConnectionSemaphore);
                }
                else
                {
                    pSelf.serviceCancellation( );

                    // reset here because of processing elsewhere
                    ResetEvent (iClientConnectionSemaphore);

                    /***********************
                    * if we were tripped, we need to
                    * walk our destination list and re-initialize
                    * the client that has gone away
                    ************************
                    */
                    allFound = false;
                    while (!allFound)
                    {
                        allFound = findAndInitializeClients();

                        pSelf.sleep (1000);
                        pSelf.serviceCancellation( );
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Unable to create connection semaphore for " << getInterfaceName() << " loading interface failed" << endl;
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CANCELLATION of CtiFDR_Inet::threadFunctionClientConnection " << endl;
    }
    // try and catch the thread death
    catch ( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Fatal Error:  CtiFDR_Inet::threadFunctionClientConnection is dead! " << endl;
        }
    }
}

bool  CtiFDR_Inet::findAndInitializeClients( void )
{
    int destEntries=0, connEntries=0, index=0;
    CtiFDRClientConnection *clientConnection = NULL;
    DWORD semRet;
    BYTE *ptr;
    bool retVal=true,foundFlag=false;
    string            desc;
    string           action;

    CtiLockGuard<CtiMutex> destGuard(iClientListMux);
    CtiLockGuard<CtiMutex> guard(iConnectionListMux);

    destEntries = iClientList.size();
    connEntries = iConnectionList.size();

    for (int x=0; x < destEntries; x++)
    {
        foundFlag = false;

        // look in our list for our name
        for (int y = 0; y < connEntries; y++)
        {
            // if the names match
            if(ciStringEqual(iConnectionList[y]->getName(),iClientList[x]))
            {
                foundFlag = true;
            }
        }

        // if we found the connection do nothing, otherwise re-init
        if (foundFlag == false)
        {
            CtiFDRSocketLayer *layer = new CtiFDRSocketLayer (iClientList[x], CtiFDRSocketLayer::Client_Multiple, this);
            retVal = layer->init();

            if (!retVal )
            {
                layer->setLinkStatusID(getClientLinkStatusID(iClientList[x]));
                retVal = layer->run();

                if (retVal)
                {
                    retVal = false;
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Initialization failed for " << iClientList[x] << endl;
                    }
                    delete layer;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Client connection initialized for " << iClientList[x] << endl;
                        dout << string (inet_ntoa(layer->getOutBoundConnection()->getAddr().sin_addr)) << endl;
                    }

                    desc = iClientList[x] + string ("'s client link has been established at ") + string (inet_ntoa(layer->getOutBoundConnection()->getAddr().sin_addr));
                    logEvent (desc,action, true);
                    iConnectionList.push_back (layer);
                }
            }
            else
            {
                retVal = false;

                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Initialization failed for " << iClientList[x] << " client layer would not initialize" << endl;
                }
                delete layer;
            }
        }
    }
    return retVal;
}



void CtiFDR_Inet::threadFunctionServerConnection( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0;
    CtiFDRServerConnection   *connection;
    int connectionIndex;
    string            desc;
    string           action;

    SOCKET tmpListener,tmpConnection;
    SOCKADDR_IN             socketAddr, returnAddr;
    int                   returnLength;
    LPHOSTENT               hostEntry;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDR_Inet::threadFunctionServerConnection " << endl;
        }

        tmpListener = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        if (tmpListener == INVALID_SOCKET)
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Failed to create listener socket FDRInet " << endl;
            }
        }
        else
        {
            BOOL ka = TRUE;
            if (setsockopt(tmpListener, SOL_SOCKET, SO_REUSEADDR, (char*)&ka, sizeof(BOOL)))
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Failed to set reuse option for listener socket in  FDRInet " << endl;
                }

                shutdown(tmpListener, 2);
                closesocket(tmpListener);
            }
            else
            {
                // Fill in the address structure
                socketAddr.sin_family = AF_INET;
                socketAddr.sin_addr.s_addr = INADDR_ANY; // Let WinNexus supply address
                socketAddr.sin_port = htons(getPortNumber());      // Use port from command line

                if (bind(tmpListener, (LPSOCKADDR)&(socketAddr), sizeof(struct sockaddr)))
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Failed to bind listener socket in  FDRInet " << endl;
                    }

                    shutdown(tmpListener, 2);
                    closesocket(tmpListener);
                }
                else
                {
                    getListener()->setConnection(tmpListener);
                    for ( ; ; )
                    {
                        pSelf.serviceCancellation( );
                        pSelf.sleep (500);

                        if (listen(getListener()->getConnection(), SOMAXCONN))
                        {
                            shutdown(getListener()->getConnection(), 2);
                            closesocket(getListener()->getConnection());
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Listening for connection on port " << getPortNumber() << endl;
                            }

                            // new socket
                            returnLength = sizeof (returnAddr);
                            tmpConnection = accept(getListener()->getConnection(), (struct sockaddr *) &returnAddr, &returnLength);

                            if (tmpConnection == INVALID_SOCKET)
                            {
                                shutdown(tmpConnection, 2);
                                closesocket(tmpConnection);
                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Accept call failed in FDRInet " <<endl;
                                }
                            }
                            else
                            {
                                // set to non blocking mode
                                ULONG param=1;
                                ioctlsocket(tmpConnection, FIONBIO, &param);

                                connection = new CtiFDRServerConnection (tmpConnection, returnAddr);

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Server connection processed for client at " << string (inet_ntoa(connection->getAddr().sin_addr)) << endl;
                                }

                                {
                                    /**********************
                                    * check our list for a possible client
                                    ***********************
                                    */
                                    CtiLockGuard<CtiMutex> guard(iConnectionListMux);
                                    connectionIndex = findClientInList (connection->getAddr());

                                    // if it returns -1, the client wasn't found
                                    if (connectionIndex == -1)
                                    {
                                        CtiFDRSocketLayer *layer = new CtiFDRSocketLayer(string(), connection, CtiFDRSocketLayer::Server_Multiple, this);

                                        // this will start everything appropriately
                                        if (!layer->init())
                                        {
                                            retVal = layer->run();
                                            if (!retVal)
                                            {
                                                iConnectionList.push_back (layer);
                                            }
                                            else
                                            {
                                                // we failed, layer is consumed in here
                                                {
                                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                    dout << CtiTime() << " Return client connection to " << string (inet_ntoa(connection->getAddr().sin_addr)) << " failed" << endl;
                                                }
                                                desc = string (" Client connection to ") + string (inet_ntoa(connection->getAddr().sin_addr)) + string (" has failed");
                                                logEvent (desc,action, true);
                                                delete layer;
                                            }
                                        }
                                        else
                                        {
                                            // we failed, layer is consumed in here
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " Return client connection to " << string (inet_ntoa(connection->getAddr().sin_addr)) << " failed" << endl;
                                            }
                                            desc = string (" Client connection to ") + string (inet_ntoa(connection->getAddr().sin_addr)) + string (" has failed");
                                            logEvent (desc,action, true);
                                            delete layer;
                                        }
                                    }
                                    else
                                    {
                                        // found the client so put the server connection there
                                        if (iConnectionList[connectionIndex]->getInBoundConnection() == NULL)
                                        {
                                            // set the parent to the found connection
                                            connection->setParent (iConnectionList[connectionIndex]);
                                            connection->init();

                                            // attach it to the client and start it up
                                            iConnectionList[connectionIndex]->setInBoundConnection (connection);
                                            iConnectionList[connectionIndex]->getInBoundConnection()->run();
                                        }
                                        else
                                        {
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " Server connection for " << iConnectionList[connectionIndex]->getName() << " already established " << endl;
                                            }
                                            desc = string (" Server connection for ") + iConnectionList[connectionIndex]->getName() + string (" already established ");
                                            logEvent (desc,action, true);
                                            delete connection;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CANCELLATION of CtiFDR_Inet::threadFunctionServerConnection " << endl;
    }
    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDR_Inet::threadFunctionServerConnection is dead! " << endl;
    }
}

void CtiFDR_Inet::threadFunctionSendDebugData( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0;
    CtiFDRPointSPtr point;
    CtiPointDataMsg     *localMsg;
    int quality = NormalQuality, index, entries;
    FLOAT value = 1.0;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDR_Inet::threadFunctionSendDebugData" << endl;
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);


            index=0;
            {
                CtiFDRManager* mgrPtr = getSendToList().getPointList();
                CtiFDRManager::readerLock guard(mgrPtr->getLock());

                CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
                CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();

                for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator )
                {
                    // find the point id
                    point = (*myIterator).second;

                    localMsg = new CtiPointDataMsg (point->getPointID(), value, quality, point->getPointType());
                    sendMessageToForeignSys (localMsg);

                    if (value > 10000.0)
                    {
                        value = 1.0;
                    }
                    value++;
                    index++;
                    delete localMsg;
                }
            }



            if (quality == NormalQuality )
                quality = ManualQuality;
            else
                quality = NormalQuality;
        }

    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CANCELLATION of CtiFDR_Inet::threadFunctionSendDebugData " << endl;
    }
    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDR_Inet::threadFunctionDebugData is dead! " << endl;
    }
}

CHAR *CtiFDR_Inet::buildForeignSystemHeartbeatMsg ()
{
    /***********************
    *  data is allocated for the buffer inside this call
    *  it will be deleted inside  the socketlayer
    ************************
    */
    CHAR *foreignSys=NULL;

    /**************************
    * we allocate an inet message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    foreignSys = new CHAR[sizeof (InetInterface_t)];
    InetInterface_t *ptr = (InetInterface_t *)foreignSys;

    // make sure we have all the pieces
    if (foreignSys != NULL)
    {
        ptr->Type = INETTYPENULL;
        iSourceName.resize(10,' ');
        strncpy (ptr->SourceName, iSourceName.c_str(),10);
    }
    return foreignSys;
}

int CtiFDR_Inet::getMessageSize(CHAR *aBuffer)
{
    int retVal = 0;
    USHORT *type = (USHORT *)aBuffer;

    switch (*type)
    {
        case INETTYPEVALUE:
            retVal = sizeof (InetValue_t);
            break;
        case INETTYPESHUTDOWN:
            retVal = sizeof (InetShutdown_t);
            break;
        case INETTYPENULL:
            retVal = sizeof (InetHeartbeat_t);
            break;
        default:
            retVal = 0;
    }
    return retVal;
}

string CtiFDR_Inet::decodeClientName(CHAR * aBuffer)
{
    InetInterface_t *data = (InetInterface_t*)aBuffer;
    string tmpName(data->SourceName);

    tmpName.resize(INETDESTSIZE,' ');
    string clientName (trim(tmpName));

    return clientName;
}


int CtiFDR_Inet::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = NORMAL;
    InetInterface_t *data = (InetInterface_t*)aBuffer;
    string clientName(data->SourceName);
    string deviceName(data->msgUnion.value.DeviceName);
    string pointName(data->msgUnion.value.PointName);

    clientName.resize(INETDESTSIZE,' ');
    deviceName.resize(20,' ');
    pointName.resize(20,' ');

    switch (data->Type)
    {
        case INETTYPESHUTDOWN:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Shutdown message received from " << clientName << endl;
                }
                break;
            }
        case INETTYPENULL:
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Heartbeat message received from " << clientName << endl;
                }

                break;
            }
        case INETTYPEVALUE:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << string (trim(deviceName)) << " " << string(trim(pointName)) << " received from " << string (trim(clientName)) << endl;
                }
                retVal = processValueMessage (data);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Default or invalid type from  " << clientName << " type " << data->Type << endl;
                }

                // process them all as value messages
                retVal = processValueMessage (data);
                break;
            }

    }
    return retVal;

}

int CtiFDR_Inet::processValueMessage(InetInterface_t *data)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    string           translationName (data->msgUnion.value.DeviceName);
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                flag = true;
    string            traceState;
    CHAR            action[60];
    string           desc;

    string tmp = string (data->msgUnion.value.PointName);
    translationName.resize(20,' ');
    tmp.resize(20,' ');

    // convert to our name
    translationName += tmp;

    // chops off the rest of the message
//    translationName.resize(40,' ');
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if (flag == true)
    {
        // need to process accordingly in here
        switch (point.getPointType())
        {
            case AnalogPointType:
            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            case CalculatedPointType:
                {
                    // assign last stuff
                    quality = ForeignToYukonQuality (data->msgUnion.value.Quality);
                    value = data->msgUnion.value.Value;
                    value *= point.getMultiplier();
                    value += point.getOffset();

                    timestamp = CtiTime (data->msgUnion.value.TimeStamp);
                    if (timestamp == PASTDATE)
                    {
                        desc = decodeClientName((CHAR*)data) + string (" analog point received with an invalid timestamp ");
                        _snprintf(action,60,"%s for pointID %d",
                                  translationName.c_str(),
                                  point.getPointID());
                        logEvent (desc,string (action));
                        retVal = !NORMAL;
                    }
                    else
                    {
                        pData = new CtiPointDataMsg(point.getPointID(),
                                                    value,
                                                    quality,
                                                    point.getPointType());

                        pData->setTime(timestamp);
                        // consumes a delete memory
                        queueMessageToDispatch(pData);

                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Analog point " << translationName;
                            dout << " value " << value << " from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
                        }

                    }
                    break;
                }
            case StatusPointType:
                {
                    // check for control functions
                    if (point.isControllable())
                    {
                        int controlState=-1;

                        // make sure the value is valid
                        if (data->msgUnion.value.Value == Inet_Open)
                        {
                            controlState = OPENED;
                        }
                        else if (data->msgUnion.value.Value == Inet_Closed)
                        {
                            controlState = CLOSED;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Invalid control state " << data->msgUnion.value.Value;
                                dout << " for " << translationName << " received from " << getInterfaceName() << endl;
                            }
                            CHAR state[20];
                            _snprintf (state,20,"%.0f",data->msgUnion.value.Value);
                            desc = decodeClientName((CHAR*)data) + string (" control point received with an invalid state ") + string (state);
                            _snprintf(action,60,"%s for pointID %d",
                                      translationName.c_str(),
                                      point.getPointID());
                            logEvent (desc,string (action));
                            retVal = !NORMAL;
                        }

                        if (controlState != -1)
                        {
                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Control point " << translationName;
                                if (controlState == OPENED)
                                {
                                    dout << " control: Open " ;
                                }
                                else
                                {
                                    dout << " control: Closed " ;
                                }

                                dout <<" from " << getInterfaceName() << " and processed for point " << point.getPointID() << endl;;
                            }

                            // build the command message and send the control
                            CtiCommandMsg *cmdMsg;
                            cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                            cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                            cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
                            cmdMsg->insert(point.getPointID());  // point for control
                            cmdMsg->insert(controlState);
                            sendMessageToDispatch(cmdMsg);
                        }
                    }
                    else
                    {
                        // assign last stuff
                        quality = ForeignToYukonQuality (data->msgUnion.value.Quality);
                        switch ((int)data->msgUnion.value.Value)
                        {
                            case Inet_Open:
                                value = OPENED;
                                traceState = string("Opened");
                                break;
                            case Inet_Closed:
                                value = CLOSED;
                                traceState = string("Closed");
                                break;
                            case Inet_Indeterminate:
                                value = INDETERMINATE;
                                traceState = string("Indeterminate");
                                break;
                            case Inet_State_Four:
                                value = STATEFOUR;
                                traceState = string("State Four");
                                break;
                            case Inet_State_Five:
                                value = STATEFIVE;
                                traceState = string("State Five");
                                break;
                            case Inet_State_Six:
                                value = STATESIX;
                                traceState = string("State Six");
                                break;
                            case Inet_Invalid:
                            default:
                                {
                                    value = INVALID;
                                }
                        }

                        if (value == INVALID)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Status point " << translationName;
                            dout << " received an invalid state " << (int)data->msgUnion.value.Value;
                            dout <<" from " << getInterfaceName() << " for point " << point.getPointID() << endl;;

                            CHAR state[20];
                            _snprintf (state,20,"%.0f",data->msgUnion.value.Value);
                            desc = decodeClientName((CHAR*)data) + string (" status point received with an invalid state ") + string (state);
                            _snprintf(action,60,"%s for pointID %d",
                                      translationName.c_str(),
                                      point.getPointID());
                            logEvent (desc,string (action));
                            retVal = !NORMAL;

                        }
                        else
                        {
                            timestamp = CtiTime (data->msgUnion.value.TimeStamp);
                            if (timestamp == PASTDATE)
                            {
                                desc = decodeClientName((CHAR*)data) + string (" status point received with an invalid timestamp ");
                                _snprintf(action,60,"%s for pointID %d",
                                          translationName.c_str(),
                                          point.getPointID());
                                logEvent (desc,string (action));
                                retVal = !NORMAL;
                            }
                            else
                            {
                                pData = new CtiPointDataMsg(point.getPointID(),
                                                            value,
                                                            quality,
                                                            StatusPointType);

                                pData->setTime(timestamp);
                                // consumes a delete memory
                                queueMessageToDispatch(pData);
                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Status point " << translationName;
                                    dout << " new state: " << traceState;
                                    dout <<" from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
                                }
                            }
                        }
                    }
                    break;
                }
        }

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Translation for point " << translationName;
            dout << " from " << getInterfaceName() << " was not found" << endl;
        }
        desc = decodeClientName((CHAR*)data) + string ("'s point is not listed in the translation table");
        _snprintf(action,60,"%s", translationName.c_str());
        logEvent (desc,string (action));
        retVal = !NORMAL;
    }

    return retVal;
}


USHORT CtiFDR_Inet::ForeignToYukonQuality (USHORT aQuality)
{
    USHORT Quality = NormalQuality;

    // Test for the various dsm2 qualities
    if (aQuality & INETDATAINVALID)
        Quality = InvalidQuality;

    if (aQuality & INETUNREASONABLE)
        Quality = AbnormalQuality;

    if (aQuality & INETPLUGGED)
        Quality = NonUpdatedQuality;

    if (aQuality & INETMANUAL)
        Quality = ManualQuality;

    if (aQuality & INETOUTOFSCAN)
        Quality = UnknownQuality;

    return(Quality);
}

USHORT CtiFDR_Inet::YukonToForeignQuality (USHORT aQuality)
{
        USHORT Quality = INETDATAINVALID;

        // Test for the various CTI Qualities and translate to Inet
        if (aQuality == NonUpdatedQuality)
                Quality = INETPLUGGED;

        if (aQuality == ManualQuality)
                Quality = INETMANUAL;

        if (aQuality == InvalidQuality)
                Quality = INETDATAINVALID;

        if (aQuality == AbnormalQuality)
                Quality = INETUNREASONABLE;

        if (aQuality == NormalQuality)
                Quality = NORMAL;

        return(htons (Quality));
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
        inetInterface = new CtiFDR_Inet();

        // now start it up
        return inetInterface->run();
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

        inetInterface->stop();
        delete inetInterface;
        inetInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif




