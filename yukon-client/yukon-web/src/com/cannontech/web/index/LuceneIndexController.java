package com.cannontech.web.index;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.search.lucene.index.IndexBuilder;
import com.cannontech.web.search.lucene.index.IndexManager;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MANAGE_INDEXES)
public class LuceneIndexController extends MultiActionController {

    IndexBuilder indexBuilder = null;

    public void setIndexBuilder(IndexBuilder indexBuilder) {
        this.indexBuilder = indexBuilder;
    }

    public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("manage");

        mav.addObject("indexList", this.indexBuilder.getIndexList());

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

        IndexManager indexManager = this.indexBuilder.getIndexManager(index);
        float percentDone = indexManager.getPercentDone();
        mav.addObject("percentDone", percentDone);
        mav.addObject("newDatabase", indexManager.getDatabase());
        mav.addObject("isBuilding", indexManager.isBuilding());
        mav.addObject("newDate", (indexManager.getDateCreated() == null) ? "--"
                : indexManager.getDateCreated());

        return mav;
    }

}
