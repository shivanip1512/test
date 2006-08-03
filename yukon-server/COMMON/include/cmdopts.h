#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CMDOPTS_H__
#define __CMDOPTS_H__

// #include <iostream.h>

// typedef RWCollectable Object;  // Smalltalk typedef
//#include <rw/collect.h>
//#include <rw/hashdict.h>
//#include <rw/btrdict.h>

#include "argkey.h"
#include "argval.h"

#include "dlldefs.h"
typedef std::map<CtiArgKey*,CtiArgValue*>::iterator mHash_itr;
typedef std::pair<std::map<CtiArgKey*,CtiArgValue*>::iterator,bool> mHash_pair;

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
   map<CtiArgKey*,CtiArgValue*>  mHash;
};


#endif // #ifndef __CMDOPTS_H__

