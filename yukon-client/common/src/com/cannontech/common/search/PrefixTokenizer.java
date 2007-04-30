package com.cannontech.common.search;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/**
 * Similar to a CharTokenizer, this will return every prefix of a term in addition to
 * the term itself. So for the term "Mack", the following tokens will be returned:
 * "m", "ma", "mac", and "mack". Note that tokens are also lower cased (although
 * this behavior can be controlled by overriding the normalize method). This allows
 * queries against the initial part of a word (which would occur in a find-as-you-type
 * scenario), without resorting to wildcard queries.
 */
public class PrefixTokenizer extends Tokenizer {

    public PrefixTokenizer(Reader input) {
        super(input);
    }

    private int offset = 0, bufferIndex = 0, dataLen = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 1024;
    private final char[] buffer = new char[MAX_WORD_LEN];
    private final char[] ioBuffer = new char[IO_BUFFER_SIZE];
    private int length = 0;
    private int start = 0;

    /** Returns true iff a character should be included in a token.  This
     * tokenizer generates as tokens adjacent sequences of characters which
     * satisfy this predicate.  Characters for which this is false are used to
     * define token boundaries and are not included in tokens. */
    public static boolean isTokenChar(char c) {
        return !((Character.isWhitespace(c)) || 
                (c == '_') || 
                (c == '-') || 
                (c == '(') ||
                (c == ')') ||
                (c == '/'));
    }

    /** Called on each token character to normalize it before it is added to the
     * token.  The default implementation does nothing.  Subclasses may use this
     * to, e.g., lowercase tokens. */
    protected char normalize(char c) {
      return Character.toLowerCase(c);
    }

    @Override
    public Token next() throws IOException {
        start = offset;
        while (true) {
            final char c;
            
            offset++;
            if (bufferIndex >= dataLen) {
                dataLen = input.read(ioBuffer);
                bufferIndex = 0;
            }
            if (dataLen == -1) {
                break;
            } else {
                c = ioBuffer[bufferIndex++];
            }
            
            if (isTokenChar(c)) {
                
                if (length == 0) {
                    start = offset - 1;
                }
                
                buffer[length++] = normalize(c); // buffer it, normalized
                return new Token(new String(buffer, 0, length), start, start + length);
                
                
            } else if (length > 0) {
                length = 0;
            }
            
        }
        
        return null;
    }
}

