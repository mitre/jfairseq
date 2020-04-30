package org.mitre.jfairseq.encoder;

public interface Encoder {
    String encode(final String sent) throws Exception;
    String decode(final String sent);
}
