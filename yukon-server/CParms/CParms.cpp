#include <windows.h>
#include <string.h>
#include <stdio.h>
#include <iomanip>
using namespace std;

#include "cparms.h"
#include "configkey.h"
#include "configval.h"

CtiConfigParameters::CtiConfigParameters(RWCString strName) :
   FileName(strName),
   RefreshRate(900)
{
   if(!FileName.isNull())
   {
      RefreshConfigParameters();
   }
}

CtiConfigParameters::~CtiConfigParameters()
{
   mHash.clearAndDestroy();
}

CtiConfigParameters& CtiConfigParameters::setConfigFile(RWCString strName)
{
   FileName = strName;
   return *this;
}

int CtiConfigParameters::RefreshConfigParameters()
{

   int   i;
   char  key;
   char  valstr[80];

   char        Buffer[MAX_CONFIG_BUFFER];
   char        chKey[MAX_CONFIG_KEY];
   char        chValue[MAX_CONFIG_VALUE];

   char        *pch;

   RWCString   CurrentLine;

   FILE*       fp;

   RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);

   mHash.clearAndDestroy();

   if( FileName.isNull() )
   {
      return -1;
   }

   if((fp = fopen(FileName, "r")) != NULL)
   {
      LastRefresh = RWTime();

      while(fgets(Buffer, MAX_CONFIG_BUFFER, fp))
      {
         // cout << Buffer;

         for(i = 0; i < MAX_CONFIG_BUFFER; i++)
         {
            if(Buffer[i] != ' ')
            {
               if( Buffer[i] != '#' && Buffer[i] != ';' )
               {
                  if((pch = strtok(&Buffer[i], ":")) != NULL)
                  {
                     HeadAndTail(pch, chKey, MAX_CONFIG_KEY);
                     if(pch = strtok(NULL, "\0\n\r"))
                     {
                        HeadAndTail(pch, chValue, MAX_CONFIG_VALUE);

                        CtiConfigKey   *Key = new CtiConfigKey(RWCString(chKey));
                        CtiConfigValue *Val = new CtiConfigValue(RWCString(chValue));

                        if(!mHash.insertKeyAndValue(Key, Val))
                        {
                           cout << "CPARM " << chKey << " has already been inserted.. \n\tPlease check for duplicate entries in the master.cfg file " << endl;
                           cout << "\t" << chKey << " : " << getValueAsString(RWCString(chKey)) << endl;
                           delete Key;
                           delete Val;
                        }
                        #ifdef DEBUGLEVEL100
                        else
                        {
                           cout << "Key/Value = " <<
                              width(30) << Key->getKey() << " : " <<
                              width(48) << Val->getValue() << endl;
                        }
                        #endif
                     }
                  }
               }
               break;
            }
         }
      }

      fclose(fp);
   }

   if(isOpt(ConfKeyRefreshRate))
   {
      RefreshRate = atoi(getValueAsString(ConfKeyRefreshRate));
      // cout << "1. Expiring every " << RefreshRate << " seconds" << endl;
   }
   else
   {
      // cout << "2. Expiring every " << RefreshRate << " seconds" << endl;
   }


   return 0;
}

void
CtiConfigParameters::Dump()
{
   CtiConfigKey     *Key;
   CtiConfigValue   *Value;

   checkForRefresh();

   RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);

   if(mHash.entries() > 0)
   {
      RWHashDictionaryIterator iter(mHash);

      cout << endl << "Configuration Parameters:" << endl;
      while(iter())
      {
         Key = (CtiConfigKey*)iter.key();
         Value = (CtiConfigValue*)iter.value();

         cout << setiosflags(ios::left) << setw(30) << Key->getKey() << " : " << setw(40) << Value->getValue() << endl;

      }
   }
   else
   {
      cout << "No configuration exists." << endl;
   }
}

