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

public class SensusToGPUFFProcessor extends SensusMessageHandlerBase {
	private Logger log = Logger.getLogger(SensusToGPUFFProcessor.class);
	private Set< URL > udpTargetAddressSet;
	
	protected void processStatusMessage(AppMessageType22 message) {
		int repId = message.getRepId();

		log.debug("Processing message for repId=" + repId + ": " + message);

		boolean fault = false;
		boolean event = message.isStatusEventTransBit();
		Date toi = message.getTimestampOfIntercept();
		boolean no60 = message.isStatusNo60HzOrUnderLineCurrent();
		float lastBatteryVoltage = message.getLastTxBatteryVoltage();

		GPUFFProtocol prot = new GPUFFProtocol();
		prot.setDeviceType(GPUFFProtocol.DEVICE_TYPE_SENSUSFCI);
		prot.setSerialNumber(repId);
		prot.setCid(getCustomerId());								// This is pulled in from the configuration file
		prot.setSequence((short) message.getAppSeq());
		prot.setDvBattery(lastBatteryVoltage);
		prot.setDvTime(toi);
		prot.buildDeviceValuesMessage(fault, event, no60);

		writePacket(prot.getBytes());
	}

	protected void processBindingMessage(AppMessageType5 message) {
		int repId = message.getRepId();

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
