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

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " ** Error sizing resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                                << "  (error code: " << errorCode << ")" << std::endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " ** Error locking resource: " << resourceId << ", "  << resourceType << " in: " << libraryName << std::endl;
                }
            }
            else
            {
                DWORD errorCode = GetLastError();

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ** Error loading resource: " << resourceId << ", "  << resourceType << " in: " << libraryName
                        << "  (error code: " << errorCode << ")" << std::endl;
            }
        }
        else
        {
            DWORD errorCode = GetLastError();

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ** Error finding resource: " << resourceId << ", " << resourceType << " in: " << libraryName
                    << "  (error code: " << errorCode << ")" << std::endl;
        }

        if ( ! FreeLibrary( library ) )
        {
            DWORD errorCode = GetLastError();

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ** Error unloading library: " << libraryName
                    << "  (error code: " << errorCode << ")" << std::endl;
        }
    }
    else
    {
        DWORD errorCode = GetLastError();

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ** Error loading library: " << libraryName
                << "  (error code: " << errorCode << ")" << std::endl;
    }

    return loadedResource;
}

}

