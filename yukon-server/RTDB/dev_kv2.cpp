
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_kv2
*
* Date:   6/13/2002
*
* Author: Eric Schmit
*
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_kv2.cpp-arc  $
*    REVISION     :  $Revision: 1.9 $
*    DATE         :  $Date: 2005/01/25 18:33:51 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic Interface used to download a file using ftp
*
*    DESCRIPTION: 
*
*    History: 
      $Log: dev_kv2.cpp,v $
      Revision 1.9  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.8  2005/01/03 23:07:15  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.7  2004/12/10 21:58:43  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.6  2004/09/30 21:37:21  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.5  2003/04/25 15:14:07  dsutton
      Changed general scan and decode result


* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "porter.h"
#include "logger.h"
#include "dev_kv2.h"
#include "utility.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceKV2::CtiDeviceKV2()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceKV2::~CtiDeviceKV2()
{
}

//=========================================================================================================================================
//scanner has decided that it's time to talk to an ansi-talking device and has called up on Us to carry out this mission - let us be brave
//
//we get handed a bunch of junk we don't care about, build a header about the command (GeneralScan) and , then pop down
//to the ansi protocol object to get info about the tables we know we need for a GeneralScan
//=========================================================================================================================================

INT CtiDeviceKV2::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList,
                               RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{

   ULONG BytesWritten;
   int   adjustment = 0;

   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );

      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;
      EstablishOutMessagePriority( OutMessage, ScanPriority );

      //let's populate this list with the tables we want for a general scan...
      BYTE *ptr = OutMessage->Buffer.OutMessage;

      buildScannerTableRequest (ptr);
      outList.insert( OutMessage );
   }
   else
   {
      return MEMORY;
   }
   return NoError;
}

//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceKV2::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist < CtiMessage >&retList,
                                RWTPtrSlist< OUTMESS >    &outList)
{
    getProtocol().receiveCommResult( InMessage );
    unsigned long *lastLpTime;
    lastLpTime =  (unsigned long *)InMessage->Buffer.InMessage;
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "ResultDecode    RWTime(lastLpTime) "<<RWTime(*lastLpTime)<< endl;
    }

    setUseScanFlags(TRUE);
    setLastLPTime(RWTime(*lastLpTime));
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "ResultDecode    getLastLPTime "<<getLastLPTime()<< endl;
    }
    resetScanPending();

    setUseScanFlags(FALSE);
//   getProtocol().recvInbound( InMessage );

//   CtiLockGuard< CtiLogger > doubt_guard( dout );
//   dout << RWTime::now() << " ==============================================" << endl;
//   dout << RWTime::now() << " ==========The KV2 responded with data=========" << endl;
//   dout << RWTime::now() << " ==============================================" << endl;

   return( 0 ); //just a val
}

//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceKV2::sendCommResult( INMESS *InMessage)
{
    setUseScanFlags(TRUE);
    unsigned long lastLpTime = getLastLPTime().seconds();

    memcpy( InMessage->Buffer.InMessage, (void *)&lastLpTime, sizeof (unsigned long) );
    InMessage->InLength = sizeof (unsigned long);
    InMessage->EventCode = NORMAL;


    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "sendCommResult    lastLPTime "<<getLastLPTime()<< endl;
        dout << RWTime() << "sendCommResult    RWTime(lastLpTime) "<<RWTime(lastLpTime)<< endl;
    }
    setUseScanFlags(FALSE);
    
//   getProtocol().recvInbound( InMessage );

   CtiLockGuard< CtiLogger > doubt_guard( dout );
   dout << RWTime::now() << " ==============================================" << endl;
   dout << RWTime::now() << " ==========The KV2 responded with data=========" << endl;
   dout << RWTime::now() << " ==============================================" << endl;

   return( 0 ); //just a val
}


