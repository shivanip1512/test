#ifndef __ERRCLIENT_HPP__
#define __ERRCLIENT_HPP__

#include "ctinexus.h"

#include "errmsg.h"
#include "proclog.h"

class CErrClient
{
public:

   // Constructors
   CErrClient();                             // Default
   CErrClient(const CErrClient& Cpy);        // Copy

   // Destructor
   ~CErrClient() {free(ClientNexus);}                         // do-nothing destructor!

   // Methods
   /*
    *  Not very well hidden, but good enough for me now.
    */
   CTINEXUS*   getNexus() { return (ClientNexus); }

   INT   Close();
   INT   Connect(const char *Name = NULL);
   INT   Log(CErrMsg &Msg);

private:
   // Data Members
   CTINEXUS    *ClientNexus;
};

#endif         //#ifndef __ERRCLIENT_HPP__


