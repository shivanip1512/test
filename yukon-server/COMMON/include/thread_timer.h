#pragma once

#include "thread.h"
#include "thread_register_data.h"

class CtiThreadTimer: public CtiThread
{
private:

   void run( void );

public:

	CtiThreadTimer();                                       	
   virtual ~CtiThreadTimer();

};
