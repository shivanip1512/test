package com.cannontech.database.incrementer;

import java.net.URL;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class IncrementerTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            XmlIncrementer lookup = new XmlIncrementer();
            URL url = ClassLoader.getSystemResource("com/cannontech/database/incrementer/table_sequences.xml");
            lookup.setConfigFile(url);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            DataSource ds = new DriverManagerDataSource("jdbc:jtds:sqlserver://mn1db02:1433;APPNAME=yukon-client;TDS=8.0","tom","tom");
            lookup.setDataSource(ds);
            lookup.parse();
            
            
            int nextValue = lookup.getNextValue("CCurtProgram");
            System.out.println("Got: " + nextValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
