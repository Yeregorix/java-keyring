package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.PointerByReference;
import net.smoofyuniverse.keyring.util.NativeUtil;

/**
 * Advanced Windows API.
 */
public interface Advapi32 extends Library {
	Advapi32 INSTANCE = NativeUtil.loadOrNull("Advapi32", Advapi32.class);

	int ERROR_NOT_FOUND = 1168;

	/**
	 * @param TargetName name of credential in store
	 * @param Type       cred type
	 * @param Flags      always zero
	 * @param Credential credential pointer
	 * @return success or failure.
	 */
	boolean CredReadA(
			String TargetName,
			DWORD Type,
			DWORD Flags,
			PointerByReference Credential
	);

	/**
	 * @param Credential credential pointer
	 * @param Flags      always zero
	 * @return success or failure.
	 */
	boolean CredWriteA(
			CREDENTIAL Credential,
			DWORD Flags
	);

	/**
	 * @param Credential who's memory we'll free.
	 * @return success or failure.
	 */
	boolean CredFree(
			Pointer Credential
	);

	/**
	 * @param TargetName name of credential in store
	 * @param Type       cred type
	 * @param Flags      always zero
	 * @return success or failure.
	 */
	boolean CredDeleteA(
			String TargetName,
			DWORD Type,
			DWORD Flags
	);
}
