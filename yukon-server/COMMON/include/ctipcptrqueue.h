// Queue wrapper
// Thain Spar


/* Ctipcptrqueue is designed to pass pointers to memory between threads. Once 
 * something is 'written' to the queue it is considered property of the queue.
 * Altering the memory after inserting it could result in undefined behavior.
 * Same goes for when you read something off the queue, once you read it off, the 
 * queue will no longer do anything with the object.
 */

#ifndef CTIPCPTRQUEUE_H
#define CTIPCPTRQUEUE_H

#include <queue>
#include <boost/thread/condition.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/xtime.hpp>
#include "mutex.h"
#include "guard.h"

template < class T >
class CtiPCPtrQueue{

    private:
		std::queue < T* > q;
        bool closed;
        boost::condition wait;
        boost::mutex mux;

    public:
        CtiPCPtrQueue(){
            closed = false;
        };
        ~CtiPCPtrQueue(){
            T* item;
            while( tryRead(item) )
                delete item;            
        };

        /* read() will return and remove the next element from the queue. 
         * The element will be considered beloning to whomever read it off.
         */
        T* read(){
			boost::mutex::scoped_lock scoped_lock(mux);
            if ( q.empty()) 
                return NULL;
            T* tmp = q.front();
            q.pop();
            return tmp;
        }

        bool read(T*& result, long milli){
            boost::mutex::scoped_lock scoped_lock(mux);
            bool success = false;
            struct boost::xtime xt;
            xt.sec = milli;
            if ( q.empty() ) {
                success = wait.timed_wait( scoped_lock, xt );
                if (success) {
                    result = q.front();
                    q.pop();
                    return true;
                }else{
                    result = NULL;
                    return false;
                }
            }else{
                result = q.front();
    			q.pop();
                return true;
            }
        }

        /* Removes and returns the next element in the queue. If the queue is
         * empty it will set result to NULL and return false.
         */
        bool tryRead(T*& result){
            boost::mutex::scoped_lock scoped_lock(mux);
            if ( q.empty() ) {
                result = NULL;
                return false;
            }
            else{
                result = q.front();
                q.pop();
                return true;
            }
        }
        /*  canRead will return true if the queue can be read from.
         *  It will return false if the queue is empty.
         */
        bool canRead(){
            boost::mutex::scoped_lock scoped_lock(mux);
            if ( q.empty() )
                return false;
            else
                return true;
        }

        /* write() will add an element to the end of the queue.
         * write() will not add an element to the queue of it is closed.
         * If there is a thread waiting for an element, it will notify that thread
         * after an element is added.
         */
        bool write( T* elem ){
            boost::mutex::scoped_lock scoped_lock(mux);
            if (closed == true)
                return false;
            q.push( elem );
			wait.notify_one();
            return true;
        };
        /* entries() calls size */
        int entries(){
            boost::mutex::scoped_lock scoped_lock(mux);
            return q.size();
        };
        /* empty() will return true if the queue is empty, false if the queue is full */
        bool empty(){
            boost::mutex::scoped_lock scoped_lock(mux);
            return q.empty();
        };
        /*  will mark the queue so as to not accept more writes.
        *   Reads will still work, until the queue is empty.
        */
        void close (void){
            boost::mutex::scoped_lock scoped_lock(mux);
            closed = true;
        };
        /* open() will open the queue for more writes */
        void open (void){
            boost::mutex::scoped_lock scoped_lock(mux);
            closed = false;
        };
        /* isOpen returns a bool true if the queue is open, false if it is closed.*/
        bool isOpen(void){
            boost::mutex::scoped_lock scoped_lock(mux);
            return !closed;
        };

};

#endif

