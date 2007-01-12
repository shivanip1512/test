package com.amdswireless.messages.rx;

// This interface is used to define the various application
// messages that are transmitted as Andorian messages in 
// the AMDS system.
// This class sould be implemented by classes that parse and
// make available the individual types of application messages
// wrapped in the Andorian stream (setup, electric meter read,
// gas meter read, etc).
public interface AppMessage {
	public String getMsgClass();
}

