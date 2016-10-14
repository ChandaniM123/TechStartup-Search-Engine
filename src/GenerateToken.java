import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class GenerateToken {

	private static Map<String, Token> lemmaTokenMap = new HashMap<String, Token>();
	private static Map<Integer, Document> docMap = new HashMap<Integer, Document>();
	private static long docCount = 0;
	private static String docId = "";

	public static void main(String[] args) {

		String inputFilePath = "./../Docs1/TestDocs";
		String outputFilePath = "./index2test";
        String docFilePath = "./docMap";
		
        GenerateToken tokenGen = new GenerateToken();
        tokenGen.readDirectoryLemma(inputFilePath);
		tokenGen.storeToken(lemmaTokenMap, outputFilePath);
		tokenGen.storeDocMap(docFilePath);
          
	}

	public void readDirectoryLemma(String dirPath) {

		try {
			File folder = new File(dirPath);
			if (folder != null) {
				File[] fileList = folder.listFiles();
				for (File file : fileList) {
					if (file.isFile()) {
						//docId = file.getPath().substring(19, file.getPath().length() - 4);
						docId = file.getPath().substring(23, file.getPath().length() - 4);
						System.out.println("Processing Doc#" + docId + "......");
						readFileLemma(file.getPath(), Integer.parseInt(docId));
						docCount++;
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Exception occured while reading files:" + ex.getMessage() + "\n");
			ex.printStackTrace();
		}
	}

	public void readFileLemma(String filePath, Integer docId) {
		TokenRules rules = new TokenRules();
		String inputLine = "";
		BufferedReader br = null;
		Lemmatizer lemmatizer = new Lemmatizer();
		int maxTf = 0;
		int docLen = 0;
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				List<String> lemmaTokens = lemmatizer.lemmatize(inputLine);
				for (String inputTokenWord : lemmaTokens) {
					List<String> inputTokenList = rules.cleanToken(inputTokenWord);
					for (String inputToken : inputTokenList) {
						if (rules.applyRulesWithoutStopWords(inputToken.trim())) {
							docLen++;
							if (!TokenRules.checkStopWords(inputToken.trim())) {
								int termfreq = 1;
								if (lemmaTokenMap.containsKey(inputToken)) {
									Token tokenStore = lemmaTokenMap.get(inputToken);
									tokenStore.frequency++;
									if (tokenStore.termFrequency.containsKey(docId)) {
										termfreq = tokenStore.termFrequency.get(docId);
										termfreq++;
									}
									tokenStore.termFrequency.put(docId, termfreq);
									lemmaTokenMap.put(inputToken, tokenStore);
								} else {
									Token tokenStore = new Token(docId);
									lemmaTokenMap.put(inputToken, tokenStore);
								}
								if (maxTf < termfreq) {
									maxTf = termfreq;
								}
							}
						}
					}
				}
			}
			docMap.put(docId, new Document(maxTf, docLen));
			br.close();
		} catch (Exception ex) {
			System.out.println(
					"Exception occured in retrieving tokens!!" + ex.getMessage() + "\n\n" + ex.getStackTrace());
		}
	}

	public void storeToken(Map<String, Token> tokenMap, String outputFilePath) {

		try {
			FileWriter writerOpFile = new FileWriter(new File(outputFilePath), false);
			writerOpFile.write("Token Id     Freq   DocID|TermFreq\n");
			for (Entry<String, Token> token : tokenMap.entrySet()) {
				writerOpFile.write(token.getKey() + "::" + token.getValue().frequency + "::");
				for (Entry<Integer, Integer> termFreq : token.getValue().termFrequency.entrySet()) {
					writerOpFile.write(termFreq.getKey() + "|" + termFreq.getValue() + "-->");
				}
				writerOpFile.write("\n");
			}
			System.out.println("File Write successfull");
			writerOpFile.close();

		} catch (IOException ex) {
			System.out.println(
					"Exception occured in retrieving tokens!!" + ex.getMessage() + "\n\n" + ex.getStackTrace());
		}

	}

	public void storeDocMap(String outputFilePath) {
		try {
			FileWriter writerOpFile = new FileWriter(new File(outputFilePath), false);
			writerOpFile.write("Doc_Id:: DocLen:: MaxFreq\n");

			for (Entry<Integer, Document> tokenEntry : docMap.entrySet()) {
                writerOpFile.write(tokenEntry.getKey() + "::" + tokenEntry.getValue().docLen + "::" + tokenEntry.getValue().maxTf + "\n");  
			}
			writerOpFile.close();
		} catch (Exception ex) {

		}
	}

	public void displayTokenInfo(Map<String, Token> tokenMap) {

		long totalTokenCount = 0;
		long singleTokenCount = 0;
		ValueComparator valComp = new ValueComparator(tokenMap);
		TreeMap<String, Token> sortedDescTokenMap = new TreeMap<String, Token>(valComp);
		sortedDescTokenMap.putAll(tokenMap);

		for (Entry<String, Token> tokenEntry : tokenMap.entrySet()) {
			totalTokenCount += tokenEntry.getValue().frequency;
			if (tokenEntry.getValue().frequency == 1) {
				singleTokenCount++;
			}
		}

		System.out.println("Total number of Tokens: " + totalTokenCount);
		System.out.println("Number of distinct Tokens: " + tokenMap.size());
		System.out.println("Number of tokens with single count: " + singleTokenCount);
		System.out.println("Average number of tokens per document: " + totalTokenCount / docCount);
		System.out.println("TokenId ---> Frequency");
		int i = 0;
		for (Entry<String, Token> tokenEntry : sortedDescTokenMap.entrySet()) {
			if (i > 30)
				break;
			System.out.println(tokenEntry.getKey() + " ---> " + tokenEntry.getValue().frequency);
			i++;
		}
	}

	class ValueComparator implements Comparator<String> {
		Map<String, Token> base;

		public ValueComparator(Map<String, Token> base) {
			this.base = base;
		}

		public int compare(String tokenKey1, String tokenKey2) {
			Token token1 = (Token) base.get(tokenKey1);
			Token token2 = (Token) base.get(tokenKey2);
			if (token1.frequency <= token2.frequency) {
				return 1;
			}
			return -1;
		}

	}
}
