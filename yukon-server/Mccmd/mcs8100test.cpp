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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/04/24 20:47:30 $
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

    string file = argv[1];

    cout << file;

    std::vector<RWCollectableString*> result;

    if( DecodeCFDATAFile( file, &result ) == false)
    {
        cout << "decode failed" << endl;
        delete_vector<result>;
        result.clear();
        return;
    }

    std::vector<RWCollectableString*>::iterator iter = result.begin();
    RWCollectableString* str;

    for( ; iter != result.end() ; ++iter )
    {
        str = *iter;
        cout << *str << endl;
        delete str;
    }
    cout << "done" << endl;


}
