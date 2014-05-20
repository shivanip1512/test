package com.cannontech.common.userpage.model;

import java.util.List;

import org.joda.time.Instant;

public final class UserPage {
    public static final class Key {
        private final int userId;
        private final String path;

        public Key(int userId, String path) {
            this.userId = userId;
            this.path = path;
        }

        public int getUserId() {
            return userId;
        }

        public String getPath() {
            return path;
        }
    }

    // The database primary key.
    private final Integer id;

    // The real primary key.
    private final Key key;

    // Variables that map to things <page> tag in module_config.xml.  These are just here (and in the database)
    // for convenience...they could all be derived from the path in the key.
    private final SiteModule module; // the name attribute of the parent <module> tag
    private final String name; // name attribute (of the page itself) of course
    private final List<String> arguments; // a list of <labelArgument> inside the <page> tag

    // Variable data...the "state" of the UserPage.
    private final boolean favorite;
    private final Instant lastAccess;

    /**
     * As a rule, these should not need to be created outside the DAO.
     */
    public UserPage(Integer id, Key key, SiteModule module, String name, List<String> arguments, boolean isFavorite,
            Instant lastAccess) {
        this.id = id;
        this.key = key;
        this.favorite = isFavorite;
        this.module = module;
        this.name = name;
        this.arguments = arguments;
        this.lastAccess = lastAccess;
    }

    public Integer getId() {
        return id;
    }

    public Key getKey() {
        return key;
    }

    public int getUserId() {
        return key.userId;
    }

    public String getPath() {
        return key.path;
    }

    public SiteModule getModule() {
        return module;
    }

    public String getModuleName() {
        return module == null ? "" : module.getName();
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
}
