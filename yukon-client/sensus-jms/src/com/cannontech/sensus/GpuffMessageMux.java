package com.cannontech.sensus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class GpuffMessageMux {
    private Thread worker;
    private Logger log = Logger.getLogger(GpuffMessageMux.class);
    private Set<URL> udpTargetAddressSet;
    private int listenPort = 62626;
    private ConfigLookup configLookup = null;

    
    private byte[] buf = new byte[2048];
    private DatagramSocket mySocket = null;
    private int ackWaitMs = 0;
    private GPUFFProtocol gpProt = new GPUFFProtocol();

    
    private class UDPGpuffAddress {
        private InetAddress address;
        private int port;
        private int sequence;
        
        public UDPGpuffAddress(InetAddress addr, int port, int seq) {
            this.setAddress(addr);
            this.setPort(port);
            this.setSequence(seq);
        }

        public void setAddress(InetAddress address) {
            this.address = address;
        }

        public InetAddress getAddress() {
            return address;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public int getSequence() {
            return sequence;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getPort() {
            return port;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((address == null) ? 0
                    : address.hashCode());
            result = prime * result + port;
            result = prime * result + sequence;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            UDPGpuffAddress other = (UDPGpuffAddress) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (address == null) {
                if (other.address != null)
                    return false;
            } else if (!address.equals(other.address))
                return false;
            if (port != other.port)
                return false;
            if (sequence != other.sequence)
                return false;
            return true;
        }

        private GpuffMessageMux getOuterType() {
            return GpuffMessageMux.this;
        }
    }
    
    private Map<UDPGpuffAddress, UDPGpuffAddress> replyNATMap = new HashMap<UDPGpuffAddress, UDPGpuffAddress>();   // The key target is the same as above in udpTargetAddressSet.  The value is the remote!    
    
    
    private DatagramSocket getSocket() {
        try {
            if (mySocket == null) {
                mySocket = new DatagramSocket(getListenPort());
            }
        } catch (SocketException e) {
            log.info("caught SocketException creating listenSocket & outSocket", e);
        }
        return mySocket;
    }

    public void startup() {
        if (worker != null) { // Make certain the worker is not already running.
            throw new RuntimeException("Worker has already been started");
        }
        log.info("GPUFFMessageMux worker thread startup.");
        // System.out.print("GPUFFMessageMux worker thread startup.\n");
        Runnable runner = new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {

                    try {
                        try {
                            // this should only block for 2.5 seconds
                            DatagramPacket pkt = readPacket();
                            if (pkt.getLength() > 0) {

                                gpProt.assignData(pkt.getData(), pkt.getLength());
                                String report = "UDP packet received: " + pkt.getAddress() + ":" + pkt.getPort() + " [" + pkt.getLength() + "] bytes." + gpProt.toHexString();
                                log.info(report);

                                UDPGpuffAddress src = new UDPGpuffAddress(pkt.getAddress(), pkt.getPort(), 0);                                
                                UDPGpuffAddress targ = replyNATMap.get(src);  // This should be my target???
                                if(targ != null) {
                                    log.info("Reply recieved from: " + pkt.getAddress() + ":" + pkt.getPort() + ". Repeating to to " + targ.getAddress() + ":" + targ.getPort());                                    
                                    sendPacket(pkt.getData(), pkt.getLength(), targ.getAddress(), targ.getPort());
                                    replyNATMap.clear();
                                } else {
                                    
                                    gpProt.decode();  //Decode the UDP packet if it is GPUFF.
                                    
                                    if (gpProt.isGpuffACKwithACKRequest()) {
                                        // This packet is an ACK with an ACK Request.  Log it as an error.
                                        log.warn("ERR ACK+ACK req rec: " + pkt.getAddress() + ":" + pkt.getPort() + " [" + pkt.getLength() + "] bytes (IGNORE)." + gpProt.toHexString());
                                    } else {
                                        
                                        // The primary function of this code is to multiplex out the UDP packet to all UDP targets.
                                        // loop over outgoing sockets and send.  
                                        // Do not send messages which ACK with the ACKRequest set.
                                        writePacket(pkt);
                                        
                                        if(gpProt.isGpuff()) {
                                            
                                            if(gpProt.isConfigDecode()) {
                                                GpuffConfig cfg = null;
                                                configLookup.reset();                           // This will make the CSV reload periodically. 
                                                cfg = configLookup.getConfigForSerial(gpProt.getSerialNumber());
                                                
                                                if(cfg != null) {
                                                    log.info("Configuration: " + cfg.toString());
                                                    if(!cfg.equals(gpProt.getConfig())) {
                                                        log.info("Configurations do not match, the device will be reconfigured");
                                                        reconfigPacket(cfg, pkt);
                                                    } else {
                                                        log.info("Configurations match, the device will NOT be reconfigured");                                                
                                                    }
                                                }
                                            }
                                            
                                            if (gpProt.isGpuffNeedingACK() && !gpProt.isGpuffACKwithACKRequest()) {
                                                ackPacket(pkt.getData(), pkt.getAddress(), pkt.getPort());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (SocketException se) {
                            System.out.print(se);
                        }
                    } catch (IOException ioe) {
                        log.warn("caught io exp in while loop", ioe);
                        System.out.print(ioe);
                        cleanupSockets();
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("Shutting down main loop");
                        break;
                    }
                }
                log.info("Main loop has completed");
            }
        };
        worker = new Thread(runner);
        worker.start();
    }

    public void destroy() {
        worker.interrupt(); // might need to check something or catch something
        // to make this work in the loop
    }

    public void sendPacket(byte[] buf, int len, InetAddress addr, int port) {

        try {
            DatagramPacket packet = new DatagramPacket(buf, len, addr, port);
            getSocket().setTrafficClass(0x04);
            getSocket().send(packet);
        } catch (IOException e) {
            System.out.print(e);
            log.warn("caught IOException in writePacket", e);
            cleanupSockets();
        }
    }

    public void ackPacket(byte[] pkt, InetAddress addr, int port) {

        try {
            // generate ACK response
            gpProt.buildACKMessage(pkt);

            try {
                Thread.sleep(getAckWaitMs());
            } catch (InterruptedException e) {
                log.debug("Sleep failed");
            }
            String report = "ACK packet     send: " + addr + ":" + port + " [" + gpProt.getBytes().length + "] bytes." + gpProt.toHexString();
            log.info(report);
            sendPacket(gpProt.getBytes(), gpProt.getBytes().length, addr, port);
        } catch (Exception e) {
            System.out.print(e);
            log.warn("caught Exception in ackPacket", e);
        }
    }

    public DatagramPacket readPacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        getSocket().setSoTimeout(2500); // 2.5 second timeout on the read?

        try {
            if (getSocket().getLocalPort() != getListenPort()) {
                log.info("Local Port has been changed for the worse! " + getSocket().getLocalPort());
            }
            getSocket().receive(packet);
        } catch (SocketTimeoutException ste) {
            // This is not an issue.... we expect this.
            packet.setLength(0); // Did not get a message!
        }

        return packet;
    }

    public void writePacket(DatagramPacket pkt) {

        byte[] writebuf = pkt.getData();
        int writeLen = pkt.getLength();
        
        Formatter hexFormatter = new Formatter();
        for (int i = 0; i < writeLen; i++) {
            hexFormatter.format(" %02X", (0x000000ff & writebuf[i]));
        }

        for (URL itr : udpTargetAddressSet) {
            try {
                
                // send request
                InetAddress address = InetAddress.getByName(itr.getHost());
                if(pkt.getAddress() != address && pkt.getPort() != itr.getPort()) {
                    DatagramPacket packet = new DatagramPacket(writebuf, writeLen, address, itr.getPort());

                    // Update our backward linkage "NAT" type work
                    UDPGpuffAddress key = new UDPGpuffAddress(address, itr.getPort(), 0);
                    UDPGpuffAddress target = new UDPGpuffAddress(pkt.getAddress(), pkt.getPort(), 0);
                    replyNATMap.put(key, target);

                    String report = "UDP packet     send: " + packet.getAddress() + ":" + packet.getPort() + " [" + packet.getLength() + "] bytes." + hexFormatter.out().toString();
                    log.info(report);

                    getSocket().setTrafficClass(0x04);
                    getSocket().send(packet);
                } else {
                    String report = "UDP packet     send: " + pkt.getAddress() + ":" + pkt.getPort() + " [MUX LOOP AVOIDANCE]";
                    log.info(report);
                }
            } catch (IOException e) {
                System.out.print(e);
                log.warn("caught IOException in writePacket", e);
                cleanupSockets();
            }
        }
    }

    public void setUdpTargetAddressSet(Set<URL> udpTargetAddressSet) {
        this.udpTargetAddressSet = udpTargetAddressSet;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public void cleanupSockets() {
        mySocket.close();
        mySocket = null;
    }

    public static void main(String[] args) {
        try {
            GpuffMessageMux mux = new GpuffMessageMux();

            mux.setListenPort(9000);
            URL url;
            url = new URL("http://127.0.0.1:51515/");
            URL url2 = new URL("http://127.0.0.1:15151/");
            Set<URL> singleton = new HashSet<URL>();
            singleton.add(url);
            singleton.add(url2);

            mux.setUdpTargetAddressSet(singleton);
            mux.startup();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setAckWaitMs(int ackWaitMs) {
        this.ackWaitMs = ackWaitMs;
    }

    public int getAckWaitMs() {
        return ackWaitMs;
    }

    public void reconfigPacket(GpuffConfig fileCfg, DatagramPacket pkt) {

        try {
            // generate RECONFIG response
            gpProt.buildReconfigPacket(fileCfg, pkt.getData());

            try {
                Thread.sleep(getAckWaitMs());
            } catch (InterruptedException e) {
                log.debug("Sleep failed");
            }
            byte[] datagrambuffer = gpProt.getBytes();
            DatagramPacket packet = new DatagramPacket(datagrambuffer, datagrambuffer.length, pkt.getAddress(), pkt.getPort());
            String report = "Reconfig pkt   send: " + packet.getAddress() + ":" + packet.getPort() + " [" + packet.getLength() + "] bytes." + gpProt.toHexString();
            log.info(report);
            getSocket().setTrafficClass(0x04);
            getSocket().send(packet);
        } catch (IOException e) {
            System.out.print(e);
            log.warn("caught IOException in writePacket", e);
            cleanupSockets();
        }
    }

    public void setConfigLookup(ConfigLookup configLookup) {
        this.configLookup = configLookup;
    }

}
