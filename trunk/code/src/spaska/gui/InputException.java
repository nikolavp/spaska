package spaska.gui;

import spaska.gui.engines.Engine;

/**
 * Basic exception for user input. Thrown by each {@link Engine}.check()
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public class InputException extends Exception{

	private static final long	serialVersionUID	= 1L;

	public InputException() {
		//
	}

	public InputException(String message) {
		super(message);
	}
}
