#pragma once

#include <proton/message.hpp>


namespace Cti::Messaging::Proton
{

class EncoderProxy 
{
private:

    proton::codec::encoder enc;
    proton::value v;

    proton::message & mess;

public:

    EncoderProxy( proton::message & mess_)
        :   v{},
            enc(v),
            mess(mess_)
    {
        enc << proton::codec::start::list();
    }

    ~EncoderProxy()
    {
        enc << proton::codec::finish();
        mess.body(v);
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

    void writeFloat(float f)
    {
        enc << f;
    }

    void writeShort(int16_t s)
    {
        enc << s;
    }
    
    void writeBoolean(bool b)
    {
        enc << b;
    }

    void writeString(const std::string & s)
    {
        enc << s;
    }

    void writeBytes(const std::vector<unsigned char>& vb)
    {
        enc << proton::binary{ std::cbegin(vb), std::cend(vb) };
    }
};

}
