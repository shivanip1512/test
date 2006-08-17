package com.cannontech.cbc.dao;

import com.cannontech.spring.YukonSpringHook;

public class CBCDaoFactory {

    public CBCDaoFactory() {
        super();
    }

    public static CBCDao getCBCDao () {
        return  (CBCDao) YukonSpringHook.getBean("cbcDao");
    }
    
}
