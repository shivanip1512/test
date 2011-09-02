// Queue wrapper
// Thain Spar


/* Ctipcptrqueue is designed to pass pointers to memory between threads. Once
 * something is 'written' to the queue it is considered property of the queue.
 * Altering the memory after inserting it could result in undefined behavior.
 * Same goes for when you read something off the queue, once you read it off, the
 * queue will no longer do anything with the object.
 */

#pragma once

#include <list>
#include <LIMITS>

#include <boost/thread/condition.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/xtime.hpp>


#include "mutex.h"
#include "guard.h"

template < class T >
class CtiPCPtrQueue{

    private:
        std::list < T* > q;
        bool closed;
        boost::condition wait;
        mutable boost::mutex mux; // This mutex is not recursive.

        // If there are elements in the queue, this returns true and the first element.
        // If there are NO elements in the queue, this returns false and result = NULL.
        bool getFront(T*& result)
        {
            if ( q.empty() ) {
                result = NULL;
                return false;
            }
            else{
                result = q.front();
                q.pop_front();
                return true;
            }
        }

    public:
        CtiPCPtrQueue(){
            closed = false;
        };
        ~CtiPCPtrQueue(){
            boost::mutex::scoped_lock scoped_lock(mux);
            closed = true;
            T* item;
            while( getFront(item) )
                delete item;
        };

        /* read() will return and remove the next element from the queue.
         * The element will be considered beloning to whomever read it off.
         */
        T* read(){
            T* temp = NULL;
            std::numeric_limits<long> lim;
            read( temp, lim.max() );

            return temp;
        }

        bool read(T*& result, unsigned milli){
            boost::mutex::scoped_lock scoped_lock(mux);
            bool success = true;
            struct boost::xtime xt;
            boost::xtime_get(&xt, boost::TIME_UTC);
            xt.sec  += milli/1000;
            xt.nsec += (milli%1000)*1000000;

            if ( q.empty() ) {
                success = wait.timed_wait( scoped_lock, xt );
                if (success && !q.empty()) {
                    result = q.front();
                    q.pop_front();
                    return true;
                }else{
                    result = NULL;
                    return false;
                }
            }else{
                result = q.front();
                q.pop_front();
                return true;
            }
        }

        /* Removes and returns the next element in the queue. If the queue is
         * empty it will set result to NULL and return false.
         */
        bool tryRead(T*& result){
            boost::mutex::scoped_lock scoped_lock(mux);
            return getFront(result);
        }

        /*  canRead will return true if the queue can be read from.
         *  It will return false if the queue is empty.
         */
        bool canRead(){
            boost::mutex::scoped_lock scoped_lock(mux);

            return !q.empty();
        };

        /* write() will add an element to the end of the queue.
         * write() will not add an element to the queue of it is closed.
         * If there is a thread waiting for an element, it will notify that thread
         * after an element is added.
         */
        bool write( T* elem ){
            boost::mutex::scoped_lock scoped_lock(mux);
            if (closed == true)
                return false;
            q.push_back( elem );
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

