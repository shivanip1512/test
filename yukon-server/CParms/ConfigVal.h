#ifndef  __CONFIGVAL_H__
#define  __CONFIGVAL_H__

#include <rw/collect.h>
#include <rw/cstring.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

class CtiConfigValue : public RWCollectable
{
private:
   RWCString Value;
public:
   RWDECLARE_COLLECTABLE(CtiConfigValue);

   CtiConfigValue();
   CtiConfigValue(RWCString val);
   ~CtiConfigValue() {}



   // Inherited virtuals from RWCollectable
   RWspace     binaryStoreSize() const;
   int         compareTo(const RWCollectable*) const;
   RWBoolean   isEqual(const RWCollectable*) const;
   unsigned    hash() const;

   CtiConfigValue& operator=(const CtiConfigValue& key);

   void restoreGuts(RWFile&);
   void saveGuts(RWFile&) const;
   void saveGuts(RWvostream&) const;
   void restoreGuts(RWvistream&);

   int ReturnStringOpt (char *opt, int len);

   RWCString&  getValue()    { return Value; }

};


#endif // #ifndef  __CONFIGVAL_H__

