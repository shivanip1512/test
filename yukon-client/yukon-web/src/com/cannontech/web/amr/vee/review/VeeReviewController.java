package com.cannontech.web.amr.vee.review;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.service.ValidationHelperService;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.AdjacentPointValues;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
@RequestMapping("/veeReview/*")
public class VeeReviewController {
    
    @Autowired private RphTagUiDao rphTagUiDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private ValidationHelperService validationHelperService;
    
    private static enum ActionType {
        DELETE,
        ACCEPT;
    }
    
    private static Ordering<ReviewPoint> reviewPointingOrdering = Ordering.natural().onResultOf(new Function<ReviewPoint, Comparable<RphTag>>() {
        @Override
        public Comparable<RphTag> apply(ReviewPoint arg0) {
            return arg0.getRphTag();
        }
    });
    
    private void setUpModel(HttpServletRequest request, ModelMap model, PagingParameters pagingParameters) {
        
        List<RphTag> selectedTags = getSelectedTags(request);
        SearchResults<ReviewPoint> searchResults = rphTagUiDao.getReviewPoints(pagingParameters, selectedTags);
        List<ReviewPoint> allReviewPoints = searchResults.getResultList();
        
        // group by device for the purpose of the narrowing step, this way the changeIds used as (linked) map keys stay in device order
        ListMultimap<String, ReviewPoint> rpsByName = LinkedListMultimap.create();
        for (ReviewPoint rp : allReviewPoints) {
            rpsByName.put(rp.getDisplayablePao().getName(), rp);
        }
        
        // narrow - group by changeId
        // loop over rpsByName by key to keep order intact
        ListMultimap<Long, ReviewPoint> commonChangeIdReviewPoints = LinkedListMultimap.create();
        for (String nameKey : rpsByName.keySet()) {
            List<ReviewPoint> rpsForName = rpsByName.get(nameKey);
            for (ReviewPoint rp : rpsForName) {
                commonChangeIdReviewPoints.put(rp.getChangeId(), rp);
            }
        }
        
        // narrow - if changeId has multiple review points only keep the one with highest display precedence
        // add keepers until the next distinct device and count >= PAGE_COUNT
        int lastPaoId = 0;
        List<ReviewPoint> keepReviewPoints = new ArrayList<ReviewPoint>();
        for (long changeId : commonChangeIdReviewPoints.keySet()) {
            
            List<ReviewPoint> rpList = commonChangeIdReviewPoints.get(changeId);
            ReviewPoint keeper = reviewPointingOrdering.min(rpList);
            
            int keeperPaoId = keeper.getDisplayablePao().getPaoIdentifier().getPaoId();
            if (lastPaoId > 0 && keeperPaoId != lastPaoId && keepReviewPoints.size() >= 
                    pagingParameters.getItemsPerPage()) {
                break;
            }
            
            keepReviewPoints.add(keeper);
            lastPaoId = keeperPaoId;
        }
        
        // make extended
        List<ExtendedReviewPoint> extendedReviewPoints = new ArrayList<ExtendedReviewPoint>();
        for (ReviewPoint rp : keepReviewPoints) {
            
            List<RphTag> otherTags = Lists.newArrayList();
            List<ReviewPoint> rpsWithCommonChangeId = commonChangeIdReviewPoints.get(rp.getChangeId());
            for (ReviewPoint rpCommon : rpsWithCommonChangeId) {
                if (!rpCommon.equals(rp)) {
                    otherTags.add(rpCommon.getRphTag());
                }
            }
            
            AdjacentPointValues adjacentPointValues = rawPointHistoryDao.getAdjacentPointValues(rp.getPointValue());
            ExtendedReviewPoint extRp = new ExtendedReviewPoint(rp, adjacentPointValues.getPreceding(), adjacentPointValues.getSucceeding(), otherTags);
            extendedReviewPoints.add(extRp);
        }
        
        // group by device for display
        Map<String, List<ExtendedReviewPoint>> groupedExtendedReviewPoints = new LinkedHashMap<String, List<ExtendedReviewPoint>>();
        for (ExtendedReviewPoint extRp : extendedReviewPoints) {
            
            String nameKey = extRp.getReviewPoint().getDisplayablePao().getName();
            if (groupedExtendedReviewPoints.get(nameKey) == null) {
                List<ExtendedReviewPoint> extRps = new ArrayList<ExtendedReviewPoint>();
                groupedExtendedReviewPoints.put(nameKey, extRps);
            }
            groupedExtendedReviewPoints.get(nameKey).add(extRp);
            
        }
        
        // tag counts
        Map<RphTag, Integer> tagCounts = rphTagUiDao.getAllValidationTagCounts();
        model.addAttribute("tagCounts", tagCounts);
        
        // mav
        int sumOfSelectedTagCounts = addDisplayTypesToModel(selectedTags, tagCounts, model);
        model.addAttribute("groupedExtendedReviewPoints", groupedExtendedReviewPoints);
        
        //for pagination
        SearchResults<ExtendedReviewPoint> pagedRows = SearchResults.pageBasedForSublist(extendedReviewPoints, 
                pagingParameters.getPage(), pagingParameters.getItemsPerPage(),
                sumOfSelectedTagCounts);
        model.addAttribute("result", pagedRows);
        
    }
    
