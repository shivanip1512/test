package com.amdswireless.messages.rx;


public class AppMessageTypeETier7 extends AppMessageTypeE implements AppMessage, java.io.Serializable {

    /**
     * 
     */
    private transient static final long serialVersionUID = 1L;
    private int   subtier=0;
    private float serviceFrequency;
    private float phaseAAmps;
    private float phaseBAmps;
    private float phaseCAmps;
    private float phaseAVolts;
    private float phaseBVolts;
    private float phaseCVolts;
    private float phaseAngleBA;
    private float phaseAngleCA;
    private float powerFactorAnglePhaseA;
    private float powerFactorAnglePhaseB;
    private float powerFactorAnglePhaseC;

    public AppMessageTypeETier7( char[] msg ) {
        super(msg);
        super.setMessageType(0xE);
        int offset=31;
        this.serviceFrequency = (float)((msg[1+offset] + (msg[2+offset]<<8))*0.1 );
        this.phaseAAmps = (float)((msg[3+offset] + (msg[4+offset]<<8))*0.1 );
        this.phaseBAmps = (float)((msg[5+offset] + (msg[6+offset]<<8))*0.1 );
        this.phaseCAmps = (float)((msg[7+offset] + (msg[8+offset]<<8))*0.1 );
        this.phaseAVolts = (float)((msg[9+offset] + (msg[10+offset]<<8))*0.1 );
        this.phaseBVolts = (float)((msg[11+offset] + (msg[12+offset]<<8))*0.1 );
        this.phaseCVolts = (float)((msg[13+offset] + (msg[14+offset]<<8))*0.1 );
        this.phaseAngleBA = (float)((msg[15+offset] + (msg[16+offset]<<8))*0.1 );
        this.phaseAngleCA = (float)((msg[17+offset] + (msg[18+offset]<<8))*0.1 );
        this.powerFactorAnglePhaseA = (float)((msg[19+offset] + (msg[20+offset]<<8))*0.1 );
        this.powerFactorAnglePhaseB = (float)((msg[21+offset] + (msg[22+offset]<<8))*0.1 );
        this.powerFactorAnglePhaseC = (float)((msg[23+offset] + (msg[24+offset]<<8))*0.1 );
    }

    public int getSubtier() {
        return this.subtier;
    }

    /**
     * @return Returns the phaseAAmps.
     */
    public float getPhaseAAmps() {
        return phaseAAmps;
    }

    /**
     * @return Returns the phaseAngleBA.
     */
    public float getPhaseAngleBA() {
        return phaseAngleBA;
    }

    /**
     * @return Returns the phaseAngleCA.
     */
    public float getPhaseAngleCA() {
        return phaseAngleCA;
    }

    /**
     * @return Returns the phaseAVolts.
     */
    public float getPhaseAVolts() {
        return phaseAVolts;
    }

    /**
     * @return Returns the phaseBAmps.
     */
    public float getPhaseBAmps() {
        return phaseBAmps;
    }

    /**
     * @return Returns the phaseBVolts.
     */
    public float getPhaseBVolts() {
        return phaseBVolts;
    }

    /**
     * @return Returns the phaseCAmps.
     */
    public float getPhaseCAmps() {
        return phaseCAmps;
    }

    /**
     * @return Returns the phaseCVolts.
     */
    public float getPhaseCVolts() {
        return phaseCVolts;
    }

    /**
     * @return Returns the powerFactorAnglePhaseA.
     */
    public float getPowerFactorAnglePhaseA() {
        return powerFactorAnglePhaseA;
    }

    /**
     * @return Returns the powerFactorAnglePhaseB.
     */
    public float getPowerFactorAnglePhaseB() {
        return powerFactorAnglePhaseB;
    }

    /**
     * @return Returns the powerFactorAnglePhaseC.
     */
    public float getPowerFactorAnglePhaseC() {
        return powerFactorAnglePhaseC;
    }

    /**
     * @return Returns the serviceFrequency.
     */
    public float getServiceFrequency() {
        return serviceFrequency;
    }

}
