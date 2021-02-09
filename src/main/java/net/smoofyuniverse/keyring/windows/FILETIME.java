package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class FILETIME extends Structure {
	public int dwLowDateTime;
	public int dwHighDateTime;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("dwLowDateTime", "dwHighDateTime");
	}
}
