
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ansi_application
*
* Date:   6/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_application.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/01/03 23:07:14 $
*    History: 
      $Log: ansi_application.cpp,v $
      Revision 1.8  2005/01/03 23:07:14  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.7  2004/12/10 21:58:40  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.6  2004/09/30 21:37:16  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.5  2003/04/25 15:13:45  dsutton
      Update of the base protocol pieces taking into account the manufacturer
      tables, etc.  New starting point

*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "ansi_application.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication::CtiANSIApplication()
{
    _currentTable = NULL;
    _lpTempBigTable = NULL;
    _parmPtr = NULL;
    for (int x = 0; x < 20; x++)
       _securityPassword[x] = 0xFF;

    _authTicket = NULL;
    _iniAuthVector = NULL;

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiANSIApplication::init( void )
{
    _currentState = identified;
    _requestedState = identified;
    _readComplete = false;
    _readFailed = false;
    _lpMode = false;

    _wrDataSize = 0;
    _negotiateRetry = 0;
    _currentTable = CTIDBG_new BYTE[1024];
    getDatalinkLayer().init();

    if (_ansiDeviceType == kv2)
    {
        getDatalinkLayer().setIdentityByte(0x40);
    }
    //else nothing

}
//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIApplication::reinitialize( void )
{
   getDatalinkLayer().reinitialize();
   destroyMe();
}

void CtiANSIApplication::terminateSession( void )
{
    _currentState = getNextState (_requestedState);
    setRetries (MAXRETRIES);
    setTableComplete (false);
    memset( _currentTable, NULL, sizeof( *_currentTable ) );
    //memset( _lpTempBigTable, NULL, sizeof( *_lpTempBigTable ) );
    _totalBytesInTable = 0;
}
//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIApplication::destroyMe( void )
{
    if( _currentTable != NULL )
    {
        delete _currentTable;
        _currentTable = NULL;
    }
    if( _lpTempBigTable != NULL )
    {
        delete _lpTempBigTable;
        _lpTempBigTable = NULL;
    }
    if (_parmPtr != NULL) 
    {
        delete _parmPtr;
        _parmPtr = NULL;
    }
    if (_authTicket != NULL)
    {
        delete _authTicket;
        _authTicket = NULL;
    }
    if (_iniAuthVector != NULL)
    {
        delete _iniAuthVector;
        _iniAuthVector = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication::~CtiANSIApplication()
{
    destroyMe();
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIApplication::generate( CtiXfer &xfer )
{
    bool retFlag = false;

     //do next step to get logged in
     switch( _currentState )
     {
     case identified:
        {
           getDatalinkLayer().buildIdentify( ident, xfer );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Identify**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           //reset the packet state
           getDatalinkLayer().initializeForNewPacket();
        }
        break;

     case negotiated:
        {
            if  ((int)_ansiDeviceType == sentinel) 
                getDatalinkLayer().buildNegotiate(negotiate_no_baud, xfer );
            else
                getDatalinkLayer().buildNegotiate(negotiate1, xfer );
            {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Negotiate**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case timingSet:
        {
           getDatalinkLayer().buildTiming(timing_setup, xfer );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Timing**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case loggedOn:
        {
           getDatalinkLayer().buildLogOn(logon, xfer );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Log On**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case secured:
        {
           getDatalinkLayer().buildSecure(security, xfer, _securityPassword );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Secure**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case authenticated:
        {
           getDatalinkLayer().buildAuthenticate(authenticate, xfer, _iniAuthVector );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Authenticate**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case request:
        {
            if (_currentTableID == 64) 
            {
                // make this generic
                //getDatalinkLayer().buildTableRequest( xfer, _currentTableID, pread_offset, _currentTableOffset, _currentType );
                getDatalinkLayer().buildTableRequest( xfer, _currentTableID, pread_offset, _currentTableOffset, _currentType, _maxPktSize.sh, _maxNbrPkts );
                {
                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                   dout << endl;
                   dout << RWTime::now() << " **Request Read LP Table**" << endl;
                   dout << endl;
                }
                _requestedState = _currentState;
                getDatalinkLayer().initializeForNewPacket();
            }
            else if (_currentTableID != 7) 
            {
                // make this generic
                BYTE operation; 
                short pktSize;
                // sentinel likes full reads, kv2 likes partial read offsets
                if  ((int)_ansiDeviceType == sentinel) 
                { 
                    if (_currentBytesExpected < _maxPktSize.sh)
                        operation =  full_read;
                    else
                        operation = pread_offset;
                }
                else 
                    operation =  pread_offset;

                
                if ((_currentBytesExpected - _currentTableOffset) < _maxPktSize.sh)
                {
                    pktSize = _currentBytesExpected - _currentTableOffset;
                }
                else
                {
                    pktSize = _maxPktSize.sh;
                }

                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                   dout << RWTime::now() << " ** _ansiDeviceType == " <<(int)_ansiDeviceType<< endl;
                   dout << RWTime::now() << " ** pktSize == " <<(int)pktSize<< endl;
                }
                getDatalinkLayer().buildTableRequest( xfer, _currentTableID, operation, _currentTableOffset, _currentType, pktSize, _maxNbrPkts );
                {
                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                   dout << endl;
                   dout << RWTime::now() << " **Request Read**" << endl;
                   dout << endl;
                }
                _requestedState = _currentState;
                getDatalinkLayer().initializeForNewPacket();
            }
            else
            {
                getDatalinkLayer().buildWriteRequest( xfer, _wrDataSize, _currentTableID, full_write, _currentProcBfld, _parmPtr, _wrSeqNbr );
                {
                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                   dout << endl;
                   dout << RWTime::now() << " **Request Write**" << endl;
                   dout << endl;
                }
                _requestedState = _currentState;
                getDatalinkLayer().initializeForNewPacket();
            }
        }
        break;

     case waitState:
         {
             getDatalinkLayer().buildWaitRequest( xfer);
             {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << endl;
                dout << RWTime::now() << " **Request Wait**" << endl;
                dout << endl;
             }
             _requestedState = _currentState;
             getDatalinkLayer().initializeForNewPacket();
         }
         break;

     case loggedOff:
        {
           getDatalinkLayer().buildLogOff(logoff, xfer );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Log Off**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case terminated:
        {
           getDatalinkLayer().buildTerminate(term, xfer );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Terminate**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case disconnected:
        {
           getDatalinkLayer().buildDisconnect(discon, xfer );
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << endl;
              dout << RWTime::now() << " **Disconnect**" << endl;
              dout << endl;
           }
           _requestedState = _currentState;
           getDatalinkLayer().initializeForNewPacket();

        }
        break;

     case passThrough:
         // this is here only to get by the generate to read new data
         break;
     }

     // we're always pass through at this point
     _currentState = passThrough;
     return retFlag;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiANSIApplication::initializeTableRequest( int aID, int aOffset, unsigned short aBytesExpected, BYTE aType, BYTE aOperation )
{

    _currentTableID = aID;
    _currentTableOffset = aOffset;
    _currentBytesExpected = aBytesExpected;
    _currentType = aType;
    _currentOperation = aOperation;

    // initialize everything here for the next table
    setRetries (MAXRETRIES);
    setTableComplete (false);
    memset( _currentTable, NULL, sizeof( *_currentTable ) );
    if (_lpMode == true) 
    {
        memset(_lpTempBigTable, NULL, sizeof( *_lpTempBigTable ) );
    } 
    _totalBytesInTable = 0;
    _initialOffset = aOffset;

    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << RWTime::now() << " ** DEBUG ****  _currentTableID  " <<(int)_currentTableID<< endl;
        dout << RWTime::now() << " ** DEBUG ****  _currentTableOffset  " <<(int)_currentTableOffset<< endl;
        dout << RWTime::now() << " ** DEBUG ****  _currentBytesExpected  " <<(int)_currentBytesExpected<< endl;
        dout << RWTime::now() << " ** DEBUG ****  _initialOffset  " <<(int)_initialOffset<< endl;
    }
}


//*************************************************************************************
//*************************************************************************************

bool CtiANSIApplication::decode( CtiXfer &xfer, int aCommStatus )
{
    bool retFlag = false;

    /****************************
    * returns true whether we received a valid piece of a packet
    *****************************
    */
    if (getDatalinkLayer().continueBuildingPacket( xfer, aCommStatus ))
    {
        // check if we have a full packet
        if (getDatalinkLayer().isPacketComplete())
        {
            // was CRC ok
            if (getDatalinkLayer().isCRCvalid())
            {
                // analyse the packet move on if successful
                if (analyzePacket())
                {
                    // rest number of tries
                    setRetries (MAXRETRIES);
                }
                else
                {
                    if( getRetries() > 0 )
                    {
                        setRetries( getRetries() - 1 );
                        _currentState = _requestedState;
                    }
                    else
                    {
                        // retries exhausted, figure out how to get from here (terminate session?)
                        _readFailed = true;
                    }
                }
            }
            else
            {
                // reset the state and ask again
                _currentState = _requestedState;
               /* {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl;
                  dout << RWTime::now() << " ** CRC Not Valid **" << endl;
                  dout << RWTime::now() << " ** _currentState/_requestedState " <<_currentState<< endl;
                  dout << endl;
               }   */

            }
        }
        else
        {
            // do nothing, continue in same state, datalink setup xfer appropriately
        }
    }
    else
    {
        if( getRetries() > 0 )
        {
            setRetries( getRetries() - 1 );
            _currentState = _requestedState;
        }
        else
        {
            // retries exhausted, figure out how to get from here (terminate session?)
            _readFailed = true;
        }
    }
    return retFlag;
}

bool CtiANSIApplication::analyzePacket()
{
    // check length and checksum here
    bool retFlag = true;

    if( checkResponse( getDatalinkLayer().getCurrentPacket()[6] ) == true ||
        (getDatalinkLayer().getPacketPart() && !getDatalinkLayer().getPacketFirst()))
    {
        switch( _requestedState )
            {
             case identified:
             {
                 identificationData( getDatalinkLayer().getCurrentPacket()+6 );
                 _currentState = getNextState (_requestedState);
                 break;
             }
             case request:
             {
                 if (_lpMode) 
                 {
                     if (getDatalinkLayer().getPacketPart())
                     {   
                         if (getDatalinkLayer().getPacketFirst()) 
                         {
                             memcpy (_lpTempBigTable+_totalBytesInTable, 
                                 getDatalinkLayer().getCurrentPacket()+9, 
                                 getDatalinkLayer().getPacketBytesReceived()-11); //header(6),crc(2),length(2),response(1)

                             _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-11;

                            if (getDatalinkLayer().getSequence() %2 !=0) //if odd, set toggle bit 
                            {
                                 getDatalinkLayer().toggleToggle();
                            }
                         }

                        else if (getDatalinkLayer().getSequence() == 0) 
                         {
                              // move the data into storage
                             memcpy (_lpTempBigTable+_totalBytesInTable, 
                                getDatalinkLayer().getCurrentPacket()+6, 
                                getDatalinkLayer().getPacketBytesReceived()-9); //header(6),crc(2),cksm(1)
                     
                             _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-9;
                                 
                         }
                         else
                         {
                              // move the data into storage
                             memcpy (_lpTempBigTable+_totalBytesInTable, 
                                getDatalinkLayer().getCurrentPacket()+6, 
                                getDatalinkLayer().getPacketBytesReceived()-8); //header(6),crc(2)
                     
                             _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-8;
                         }
                     }
                     else
                     {
                         // move the data into storage
                         memcpy (_lpTempBigTable+_totalBytesInTable, 
                            getDatalinkLayer().getCurrentPacket()+9, 
                            getDatalinkLayer().getPacketBytesReceived()-12); //header(6),crc(2),length(2),checksum(1),response(1)

                         _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-12;
                     }   
                 }
                 else if (getDatalinkLayer().getPacketPart()) 
                 { 
                     if (getDatalinkLayer().getPacketFirst()) 
                     {
                         // move the data into storage
                         memcpy (_currentTable+_totalBytesInTable, 
                            getDatalinkLayer().getCurrentPacket()+9, 
                            getDatalinkLayer().getPacketBytesReceived()-11); //header(6),crc(2),length(2),response(1)
                 
                         _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-11;

                         if (getDatalinkLayer().getSequence() %2 !=0) //if odd, set toggle bit 
                         {
                              getDatalinkLayer().toggleToggle();
                         }

                     }
                     else if (getDatalinkLayer().getSequence() == 0) 
                     {
                          // move the data into storage
                         memcpy (_currentTable+_totalBytesInTable, 
                            getDatalinkLayer().getCurrentPacket()+6, 
                            getDatalinkLayer().getPacketBytesReceived()-9); //header(6),crc(2),cksm(1)
                 
                         _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-9;
                             
                     }
                     else
                     {
                          // move the data into storage
                         memcpy (_currentTable+_totalBytesInTable, 
                            getDatalinkLayer().getCurrentPacket()+6, 
                            getDatalinkLayer().getPacketBytesReceived()-8); //header(6),crc(2)
                 
                         _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-8;
                     }
                 }
                 else if (_currentTableID == 7) 
                 {
                     setTableComplete (true);
                     _currentState = _requestedState;
                    break;
                 }
                 else
                 {
                     // move the data into storage
                     memcpy (_currentTable+_totalBytesInTable, 
                         getDatalinkLayer().getCurrentPacket()+9, 
                         getDatalinkLayer().getPacketBytesReceived()-12); //header(6),crc(2),length(2),checksum(1),response(1)

                     _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-12;
                 }

                 // are there more pieces to this table
                 if( areThereMorePackets() )
                 {
                     // we need more data for this individual table
                     _currentTableOffset = _totalBytesInTable + _initialOffset;
                     if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                     {
                          CtiLockGuard< CtiLogger > doubt_guard( dout );
                          dout << RWTime::now() << " ** DEBUG ****  _currentTableOffset " << _currentTableOffset<< endl;
                          dout << RWTime::now() << " ** DEBUG ****  _totalBytesInTable " << _totalBytesInTable<< endl;
                     }
                     setTableComplete(false);
                     _currentState = passThrough;
                 }
                 else
                 {
                     setTableComplete (true);
                     if (getDatalinkLayer().getPacketPart() && getDatalinkLayer().getSequence() == 0)
                     {
                         _currentState = waitState;
                     }
                     else
                     {
                         _currentState = _requestedState;
                     }
                 }
                 break;
             }
        case negotiated:
        {
            _maxPktSize.ch[1] =  getDatalinkLayer().getCurrentPacket()[7];
            _maxPktSize.ch[0] =  getDatalinkLayer().getCurrentPacket()[8];
            _maxNbrPkts =  getDatalinkLayer().getCurrentPacket()[9];
            _negBaudRate = getDatalinkLayer().getCurrentPacket()[10];
            if (getDatalinkLayer().getCurrentPacket()[10] == 0xb0)
            {
                _negotiateRetry++;
                _currentState = terminated;
            }
            else
            {
                 _currentState = getNextState (_requestedState);
            }
            break;
        }
        case waitState:
            case timingSet:
            case loggedOff:
            case terminated:
            case disconnected:
            case loggedOn:
            case secured:
            case authenticated:

                 _currentState = getNextState (_requestedState);
             default:
                      break;
            }
    }
    else if (_currentTableID == 7)
    {
        setTableComplete (true);

        retFlag = true;
    }
    else
    {
        {
           CtiLockGuard< CtiLogger > doubt_guard( dout );
           dout << RWTime::now() << " " << _requestedState << " response is not ok " << endl;
        }
        retFlag = false;
    }

    return retFlag;
}

bool CtiANSIApplication::areThereMorePackets()
{
    bool retVal;
    if (getDatalinkLayer().getPacketPart() )
    {
        if(getDatalinkLayer().getSequence() == 0 )  
        {
            if (_totalBytesInTable < _currentBytesExpected)
            {
                retVal = true;
            }
            else
            {
                retVal = false;
            }
        }
        else
        {
            retVal = true;
        }
    }
    else
    { 
        if (_totalBytesInTable < _currentBytesExpected)
        {
            retVal = true;
        }
        else
        {
            retVal = false;
        }
    }
    return retVal;
}


bool CtiANSIApplication::checkResponse( BYTE aResponseByte)
{
   bool     proceed = false;
   BYTE     msg;

   switch( aResponseByte )
   {
   case ok:
      {
         proceed = true;
      }
      break;

   case err:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Service Request Rejected" << endl;

         msg = err;
      }
      break;

   case sns:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Service Not Supported" << endl;

         msg = sns;
      }
      break;

   case isc:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Insufficent Security Clearance" << endl;

         msg = isc;
      }
      break;

   case onp:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Operation Not Possible" << endl;

         msg = onp;
      }
      break;

   case iar:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Inappropriate Action Requested" << endl;

         msg = iar;
      }
      break;

   case bsy:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Device Busy" << endl;

         msg = bsy;
      }
      break;

   case dnr:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Data Not Ready" << endl;

         msg = dnr;
      }
      break;

   case dlk:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Data Locked" << endl;

         msg = dlk;
      }
      break;

   case rno:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Renegotiate Request" << endl;

         msg = rno;
      }
      break;

   case isss:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << RWTime::now() << " The KV2 responded: Invalid Service Sequence State" << endl;

         msg = isss;
      }
      break;
   }
   return proceed;
}

