#pragma once

#include <string>

#include "thread.h"

typedef int (*fooptr)();//I want this moved down like it is in register_data

class CtiThreadListener: public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiThreadListener(const CtiThreadListener&);
    CtiThreadListener& operator=(const CtiThreadListener&);

private:

   void run( void );
   void registerThread( std::string name, int id, fooptr shutdown, fooptr alt, ULONG freq );

public:

	CtiThreadListener();
   virtual ~CtiThreadListener();

};
