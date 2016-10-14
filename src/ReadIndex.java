import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ReadIndex {
	
	private static Map<String, Token> lemmaTokenMap = new HashMap<String, Token>();
	private static Map<Integer, Document> docMap = new HashMap<Integer, Document>();
	private static Map<Integer, Query> queryMap = new HashMap<Integer, Query>();
	private static long docCount = 0;
	private static String docId = "";
	private static String outputFileWeight = "./opWeight";
	private static String queryText = "davis cup final";
	private static Integer collectionSize = 0;
	private static Double avgDocLen = 0d;

	public static void main(String[] args) {

		String outputFilePath = "./index2";
		String docFilePath = "./docMap";

		ReadIndex rIndex = new ReadIndex();
		
		rIndex.readIndexFile(outputFilePath);
		rIndex.readDocFile(docFilePath);

		Query searchQuery = rIndex.readQueryFile(queryText);
		searchQuery = rIndex.computeWeight(searchQuery);
		rIndex.getTopRankedDoc(searchQuery);
	}

	public void readIndexFile(String filePath) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filePath));
			String inputLine = br.readLine();

			while ((inputLine = br.readLine()) != null) {
				String[] tokenInfo = inputLine.split("::");
				Map<Integer, Integer> termFreq = new HashMap<Integer, Integer>();
				int i = 0;
				for (String termFreqInfo : tokenInfo[2].split("-->")) {
					String[] termFreqDoc = termFreqInfo.split("\\|");

					termFreq.put(Integer.parseInt(termFreqDoc[0]),
							Integer.parseInt(termFreqDoc[1]));
				}
				Token token = new Token();
				token.termFrequency = termFreq;
				lemmaTokenMap.put(tokenInfo[0], token);
			}
			br.close();
		} catch (Exception ex) {
			System.out.println("Exception occured while reading Index file!!");
			ex.printStackTrace();
		}
	}

	public void readDocFile(String docFilePath) {
		BufferedReader br = null;
		Double sumDocLen = 0d;
		try {
			br = new BufferedReader(new FileReader(docFilePath));
			String inputLine = br.readLine();
			while ((inputLine = br.readLine()) != null) {
				String[] docInfo = inputLine.split("::");
				Document doc = new Document(Integer.parseInt(docInfo[1]),
						Integer.parseInt(docInfo[2]));
				docMap.put(Integer.parseInt(docInfo[0]), doc);
				collectionSize++;
				sumDocLen += Double.parseDouble(docInfo[1]);
			}
			br.close();
			avgDocLen = sumDocLen / collectionSize;
		} catch (Exception ex) {
			System.out.println("Exception occured while reading Document file!!");
			ex.printStackTrace();
		}
	}

	public Query readQueryFile(String queryText) {
		TokenRules rules = new TokenRules();
		String inputLine = "";
		BufferedReader br = null;
		Lemmatizer lemmatizer = new Lemmatizer();
		Query queryStore = new Query(queryText);

		int queryCount = 0;
		try {
			int docLen = 0;
			int maxTermFreq = -1;
			List<String> lemmaTokens = lemmatizer.lemmatize(queryText);
			for (String inputTokenWord : lemmaTokens) {
				List<String> inputTokenList = rules.cleanToken(inputTokenWord);
				for (String inputToken : inputTokenList) {
					if (rules.applyRulesWithoutStopWords(inputToken.trim())) {
						docLen = docLen + 1; // increment the document
												// length
						if (!TokenRules.checkStopWords(inputToken.trim())) {
							WeightToken queryToken = new WeightToken();
							queryToken.termFreq = 1;
							if (queryStore.queryVector.containsKey(inputToken)) {
								queryToken = queryStore.queryVector.get(inputToken);
								queryToken.termFreq++;
							}

							queryStore.queryVector.put(inputToken, queryToken);
							if (maxTermFreq < queryToken.termFreq) {
								maxTermFreq = queryToken.termFreq;
							}
						}
					}
				}
			}
			queryStore.maxTf = maxTermFreq;
			queryStore.docLen = docLen;
			queryMap.put(queryCount, queryStore);

		} catch (Exception ex) {
			System.out.println("Exception occured in reading  Query File!!" + ex.getMessage()
					+ "\n\n" + ex.getStackTrace());
		}
		return queryStore;
	}

	public void getTopRankedDoc(Query searchQuery) {
		double docWeight1 = 0;
		double docWeight2 = 0;
		int docFreq = 0;
		int qCount = 1;

		StringBuilder qVectorToken = new StringBuilder();
		StringBuilder qVectorWeight1 = new StringBuilder();
		StringBuilder qVectorWeight2 = new StringBuilder();

		Map<Integer, Double> topDocWeight1 = new HashMap<Integer, Double>();
		Map<Integer, Double> topDocWeight2 = new HashMap<Integer, Double>();
		DecimalFormat df = new DecimalFormat("#.######");
		try {
			FileWriter fileWrite = new FileWriter(new File(outputFileWeight), false);

			qVectorToken.setLength(0);
			qVectorWeight1.setLength(0);
			qVectorWeight2.setLength(0);

			qVectorToken.append("<");
			qVectorWeight1.append("<");
			qVectorWeight2.append("<");

			for (Entry<Integer, Document> termDoc : docMap.entrySet()) {
				docWeight1 = 0;
				docWeight2 = 0;
				double normalizedWeight1 = 0;
				double normalizedWeight2 = 0;
				// Get all the terms in the query
				for (Entry<String, WeightToken> queryToken : searchQuery.queryVector.entrySet()) {
					int tf = 0;

					if (lemmaTokenMap.containsKey(queryToken.getKey())) {
						docFreq = lemmaTokenMap.get(queryToken.getKey()).termFrequency.size();
						if (lemmaTokenMap.get(queryToken.getKey()).termFrequency
								.containsKey(termDoc.getKey())) {
							tf = lemmaTokenMap.get(queryToken.getKey()).termFrequency
									.get(termDoc.getKey());
						}
					}
					queryToken.getValue().docWeight1 = computeWeight1(tf,
							docMap.get(termDoc.getKey()).maxTf, docFreq);
					queryToken.getValue().docWeight2 = computeWeight2(tf,
							docMap.get(termDoc.getKey()).docLen, docFreq);
					// Summing the weights of query terms
					docWeight1 += Math.pow(queryToken.getValue().docWeight1, 2);
					docWeight2 += Math.pow(queryToken.getValue().docWeight2, 2);
				}
				docWeight1 = Math.sqrt(docWeight1);
				docWeight2 = Math.sqrt(docWeight2);
				for (Entry<String, WeightToken> queryToken : searchQuery.queryVector.entrySet()) {
					// for each query term calculate the normalized weight
					// by dividing it by totalWeight
					if (docWeight1 != 0) {
						normalizedWeight1 += (queryToken.getValue().docWeight1 / docWeight1)
								* queryToken.getValue().weight1;
					}
					if (docWeight2 != 0) {
						normalizedWeight2 += (queryToken.getValue().docWeight2 / docWeight2)
								* queryToken.getValue().weight2;
					}
				}

				topDocWeight1.put(termDoc.getKey(), normalizedWeight1);
				topDocWeight2.put(termDoc.getKey(), normalizedWeight2);
			}
			for (Entry<String, WeightToken> queryToken : searchQuery.queryVector.entrySet()) {
				// write the query vector to file one token and its
				// weight
				// at a time
				qVectorToken.append(queryToken.getKey() + ",");
				qVectorWeight1.append(df.format(queryToken.getValue().weight1) + ",");
				qVectorWeight2.append(df.format(queryToken.getValue().weight2) + ",");
			}
			fileWrite.write("\nQ " + qCount + ") " + searchQuery.queryString + "\n");
			qVectorToken.replace(qVectorToken.length() - 1, qVectorToken.length(), ">");
			qVectorWeight1.replace(qVectorWeight1.length() - 1, qVectorWeight1.length(), ">");
			qVectorWeight2.replace(qVectorWeight2.length() - 1, qVectorWeight2.length(), ">");

			if (topDocWeight1.size() > 0) {
				searchQuery.topDocsWeight1 = sortByWeight(topDocWeight1);

				fileWrite.write("Query Tokens:\n");
				fileWrite.write(qVectorToken.toString() + "\n");
				fileWrite.write("Weight1:\nTop 5 Documents: " + searchQuery.topDocsWeight1 + "\n");
				fileWrite.write("Query Vector:\n" + qVectorWeight1.toString() + "\n");
				int rnk = 1;
				// Display the document Info for top 5 documents.#Weight1#
				for (Integer docid : searchQuery.topDocsWeight1) {
					fileWrite.write("\nDoc#" + docid + ", Rank: " + (rnk++) + ", Score: "
							+ ((double) topDocWeight1.get(docid)));
				}
				rnk = 1;
				searchQuery.topDocsWeight2 = sortByWeight(topDocWeight2);

				fileWrite
						.write("\nWeight2:\nTop 5 Documents: " + searchQuery.topDocsWeight2 + "\n");
				fileWrite.write("Query Vector:\n" + qVectorWeight2.toString() + "\n");

				// Display the document Info for top 5 documents.#Weight2#
				for (Integer docid : searchQuery.topDocsWeight2) {
					fileWrite.write("\nDoc#" + docid + ", Rank: " + (rnk++) + ", Score: "
							+ ((double) topDocWeight2.get(docid)));
				}

			} else {
				fileWrite.write("No matching documents found for Query");
			}

			topDocWeight1.clear();
			topDocWeight2.clear();
			qCount++;
			fileWrite.close();
			System.out.println("Weight File write successfull!!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception occured while writing file: " + ex.getMessage());
		}
	}

	public List<Integer> sortByWeight(Map<Integer, Double> topDocWeight) {

		List<Integer> topDocList = new ArrayList<Integer>();
		List<Map.Entry<Integer, Double>> docWeightList = new LinkedList<Map.Entry<Integer, Double>>(
				topDocWeight.entrySet());

		Collections.sort(docWeightList, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> w1, Map.Entry<Integer, Double> w2) {
				Double d1 = w1.getValue(); /// docMap.get(w1.getKey()).docLen;
				Double d2 = w2.getValue(); /// docMap.get(w2.getKey()).docLen;
				return (d1).compareTo(d2) * (-1);
			}
		});
		int count = 0;
		Iterator<Map.Entry<Integer, Double>> itrWeight = docWeightList.iterator();
		while (count < 5 && itrWeight.hasNext()) {
			Map.Entry<Integer, Double> wtEntry = itrWeight.next();
			topDocList.add(wtEntry.getKey());
			count++;
		}
		return topDocList;
	}

	public Query computeWeight(Query searchQuery) {
		double sumWeight1 = 0;
		double sumWeight2 = 0;
		for (Entry<String, WeightToken> queryToken : searchQuery.queryVector.entrySet()) {
			int docFreq = 0;
			if (lemmaTokenMap.containsKey(queryToken.getKey())) {
				docFreq = lemmaTokenMap.get(queryToken.getKey()).termFrequency.size();
			}
			queryToken.getValue().weight1 = computeWeight1(queryToken.getValue().termFreq,
					searchQuery.maxTf, docFreq);
			queryToken.getValue().weight2 = computeWeight2(queryToken.getValue().termFreq,
					searchQuery.docLen, docFreq);
			sumWeight1 += Math.pow(queryToken.getValue().weight1, 2);
			sumWeight2 += Math.pow(queryToken.getValue().weight2, 2);
		}
		sumWeight1 = Math.sqrt(sumWeight1);
		sumWeight2 = Math.sqrt(sumWeight2);
		for (Entry<String, WeightToken> queryToken : searchQuery.queryVector.entrySet()) {
			queryToken.getValue().weight1 = queryToken.getValue().weight1 / sumWeight1;
			queryToken.getValue().weight2 = queryToken.getValue().weight2 / sumWeight2;
		}
		return searchQuery;
	}

	public double computeWeight1(int termFreq, int maxTf, int docFreq) {
		// Consider only those documents that contain the query term
		// (termFreq!=0)
		// Consider only those terms that are present in at least one of the
		// document in the collection (docFreq!=0)
		if (docFreq != 0 && termFreq != 0) {
			return (0.4 + 0.6 * Math.log((double)termFreq + 0.5) / Math.log((double)maxTf + 1.0))
					* (Math.log((double) collectionSize / docFreq) / Math.log(collectionSize));
		}
		return 0;
	}

	public double computeWeight2(int termFreq, int docLen, int docFreq) {
		// Consider only those documents that contain the query term
		// (termFreq!=0)
		// Consider only those terms that are present in at least one of the
		// document in the collection (docFreq!=0)
		if (docFreq != 0 && termFreq != 0) {
			return (0.4 + 0.6
					* ((double) termFreq / (termFreq + 0.5 + 1.5 * ((double) docLen / avgDocLen)))
					* Math.log((double) collectionSize / docFreq) / Math.log(collectionSize));
		}
		return 0;
	}
}
