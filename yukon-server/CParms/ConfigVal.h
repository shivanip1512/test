#ifndef  __CONFIGVAL_H__
#define  __CONFIGVAL_H__

#include <rw/collect.h>

#include <rw/pstream.h>
#include <rw/rstream.h>
#include <string>

using namespace std;

class CtiConfigValue : public RWCollectable
{
private:
   string Value;
public:
   RWDECLARE_COLLECTABLE(CtiConfigValue);

   CtiConfigValue();
   CtiConfigValue(const string& val);
   ~CtiConfigValue() {}



   // Inherited virtuals from RWCollectable
   /*
   RWspace     binaryStoreSize() const;

   virtual int         compareTo(const RWCollectable*) const;
   virtual RWBoolean   isEqual(const RWCollectable*) const;
   virtual unsigned    hash() const;
   */      

   CtiConfigValue& operator=(const CtiConfigValue& key);

   void restoreGuts(RWFile&);
   void saveGuts(RWFile&) const;
   void saveGuts(RWvostream&) const;
   void restoreGuts(RWvistream&);

   int ReturnStringOpt (char *opt, int len);

   const string&  getValue()    { return Value; }

};


#endif // #ifndef  __CONFIGVAL_H__

