package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;

import java.util.Arrays;
import java.util.List;

public class CREDENTIAL extends Structure {
	public int Flags;
	public int Type;
	public String TargetName;
	public String Comment;
	public FILETIME LastWritten;
	public int CredentialBlobSize;
	public Pointer CredentialBlob;
	public int Persist;
	public int AttributeCount;
	public Pointer Attributes;
	public String TargetAlias;
	public String UserName;

	public CREDENTIAL() {}

	public CREDENTIAL(Pointer ptr) {
		super(ptr);
		read();
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("Flags", "Type", "TargetName", "Comment", "LastWritten", "CredentialBlobSize",
				"CredentialBlob", "Persist", "AttributeCount", "Attributes", "TargetAlias", "UserName");
	}
}
