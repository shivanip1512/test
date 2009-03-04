/*---------------------------------------------------------------------------
        Filename:  ccstatscounters.h
        
        Programmer:  Julie Richter
                
        Description:    Header file for CCStatsObject
                        

        Initial Date:  2/24/2009
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#ifndef CCSTATSOBJECT_H
#define CCSTATSOBJECT_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <list>


#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"



class CCStatsObject
{
public:
    CCStatsObject();
    ~CCStatsObject();

    LONG getOpCount();
    void setOpCount(LONG val);
    LONG getFailCount();
    void setFailCount(LONG val);
    void incrementOpCount(LONG val);
    DOUBLE getTotal();
    void setTotal(DOUBLE val);
    void incrementTotal(DOUBLE val);

    DOUBLE getAverage();

    CCStatsObject& operator=(const CCStatsObject& right);


private:

    LONG _opCount;       
    DOUBLE _total;  
    LONG _failCount;  
};


#endif

