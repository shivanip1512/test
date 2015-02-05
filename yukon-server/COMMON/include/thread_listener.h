#pragma once

#include <string>

#include "thread.h"

typedef int (*fooptr)();//I want this moved down like it is in register_data

class CtiThreadListener: public CtiThread
{
   void run( void );
   void registerThread( std::string name, int id, fooptr shutdown, fooptr alt, ULONG freq );

public:

    CtiThreadListener();
   virtual ~CtiThreadListener();

};
