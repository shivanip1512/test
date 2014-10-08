package com.cannontech.web.dev;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.collect.ImmutableMap;

@Controller
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class DeveloperController {
    private final Logger log = YukonLogManager.getLogger(DeveloperController.class);

    @Autowired private DbChangeManager dbChangeManager;

    private final Map<String, Integer> databaseFields;
    private final Map<String, String> categoryFields;

    @Autowired
    public DeveloperController(ConfigurationSource configurationSource) {
        Map<String, Integer> mutableDatabaseFields = new HashMap<>();
        Map<String, String> mutableCategoryFields = new HashMap<>();
        if (configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)) {
            try {
                DBChangeMsg obj = new DBChangeMsg();
                Class<?> objClass = obj.getClass();
                Field[] fields = objClass.getFields();
                for (Field field : fields) {
                    String name = field.getName();
                    Object value = field.get(obj);
                    if (name.startsWith("CAT_")) {
                        mutableCategoryFields.put(name, (String) value);
                    } else if (name.startsWith("CHANGE_")) {
                        mutableDatabaseFields.put(name, (Integer) value);
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.warn("DBChange development tool was not setup properly. Unexpected error occured. ", e);
            }
        }
        databaseFields = ImmutableMap.copyOf(mutableDatabaseFields);
        categoryFields = ImmutableMap.copyOf(mutableCategoryFields);
    }

    @RequestMapping(value = { "/", "/dev" })
    public String root() {
        return "development.jsp";
    }

    @RequestMapping("/db-change")
    public String dbChangePage(ModelMap model) {
        model.addAttribute("databaseFields", databaseFields);
        model.addAttribute("categoryFields", categoryFields);
        model.addAttribute("dbChangeTypes", DbChangeType.values());
        return "dbChange.jsp";
    }

    @RequestMapping("/do-db-change")
    @ResponseBody
    public String doDbChangePage(Integer itemId, String databaseField, String categoryField, DbChangeType type) {
        Integer database = databaseFields.get(databaseField);
        String category = categoryFields.get(categoryField);

        dbChangeManager.processDbChange(itemId, database, category, category, type);

        return "success";
    }
}
