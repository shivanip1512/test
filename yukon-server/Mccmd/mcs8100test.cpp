#include "yukon.h"
#include "wpsc.h"

/*-----------------------------------------------------------------------------*
*
* File:   mcs8100test
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MCCMD/mcs8100test.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


void main(int argc, char* argv[])
{
    if( argc <= 1 )
    {
        cout << "Usage: wpcs filename" << endl;
        return;
    }

    RWCString file = argv[1];

    cout << file;

    RWOrdered result;

    if( DecodeCFDATAFile( file, &result ) == false)
    {
        cout << "decode failed" << endl;
        result.clearAndDestroy();
        return;
    }

    RWOrderedIterator iter(result);
    RWCollectableString* str;

    while( (str = (RWCollectableString*) iter()) != NULL )
    {
        cout << *str << endl;
        delete str;
    }
    cout << "done" << endl;


}