BOOL CtiConfigParameters::isOpt(RWCString key)
{
   CtiConfigKey     Key(key);
   CtiConfigValue   *Value;

   checkForRefresh();

   RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
   Value = (CtiConfigValue*)mHash.findValue(&Key);

   if(Value)
      return TRUE;
   else
      return FALSE;
}


RWCString
CtiConfigParameters::getValueAsString(RWCString key)
{
   BOOL           bRet = TRUE;
   CtiConfigKey   Key(key);
   CtiConfigValue *Value;

   RWCString      retStr = RWCString();      // A Null string.

   checkForRefresh();

   RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
   Value = (CtiConfigValue*)mHash.findValue(&Key);

   if(Value != NULL)
   {
      retStr = Value->getValue();
   }
   return retStr;
}

int CtiConfigParameters::getValueAsInt(RWCString key, int defaultval)
{
    int ret = defaultval;

    if(isOpt(key))
    {
        ret = atoi(getValueAsString(key));
    }

    return ret;
}

ULONG CtiConfigParameters::getValueAsULong(RWCString key, ULONG defaultval, int base)
{
    char *ch;
    ULONG ret = defaultval;

    if(isOpt(key))
    {
        ret = strtoul(getValueAsString(key).data(), &ch, base);
    }

    return ret;
}

double CtiConfigParameters::getValueAsDouble(RWCString key, double defaultval)
{
    double ret = defaultval;

    if(isOpt(key))
    {
        ret = atof(getValueAsString(key));
    }

    return ret;
}

#ifdef TESTOBJECT

void Usage(void)
{
   cout << "You must be daft" << endl;
}

int main(int argc, char **argv)
{

   if(argc > 0)
   {
      CtiConfigParameters Opts(argc, argv);

      Opts.Dump();

      if(Opts.isOpt('?'))
      {
         Usage();
         exit(0);
      }

      cout << "d is set to " << Opts.ReturnIntOpt('d') << endl;
      if(Opts.isError()) cout << "Error " << Opts.isError() << endl;
      cout << "e is set to " << Opts.ReturnDoubleOpt('e') << endl;
      if(Opts.isError()) cout << "Error " << Opts.isError() << endl;
   }
   else
   {
      Usage();
   }



   return 0;
}
#endif

void CtiConfigParameters::HeadAndTail(char *source, char *dest, size_t len)
{
   int      i, j;
   char     *pch;

   dest[0] = '\0';

   for(i = 0; i < len; i++)
   {
      if(source[i] != ' ')                // Leading Spaces are Gone Gone Gone...
      {
         if( source[i] != '#' && source[i] != ';' )
         {

            /*
             *  This loop copies all bytes up to len into the dest buffer
             *  It stops on the occurance of any of the chars listed in the if
             */
            for(j = 0; j < len - i; j++)
            {
               dest[j] = source[i+j];

               if(
                   dest[j] == '#'  ||
                   dest[j] == ';'  ||
                   dest[j] == '\0' ||
                   dest[j] == '\r' ||
                   dest[j] == '\n'
                 )
               {
                  dest[j] = '\0';
                  break;                  // inner for
               }
            }

            /*
             *  Starting at the end and cruising backwards, this loop searches for the first non-whitespace
             *  character in the string.
             */
            for(i = strlen(dest) - 1; i > 0; i--)
            {
               if(dest[i] == ' ' || dest[i] == '\t')
               {
                  dest[i] = '\0';
               }
               else
               {
                  break; // the for
               }

            }
         }
         break;
      }
   }
}

bool CtiConfigParameters::checkForRefresh()
{
   bool bRet = false;

   // cout << " Checking " << RWTime() - RefreshRate << " > " << LastRefresh << endl;
   RWTime now;


   if(now.seconds() - (ULONG)RefreshRate > LastRefresh.seconds())
   {
      // cout << " Reloading the CPARM data " << endl;
      RefreshConfigParameters();
      bRet = true;
   }

   return bRet;
}

