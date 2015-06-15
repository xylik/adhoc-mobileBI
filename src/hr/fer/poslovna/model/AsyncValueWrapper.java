package hr.fer.poslovna.model;

public class AsyncValueWrapper {
	public IResult result;
	public String stringVal;
	
	public AsyncValueWrapper(IResult result, String stringVal) {
		super();
		this.result = result;
		this.stringVal = stringVal;
	}
}