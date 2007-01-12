package com.amdswireless.messages.rx;


import java.util.ArrayList;
import java.util.Date;

/**
   Superclass for all READ type messages.  Contains only the current reading and the
   timestamps of that read.  Subclassed by most read messages.
 * @author johng
 * @see AppMessageType3
 * @see AppMessageTypeC
 */
public class ReadMessage extends DataMessage implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private transient ArrayList<Integer> binBuffer = new ArrayList<Integer>();
	protected java.util.Date readTime;
	protected int relativeTimestamp;
	protected int currentReading;
	
	public ReadMessage() {
		super();
		super.setMessageClass("READ");
	}
	
	public ReadMessage(char[] buf) {
		super(buf);
		super.setMessageClass("READ");
	}
	

	public int[] decompress(java.util.BitSet bs) {
        int i = 0;
        int bins[];
        boolean cont = true;
        while (cont == true && i < 160) {
            int ones = 0;
            while (bs.get(i)) { // while the next bit is 1 (true)
                i++; // increment the overall counter
                ones++; // and the local count of the number of conseq 1's
            }
            i++; // skip the zero that kicked us out of the while loop
            // if the number of consecutive 1's is 0-5, then that is the
            // value of this bin
            // remember binBuffer is just an ArrayList
            if (ones < 6) {
                binBuffer.add(new Integer(ones));
                // ok, special case #1, values 6-37
            } else if (ones == 6) {
                int reading = 0;
                for (int j = 0; j < 5; j++) { // read the next 5 bits
                    reading += ((bs.get(i) ? 1 : 0) << j);
                    i++;
                }
                binBuffer.add(new Integer(reading + 6));
                // special case #2, values greater than 37
            } else if (ones == 7) {
                int reading = 0;
                for (int j = 0; j < 13; j++) { // read the next 5 bits
                    reading += ((bs.get(i) ? 1 : 0) << j);
                    i++;
                }
                binBuffer.add(new Integer(reading + 38));
                // finally, that magic case where we're at the end of our
                // rope and we get more than 7 1's in a row...
            } else if (ones == 8) {
                cont = false;
            }
        }
        if ( binBuffer.size() == 0 ) {
            bins = new int[0];
        } else {
            bins = new int[binBuffer.size()-1];
            for (i = 0; i < binBuffer.size()-1; i++) {
                bins[i] = ((Integer) (binBuffer.get(i))).intValue();
            }
        }
        return bins;
    }

	public ArrayList<IntervalReading> getIntervalTimestamps(int[] readings, long toi, int relTimeStamp, int DDT) {
			
		Date base = new Date( toi - (2000 * relTimeStamp) );
		long offset = 5 * 60 * 1000;
		
		if( DDT == 0 ) {
			offset = 5 * 60 * 1000;			// 5 minutes
		} else if( DDT == 1 ) {
			offset = 15 * 60 * 1000;		// 15 minutes
		} else if( DDT == 2 ) {
			offset = 60 * 60 * 1000;		// 1 hour
		} else if( DDT == 3 ) {
			offset = 360 * 60 * 1000;		// 6 hours
		} else if( DDT == 4 ) {
			offset = 720 * 60 * 1000;		// 12 hours
		} else if( DDT == 5 ) {
			offset = 1440 * 60 * 1000;		// 24 hours
		}

		ArrayList<IntervalReading> results = new ArrayList<IntervalReading>( readings.length );

		for( int i = 0; i < readings.length; i++ ) {
			results.add( new IntervalReading( readings[i], new Date( base.getTime() - (offset * i) ) ) );
		}

		return results;
	}

	/**
	 * @return Returns the currentReading.
	 */
	public int getCurrentReading() {
		return currentReading;
	}

	/**
	 * @param currentReading The currentReading to set.
	 */
	public void setCurrentReading(int currentReading) {
		this.currentReading = currentReading;
	}

	/**
	 * @return Returns the time which this reading was made, as determined
	 * using the TOI and the relative Timestamp.
	 */
	public java.util.Date getReadTime() {
		return readTime;
	}

	/**
	 * @param readTime The readTime to set.
	 */
	public void setReadTime(java.util.Date readTime) {
		this.readTime = readTime;
	}

	/**
	 * @return Returns the relativeTimestamp.
	 */
	public int getRelativeTimestamp() {
		return relativeTimestamp;
	}

	/**
	 * @param relativeTimestamp The relativeTimestamp to set.
	 */
	public void setRelativeTimestamp(int relativeTimestamp) {
		this.relativeTimestamp = relativeTimestamp;
	}
}
