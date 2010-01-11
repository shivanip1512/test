package com.cannontech.web.menu;

import java.util.List;

public interface ModuleBuilder {

    public ModuleBase getModuleBase(String moduleName);
    public List<ModuleBase> getAllModules();
}