void CtiANSIApplication::identificationData( BYTE *aPacket)
{                                                                 
    _prot_version = aPacket[1];
    {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << "  _prot_version " <<(int) _prot_version<< endl;
      }
    if( aPacket[4] == 0x00 )                //no authentication will be used
    {
     _authenticate = false;
    }
    else if( aPacket[4] == 0x01 )           //we'll use authentication
    {
     _authenticate = true;
     _authenticationType = aPacket[5];
     _algorithmID = aPacket[6];
    }
    else if( aPacket[4] == 0x02 )           //the device will even send us the value used by the auth. algorithm
    {
     _authenticate = true;
     _authenticationType = aPacket[5];
     _algorithmID = aPacket[6];

     _authTicketLength = aPacket[7];

     _authTicket = new BYTE[_authTicketLength];
     for (int i = 0; i < _authTicketLength; i++)
     {
         _authTicket[i] = aPacket[8+i];
     }
     encryptDataMethod();
    
     //FIXME: well, finish
     //we need to grab the algorithm value but I don't know
     //how it is used, so I'm not sure how to store it
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

BYTE* CtiANSIApplication::getCurrentTable( )
{
    if (_lpMode) 
    {
        return (_lpTempBigTable);
    }
    else
    {
        return (_currentTable);
    }
}

CtiANSIApplication::ANSI_STATES CtiANSIApplication::getNextState( ANSI_STATES current )
{
  ANSI_STATES   next;

   switch( current )
   {
   case identified:
      next = negotiated;
      break;

   case negotiated:
    {
        if (_negotiateRetry > 1 && _negotiateRetry < 3)
        {
            next = terminated;
        }
        else
        {
            // timing state only valid for 12_21
            if (_prot_version == ANSI_C12_21)
            {
                next = timingSet;
            }
            else
            {
                next = loggedOn;
            }
        }
        break;
    }
   case timingSet:
      next = loggedOn;
      break;

   case loggedOn:
      next = secured;
       //next = authenticated;
       break;

   case secured:
       {
           // timing state only valid for 12_21
           if (_prot_version == ANSI_C12_21)
           {
               if( _authenticate == true )
                  next = authenticated;
               else
                   next = request;
           }
           else
           {
               next = request;
           }
           break;
       }
   case authenticated:
      next = request;
      break;

   case request:
      next = loggedOff;
      // next = terminated;
      break;
   case waitState:
      //next = loggedOff;
       if (_currentTableID == -1) 
       {
           next = terminated;
       }
       else
       {
           next = request;
       }
      break;

   case loggedOff:
      next = terminated;
      break;

   case terminated:
      {
          if (_negotiateRetry > 1 && _negotiateRetry < 3)
          {
              next =  identified;
          }
          else
          {
              if( (int)_prot_version == ANSI_C12_21 )
                 next = disconnected;
              else
                 _readComplete =  true;
            //            next = finalAck;
          }
      }
      break;

   case disconnected:
      _readComplete=  true;
      //      next = finalAck;
      break;

   }
   return( next );
}


//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIDatalink &CtiANSIApplication::getDatalinkLayer( void )
{
   return _datalinkLayer;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiANSIApplication::isReadComplete( void )
{
   return _readComplete;
}

bool CtiANSIApplication::isReadFailed( void )
{
   return _readFailed;
}

bool CtiANSIApplication::isTableComplete( void )
{
   return _tableComplete;
}

CtiANSIApplication &CtiANSIApplication::setTableComplete( bool aFlag )
{
   _tableComplete = aFlag;
   return *this;
}
//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication &CtiANSIApplication::setRetries( int trysLeft )
{
   _retries = trysLeft;
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiANSIApplication::getRetries( void )
{
   return _retries;
}


void CtiANSIApplication::setLPDataMode( bool value, int sizeOfLpTable )
{
    _lpMode = value;
    _sizeOfLpTable = sizeOfLpTable;
    if (_lpMode) 
    {
        _lpTempBigTable = CTIDBG_new BYTE[_sizeOfLpTable];
    }
    else 
    {
        if (_lpTempBigTable != NULL) 
        {
            delete []_lpTempBigTable;
            _lpTempBigTable = NULL;
        }
    }
}

void CtiANSIApplication::populateParmPtr(BYTE *value, int size)
{
    if (_parmPtr != NULL) 
    {
        delete _parmPtr;
        _parmPtr = NULL;
    } 
    _parmPtr = new BYTE[size];
     
    for (int x = 0; x < size; x++) 
    {
        _parmPtr[x] = value[x];
    }
    return;
}
void CtiANSIApplication::setProcBfld( TBL_IDB_BFLD value)
{
    _currentProcBfld.selector = value.selector;
    _currentProcBfld.std_vs_mfg_flag = value.std_vs_mfg_flag;
    _currentProcBfld.tbl_proc_nbr = value.tbl_proc_nbr;
    return;
}

void CtiANSIApplication::setWriteSeqNbr( BYTE seqNbr )
{
    _wrSeqNbr = seqNbr;
    return;
}

void CtiANSIApplication::setProcDataSize( USHORT dataSize )
{
    _wrDataSize = dataSize;
    return;
}

void CtiANSIApplication::setPassword( BYTE *password )
{
    //password is 20 bytes HEX...So, they input a HEX string from the DB Editor.
    for (int x = 0; x < 20; x++)
    {
        _securityPassword[x] = *password;
        password++;
    }
    return;
}

void CtiANSIApplication::setAnsiDeviceType(BYTE devType)
{
    _ansiDeviceType = (ANSI_DEVICE_TYPE) devType;
    return;
}


/*****************************************************************************************
*   
*  Data Encryption Standard (ANSI Std X3.92-1981)
*
******************************************************************************************/


static BYTE    perm1[56] = {
    57, 49, 41, 33, 25, 17,  9,  1, 58, 50, 42, 34, 26, 18,
    10,  2, 59, 51, 43, 35, 27, 19, 11,  3, 60, 52, 44, 36,
    63, 55, 47, 39, 31, 23, 15,  7, 62, 54, 46, 38, 30, 22,
    14,  6, 61, 53, 45, 37, 29, 21, 13,  5, 28, 20, 12, 4
};

static BYTE    perm2[56] = {
     2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,
    16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,  1,
    30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
    44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 29
};

static BYTE    perm3[48] = {
    14, 17, 11, 24,  1,  5,  3, 28, 15,  6, 21, 10, 23, 19, 12,  4,
    26,  8, 16,  7, 27, 20, 13,  2, 41, 52, 31, 37, 47, 55, 30, 40,
    51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
};

static BYTE    perm4[64] = {
    58, 50, 42, 34, 26, 18, 10,  2, 60, 52, 44, 36, 28, 20, 12,  4,
    62, 54, 46, 38, 30, 22, 14,  6, 64, 56, 48, 40, 32, 24, 16,  8,
    57, 49, 41, 33, 25, 17,  9,  1, 59, 51, 43, 35, 27, 19, 11,  3,
    61, 53, 45, 37, 29, 21, 13,  5, 63, 55, 47, 39, 31, 23, 15,  7,
};

static BYTE    perm5[48] = {
    32,  1,  2,  3,  4,  5,  4,  5,  6,  7,  8,  9,  8,  9, 10, 11,
    12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21,
    22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,  1,
};

static BYTE    perm6[32] = {
    16,  7, 20, 21, 29, 12, 28, 17,  1, 15, 23, 26,  5, 18, 31, 10,
     2,  8, 24, 14, 32, 27,  3,  9, 19, 13, 30,  6, 22, 11,  4, 25,
};

static BYTE    perm7[64] = {
    40,  8, 48, 16, 56, 24, 64, 32, 39,  7, 47, 15, 55, 23, 63, 31,
    38,  6, 46, 14, 54, 22, 62, 30, 37,  5, 45, 13, 53, 21, 61, 29,
    36,  4, 44, 12, 52, 20, 60, 28, 35,  3, 43, 11, 51, 19, 59, 27,
    34,  2, 42, 10, 50, 18, 58, 26, 33,  1, 41,  9, 49, 17, 57, 25,
};

static BYTE    sboxes[8][64] = {
{
    14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,
    4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13,
},{
    15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10,3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5,
    0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15,13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9,
},{
    10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8,13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1,
    13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7,1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12,
},{
    7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15,13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9,
    10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4,3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14,
},{
    2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9,14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6,
    4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14,11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3,
},{
    12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11,10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8,
    9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6,4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13,
},{
    4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1,13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6,
    1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2,6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12,
},{
    13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7,1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2,
    7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8,2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11,
}};

static BYTE    keys[16][48];


void permutation(BYTE *dst, BYTE *src, BYTE lgn, BYTE *perm_table)
{
    BYTE tmp[64];

    if (src == NULL)
    {
        src = tmp;
        memcpy(src, dst, 64);
    }

    for (; lgn > 0; lgn--, dst++, perm_table++)
    {
        *dst = src[*perm_table - 1];
    }
}

void encryptionXor(BYTE *dst, BYTE *src, BYTE lgn)
{
    for ( ; lgn > 0; lgn--, dst++, src++)
    {
        *dst ^= *src;
    }
}

void encryptionCopy(BYTE *dst, BYTE *src, BYTE lgn)
{
    for (;lgn > 0; lgn--, dst++, src++)
    {
        *dst = *src;
    }
}
void sBoxes(BYTE *dst, BYTE *src, BYTE *sbox)
{
    int ii;

    ii = src[4];
    ii |= src[3] << 1;
    ii |= src[2] << 2;
    ii |= src[1] << 3;
    ii |= src[5] << 4;
    ii |= src[0] << 5;

    ii = sbox[ii];

    dst[3] = ii & 1;
    dst[2] = ii >> 1 & 1;
    dst[1] = ii >> 2 & 1;
    dst[0] = ii >> 3 & 1;
   
    
}
void dataEncryptionStandard(BYTE *_key, BYTE *_data, int _encrypt)
{
    BYTE key[64], data[64], right[48];
    int i, j;

    permutation(key, _key, 56, perm1);

    for (i = 1; i <= 16; i++)
    {
        permutation(key, NULL, 56, perm2);
        if (i != 1 && i != 2 && i !=9 && i != 16)
            permutation(key, NULL, 56, perm2);
        permutation(keys[_encrypt ? i - 1: 16 - i], key, 48, perm3);
    }
    permutation(data, _data, 64, perm4);

    for (i = 1; i <=16; i++)
    {
        permutation (right, data +32, 48, perm5);
        encryptionXor(right, keys[i - 1], 48);

        for (j = 0; j < 8; j++)
            sBoxes(right + 4 * j, right + 6 * j, sboxes[j]);

        permutation(right, NULL, 32, perm6);
        encryptionXor(right, data, 32);
        encryptionCopy(data, data + 32, 32);
        encryptionCopy(data +32, right, 32);
    }

    encryptionCopy(_data, data + 32, 32);
    encryptionCopy(_data + 32, data, 32);
    permutation(_data, NULL, 64, perm7);
}
int CtiANSIApplication::encryptDataMethod()
{

    BYTE key1[8] = {'B', 'E', 'E', 'F','B', 'E', 'E', 'F'};
     
    BYTE key[64];
    BYTE data[64];

    for (int i = 0; i < 8; i++)
    {
        key[(i*8)+0] = key1[i] & 0x01;
        key[(i*8)+1] = key1[i] & 0x02;
        key[(i*8)+2] = key1[i] & 0x04;
        key[(i*8)+3] = key1[i] & 0x08;
        key[(i*8)+4] = key1[i] & 0x10;
        key[(i*8)+5] = key1[i] & 0x20;
        key[(i*8)+6] = key1[i] & 0x40;
        key[(i*8)+7] = key1[i] & 0x80;

        data[(i*8)+0] = _authTicket[i] & 0x01;  
        data[(i*8)+1] = _authTicket[i] & 0x02;  
        data[(i*8)+2] = _authTicket[i] & 0x04;  
        data[(i*8)+3] = _authTicket[i] & 0x08;  
        data[(i*8)+4] = _authTicket[i] & 0x10;  
        data[(i*8)+5] = _authTicket[i] & 0x20;  
        data[(i*8)+6] = _authTicket[i] & 0x40;  
        data[(i*8)+7] = _authTicket[i] & 0x80;  


    }
    

    dataEncryptionStandard(key, data, 1);
    dataEncryptionStandard(key, data, 0);


    _iniAuthVector = new BYTE[8];
    for (i = 0; i < 8; i++)
    {
        _iniAuthVector[i] = (data[(i*8)+0] & 0x01) |
                            ((data[(i*8)+1] & 0x01) << 1) |
                            ((data[(i*8)+2] & 0x01) << 2) |
                            ((data[(i*8)+3] & 0x01) << 3) |
                            ((data[(i*8)+4] & 0x01) << 4) |
                            ((data[(i*8)+5] & 0x01) << 5) |
                            ((data[(i*8)+6] & 0x01) << 6) |
                            ((data[(i*8)+7] & 0x01) << 7);
                            
    }
    return 0;
}




