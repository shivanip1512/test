#ifndef  __CONFIGKEY_H__
#define  __CONFIGKEY_H__

#include <rw/collect.h>
#include <rw/cstring.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

class CtiConfigKey : public RWCollectable
{
private:
   RWCString   Key;

public:
   RWDECLARE_COLLECTABLE(CtiConfigKey);

   CtiConfigKey();
   CtiConfigKey(RWCString key);
   ~CtiConfigKey() {}

   // Inherited virtuals from RWCollectable
   RWspace     binaryStoreSize() const;
   int         compareTo(const RWCollectable*) const;
   RWBoolean   isEqual(const RWCollectable*) const;
   unsigned    hash() const;

   CtiConfigKey& operator=(const CtiConfigKey& key);

   void restoreGuts(RWFile&);
   void restoreGuts(RWvistream&);
   void saveGuts(RWFile&) const;
   void saveGuts(RWvostream&) const;

   RWCString   getKey() const     { return Key; }
   RWCString&  getKey()     { return Key; }
};

#endif //#ifndef  __CONFIGKEY_H__

