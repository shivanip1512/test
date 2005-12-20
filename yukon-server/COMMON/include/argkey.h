#ifndef  __CtiARGKEY_H__
#define  __CtiARGKEY_H__

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>
#include <string>

using std::string;

class CtiArgKey : public RWCollectable
{
public:
   CtiArgKey();
   CtiArgKey(char key);
   ~CtiArgKey() {}


   RWDECLARE_COLLECTABLE(CtiArgKey);

   // Inherited virtuals from RWCollectable
   RWspace     binaryStoreSize() const;
   int         compareTo(const RWCollectable*) const;
   RWBoolean   isEqual(const RWCollectable*) const;
   unsigned    hash() const;

   CtiArgKey& operator=(const CtiArgKey& key);

   void restoreGuts(RWvistream&);
   void saveGuts(RWvostream&) const;

   std::string&  getKey()    { return Key; }

private:
   std::string   Key;
};

#endif //#ifndef  __CtiARGKEY_H__

