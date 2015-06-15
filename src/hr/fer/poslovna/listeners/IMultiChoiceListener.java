package hr.fer.poslovna.listeners;

import android.content.DialogInterface;

public interface IMultiChoiceListener {
	public void listenMultiChoice(DialogInterface dialog, int which, boolean isChecked);
}