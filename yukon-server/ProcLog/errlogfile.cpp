#include "yukon.h"

#include <windows.h>
#include <fstream>
using namespace std;

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <memory.h>
#include <process.h>



#ifdef OLD_STUFF
extern "C" {
   #include "os2_2w32.h"
   #include "cticalls.h"
   #include "ctinexus.h"
   #include "dsm2.h"
}
#else
   #include "os2_2w32.h"
   #include "cticalls.h"
   #include "ctinexus.h"
   #include "dsm2.h"
#endif

#include "proclog.h"
#include "dllmain.h"
#include "errmsg.h"
#include "errclient.h"


#include "errserver.h"


CErrLogFile::~CErrLogFile()
{
   // Do destructor stuff here

   // Dump any entries which may have come in since the last dump!
   LogDump();
}

CErrLogFile::CErrLogFile()
{
   TransactionLimit = 10;
   TimeLimit        = 10;
   NextWrite = (LONG)LongTime() + (LONG)TimeLimit;
   SelfImportance = 0;
}

CErrLogFile::CErrLogFile(const char *s)
{
   TransactionLimit = 10;
   TimeLimit        = 10;
   NextWrite = (LONG)LongTime() + (LONG)TimeLimit;
   // strcpy(FileName, s);
   FileName = RWCString(s);
   SelfImportance = 0;
}

INT CErrLogFile::Add(CErrMsg *Msg)
{
   RWMutexLock::LockGuard guard(ListMux);
   int      nRet = 0;
   INT      Op   = Msg->getEMsgOpFlags();

   if(Msg->isQueuable())
   {
      ErrList.insert(Msg);

      if(Op)
      {
         // Any Message I don't handle will return here to be handled higher up as well
         nRet = HandleErrMessage(*Msg);
      }
   }
   else
   {
      LogDump();             // Clear out the queue....
      Msg->Execute(TRUE);    // Do whatever the command says to do in terms of the message

      // Any Message I don't handle will return here to be handled higher up as well
      nRet = HandleErrMessage(*Msg);
   }

   return nRet;
}

INT CErrLogFile::AddACopy(CErrMsg *OldMsg)
{
   int      nRet = 0;
   CErrMsg *Msg  = new CErrMsg(*OldMsg);
   INT      Op   = Msg->getEMsgOpFlags();

   if(Msg->isQueuable())
   {
      // RWMutexLock::LockGuard guard(ListMux);

      ErrList.insert(Msg);

      if(Op)
      {
         // Any Message I don't handle will return here to be handled higher up as well
         nRet = HandleErrMessage(*Msg);
      }
   }
   else
   {
      LogDump();             // Clear out the queue....
      Msg->Execute(TRUE);    // Do whatever the command says to do in terms of the message

      // Any Message I don't handle will return here to be handled higher up as well
      nRet = HandleErrMessage(*Msg);
   }

   return nRet;
}

int CErrLogFile::LogDump()
{
   int   i;
   int   iRet = 0;
   LONG  Now  = (LONG)LongTime();
   int   Num  = ErrList.entries();

   CErrMsg  *Err;

   if(FileName.length() > 0)
   {
      ofstream  fout(FileName  , ios_base::out | ios_base::app);     // append to end of file

      NextWrite = Now + 10L;      // 10 seconds

      if(Num)
      {
         if(fout.is_open())
         {
            for(i = 0; i < Num; i++)
            {
               fout << *ErrList[i] << endl;                       // Write it out to the file
            }

            if(fout.is_open())
               fout.close();
         }
         else
         {
            cerr << "Cannot open " << FileName << endl;
         }

         for(i = 0; i < Num; i++)
         {
            Err = ErrList.removeFirst();                          // get rid of the list entry
            if(!SelfImportance)
            {
               cout << *Err << endl;                                 // Write it out to the screen
            }
            delete Err;                                           // get rid of the error class instance
         }
      }
   }

   return iRet;
}

BOOL CErrLogFile::DoLogDump()
{
   BOOL  bRet = FALSE;
   LONG  Now  = (LONG)LongTime();

   if(
     ErrList.entries() >= TransactionLimit              ||
     ((NextWrite <= Now) && (ErrList.entries() > 0))
     )
   {
      bRet = TRUE;
   }

   return bRet;
}

CErrLogFile& CErrLogFile::operator=(const CErrLogFile &e2)
{
   if(this != &e2)
   {

      TransactionLimit = e2.TransactionLimit;
      NextWrite = e2.NextWrite;
      // strcpy(FileName, e2.FileName);

      FileName = e2.FileName;
   }

   return *this;
}
BOOL operator==(const CErrLogFile &e1, const CErrLogFile &e2)
{
   BOOL bRet = FALSE;

   if(!strcmp(e1.FileName, e2.FileName))
   {
      bRet = TRUE;
   }

   return bRet;

}

INT CErrLogFile::HandleErrMessage(CErrMsg &Msg)
{
   INT   nRet = 0;

   switch(Msg.getEMsgCommand())
   {
   case CMD_CLOSEMYSOCKET:
      {
         // Just Pass this on on up the chain...
         nRet = Msg.getEMsgCommand();
         break;
      }
   case CMD_SETLIMITS:
      {
         nRet = Msg.getEMsgCommand();

         cout \
         << "Reseting " \
         << Msg.getEMsgFile() \
         << "'s operational parameters to: "
         << endl \
         << " Forced Write every " \
         << Msg.getEMsgTime() \
         << " seconds, or " \
         << Msg.getEMsgOpFlags() \
         << " messages, whichever is first." << endl;

         TimeLimit         = (INT)Msg.getEMsgTime();
         TransactionLimit  = Msg.getEMsgOpFlags();

         break;
      }
   case CMD_DEFAULT:
      {

         if((Msg.getEMsgOpFlags() & PLOG_OP_IMMEDIATE))
         {
            // Do an immediate write of the log.
            // cout << "Immediate Dump " << Msg.getEMsgOpFlags() << endl;
            LogDump();
         }
         break;
      }
   default:
      {
         break;
      }
   }

   return nRet;
}
