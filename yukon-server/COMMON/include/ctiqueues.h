// Queue wrapper
// Thain Spar


/* 
 *   Different Queue implementations to replace some of the RW queues.
 *
 */

#ifndef CTIQUEUES_H
#define CTIQUEUES_H

#include <queue>

template < class T >
class CtiPtrDeque{

    private:
		std::queue < T* > q;

    public:
        CtiPtrDeque(){}

        ~CtiPtrDeque(){
            T* item;
            while( !empty() ){
                item = popFront();
                delete item;            
            }
        }

        /* popFront() will return and remove the next element from the queue. */
        T* popFront(){
            if ( q.empty()) 
                return NULL;
            T* tmp = q.front();
            q.pop();
            return tmp;
        }
        
        bool append( T* elem ){
            q.push( elem );
            return true;
        }

        /* entries() calls size */
        int entries(){
            return q.size();
        }
        /* empty() will return true if the queue is empty, false if the queue is full */
        bool empty(){
            return q.empty();
        }

};

template < class T >
class CtiValDeque{

    private:
		std::queue < T > q;

    public:
        CtiValDeque(){};

        ~CtiValDeque(){
        }

        /* popFront() will return and remove the next element from the queue. */
        T popFront(){
            if ( q.empty()) 
                return NULL;
            T tmp = q.front();
            q.pop();
            return tmp;
        }
        T removeFirst(){
            return popFront();
        };       
        bool append( T elem ){
            q.push( elem );
            return true;
        }
        /* entries() calls size */
        int entries(){
            return q.size();
        }
        /* empty() will return true if the queue is empty, false if the queue is full */
        bool empty(){
            return q.empty();
        };
        void clear(){
            while ( !empty() ) {
                popFront();
            }
        }

};
#endif


