package com.cannontech.common.util;

import com.google.common.base.Strings;

/**
 * A Base 94 encoder for representing non-negative numbers in a compact textual form, typically for use in log files.
 * <br>For example, 32,767 is represented as <code>$cX</code>.
 * <p>Base 94 uses the printable ASCII range (33-126, excluding 32, the space character) to encode ~6.55 bits per character:
 * <pre>ln(94) / ln(2) = 4.5433 / 0.69315 = ~6.55</pre>
 * <p>This is more efficient than base64, which is 6 bits per character:
 * 
 * <table>
 *   <tr><th>Chars</th><th>Base94 max</th><th>Base64 max</th><th>delta</th></tr>
 *   <tr><td>1</td><td>93</td><td>63</td><td>+47%</td></tr>
 *   <tr><td>2</td><td>9,025</td><td>4,095</td><td>+120%</td></tr>
 *   <tr><td>3</td><td>857,375</td><td>262,143</td><td>+227%</td></tr>
 *   <tr><td>4</td><td>81,450,625</td><td>16,777,215</td><td>+387%</td></tr>
 * </table>
 * 
 * @see <a href="https://stackoverflow.com/questions/49205570/what-is-base94-encoding-and-how-does-it-work">base94 on Stack Overflow</a>
 * @author E9816181
 *
 */
public class Base94 {
    private static final int BASE = 94;
    private static final int PADDING = 3;
    private static final char OFFSET = '!';  //  ASCII 33
    
    private Base94() {
    }
    
    /**
     * Creates a Base94-encoded version of the input number, padded to 3 characters.
     * @param input The non-negative number to convert
     * @return The number encoded as a base94 string 
     */
    public static String of(long input) {
        
        var sb = new StringBuilder();

        while (input != 0) {
            //  (input % BASE) is guaranteed to be 0-93, so it will fit in an int
            sb.appendCodePoint((int)(input % BASE) + OFFSET);
            input /= BASE;
        }

        sb.reverse();

        return Strings.padStart(sb.toString(), PADDING, OFFSET);
    }
    
    /**
     * Returns the maximum value that can be stored in the specified number of base94 digits.  
     * @param digits The number of base94 digits. 
     * @return The maximum value that can be stored.
     */
    public static long max(int digits) {
        return (long)(Math.pow(BASE, digits)) - 1;
    }
}
