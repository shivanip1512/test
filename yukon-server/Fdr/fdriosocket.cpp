#include "yukon.h"

#include "fdriosocket.h"

template <class T>
CtiFDRIOSocket<T>::~CtiFDRIOSocket( )
{   
    _initThreadFunc.requestCancellation( );
    _initThreadFunc.join( );
    
    try
    {
        delete _inPortal;
        delete _outPortal;
    }
    catch( RWSockErr &msg )
    {
        if(msg.errorNumber() != RWNETNOTINITIALISED)
        {
            cout << "Socket Error :" << msg.errorNumber() << " occurred" << endl;
            cout << "  " << msg.why() << endl;
        }
    }
}


template <class T>
void CtiFDRIOSocket<T>::_initThread( void )
{
    try
    {   //  connect if we're active, else wait to receive one from the outside
        if( _portNumber )
        {
            _inetPort = RWInetPort(_portNumber);
            _inetAddr = RWInetAddr(_inetPort);
            _outPortal = RWSocketPortal( _inetAddr );
        }
        else
        {
            //  FIX_ME:  need to put rwServiceCancellation calls in here,
            //             and put a timeout on aquiring the socket.
            //             ditto below (~lines 64-67).
            //  FIX_ME:  need to extract the destination(?) and port from this portal
            //             for usage below, if we're a dual socket connection

            _submittedSockSem.acquire( );   //  this will wait for submitSocket( ) to be called,
            _inPortal = _submittedPortal;   //    which will mean that an RWSocketPortal has been
        }                                   //    placed into _submittedPortal, and the destination 
                                            //    and port have been extracted and set.

        //  if we are not a dual socket connection, then the in and out portals are the same.
        //    otherwise, we need to either connect or receive again.
        //    perhaps eventually make this into a couple small functions to cut down on code
        //    redundancy here... ?
        if( _socketStyle == FDRSingleSocket )
        {
            if( _portNumber )
                _inPortal = _outPortal;
            else
                _outPortal = _inPortal;
        }
        else
        {
            if( _portNumber )
            {
                //  fix me too, please

                _submittedSockSem.acquire( );   //  this will wait for submitSocket( ) to be called
                _outPortal = _submittedPortal;
            }
            else
            {
                _inetPort = RWInetPort(_portNumber);
                _inetAddr = RWInetAddr(_inetPort);
                _inPortal = RWSocketPortal( _inetAddr );
            }
        }
    }
    catch( RWCancellation &cancellationMsg )
    {
        return;
    }

    _inSockSem.release( );
    _outSockSem.release( );
}


template <class T>
void CtiFDRIOSocket<T>::submitSocket( RWSocketPortal *newPortal )
{
    _submittedPortal = newPortal;
    //  will only be called once by the DLL functions, so we'll never overwrite _submittedPortal
    //    (or release more than once).
    _submittedSem.release( );
}


template <class T>
void CtiFDRIOSocket<T>::_inThread( void )
{
    RWRunnableSelf  _pSelf = rwRunnable( );
    char            *incomingCharMsg,
                    signature[4];
    T               *incomingTMsg;
    int             incomingSize;
        
    try
    {
        for( ; ; )
        {
            _inSockSem.acquire( 200 );  //  wait for the socket to be ready
            _pSelf.serviceCancellation( );
        }
        
        for( ; ; )
        {
            //  wait for 4 bytes
            for( ; _inPortal->socket( ).recv( signature, 4, MSG_PEEK ) != 4; )
            {
                rwSleep( 50 );
                _pSelf.serviceCancellation( );
            }
            
            //  find out how much we need to read in
            incomingSize = _idSizeof( signature );
            incomingCharMsg = new char[incomingSize];

            //  wait until it's all waiting there to be read
            for( ; _inPortal->socket( ).recv( incomingCharMsg, incomingSize, MSG_PEEK ) != incomingSize; )
            {
                rwSleep( 50 );
                _pSelf.serviceCancellation( );
            }

            
            _inPortal->recv( incomingCharMsg, incomingSize );   //  read in the message
            
            incomingTMsg = _prepareInput( incomingCharMsg );    //  translate it into queue form
            _postInput( incomingTMsg );                         //  and stuff it into the queue
        }
    }
    catch( RWCancellation &cancellationMsg )
    {
        return;
    }
}

template <class T>
void CtiFDRIOSocket<T>::_outThread( void )
{
    RWRunnableSelf  _pSelf = rwRunnable( );
    T               *outgoingTMsg;
    char            *outgoingCharMsg;
    int             bytesToSend, bytesSent;
        
    try
    {
        for( ; ; )
        {
            _outSockSem.acquire( 200 ); //  wait for the socket to be ready
            _pSelf.serviceCancellation( );
        }
        
        for( ; ; )
        {
            _pSelf.serviceCancellation( );
            if( outgoingTMsg = _grabOutput( 200 ) ) //  if something's in the queue (null on nothing queued up)
            {
                outgoingCharMsg = _prepareOutput( T *toOutput );    //  translate the message into raw form
                bytesToSend = _idSizeof( outgoingCharMsg );     //  find out how big it is
                bytesSent = 0;  
                //  until we've sent it all
                for( ; bytesToSend - bytesSent; )
                {
                    //  increment by number of bytes actually sent
                    bytesSent += _outPortal->send( outgoingCharMsg + bytesSent, bytesToSend - bytesSent );

                    pSelf.serviceCancellation( );   //  i'm not sure how safe this is, but if we're
                }                                   //    cancelled, the other side will die eventually,
            }                                       //    and anything partially sent will be discarded.
        }
    }
    catch( RWCancellation &cancellationMsg )
    {
        return;
    }
}


