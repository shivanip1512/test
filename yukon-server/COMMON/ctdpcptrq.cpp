/*-----------------------------------------------------------------------------
    Filename:  ctdpcptrq.cpp
                    
    Programmer:  Aaron Lauinger
    
    Description: Source file for CtiCountedPDCQueue.
                 Combines the functionality of RWPCPtrQueue and 
                 RWCountingBody to make a queue that can be reference counted
                 and is thread-safe.
        
    Initial Date:  5/12/99
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "ctdpcptrq.h"
