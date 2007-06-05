package com.cannontech.web.amr.csr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.csr.model.CsrSearchField;
import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.amr.csr.model.SearchPao;
import com.cannontech.amr.csr.service.CsrService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Spring controller class for csr
 */
public class CsrController extends MultiActionController {

    private PaoDao paoDao = null;

    private CsrService csrService = null;

    public CsrController() {
        super();
    }

    public void setCsrService(CsrService csrService) {
        this.csrService = csrService;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceSelection.jsp");

        // Set the request url and parameters as a session attribute
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        request.getSession().setAttribute("searchResults",
                                          url + ((urlParams != null) ? "?" + urlParams : ""));

        // Get the search start index
        int startIndex = ServletRequestUtils.getIntParameter(request, "startIndex", 0);
        if (request.getParameter("Filter") != null) {
            startIndex = 0;
        }

        // Get the search result count
        int count = ServletRequestUtils.getIntParameter(request, "count", 25);

        // Get the order by field
        String orderByField = ServletRequestUtils.getStringParameter(request,
                                                                     "orderBy",
                                                                     CsrSearchField.PAONAME.toString());
        OrderBy orderBy = new OrderBy(orderByField,
                                      ServletRequestUtils.getBooleanParameter(request,
                                                                              "descending",
                                                                              false));

        // Build up filter by list
        List<FilterBy> filterByList = new ArrayList<FilterBy>();
        for (CsrSearchField field : CsrSearchField.values()) {

            String fieldValue = ServletRequestUtils.getStringParameter(request, field.getName(), "");
            if (StringUtils.isNotBlank(fieldValue)) {
                filterByList.add(new FilterBy(field, fieldValue));
            }
        }

        // Create filter by string to display on jsp
        List<String> filterByString = new ArrayList<String>();
        for (FilterBy filterBy : filterByList) {
            filterByString.add(filterBy.toCsrString());
        }

        // Perform the search
        SearchResult<SearchPao> results = csrService.search(filterByList,
                                                            orderBy,
                                                            startIndex,
                                                            count);

        mav.addObject("filterByString", StringUtils.join(filterByString, " and "));
        mav.addObject("orderBy", orderBy);
        mav.addObject("results", results);
        mav.addObject("fields", CsrSearchField.values());
        mav.addObject("filterByList", filterByList);

        return mav;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceHome.jsp");

        String deviceId = request.getParameter("deviceId");
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(Integer.valueOf(deviceId));

        mav.addObject("device", pao);
        mav.addObject("deviceId", deviceId);

        return mav;
    }

}
