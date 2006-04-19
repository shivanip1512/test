// Queue wrapper
// Thain Spar


/* 
 *   Different Queue implementations to replace some of the RW queues.
 *
 */

#ifndef CTIQUEUES_H
#define CTIQUEUES_H

#include <queue>
#include <stack>

using std::stack;

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

template< class T >
class CtiStack{

    private:
        stack<T> s;
        int size;
    public:
        CtiStack(){
            size = 0;
        }
        int entries(){
            return size;
        }
        bool isEmpty(){
            return s.empty();
        }
        void clear(){
            while( !s.empty() ){
                s.pop();
            }
        }
        void push( T a ){
            s.push(a);
            ++size;
        }
        T pop(){
            T temp = s.top();
            s.pop();
            --size;
            return ( temp );
        }
        T top(){
            return s.top();
        }

};
#endif


