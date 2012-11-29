package com.cannontech.web.amr.meter;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;

public class HistoricalReadingsControllerTest {
    IMocksControl mocksControl = EasyMock.createStrictControl();

    HistoricalReadingsController historicalReadingsController;

    YukonUserContextMessageSourceResolverMock messageSourceResolver;
    
    RawPointHistoryDao rawPointHistoryDaoMock;
    PointFormattingService pointFormattingServiceMock;

    @Before
    public void setup() {
        buildMessageSourceResolver();
        
        rawPointHistoryDaoMock = mocksControl.createMock(RawPointHistoryDao.class);
        pointFormattingServiceMock = mocksControl.createMock(PointFormattingService.class);
        
        historicalReadingsController = new HistoricalReadingsController();
        ReflectionTestUtils.setField(historicalReadingsController, "rawPointHistoryDao", rawPointHistoryDaoMock);
        ReflectionTestUtils.setField(historicalReadingsController, "messageSourceResolver", messageSourceResolver);
        ReflectionTestUtils.setField(historicalReadingsController, "pointFormattingService", pointFormattingServiceMock);
    }

    @Test
    public void csvFileIsGeneratedAndReturned() throws Exception {
        YukonUserContext yukonUserContext = new SystemUserContext();
        MockHttpServletRequest request = buildHttpRequest("all", 42, Order.FORWARD, OrderBy.TIMESTAMP);
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        List<PointValueHolder> pointValues = new ArrayList<PointValueHolder>();
        pointValues.add(new SimplePointValue(1, new Date(1), 1, 1.0d));
        pointValues.add(new SimplePointValue(2, new Date(1000), 2, 2.0d));
        pointValues.add(new SimplePointValue(3, new Date(1000000), 3, 3.0d));
        
        expect(rawPointHistoryDaoMock.getPointData(42, new Date(0), null, Clusivity.INCLUSIVE_INCLUSIVE, Order.FORWARD))
            .andReturn(pointValues);
        
        for(PointValueHolder value : pointValues) {
            expect(pointFormattingServiceMock.getValueString(value, Format.DATE, yukonUserContext)).andReturn(value.getPointDataTimeStamp().toString());
            expect(pointFormattingServiceMock.getValueString(value, Format.SHORT, yukonUserContext)).andReturn(Double.toString(value.getValue()));
        }
        
        mocksControl.replay();
        
        historicalReadingsController.download(null, request, response, yukonUserContext);
        
        mocksControl.verify();
        
        expectThatCsvDataIsGeneratedProperly(response, pointValues);
    }

    private void buildMessageSourceResolver() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("yukon.web.modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings.tableHeader.timestamp.linkText", Locale.US, "Timestamp");
        messageSource.addMessage("yukon.web.modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings.tableHeader.value.linkText", Locale.US, "Value");
        
        messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
        messageSourceResolver.setMessageSource(messageSource);
    }

    private MockHttpServletRequest buildHttpRequest(String period, Integer pointId, Order order, OrderBy orderBy) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        request.addParameter("period", period);
        request.addParameter("pointId", pointId.toString());
        request.addParameter("order", order.name());
        request.addParameter("orderBy", orderBy.name());
        
        return request;
    }

    private void expectThatCsvDataIsGeneratedProperly(MockHttpServletResponse response, List<PointValueHolder> pointValues) throws Exception {
        StringReader reader = new StringReader(response.getContentAsString());
        CSVReader csvReader = new CSVReader(reader);
        
        List<String[]> data = csvReader.readAll();
        
        assertThat(data.size(), is(equalTo(pointValues.size() + 1)));
        assertThat(data.get(0).length, is(equalTo(2)));
        assertThat(data.get(0)[0], is(equalTo("Timestamp")));
        assertThat(data.get(0)[1], is(equalTo("Value")));
        
        for(int i = 1; i < data.size(); i++) {
            assertThat(data.get(i)[0], is(equalTo(pointValues.get(i-1).getPointDataTimeStamp().toString())));
            assertThat(data.get(i)[1], is(equalTo(Double.toString(pointValues.get(i-1).getValue()))));
        }
    }
}
