package com.cannontech.messaging.serialization.thrift.util;

import org.apache.thrift.TEnum;

import com.google.common.base.Converter;
import com.google.common.base.Enums;

public class ThriftEnumHelper<ThriftEnum extends Enum<ThriftEnum> & TEnum, MsgEnum extends Enum<MsgEnum>> {

    private Converter<String, ThriftEnum> thriftConverter;
    private Converter<String, MsgEnum> javaConverter;
    
    ThriftEnumHelper(Class<ThriftEnum> tClass, Class<MsgEnum> mClass) {
        thriftConverter = Enums.stringConverter(tClass);
        javaConverter = Enums.stringConverter(mClass);
    }
    
    public ThriftEnum toThrift(MsgEnum me) {
        return thriftConverter.convert(me.name());
    }
    
    public MsgEnum toJava(ThriftEnum te) {
        return javaConverter.convert(te.name());
    }
    
    public static <TE extends Enum<TE> & TEnum, ME extends Enum<ME>> ThriftEnumHelper<TE, ME> of(Class<TE> tClass, Class<ME> mClass) {
        return new ThriftEnumHelper<>(tClass, mClass);
    }
}
