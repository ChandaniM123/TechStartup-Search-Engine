import java.util.Map;
import java.util.TreeMap;

public class Token {

	public long frequency;
	public Map<Integer, Integer> termFrequency;
	public Token(){
		frequency = 1;
	}
	
	public Token(int docId){
		frequency = 1;
		termFrequency =  new TreeMap<Integer,Integer>();
		termFrequency.put(docId, 1);
	}
	
	public Token(long frequency){
		this.frequency = frequency;
	}
	
	public Token(Token copyToken){
		this.frequency = copyToken.frequency;
		this.termFrequency = copyToken.termFrequency;
	}
}
