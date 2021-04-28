#pragma once

#include <proton/codec/encoder.hpp>

namespace Cti::Messaging::Proton {

class EncoderProxy 
{
private:
    proton::codec::encoder& enc;
public:
    EncoderProxy(proton::codec::encoder& enc_)
        :   enc(enc_)
    {
        enc << proton::codec::start::list();
    }
    ~EncoderProxy()
    {
        enc << proton::codec::finish();
    }

    void writeByte(uint8_t b)
    {
        enc << b;
    }
    void writeInt(int32_t i)
    {
        enc << i;
    }
    void writeLong(int64_t l)
    {
        enc << l;
    }
};

}