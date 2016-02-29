package com.cannontech.web.search.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

/**
 * Similar to a CharTokenizer, this will return every prefix of a term in addition to
 * the term itself. So for the term "Mack", the following tokens will be returned:
 * "m", "ma", "mac", and "mack". Note that tokens are also lower cased (although
 * this behavior can be controlled by overriding the normalize method). This allows
 * queries against the initial part of a word (which would occur in a find-as-you-type
 * scenario), without resorting to wildcard queries.
 */
public final class PrefixTokenizer extends Tokenizer {
    public final static String TOKEN_DELIMITER_PATTERN = "[\\p{javaWhitespace}_\\-()/]";

    private int offset = 0;
    private int bufferIndex = 0;
    private int dataLen = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 1024;
    private final char[] buffer = new char[MAX_WORD_LEN];
    private final char[] ioBuffer = new char[IO_BUFFER_SIZE];
    private int length = 0;
    private int start = 0;
    final protected CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    final protected OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    
    public PrefixTokenizer() {
        clearAttributes();
        addAttribute(CharTermAttribute.class);
    }
    
    /**
     * Called on each token character to normalize it before it is added to the
     * token. The default implementation does nothing. Subclasses may use this
     * to, e.g., lowercase tokens.
     */
    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }

    /**
     * Returns true if a character should be included in a token. This tokenizer generates as tokens adjacent
     * sequences of characters which satisfy this predicate. Characters for which this is false are used to
     * define token boundaries and are not included in tokens.
     * 
     * This needs to match {@link #TOKEN_CHAR_PATTERN}. We could just use that here but it would be slower.
     */
    protected static boolean isTokenChar(int c) {
        return !Character.isWhitespace(c) && c != '_' && c != '-' && c != '(' && c != ')' && c != '/';
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();

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
            }
            c = ioBuffer[bufferIndex++];

            if (isTokenChar(c)) {
                if (length == 0) {
                    start = offset - 1;
                }
                buffer[length++] = normalize(c); // buffer it, normalized
                termAtt.copyBuffer(buffer, 0, length);
                offsetAtt.setOffset(correctOffset(start), correctOffset(start+length));
                return true;
            } else if (length > 0) {
                length = 0;
            }
        }
        return false;
    }
    
    @Override
    public final void end() throws IOException {
      super.end();
      final int finalOffset = correctOffset(offset);
      this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
      super.reset();
      offset = bufferIndex = dataLen = length= 0;
    }
}
