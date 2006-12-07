package com.cannontech.tdc.utils;

import java.awt.Frame;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JDialog;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.model.ModelContext;
import com.cannontech.tdc.model.ModelContextException;
import com.cannontech.tdc.model.TDCDataModel;
import com.cannontech.tdc.model.TDCDataModelCollection;
import com.cannontech.tdc.template.TemplateDisplay;
import com.cannontech.tdc.template.TemplateDisplayModel;

public class DataModelUtils {

    private static Class[] ALL_MODELS = { TemplateDisplayModel.class };

    private static Hashtable<String, List<String>> allModelMap = new Hashtable<String, List<String>>(10);

    public DataModelUtils() {
        super();

    }

    public static TDCDataModel getComponentDataModel(Frame owner,
            Object component, String cxt) {
        String modelName = null;
        String compName = component.getClass().getName();
        // iterate though the mapping and get the model from the list
        Collection<String> keys = allModelMap.keySet();
        for (String key : keys) {
            List<String> compList = allModelMap.get(key);
            for (String comp : compList) {
                if (comp.equalsIgnoreCase(compName)) {
                    // make sure that we get the component in the right context
                    try {
                        Method method = component.getClass()
                                                 .getMethod("getModelContextList");
                        List<String> ctxList = (List<String>) method.invoke(component);
                        if (ctxList.contains(cxt)) {
                            modelName = key;

                        }
                    } catch (Exception e) {}
                }
            }
        }
        if ((modelName != null) && (owner != null)) {
            TDCDataModel dataModel = ((TDCMainFrame) owner).getDataModel();
            TDCDataModelCollection dataModelCollection = ((TDCDataModelCollection) dataModel);
            return dataModelCollection.getTDCModel(modelName);
        }
        return null;
    }

    private synchronized static void registerComponentsForModel(Class model,
            Class component) throws ModelContextException {

        List<String> components;
        String modelName = model.getName();
        String compName = component.getName();

        try {

            ModelContext newInstance = (ModelContext) component.newInstance();
        } catch (InstantiationException e) {} catch (IllegalAccessException e) {} catch (ClassCastException e) {
            CTILogger.error("Could not register component because it doesn't implement ModelContext");
        }
        if (!allModelMap.containsKey(modelName)) {
            components = new ArrayList<String>(10);
        } else {
            components = allModelMap.get(modelName);
        }
        components.add(compName);
        allModelMap.put(modelName, components);

    }

    private static void initAllModels(TDCDataModel model) {
        for (int i = 0; i < ALL_MODELS.length; i++) {
            Class modelClass = ALL_MODELS[i];

            String name = modelClass.getName();

            TDCDataModel m;
            try {
                TDCDataModel newInstance = (TDCDataModel) modelClass.newInstance();
                ((TDCDataModelCollection) model).addTDCModel(name, newInstance);

                m = newInstance;
                String[] comps = m.getAllRegisteredComponents();
                for (int j = 0; j < comps.length; j++) {
                    String comp = comps[j];
                    registerComponentsForModel(modelClass, Class.forName(comp));

                }
            } catch (Exception e) {
                CTILogger.error(e);
            }

        }
    }

    public static TDCDataModel createMainModel(Frame f) {
        TDCDataModelCollection allModels = new TDCDataModelCollection(f);
        initAllModels(allModels);
        return allModels;
    }

    public static void saveDataModel(Frame frame) {
        TDCDataModel dataModel = ((TDCMainFrame) frame).getDataModel();
        dataModel.saveModel();

    }

    public static Integer getDisplayNum(String dispName) {
        String sqlStmt = "SELECT DisplayNum FROM Display WHERE ";
        sqlStmt += "Name like ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        Object tempInt = yukonTemplate.queryForObject(sqlStmt,
                                                      new String[] { dispName },
                                                      Integer.class);
        return (Integer) tempInt;
    }

    public static List<Integer> getAllDisplaysForTemplate(Integer templateNum) {
        String sqlStmt = "SELECT DisplayNum FROM " + TemplateDisplay.TABLE_NAME + " WHERE ";
        sqlStmt += "TemplateNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(sqlStmt,
                                          new Integer[] { templateNum },
                                          Integer.class);
    }



    public static void templatizeDisplay(Integer templateNum, Integer displayNum) {
        String sqlStmt = "insert into displaycolumns " +  
            "select ?, title, typenum, ordering, width  from templatecolumns where templatenum = ?";  
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt, new Object[] { displayNum, templateNum });
        
    }
    
    public static void deleteDisplayColumns (Integer dispNum) {
        String sqlStmt = "DELETE FROM DisplayColumns where DisplayNum = ?"; 
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.update(sqlStmt, new Integer[] {dispNum});
    }
    

    public static int getDisplayTemplate(long currentDisplayNumber) {
        int templateNum = -1;
        String sqlStmt = "SELECT TemplateNum FROM " + TemplateDisplay.TABLE_NAME + " WHERE ";
        sqlStmt += "DisplayNum = ?";
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        try
        {
        
            templateNum = yukonTemplate.queryForInt(sqlStmt,
                                          new Integer[] { (Integer.parseInt("" + currentDisplayNumber)) });
        }
        catch (IncorrectResultSizeDataAccessException e)
        {
            templateNum = -1;
        }
        return templateNum;
    } 



}
