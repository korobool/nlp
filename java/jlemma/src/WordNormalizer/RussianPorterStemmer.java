package WordNormalizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class implements basics of Porter Stemmer for russian (it is imperfect, but idea is represented)
// It uses language word formation rules to try to get basic form. It cannot detect gender (but we can use machine
// learning to make it able to detect gender)

// The Idea of this class in project not to get lemma if it wasn't found in machine - learnt model. It can be useful
// in finding correct lemma heuristically from set of lemmas (in case of tree truncating we can try to sustain quality
// of lemmatiazation process)
public class RussianPorterStemmer {

    // Language dependent patterns
    private static final Pattern NOUN_ENDING =
            Pattern.compile("(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях" +
                    "|ь|ию|ью|ю|ия|ья|я|ы)$");

    private static final Pattern SIMPLE_ENDING =
            Pattern.compile("^(.*?[аеиоуыэюя])(.*)$");

    private static final Pattern SELF_CALL = Pattern.compile("(с[яь])$");

    private static final Pattern ADJECTIVE_ENDING =
            Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$");

    private static final Pattern PARTICIPLE_SUFFIX =
            Pattern.compile("((ивш|ывш|ующ)|((?<=[ая])(ем|нн|вш|ющ|щ)))$");

    private static final Pattern VERB_ENDING =
            Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|" +
                    "ены|ить|ыть|ишь|ую|ю)|((?<=[ая])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");

    private static final Pattern PERFECT =
            Pattern.compile("((ив|ывшись|ивши|ывши|ившись|ыв)|((?<=[ая])(вши|в|вшись)))$");

    private static final Pattern INHERIT =
            Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");

    private static final Pattern DOUBLE_N = Pattern.compile("нн$");
    private static final Pattern I = Pattern.compile("и$");
    private static final Pattern SIGN = Pattern.compile("ь$");

    private static final Pattern DER =
            Pattern.compile("ость?$");

    private static final Pattern SUPER =
            Pattern.compile("(ейш|ейше)$");

    // Call this entry to get basic form of word. It's only guess about possible lemma, but can be really good
    // after some improvements.
    public String do_stemming(String word) {
        // Normalization to lower case
        word = word.toLowerCase();

        // Not simple to describe stemming algorithm, it requires a lot of improvements, but seems to be
        // basically working
        Matcher m = SIMPLE_ENDING.matcher(word);
        if (m.matches()) {
            String prefix = m.group(1);
            String rt = m.group(2);
            String ending = PERFECT.matcher(rt).replaceFirst("");
            if (ending.equals(rt)) {
                rt = SELF_CALL.matcher(rt).replaceFirst("");
                ending = ADJECTIVE_ENDING.matcher(rt).replaceFirst("");
                if (!ending.equals(rt)) {
                    rt = ending;
                    rt = PARTICIPLE_SUFFIX.matcher(rt).replaceFirst("");
                } else {
                    ending = VERB_ENDING.matcher(rt).replaceFirst("");
                    if (ending.equals(rt)) {
                        rt = NOUN_ENDING.matcher(rt).replaceFirst("");
                    } else {
                        rt = ending;
                    }
                }

            } else {
                rt = ending;
            }

            rt = I.matcher(rt).replaceFirst("");

            if (INHERIT.matcher(rt).matches()) {
                rt = DER.matcher(rt).replaceFirst("");
            }

            ending = SIGN.matcher(rt).replaceFirst("");
            if (ending.equals(rt)) {
                rt = SUPER.matcher(rt).replaceFirst("");
                rt = DOUBLE_N.matcher(rt).replaceFirst("н");
            }else{
                rt = ending;
            }
            word = prefix + rt;

        }

        // Resulting guess about possible lemma
        return word;
    }

}