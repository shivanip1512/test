package com.cannontech.yukon.api.amr.endpoint;

import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Node;

import com.cannontech.amr.device.ProfileAttributeChannel;
import com.cannontech.amr.profileCollection.service.ProfileCollectionService;
import com.cannontech.amr.toggleProfiling.service.ProfilingService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.pao.service.PaoSelectionService.PaoSelectionData;
import com.cannontech.common.pao.service.PaoSelectionUtil;
import com.cannontech.common.token.Token;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Sets;

@Endpoint
public class ProfileCollectionRequestEndpoint {
    private final static Namespace ns = YukonXml.getYukonNamespace();

    private final static Duration minOffset = Period.minutes(59).toStandardDuration();

    @Autowired private PaoSelectionService paoSelectionService;
    @Autowired private ProfilingService toggleProfilingService;
    @Autowired private AttributeService attributeService;
    @Autowired private ProfileCollectionService profileCollectionService;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="profileCollectionRequest")
    public Element invoke(Element requestElem, YukonUserContext userContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(requestElem, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element responseElem = new Element("profileCollectionResponse", ns);

        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(requestElem);

        Node startNode = requestTemplate.evaluateAsNode("/y:profileCollectionRequest/y:startCollection");
        if (startNode != null) {
            handleStartCollection(responseElem, startNode, userContext);
            return responseElem;
        }

        Node stopNode = requestTemplate.evaluateAsNode("/y:profileCollectionRequest/y:stopCollection");
        if (stopNode != null) {
            handleStopCollection(responseElem, stopNode, userContext);
            return responseElem;
        }

        Node cancelNode = requestTemplate.evaluateAsNode("/y:profileCollectionRequest/y:cancelCollection");
        if (cancelNode != null) {
            handleCancelCollection(responseElem, cancelNode, userContext);
            return responseElem;
        }

        responseElem.addContent(XMLFailureGenerator.makeSimple("InvalidXml",
            "One of startCollection or stopCollection is required."));
        return responseElem;
    }

    private void handleStartCollection(Element responseElem, Node startCollectionNode,
                                       YukonUserContext userContext) {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(startCollectionNode);

        Instant now = new Instant();
        Instant start = template.evaluateAsInstant("@start", now);
        Instant stop = template.evaluateAsInstant("@stop");

        if (stop != null && !start.isBefore(stop.minus(Period.hours(1).toStandardDuration()))) {
            responseElem.addContent(XMLFailureGenerator.makeSimple("InvalidData",
                    "Start date must be at least one hour before stop date."));
            return;
        }

        boolean doingPast = start.isBefore(now.minus(minOffset));
        boolean schedulingFuture = stop == null || stop.isAfter(now.plus(minOffset));
        if (!doingPast && !schedulingFuture) {
            responseElem.addContent(XMLFailureGenerator.makeSimple("InvalidData",
                    "Start date must be at least one hour in the past and/or stop date must be " +
                    "at least one hour in the future."));
            return;
        }

        ProfileAttributeChannel channel = parseChannel(template, responseElem);
        if (channel == null) {
            // Error was added by parseChannel.
            return;
        }

        Node paoNode = template.evaluateAsNode("y:paos");
        PaoSelectionData paoData = paoSelectionService.selectPaoIdentifiersByType(paoNode);
        paoSelectionService.addLookupErrorsNode(paoData, responseElem);

        int channelNum = channel.getChannel();

        // Build list of devices with valid point while we (maybe) set up future collection.
        Set<PaoIdentifier> devices = Sets.newHashSet();
        Instant futureCollectionStart = start;
        if (futureCollectionStart.isBefore(now)) {
            futureCollectionStart = now;
        }
        Element invalidChannelElem = new Element("invalidChannel", ns);
        boolean hasInvalidChannelErrors = false;
        boolean didScheduleFuture = false;
        for (PaoIdentifier device : paoData.getPaoDataById().keySet()) {
            if (!checkChannelForDevice(device, channel, invalidChannelElem)) {
                // Error added by checkChannelForDevice
                hasInvalidChannelErrors = true;
                continue;
            }

            devices.add(device);
            if (schedulingFuture) {
                int paoId = device.getPaoId();

                toggleProfilingService.disableScheduledStart(paoId, channelNum);
                if (futureCollectionStart.isBefore(now.plus(minOffset))) {
                    toggleProfilingService.startProfilingForDevice(paoId, channelNum);
                } else {
                    toggleProfilingService.stopProfilingForDevice(paoId, channelNum);
                    toggleProfilingService.scheduleStartProfilingForDevice(paoId, channelNum,
                        futureCollectionStart.toDate(), userContext);
                }

                toggleProfilingService.disableScheduledStop(paoId, channelNum);
                if (stop != null) {
                    toggleProfilingService.scheduleStopProfilingForDevice(paoId, channelNum,
                        stop.toDate(), userContext);
                }

                didScheduleFuture = true;
            }
        }
        if (didScheduleFuture) {
            Element futureElem = new Element("futureScheduleUpdated", ns);
            responseElem.addContent(futureElem);
        }

        if (doingPast) {
            Instant pastCollectionStop = stop;
            if (pastCollectionStop == null || pastCollectionStop.isAfter(now)) {
                pastCollectionStop = now;
            }
            Token token = profileCollectionService.createJob(devices, channelNum, start,
                                                             pastCollectionStop, userContext);
            Element tokenElem = new Element("token", ns);
            tokenElem.setAttribute("value", token.getString());
            responseElem.addContent(tokenElem);
        }

        if (hasInvalidChannelErrors) {
            responseElem.addContent(invalidChannelElem);
        }
    }

    private void handleStopCollection(Element responseElem, Node stopCollectionNode,
                                      YukonUserContext userContext) {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(stopCollectionNode);

        Instant now = new Instant();
        Instant stop = template.evaluateAsInstant("@stop");
        if (stop != null && stop.isBefore(now.minus(minOffset))) {
            responseElem.addContent(XMLFailureGenerator.makeSimple("InvalidData",
                    "Stop time must be empty (stop now) or be in the future."));
            return;
        }

        ProfileAttributeChannel channel = parseChannel(template, responseElem);
        if (channel == null) {
            // Error was added by parseChannel.
            return;
        }

        Node paoNode = template.evaluateAsNode("y:paos");
        PaoSelectionData paoData = paoSelectionService.selectPaoIdentifiersByType(paoNode);
        paoSelectionService.addLookupErrorsNode(paoData, responseElem);

        int channelNum = channel.getChannel();

        Element invalidChannelElem = new Element("invalidChannel", ns);
        boolean hasInvalidChannelErrors = false;
        boolean didScheduleFuture = false;
        for (PaoIdentifier device : paoData.getPaoDataById().keySet()) {
            if (!checkChannelForDevice(device, channel, invalidChannelElem)) {
                // Error added by checkChannelForDevice
                hasInvalidChannelErrors = true;
                continue;
            }

            int paoId = device.getPaoId();

            Instant scheduledStart = toggleProfilingService.getScheduledStart(paoId, channelNum);
            boolean isRunningNow = toggleProfilingService.isProfilingOnNow(paoId, channelNum);
            // This is a "stop collection" so stop == null means stop now.
            if (stop == null || scheduledStart != null && !scheduledStart.isBefore(stop.minus(minOffset))) {
                toggleProfilingService.disableScheduledStart(paoId, channelNum);
            }

            toggleProfilingService.disableScheduledStop(paoId, channelNum);

            if (stop == null) {
                toggleProfilingService.stopProfilingForDevice(paoId, channelNum);
            } else if (scheduledStart != null || isRunningNow) {
                toggleProfilingService.scheduleStopProfilingForDevice(paoId, channelNum,
                                                                      stop.toDate(), userContext);
            }

            didScheduleFuture = true;
        }

        if (didScheduleFuture) {
            Element futureElem = new Element("futureScheduleUpdated", ns);
            responseElem.addContent(futureElem);
        }

        if (hasInvalidChannelErrors) {
            responseElem.addContent(invalidChannelElem);
        }
    }

    private void handleCancelCollection(Element responseElem, Node cancelNode,
                                        YukonUserContext userContext) {
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(cancelNode);
        String tokenStr = template.evaluateAsString("y:token/@value");
        Token token = new Token(tokenStr);
        try {
            profileCollectionService.cancelJob(token, userContext);
            responseElem.addContent(new Element("canceled", ns));
        } catch (IllegalArgumentException iae) {
            responseElem.addContent(new Element("unknownToken", ns));
        }
    }

    /**
     * Grab the channel from the given node.  Adds an error to responseElem if the channel isn't
     * valid.
     */
    private ProfileAttributeChannel parseChannel(SimpleXPathTemplate template, Element responseElem) {
        String channelStr = template.evaluateAsString("@channel");
        if (channelStr == null) {
            return ProfileAttributeChannel.LOAD_PROFILE;
        }
        ProfileAttributeChannel channel = null;
        try {
            channel = ProfileAttributeChannel.valueOf(channelStr);
        } catch (IllegalArgumentException iae) {
            responseElem.addContent(XMLFailureGenerator.makeSimple("InvalidData",
                channelStr + " is not a valid channel."));
        }
        return channel;
    }

    /**
     * Check that the device has an attribute for the given channel.  Adds an error to the
     * responseElem if not.  Returns true if the channel is valid for the given channel.
     */
    private boolean checkChannelForDevice(YukonPao device, ProfileAttributeChannel channel,
                                          Element invalidChannelElem) {
        try {
            attributeService.getPointForAttribute(device, channel.getAttribute());
        } catch (IllegalUseOfAttribute iuoae) {
            invalidChannelElem.addContent(PaoSelectionUtil.makePaoElement(device));
            return false;
        }
        return true;
    }
}
