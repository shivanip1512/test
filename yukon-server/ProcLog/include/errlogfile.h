#include "errmsg.h"
#include <rw/tpsrtvec.h>
#include <rw\thr\mutex.h>

class CErrLogFile
{
private:
   // RogueWave Collection Class
   // Collection of Pointers to Error Messages!
   RWMutexLock                                     ListMux;
   RWTPtrSortedVector<CErrMsg, greater<CErrMsg> >  ErrList;

   int         SelfImportance;

   RWCString   FileName;

   int         TimeLimit;                   // Max number of entries before a write is done
   int         TransactionLimit;                   // Max number of entries before a write is done
   time_t      NextWrite;                          // Max number of seconds before a write is done

public:

   // Constructors
   CErrLogFile();
   CErrLogFile(const char *);

   ~CErrLogFile();

   INT   Add(CErrMsg *Msg);
   INT   AddACopy(CErrMsg *OldMsg);
   INT   HandleErrMessage(CErrMsg &Msg);

   BOOL  DoLogDump();                           // Returns TRUE if NextWrite or TransactionLimit say to do the dump
   int   LogDump();                             // Does the output functinoality

   CErrLogFile& operator=(const CErrLogFile &e2);
   friend BOOL operator==(const CErrLogFile &e1, const CErrLogFile &e2);

   INT      setTimeLimit(INT n)           { return(TimeLimit = n);         }
   INT      setTransactionLimit(INT n)    { return(TransactionLimit = n);  }

   INT      getImportance()               { return(SelfImportance);        }
   VOID     BumpImportance()              { SelfImportance++;              }

};
