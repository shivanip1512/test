#include "yukon.h"

#include <windows.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "dlldefs.h"
#include "argkey.h"
#include "argval.h"
#include "cmdopts.h"

DLLEXPORT
CtiCmdLineOpts::CtiCmdLineOpts() :
   OptCount(0)
{ }

DLLEXPORT
CtiCmdLineOpts::CtiCmdLineOpts(int argc, char **argv)
{
   setOpts(argc, argv);
}

DLLEXPORT void
CtiCmdLineOpts::setOpts(int argc, char **argv)
{
   int   i;
   char  key;
   char  valstr[80];

   if(argc > 1)
   {
      OptCount = argc - 1;

      for(i = 1; i < argc; i++)
      {
         key = *(argv[i] + 1);

         if( *(argv[i] + 2) == ':' )
         {
            strcpy(valstr, argv[i] + 3);
         }
         else
         {
            strcpy(valstr, "1");             // A "Non-zero" Value!
         }

         CtiArgKey     *NewKey     = new CtiArgKey(key);
         CtiArgValue   *NewValue   = new CtiArgValue(valstr);

         if(!mHash.insertKeyAndValue(NewKey, NewValue))
         {
            cout << "Broken insert " << endl;
         }
      }
   }
   else
   {
      OptCount = 0;
   }

   return;
}

DLLEXPORT void
CtiCmdLineOpts::Puke()
{
   CtiArgKey     *Key;
   CtiArgValue   *Value;

   if(OptCount > 0)
   {
      RWHashDictionaryIterator iter(mHash);

      cout << "Command Line Arguments" << endl;
      while(iter())
      {
         Key = (CtiArgKey*)iter.key();
         Value = (CtiArgValue*)iter.value();

         cout << "\tKey: " << Key->getKey() << " = " << Value->getValue() << endl;
      }
   }
}

DLLEXPORT BOOL
CtiCmdLineOpts::isOpt(char key)
{
   CtiArgKey     Key(key);
   CtiArgValue   *Value;

   Value = (CtiArgValue*)mHash.findValue(&Key);

   if(Value)
      return TRUE;
   else
      return FALSE;
}

DLLEXPORT int
CtiCmdLineOpts::ReturnIntOpt(char key)
{
   int   iRet = 0;
   CtiArgKey     Key(key);
   CtiArgValue   *Value;

   ErrState = NoError;

   Value = (CtiArgValue*)mHash.findValue(&Key);

   if(Value)
   {
      if(!Value->isNumeric())
      {
         ErrState = NotNumeric;
      }

      if(Value)
      {
         Value->ReturnIntOpt(&iRet);
      }
   }

   return iRet;
}

DLLEXPORT double
CtiCmdLineOpts::ReturnDoubleOpt(char key)
{
   double         dRet = 0.0;
   CtiArgKey     Key(key);
   CtiArgValue   *Value;

   ErrState = NoError;

   Value = (CtiArgValue*)mHash.findValue(&Key);

   if(Value)
   {
      Value->ReturnDoubleOpt(&dRet);
   }

   return dRet;
}

DLLEXPORT BOOL
CtiCmdLineOpts::ReturnStringOpt(char key, char *opt, int len)
{
   BOOL           bRet = TRUE;
   CtiArgKey     Key(key);
   CtiArgValue   *Value;

   Value = (CtiArgValue*)mHash.findValue(&Key);

   if(Value)
   {
      if(!Value->isNumeric())
      {
         ErrState = NotNumeric;
      }

      if(Value)
      {
         Value->ReturnStringOpt(opt, len);
      }
   }
   else
   {
      bRet = FALSE;
   }

   return bRet;
}

#ifdef TESTCLASS
void Usage(void)
{
   cout << "You must be daft" << endl;
}


int main(int argc, char **argv)
{

   if(argc > 0)
   {
      CtiCmdLineOpts Opts(argc, argv);

      Opts.Puke();

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


