#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CMDOPTS_H__
#define __CMDOPTS_H__

// #include <iostream.h>

// typedef RWCollectable Object;  // Smalltalk typedef
#include <rw/collect.h>
#include <rw/cstring.h>
#include <rw/hashdict.h>
#include <rw/btrdict.h>


#include "dlldefs.h"

class IM_EX_CMDLINE CtiCmdLineOpts
{
public:

   CtiCmdLineOpts();
   CtiCmdLineOpts(int argc, char **argv);

   void     setOpts(int argc, char **argv);        // If you use the default constructor.

   int      ReturnIntOpt    (char key);
   double   ReturnDoubleOpt (char key);
   BOOL     ReturnStringOpt (char key, char *opt, int len);

   BOOL     isOpt(char key);
   int      isError()       { return ErrState; }

   void     Puke();

private:
   INT               OptCount;
   INT               ErrState;
   RWHashDictionary  mHash;
};


#endif // #ifndef __CMDOPTS_H__

