#include "yukon.h"

/* RW stuff MUST be first to avoid crap with macros max and min.. these WILL bite your butt */
#include <rw\rwtime.h>

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <process.h>

#include <iostream>
#include <iomanip>
using namespace std;

#ifdef OLD_STUFF
extern "C" {
   #include "os2_2w32.h"
   #include "cticalls.h"
   #include "ctinexus.h"
   #include "dsm2.h"
}
#else
   #include "cticalls.h"
   #include "ctinexus.h"
   #include "dsm2.h"
#endif


#include "proclog.h"

#include "errmsg.h"

CErrMsg::CErrMsg()
{
   MsgValid       = 0;

   ErrMsg.Command = 0;
   ErrMsg.OpFlags = 0;
   ErrMsg.Time    = 0;

   ErrMsg.Source[0] = '\0';
   ErrMsg.File[0] = '\0';
   ErrMsg.Message[0] = '\0';

   return;
}

CErrMsg::CErrMsg(const CErrMsg& Msg)
{
   if(this == &Msg)
      return;

   MsgValid       = 0;

   ErrMsg.Command = Msg.ErrMsg.Command;
   ErrMsg.OpFlags = Msg.ErrMsg.OpFlags;
   ErrMsg.Time    = Msg.ErrMsg.Time;

   strcpy(ErrMsg.Source    ,Msg.ErrMsg.Source);
   strcpy(ErrMsg.File      ,Msg.ErrMsg.File);
   strcpy(ErrMsg.Message   ,Msg.ErrMsg.Message);

   return;
}


CErrMsg::CErrMsg(CTIERRMSG *Msg)
{
   MsgValid       = 0;

   ErrMsg.Command = Msg->Command;
   ErrMsg.OpFlags = Msg->OpFlags;
   ErrMsg.Time    = Msg->Time;

   strcpy(ErrMsg.Source    ,Msg->Source);
   strcpy(ErrMsg.File      ,Msg->File);
   strcpy(ErrMsg.Message   ,Msg->Message);

   return;
}


BOOL CErrMsg::isQueuable()
{
   BOOL bRet;

   switch(ErrMsg.Command)
   {
   case CMD_CLOSEPROCLOG:
   case CMD_CLOSEMYSOCKET:
   case CMD_PURGEFILE:
   case CMD_SETLIMITS:
      {
         bRet = FALSE;
         break;
      }
   case CMD_DEFAULT:
   default:
      {
         bRet = TRUE;
         break;
      }
   }

   return bRet;
}

INT CErrMsg::Execute(BOOL bDump)
{

   if(bDump)
      Dump();              // Member Function to Dump the output

   switch(ErrMsg.Command)
   {
   case CMD_CLOSEPROCLOG:
      {
         // Handled at a higher level
         break;
      }
   case CMD_CLOSEMYSOCKET:
      {
         // printf("Closing Socket\n");
         break;
      }
   case CMD_DEFAULT:
      {
         break;
      }
   case CMD_PURGEFILE:
      {
         printf("Deleting all entries in %s\n",ErrMsg.File);
         if(_unlink(ErrMsg.File))
         {
            cerr << "Unable to delete the file" << endl;
         }
         break;
      }
   default:
      {
         break;
      }
   }

   return 0;
}

INT CErrMsg::Dump(void)
{
   CHAR  Temp[80];

   return 0;

   if(ErrMsg.Time)
   {
      UCTAsciiTime(ErrMsg.Time, (USHORT)DSTFlag(), Temp);
   }
   else
   {
      UCTAsciiTime(LongTime(), (USHORT)DSTFlag(), Temp);
   }


   printf("Log Request From: %s\n", ErrMsg.Source);
   printf("\tTime     : %ld = %s",  ErrMsg.Time, Temp);
   printf("\tCommand  : %d\n",      ErrMsg.Command);
   printf("\tOperation: %d\n",      ErrMsg.OpFlags);
   printf("\tLog to   : %s\n",      ErrMsg.File);
   printf("\tLog msg  : %s\n\n",    ErrMsg.Message);

   return 0;
}

INT CErrMsg::Get(CTINEXUS *Nexus, LONG Timeout)
{
   ULONG    BRead;

   MsgValid = Nexus->CTINexusRead(&ErrMsg,
                                  sizeof(CTIERRMSG),
                                  &BRead,
                                  Timeout);

   // cout << __FILE__ << " (" << __LINE__ << ")" << endl;


   if(!ErrMsg.Time)
   {
      ErrMsg.Time = LongTime();
   }

   return(MsgValid);


}

BOOL operator==(const CErrMsg &e1, const CErrMsg &e2)
{
   if(e1.ErrMsg.Time == e2.ErrMsg.Time)
      return TRUE;
   else
      return FALSE;

}

BOOL operator<(const CErrMsg &e1, const CErrMsg &e2)
{
   if(e1.ErrMsg.Time < e2.ErrMsg.Time)
      return TRUE;
   else
      return FALSE;
}

BOOL operator>(const CErrMsg &e1, const CErrMsg &e2)
{
   return (e1.ErrMsg.Time > e2.ErrMsg.Time);
}

ostream& operator << (ostream &out, CErrMsg e)
{
   out.flags(ios::left);
   // get the time adjusted and blasted to the file
   out << RWTime((ULONG)e.getEMsgTime() + rwEpoch).asString() <<": ";
   out << setw(10) << e.getEMsgSource() << ": ";
   return (out << " " << e.getEMsgMessage());
}


const CHAR* CErrMsg::setEMsgSource(CHAR *s)
{
   INT Max = strlen(ErrMsg.Source);

   if(strlen(s) > Max)
   {
      strncpy(ErrMsg.Source, s, Max - 1);
   }
   else
   {
      strcpy(ErrMsg.Source, s);
   }

   return (ErrMsg.Source);
}

const CHAR* CErrMsg::setEMsgFile(CHAR *s)
{
   INT Max = strlen(ErrMsg.File);

   if(strlen(s) > Max)
   {
      strncpy(ErrMsg.File, s, Max - 1);
   }
   else
   {
      strcpy(ErrMsg.File, s);
   }

   return (ErrMsg.File);
}

const CHAR* CErrMsg::setEMsgMessage(CHAR *s)
{
   INT Max = strlen(ErrMsg.Message);

   if(strlen(s) > Max)
   {
      strncpy(ErrMsg.Message, s, Max - 1);
   }
   else
   {
      strcpy(ErrMsg.Message, s);
   }

   return (ErrMsg.Message);
}

