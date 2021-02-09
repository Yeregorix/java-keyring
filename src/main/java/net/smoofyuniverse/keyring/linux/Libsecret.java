package net.smoofyuniverse.keyring.linux;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import net.smoofyuniverse.keyring.util.NativeUtil;

public interface Libsecret extends Library {
	Libsecret INSTANCE = NativeUtil.loadOrNull("secret-1", Libsecret.class);

	Pointer secret_password_lookup_sync(SecretSchema schema, Pointer cancellable, PointerByReference error, Object... args);

	void secret_password_free(Pointer password);

	boolean secret_password_store_sync(SecretSchema schema, String collection, String label, String password,
									   Pointer cancellable, PointerByReference error, Object... args);

	boolean secret_password_clear_sync(SecretSchema schema, Pointer cancellable, PointerByReference error, Object... args);
}
