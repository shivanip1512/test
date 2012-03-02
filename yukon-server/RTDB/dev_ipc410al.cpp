
#include "precompiled.h"
#include "dev_ipc410al.h"

using namespace Cti::Protocols::Ansi;

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

Ipc410ALDevice::Ipc410ALDevice()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

Ipc410ALDevice::~Ipc410ALDevice()
{
}

int Ipc410ALDevice::getTables(ANSI_TABLE_WANTS* table )
{

    ANSI_TABLE_WANTS    scanValues[] = {
        {  0,       0,      60,     ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,       0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 2052,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        { -1,       0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
    };


    int sizeOfTable = 0;
   
    for each (ANSI_TABLE_WANTS tableInfo in scanValues)
    {
        table[sizeOfTable++] = tableInfo; 
    }
    return sizeOfTable;
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int Ipc410ALDevice::buildScannerTableRequest (BYTE *aMsg, UINT flags)
{
    WANTS_HEADER  header;
    ANSI_TABLE_WANTS table[100];
    header.numTablesRequested = getTables(table);
    header.lastLoadProfileTime = 0;
    header.command = 5; // 
    BYTE scanOperation = 0; //0 = general scan

    // currently defaulted at billing data only
    if (useScanFlags())
    {
        header.lastLoadProfileTime = getLastLPTime().seconds();
        if (  getLastLPTime().seconds() > CtiTime().seconds() + (3600 * 12)  || // 12 hours ahead
              getLastLPTime().seconds() < CtiTime().seconds() - (86400 * 90)  ) // 3 months old
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ** INVALID LAST LP TIME ** Adjusting" << getName() <<"'s lastLPTime from: "<<getLastLPTime()<< endl;
            }
            setLastLPTime( CtiTime(CtiTime().seconds() - (86400 * 30)) );
        }
        if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() <<" lastLPTime "<<getLastLPTime()<< endl;
        }
    }

    return buildTableRequest (aMsg, table, header, scanOperation, flags);
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int Ipc410ALDevice::buildCommanderTableRequest (BYTE *aMsg, UINT flags)
{
    WANTS_HEADER   header;
    ANSI_TABLE_WANTS table[100];
    header.numTablesRequested = getTables(table);
    header.lastLoadProfileTime = 0;
    header.command = 5; // 
    BYTE scanOperation = 1; //1 = general pil scan

    return buildTableRequest (aMsg, table, header, scanOperation, flags);

}



