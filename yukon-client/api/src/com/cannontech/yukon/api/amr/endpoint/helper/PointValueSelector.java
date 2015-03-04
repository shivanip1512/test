package com.cannontech.yukon.api.amr.endpoint.helper;

import org.joda.time.Instant;
import org.w3c.dom.Node;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.AfterParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.AllAfterParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.AllBeforeParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.AllBetweenParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.AllParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.BeforeParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.LatestParser;
import com.cannontech.yukon.api.amr.endpoint.helper.parsers.ValueParser;

public class PointValueSelector {
    private SelectorType selectorType;
    private Range<Instant> instantRange;
	private Order order;
    private Integer numberOfRows;
    private String label;

    public static PointValueSelector fromNode(Node pointValueNode) {
        SelectorType type = SelectorType.getByName(pointValueNode.getLocalName());
        return type.parseValueSelector(pointValueNode);
    }
    
    public Range<Instant> getInstantRange() {
		return instantRange;
	}

	public void setInstantRange(Range<Instant> dateRange) {
		this.instantRange = dateRange;
	}

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order oder) {
        this.order = oder;
    }

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(Integer index) {
        this.numberOfRows = index;
    }

    public SelectorType getValueSelectorType() {
        return selectorType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "PointValueSelector [selectorType=" + selectorType + ", DateRange=" + instantRange + ", order=" + order
            + ", numberOfRows=" + numberOfRows + ", label=" + label + "]";
    }

    public enum SelectorType {
        BEFORE("before", new BeforeParser()),
        AFTER("after", new AfterParser()),
        ALL_BEFORE("allBefore", new AllBeforeParser()),
        ALL_AFTER("allAfter", new AllAfterParser()),
        ALL_BETWEEN("allBetween", new AllBetweenParser()),
        ALL("all", new AllParser()),
        LATEST("latest", new LatestParser()),
        SNAPSHOT("snapshot", null);

        private ValueParser parser;
        private String name;

        private SelectorType(String name, ValueParser parser) {
            this.name = name;
            this.parser = parser;
        }

        public PointValueSelector parseValueSelector(Node node) {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);
            return parser.parse(template);
        }

        public static SelectorType getByName(String name) {
            if (name == null) {
                return null;
            }

            for (SelectorType type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum OrderHelper {
        ASCENDING("ascending", Order.FORWARD),
        DESCENDING("descending", Order.REVERSE);

        private String name;
        private Order order;

        private OrderHelper(String name, Order order) {
            this.name = name;
            this.order = order;
        }

        public Order getOrder() {
            return order;
        }

        public static Order getOrderByName(String name, Order defaultOrder) {
            if (name == null) {
                return defaultOrder;
            }

            for (OrderHelper order : values()) {
                if (order.name.equals(name)) {
                    return order.order;
                }
            }
            return defaultOrder;
        }
    }
}
