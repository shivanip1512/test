#pragma once

#include <queue>

#include <rw/thr/mutex.h>
#include <rw/thr/guard.h>
#include <rw/thr/thrfunc.h>


template <class T> 
class CtiFDRIO
{
public:
    CtiFDRIO( ) : _inputSpunUp(FALSE), _outputSpunUp(FALSE)
        {   };

    ~CtiFDRIO( void )
    {
        T *tmp;
        //  i'm pretty sure this code is required, as the elements are allocated outside
        //    of this class.  otherwise, i'd just let the queue class delete them all.

        for( ; !_inbox.empty( ); )
        {
            tmp = _inbox.front( );
            _inbox.pop( );
            delete tmp;
        }
        for( ; !_outbox.empty( ); )
        {
            tmp = _outbox.front( );
            _outbox.pop( );
            delete tmp;
        }
    };

private:
    std::queue<T *> _inbox;
    std::queue<T *> _outbox;

    RWMutexLock _inboxMux;
    RWMutexLock _outboxMux;
    
    RWSemaphore _inputWaiting;
    RWSemaphore _outputWaiting;
    BOOL        _inputSpunUp;
    BOOL        _outputSpunUp;

    RWThreadFunction _inThreadFunc, _outThreadFunc;

    virtual void _inThread( void ) = 0;         //  these two functions must be implemented in any derived classes.
                                                //    see FDRIODispatch for an example.
    virtual void _outThread( void ) = 0;        //    they use the protected _postInput and _grabOutput functions
                                                //    (respectively) to interface with the queues.
protected:
    void _inReady( void )   {   _inputSpunUp = TRUE;    };
    void _outReady( void )  {   _outputSpunUp = TRUE;    };

    void _postInput( T *toSend );   //  places element in incoming queue.  consumes the element.

    T *_grabOutput( long milliseconds=0 );      //  waits for the specified interval for input.
                                                //    returns element from incoming queue;  NULL 
                                                //    if none waiting.  this element is on the heap,
                                                //    so delete it when you're finished
public:
    void startIO( void );

    void stopIO( void );
    
    BOOL ready( void )
    {   
        if( _inputSpunUp && _outputSpunUp )
            return TRUE;
        return FALSE;
    }
    
    void send( T *toSend );     //  places element in outgoing queue.  consumes the element.

    T *recv( long milliseconds=0 );     //  waits for the specified interval for input.
                                        //    returns element from incoming queue;  NULL if none waiting.
                                        //    this element is on the heap, so delete it when you're finished
};
