#include "wpsc.h"
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mcs8100test
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MCCMD/mcs8100test.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:08 $
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
