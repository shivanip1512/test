package com.cannontech.core.dao.impl;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;

public class RawPointHistoryDaoImplTest {
    RawPointHistoryDao dao;

    @Before
    public void setUp() throws Exception {
        System.setProperty("java.locale.providers", "COMPAT,SPI");
        // we need to create a special version that stubs out the actual DB method
        dao = new RawPointHistoryDaoImpl() {
            @Override
            public List<PointValueHolder> getPointData(int pointId, Date startDate, Date stopDate) {
                List<PointValueHolder> result = new ArrayList<PointValueHolder>();
                result.add(new PointValue("5/5/05 4:30 pm", 5.5));
                result.add(new PointValue("5/5/05 4:35 pm", 5.6));
                result.add(new PointValue("5/5/05 4:36 pm", 5.7));
                result.add(new PointValue("5/5/05 4:37 pm", 4.5));
                result.add(new PointValue("5/5/05 4:39 pm", 5.5));

                result.add(new PointValue("5/5/05 6:30 pm", 9.9));
                result.add(new PointValue("5/5/05 6:35 pm", 3.6));
                result.add(new PointValue("5/5/05 6:36 pm", 3.7));
                result.add(new PointValue("5/5/05 6:37 pm", 2.5));
                result.add(new PointValue("5/5/05 6:39 pm", 3.5));

                result.add(new PointValue("5/5/05 8:30 pm", 3.5));
                result.add(new PointValue("5/5/05 8:35 pm", 3.6));
                result.add(new PointValue("5/5/05 8:36 pm", 3.7));
                result.add(new PointValue("5/5/05 8:37 pm", 2.5));
                result.add(new PointValue("5/5/05 8:39 pm", 9.9));
                return result;
            }
        };
    }
    
    @Test
    public void testGetBinnedPointData1() throws ParseException {
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        List<PointValueHolder> binnedPointData = dao.getIntervalPointData(0, dateTimeInstance.parse("5/4/05 4:36 pm"), dateTimeInstance.parse("5/6/05 4:36 pm"), ChartInterval.HOUR, RawPointHistoryDao.Mode.HIGHEST);
        List<PointValueHolder> expected = new ArrayList<PointValueHolder>();
        expected.add(new PointValue("5/5/05 4:36 pm", 5.7));
        expected.add(new PointValue("5/5/05 6:30 pm", 9.9));
        expected.add(new PointValue("5/5/05 8:39 pm", 9.9));
        
        assertEquals(expected, binnedPointData);
    }

    @Test
    public void testGetBinnedPointData2() throws ParseException {
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        List<PointValueHolder> binnedPointData = dao.getIntervalPointData(0, dateTimeInstance.parse("5/4/05 4:36 pm"), dateTimeInstance.parse("5/6/05 4:36 pm"), ChartInterval.HOUR, RawPointHistoryDao.Mode.LAST);
        List<PointValueHolder> expected = new ArrayList<PointValueHolder>();
        expected.add(new PointValue("5/5/05 4:39 pm", 5.5));
        expected.add(new PointValue("5/5/05 6:39 pm", 3.5));
        expected.add(new PointValue("5/5/05 8:39 pm", 9.9));
        
        assertEquals(expected, binnedPointData);
    }
    
    private class PointValue implements PointValueHolder {

        private Date date;
        private final double value;
        private final String dateStr;

        public PointValue(String dateStr, double value) {
            this.dateStr = dateStr;
            this.value = value;
            try {
                DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                date = dateTimeInstance.parse(dateStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public int getId() {
            return 0;
        }

        public Date getPointDataTimeStamp() {
            return date;
        }

        public int getType() {
            return 0;
        }

        public double getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value + " @ " + dateStr;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((date == null) ? 0 : date.hashCode());
            long temp;
            temp = Double.doubleToLongBits(value);
            result = PRIME * result + (int) (temp ^ (temp >>> 32));
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
            final PointValue other = (PointValue) obj;
            if (date == null) {
                if (other.date != null)
                    return false;
            } else if (!date.equals(other.date))
                return false;
            if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
                return false;
            return true;
        }
        
    }

}
