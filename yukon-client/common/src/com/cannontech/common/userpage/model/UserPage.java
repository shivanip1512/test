package com.cannontech.common.userpage.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;

public final class UserPage {
    private final Integer id;
    private final int userId;
    private final String path;
    private final SiteModule module;
    private final String name;
    private final List<String> arguments;
    private final boolean favorite;
    private final Instant lastAccess;

    public UserPage(int userId, String path, boolean isFavorite) {
        this(userId, path, isFavorite, null, null, new ArrayList<String>(), new Instant(), null);
    }

    public UserPage(int userId, String path, boolean category, SiteModule module, String name, List<String> arguments) {
        this(userId, path, category, module, name, arguments, new Instant(), null);
    }

    public UserPage(int userId, String path, boolean isFavorite, SiteModule module, String name,
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

    public int getUserId() {
        return userId;
    }

    public String getPath() {
        return path;
    }

    public SiteModule getModule() {
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
