package no.smileyface.discordbot.files.generic;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A generic implementation of {@link CollectionFileInterface} with {@code long}.
 */
public class LongCollectionFileInterface extends CollectionFileInterface<Long> {
	protected LongCollectionFileInterface(String path) throws IOException {
		super(path, 8);
	}

	@Override
	protected final Long itemFromBytes(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}

	@Override
	protected final byte[] itemToBytes(Long item) {
		return ByteBuffer.allocate(8).putLong(item).array();
	}
}
