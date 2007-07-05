/*****************************************************************************
*
*    FILE NAME: fdrareva.cpp
*
*    DATE: 5/15/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: Eliminate the extra step between Areva and FDR for Progress
*
*    DESCRIPTION: Take changes from the oracle DB directly to yukon instead of using the 3rd party program.
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/



#include "yukon.h"
#include "dllbase.h"
#include <fstream>
#include <iostream>
#include "fdrareva.h"
#include "configparms.h"
#include "dbaccess.h"
#include "time.h"
#include <stdlib.h>
#include <list>
#include <rw/db/expr.h>
#include "rwutil.h"
#include "windows.h"

using namespace std;
using std::string;

const CHAR * FDRAreva::KEY_DB_RELOAD = "FDR_AREVA_DB_RELOAD_RATE";
const CHAR * FDRAreva::KEY_DB_SLEEP = "FDR_AREVA_DB_SLEEP_TIME";

BOOL _arevaGo = true;
FDRAreva * arevaInterface;
int sleepTime = 30000;

FDRAreva::FDRAreva()
: CtiFDRInterface(string("AREVA"))
{}

FDRAreva::~FDRAreva(){    
  //  if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
  //  {
  //      CtiLockGuard<CtiLogger> doubt_guard(dout);
  //      dout << CtiTime() << " FDRAreva has deconstructed. \n";
  //  }
}

BOOL FDRAreva::init()
{
    //get FDRManager, pass in the interface name/type call get
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRAreva has started. \n";
    }

    Inherited::init();

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRAreva has initialized. \n";
    }

    setReloadRate(300);
    readConfig();

	//create thread object
	threadMasterFunction = rwMakeThreadFunction(*this, &FDRAreva::masterFunction);

    return loadTranslationLists();
}

bool FDRAreva::loadTranslationLists()
{   
    CtiFDRPoint*     translationPoint;
    string           DEVID;
    RWDBStatus       listStatus;

    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(getInterfaceName(),string (FDR_INTERFACE_RECEIVE)); //check into what the second parameter means for the manager
        // keep the status
        listStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( listStatus.errorCode() == (RWDBStatus::ok))
        {
            if(pointList->entries() > 0) 
            {
                CtiFDRManager::CTIFdrPointIterator  myIterator = pointList->getMap().begin();
                for ( ; myIterator != pointList->getMap().end(); ++myIterator)
                {
                    translationPoint = (*myIterator).second;
                    for (int x=0; x < translationPoint->getDestinationList().size(); x++)
                    {
                        //go through destination list?
                        DEVID = translationPoint->getDestinationList()[x].getTranslationValue("Point");

                        if( !DEVID.empty())
                        {
                            translationPoint->getDestinationList()[x].setTranslation (DEVID);

                            //This only allows for one file name. In the event we want to use multiple files for different points
                            //the filename and path will need to be added into CtiFDRPoint or tracked per point by another method.
                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Device ID from Areva: " << DEVID <<std::endl;
                            }
                            //Also the last point loaded gets the final say in the path and filename
                        }
                    }
                }
                // lock the receive list and remove the old one
                {
                    CtiLockGuard<CtiMutex> sendGuard(getReceiveFromList().getMutex()); 
                    if (getReceiveFromList().getPointList() != NULL)
                    {
                        getReceiveFromList().deletePointList();
                    }
                    getReceiveFromList().setPointList (pointList);
                }
            }
        }else{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - FDR - Areva - in Loading Translation**** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

     }catch(...){
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " **** Checkpoint - FDR - Areva - in Loading Translation**** " << __FILE__ << " (" << __LINE__ << ")" << endl;
     }

     return true;
}

bool FDRAreva::readConfig()
{
	bool     successful = TRUE;
    string   tempStr;

	tempStr = getCparmValueAsString(KEY_DB_RELOAD);
	if (tempStr.length() > 0){
		setReloadRate (atoi(tempStr.c_str()));
	} 
	else{
		setReloadRate (86400);
	}

	tempStr = getCparmValueAsString(KEY_DB_SLEEP);
	if (tempStr.length() > 0){
		sleepTime = atoi(tempStr.c_str());
	} 

	return successful;
}

bool FDRAreva::sendMessageToForeignSys( CtiMessage *msg )
{
    //Not Implemented
    return true;
}

int FDRAreva::processMessageFromForeignSystem( char* )
{
    //Not Implemented
    return 0;
}

list<FDRAreva::quadContainer> FDRAreva::getDataFromAreva(  )
{
	//store the containers in a list
	list<quadContainer> arevaData;

	RWDBConnection conn = getConnection(1);
	RWDBDatabase myDbase   = conn.database();
	

	if (!myDbase.isValid()) {
		cout << myDbase.status().message() << endl;
	}

	RWDBSelector select1 = myDbase.selector();

	RWDBTable VMSDATA = myDbase.table("ETA_MASTER.VMSDATA");

	select1.orderByDescending(VMSDATA["HIST_TIMESTAMP"]);

	select1 << VMSDATA["HIST_TIMESTAMP"];

	RWDBReader rdr1 = select1.reader();

	RWDBDateTime HIST_TIMESTAMP;

	rdr1();
	rdr1 >> HIST_TIMESTAMP;

	RWDBTable VMSDATA_FEEDER_SUMMARY_KILO = myDbase.table("ETA_MASTER.VMSDATA_FEEDER_SUMMARY_KILO");

	RWDBSelector select2 = myDbase.selector();

	select2 << VMSDATA_FEEDER_SUMMARY_KILO["DEVID"] 
		<< VMSDATA_FEEDER_SUMMARY_KILO["KVAR"] 
		<< VMSDATA_FEEDER_SUMMARY_KILO["KW"];
	select2.where(VMSDATA_FEEDER_SUMMARY_KILO["HIST_TIMESTAMP_UTC"] == HIST_TIMESTAMP);

	string str_HIST_TIMESTAMP = HIST_TIMESTAMP.asString();

	RWDBReader rdr2 = select2.reader();

	while(rdr2()) {
		if (rdr2.isValid())
			{
			//strings to store data from DB
			string tempString;
			string DEVID = "";
			double KVAR = -1;
			double KW = -1;
			rdr2 >> DEVID >> KVAR >> KW;

			//store the data in an object
			quadContainer quad;
			quad.setQuadContainer(DEVID,KVAR,KW,str_HIST_TIMESTAMP);

			//store the object in a list
			arevaData.push_back(quad);
		}
	}
	return arevaData;
}

bool FDRAreva::sendDataToYukon(list<FDRAreva::quadContainer> arevaData)
{
	for(list<quadContainer>::iterator itr =  arevaData.begin(); itr!=arevaData.end(); itr++)
		{
		CtiFDRPoint  point;
		double arevaValue;  
		bool flag;

		//break up the pair of strings
		string DEVID = (*itr).getDevid();
		double KVAR  = (*itr).getKvar(); 
		double KW  = (*itr).getKw(); 

		//get rid of the trailing spaces
		trimSpaces(DEVID);

		flag = findTranslationNameInList ( DEVID, getReceiveFromList(), point);
		if ((flag == true) && ((point.getPointType() == AnalogPointType) ))
			{
			if(KVAR!=-1) {
				arevaValue = KVAR;
			}
			else
			{
				arevaValue = KW;
			}
			CtiPointDataMsg *pData = new CtiPointDataMsg(point.getPointID(),arevaValue,NormalQuality, AnalogPointType);
			pData->setMessageTime(CtiTime());

			// consumes a delete memory
			queueMessageToDispatch(pData);
		}
	} 

  return 0;
}

void FDRAreva::purgeRealTimeTable ()
{
	RWDBConnection conn = getConnection(1);
	RWDBDatabase myDbase   = conn.database();

	if (!myDbase.isValid()) {
		cout << myDbase.status().message() << endl;
	}
	RWDBTable VMSDATA = myDbase.table("ETA_MASTER.VMSDATA");
	RWDBDeleter deleter = VMSDATA.deleter();
	deleter.execute();
}
void FDRAreva::purgeHistoryTable ()
{
	RWDBConnection conn = getConnection(1);
	RWDBDatabase myDbase   = conn.database();

	if (!myDbase.isValid()) {
		cout << myDbase.status().message() << endl;
	}
	RWDBTable VMSDATA_HIST = myDbase.table("ETA_MASTER.VMSDATA_HIST");
	RWDBDeleter deleter = VMSDATA_HIST.deleter();
	deleter.execute();
}

void FDRAreva::trimSpaces( string& str)
{
	// Trim Both leading and trailing spaces
	size_t startpos = str.find_first_not_of(" \t"); // Find the first character position after excluding leading blank spaces
	size_t endpos = str.find_last_not_of(" \t"); // Find the first character position from reverse af

	if(( string::npos == startpos ) || ( string::npos == endpos))
		{
		str = "";
	}
	else
		str = str.substr( startpos, endpos-startpos+1 );
	/* //Trim Leading Spaces
	size_t startpos = str.find_first_not_of(” \t”); // Find the first character position after excluding leading blank spaces
	if( string::npos != startpos )
	str = str.substr( startpos );
	*/
	if( string::npos != endpos )
		str = str.substr( 0, endpos+1 );
}

