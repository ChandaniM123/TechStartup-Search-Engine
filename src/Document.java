import java.util.Map;

public class Document {

	public Integer maxTf;
	public Integer docLen;
	public String dVector;
	public String dVectorWeight1;
	public String dVectorWeight2;
	public String docTitle;
	public Document(){}
	public Document(int maxTf, int docLen){
		this.maxTf = maxTf;
		this.docLen = docLen;
	}
}
