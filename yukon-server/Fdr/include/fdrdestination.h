#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrdestination.h
*
*    DATE: 05/24/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: CtiFDRDestination class header
*
*    DESCRIPTION: contains destination information for a point 
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRDESTINATION_H__
#define __FDRDESTINATION_H__

/** include files **/
#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
                        //         with ctitypes.h...  i only put this in here because
                        //         the compiler was having fits with BOOL.

#include <rw/cstring.h>
#include "dlldefs.h"
#include "fdr.h"


class IM_EX_FDRBASE CtiFDRDestination
{
    public:    
        CtiFDRDestination (RWCString &translation, RWCString &destination = RWCString());
        virtual ~CtiFDRDestination();
        CtiFDRDestination& operator=( const CtiFDRDestination &other );


        RWCString & getTranslation(void);
        RWCString  getTranslation(void) const;
        CtiFDRDestination& setTranslation (RWCString aTranslation);

        RWCString getTranslationValue(RWCString propertyName) const;

        RWCString & getDestination(void);
        RWCString  getDestination(void) const;
        CtiFDRDestination& setDestination (RWCString aDestination);

    private:
        // private data
        RWCString           iTranslation;
        RWCString           iDestination;

};

#endif  //  #ifndef __FDRDESTINATION_H__

