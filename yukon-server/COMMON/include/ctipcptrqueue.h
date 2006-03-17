// Queue wrapper
// Thain Spar


// pop will return the element. Use pop to keep it threadsafe.
// Using front() then pop() has a small chance of erroring due to threads.

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
        ~CtiPCPtrQueue(){//???  check
            T* item;
            while( tryRead(item) )
                delete item;            
        };

        /* read() will return and remove the next element from the queue. 
         * this is in place for RW compatability.
         */
        T* read(){
			boost::mutex::scoped_lock scoped_lock(mux);
            if ( q.empty()) 
                return NULL;
            T* tmp = q.front();// Check This
            q.pop();
            return tmp;
        }

        bool read(T*& result, long milli){
            boost::mutex::scoped_lock scoped_lock(mux);
            bool timeout = false;
            struct boost::xtime xt;
            xt.sec = milli;
            if ( q.empty() ) {
                timeout = wait.timed_wait( scoped_lock, xt );
                if (timeout) {
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
         * empty and closed it throws an exception, otherwise just returns false if empty.
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
         *  conditions where it cannot include, being closed or being empty.
         */
        bool canRead(){
            boost::mutex::scoped_lock scoped_lock(mux);
            if ( q.empty() )
                return false;
            else
                return true;
        }

        /* write() will add an element to the end of the queue.
         * It is here in addition to push() for RW compatability.
         * write() will not add an element to the queue of it is closed.
         */
        bool write( T* elem ){
            boost::mutex::scoped_lock scoped_lock(mux);
            if (closed == true)
                return false;
            q.push( elem );
			wait.notify_one();
            return true;
        };
        /* entries() calls size. This is here to match RW and be backwards compatible.
         */
        int entries(){
            boost::mutex::scoped_lock scoped_lock(mux);
            return q.size();
        };
        bool empty(){
            boost::mutex::scoped_lock scoped_lock(mux);
            return q.empty();
        };
        void close (void){
            boost::mutex::scoped_lock scoped_lock(mux);
            closed = true;
        };
        void open (void){
            boost::mutex::scoped_lock scoped_lock(mux);
            closed = false;
        };
        bool isOpen(void){
            boost::mutex::scoped_lock scoped_lock(mux);
            return !closed;
        };

};

#endif

