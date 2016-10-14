import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TokenRules {

	public static final String patternWhiteSpace = "\\s+";
	public static final String patternTag = "<[/]?\\w+>|>[/]?\\w+(\\w=[\\w])+>";
	public static final String patternNumber = "\\d+";
	public static final String patternNumberWord = "[.]*\\d+[.*]";
	public static final String patternWord = ".*\\w+.*";

	public static final String patternPunctuation = "\\p{Punct}+";

	public static final String emptyString = "";

	public static final List<String> stopWordList = new ArrayList<String>(
			Arrays.asList("a", "a's", "able", "about", "above", "according", "accordingly", "across",
					"actually", "after", "afterwards", "again", "against", "ain't", "all", "allow", "allows", "almost",
					"alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and",
					"another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart",
					"appear", "appreciate", "appropriate", "are", "aren't", "around", "as", "aside", "ask", "asking",
					"associated", "at", "available", "away", "awfully", "b", "be", "became", "because", "become",
					"becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside",
					"besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "c'mon", "c's",
					"came", "can", "can't", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes",
					"clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering",
					"contain", "containing", "contains", "corresponding", "could", "couldn't", "course", "currently", "d",
					"definitely", "described", "despite", "did", "didn't", "different", "do", "does", "doesn't", "doing",
					"don't", "done", "down", "downwards", "during", "e", "each", "edu", "eg", "eight", "either", "else",
					"elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody",
					"everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "far", "few",
					"fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth",
					"four", "from", "further", "furthermore", "g", "get", "gets", "getting", "given", "gives", "go",
					"goes", "going", "gone", "got", "gotten", "greetings", "h", "had", "hadn't", "happens", "hardly",
					"has", "hasn't", "have", "haven't", "having", "he", "he's", "hello", "help", "hence", "her", "here",
					"here's", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself",
					"his", "hither", "hopefully", "how", "howbeit", "however", "i", "i'd", "i'll", "i'm", "i've", "ie",
					"if", "ignored", "immediate", "in", "inasmuch", "inc", "inc.", "indeed", "indicate", "indicated",
					"indicates", "inner", "insofar", "instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll",
					"it's", "its", "itself", "j", "just", "k", "keep", "keeps", "kept", "know", "knows", "known", "l",
					"last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", "like",
					"liked", "likely", "little", "look", "looking", "looks", "ltd", "m", "mainly", "many", "may", "maybe",
					"me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must",
					"my", "myself", "n", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither",
					"never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor",
					"normally", "not", "nothing", "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh",
					"ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise",
					"ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "p", "particular",
					"particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably",
					"provides", "q", "que", "quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "regarding",
					"regardless", "regards", "relatively", "respectively", "right", "s", "said", "same", "saw", "say",
					"saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen",
					"self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she",
					"should", "shouldn't", "since", "six", "so", "some", "somebody", "somehow", "someone", "something",
					"sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify",
					"specifying", "still", "sub", "such", "sup", "sure", "t", "t's", "take", "taken", "tell", "tends",
					"th", "than", "thank", "thanks", "thanx", "that", "that's", "thats", "the", "their", "theirs", "them",
					"themselves", "then", "thence", "there", "there's", "thereafter", "thereby", "therefore", "therein",
					"theres", "thereupon", "these", "they", "they'd", "they'll", "they're", "they've", "think", "third",
					"this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus",
					"to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying",
					"twice", "two", "u", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up",
					"upon", "us", "use", "used", "useful", "uses", "using", "usually", "uucp", "v", "value", "various",
					"very", "via", "viz", "vs", "w", "want", "wants", "was", "wasn't", "way", "we", "we'd", "we'll",
					"we're", "we've", "welcome", "well", "went", "were", "weren't", "what", "what's", "whatever", "when",
					"whence", "whenever", "where", "where's", "whereafter", "whereas", "whereby", "wherein", "whereupon",
					"wherever", "whether", "which", "while", "whither", "who", "who's", "whoever", "whole", "whom",
					"whose", "why", "will", "willing", "wish", "with", "within", "without", "won't", "wonder", "would",
					"would", "wouldn't", "x", "y", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your",
					"yours", "yourself", "yourselves", "z", "zero"));


	public static boolean checkWhiteSpace(String inputWord) {

		return Pattern.matches(patternWhiteSpace, inputWord);
	}

	public static boolean checkTag(String inputWord) {
		return Pattern.matches(patternTag, inputWord);
	}

	public static boolean checkNumber(String inputWord) {
		return Pattern.matches(patternNumber, inputWord);
	}

	public static boolean checkPunctuation(String inputWord) {
		return Pattern.matches(patternPunctuation, inputWord);
	}

	public static boolean checkLength(String inputWord) {
		return inputWord.length() <= 0 ? true : false;
	}

	public static boolean checkStopWords(String inputWord) {
		return stopWordList.contains(inputWord);
	}

	public boolean applyRules(String inputWord) {
		if (!checkNumber(inputWord) && !checkTag(inputWord) && !checkWhiteSpace(inputWord)
				&& !checkPunctuation(inputWord) && !checkLength(inputWord) && !checkStopWords(inputWord)) {
			return true;
		}
		return false;
	}

	public boolean applyRulesWithoutStopWords(String inputWord) {
		if (!checkNumber(inputWord) && !checkTag(inputWord) && !checkWhiteSpace(inputWord)
				&& !checkPunctuation(inputWord) && !checkLength(inputWord)) {
			return true;
		}
		return false;
	}

	public List<String> cleanToken(String inputWord) {
		List<String> tokenWords = new ArrayList<>();

		// remove all the numbers in the string
		inputWord = inputWord.replaceAll(patternNumber, emptyString);
		//check at least one alphabet present for creating token 
		if (Pattern.matches(patternWord, inputWord)) {
			if (checkTag(inputWord)) {
				tokenWords.add(inputWord);
			} else if (inputWord.contains("-")) {
				// if word contains hyphen then split it and store two separate
				// words
				tokenWords = getSplitTokenList(inputWord, "-");
			} else if (inputWord.contains("'") && inputWord.split("'").length >= 1) {
				tokenWords.add(inputWord.split("'")[0].toLowerCase().replaceAll(patternPunctuation, emptyString));
			} else {
				tokenWords.add(inputWord.toLowerCase().replaceAll(patternPunctuation, emptyString));
			}
		}
		return tokenWords;
	}

	public List<String> getSplitTokenList(String inputWord, String splitChar) {
		List<String> tokenWords = new ArrayList<>();
		String[] splitWords = inputWord.split(splitChar);
		for (String splitWord : splitWords) {
			tokenWords.add(splitWord.toLowerCase().replaceAll(patternPunctuation, emptyString));
		}
		return tokenWords;
	}
}
