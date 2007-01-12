package com.amdswireless.messages.rx;


public class TransmitMessageReport extends AndorianMessage implements java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private final String msgClass = "TGB";
	private int resultCode;
        private int echoedAndorianSequence;
        private int echoedToi;

	public TransmitMessageReport( char[] msg ) {
		super(msg);
		this.resultCode=(int)msg[23];
                this.echoedAndorianSequence=(int)msg[17];
                this.echoedToi=(int)(msg[21]<<24)+(int)(msg[20]<<16)+(int)(msg[19]<<8)+(int)(msg[18]);
	}

	public int getResultCode() {
		return this.resultCode;
	}

	public String getMsgClass() {
		return this.msgClass;
	}

        public int getEchoedAndorianSequence() {
            return this.echoedAndorianSequence;
        }

        public int getEchoedToi() {
            return this.echoedToi;
        }

        public boolean isAcked() {
            return this.resultCode<2;
        }
}
