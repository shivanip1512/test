#include "precompiled.h"

#include "resource_helper.h"

#include "logger.h"

namespace Cti {

DataBuffer loadResourceFromLibrary( const ResourceIds resourceId, const char * resourceType, const char * libraryName )
{
    DataBuffer  loadedResource;

    if ( HINSTANCE library = LoadLibrary( libraryName ) )
    {
        if ( HRSRC resourceSearch = FindResource( library, MAKEINTRESOURCE( resourceId ), resourceType ) )
        {
            if ( HGLOBAL resource = LoadResource( library, resourceSearch ) )
            {
                if ( const unsigned char * data = static_cast<unsigned char *>( LockResource( resource ) ) )
                {
                    if ( DWORD size = SizeofResource( library, resourceSearch ) )
                    {
                        loadedResource.assign( data, data + size );
                    }
                    else
                    {
                        DWORD errorCode = GetLastError();

                        CTILOG_ERROR(dout, "Failed to size resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                                << "  (error code: " << errorCode << ")");
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Failed to lock resource: " << resourceId << ", "  << resourceType << " in: " << libraryName);
                }
            }
            else
            {
                DWORD errorCode = GetLastError();

                CTILOG_ERROR(dout, "Failed to load resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                        << "  (error code: " << errorCode << ")");
            }
        }
        else
        {
            DWORD errorCode = GetLastError();

            CTILOG_ERROR(dout, "Failed to find resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                    << "  (error code: " << errorCode << ")");

        }

        if ( ! FreeLibrary( library ) )
        {
            DWORD errorCode = GetLastError();

            CTILOG_ERROR(dout, "Failed to unload library: " << libraryName
                    << "  (error code: " << errorCode << ")");
        }
    }
    else
    {
        DWORD errorCode = GetLastError();

        CTILOG_ERROR(dout, "Failed to load library: " << libraryName
                << "  (error code: " << errorCode << ")");
    }

    return loadedResource;
}

}

