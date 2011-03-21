package com.cannontech.analysis;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.base.Function;

public enum ReportFilterType {

    NONE {
        @Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
        }
    },
	METERNUMBER {
        @Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
    	    String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_METER_VALUES).trim();
            loadPao(model, filterValueList, new Function<String, LiteYukonPAObject>() {
                @Override
                public LiteYukonPAObject apply(String from) {
                    return DaoFactory.getDeviceDao().getLiteYukonPaobjectByMeterNumber(from);
                }
            });
        }
    }
	,
	DEVICENAME {
        @Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
    	    String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_DEVICE_VALUES).trim();
            loadPao(model, filterValueList, new Function<String, LiteYukonPAObject>() {
                @Override
                public LiteYukonPAObject apply(String from) {
                    return DaoFactory.getDeviceDao().getLiteYukonPaobjectByDeviceName(from);
                }
            });
        }
    }
	,
	DEVICEGROUP {
		@Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
            String[] paramArray = request.getParameterValues(ReportModelBase.ATT_FILTER_MODEL_VALUES);
            if( paramArray != null) {
                model.setBillingGroups(paramArray);
            }
        }
	}
	,
	PAOBJECTID {
		@Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
            String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
            int[] idsArray = StringUtils.parseIntString(filterValuesStr); 
            if (idsArray.length > 0) {
                model.setPaoIDs(idsArray);
            }
        }
    }
	,
	ACCOUNTNUMBER {
        @Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
        }
    }
    ,
	USER {
        @Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
        }
    }
    ,
	SERIALNUMBER {
        @Override
        public void applyParameters(ReportModelBase<?> model, HttpServletRequest request) {
        }
    },
    ;
	
	public abstract void applyParameters(ReportModelBase<?> model, HttpServletRequest request);

	private static void loadPao(ReportModelBase<?> model, String filterValueList, Function<String, LiteYukonPAObject> lookup) {

	    StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
	    int[] idsArray = new int[st.countTokens()];
	    int i = 0;
	    while (st.hasMoreTokens()) {
	        String meterNumber = st.nextToken().trim();
	        LiteYukonPAObject lPao = lookup.apply(meterNumber);
	        if( lPao != null) {
	            idsArray[i++] = lPao.getYukonID();
	        }
	    }
	    if( idsArray.length > 0 ) {
	        model.setPaoIDs(idsArray);
	    }
	}
}