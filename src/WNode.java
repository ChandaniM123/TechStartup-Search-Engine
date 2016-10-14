
public class WNode {

	public Integer nodeId;
	public String nodeurl;
	public Double pageRank;
	
	public WNode(){}
	public WNode(Integer nodeId, String nodeurl){
		this.nodeId = nodeId;
		this.nodeurl = nodeurl;
	}
	public WNode(String nodeurl){
		this.nodeurl = nodeurl;
	}
}
