
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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/09/30 21:37:16 $
*    History: 
      $Log: ansi_application.cpp,v $
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

    _julieTest = 0;
    _wrDataSize = 0;

    _currentTable = CTIDBG_new BYTE[1024];
    getDatalinkLayer().init();
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
           getDatalinkLayer().buildSecure(security, xfer );
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
           getDatalinkLayer().buildAuthenticate(authenticate, xfer );
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
                getDatalinkLayer().buildTableRequest( xfer, _currentTableID, pread_offset, _currentTableOffset, _currentType );
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
                //getDatalinkLayer().buildTableRequest( xfer, _currentTableID, pread_offset, _currentTableOffset, _currentType );
                getDatalinkLayer().buildTableRequest( xfer, _currentTableID, full_read, _currentTableOffset, _currentType );
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
                if (_currentTableID == 7)
                {
                    {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl;
                  dout << RWTime::now() << " ** JULIE ** " <<_currentTableID<< endl;
                  dout << endl;
               }
                }
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
    if (_currentTableID == 7)
        {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl;
                  dout << RWTime::now() << " ** JULIE ** " <<_currentTableID<< endl;
                  dout << endl;
               }


    if( checkResponse( getDatalinkLayer().getCurrentPacket()[6] ) == true ||
        (getDatalinkLayer().getPacketPart() && !getDatalinkLayer().getPacketFirst()))
    {
        switch( _requestedState )
            {
             case identified:
             {
                 identificationData( getDatalinkLayer().getCurrentPacket() );
                 _currentState = getNextState (_requestedState);
                 break;
             }
             case request:
             {
                 if (_lpMode) 
                 {
                     memcpy (_lpTempBigTable+_totalBytesInTable, 
                         getDatalinkLayer().getCurrentPacket()+9, 
                         getDatalinkLayer().getPacketBytesReceived()-12); //header(6),crc(2),length(2),checksum(1),response(1)

                     _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-12;
                    {
                      CtiLockGuard< CtiLogger > doubt_guard( dout );
                      dout << RWTime::now() << " &&&&&**JULIE****&&&&  _lpMode " << endl;
                    }
                    if (getDatalinkLayer().getPacketPart() && getDatalinkLayer().getPacketFirst())
                    {
                        if (getDatalinkLayer().getSequence() %2 !=0) 
                              getDatalinkLayer().toggleToggle();
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
                              {
                              CtiLockGuard< CtiLogger > doubt_guard( dout );
                              dout << RWTime::now() << " &&&&&**JULIE****&&&&  toggleToggle " << endl;
                              } 
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
                 //_totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-12;

                 //commented out for sentinel...
                 //_currentState = _requestedState;
                 {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << RWTime::now() << " **JULIE****  _totalBytesInTable " << _totalBytesInTable<< endl;
              dout << RWTime::now() << " **JULIE****  _currentBytesExpected " << (int)_currentBytesExpected<< endl;
              dout << RWTime::now() << " **JULIE****  areThereMorePackets() " << areThereMorePackets()<< endl;
                }
                 // are there more pieces to this table
                 if( areThereMorePackets() )
                 {
                     // we need more data for this individual table
                     _currentTableOffset = _totalBytesInTable;
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
            case waitState:
            case negotiated:
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
        // timing state only valid for 12_21
        if (_prot_version == ANSI_C12_21)
        {
            next = timingSet;
        }
        else
        {
            next = loggedOn;
        }
        break;
    }
   case timingSet:
      next = loggedOn;
      break;

   case loggedOn:
      next = secured;
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
      //next = loggedOff;
       next = terminated;
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
         if( _prot_version == ANSI_C12_21 )
            next = disconnected;
         else
            _readComplete =  true;
            //            next = finalAck;
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
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );                              
        dout <<"  *******JULIE  *value "<<(int)*value<<" "<<(int)*(value +1)<<"  " <<(int)*(value +2) <<"  " <<(int)*(value +3) <<endl;

    } 
    for (int x = 0; x < size; x++) 
    {
        _parmPtr[x] = value[x];
    }

    /*{
        CtiLockGuard< CtiLogger > doubt_guard( dout );                              
        dout <<"  *******JULIE  *_parmPtr "<<(int)*_parmPtr<<"  " <<(int)*(_parmPtr +1)<<"  " <<(int)*(_parmPtr +2) <<endl;
        dout <<"  *******JULIE  *value "<<(int)*value<<"  " <<(int)*(value +1)<<"  " <<(int)*(value +2) <<endl;
    }  */

    return;
}
void CtiANSIApplication::setProcBfld( TBL_IDB_BFLD value)
{
    _currentProcBfld.selector = value.selector;
    _currentProcBfld.std_vs_mfg_flag = value.std_vs_mfg_flag;
    _currentProcBfld.tbl_proc_nbr = value.tbl_proc_nbr;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );                              
        dout <<"  *******JULIE  _currentProcBfld "<<(int)_currentProcBfld.selector<<"  " <<(bool)_currentProcBfld.std_vs_mfg_flag<<"  " <<(int)_currentProcBfld.tbl_proc_nbr <<endl;
    }
    USHORT *temp;
    temp = (USHORT *)&_currentProcBfld;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );                              
        dout <<"  *******JULIE  temp = (USHORT *)&_currentProcBfld "<<(int)*temp<<endl;
    }
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







