package org.mitre.jfairseq;

import org.mitre.jfairseq.data.Dictionary;
import org.mitre.jfairseq.encoder.Encoder;
import org.mitre.jfairseq.tokenizer.Tokenizer;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FairseqTranslator {

    final private Encoder encoder;
    final private Tokenizer tokenizer;
    final private Dictionary sourceDict;
    final private Dictionary targetDict;
    final private Module mod;

    public FairseqTranslator(
            final File sourceVocab,
            final File targetVocab,
            final File modelFile,
            final Encoder enc,
            final Tokenizer tok
    ) throws IOException {
        sourceDict = new Dictionary();
        sourceDict.load(sourceVocab);
        targetDict = new Dictionary();
        targetDict.load(targetVocab);

        encoder = enc;
        tokenizer = tok;
        mod = Module.load(modelFile.getPath());
    }

    public String translate(String sent) throws Exception {
        String tokenizedSent = tokenizer.tokenize(sent);
        String encodedSent = encoder.encode(tokenizedSent);
        Tensor ids = sourceDict.encodeLine(encodedSent, line -> line.split("\\s+"), false);

        Map<String, IValue> map = new HashMap<>();
        Map<String, IValue> inner_map = new HashMap<>();

        Tensor sourceLengths = Tensor.fromBlob(new long[] {ids.shape()[1]}, new long[] {1});

        inner_map.put("src_tokens", IValue.from(ids));
        inner_map.put("src_lengths", IValue.from(sourceLengths));

        map.put("net_input", IValue.dictStringKeyFrom(inner_map));

        IValue input = IValue.dictStringKeyFrom(map);

        IValue[] preds = mod.forward(input).toList();

        Map<String, IValue> translations = preds[0].toList()[0].toDictStringKey();
        long[] targetIds = translations.get("tokens").toTensor().getDataAsLongArray();

        String tgtEncodedSent = targetDict.string(targetIds);
        String tgtDecodedSent = encoder.decode(tgtEncodedSent);
        return tokenizer.detokenize(tgtDecodedSent);
    }
}
