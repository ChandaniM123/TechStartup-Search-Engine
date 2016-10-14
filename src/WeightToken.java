//Represents a token in a query and stores the weight1, weight2 and termFrequency associated with 
//that token.
public class WeightToken {
	public double weight1;
	public double weight2;
	public double docWeight1; //field stores weight_1 of a query term in a document
	public double docWeight2; //field stores weight_2 of a query term in a document
	public Integer termFreq;
}
