package com.cannontech.web.admin.encryption;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.TransactionType;
import com.cannontech.database.db.pao.StaticPaoInfo;
import com.cannontech.encryption.EncryptedRoute;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/encryption/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class EncryptionController {
    
    private Logger log = YukonLogManager.getLogger(EncryptionController.class);
    private static final String CPS_ONE_WAY_ENCRYPTION_KEY = "CPS_ONE_WAY_ENCRYPTION_KEY";
    private DBPersistentDao dbPersistentDao;
    private EncryptedRouteDao encryptedRouteDao;

    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model) {

        List<EncryptedRoute> encryptedRoutes = encryptedRouteDao.getAllEncryptedRoutes();
        model.addAttribute("encryptedRoutes", encryptedRoutes);

        return "encryptionSettings.jsp";
    }

    @RequestMapping("save")
    public String save(HttpServletRequest request, ModelMap model, String[] value,
                       Integer[] paobjectId) {
        
        StaticPaoInfo spi = new StaticPaoInfo(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY);

        if (paobjectId != null
                && value != null
                && paobjectId.length == value.length
                && paobjectId.length != 0) {
            
            for (int i = 0; i < paobjectId.length; i++) {
                spi.setInfoKey(CPS_ONE_WAY_ENCRYPTION_KEY);
                spi.setPaobjectId(paobjectId[i]);
                try {
                    dbPersistentDao.performDBChange(spi, TransactionType.RETRIEVE);
                    if (value[i].length() == 0) {
                        spi.setValue(null);
                    } else {
                        spi.setValue(value[i]);
                    }
                    dbPersistentDao.performDBChange(spi, TransactionType.UPDATE);
                } catch (Exception e) {
                    log.error("Problem while trying to save an encryption key to the database.",e);
                }
            }
        } else {
            log.warn("Unable to save encryption keys in database.");
        }

        return "redirect:view";
    }
    
    @Autowired
    public void setEncryptedRouteDao(EncryptedRouteDao encryptedRouteDao) {
        this.encryptedRouteDao = encryptedRouteDao;
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

}