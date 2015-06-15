package hr.fer.poslovna.listeners;

public interface IFrgDataReceiveListener {
	public void receiveData(String srcFrgCustomTag, Object data);
	public String getSystemTag();
	public String getCustomTag();
}