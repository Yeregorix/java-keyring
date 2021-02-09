package net.smoofyuniverse.keyring.linux;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class SecretSchemaAttribute extends Structure {
	public static final int SECRET_SCHEMA_ATTRIBUTE_STRING = 0;

	public String name;
	public int type;

	public SecretSchemaAttribute() {}

	public SecretSchemaAttribute(String name) {
		this.name = name;
		this.type = SECRET_SCHEMA_ATTRIBUTE_STRING;
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "type");
	}
}
