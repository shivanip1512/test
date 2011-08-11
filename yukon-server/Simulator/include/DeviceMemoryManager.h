#pragma once

#include "types.h"

namespace Cti {
namespace Simulator {

class DeviceMemoryManager {

public:

    static const std::string memoryMapDirectory;

    DeviceMemoryManager(int address, int mapSize);

    unsigned getValueFromMemoryMapLocation(unsigned pos, unsigned length);
    unsigned getValueFromMemoryMapLocation(unsigned pos);
    bytes    getValueVectorFromMemoryMap(unsigned pos, unsigned length);

    void writeValueToMemoryMap(unsigned pos, const unsigned char &value);
    void writeDataToMemoryMap(unsigned pos, const bytes &data);

    bool isInitialized();

private:

    void serializeMemoryMapToFile();
    void initializeMemoryMapFromFile();

    bool _initialized;

    bytes _memory_map;

    int _address;
    int _mapSize;

};

}
}
