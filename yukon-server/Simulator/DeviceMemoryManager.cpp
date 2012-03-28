#include "precompiled.h"

#include "DeviceMemoryManager.h"
#include "numstr.h"

#include <boost/filesystem.hpp>

using namespace std;
using namespace boost;

namespace Cti {
namespace Simulator {

const string DeviceMemoryManager::memoryMapDirectory = "SimulatorMemoryMaps";

DeviceMemoryManager::DeviceMemoryManager(int address, int mapSize) :
    _address(address),
    _mapSize(mapSize),
    _initialized(false)
{
    initializeMemoryMapFromFile();
}

unsigned DeviceMemoryManager::getValueFromMemoryMapLocation(unsigned pos)
{
    return getValueFromMemoryMapLocation(pos, 1);
}

unsigned DeviceMemoryManager::getValueFromMemoryMapLocation(unsigned pos, unsigned length)
{
    unsigned retval = 0;

    for( int i = 0; i < length; i++ )
    {
        retval <<= 8;
        retval |= _memory_map[pos + i];
    }

    return retval;
}

bytes DeviceMemoryManager::getValueVectorFromMemoryMap(unsigned pos, unsigned length)
{
    if( pos + length > _memory_map.size() )
    {
        return bytes(length, 0x00);
    }
    else
    {
        bytes data = bytes(length, '\0');

        copy(_memory_map.begin() + pos,
             _memory_map.begin() + pos + length,
             data.begin());
    
        return data;
    }
}

void DeviceMemoryManager::writeValueToMemoryMap(unsigned pos, const unsigned char &value)
{
    bytes data(1, value);

    writeDataToMemoryMap(pos, data);
}

void DeviceMemoryManager::writeDataToMemoryMap(unsigned pos, const bytes &data)
{
    if( pos + data.size() <= _memory_map.size() )
    {
        copy(data.begin(), data.begin() + data.size(), _memory_map.begin() + pos);

        // Write the data out to the file!
        serializeMemoryMapToFile();
    }
}

bool DeviceMemoryManager::isInitialized()
{
    return _initialized;
}

void DeviceMemoryManager::serializeMemoryMapToFile()
{
    string filename = memoryMapDirectory + "/" + CtiNumStr(_address) + ".bin";
    ofstream mapFile( filename.c_str(), ios::out | ios::binary );

    mapFile.write( (char*)&_memory_map.front(), _memory_map.size() ); 

    mapFile.close();
}

void DeviceMemoryManager::initializeMemoryMapFromFile()
{
    //filesystem::path dir_path( directoryName + "/" + CtiNumStr(_address) + ".bin" );
    string dir_path( memoryMapDirectory + "/" + CtiNumStr(_address) + ".bin" );

    wstring wide_dir_path(dir_path.begin(), dir_path.end());

    if( filesystem::exists(wide_dir_path) )
    {
        _memory_map.clear();

        ifstream mapFile( dir_path.c_str(), ios::in | ios::binary );
    
        // The number of bytes in the file.
        mapFile.seekg(0, ios::end);
        int length = mapFile.tellg();
        mapFile.seekg(0, ios::beg);
    
        unsigned char byte;
    
        for( int i = 0; i < length; i++ )
        {
            byte = mapFile.get();
            _memory_map.push_back( byte );
        }
    
        mapFile.close();

        _initialized = true;
    }
    else
    {
        // File wasn't present, preload with the correct amount of 0s.
        _memory_map = bytes(_mapSize, 0x00);
    }
}


}
}
