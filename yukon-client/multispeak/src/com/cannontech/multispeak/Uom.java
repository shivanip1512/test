/**
 * Uom.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Uom implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Uom(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Wh";
    public static final java.lang.String _value2 = "W";
    public static final java.lang.String _value3 = "kWh";
    public static final java.lang.String _value4 = "kW";
    public static final java.lang.String _value5 = "MWh";
    public static final java.lang.String _value6 = "MW";
    public static final java.lang.String _value7 = "GWh";
    public static final java.lang.String _value8 = "GW";
    public static final java.lang.String _value9 = "mWh";
    public static final java.lang.String _value10 = "mW";
    public static final java.lang.String _value11 = "microWh";
    public static final java.lang.String _value12 = "microW";
    public static final java.lang.String _value13 = "VARh";
    public static final java.lang.String _value14 = "VAR";
    public static final java.lang.String _value15 = "kVARh";
    public static final java.lang.String _value16 = "kVAR";
    public static final java.lang.String _value17 = "MVARh";
    public static final java.lang.String _value18 = "MVAR";
    public static final java.lang.String _value19 = "GVARh";
    public static final java.lang.String _value20 = "GVAR";
    public static final java.lang.String _value21 = "mVARh";
    public static final java.lang.String _value22 = "mVAR";
    public static final java.lang.String _value23 = "microVARh";
    public static final java.lang.String _value24 = "microVAR";
    public static final java.lang.String _value25 = "VAh";
    public static final java.lang.String _value26 = "VA";
    public static final java.lang.String _value27 = "kVAh";
    public static final java.lang.String _value28 = "kVA";
    public static final java.lang.String _value29 = "MVAh";
    public static final java.lang.String _value30 = "MVA";
    public static final java.lang.String _value31 = "GVAh";
    public static final java.lang.String _value32 = "GVA";
    public static final java.lang.String _value33 = "mVAh";
    public static final java.lang.String _value34 = "mVA";
    public static final java.lang.String _value35 = "microVAh";
    public static final java.lang.String _value36 = "microVA";
    public static final java.lang.String _value37 = "Qh";
    public static final java.lang.String _value38 = "Q";
    public static final java.lang.String _value39 = "kQh";
    public static final java.lang.String _value40 = "kQ";
    public static final java.lang.String _value41 = "MQh";
    public static final java.lang.String _value42 = "MQ";
    public static final java.lang.String _value43 = "GQh";
    public static final java.lang.String _value44 = "GQ";
    public static final java.lang.String _value45 = "mQh";
    public static final java.lang.String _value46 = "mQ";
    public static final java.lang.String _value47 = "microQh";
    public static final java.lang.String _value48 = "microQ";
    public static final java.lang.String _value49 = "V RMS";
    public static final java.lang.String _value50 = "kV RMS";
    public static final java.lang.String _value51 = "MV RMS";
    public static final java.lang.String _value52 = "mV RMS";
    public static final java.lang.String _value53 = "microV RMS";
    public static final java.lang.String _value54 = "V";
    public static final java.lang.String _value55 = "kV";
    public static final java.lang.String _value56 = "MV";
    public static final java.lang.String _value57 = "mV";
    public static final java.lang.String _value58 = "microV";
    public static final java.lang.String _value59 = "Vsquared RMS";
    public static final java.lang.String _value60 = "kVsquared RMS";
    public static final java.lang.String _value61 = "MVsquared RMS";
    public static final java.lang.String _value62 = "mVsquared RMS";
    public static final java.lang.String _value63 = "microVsquared RMS";
    public static final java.lang.String _value64 = "Amps RMS";
    public static final java.lang.String _value65 = "kAmps RMS";
    public static final java.lang.String _value66 = "Mamps RMS";
    public static final java.lang.String _value67 = "mAmps RMS";
    public static final java.lang.String _value68 = "microAmps RMS";
    public static final java.lang.String _value69 = "Amps";
    public static final java.lang.String _value70 = "kAmps";
    public static final java.lang.String _value71 = "Mamps";
    public static final java.lang.String _value72 = "mAmps";
    public static final java.lang.String _value73 = "microAmps";
    public static final java.lang.String _value74 = "Amps squared RMS";
    public static final java.lang.String _value75 = "kAmps squared RMS";
    public static final java.lang.String _value76 = "Mamps squared RMS";
    public static final java.lang.String _value77 = "mAmps squared RMS";
    public static final java.lang.String _value78 = "microAmps squared RMS";
    public static final java.lang.String _value79 = "T.H.D. Voltage";
    public static final java.lang.String _value80 = "T.H.D. Current";
    public static final java.lang.String _value81 = "Voltage phase angle";
    public static final java.lang.String _value82 = "Current phase angle";
    public static final java.lang.String _value83 = "Power factor";
    public static final java.lang.String _value84 = "Time";
    public static final java.lang.String _value85 = "Date";
    public static final java.lang.String _value86 = "Date time";
    public static final java.lang.String _value87 = "Interval timer";
    public static final java.lang.String _value88 = "Frequency";
    public static final java.lang.String _value89 = "Counter";
    public static final java.lang.String _value90 = "Sense input";
    public static final java.lang.String _value91 = "Nbr of pulse";
    public static final java.lang.String _value92 = "Nbr of sag";
    public static final java.lang.String _value93 = "Nbr of swells";
    public static final java.lang.String _value94 = "Nbr of power outage";
    public static final java.lang.String _value95 = "Nbr of excursion low";
    public static final java.lang.String _value96 = "Nbr of excursion high";
    public static final java.lang.String _value97 = "Normal voltage period";
    public static final java.lang.String _value98 = "Nbr of voltage unbalance";
    public static final java.lang.String _value99 = "Nbr of V T.H.D. excess";
    public static final java.lang.String _value100 = "Nbr of amps T.H.D. excess";
    public static final java.lang.String _value101 = "Nbr of demand resets";
    public static final java.lang.String _value102 = "Nbr of times programmed";
    public static final java.lang.String _value103 = "Minutes on battery";
    public static final java.lang.String _value104 = "Nbr of inversion";
    public static final java.lang.String _value105 = "Nbr of removal";
    public static final java.lang.String _value106 = "Nbr of reprogramming";
    public static final java.lang.String _value107 = "Nbr of power loss";
    public static final java.lang.String _value108 = "Nbr of reverse rotation";
    public static final java.lang.String _value109 = "Nbr of physical tamper";
    public static final java.lang.String _value110 = "Nbr of encoder tamper";
    public static final java.lang.String _value111 = "Nbr of watchdog";
    public static final java.lang.String _value112 = "cubic meters uncorrected";
    public static final java.lang.String _value113 = "cubic meters per Hr uncorrected";
    public static final java.lang.String _value114 = "cubic meters corrected";
    public static final java.lang.String _value115 = "cubic meters per Hr corrected";
    public static final java.lang.String _value116 = "cubic feet uncorrected";
    public static final java.lang.String _value117 = "cubic feet per Hr uncorrected";
    public static final java.lang.String _value118 = "cubic feet corrected";
    public static final java.lang.String _value119 = "cubic feet per Hr corrected";
    public static final java.lang.String _value120 = "deg C";
    public static final java.lang.String _value121 = "deg F";
    public static final java.lang.String _value122 = "deg K";
    public static final java.lang.String _value123 = "Joules";
    public static final java.lang.String _value124 = "Joules per Hr";
    public static final java.lang.String _value125 = "kJoules";
    public static final java.lang.String _value126 = "kJoules per Hr";
    public static final java.lang.String _value127 = "MJoules";
    public static final java.lang.String _value128 = "MJoules per Hr";
    public static final java.lang.String _value129 = "mJoules";
    public static final java.lang.String _value130 = "mJoules per Hr";
    public static final java.lang.String _value131 = "microJoules";
    public static final java.lang.String _value132 = "microJoules per Hr";
    public static final java.lang.String _value133 = "Therm";
    public static final java.lang.String _value134 = "Therm per Hr";
    public static final java.lang.String _value135 = "Static pascal";
    public static final java.lang.String _value136 = "Differential pascal";
    public static final java.lang.String _value137 = "Static pound per square inch";
    public static final java.lang.String _value138 = "Differential pound per square inch";
    public static final java.lang.String _value139 = "Gram square cm";
    public static final java.lang.String _value140 = "Meter HG column";
    public static final java.lang.String _value141 = "Inches HG column";
    public static final java.lang.String _value142 = "Inches H2O column";
    public static final java.lang.String _value143 = "Bar";
    public static final java.lang.String _value144 = "percent relative humidity";
    public static final java.lang.String _value145 = "PPM odorant";
    public static final java.lang.String _value146 = "cubic meter";
    public static final java.lang.String _value147 = "cubic meter per Hr";
    public static final java.lang.String _value148 = "cubic feet";
    public static final java.lang.String _value149 = "cubic feet per Hr";
    public static final java.lang.String _value150 = "ccf";
    public static final java.lang.String _value151 = "ccf per Hr";
    public static final java.lang.String _value152 = "US gl";
    public static final java.lang.String _value153 = "US gl per Hr";
    public static final java.lang.String _value154 = "IMP gl";
    public static final java.lang.String _value155 = "IMP gl per Hr";
    public static final java.lang.String _value156 = "Acre ft";
    public static final java.lang.String _value157 = "PPM lead";
    public static final java.lang.String _value158 = "turbidity";
    public static final java.lang.String _value159 = "PPM chlorine";
    public static final java.lang.String _value160 = "PH factor";
    public static final java.lang.String _value161 = "Corrosion";
    public static final java.lang.String _value162 = "Ionization";
    public static final java.lang.String _value163 = "PPM SO2";
    public static final java.lang.String _value164 = "liters";
    public static final java.lang.String _value165 = "pounds per square foot";
    public static final java.lang.String _value166 = "inches of water";
    public static final java.lang.String _value167 = "ft of water";
    public static final java.lang.String _value168 = "atmospheres";
    public static final java.lang.String _value169 = "Dollar";
    public static final java.lang.String _value170 = "inches";
    public static final java.lang.String _value171 = "ft";
    public static final java.lang.String _value172 = "m";
    public static final java.lang.String _value173 = "dm";
    public static final java.lang.String _value174 = "km";
    public static final java.lang.String _value175 = "cm";
    public static final java.lang.String _value176 = "mm";
    public static final java.lang.String _value177 = "micro m";
    public static final Uom value1 = new Uom(_value1);
    public static final Uom value2 = new Uom(_value2);
    public static final Uom value3 = new Uom(_value3);
    public static final Uom value4 = new Uom(_value4);
    public static final Uom value5 = new Uom(_value5);
    public static final Uom value6 = new Uom(_value6);
    public static final Uom value7 = new Uom(_value7);
    public static final Uom value8 = new Uom(_value8);
    public static final Uom value9 = new Uom(_value9);
    public static final Uom value10 = new Uom(_value10);
    public static final Uom value11 = new Uom(_value11);
    public static final Uom value12 = new Uom(_value12);
    public static final Uom value13 = new Uom(_value13);
    public static final Uom value14 = new Uom(_value14);
    public static final Uom value15 = new Uom(_value15);
    public static final Uom value16 = new Uom(_value16);
    public static final Uom value17 = new Uom(_value17);
    public static final Uom value18 = new Uom(_value18);
    public static final Uom value19 = new Uom(_value19);
    public static final Uom value20 = new Uom(_value20);
    public static final Uom value21 = new Uom(_value21);
    public static final Uom value22 = new Uom(_value22);
    public static final Uom value23 = new Uom(_value23);
    public static final Uom value24 = new Uom(_value24);
    public static final Uom value25 = new Uom(_value25);
    public static final Uom value26 = new Uom(_value26);
    public static final Uom value27 = new Uom(_value27);
    public static final Uom value28 = new Uom(_value28);
    public static final Uom value29 = new Uom(_value29);
    public static final Uom value30 = new Uom(_value30);
    public static final Uom value31 = new Uom(_value31);
    public static final Uom value32 = new Uom(_value32);
    public static final Uom value33 = new Uom(_value33);
    public static final Uom value34 = new Uom(_value34);
    public static final Uom value35 = new Uom(_value35);
    public static final Uom value36 = new Uom(_value36);
    public static final Uom value37 = new Uom(_value37);
    public static final Uom value38 = new Uom(_value38);
    public static final Uom value39 = new Uom(_value39);
    public static final Uom value40 = new Uom(_value40);
    public static final Uom value41 = new Uom(_value41);
    public static final Uom value42 = new Uom(_value42);
    public static final Uom value43 = new Uom(_value43);
    public static final Uom value44 = new Uom(_value44);
    public static final Uom value45 = new Uom(_value45);
    public static final Uom value46 = new Uom(_value46);
    public static final Uom value47 = new Uom(_value47);
    public static final Uom value48 = new Uom(_value48);
    public static final Uom value49 = new Uom(_value49);
    public static final Uom value50 = new Uom(_value50);
    public static final Uom value51 = new Uom(_value51);
    public static final Uom value52 = new Uom(_value52);
    public static final Uom value53 = new Uom(_value53);
    public static final Uom value54 = new Uom(_value54);
    public static final Uom value55 = new Uom(_value55);
    public static final Uom value56 = new Uom(_value56);
    public static final Uom value57 = new Uom(_value57);
    public static final Uom value58 = new Uom(_value58);
    public static final Uom value59 = new Uom(_value59);
    public static final Uom value60 = new Uom(_value60);
    public static final Uom value61 = new Uom(_value61);
    public static final Uom value62 = new Uom(_value62);
    public static final Uom value63 = new Uom(_value63);
    public static final Uom value64 = new Uom(_value64);
    public static final Uom value65 = new Uom(_value65);
    public static final Uom value66 = new Uom(_value66);
    public static final Uom value67 = new Uom(_value67);
    public static final Uom value68 = new Uom(_value68);
    public static final Uom value69 = new Uom(_value69);
    public static final Uom value70 = new Uom(_value70);
    public static final Uom value71 = new Uom(_value71);
    public static final Uom value72 = new Uom(_value72);
    public static final Uom value73 = new Uom(_value73);
    public static final Uom value74 = new Uom(_value74);
    public static final Uom value75 = new Uom(_value75);
    public static final Uom value76 = new Uom(_value76);
    public static final Uom value77 = new Uom(_value77);
    public static final Uom value78 = new Uom(_value78);
    public static final Uom value79 = new Uom(_value79);
    public static final Uom value80 = new Uom(_value80);
    public static final Uom value81 = new Uom(_value81);
    public static final Uom value82 = new Uom(_value82);
    public static final Uom value83 = new Uom(_value83);
    public static final Uom value84 = new Uom(_value84);
    public static final Uom value85 = new Uom(_value85);
    public static final Uom value86 = new Uom(_value86);
    public static final Uom value87 = new Uom(_value87);
    public static final Uom value88 = new Uom(_value88);
    public static final Uom value89 = new Uom(_value89);
    public static final Uom value90 = new Uom(_value90);
    public static final Uom value91 = new Uom(_value91);
    public static final Uom value92 = new Uom(_value92);
    public static final Uom value93 = new Uom(_value93);
    public static final Uom value94 = new Uom(_value94);
    public static final Uom value95 = new Uom(_value95);
    public static final Uom value96 = new Uom(_value96);
    public static final Uom value97 = new Uom(_value97);
    public static final Uom value98 = new Uom(_value98);
    public static final Uom value99 = new Uom(_value99);
    public static final Uom value100 = new Uom(_value100);
    public static final Uom value101 = new Uom(_value101);
    public static final Uom value102 = new Uom(_value102);
    public static final Uom value103 = new Uom(_value103);
    public static final Uom value104 = new Uom(_value104);
    public static final Uom value105 = new Uom(_value105);
    public static final Uom value106 = new Uom(_value106);
    public static final Uom value107 = new Uom(_value107);
    public static final Uom value108 = new Uom(_value108);
    public static final Uom value109 = new Uom(_value109);
    public static final Uom value110 = new Uom(_value110);
    public static final Uom value111 = new Uom(_value111);
    public static final Uom value112 = new Uom(_value112);
    public static final Uom value113 = new Uom(_value113);
    public static final Uom value114 = new Uom(_value114);
    public static final Uom value115 = new Uom(_value115);
    public static final Uom value116 = new Uom(_value116);
    public static final Uom value117 = new Uom(_value117);
    public static final Uom value118 = new Uom(_value118);
    public static final Uom value119 = new Uom(_value119);
    public static final Uom value120 = new Uom(_value120);
    public static final Uom value121 = new Uom(_value121);
    public static final Uom value122 = new Uom(_value122);
    public static final Uom value123 = new Uom(_value123);
    public static final Uom value124 = new Uom(_value124);
    public static final Uom value125 = new Uom(_value125);
    public static final Uom value126 = new Uom(_value126);
    public static final Uom value127 = new Uom(_value127);
    public static final Uom value128 = new Uom(_value128);
    public static final Uom value129 = new Uom(_value129);
    public static final Uom value130 = new Uom(_value130);
    public static final Uom value131 = new Uom(_value131);
    public static final Uom value132 = new Uom(_value132);
    public static final Uom value133 = new Uom(_value133);
    public static final Uom value134 = new Uom(_value134);
    public static final Uom value135 = new Uom(_value135);
    public static final Uom value136 = new Uom(_value136);
    public static final Uom value137 = new Uom(_value137);
    public static final Uom value138 = new Uom(_value138);
    public static final Uom value139 = new Uom(_value139);
    public static final Uom value140 = new Uom(_value140);
    public static final Uom value141 = new Uom(_value141);
    public static final Uom value142 = new Uom(_value142);
    public static final Uom value143 = new Uom(_value143);
    public static final Uom value144 = new Uom(_value144);
    public static final Uom value145 = new Uom(_value145);
    public static final Uom value146 = new Uom(_value146);
    public static final Uom value147 = new Uom(_value147);
    public static final Uom value148 = new Uom(_value148);
    public static final Uom value149 = new Uom(_value149);
    public static final Uom value150 = new Uom(_value150);
    public static final Uom value151 = new Uom(_value151);
    public static final Uom value152 = new Uom(_value152);
    public static final Uom value153 = new Uom(_value153);
    public static final Uom value154 = new Uom(_value154);
    public static final Uom value155 = new Uom(_value155);
    public static final Uom value156 = new Uom(_value156);
    public static final Uom value157 = new Uom(_value157);
    public static final Uom value158 = new Uom(_value158);
    public static final Uom value159 = new Uom(_value159);
    public static final Uom value160 = new Uom(_value160);
    public static final Uom value161 = new Uom(_value161);
    public static final Uom value162 = new Uom(_value162);
    public static final Uom value163 = new Uom(_value163);
    public static final Uom value164 = new Uom(_value164);
    public static final Uom value165 = new Uom(_value165);
    public static final Uom value166 = new Uom(_value166);
    public static final Uom value167 = new Uom(_value167);
    public static final Uom value168 = new Uom(_value168);
    public static final Uom value169 = new Uom(_value169);
    public static final Uom value170 = new Uom(_value170);
    public static final Uom value171 = new Uom(_value171);
    public static final Uom value172 = new Uom(_value172);
    public static final Uom value173 = new Uom(_value173);
    public static final Uom value174 = new Uom(_value174);
    public static final Uom value175 = new Uom(_value175);
    public static final Uom value176 = new Uom(_value176);
    public static final Uom value177 = new Uom(_value177);
    public java.lang.String getValue() { return _value_;}
    public static Uom fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Uom enumeration = (Uom)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Uom fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Uom.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
