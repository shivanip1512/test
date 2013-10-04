package com.cannontech.common.userpage.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;

public final class UserPage {
    private final Integer id;
    private final Integer userId;
    private final String path;
    private final String module;
    private final String name;
    private final List<String> arguments;
    private final boolean favorite;
    private final Instant lastAccess;
    private final static Logger log = YukonLogManager.getLogger(UserPage.class);

    public enum Module implements DisplayableEnum {
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

        final int order;

        Module(int order) {
            this.order = order;
        }

        public static Comparator<Module> menuOrder() {
            return new Comparator<Module>() {
                @Override
                public int compare(Module left, Module right) {
                    return Integer.compare(left.order, right.order);
                }
            };
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.menu." + name().toLowerCase(Locale.US);
        }
    }

    private final static Map<String, String> mappedModuleNames = ImmutableMap.of("capcontrol", "vv", "amr", "ami",
        "adminSetup", "admin", "operator", "assets", "survey", "assets");

    public Module getModuleEnum() {
        String moduleName = module;
        if (mappedModuleNames.containsKey(moduleName)) {
            moduleName = mappedModuleNames.get(moduleName);
        }
        moduleName = moduleName.toUpperCase(Locale.US);

        Module temp = Module.UNKNOWN;
        try {
            temp = Module.valueOf(moduleName);
        } catch (Exception e) {
            log.error("unknown module name " + moduleName);
        }
        return temp;
    }

    public UserPage(Integer userId, String path, boolean isFavorite) {
        this(userId, path, isFavorite, null, null, new ArrayList<String>(), new Instant(), null);
    }

    public UserPage(Integer userId, String path, boolean category, String moduleName, String name,
            List<String> arguments) {
        this(userId, path, category, moduleName, name, arguments, new Instant(), null);
    }

    public UserPage(Integer userId, String path, boolean isFavorite, String module, String name,
            List<String> arguments, Instant lastAccess, Integer id) {
        this.id = id;
        this.userId = userId;
        this.path = path;
        this.favorite = isFavorite;
        this.module = module;
        this.name = name;
        this.arguments = arguments;
        this.lastAccess = lastAccess;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getPath() {
        return path;
    }

    public String getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public Instant getLastAccess() {
        return lastAccess;
    }

    public UserPage withId(Integer newId) {
        if (id == newId) {
            return this;
        }
        return new UserPage(userId, path, favorite, module, name, arguments, lastAccess, newId);
    }

    public UserPage withFavorite(boolean newFavorite) {
        if (favorite == newFavorite) {
            return this;
        }
        return new UserPage(userId, path, newFavorite, module, name, arguments, lastAccess, id);
    }

    public UserPage withLastAccess(Instant newLastAccess) {
        if (lastAccess == newLastAccess) {
            return this;
        }
        return new UserPage(userId, path, favorite, module, name, arguments, newLastAccess, id);
    }

    public UserPage withArguments(List<String> newArguments) {
        if (arguments == newArguments) {
            return this;
        }
        return new UserPage(userId, path, favorite, module, name, newArguments, lastAccess, id);
    }
}
