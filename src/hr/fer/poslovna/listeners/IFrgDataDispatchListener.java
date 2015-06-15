package hr.fer.poslovna.listeners;

public interface IFrgDataDispatchListener {
	public void dispatchData(String srcFrgCustomTag, String destFrgCustomTag, Object data);
}
