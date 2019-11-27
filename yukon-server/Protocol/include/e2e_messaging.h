#pragma once

#include <string>
#include <optional>
#include <vector>

namespace Cti::Protocols::Coap {

    class BlockSize
    {
    public:
        size_t getSize() const { return 16 << static_cast<uint8_t>(szx); }
        uint8_t getSzx() const { return szx; }

        static std::optional<BlockSize> ofSzx(uint8_t szx)
        {
            if( szx > 6 )
            {
                return std::nullopt;
            }
            return BlockSize(szx);
        }
        template<size_t size>
        static constexpr BlockSize ofSize()
        {
            static_assert(size >= 16, "size must be at least 16");

            constexpr auto szx = log2<size>() - log2<16>();

            static_assert(szx < 7, "szx must be in the range 0-6");
            
            return BlockSize(szx);
        }
    private:

        uint8_t szx;
        BlockSize(uint8_t szx_) : szx { szx_ } {}
    };

    struct Block
    {
        unsigned num;
        bool more;
        BlockSize blockSize;

        size_t start() const { return blockSize.getSize() * (num + 0); }
        size_t end() const   { return blockSize.getSize() * (num + 1); }
    };
}

namespace Cti::Protocols::E2e {

    using Bytes = std::vector<unsigned char>;

    struct EndpointMessage
    {
        bool nodeOriginated;

        unsigned short id;

        int code;  //  Either a ResponseCode or a RequestMethod.

        unsigned long token;

        std::string path;

        Bytes data;

        bool confirmable;

        std::optional<Coap::Block> block;
    };
}