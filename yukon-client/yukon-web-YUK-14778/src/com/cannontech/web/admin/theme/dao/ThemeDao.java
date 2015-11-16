package com.cannontech.web.admin.theme.dao;

import java.util.List;

import com.cannontech.web.admin.theme.model.Theme;

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

    public List<Theme> getThemes();

    public Theme getCurrentTheme();

    /**
     * Set the theme with the id as the currently selected theme.
     */
    public void setCurrentTheme(int id);
    
}