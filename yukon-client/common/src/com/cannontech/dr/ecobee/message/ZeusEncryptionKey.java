package com.cannontech.dr.ecobee.message;

import org.joda.time.Instant;

public class ZeusEncryptionKey {

	private String privateKey;
	private String publicKey;
	private Instant timestamp;

	public ZeusEncryptionKey() {
	}

	public ZeusEncryptionKey(String privateKey, String publicKey, Instant timestamp) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.timestamp = timestamp;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}
