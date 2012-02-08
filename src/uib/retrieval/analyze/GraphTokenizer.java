/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.analyze;

import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.Token;


import java.io.IOException;


/**
 *
 * @author Olav
 */
class GraphTokenizer extends TokenStream {
    private char last=' ';
    private Reader reader;
    private boolean tokenstart, tokenend;

    public GraphTokenizer(Reader in) {
//        super(in);
        reader = in;
    }

    protected boolean isTokenChar(char c) {
        boolean returnValue = false;
        if (c == ' ' && last == ']') {
            returnValue = true;
        }
        last = c;
        return returnValue;
    }

    public Token next() throws IOException {
        StringBuilder currenttoken = new StringBuilder(64);
        // currenttoken.append('[');
        char[] character = new char[1];
        int i = reader.read(character);
        // reset our states :)
        tokenstart = false;
        tokenend = false;
        do {
            // end of stream reached ...
            if (i == 0) return null;

            if (character[0] == '[') { // token starts here ...
                tokenstart = true;
            } else if (character[0] == ']') { // token ends here ...
                tokenend = true;
            } else if (tokenstart && !tokenend) { // between end and start ...
                currenttoken.append(character[0]);
            }
            // we found our token and return it ...
            if (tokenstart && tokenend) {
                // currenttoken.append(']');
                // prepend a token because lucene does not allow leading wildcards. 
                currenttoken.insert(0, '_');
                String tokenString = currenttoken.toString().toLowerCase().replace(' ', '_').trim();
                Token t = new Token(tokenString, 0, tokenString.length()-1);
                return t;
            }
            i = reader.read(character);
        } while (i>0 && !tokenend);
        return null;
    }

    @Override
    public boolean incrementToken() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
