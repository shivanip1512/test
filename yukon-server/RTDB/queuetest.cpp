#include <windows.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   queuetest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/queuetest.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/11/15 14:08:22 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "que_omsg.h"
#include "queent.h"
#include "queue.h"

#include "rw\cstring.h"

void main(int argc, char **argv)
{
   int Tag = 100;
   RWCString Name("Pal");

   if(argc > 1)
   {
      Tag = atoi(argv[1]);
   }

   if(argc > 2)
   {
      Name = RWCString(argv[2]);
   }

   CtiQueue<CCTIOMsgQueue> Q(10);

   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi1"), 15, 10));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi2"), 14, 0));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi3"), 13, 0));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi4"), 15, 1));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi5"), 15, 4));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi6"), 10, 6));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi7"), 0, 7));
   Q.putQueue(CTIDBG_new CCTIOMsgQueue(RWCString("Hi8"), 1, 8));

   cout << "Just find a tagged entry" << endl;

   qPtr = Q.getByFunc(OMsgTagMatch, &Tag);
   if(qPtr)
   {
      cout << qPtr->getString() << " Priority " << qPtr->getPriority() << " Tag " <<  qPtr->getTag() << endl;
      delete qPtr;
   }

   cout << "Just found a tagged entry" << endl << endl;


   cout << "Just find a tagged entry" << endl;

   qPtr = Q.getByFunc(OMsgNameMatch, &Name);
   if(qPtr)
   {
      cout << qPtr->getString() << " Priority " << qPtr->getPriority() << " Tag " <<  qPtr->getTag() << endl;
      delete qPtr;
   }

   cout << "Just found a tagged entry" << endl << endl;

   while(Q.entries())
   {
      qPtr = Q.getQueue();

      cout << qPtr->getString() \
         << " Priority " << qPtr->getPriority() \
         << " Tag " <<  qPtr->getTag() \
         << " Time " << qptr->getTime().asString() << endl;

      delete qPtr;
   }


}
