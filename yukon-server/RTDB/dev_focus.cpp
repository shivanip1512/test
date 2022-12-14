#include "precompiled.h"
#include "porter.h"
#include "logger.h"
#include "dev_focus.h"
#include "utility.h"
#include "pt_analog.h"
#include "pt_status.h"
#include "cmdparse.h"
#include "numstr.h"
#include "ctidate.h"
#include "ctitime.h"

using namespace Cti::Protocols::Ansi;

using std::string;
using std::endl;

namespace Cti {
namespace Devices {

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceFocus::CtiDeviceFocus()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiDeviceFocus::buildSingleTableRequest(BYTE *aMsg, UINT tableId)
{
    WANTS_HEADER   header = {0, 1, 0};

    //here is the password for the sentinel (should be changed to a cparm, I think)
    BYTE        password[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    ANSI_TABLE_WANTS    table[1] = {0,0,60, ANSI_TABLE_TYPE_STANDARD,ANSI_OPERATION_READ};

    BYTE scanOperation = 3; //3 = loopback
    UINT flags = 0;
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)) +sizeof(BYTE)),
            &flags, sizeof(UINT));
}


int CtiDeviceFocus::getCommanderTables(ANSI_TABLE_WANTS* table )
{

    ANSI_TABLE_WANTS    scanValues[] = {
        {  0,     0,      60,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 25,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 27,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 28,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
    };


    int sizeOfTable = 0;

    for each (ANSI_TABLE_WANTS tableInfo in scanValues)
    {
        table[sizeOfTable++] = tableInfo;
    }
    return sizeOfTable;
}


int CtiDeviceFocus::getScannerTables(ANSI_TABLE_WANTS* table )
{

    ANSI_TABLE_WANTS    scanValues[] = {
        {  0,     0,      60,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 27,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 28,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 31,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 32,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 33,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 61,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 62,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 63,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 64,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  -1,    0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
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
void CtiDeviceFocus::buildScannerTableRequest (BYTE *aMsg, UINT flags)
{
    WANTS_HEADER   header;
    ANSI_TABLE_WANTS table[100];
    header.numTablesRequested = getScannerTables(table);
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
            CTILOG_WARN(dout, "** INVALID LAST LP TIME ** Adjusting" << getName() <<"'s lastLPTime from: "<<getLastLPTime());

            setLastLPTime( CtiTime(CtiTime().seconds() - (86400 * 30)) );
        }

        if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
        {
            CTILOG_DEBUG(dout, getName() <<" lastLPTime "<<getLastLPTime());
        }
    }

    buildTableRequest (aMsg, table, header, scanOperation, flags);
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
void CtiDeviceFocus::buildCommanderTableRequest (BYTE *aMsg, UINT flags)
{
    WANTS_HEADER   header;
    ANSI_TABLE_WANTS table[100];
    header.numTablesRequested = getCommanderTables(table);
    header.lastLoadProfileTime = 0;
    header.command = 5; //
    BYTE scanOperation = 1; //1 = general pil scan

    buildTableRequest (aMsg, table, header, scanOperation, flags);
}

int CtiDeviceFocus::buildTableRequest (BYTE *aMsg, ANSI_TABLE_WANTS *table, WANTS_HEADER  header, BYTE scanOperation, UINT flags)
{
    //here is the password for the sentinel (should be changed to a cparm, I think)
    BYTE  password[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    string pswdTemp = getIED().getPassword();

    const BYTE *temp = (const BYTE *)pswdTemp.c_str();
    for (int i = 0; i < pswdTemp.length(); i++)
        password[i] = *(temp + i);


    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)) +sizeof(BYTE)),
            &flags, sizeof(UINT));


    return ClientErrors::None;
}


//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI& CtiDeviceFocus::getANSIProtocol( void )
{
   return  _stdAnsiProtocol;
}

//=========================================================================================================================================
//
//=========================================================================================================================================
unsigned long CtiDeviceFocus::updateLastLpTime()
{
    return getANSIProtocol().getLPTime(0);
}

}
}
