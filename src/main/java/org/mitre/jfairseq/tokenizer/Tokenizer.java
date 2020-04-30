package org.mitre.jfairseq.tokenizer;

public interface Tokenizer {
    String tokenize(final String sent);
    String detokenize(final String tokens);
}
