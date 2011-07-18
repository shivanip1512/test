#include "precompiled.h"

#include "string_utility.h"

namespace Cti {

const char *filename_only(const char *filename_and_path)
{
    const char *filename_pos = filename_and_path;

    //  while we're not at the end of the string
    while( *filename_and_path )
    {
        //  if the current character is a backslash (Windows file separator)
        if( *(filename_and_path++) == '\\' )
        {
            //  and the following character isn't null
            if( *filename_and_path )
            {
                //  then that following character is the next possible position
                //    of the start of the filename
                filename_pos = filename_and_path;
            }
        }
    }

    return filename_pos;
}

}
