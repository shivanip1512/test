#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrareva.h
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
#ifndef __FDRAREVA_H__
#define __FDRAREVA_H__

#include <windows.h>  
#include <list>
#include <fstream>
#include <iostream>  
#include "dlldefs.h"
#include "fdrinterface.h"
#include "string.h"
#include <sstream>
#include <string>
#include <stdexcept>

using std::string;
using std::istream;


class __declspec(dllexport) FDRAreva : public CtiFDRInterface
{
        typedef CtiFDRInterface Inherited;
    public:
        // constructors and destructors
        FDRAreva(); 
        virtual ~FDRAreva();

        //Initializers
        BOOL init();
        bool readConfig();

		//class to store data collected from areva db
		class quadContainer {
			public:
				quadContainer();
				virtual ~quadContainer();
				void setQuadContainer(string DEVID, double KVAR, double KW, string HIST_TIMESTAMP);
				string getDevid();
				double getKvar();
				double getKw();
				string getHist_Timestamp();

			private:
				string devid;
				double kvar;
				double kw;
				string hist_timestamp;
		};

		//functions to get data from DB, convert it, and send to our DB
		list<quadContainer> getDataFromAreva();
		bool sendDataToYukon(list<quadContainer> arevaData);

		//additional useful function
		void trimSpaces(string& str);

		// gets called from fdr anytime a point data msg comes in. 
		bool sendMessageToForeignSys( CtiMessage *aMessage );
        int processMessageFromForeignSystem( char* );

		//this function calls the other functions in FDRAreva
		int  masterFunction();
		
		virtual bool loadTranslationLists();

		//functions to clean out areva databases
		void purgeRealTimeTable (); 
		void purgeHistoryTable (); 

		//functions to control thread
		virtual BOOL run(bool flag);
		virtual BOOL stop(bool flag);

        static const CHAR * KEY_DB_RELOAD;
		static const CHAR * KEY_DB_SLEEP;

    protected:
		//thread function that runs the subfunctions querying DB
		RWThreadFunction threadMasterFunction;

    private:

};

#endif

