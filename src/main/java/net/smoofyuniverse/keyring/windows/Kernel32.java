package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import net.smoofyuniverse.keyring.util.NativeUtil;

public interface Kernel32 extends Library {
	Kernel32 INSTANCE = NativeUtil.loadOrNull("Kernel32", Kernel32.class);

	int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x00000100;
	int FORMAT_MESSAGE_FROM_SYSTEM     = 0x00001000;
	int FORMAT_MESSAGE_IGNORE_INSERTS  = 0x00000200;

	int GetLastError();

	Pointer LocalFree(Pointer hMem);

	int FormatMessageA(int dwFlags, Pointer lpSource, int dwMessageId,
					   int dwLanguageId, PointerByReference lpBuffer, int nSize,
					   Pointer va_list);
}
