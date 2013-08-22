package com.cannontech.common.userpage.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.cannontech.common.i18n.DisplayableEnum;

public class UserPage {

    private final Integer id;
    private final Integer userId;
    private final String path;
    private final String module;
    private final String name;
    private final List<String> arguments;
    private final Category category;
    private final Date timestamp;

    public enum Module implements DisplayableEnum, Comparable<Module> {
        AMI(1),
        DR(2),
        VV(3),
        ASSETS(4),
        TOOLS(5),
        ADMIN(6),
        SUPPORT(7),
        DEVELOPMENT(8),
        UNKNOWN(999),
        ;

        int order;

        Module(int order){
            this.order = order;
        }

        public static Comparator<Module> menuOrder() {
            return new Comparator<Module>() {
                @Override
                public int compare(Module left, Module right) {
                    return Integer.compare(left.order, right.order);
                }};
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.menu." + name().toLowerCase(Locale.US);
        }
    }

    public enum Category{HISTORY, FAVORITE};

    public  Module getModuleEnum() {
        String moduleName = module;
        if (moduleName.equals("capcontrol")) {
            moduleName = "vv";
        }
        if (moduleName.equals("amr")) {
            moduleName = "ami";
        }
        if (moduleName.equals("adminSetup")) {
            moduleName = "admin";
        }
        if (moduleName.equals("operator")) {
            moduleName = "assets";
        }
        if (moduleName.equals("survey")) {
            moduleName = "assets";
        }

        moduleName = moduleName.toUpperCase(Locale.US);

        Module temp = Module.UNKNOWN;
        try {
            temp = Module.valueOf(moduleName);
        } catch(Exception e) {
            temp = Module.UNKNOWN;
        }
        return temp;
    }

    public UserPage(Integer userId, String path, Category category) {
        this(userId, path, category, null, null, new ArrayList<String>(), new Date(), null);   
    }

    public UserPage(Integer userId, String path, Category category, String moduleName, String name, List<String> arguments) {
     this(userId, path, category, moduleName, name, arguments, new Date(), null);   
    }

    public UserPage(Integer userId, String path, Category category,  String module, String name, List<String> arguments, Date timestamp, Integer id) {
        this.id = id;
        this.userId = userId;
        this.path = path;
        this.category = category;
        this.module = module;
        this.name = name;
        this.arguments = arguments;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }
    public final Integer getUserId() {
        return userId;
    }
    public final String getPath() {
        return path;
    }
    public final String getModule() {
        return module;
    }
    public final String getName() {
        return name;
    }
    public final List<String> getArguments() {
        return arguments;
    }
    public final Category getCategory() {
        return category;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public UserPage updateId(Integer id) {
        if (this.id == id) {
            return this;
        } else {
            return new UserPage(this.userId, this.path, this.category, this.module, this.name, this.arguments, this.timestamp, id);
        }
    }
}