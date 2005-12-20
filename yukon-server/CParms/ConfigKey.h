#ifndef  __CONFIGKEY_H__
#define  __CONFIGKEY_H__

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>

#include <string>

using std::string;

class CtiConfigKey : public RWCollectable
{
private:
   string   Key;

public:
   RWDECLARE_COLLECTABLE(CtiConfigKey);

   CtiConfigKey();
   CtiConfigKey(const string& key);
   ~CtiConfigKey() {}

   // Inherited virtuals from RWCollectable
   /*
   RWspace     binaryStoreSize() const;
   */
   int         compareTo(const RWCollectable*) const;
   RWBoolean   isEqual(const RWCollectable*) const;
   unsigned    hash() const;
   
   CtiConfigKey& operator=(const CtiConfigKey& key);

   void restoreGuts(RWFile&);
   void restoreGuts(RWvistream&);
   void saveGuts(RWFile&) const;
   void saveGuts(RWvostream&) const;

   string   getKey() const { return Key; }
   string&   getKey()        { return Key; }
};

#endif //#ifndef  __CONFIGKEY_H__

