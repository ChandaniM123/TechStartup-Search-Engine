import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LuceneIndexer {

	/*public static Map<Integer, String> WGraph = new HashMap<Integer, String>();
	public static Map<Integer, String> WGraphPageRank = new HashMap<Integer, String>();
	public static Map<Integer, Document> resultDoc = new HashMap<Integer, Document>();*/
	IndexWriter indexWriter;
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

		/*String dirPath = "../Docs1/Data";
		String urlPath = "../NewUrl.txt";
		String docPageRankPath = "../docPageRank";
		String filePath = "";
		//String dirPath = "F://code//informationretrieval//Assignment1//IRDataTest";
		String queryString = "david law tennis podcast";
		String indexDir = "./luceneIndexDir/Index_New";
		String indexDirPR = "./luceneIndexDir/Index_PR";*/
		
		LuceneIndexer lIndexer = new LuceneIndexer();
		//lIndexer.createWebMap(urlPath);
		//lIndexer.assignPageRank(docPageRankPath);
		//lIndexer.readDirectoryLemma(dirPath, indexDir);
/*		lIndexer.searchIndex(queryString, indexDir);
		System.out.println("\n\nPage Rank Results:");
		lIndexer.searchIndexWithPageRank(queryString, indexDirPR);*/
		lIndexer.createIndex();
		
	}

	/*public void createWebMap(String filePath) {

		BufferedReader br = null;
		String inputLine = "";

		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				String[] nodeVal = inputLine.split("\t");
				WGraph.put(Integer.parseInt(nodeVal[0]), nodeVal[2]);
			}
		} catch (Exception ex) {
       System.out.println("Exception occured while creating web map");
		}
	}
	public void assignPageRank(String filePath){
		BufferedReader br = null;
		String inputLine = "";

		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				String[] nodeVal = inputLine.split(":");
				WGraphPageRank.put(Integer.parseInt(nodeVal[0]), nodeVal[1]);
			}
		} catch (Exception ex) {
       System.out.println("Exception occured while creating web map");
		}
		
	}*/
	
	public void createIndex() throws FileNotFoundException, IOException, ParseException {

		JSONArray jsonObjects = parseJSONFiles();
	
			openIndex();
		
		addDocuments(jsonObjects);
		finish();
		/*BufferedReader br = null;
		String inputLine = "";
		String docText = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				docText += inputLine;
			}

			Document doc = new Document();
			doc.add(new StringField("id", docId, Field.Store.YES));
			doc.add(new StringField("url", WGraph.get(Integer.parseInt(docId)), Field.Store.YES));
			doc.add(new TextField("content", docText, Field.Store.YES));
			
		
			indexWriter.addDocument(doc);
		} catch (Exception ex) {
			ex.printStackTrace();

			System.out.println(
					"Exception occured in retrieving tokens!!" + ex.getMessage() + "\n\n" + ex.getStackTrace());
		}*/
		
	}
	
	public JSONArray parseJSONFiles() throws FileNotFoundException, IOException, ParseException{
		File folder = new File("JSONFiles");
		JSONArray jsonArray = new JSONArray();
		System.out.println(jsonArray.size());
		if (folder != null) {
			File[] fileList = folder.listFiles();
			for (File file : fileList) {
				if (file.isFile()) {
					// docId = file.getPath().substring(17,
					// file.getPath().length() - 4);
					String filename = file.getPath();
					JSONParser jsonParser = new JSONParser();
					Object obj;

					obj = jsonParser.parse(new FileReader(filename));

					JSONObject jsonObject = (JSONObject) obj;
					jsonArray.add(jsonObject);
					System.out.println(jsonObject.get("title"));

				}
			}
		}

		System.out.println(jsonArray.size());
		return jsonArray;
	}
	
	public boolean openIndex() throws IOException {
		try{
		Path indexDirPath = FileSystems.getDefault().getPath("./luceneIndexDir/Index_New");

		Directory dir = FSDirectory.open(indexDirPath);

		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		config.setOpenMode(OpenMode.CREATE);

		indexWriter = new IndexWriter(dir, config);
		
		return true;
		}
		catch(Exception e){
			System.out.println("Exception" + e);
		}

		return false;
	}
	
	public void addDocuments(JSONArray jsonObjects){
		for(JSONObject object : (List<JSONObject>) jsonObjects){
            Document doc = new Document();
            for(String field : (Set<String>) object.keySet()){
                Class type = object.get(field).getClass();
                if(type.equals(String.class)){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.NO));
                }else if(type.equals(Boolean.class)){
                    doc.add(new StringField(field, object.get(field).toString(), Field.Store.YES));
                }
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
		
	}
	
	public void finish(){
		try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
	}
	
	
	/*public void readDirectoryLemma(String dirPath, String indexDir) {
		String docId = "";
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);
		try {
			File folder = new File("JSONFiles");
			Directory dir = FSDirectory.open(indexDirPath);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter indexWriter = new IndexWriter(dir, config);
			if (folder != null) {
				File[] fileList = folder.listFiles();
				for (File file : fileList) {
					if (file.isFile()) {
						//docId = file.getPath().substring(17, file.getPath().length() - 4);
						String filename = file.getPath();
						//docId = filename.substring(file.getParent().length() + "Cranfield".length() + 1, filename.length());
						//docId = filename.substring(file.getParent().length() + "Doc".length() + 1, filename.length() - 4);
						
						//System.out.println("Processing Doc#" + docId + "......");
						JSONParser jsonParser = new JSONParser();
						Object obj = jsonParser.parse(new FileReader(filename));
						JSONObject jsonObject = (JSONObject) obj;
						createIndex(file.getPath(), docId, indexWriter);
					}
				}
			}
			indexWriter.close();
		} catch (Exception ex) {
			System.out.println("Exception occured while reading files:" + ex.getMessage() + "\n");
			ex.printStackTrace();
		}
	}

	public void searchIndex(String queryString, String indexDir) {
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);

		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs collector = searcher.search(query, 10);
			ScoreDoc[] hits = collector.scoreDocs;
			for(int i =0;i<10;i++){
				Document d = searcher.doc(hits[i].doc);
			    System.out.println(d.get("id") + "-->" + d.get("url") + "  -->" +d.get("pagerank"));
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception occured while searching index:" + ex.getMessage());
		}
	}
	public void searchIndexWithPageRank(String queryString, String indexDir) {
		Path indexDirPath = FileSystems.getDefault().getPath(indexDir);

		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirPath)));
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs collector = searcher.search(query, 10);
			ScoreDoc[] hits = collector.scoreDocs;
			for(int i =0;i<10;i++){
				Document d = searcher.doc(hits[i].doc);
				resultDoc.put(i, d);
			    //System.out.println(d.get("id") + "-->" + d.get("url") + "-->" +d.get("pagerank"));
			}
			
			Map<Integer, Document> rankedPage = rankByPageRank(resultDoc);
			for(int i =0;i<10;i++){
				Document d = rankedPage.get(i);
				System.out.println(d.get("id") + "-->" + d.get("url") + "  -->" +d.get("pagerank"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception occured while searching index:" + ex.getMessage());
		}
	}

	public Map<Integer, Document> rankByPageRank(Map<Integer, Document> resultDoc) {

		List<Map.Entry<Integer, Document>> docRankList = new LinkedList<Map.Entry<Integer, Document>>(resultDoc.entrySet());

		Collections.sort(docRankList, new Comparator<Map.Entry<Integer, Document>>() {
			public int compare(Map.Entry<Integer, Document> doc1, Map.Entry<Integer, Document> doc2) {
				Double d1 = Double.parseDouble(doc1.getValue().get("pagerank")); /// docMap.get(w1.getKey()).docLen;
				Double d2 = Double.parseDouble(doc2.getValue().get("pagerank")); /// docMap.get(w2.getKey()).docLen;
				return (d1).compareTo(d2) * (-1);
			}
		});
		int count = 0;
		Map<Integer, Document> topDocList = new LinkedHashMap<Integer, Document>();
		for(Entry<Integer, Document> entry: docRankList){
			topDocList.put(count, entry.getValue());
		    count++;
		}
		return topDocList;
	}
	
	
	
	public void createIndexWithPageRank(String filePath, String docId, IndexWriter indexWriter) {

		BufferedReader br = null;
		String inputLine = "";
		String docText = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((inputLine = br.readLine()) != null) {
				docText += inputLine;
			}

			Document doc = new Document();
			doc.add(new StringField("id", docId, Field.Store.YES));
			doc.add(new StringField("url", WGraph.get(Integer.parseInt(docId)), Field.Store.YES));
			doc.add(new StringField("pagerank", WGraphPageRank.get(Integer.parseInt(docId)), Field.Store.YES));
			doc.add(new TextField("content", docText, Field.Store.YES));
		
			indexWriter.addDocument(doc);
		} catch (Exception ex) {
			ex.printStackTrace();

			System.out.println(
					"Exception occured in retrieving tokens!!" + ex.getMessage() + "\n\n" + ex.getStackTrace());
		}*/
	
}
