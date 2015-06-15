package hr.fer.poslovna.listeners;

public interface IFrgRegistration {
	public void register(IFrgDataReceiveListener listener);
	public void unregister(IFrgDataReceiveListener listener);
}
