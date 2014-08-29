#pragma once

#include "thread.h"
#include "thread_register_data.h"

class CtiThreadTimer: public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiThreadTimer(const CtiThreadTimer&);
    CtiThreadTimer& operator=(const CtiThreadTimer&);

private:

   void run( void );

public:

	CtiThreadTimer();                                       	
   virtual ~CtiThreadTimer();

};
