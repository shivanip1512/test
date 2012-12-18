package com.cannontech.web.common.vee.review;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	
	private RphTagUiDao rphTagUiDao;
	private RawPointHistoryDao rawPointHistoryDao;
	private ValidationHelperService validationHelperService;
	
	private static int IDEAL_PAGE_COUNT = 15;
	private static int OVER_RETRIEVE_FACTOR = 3;

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
	
	@RequestMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("vee/review/review.jsp");
        int afterPaoId = ServletRequestUtils.getIntParameter(request, "afterPaoId", 0);

        // get review points
        List<RphTag> selectedTags = getSelectedTags(request);
        List<ReviewPoint> allReviewPoints = rphTagUiDao.getReviewPoints(afterPaoId, IDEAL_PAGE_COUNT * OVER_RETRIEVE_FACTOR, selectedTags, false);
        
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
        // add keepers until the next distinct device and count >= IDEAL_PAGE_COUNT
        int lastPaoId = 0;
        List<ReviewPoint> keepReviewPoints = new ArrayList<ReviewPoint>();
        for (long changeId : commonChangeIdReviewPoints.keySet()) {
        	
        	List<ReviewPoint> rpList = commonChangeIdReviewPoints.get(changeId);
        	ReviewPoint keeper = reviewPointingOrdering.min(rpList);
        	
        	int keeperPaoId = keeper.getDisplayablePao().getPaoIdentifier().getPaoId();
        	if (lastPaoId > 0 && keeperPaoId != lastPaoId && keepReviewPoints.size() >= IDEAL_PAGE_COUNT) {
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
        int finalPaoId = 0;
        Map<String, List<ExtendedReviewPoint>> groupedExtendedReviewPoints = new LinkedHashMap<String, List<ExtendedReviewPoint>>();
        for (ExtendedReviewPoint extRp : extendedReviewPoints) {
        	
        	String nameKey = extRp.getReviewPoint().getDisplayablePao().getName();
        	if (groupedExtendedReviewPoints.get(nameKey) == null) {
        		List<ExtendedReviewPoint> extRps = new ArrayList<ExtendedReviewPoint>();
        		groupedExtendedReviewPoints.put(nameKey, extRps);
        	}
        	groupedExtendedReviewPoints.get(nameKey).add(extRp);
        	
        	finalPaoId = extRp.getReviewPoint().getDisplayablePao().getPaoIdentifier().getPaoId();
        }
        
        // tag counts
        Map<RphTag, Integer> tagCounts = rphTagUiDao.getAllValidationTagCounts();
        mav.addObject("tagCounts", tagCounts);
        
        // mav
        mav.addObject("afterPaoId", afterPaoId);
        mav.addObject("nextPaoId", finalPaoId);
        addDisplayTypesToMav(selectedTags, tagCounts, mav);
        mav.addObject("groupedExtendedReviewPoints", groupedExtendedReviewPoints);
        
        return mav;
    }
	
	@RequestMapping
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, LiteYukonUser user) throws Exception {
        
        ModelAndView mav = new ModelAndView("redirect:home");
        int afterPaoId = ServletRequestUtils.getIntParameter(request, "afterPaoId", 0);
        
        // gather changeIds
        List<Long> deleteChangeIds = Lists.newArrayList();
        List<Long> acceptChangeIds = Lists.newArrayList();
        
        Map<String, String> actionParameters = ServletUtil.getStringParameters(request, "ACTION_");
        for (String changeIdStr : actionParameters.keySet()) {
        	
        	String actionTypeStr = actionParameters.get(changeIdStr);
        	if (!StringUtils.isBlank(actionTypeStr)) {
	        	ActionType actionType = ActionType.valueOf(actionTypeStr);
	        	if (ActionType.DELETE.equals(actionType)) {
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
        
        // mav
        mav.addObject("afterPaoId", afterPaoId);
        for (RphTag tag : getSelectedTags(request)) {
        	mav.addObject(tag.name(), true);
        }
        
        return mav;
	}
	
	private void addDisplayTypesToMav(List<RphTag> selectedTags, Map<RphTag, Integer> tagCounts, ModelAndView mav) {
		
		List<DisplayType> displayTypes = new ArrayList<DisplayType>();
		for (RphTag tag : RphTag.getAllValidation()) {
			boolean checked = false;
			if (selectedTags.contains(tag)) {
				checked = true;
			}
			displayTypes.add(new DisplayType(tag, checked, tagCounts.get(tag)));
		}
		
		mav.addObject("displayTypes", displayTypes);
	}
	
	public class DisplayType {
		
		private RphTag rphTag;
		private boolean checked;
		private int count;
		
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
	
	public class ExtendedReviewPoint {
		
		private ReviewPoint reviewPoint;
		private PointValueHolder prevPointValue;
		private PointValueHolder nextPointValue;
		private List<RphTag> otherTags;
		
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
