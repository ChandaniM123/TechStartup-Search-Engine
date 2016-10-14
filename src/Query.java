//Represents a query and stores the queryVector, MaxTermFreq and docLength
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Query {

	public String queryString;
	public Integer maxTf;
	public Integer docLen;
	public Map<String, WeightToken> queryVector;
	public List<Integer> topDocsWeight1;
	public List<Integer> topDocsWeight2;
	public Query(){}
	public Query(String queryString){
		this.queryString = queryString;
		this.queryVector = new HashMap<String, WeightToken>();
		this.topDocsWeight1 = new ArrayList<Integer>();
	}
}
