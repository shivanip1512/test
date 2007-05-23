#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrareva.cpp
*
*    DATE: 5/15/2007
*
*    AUTHOR: Thain Spar
*
*    PURPOSE: Eliminate the extra step between Areva and FDR for Progress
*
*    DESCRIPTION: Take changes from the oracle DB directly to yukon instead of using the 3rd party program.
*    
*****************************************************************************/
#ifndef __FDRAREVA_H__
#define __FDRAREVA_H__

#include <windows.h>    
#include "dlldefs.h"
#include "fdrinterface.h"
#include "string.h"

using std::string;

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
        bool loadTranslationLists();

        void translateArevaToYukon( string pointName );
        string getDataFromAreva();

        /* gets called from fdr anytime a point data msg comes in. */
        bool sendMessageToForeignSys( CtiMessage *aMessage );
        int processMessageFromForeignSystem( char* );

    private:

};

#endif

