#ifndef __ERRMSG_HPP__
#define __ERRMSG_HPP__

#include <windows.h>
#include <iostream>
using namespace std;

#include "proclog.h"

#include <rw/tpsrtvec.h>
#include <rw/rstream.h>

class CErrMsg
{
public:


   // Constructors
   CErrMsg();                       // Default
   CErrMsg(const CErrMsg& Msg);     // copy constructor

   CErrMsg(CTIERRMSG *Msg);      // A Conversion from C constructor

   // Destructor
   ~CErrMsg() {};                   //  A do-nothing destructor!

   // Methods
   INT         Get(CTINEXUS *Nexus, LONG Timeout);
   INT         Execute(BOOL bDump = TRUE);
   INT         Dump(void);

   friend BOOL operator==(const CErrMsg &e1, const CErrMsg &e2);
   friend BOOL operator<(const CErrMsg &e1, const CErrMsg &e2);
   friend BOOL operator>(const CErrMsg &e1, const CErrMsg &e2);
   friend ostream& operator<<(ostream &out, CErrMsg e);


   BOOL        isQueuable();
   INT         getMsgValid()        {return(MsgValid);}
   INT         setMsgValid(INT n)   {return(MsgValid = n);}

/*
typedef struct {

   INT               Command;
   INT               OpFlags;

   LONG              Time;

   CHAR              Source[64];    // Who sourced the message,
   CHAR              File[64];
   CHAR              Message[256];

} CTIERRMSG;
*/

   INT               getEMsgCommand()        { return (ErrMsg.Command);          }
   INT               setEMsgCommand(int n)   { return (ErrMsg.Command = n);      }
   INT               getEMsgOpFlags()        { return (ErrMsg.OpFlags);          }
   INT               setEMsgOpFlags(int n)   { return (ErrMsg.OpFlags = n);      }
   LONG              getEMsgTime()           { return (ErrMsg.Time);             }
   LONG              setEMsgTime(LONG l)     { return (ErrMsg.Time = l);         }

   const CHAR*       getEMsgSource()         { return (ErrMsg.Source);           }
   const CHAR*       getEMsgFile()           { return (ErrMsg.File);             }
   const CHAR*       getEMsgMessage()        { return (ErrMsg.Message);          }

   const CHAR*       setEMsgSource(CHAR *s);
   const CHAR*       setEMsgFile(CHAR *s);
   const CHAR*       setEMsgMessage(CHAR *s);

   CTIERRMSG*        getEMsgPtr()            { return (&ErrMsg);                 }

private:

   // Data Members
   CTIERRMSG   ErrMsg;
   INT         MsgValid;            // = 0 if the message is valid after a get call.
};

#endif                              // #ifndef __ERRMSG_HPP__

