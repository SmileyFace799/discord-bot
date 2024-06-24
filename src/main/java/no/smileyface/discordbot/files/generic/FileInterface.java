package no.smileyface.discordbot.files.generic;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An interface for persistent storage of any object, serialized as bytes.
 *
 * @param <T> The object type to store
 */
public abstract class FileInterface<T> {
	private static final Path BASE_PATH = FileSystems.getDefault().getPath("botFiles");

	private final Path path;
	private T value;

	/**
	 * Creates the file interface.
	 *
	 * @param path The path to the file this interface is for
	 * @throws IOException If the file at the provided path cannot be read
	 */
	protected FileInterface(String path) throws IOException {
		this(path, true);
	}

	/**
	 * Creates the file interface.
	 *
	 * @param path The path to the file this interface is for
	 * @param load If the stored value should be immediately loaded from the serialized file
	 * @throws IOException If the file at the provided path cannot be read
	 */
	protected FileInterface(String path, boolean load) throws IOException {
		this.path = BASE_PATH.resolve(path);
		try {
			Files.createFile(this.path);
		} catch (FileAlreadyExistsException ignored) {
			// File exists, all good
		}
		if (load) {
			load();
		}
	}

	/**
	 * Get the stored value.
	 *
	 * @return The stored value
	 */
	protected final T get() {
		return value;
	}

	/**
	 * Set the stored value.
	 *
	 * @param value The stored value.
	 * @throws IOException If The new value can't be saved
	 */
	protected final void set(T value) throws IOException {
		this.value = value;
		save();
	}

	protected final void load() throws IOException {
		this.value = fromBytes(Files.readAllBytes(this.path));
	}

	protected final void save() throws IOException {
		Files.write(path, toBytes(value));
	}

	/**
	 * Converts the serialized value into a real values.
	 *
	 * @param bytes The bytes of the serialized value
	 * @return The recreated value
	 */
	protected abstract T fromBytes(byte[] bytes);

	/**
	 * Serializes the stored value into bytes.
	 *
	 * @param value The stored value to serialize
	 * @return The serialized bytes
	 */
	protected abstract byte[] toBytes(T value);
}