//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceKV2::ErrorDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist < CtiMessage >&vgList, RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList)
{

   return( 1 ); //just a val
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceKV2::buildScannerTableRequest (BYTE *aMsg)
{
    WANTS_HEADER   header;

    //ANSI_SCAN_OPERATION scanOperation = generalScan;
    //here is the password for the kv2 (should be changed to a cparm, I think)
    //BYTE        password[] = { 0xab, 0xc1, 0xab, 0xc2, 0xab, 0xc3, 0xab, 0xc4, 0xab, 0xc5, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
    //BYTE        password[] = { 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a };
    BYTE        password[] = { 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };


    // here are the tables requested for the kv2
    ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      30,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  3,     0,      0,      ANSI_TABLE_TYPE_STANDARD,     ANSI_OPERATION_READ},
       //        {  7,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_WRITE},
//        {  8,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 14,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // { 51,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        //{ 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 61,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 62,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 63,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 64,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  0,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        { 70,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {110,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
        
//        {110,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
    };     

    // currently defaulted at billing data only
    header.lastLoadProfileTime = getLastLPTime().seconds();
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "lastLPTime "<<getLastLPTime()<< endl;
    }

    RWCString pswdTemp;
    pswdTemp = getIED().getPassword();
    pswdTemp.toUpper();
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "pswdTemp "<<pswdTemp<< endl;
    }
    BYTE *temp;
    temp = (BYTE *)pswdTemp.data();
    struct CHexMap
    {
        char c;
        int val;
    };

    CHexMap HexMap[16] = {
        {'0', 0}, {'1', 1},
        {'2', 2}, {'3', 3},
        {'4', 4}, {'5', 5},
        {'6', 6}, {'7', 7},
        {'8', 8}, {'9', 9},
        {'A', 10}, {'B', 11},
        {'C', 12}, {'D', 13},
        {'E', 14}, {'F', 15}
    };


    int pwdTemp;
    int nibble;
    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;
    for (int a = 0; a < 10; a++)
    {       
        nibble = 0;
        for (int nib = 0; nib < 2; nib++)
        {   
            pwdTemp = 0;
            for (int i = 0; i < 16; i++)
            {
                if (*temp == HexMap[i].c)
                {
                    pwdTemp = HexMap[i].val;
                }
            }
            if (nib == 0)
            {
                pwdTemp = pwdTemp << 4;
            }

            password[a] |= pwdTemp;
            temp++;
        }
    }
    // lazyness so I don't have to continually remember to update this
    header.numTablesRequested = 0;
    for (int x=0; x < 100; x++)
    {
        if (table[x].tableID < 0)
        {
            break;
        }
        else
        {
            header.numTablesRequested++;
        }
    }
    header.command = 5; // ?

    BYTE scanOperation = 0;  //0 = general Scan

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof(ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));

    // keep the list on the scanner side for decode
    getProtocol().buildWantedTableList (aMsg);
    return NORMAL;
}

