package com.cannontech.web.menu;

import java.util.List;

public interface ModuleBuilder {
    Module getModule(String moduleName);
    List<Module> getAllModules();
}