#pragma once

#include <string>
#include <optional>
#include <vector>

namespace Cti::Protocols::E2e {

    using Bytes = std::vector<unsigned char>;

    struct BlockSize
    {
        unsigned szx;

        size_t getSize() const { return 16 << szx; }
    };

    struct Block
    {
        unsigned num;
        bool more;
        BlockSize size;

        size_t start() const { return size.getSize() * (num + 0); }
        size_t end() const   { return size.getSize() * (num + 1); }
    };

    struct EndpointMessage
    {
        bool nodeOriginated;

        unsigned short id;

        int code;  //  Either a ResponseCode or a RequestMethod.

        unsigned long token;

        std::string path;

        Bytes data;

        bool confirmable;

        std::optional<Block> block;
    };
}
