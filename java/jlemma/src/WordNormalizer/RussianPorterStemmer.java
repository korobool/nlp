package WordNormalizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RussianPorterStemmer {

    private static final Pattern ADJECTIVE_ENDING =
            Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$");

    private static final Pattern PARTICIPLE_SUFFIX =
            Pattern.compile("((ивш|ывш|ующ)|((?<=[ая])(ем|нн|вш|ющ|щ)))$");

    private static final Pattern VERB_ENDING =
            Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|" +
                    "ены|ить|ыть|ишь|ую|ю)|((?<=[ая])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");

    private static final Pattern NOUN_ENDING =
            Pattern.compile("(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях|ы" +
                    "|ь|ию|ью|ю|ия|ья|я)$");

    private static final Pattern SIMPLE_ENDING =
            Pattern.compile("^(.*?[аеиоуыэюя])(.*)$");

    private static final Pattern SELF_CALL = Pattern.compile("(с[яь])$");

    private static final Pattern PERFECT =
            Pattern.compile("((ив|ывшись|ивши|ывши|ившись|ыв)|((?<=[ая])(вши|в|вшись)))$");

    private static final Pattern INHERIT =
            Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");

    private static final Pattern DER =
            Pattern.compile("ость?$");

    private static final Pattern SUPER =
            Pattern.compile("(ейш|ейше)$");

    private static final Pattern DOUBLE_N = Pattern.compile("нн$");
    private static final Pattern I = Pattern.compile("и$");
    private static final Pattern SIGN = Pattern.compile("ь$");

    public String stem(String word) {
        word = word.toLowerCase();
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

        return word;
    }

}