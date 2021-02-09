package net.smoofyuniverse.keyring.linux;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;

/**
 * A keyring using Freedesktop Secret Service.
 * See https://wiki.gnome.org/Projects/Libsecret
 */
public class SecretServiceKeyring implements Keyring {
	private final SecretSchema schema;

	/**
	 * Creates a new keyring using Freedesktop Secret Service.
	 *
	 * @throws UnsupportedBackendException if the backend for this implementation is not available.
	 */
	public SecretServiceKeyring() throws UnsupportedBackendException {
		if (GLib.INSTANCE == null || Libsecret.INSTANCE == null)
			throw new UnsupportedBackendException("Failed to load native libraries");

		this.schema = new SecretSchema();
		this.schema.name = "net.smoofyuniverse.keyring.Password";
		this.schema.flags = 0;

		SecretSchemaAttribute[] attributes = this.schema.attributes;
		attributes[0] = new SecretSchemaAttribute("service");
		attributes[1] = new SecretSchemaAttribute("account");
		attributes[2] = new SecretSchemaAttribute("NULL");
	}

	@Override
	public String getBackendName() {
		return "Freedesktop Secret Service";
	}

	private static void handleError(PointerByReference ref) throws PasswordAccessException {
		Pointer ptr = ref.getValue();
		if (ptr != null) {
			GError error = new GError(ptr);
			String errorMsg = error.message;
			GLib.INSTANCE.g_error_free(error);
			throw new PasswordAccessException(errorMsg);
		}
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		Keyring.validateService(service);
		Keyring.validateAccount(account);

		PointerByReference error = new PointerByReference();
		Pointer password = Libsecret.INSTANCE.secret_password_lookup_sync(this.schema, null, error,
				"service", service, "account", account, null);

		handleError(error);

		if (password == null)
			return null;

		String passwordCopy = password.getString(0);
		Libsecret.INSTANCE.secret_password_free(password);
		return passwordCopy;
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		Keyring.validateService(service);
		Keyring.validateAccount(account);
		Keyring.validatePassword(password);

		PointerByReference error = new PointerByReference();

		if (password == null) {
			Libsecret.INSTANCE.secret_password_clear_sync(this.schema, null, error,
					"service", service, "account", account, null);
		} else {
			Libsecret.INSTANCE.secret_password_store_sync(this.schema, null, "A java-keyring password", password, null, error,
					"service", service, "account", account, null);
		}

		handleError(error);
	}
}
