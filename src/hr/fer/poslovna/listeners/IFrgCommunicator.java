package hr.fer.poslovna.listeners;

public interface IFrgCommunicator {
	public void register(IFrgDataReceiveListener listener);
	public void unregister(IFrgDataReceiveListener listener);
	public void dispatchData(String srcFrgCustomTag, String destFrgCustomTag, Object data);
	public void requestPageChange(int position);
}
