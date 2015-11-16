package com.cannontech.web.index;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.lucene.index.IndexBuilder;
import com.cannontech.web.search.lucene.index.IndexManager;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_INDEXES)
public class LuceneIndexController {
    
    @Autowired private IndexBuilder builder;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private final static String key = "yukon.web.modules.support.indexes.";
    
    @RequestMapping("manage")
    public String manage(ModelMap model, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, String> names = new HashMap<>();
        List<IndexManager> indexes = builder.getIndexList();
        
        Comparator<IndexManager> comparator = Ordering.natural().onResultOf(new Function<IndexManager, String>() {
            @Override
            public String apply(IndexManager index) {
                names.put(index.getIndexName(), accessor.getMessage(key + index.getIndexName()));
                return index.getIndexName();
            }
        });
        Collections.sort(indexes, comparator);
        model.addAttribute("indexes", indexes);
        model.addAttribute("names", names);
        
        return "manage.jsp";
    }
    
    @RequestMapping("{index}/build")
    public void build(HttpServletResponse resp, @PathVariable String index) {
        builder.buildIndex(index);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    /** Return a map of index name to index status. */
    @RequestMapping("status")
    public @ResponseBody Map<String, Object> status() {
        
        Map<String, Object> indexes = new HashMap<>();
        
        for (IndexManager index : builder.getIndexList()) {
            Map<String, Object> data = new HashMap<>();
            float percentDone = index.getPercentDone();
            data.put("percentDone", percentDone);
            data.put("building", index.isBuilding());
            data.put("date", (index.getDateCreated() == null) ? "--" : index.getDateCreated());
            indexes.put(index.getIndexName(), data);
        }
        
        return indexes;
    }
    
}