FDRAreva::quadContainer::quadContainer(){
}

FDRAreva::quadContainer::~quadContainer(){    
}

void FDRAreva::quadContainer::setQuadContainer(string DEVID, double KVAR, double KW, string HIST_TIMESTAMP)
{
	devid = DEVID;
	kvar = KVAR;
	kw = KW;
	hist_timestamp.append(HIST_TIMESTAMP);
}

string FDRAreva::quadContainer::getDevid()
{
	return devid;
}

double FDRAreva::quadContainer::getKvar()
{
	return kvar;
}

double FDRAreva::quadContainer::getKw()
{
	return kw;
}

string FDRAreva::quadContainer::getHist_Timestamp()
{
	return hist_timestamp;
}

BOOL FDRAreva::run(bool flag)
{
	CtiFDRInterface::run();
	threadMasterFunction.start();
	return TRUE;
}

BOOL FDRAreva::stop(bool flag)
{
	_arevaGo = false;
	return TRUE;
}

	//this function is spawned as a thread
 int  FDRAreva::masterFunction()
 {
	 while(_arevaGo) {
		 list<quadContainer> arevaData;
		 arevaData = arevaInterface->getDataFromAreva();
		 arevaInterface->sendDataToYukon(arevaData);
		 purgeRealTimeTable();
		 purgeHistoryTable();
		 //sleep but wake up at intervals
		 for(int i = 0; (i < (sleepTime/500) && _arevaGo); i++) {
			 Sleep(500);
		 }
	 }
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
        arevaInterface = new FDRAreva();
        arevaInterface->init();

        // now start it up
        return arevaInterface->run(1);
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

        arevaInterface->stop(1);
        delete arevaInterface;
        arevaInterface = 0;

        return 0;
    }
#ifdef __cplusplus
}
#endif
