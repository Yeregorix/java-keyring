package net.east301.keyring.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;

/**
 * File based lock
 */
public class FileBasedLock {

    /**
     * Path to a file to be used to lock
     */
    private final Path path;

    /**
     * A File instance to be used to lock
     */
    private File file;

    /**
     * A FileChannel instance obtained from m_file
     */
    private FileChannel channel;

    /**
     * A FileLock instance obtained from m_channel
     */
    private FileLock lock;

    /**
     * Initializes an instance of FileBasedLock
     *
     * @param path  Path to a file to be used to lock
     */
    public FileBasedLock(Path path) {
        this.path = path;
    }

    /**
     * Lock
     */
    public synchronized void lock() throws LockException {
        if (this.file != null || this.channel != null || this.lock != null)
            throw new LockException("Already locked");

        try {
            this.file = this.path.toFile();
            this.file.createNewFile();

            this.channel = new RandomAccessFile(this.file, "rw").getChannel();
            this.lock = this.channel.lock();
        } catch (IOException e) {
            throw new LockException("Failed to obtain lock", e);
        }
    }

    /**
     * Release lock
     */
    public synchronized void release() throws LockException {
        try {
            if (this.lock != null && this.lock.isValid())
                this.lock.release();
        } catch (Exception e) {
            throw new LockException("Failed to release lock", e);
        }

        try {
            if (this.channel != null && this.channel.isOpen())
                this.channel.close();
        } catch (Exception e) {
           throw new LockException("Failed to close channel", e);
        }

        this.file = null;
        this.channel = null;
        this.lock = null;
    }

    /**
     * Returns path to a file to be used to lock
     */
    public Path getPath() {
        return this.path;
    }

}
