package com.cannontech.yukon.api.amr.endpoint.helper;

import java.util.Arrays;

import org.w3c.dom.Node;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.database.data.point.PointType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class PointSelector {
    public enum Type {
        ATTRIBUTE("attribute"),
        POINT_NAME("pointName"),
        TYPE_AND_OFFSET("typeAndOffset"),
        DEFAULT_POINT_NAME("defaultPointName");

        private final static ImmutableMap<String, Type> byElementName;
        static {
            Function<Type, String> keyFunction =
                new Function<Type, String>() {
                    @Override
                    public String apply(Type from) {
                        return from.elementName;
                    }
                };
            byElementName = Maps.uniqueIndex(Arrays.asList(values()), keyFunction);
        }

        private final String elementName;

        private Type(String elementName) {
            this.elementName = elementName;
        }

        public static Type getByElementName(String elementName) {
            return byElementName.get(elementName);
        }
    }

    private Type type;
    private String name;
    private PointType pointType;
    private int offset;
    private PointIdentifier pointIdentifier = null;

    public PointSelector(SimpleXPathTemplate pointNodeTemplate) {
        Node pointSelectorNode = pointNodeTemplate.evaluateAsNode("*[1]");
        type = PointSelector.Type.getByElementName(pointSelectorNode.getNodeName());
        if (type == Type.TYPE_AND_OFFSET) {
            String typeStr = pointNodeTemplate.evaluateAsString("*[1]/@type");
            pointType = PointType.getForString(typeStr);
            offset = pointNodeTemplate.evaluateAsInt("*[1]/@offset");
        } else {
            name = pointNodeTemplate.evaluateAsString("*[1]/@name");
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public PointIdentifier getPointIdentifier() {
        if (type != Type.TYPE_AND_OFFSET) {
            throw new RuntimeException("can only get PointIdentifer for " +
                                       Type.TYPE_AND_OFFSET.elementName + " point selections");
        }
        if (pointIdentifier == null) {
            pointIdentifier = new PointIdentifier(pointType, offset);
        }
        return pointIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + offset;
        result = prime * result + ((pointType == null) ? 0 : pointType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointSelector other = (PointSelector) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (offset != other.offset)
            return false;
        if (pointType != other.pointType)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public String toString() {
        return type + ":" + (type == Type.TYPE_AND_OFFSET ? pointType + "/" + offset : name);
    }
}
