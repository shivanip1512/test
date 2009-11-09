package com.cannontech.web.common.vee.review;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.cannontech.common.events.loggers.VeeReviewEventLogService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.validation.dao.RphTagDao;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.ReviewPoint;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
@RequestMapping("/veeReview/*")
public class VeeReviewController {
	
	private RphTagDao rphTagDao;
	private RphTagUiDao rphTagUiDao;
	private RawPointHistoryDao rawPointHistoryDao;
	private VeeReviewEventLogService veeReviewEventLogService;
	
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
        ListMultimap<Integer, ReviewPoint> commonChangeIdReviewPoints = LinkedListMultimap.create();
        for (String nameKey : rpsByName.keySet()) {
        	List<ReviewPoint> rpsForName = rpsByName.get(nameKey);
	        for (ReviewPoint rp : rpsForName) {
	        	commonChangeIdReviewPoints.put(rp.getChangeId(), rp);
	        }
        }
        
        // narrow - if changeId has multiple review points only keep the one with highest display precedence
        // add keepers until the next distinct device and count >= IDEAL_PAGE_COUNT
        List<ReviewPoint> keepReviewPoints = new ArrayList<ReviewPoint>();
        List<Integer> changeIdList = new ArrayList<Integer>(commonChangeIdReviewPoints.keySet());
        for (int i = 0; i < changeIdList.size(); i++) {
        	
        	int changeId = changeIdList.get(i);
        	List<ReviewPoint> rpList = commonChangeIdReviewPoints.get(changeId);
        	ReviewPoint keeper = reviewPointingOrdering.min(rpList);
        	keepReviewPoints.add(keeper);
        	
        	int keeperPaoId = keeper.getDisplayablePao().getPaoIdentifier().getPaoId();
        	int nextPaoId = 0;
        	if (i +1 < changeIdList.size()) {
        		int nextChangeId = changeIdList.get(i + 1);
        		List<ReviewPoint> nextRpList = commonChangeIdReviewPoints.get(nextChangeId);
        		nextPaoId = nextRpList.get(0).getDisplayablePao().getPaoIdentifier().getPaoId();
        	}
        	
        	if (nextPaoId != keeperPaoId && keepReviewPoints.size() >= IDEAL_PAGE_COUNT) {
        		break;
        	}
        }
        
        // make extended
        List<ExtendedReviewPoint> extendedReviewPoints = new ArrayList<ExtendedReviewPoint>();
        for (ReviewPoint rp : keepReviewPoints) {
        	
        	List<RphTag> otherTags = new ArrayList<RphTag>();
        	List<ReviewPoint> rpsWithCommonChangeId = commonChangeIdReviewPoints.get(rp.getChangeId());
        	for (ReviewPoint rpCommon : rpsWithCommonChangeId) {
        		if (!rpCommon.equals(rp)) {
        			otherTags.add(rpCommon.getRphTag());
        		}
        	}
        	
        	List<PointValueQualityHolder> adjacentPointValues = rawPointHistoryDao.getAdjacentPointValues(rp.getChangeId(), -1, 1);
        	ExtendedReviewPoint extRp = new ExtendedReviewPoint(rp, adjacentPointValues.get(0), adjacentPointValues.get(1), otherTags);
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
        Map<RphTag, Integer> tagCountsOrg = rphTagUiDao.getTagCounts();
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();
        for (RphTag tag : tagCountsOrg.keySet()) {
        	tagCounts.put(tag.name(), tagCountsOrg.get(tag));
        }
        mav.addObject("tagCounts", tagCounts);
        
        // mav
        mav.addObject("afterPaoId", afterPaoId);
        mav.addObject("nextPaoId", finalPaoId);
        addSelectedTagsToMav(selectedTags, mav);
        mav.addObject("groupedExtendedReviewPoints", groupedExtendedReviewPoints);
        
        return mav;
    }
	
	@RequestMapping
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("redirect:home");
        int afterPaoId = ServletRequestUtils.getIntParameter(request, "afterPaoId", 0);
        
        // gather changeIds
        List<String[]> deleteChangeIdPointIdStrs = new ArrayList<String[]>();
        List<String[]> acceptChangeIdPointIdStrs = new ArrayList<String[]>();
        
        Map<String, String> actionParameters = ServletUtil.getStringParameters(request, "ACTION_");
        for (String changeIdPointIdStr : actionParameters.keySet()) {
        	
        	String[] changeIdPointIdStrParts = StringUtils.split(changeIdPointIdStr, "_");
        	String actionTypeStr = actionParameters.get(changeIdPointIdStr);
        	if (!StringUtils.isBlank(actionTypeStr)) {
	        	ActionType actionType = ActionType.valueOf(actionTypeStr);
	        	if (ActionType.DELETE.equals(actionType)) {
	        		deleteChangeIdPointIdStrs.add(changeIdPointIdStrParts);
	        	} else if (ActionType.ACCEPT.equals(actionType)) {
	        		acceptChangeIdPointIdStrs.add(changeIdPointIdStrParts);
	        	}
        	}
        }
        
        // delete
        for (String[] deleteChangeIdPointIdStr : deleteChangeIdPointIdStrs) {
        	deleteAction(Integer.valueOf(deleteChangeIdPointIdStr[0]), Integer.valueOf(deleteChangeIdPointIdStr[1]));
        }
        
        // accept
        for (String[] acceptChangeIdPointIdStr : acceptChangeIdPointIdStrs) {
        	
        	acceptAction(Integer.valueOf(acceptChangeIdPointIdStr[0]), Integer.valueOf(acceptChangeIdPointIdStr[1]));
        }
        
        // mav
        mav.addObject("afterPaoId", afterPaoId);
        addSelectedTagsToMav(getSelectedTags(request), mav);
        
        return mav;
	}
	
	private void deleteAction(int changeId, int pointId) {
		
		rawPointHistoryDao.deleteValue(changeId);
		veeReviewEventLogService.deletePointValue(changeId, pointId);
	}
	
	private void acceptAction(int changeId, int pointId) {
		
		rphTagDao.insertTag(changeId, RphTag.OK);
		veeReviewEventLogService.acceptPointValue(changeId, pointId);
		
		PointValueQualityHolder pointValueQualityHolder = rawPointHistoryDao.getPointValueQualityForChangeId(changeId);
    	if (pointValueQualityHolder.getPointQuality().equals(PointQuality.Questionable)) {
    		rawPointHistoryDao.changeQuality(changeId, PointQuality.Normal);
    		veeReviewEventLogService.updateQuestionableQuality(changeId, pointId);
    	}
	}
	
	private void addSelectedTagsToMav(List<RphTag> selectedTags, ModelAndView mav) {
		
		Map<String, RphTag> allTagsMap = new HashMap<String, RphTag>();
		
		for (RphTag tag : RphTag.getAllValidation()) {
			mav.addObject(tag.name(), false);
			if (selectedTags.contains(tag)) {
				mav.addObject(tag.name(), true);
			}
			
			allTagsMap.put(tag.name(), tag);
		}
		
		mav.addObject("allTagsMap", allTagsMap);
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
	public void setRphTagDao(RphTagDao rphTagDao) {
		this.rphTagDao = rphTagDao;
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
	public void setVeeReviewEventLogService(VeeReviewEventLogService veeReviewEventLogService) {
		this.veeReviewEventLogService = veeReviewEventLogService;
	}
}
