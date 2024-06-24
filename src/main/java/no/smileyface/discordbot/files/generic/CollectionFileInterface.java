package no.smileyface.discordbot.files.generic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for persistent storage of collections, serialized as bytes.
 * <p><b>Note: Elements in this collection should <i>not</i> be modified after being added,
 * as this will not save the updated element</b></p>
 *
 * @param <T> The collection type
 */
public abstract class CollectionFileInterface<T>
		extends FileInterface<Collection<T>>
		implements Collection<T> {
	private static final Logger LOGGER = Logger.getLogger(CollectionFileInterface.class.getName());

	private final Collection<T> collection;
	private final int fixedLength;

	/**
	 * Creates the file interface, with dynamic length for each serialized element.
	 * The max length for a serialized element is {@link Integer#MAX_VALUE}.
	 *
	 * @param path The path to the file this interface is for
	 * @throws IOException If the file at the provided path cannot be read
	 */
	protected CollectionFileInterface(String path) throws IOException {
		this(path, -1);
	}

	/**
	 * Creates the file interface, with a fixed length for each serialized element.
	 * This saves 4 bytes of memory per element, as the length of each element (a 4-byte integer)
	 * does not need to be stored with it.
	 *
	 * @param path The path to the file this interface is for
	 * @param fixedLength The fixed length of each serialized element
	 * @throws IOException If the file at the provided path cannot be read
	 */
	protected CollectionFileInterface(String path, int fixedLength) throws IOException {
		super(path, false);
		this.fixedLength = fixedLength;
		load();
		this.collection = get();
	}

	private <U> boolean ioSuccessOrLog(IoFunction<U, Boolean> ioFunction, U item) {
		boolean returnValue;
		try {
			returnValue = ioFunction.apply(item);
		} catch (IOException ioe) {
			returnValue = false;
			LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
		}
		return returnValue;
	}

	private interface IoFunction<U, R> {
		R apply(U item) throws IOException;
	}

	@Override
	public int size() {
		return collection.size();
	}

	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return new IoIterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return collection.toArray();
	}

	@NotNull
	@Override
	public <T1> T1[] toArray(@NotNull T1[] a) {
		return collection.toArray(a);
	}

	/**
	 * {@inheritDoc}
	 * <p>A call to {@link #addChecked(Object)} is preferred over this.</p>
	 *
	 * @see #addChecked(Object)
	 */
	@Override
	public boolean add(T t) {
		return ioSuccessOrLog(this::addChecked, t);
	}

	/**
	 * Same as {@link #add(Object)}, but throws an {@link IOException}
	 * if saving the collection happens to throw one.
	 * A call to this is preferred over {@link #add(Object)}.
	 *
	 * @param t Element whose presence in this collection is to be ensured
	 * @return True if this collection changed as a result of the call
	 * @throws IOException If saving the collection to a file threw one
	 */
	public boolean addChecked(T t) throws IOException {
		boolean changed = collection.add(t);
		if (changed) {
			save();
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 * <p>A call to {@link #removeChecked(Object)} is preferred over this.</p>
	 *
	 * @see #removeChecked(Object)
	 */
	@Override
	public boolean remove(Object o) {
		return ioSuccessOrLog(this::removeChecked, o);
	}

	/**
	 * Same as {@link #remove(Object)}, but throws an {@link IOException}
	 * if saving the collection happens to throw one.
	 * A call to this is preferred over {@link #remove(Object)}.
	 *
	 * @param o Element to be removed from this collection, if present
	 * @return True if an element was removed as a result of this call
	 * @throws IOException If saving the collection to a file threw one
	 */
	public boolean removeChecked(Object o) throws IOException {
		boolean removed = collection.remove(o);
		if (removed) {
			save();
		}
		return removed;
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return collection.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 * <p>A call to {@link #addAllChecked(Collection)} is preferred over this.</p>
	 *
	 * @see #addAllChecked(Collection)
	 */
	@Override
	public boolean addAll(@NotNull Collection<? extends T> c) {
		return ioSuccessOrLog(this::addAllChecked, c);
	}

	/**
	 * Same as {@link #addAll(Collection)}, but throws an {@link IOException}
	 * if saving the collection happens to throw one.
	 * A call to this is preferred over {@link #addAll(Collection)}.
	 *
	 * @param c Collection containing elements to be added to this collection
	 * @return True if this collection changed as a result of the call
	 * @throws IOException If saving the collection to a file threw one
	 */
	public boolean addAllChecked(@NotNull Collection<? extends T> c) throws IOException {
		boolean changed = collection.addAll(c);
		if (changed) {
			save();
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 * <p>A call to {@link #removeAllChecked(Collection)} is preferred over this.</p>
	 *
	 * @see #removeAllChecked(Collection)
	 */
	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return ioSuccessOrLog(this::removeAllChecked, c);
	}

	/**
	 * Same as {@link #removeAll(Collection)}, but throws an {@link IOException}
	 * if saving the collection happens to throw one.
	 * A call to this is preferred over {@link #removeAll(Collection)}.
	 *
	 * @param c Collection containing elements to be removed from this collection
	 * @return True if this collection changed as a result of the call
	 * @throws IOException If saving the collection to a file threw one
	 */
	public boolean removeAllChecked(@NotNull Collection<?> c) throws IOException {
		boolean changed = collection.removeAll(c);
		if (changed) {
			save();
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 * <p>A call to {@link #retainAllChecked(Collection)} is preferred over this.</p>
	 *
	 * @see #retainAllChecked(Collection)
	 */
	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return ioSuccessOrLog(this::retainAllChecked, c);
	}

	/**
	 * Same as {@link #retainAll(Collection)}, but throws an {@link IOException}
	 * if saving the collection happens to throw one.
	 * A call to this is preferred over {@link #retainAll(Collection)}.
	 *
	 * @param c Collection containing elements to be retained in this collection
	 * @return True if this collection changed as a result of the call
	 * @throws IOException If saving the collection to a file threw one
	 */
	public boolean retainAllChecked(@NotNull Collection<?> c) throws IOException {
		boolean changed = collection.retainAll(c);
		if (changed) {
			save();
		}
		return changed;
	}

	@Override
	public void clear() {
		collection.clear();
	}

	/**
	 * Create an item from an array of serialized bytes.
	 *
	 * @param bytes The array of serialized bytes to create an item from
	 * @return The created item
	 */
	protected abstract T itemFromBytes(byte[] bytes);

	/**
	 * Serializes a single item to an array of bytes.
	 *
	 * @param item The item to serialize
	 * @return The serialized array of bytes
	 */
	protected abstract byte[] itemToBytes(T item);

	@Override
	protected final Collection<T> fromBytes(byte[] bytes) {
		Collection<T> items = new HashSet<>();
		int currentPosition = 0;
		while (currentPosition < bytes.length) {
			int length;
			if (fixedLength == -1) {
				length = ByteBuffer.wrap(Arrays.copyOfRange(
						bytes,
						currentPosition,
						currentPosition + 4
				)).getInt();
				currentPosition += 4;
			} else {
				length = fixedLength;
			}

			items.add(itemFromBytes(Arrays.copyOfRange(
					bytes,
					currentPosition,
					currentPosition + length
			)));
			currentPosition += length;
		}
		return items;
	}

	@Override
	protected final byte[] toBytes(Collection<T> value) {
		return value
				.stream()
				.map(item -> {
					byte[] bytes;
					if (fixedLength == -1) {
						byte[] itemBytes = itemToBytes(item);
						byte[] lengthBytes = ByteBuffer
								.allocate(4)
								.putInt(itemBytes.length)
								.array();
						bytes = new byte[itemBytes.length + 4];
						System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
						System.arraycopy(itemBytes, 0, bytes, 4, itemBytes.length);
					} else {
						bytes = itemToBytes(item);
						if (bytes.length != fixedLength) {
							throw new IllegalStateException("An item got serialized to a length "
									+ "different from the set fixed length");
						}
					}
					return bytes;
				}).collect(
						ByteArrayOutputStream::new,
						ByteArrayOutputStream::writeBytes,
						(a, b) -> a.writeBytes(b.toByteArray())
				)
				.toByteArray();
	}

	/**
	 * Iterator that will make sure to keep the file updated
	 * if changes are made to the underlying collection during iteration.
	 */
	public class IoIterator implements Iterator<T> {
		private final Iterator<T> collectionIterator;
		private T currentNext;

		private IoIterator() {
			this.collectionIterator = collection.iterator();
		}

		@Override
		public boolean hasNext() {
			return collectionIterator.hasNext();
		}

		@Override
		public T next() {
			currentNext = collectionIterator.next();
			return currentNext;
		}

		/**
		 * {@inheritDoc}
		 * <p>A call to {@link #removeChecked()} is preferred over this.</p>
		 *
		 * @see #removeChecked()
		 */
		@Override
		public void remove() {
			try {
				this.removeChecked();
			} catch (IOException ioe) {
				LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
			}
		}

		/**
		 * Same as {@link #remove()}, but throws an {@link IOException}
		 * if saving the underlying collection happens to throw one.
		 * A call to this is preferred over {@link #remove()}.
		 *
		 * @throws IOException If saving the collection to a file threw one
		 */
		public void removeChecked() throws IOException {
			collectionIterator.remove();
			CollectionFileInterface.this.removeChecked(currentNext);
		}

		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			collectionIterator.forEachRemaining(action);
		}
	}
}
