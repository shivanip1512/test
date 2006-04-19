package com.cannontech.database.incrementer;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class IncrementerTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = 
                new ClassPathXmlApplicationContext("com/cannontech/database/incrementer/testContext.xml");
            NextValueHelper valueHelper = (NextValueHelper) context.getBean("nextValueHelper");

            System.out.println("Next: " + valueHelper.getNextValue("Point"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
