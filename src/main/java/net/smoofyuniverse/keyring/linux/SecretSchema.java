package net.smoofyuniverse.keyring.linux;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class SecretSchema extends Structure {
	public String name;
	public int flags;
	public SecretSchemaAttribute[] attributes = new SecretSchemaAttribute[32];

	// reserved
	public int reserved;
	public Pointer reserved1;
	public Pointer reserved2;
	public Pointer reserved3;
	public Pointer reserved4;
	public Pointer reserved5;
	public Pointer reserved6;
	public Pointer reserved7;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "flags", "attributes", "reserved", "reserved1",
				"reserved2", "reserved3", "reserved4", "reserved5", "reserved6", "reserved7");
	}
}
