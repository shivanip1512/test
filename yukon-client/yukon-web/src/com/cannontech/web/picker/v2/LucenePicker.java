package com.cannontech.web.picker.v2;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.search.YukonObjectCriteria;

public abstract class LucenePicker<T> extends BasePicker<T> {
    protected YukonObjectCriteria criteria = null;

    public void setCriteria(String criteriaClassName) {
        if (StringUtils.isNotBlank(criteriaClassName)) {
            try {
                Class<?> criteriaClass = getClass().getClassLoader().loadClass(criteriaClassName);
                this.criteria = (YukonObjectCriteria) criteriaClass.newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("could not find class " + criteriaClassName, cnfe);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("error instantiating class " + criteriaClassName, iae);
            } catch (InstantiationException ie) {
                throw new RuntimeException("error instantiating class " + criteriaClassName, ie);
            }
        }
    }
}
