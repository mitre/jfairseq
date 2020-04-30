package org.mitre.jfairseq.encoder;

import org.mitre.jfastbpe.FastBPE;

import java.io.File;
import java.io.IOException;

public class FastBPEEncoder implements Encoder {

    final private FastBPE bpe;

    public FastBPEEncoder(final File code) throws IOException {
        bpe = new FastBPE(code);
    }

    @Override
    public String encode(String sent) throws InterruptedException {
        return bpe.applyBPE(sent);
    }

    @Override
    public String decode(String sent) {
        return (sent + " ").replace("@@ ", "");
    }
}