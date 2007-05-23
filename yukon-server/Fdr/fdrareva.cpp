#include "yukon.h"

#include "dllbase.h"
#include <fstream>
#include <iostream>
#include "fdrareva.h"

using namespace std;
using std::string;

FDRAreva * arevaInterface;

FDRAreva::FDRAreva()
: CtiFDRInterface(string("AREVA"))
{}

FDRAreva::~FDRAreva(){    
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDRAreva has deconstructed. \n";
    }
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

    return loadTranslationLists();
}

bool FDRAreva::readConfig()
{
    return false;
}

bool FDRAreva::loadTranslationLists()
{   
    //Not Implemented
    return true;
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

string FDRAreva::getDataFromAreva(  )
{
    //see dbaccess.h
    
    // getConnection(1);
    // getDatabase(1);

    //dbConn->

    //sql commands from the C# files.



    return string("");
}

/*
select * from POINT;
select POINTID from POINT where POINTNAME = 'C5003 kw';

insert into FDRInterface values (26, 'AREVA', 'Receive', 'f' );
insert into FDRInterfaceOption values(26, 'Point', 1, 'Text', '(none)' );

*/
void FDRAreva::translateArevaToYukon( string data )
{
    //String should be in format PointName(Progress's DeviceName)|Double

    //tokenize or just find the position of the |

    return;
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
        return arevaInterface->run();
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

        arevaInterface->stop();
        delete arevaInterface;
        arevaInterface = 0;

        return 0;
    }
#ifdef __cplusplus
}
#endif