//  socket reading, writing, and peeking code from ctinexus.cpp, for reference

/*
INT CTINEXUS::CTINexusWrite(VOID *buf, ULONG len, PULONG BytesWritten, LONG TimeOut)
{
   ULONG    BytesSent   = 0;
   CHAR     *bptr       = (CHAR*)buf;
   INT      nReason;


   *BytesWritten = 0;

   do
   {
      BytesSent = send(sockt, bptr, len, 0);

      if(BytesSent == SOCKET_ERROR)
      {
         nReason = CTIGetLastError();
         CTINexusReportError(__FILE__, __LINE__, nReason);
         return nReason;
      }

      *BytesWritten  += BytesSent;
      bptr           += BytesSent;
      len            -= BytesSent;

   } while(len > 0);

   return(0);
}
*/

/*
IM_EX_CTIBASE INT CTINexusRead(CTINEXUS *Nexus, VOID *buf, ULONG len, PULONG BRead, LONG TimeOut)
{
    INT      BytesRead   = 0;
    ULONG    BytesAvail  = 0;
    CHAR     *bptr       = (CHAR*)buf;
    INT      nReturn;
    LONG    nLoops      = 0;


    if( TimeOut > 0L )
    {
        do
        {
            if( Nexus->NexusState == CTINEXUS_STATE_NULL )
            {
                // This guy is broken!
                break; // the do loop
            }
            nReturn = ioctlsocket(Nexus->socket, FIONREAD, &BytesAvail);

            if( nReturn == SOCKET_ERROR )
            {
                INT Error = CTIGetLastError();
                CTINexusReportError(__FILE__, __LINE__, Error );
                return(-Error);
            }
            else if( 
                   (BytesAvail                 && Nexus->NexusFlags | CTINEXUS_FLAG_READANY)       ||
                   (BytesAvail >= (ULONG)len   && Nexus->NexusFlags | CTINEXUS_FLAG_READEXACTLY)
                   )
            {
                break;
            }

            Sleep(50L);
        } while( ++nLoops < TimeOut * 20 );

        if( Nexus->NexusState != CTINEXUS_STATE_NULL && nLoops >= TimeOut * 20 )
        {
            if( 
              (Nexus->NexusFlags | CTINEXUS_FLAG_READEXACTLY && BytesAvail < (ULONG)len)          ||
              (Nexus->NexusFlags | CTINEXUS_FLAG_READANY     && BytesAvail == 0)
              )
            {
                return(-ERR_CTINEXUS_READTIMEOUT);
            }
        }
    }
    else if( TimeOut < 0L )
    {
        for( ;Nexus->NexusState != CTINEXUS_STATE_NULL; )
        {
            nReturn = ioctlsocket(Nexus->socket, FIONREAD, &BytesAvail);

            if( nReturn == SOCKET_ERROR )
            {
                INT Error = CTIGetLastError();
                CTINexusReportError(__FILE__, __LINE__, Error );
                return(-Error);
            }
            else if( 
                   (BytesAvail                 && Nexus->NexusFlags | CTINEXUS_FLAG_READANY)       ||
                   (BytesAvail >= (ULONG)len   && Nexus->NexusFlags | CTINEXUS_FLAG_READEXACTLY)
                   )
            {
                break;
            }
            else
            {
                try
                {
                    rwServiceCancellation();
                }
                catch( RWCancellation &c )
                {
                    // cout << "Caught a cancellation in the nexus read" << endl;
                    Nexus->NexusState = CTINEXUS_STATE_NULL;
                    break;
                }

                Sleep(1000L); //Sleep(50L);
            }
        }
    }

    //
    //  data is there, or we don't care about the any TimeOut value!
    //
    if( Nexus->NexusState != CTINEXUS_STATE_NULL )
    {
        do
        {
            BytesRead = recv(Nexus->socket, bptr, len, 0);

            if( BytesRead == SOCKET_ERROR )
            {
                CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
                return(ErrorNexusRead);
            }

            *BRead += BytesRead;

            if( Nexus->NexusFlags | CTINEXUS_FLAG_READANY )
            {
                break;
            }

            bptr += BytesRead;
            len -= BytesRead;
        } while( len > 0 );
    }

    return(0);
}
*/

/*
IM_EX_CTIBASE INT CTINexusPeek(CTINEXUS *Nexus, VOID *buf, ULONG len, PULONG BRead)
{
    INT      BytesRead   = 0;
    ULONG    BytesAvail  = 0;
    CHAR     *bptr       = (CHAR*)buf;
    INT      nReturn;
    LONG    nLoops      = 0;


    nReturn = ioctlsocket(Nexus->socket, FIONREAD, &BytesAvail);

    if( nReturn == SOCKET_ERROR )
    {
        CTINexusReportError(__FILE__, __LINE__, CTIGetLastError() );
        return(-CTIGetLastError());
    }

    //
    //  data is there, or we don't care about the any TimeOut value!
    //

    if( BytesAvail )
    {
        if( len > BytesAvail )
            len = BytesAvail;

        do
        {
            BytesRead = recv(Nexus->socket, bptr, len, MSG_PEEK);

            if( BytesRead == SOCKET_ERROR )
            {
                CTINexusReportError(__FILE__, __LINE__, CTIGetLastError());
                return(ErrorNexusRead);
            }

            *BRead += BytesRead;

            if( Nexus->NexusFlags | CTINEXUS_FLAG_READANY )
            {
                break;
            }

            bptr += BytesRead;
            len -= BytesRead;
        } while( len > 0 );
    }

    return(0);
}
*/
