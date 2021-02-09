package net.smoofyuniverse.keyring.linux;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class GError extends Structure {
	public int domain;
	public int code;
	public String message;

	public GError(Pointer ptr) {
		super(ptr);
		read();
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("domain", "code", "message");
	}
}