    @RequestMapping("home")
    public String home(HttpServletRequest request, ModelMap model, PagingParameters pagingParameters) {
        setUpModel(request, model, pagingParameters);
        // get review points
        return "vee/review/review.jsp";
    }
    
    @RequestMapping("reviewTable")
    public String reviewTable(HttpServletRequest request, ModelMap model, PagingParameters pagingParameters) {
        setUpModel(request,  model, pagingParameters);
        return "vee/review/reviewTable.jsp";
    }
    @RequestMapping("save")
    public String save(HttpServletRequest request, ModelMap model, LiteYukonUser user, PagingParameters pagingParameters) 
            throws NumberFormatException {
        // gather changeIds
        List<Long> deleteChangeIds = Lists.newArrayList();
        List<Long> acceptChangeIds = Lists.newArrayList();
        
        Map<String, String> actionParameters = ServletUtil.getStringParameters(request, "ACTION_");
        for (String changeIdStr : actionParameters.keySet()) {
            
            String actionTypeStr = actionParameters.get(changeIdStr);
            if (!StringUtils.isBlank(actionTypeStr)) {
                ActionType actionType = ActionType.valueOf(actionTypeStr);
                if (actionType == ActionType.DELETE) {
                    deleteChangeIds.add(Long.valueOf(changeIdStr));
                } else if (ActionType.ACCEPT.equals(actionType)) {
                    acceptChangeIds.add(Long.valueOf(changeIdStr));
                }
            }
        }
        
        // delete
        for (long deleteChangeId : deleteChangeIds) {
            validationHelperService.deleteRawPointHistoryRow(deleteChangeId, user);
        }
        
        // accept
        for (long acceptChangeId : acceptChangeIds) {
            validationHelperService.acceptRawPointHistoryRow(acceptChangeId, user);
        }

        setUpModel(request,  model, pagingParameters);
        return "vee/review/reviewTable.jsp";
    }
    
    @RequestMapping("advancedProcessing")
    public String advancedProcessing(HttpServletRequest request, ModelMap model, PagingParameters pagingParameters) {
        return "vee/review/menu.jsp";
    }
    
    private int addDisplayTypesToModel(List<RphTag> selectedTags, Map<RphTag, Integer> tagCounts, ModelMap model) {
        int sumOfSelectedTagCounts = 0;
        List<DisplayType> displayTypes = new ArrayList<DisplayType>();
        for (RphTag tag : RphTag.getAllValidation()) {
            boolean checked = false;
            if (selectedTags.contains(tag)) {
                checked = true;
                sumOfSelectedTagCounts += tagCounts.get(tag);
            }
            displayTypes.add(new DisplayType(tag, checked, tagCounts.get(tag)));
        }
        
        model.addAttribute("totalTagCount", sumOfSelectedTagCounts);
        model.addAttribute("displayTypes", displayTypes);
        
        return sumOfSelectedTagCounts;
    }
    
    public final static class DisplayType {
        
        private final RphTag rphTag;
        private final boolean checked;
        private final int count;
        
        public DisplayType(RphTag rphTag, boolean checked, int count) {
            this.rphTag = rphTag;
            this.checked = checked;
            this.count = count;
        }
        
        public RphTag getRphTag() {
            return rphTag;
        }
        public boolean isChecked() {
            return checked;
        }
        public int getCount() {
            return count;
        }
    }
    
    private List<RphTag> getSelectedTags(HttpServletRequest request) {
        
        List<RphTag> tags = new ArrayList<RphTag>();

        for (RphTag rphTag : RphTag.getAllValidation()) {
            
            boolean tagChecked = ServletRequestUtils.getBooleanParameter(request, rphTag.name(), false);
            if (tagChecked) {
                tags.add(rphTag);
            }
        }
        
        if (tags.size() == 0) {
            tags.addAll(RphTag.getAllValidation());
        }
        
        return tags;
    }
    
    public final static class ExtendedReviewPoint {
        
        private final ReviewPoint reviewPoint;
        private final PointValueHolder prevPointValue;
        private final PointValueHolder nextPointValue;
        private final List<RphTag> otherTags;
        
        public ExtendedReviewPoint(ReviewPoint reviewPoint, PointValueHolder prevPointValue, PointValueHolder nextPointValue, List<RphTag> otherTags) {
            this.reviewPoint = reviewPoint;
            this.prevPointValue = prevPointValue;
            this.nextPointValue = nextPointValue;
            this.otherTags = otherTags;
        }
        
        public ReviewPoint getReviewPoint() {
            return reviewPoint;
        }
        public PointValueHolder getPrevPointValue() {
            return prevPointValue;
        }
        public PointValueHolder getNextPointValue() {
            return nextPointValue;
        }
        public List<RphTag> getOtherTags() {
            return otherTags;
        }
    }
    
    @Autowired
    public void setRphTagUiDao(RphTagUiDao rphTagUiDao) {
        this.rphTagUiDao = rphTagUiDao;
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
    
    @Autowired
    public void setValidationHelperService(ValidationHelperService validationHelperService) {
        this.validationHelperService = validationHelperService;
    }
}
