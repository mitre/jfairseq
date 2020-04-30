package org.mitre.jfairseq.tokenizer;

public class WhitespaceTokenizer implements Tokenizer {

    @Override
    public String tokenize(String sent) {
        return String.join(" ", sent.split("\\s+"));
    }

    @Override
    public String detokenize(String tokens) {
        return tokens.replace("\\s+", " ");
    }
}
