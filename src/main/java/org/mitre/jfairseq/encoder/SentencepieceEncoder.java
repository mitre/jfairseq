package org.mitre.jfairseq.encoder;

import com.github.google.sentencepiece.SentencePieceProcessor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SentencepieceEncoder implements Encoder {

    final private SentencePieceProcessor spm;

    public SentencepieceEncoder(final File code) {
        spm = new SentencePieceProcessor();
        spm.load(code.toString());
    }

    @Override
    public String encode(String sent) {
        List<String> pieces = spm.encodeAsPieces(sent);
        return String.join(" ", pieces);
    }

    @Override
    public String decode(String sent) {
        String[] pieces = sent.split("\\s+");
        return spm.decodePieces(Arrays.asList(pieces));
    }
}
