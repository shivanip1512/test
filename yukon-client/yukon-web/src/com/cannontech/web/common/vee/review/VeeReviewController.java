package com.cannontech.web.common.vee.review;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.services.validation.dao.RphTagDao;
import com.cannontech.services.validation.dao.RphTagUiDao;
import com.cannontech.services.validation.model.ReviewPoint;
import com.cannontech.services.validation.model.RphTag;
import com.cannontech.util.ServletUtil;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/veeReview/*")
public class VeeReviewController {
	
	private RphTagDao rphTagDao;
	private RphTagUiDao rphTagUiDao;
	private RawPointHistoryDao rawPointHistoryDao;
	
	private static int IDEAL_PAGE_COUNT = 20;
	private static int OVER_RETRIEVE_FACTOR = 5;

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
        int prevPaoId = ServletRequestUtils.getIntParameter(request, "prevPaoId", 0);

        // get review points
        List<RphTag> selectedTags = getSelectedTags(request);
        List<ReviewPoint> allReviewPoints = rphTagUiDao.getReviewPoints(prevPaoId, IDEAL_PAGE_COUNT * OVER_RETRIEVE_FACTOR, selectedTags, false);
        
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
        boolean hasMore = true;
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
        		if (i == changeIdList.size() - 1) {
        			hasMore = false;
        		}
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
        
        if (!hasMore) {
        	finalPaoId = 0;
        }
        
        // mav
        mav.addObject("prevPaoId", finalPaoId);
        mav.addObject("hasMore", hasMore);
        addSelectedTagsToMav(selectedTags, mav);
        mav.addObject("groupedExtendedReviewPoints", groupedExtendedReviewPoints);
        
        return mav;
    }
	
	@RequestMapping
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("redirect:home");
        int prevPaoId = ServletRequestUtils.getIntParameter(request, "prevPaoId", 0);
        
        // gather changeIds
        List<Integer> deleteChangeIds = new ArrayList<Integer>();
        List<Integer> acceptChangeIds = new ArrayList<Integer>();
        
        Map<String, String> actionParameters = ServletUtil.getStringParameters(request, "ACTION_");
        for (String changeIdStr : actionParameters.keySet()) {
        	
        	int changeId = Integer.valueOf(changeIdStr);
        	
        	String actionTypeStr = actionParameters.get(changeIdStr);
        	if (!StringUtils.isBlank(actionTypeStr)) {
	        	ActionType actionType = ActionType.valueOf(actionTypeStr);
	        	if (ActionType.DELETE.equals(actionType)) {
	        		deleteChangeIds.add(changeId);
	        	} else if (ActionType.ACCEPT.equals(actionType)) {
	        		acceptChangeIds.add(changeId);
	        	}
        	}
        }
        
        // delete
        for (int deleteChangeId : deleteChangeIds) {
        	rawPointHistoryDao.deleteValue(deleteChangeId);
        }
        
        // accept
        for (int acceptChangeId : acceptChangeIds) {
        	rphTagDao.insertTag(acceptChangeId, RphTag.OK);
        }
        
        // mav
        mav.addObject("prevPaoId", prevPaoId);
        addSelectedTagsToMav(getSelectedTags(request), mav);
        
        return mav;
	}
	
	private void addSelectedTagsToMav(List<RphTag> selectedTags, ModelAndView mav) {
		
		Map<String, RphTag> allTagsMap = new HashMap<String, RphTag>();
		
		for (RphTag tag : RphTag.values()) {
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

		for (RphTag rphTag : RphTag.values()) {
			
			boolean tagChecked = ServletRequestUtils.getBooleanParameter(request, rphTag.name(), false);
			if (tagChecked) {
				tags.add(rphTag);
			}
		}
		
		if (tags.size() == 0) {
			tags.addAll(Arrays.asList(RphTag.values()));
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
}
