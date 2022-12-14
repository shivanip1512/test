package com.cannontech.web.updater.development;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.development.model.DevObject;
import com.cannontech.web.dev.DevDbSetupTask;
import com.cannontech.web.updater.ResultAccessor;


public enum DevExecutionUpdaterTypeEnum {
    AMR_TOTAL_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getTotalCountString(devDbSetupTask, getDevAmr(devDbSetupTask));
            return result;
        }
    }),
	CC_TOTAL_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getTotalCountString(devDbSetupTask, getDevCC(devDbSetupTask));
            return result;
        }
    }),
	STARS_TOTAL_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getTotalCountString(devDbSetupTask, getDevStars(devDbSetupTask));
            return result;
        }
    }),
	AMR_SUCCESS_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getSuccessCountString(devDbSetupTask, getDevAmr(devDbSetupTask));
            return result;
        }
    }),
	CC_SUCCESS_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getSuccessCountString(devDbSetupTask, getDevCC(devDbSetupTask));
            return result;
        }
    }),
	STARS_SUCCESS_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getSuccessCountString(devDbSetupTask, getDevStars(devDbSetupTask));
            return result;
        }
    }),
	AMR_FAILURE_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getFailureCountString(devDbSetupTask, getDevAmr(devDbSetupTask));
            return result;
        }
    }),
	CC_FAILURE_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getFailureCountString(devDbSetupTask, getDevCC(devDbSetupTask));
            return result;
        }
    }),
	STARS_FAILURE_COUNT(new ResultAccessor<DevDbSetupTask>() {
        @Override
        public Object getValue(DevDbSetupTask devDbSetupTask) {
            String result = getFailureCountString(devDbSetupTask, getDevStars(devDbSetupTask));
            return result;
        }
    })
	;
	
    private ResultAccessor<DevDbSetupTask> devDbSetupTaskAccessor;

    DevExecutionUpdaterTypeEnum(ResultAccessor<DevDbSetupTask> devDbSetupTaskAccessor) {
        this.devDbSetupTaskAccessor = devDbSetupTaskAccessor;
    }
    
    public Object getValue(DevDbSetupTask devDbSetupTaskAccessor) {
        return this.devDbSetupTaskAccessor.getValue(devDbSetupTaskAccessor);
    }
    
    private static String getSuccessCountString(DevDbSetupTask dbSetupTask, DevObject devObj) {
        if (dbSetupTask == null || devObj == null) {
            return StringUtils.EMPTY;
        }
        int count = devObj.getSuccessCount();
        String result = String.valueOf(count);
        return result;
    }
    
    private static String getFailureCountString(DevDbSetupTask dbSetupTask, DevObject devObj) {
        if (dbSetupTask == null || devObj == null) {
            return StringUtils.EMPTY;
        }
        int count = devObj.getFailureCount();
        String result = String.valueOf(count);
        return result;
    }
    
    private static String getTotalCountString(DevDbSetupTask dbSetupTask, DevObject devObj) {
        if (dbSetupTask == null || devObj == null) {
            return StringUtils.EMPTY;
        }
        int count = devObj.getTotal();
        String result = String.valueOf(count);
        return result;
    }
    
    private static DevObject getDevAmr(DevDbSetupTask devDbSetupTask) {
        DevObject devObj = devDbSetupTask != null ? devDbSetupTask.getDevAmr() : null;
        return devObj;
    }
    
    private static DevObject getDevCC(DevDbSetupTask devDbSetupTask) {
        DevObject devObj = devDbSetupTask != null ? devDbSetupTask.getDevCapControl() : null;
        return devObj;
    }
    
    private static DevObject getDevStars(DevDbSetupTask devDbSetupTask) {
        DevObject devObj = devDbSetupTask != null ? devDbSetupTask.getDevStars() : null;
        return devObj;
    }
}
