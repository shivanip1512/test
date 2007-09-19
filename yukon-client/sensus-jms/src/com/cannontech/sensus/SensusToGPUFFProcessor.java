package com.cannontech.sensus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.amdswireless.messages.rx.AppMessageType1;
import com.amdswireless.messages.rx.AppMessageType22;
import com.amdswireless.messages.rx.AppMessageType5;
import com.amdswireless.messages.rx.DataMessage;
import com.cannontech.clientutils.YukonLogManager;

public class SensusToGPUFFProcessor extends SensusMessageHandlerBase {
	private Logger log = YukonLogManager
			.getLogger(SensusToGPUFFProcessor.class);
	
	private Set< URL > udpTargetAddressSet;
	
	private String udpAddress = "127.0.0.1";
	private int udpPort = 10000;

	public String getUdpAddress() {
		return udpAddress;
	}

	public void setUdpAddress(String ipAddress) {
		this.udpAddress = ipAddress;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	protected void processStatusMessage(AppMessageType22 message) {
		int repId = message.getRepId();
		log.debug("Processing message for repId=" + repId + ": " + message);

		boolean fault = false;
		boolean event = message.isStatusEventTransBit();
		Date toi = message.getTimestampOfIntercept();

		if ((message.isStatusEventTransBit() || isIgnoreEventBit())
				&& message.getLastEvent().isPopulated()) {
			fault = message.getLastEvent().isFaultDetected();
			long millis = toi.getTime() - message.getLastEvent().getSecondsSinceEvent() * 1000;
			Date eventDate = new Date(millis);
			// faultGenerator.writePointDataMessage(device, fault, eventDate);
		} else {
			log.info("Got supervisory message or event message without lastEvent populated");

			// CGP: This is the best info we have on the curent fault status.
			boolean latchedFault = message.isStatusLatchedFault();
			// faultGenerator.writePointDataMessage(device, latchedFault, toi);
		}

		boolean latchedFault = message.isStatusLatchedFault();
		// faultLatchGenerator.writePointDataMessage(device, latchedFault, toi);

		boolean no60 = message.isStatusNo60HzOrUnderLineCurrent();
		// no60Generator.writePointDataMessage(device, no60, toi);

		float lastBatteryVoltage = message.getLastTxBatteryVoltage();
		// batteryVoltageGenerator.writePointDataMessage(device,
		// lastBatteryVoltage, toi);

		int currentDeviceTemperature = message.getCurrentDeviceTemperature();
		// deviceTemperatureGenerator.writePointDataMessage(device,
		// currentDeviceTemperature, toi);

		boolean batterLow = message.isLowBattery();
		// batteryLowGenerator.writePointDataMessage(device, batterLow, toi);

		// Attempt to record and report the best signal strength seen. Only if
		// in Normal Mode.
		if (message.getRepeatLevel() == 0) {
			/*
			 * tgbStrength strength = tgbMap.get(repId); if(strength != null){
			 * int newDelta = message.getSigStrength() - message.getSigNoise();
			 * if(strength.sigDelta < newDelta) { // the newDelta is larger.
			 * Update the map. strength.tgbId = message.getTgbId();
			 * strength.sigDelta = newDelta; // now get them to Dispatch.
			 * tgbIdGenerator.writePointDataMessage(device, strength.tgbId,
			 * toi); sigStrengthGenerator.writePointDataMessage(device,
			 * strength.sigDelta, toi); } } else { // strength = new
			 * tgbStrength(); strength.tgbId = message.getTgbId();
			 * strength.sigDelta = message.getSigStrength() -
			 * message.getSigNoise(); tgbMap.put(repId, strength); }
			 */
			GPUFFProtocol prot = new GPUFFProtocol();
			prot.setDeviceType(GPUFFProtocol.DEVICE_TYPE_SENSUSFCI);
			prot.setSerialNumber(repId);
			prot.setCid(getCustomerId());								// This is pulled in from the configuration file
			prot.setSequence((short) message.getAppSeq());
			prot.setDvBattery((short) (lastBatteryVoltage * 100.0));
			prot.setDvTime(toi);
			prot.buildDeviceValuesMessage(fault, event);

			writePacket(prot.getBytes());
		}
	}

	protected void processBindingMessage(AppMessageType5 message) {
		String iconSerialNumber = message.getIconSerialNumber();
		int repId = message.getRepId();
		String info;
		if (!iconSerialNumber.matches(getBindingKeyRegEx())) {
			log.debug("Ignoring binding message with iconSerialNumber='"
					+ iconSerialNumber + "'");
			return;
		}
		info = "Received binding message for known serial number. iconSerialNumber="
				+ iconSerialNumber + ", repId=" + repId;
		log.info(info);

		GPUFFProtocol prot = new GPUFFProtocol();
		prot.setDeviceType(GPUFFProtocol.DEVICE_TYPE_FCI);
		prot.setSerialNumber(repId);
		prot.setCid(getCustomerId());								// This is pulled in from the configuration file
		prot.setSequence((short) message.getAppSeq());
		prot.setCmLatitude(message.getLatitude());
		prot.setCmLongitude(message.getLongitude());
		prot.setCmName(message.getIconSerialNumber());
		prot.buildDeviceCommissioningInfo();

		writePacket(prot.getBytes());
	}

	protected void processSetupMessage(AppMessageType1 message) {

	}

	public void writePacket(byte[] buf) {

        for (URL itr : udpTargetAddressSet) {
			try {
				// get a datagram socket
				DatagramSocket socket = new DatagramSocket();

				// send request
				InetAddress address = InetAddress.getByName(itr.getHost());
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, itr.getPort());
				socket.send(packet);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void setUdpTargetAddressSet(Set<URL> udpTargetAddressSet) {
		this.udpTargetAddressSet = udpTargetAddressSet;
	}
}
