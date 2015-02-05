#pragma once

#include "dlldefs.h"
#include "msg_multi.h"

class IM_EX_MSG CtiMultiWrapper
{
   CtiMultiMsg *_pMulti;

public:
   CtiMultiWrapper(CtiMultiMsg *pMsg = NULL);
   CtiMultiWrapper(const CtiMultiWrapper& aRef);

   virtual ~CtiMultiWrapper();
   CtiMultiWrapper& operator=(const CtiMultiWrapper& aRef);

   CtiMultiMsg* getMulti();
   CtiMultiMsg* getMulti() const;

   CtiMultiMsg* releaseMulti();
   void freeMulti();

   bool isNotNull() const;
   size_t entries() const;

};
