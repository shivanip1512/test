package com.cannontech.system.dao;

import java.util.List;

import com.cannontech.system.model.Theme;

public interface ThemeDao {

    public Theme getTheme(int themeId);
    
    /**
     * Saves the theme, creating a new one if this theme does not exist,
     * updating otherwise.
     * @return the saved theme, if it was created, the returned theme will
     *         have a real themeId
     */
    public Theme saveTheme(Theme theme);
    
    public void deleteTheme(int themeId);

    /**
     * Returns a list of the default themes (Yukon Grey and Yukon Blue)
     */
    public List<Theme> getDefaultThemes();

    public List<Theme> getNonDefaultThemes();
    
}