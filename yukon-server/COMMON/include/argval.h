#ifndef  __CtiARGVAL_H__
#define  __CtiARGVAL_H__

#include <rw/collect.h>
#include <rw/cstring.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

class CtiArgValue : public RWCollectable
{
public:
   CtiArgValue();
   CtiArgValue(char* val);
   ~CtiArgValue() {}


   RWDECLARE_COLLECTABLE(CtiArgValue);

   // Inherited virtuals from RWCollectable
   RWspace     binaryStoreSize() const;
   int         compareTo(const RWCollectable*) const;
   RWBoolean   isEqual(const RWCollectable*) const;
   unsigned    hash() const;

   CtiArgValue& operator=(const CtiArgValue& key);

   void restoreGuts(RWFile&);
   void saveGuts(RWFile&) const;
   void saveGuts(RWvostream&) const;
   void restoreGuts(RWvistream&);

   int ReturnIntOpt    (int *opt);
   int ReturnDoubleOpt (double *opt);
   int ReturnStringOpt (char *opt, int len);

   RWCString&  getValue()    { return Value; }
   int         isNumeric();


private:
   RWCString Value;
};


#endif // #ifndef  __CtiARGVAL_H__

