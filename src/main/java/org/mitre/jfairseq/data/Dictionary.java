package org.mitre.jfairseq.data;

import org.pytorch.Tensor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dictionary {
    final private Map<String, Long> indices;
    final private List<String> symbols;
    final private List<Long> counts;
    final public String pad;
    final public String eos;
    final public String unk;
    final public String bos;

    public Dictionary(final String pad, final String eos, final String unk, final String bos) {
        indices = new HashMap<>();
        symbols = new ArrayList<>();
        counts = new ArrayList<>();
        this.pad = pad;
        this.eos = eos;
        this.unk = unk;
        this.bos = bos;
        addSymbol(bos);
        addSymbol(pad);
        addSymbol(eos);
        addSymbol(unk);
    }

    public Dictionary() {
        this("<pad>", "</s>", "<unk>", "<s>");
    }

    private Long padIdx() {
        return indices.get(this.pad);
    }

    private Long unkIdx() {
        return indices.get(this.unk);
    }

    private Long eosIdx() {
        return indices.get(this.eos);
    }

    private Long bosIdx() {
        return indices.get(this.bos);
    }

    public String getSymbol(long i) {
        if (i < symbols.size()) {
            return symbols.get((int) i);
        } else {
            return unk;
        }
    }

    protected long index(final String tok) {
        return indices.getOrDefault(tok, unkIdx());
    }

    private long addSymbol(final String symbol, final Long count, final Boolean overwrite) {

        long idx;
        if (symbols.contains(symbol) && !overwrite) {
            idx = indices.get(symbol);
            counts.set((int) idx, counts.get((int) idx) + count);
        } else {
            idx = symbols.size();
            indices.put(symbol, idx);
            symbols.add(symbol);
            counts.add(count);
        }
        return idx;
    }

    private long addSymbol(final String symbol) {
        return addSymbol(symbol, 1L, false);
    }

    public void load(final Path dictFile) throws IOException  {
        try (Stream<String> stream = Files.lines(dictFile)) {
            stream.forEach(line -> {
                boolean overwrite = false;
                String lineWithoutTrailingWhitespace = line.replaceAll("\\s+$", "");
                int lastWhitespace = lineWithoutTrailingWhitespace.lastIndexOf(" ");
                int strLength = lineWithoutTrailingWhitespace.length();
                String word = lineWithoutTrailingWhitespace.substring(0, lastWhitespace);
                String field = lineWithoutTrailingWhitespace.substring(lastWhitespace+1, strLength);
                if (field.equals("#fairseq:overwrite")) {
                    overwrite = true;
                    int lastWhitespaceInWord = word.lastIndexOf(" ");

                    String newWord = word.substring(0, lastWhitespaceInWord);
                    String newField = word.substring(lastWhitespaceInWord+1, strLength);
                    word = newWord;
                    field = newField;
                }
                Long count = Long.parseLong(field);
                this.addSymbol(word, count, overwrite);
            });
        }
    }

    public void load(final File dictFile) throws IOException {
        load(dictFile.toPath());
    }

    private String unkWord(boolean escape) {
        if (escape) {
            return "<" + this.unk + ">";
        } else {
            return this.unk;
        }
    }

    private String tokenString(long i, boolean escapeUnk) {
        if (i == unkIdx()) {
            return unkWord(escapeUnk);
        } else {
            return getSymbol(i);
        }
    }

    public String string(final long[] tokenIds, boolean escapeUnk, Set<Long> extraSymbolsToIgnore) {
        extraSymbolsToIgnore.add(this.eosIdx());
        extraSymbolsToIgnore.add(this.bosIdx());

        Stream<String> s = Arrays.stream(tokenIds)
                .filter(i -> !extraSymbolsToIgnore.contains(i))
                .mapToObj(i -> tokenString(i, escapeUnk));

        return String.join(" ", s.collect(Collectors.toList()));
    }

    public String string(final long[] tokenIds) {
        return string(tokenIds, false, new HashSet<>());
    }

    public Tensor encodeLine(final String line, final Function<String, String[]> lineTokenizer, boolean addIfNotExist, boolean appendEos, boolean reverseOrder) {
        String[] tokens = lineTokenizer.apply(line);
        int numWords = tokens.length;
        int fillLength = appendEos ? tokens.length + 1 : tokens.length;

        long[] ids = new long[fillLength];
        Arrays.fill(ids, eosIdx());


        for (int i = 0; i < numWords; ++i) {
            long idx;
            String token = tokens[i];
            if (addIfNotExist) {
                idx = addSymbol(token);
            } else {
                idx = index(token);
            }
            ids[i] = idx;
        }

        return Tensor.fromBlob(ids, new long[] {1, fillLength});
    }

    public Tensor encodeLine(final String line, final Function<String, String[]> lineTokenizer) {
        return encodeLine(line, lineTokenizer, true, true, false);
    }

    public Tensor encodeLine(final String line, final Function<String, String[]> lineTokenizer, boolean addIfNotExist) {
        return encodeLine(line, lineTokenizer, addIfNotExist, false, false);
    }
}
