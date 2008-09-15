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
#include <string>
#include "dlldefs.h"
#include "fdr.h"

using std::string;

class CtiFDRPoint;

class IM_EX_FDRBASE CtiFDRDestination
{
    public:    
        CtiFDRDestination () {}; // this is only defined so this class can be used in an std::map
        CtiFDRDestination (CtiFDRPoint* parentPoint, string &translation, string &destination = string());
        virtual ~CtiFDRDestination();
        CtiFDRDestination& operator=( const CtiFDRDestination &other );


        string & getTranslation(void);
        string  getTranslation(void) const;
        CtiFDRDestination& setTranslation (string aTranslation);

        string getTranslationValue(string propertyName) const;

        string & getDestination(void);
        string  getDestination(void) const;
        CtiFDRDestination& setDestination (string aDestination);

        CtiFDRPoint* getParentPoint(void) const;
        CtiFDRDestination& setParentPoint (CtiFDRPoint* parentPoint);
        
        bool operator<(const CtiFDRDestination& other) const;
        bool operator==(const CtiFDRDestination& other) const;

    private:
        // private data
        string           iTranslation;
        string           iDestination;
        CtiFDRPoint*     iParentPoint;

};

IM_EX_FDRBASE std::ostream& operator<< (std::ostream& os, const CtiFDRDestination& dest);

#endif  //  #ifndef __FDRDESTINATION_H__

