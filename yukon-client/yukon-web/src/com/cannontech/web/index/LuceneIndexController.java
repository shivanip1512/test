package com.cannontech.web.index;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.search.lucene.index.IndexBuilder;
import com.cannontech.web.search.lucene.index.IndexManager;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_INDEXES)
public class LuceneIndexController extends MultiActionController {
    @Autowired private IndexBuilder indexBuilder = null;

    public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("manage");
        List<IndexManager> managers = indexBuilder.getIndexList();

        Comparator<IndexManager> comparator = Ordering.natural().onResultOf(new Function<IndexManager, String>() {
            @Override
            public String apply(IndexManager indexManager) {
                return indexManager.getIndexName();
            }
        });
        Collections.sort(managers, comparator);
        mav.addObject("indexList", managers);

        return mav;
    }

    public ModelAndView buildIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("manage");

        final String index = request.getParameter("index");

        indexBuilder.buildIndex(index);

        return mav;
    }

    public ModelAndView percentDone(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());

        final String index = request.getParameter("index");

        IndexManager indexManager = indexBuilder.getIndexManager(index);
        float percentDone = indexManager.getPercentDone();
        mav.addObject("percentDone", percentDone);
        mav.addObject("isBuilding", indexManager.isBuilding());
        mav.addObject("newDate", (indexManager.getDateCreated() == null) ? "--" : indexManager.getDateCreated());

        return mav;
    }
}