//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI & CtiDeviceKV2::getProtocol( void )
{
   return _ansiProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceKV2::processDispatchReturnMessage( CtiReturnMsg *msgPtr )
{

    CtiMultiMsg                      *msgMulti = CTIDBG_new CtiMultiMsg; 
    CtiPointDataMsg                  *pData = NULL;
    CtiPointBase                     *pPoint = NULL;
    double value = 0;
    int    qual = 0;
    bool                             foundSomething = false;
    bool gotValue = false;
    bool gotLPValues = false;
    int x, y, z;

    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Process Dispatch Message In Progress For " << getName() << "----" << endl;
    }
    x =  OFFSET_TOTAL_KWH;
    while (x <= OFFSET_HIGHEST_CURRENT_OFFSET) 
    {
        pPoint = getDevicePointOffsetTypeEqual( x, AnalogPointType );
        if (pPoint != NULL) 
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "----- (_8(|) WHOOHOO!!! ----" << endl;
                dout << RWTime() << " Point Offset ==> " <<x<< endl;
            }
            foundSomething = true;
            pData = CTIDBG_new CtiPointDataMsg();
            value = 0;
            qual = 0;
            pData->setId( pPoint->getID() );

            switch (x) 
            {
                case OFFSET_TOTAL_KWH:
                case OFFSET_RATE_A_KWH:
                case OFFSET_RATE_B_KWH:
                case OFFSET_RATE_C_KWH:
                case OFFSET_RATE_D_KWH:
                case OFFSET_RATE_E_KWH:

                case OFFSET_TOTAL_KVARH:
                case OFFSET_RATE_A_KVARH:
                case OFFSET_RATE_B_KVARH:
                case OFFSET_RATE_C_KVARH:
                case OFFSET_RATE_D_KVARH:
                case OFFSET_RATE_E_KVARH:

                case OFFSET_TOTAL_KVAH:
                case OFFSET_RATE_A_KVAH:
                case OFFSET_RATE_B_KVAH:
                case OFFSET_RATE_C_KVAH:
                case OFFSET_RATE_D_KVAH:
                case OFFSET_RATE_E_KVAH:
                {
                    gotValue = getProtocol().retreiveSummation( x, &value );
                    break;
                }
                case OFFSET_PEAK_KW_OR_RATE_A_KW:
                case OFFSET_RATE_B_KW:
                case OFFSET_RATE_C_KW:
                case OFFSET_RATE_D_KW:
                case OFFSET_RATE_E_KW:
                case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:

                case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
                case OFFSET_RATE_B_KVAR:
                case OFFSET_RATE_C_KVAR:
                case OFFSET_RATE_D_KVAR:
                case OFFSET_RATE_E_KVAR:
                case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:

                case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
                case OFFSET_RATE_B_KVA:
                case OFFSET_RATE_C_KVA:
                case OFFSET_RATE_D_KVA:
                case OFFSET_RATE_E_KVA:
                case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
                {
                    gotValue = getProtocol().retreiveDemand( x, &value );
                    break;
                }
                case OFFSET_LOADPROFILE_KW:  
                case OFFSET_LOADPROFILE_KVAR:
                case OFFSET_LOADPROFILE_QUADRANT1_KVAR:
                case OFFSET_LOADPROFILE_QUADRANT2_KVAR:
                case OFFSET_LOADPROFILE_QUADRANT3_KVAR:
                case OFFSET_LOADPROFILE_QUADRANT4_KVAR:
                case OFFSET_LOADPROFILE_KVA:
                case OFFSET_LOADPROFILE_QUADRANT1_KVA:
                case OFFSET_LOADPROFILE_QUADRANT2_KVA:
                case OFFSET_LOADPROFILE_QUADRANT3_KVA:
                case OFFSET_LOADPROFILE_QUADRANT4_KVA:
                {

                    gotLPValues = getProtocol().retreiveLPDemand( x, 1);  // 1=table64 - kv2 only uses that lp table.
                    break;  
                }
                case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
                case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
                case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
                case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:   
                case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE: 
                case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:   
                case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT: 
                case OFFSET_LOADPROFILE_PHASE_A_CURRENT:   
                case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT: 
                case OFFSET_LOADPROFILE_PHASE_B_CURRENT:  
                case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT: 
                case OFFSET_LOADPROFILE_PHASE_C_CURRENT:   
                case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT: 
                case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:   
                {
                    gotValue = getProtocol().retreivePresentValue(x, &value);
                    break;
                }
                default:
                {  
                    gotValue = false;
                    gotLPValues = false;
                }
                break;
            }                 
             
            if (gotValue) 
            {
                pData = CTIDBG_new CtiPointDataMsg();
                pData->setId( pPoint->getID() );

                pData->setValue( value );
                pData->setQuality( qual );
                pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                pData->setTime( RWTime() );
                pData->setType( pPoint->getType() );

                msgPtr->insert(pData); 
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " gotValue! "<< endl;
                }

                pData = NULL;
            }
            else if (gotLPValues) 
            {
                setUseScanFlags(TRUE);

                for (y = getProtocol().getTotalWantedLPBlockInts()-1; y >= 0; y--) 
                {
                    if (getProtocol().getLPTime(y) > getLastLPTime().seconds())
                    {
                        pData = new CtiPointDataMsg(pPoint->getID(), getProtocol().getLPValue(y), qual, pPoint->getType());
                        pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                        pData->setTime( RWTime(getProtocol().getLPTime(y)) );
                        msgPtr->insert(pData); 
                        pData = NULL;
                    }
                    else
                    {
                        y = -1;
                    }
                    

                }

                setUseScanFlags(FALSE);
                setUseScanFlags(TRUE);
                setLastLPTime(RWTime(getProtocol().getLPTime(getProtocol().getTotalWantedLPBlockInts()-1)));
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "getProtocol().getTotalWantedLPBlockInts() time  "<<RWTime(getProtocol().getLPTime(getProtocol().getTotalWantedLPBlockInts()-1))<< endl;
                    dout << RWTime() << "lastLPTime "<<RWTime(getProtocol().getlastLoadProfileTime())<< endl;
                }
                setUseScanFlags(FALSE);
            } 
            if (pData != NULL) 
            {
                delete []pData;
                pData = NULL;
            } 
        }
        pPoint = NULL;
        gotValue = false;
        gotLPValues = false;
        x++;
    }

    if( msgMulti != NULL )
    {
       delete msgMulti;
       msgMulti = NULL;
    }  
}



