package com.cannontech.yukon.api.pointsnapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.YukonXml;

public class PointDataRequestEndpointTest {
    private PointDataRequestEndpoint impl;

    @Before
    public void setUp() throws Exception {
        impl = new PointDataRequestEndpoint();
        impl.setPointDataSnapshotService(new PointDataSnapshotService() {
            @Override
            public PointValueQualityHolder getSnapshot(int pointId) {
                return createMockPvh(pointId);
            }

            @Override
            public List<PointValueQualityHolder> getSnapshots(
                    Collection<Integer> pointIds) {
                List<PointValueQualityHolder> result = new ArrayList<PointValueQualityHolder>(pointIds.size());
                for (Integer integer : pointIds) {
                    result.add(createMockPvh(integer));
                }
                return result;
            }

            @Override
            public SnapshotResult getUpdatedSnapshots(
                    Collection<Integer> pointIds, final Long after) {
                final List<PointValueQualityHolder> snapshots = getSnapshots(pointIds);
                SnapshotResult snapshotResult = new SnapshotResult() {
                    @Override
                    public long getAsOf() {
                        return after + 1;
                    }

                    @Override
                    public List<PointValueQualityHolder> getValues() {
                        return snapshots;
                    }
                    
                };
                return snapshotResult;
            }
            
        });
        
        impl.initialize();
    }
    
    public static PointValueQualityHolder createMockPvh(final int pointId) {
        PointValueQualityHolder holder = new PointValueQualityHolder() {

            @Override
            public PointQuality getQualityEnum() {
                return PointQuality.Normal;
            }

            @Override
            public int getId() {
                return pointId;
            }

            @Override
            public Date getPointDataTimeStamp() {
                return new Date();
            }

            @Override
            public int getType() {
                return 0;
            }

            @Override
            public double getValue() {
                return 10 * pointId;
            }
            
        };
        
        return holder;
    }

    @Test
    public void testInvoke() throws Exception {
        Resource resource = new ClassPathResource("pointDataRequest.xml", PointDataRequestEndpointTest.class);
        Element inputElement = createElementFromResource(resource);
        Element outputElement = impl.invoke(inputElement);
        
        //new XMLOutputter(Format.getPrettyFormat()).output(outputElement, System.out);
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(outputElement));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());
        
        assertEquals(2, template.evaluateAsLong("count(/y:pointDataResponseMsg/y:pointValues/y:pointData)"));
        assertEquals(2, template.evaluateAsLong("count(/y:pointDataResponseMsg/y:pointValues/y:pointData/y:pointId)"));
        assertEquals(2, template.evaluateAsLong("count(/y:pointDataResponseMsg/y:pointValues/y:pointData/y:time)"));
        assertEquals(2, template.evaluateAsLong("count(/y:pointDataResponseMsg/y:pointValues/y:pointData/y:value)"));
        assertEquals(2, template.evaluateAsLong("count(/y:pointDataResponseMsg/y:pointValues/y:pointData/y:quality)"));
        
        List<Long> pointIds = template.evaluateAsLongList("/y:pointDataResponseMsg/y:pointValues/y:pointData/y:pointId");
        assertTrue(pointIds.contains(Long.valueOf(1234))); //be sure the 1234 literal gets interpreted as a long
        assertTrue(pointIds.contains(Long.valueOf(983))); 
        
        assertEquals(3121985, template.evaluateAsLong("/y:pointDataResponseMsg/y:validThrough"));
        
    }

    private static Element createElementFromResource(Resource resource)
            throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(resource.getInputStream());
        Element inputElement = document.getRootElement();
        return inputElement;
    }

}
