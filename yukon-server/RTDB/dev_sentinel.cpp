

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_sentinel
*
* Date:   8/19/2004
*
* Author: Julie Richter
*
** Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "porter.h"
#include "logger.h"
#include "dev_sentinel.h"
#include "utility.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceSentinel::CtiDeviceSentinel()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceSentinel::~CtiDeviceSentinel()
{
}

//=========================================================================================================================================
//scanner has decided that it's time to talk to an ansi-talking device and has called up on Us to carry out this mission - let us be brave
//
//we get handed a bunch of junk we don't care about, build a header about the command (GeneralScan) and , then pop down
//to the ansi protocol object to get info about the tables we know we need for a GeneralScan
//=========================================================================================================================================

INT CtiDeviceSentinel::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList,
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

INT CtiDeviceSentinel::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist < CtiMessage >&retList,
                                RWTPtrSlist< OUTMESS >    &outList)
{
    getProtocol().receiveCommResult( InMessage );
//   getProtocol().recvInbound( InMessage );

//   CtiLockGuard< CtiLogger > doubt_guard( dout );
//   dout << RWTime::now() << " ==============================================" << endl;
//   dout << RWTime::now() << " ==========The Sentinel responded with data=========" << endl;
//   dout << RWTime::now() << " ==============================================" << endl;

   return( 1 ); //just a val
}

//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceSentinel::ErrorDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist < CtiMessage >&vgList, RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList)
{

   return( 1 ); //just a val
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceSentinel::buildScannerTableRequest (BYTE *aMsg)
{
    WANTS_HEADER   header;
    // here are the tables requested for the sentinel
    ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
//        {  7,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_WRITE},
//        {  8,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
    //    { 51,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 61,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 62,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 63,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 64,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // {  0,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
       // { 70,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
        
    };


    // currently defaulted at billing data only
    header.lastLoadProfileTime = getLastLPTime().seconds();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "lastLPTime "<<getLastLPTime()<< endl;
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

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy ((aMsg+sizeof(header)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));

    // keep the list on the scanner side for decode
    getProtocol().buildWantedTableList (aMsg);
    return NORMAL;
}

//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI & CtiDeviceSentinel::getProtocol( void )
{
   return  _ansiProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceSentinel::processDispatchReturnMessage( CtiReturnMsg *msgPtr )
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
                dout << RWTime() << "----- (_8-(|) WHOOHOO!!! ----" << endl;
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
                    /*unsigned short nbrBlkInts = getProtocol().getTotalWantedLPBlockInts();
                    lpValues = new double[nbrBlkInts];
                    lpTimes = new ULONG[nbrBlkInts];
                    for (y = 0; y < nbrBlkInts; y++) 
                    {
                        lpValues[y] = getProtocol().getLPValue(y);
                        lpTimes[y] = getProtocol().getLPTime(y);
                    } */
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

                msgMulti->getData().insert( pData );

                pData = NULL;
            }
            else if (gotLPValues) 
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " getProtocol().getTotalWantedLPBlockInts()  "<< getProtocol().getTotalWantedLPBlockInts()<< endl;
                }
                for (y = 0; y < getProtocol().getTotalWantedLPBlockInts(); y++) 
                {

                    pData = new CtiPointDataMsg(pPoint->getID(), getProtocol().getLPValue(y), qual, pPoint->getType());
                    pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                    pData->setTime( RWTime(getProtocol().getLPTime(y)) );

                    msgMulti->getData().insert( pData );

                    //pData = NULL;

                } 
                setUseScanFlags(TRUE);
                setLastLPTime(RWTime(getProtocol().getLPTime(getProtocol().getTotalWantedLPBlockInts() - 1)));
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "getProtocol().getTotalWantedLPBlockInts()-1 time  "<<RWTime(getProtocol().getLPTime(getProtocol().getTotalWantedLPBlockInts() - 1))<< endl;
                    dout << RWTime() << "lastLPTime "<<getLastLPTime()<< endl;
                }
                setUseScanFlags(FALSE);
            }
            if (pData != NULL) 
            {
                delete []pData;
                pData = NULL;
            } 
            /*pPoint = NULL;
            gotValue = false;
            gotLPValues = false;
            */
        }
        pPoint = NULL;
        gotValue = false;
        gotLPValues = false;
        x++;
    }

    msgPtr->insert( msgMulti );
    {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << "msgPtr->insert( msgMulti ) "<< endl;
    }
    /*if( msgMulti != NULL )
    {
       delete msgMulti;
       msgMulti = NULL;
    }  */
}



