package no.smileyface.discordbot.files.properties;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Represents a node in the property tree.
 */
public class PropertyNode {
	private static final PropertyNode ROOT = new PropertyNode();
	private static boolean loaded = false;

	private final Map<String, PropertyNode> children;

	private String value;

	/**
	 * Constructor.
	 */
	private PropertyNode() {
		setValue(null);
		this.children = new HashMap<>();
	}

	private static void throwLine(LineNumberReader reader) throws PropertyLoadException {
		throw new PropertyLoadException(String.format(
				"Invalid .properties formatting (Line %s)",
				reader.getLineNumber()
		));
	}

	/**
	 * Loads the property tree from the .properties file.
	 *
	 * @throws PropertyLoadException If loading the properties failed
	 */
	public static void loadTree() throws PropertyLoadException {
		Path filename;
		try (Stream<Path> fileStream = Files.list(FileSystems.getDefault().getPath(""))) {
			PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.properties");
			filename = fileStream
					.filter(file -> matcher.matches(file.getFileName()))
					.findFirst()
					.orElseThrow(() -> new PropertyLoadException("No .properties file found"));
		} catch (IOException ioe) {
			throw new PropertyLoadException(
					"Could not read files in bot directory, unable to find .properties file",
					ioe
			);
		}
		try (LineNumberReader reader = new LineNumberReader(new FileReader(filename.toFile()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isBlank()) {
					String[] splitLine = line.split("=", 2);
					if (splitLine.length < 2) {
						throwLine(reader);
					}
					String[] treePath = splitLine[0].strip().split("\\.");
					PropertyNode currentNode = ROOT;
					for (String node : treePath) {
						currentNode = currentNode.makeOrGetChild(node);
					}
					currentNode.setValue(splitLine[1].strip());
				}
			}
		} catch (IOException ioe) {
			throw new PropertyLoadException("Could not load the .properties file", ioe);
		}
		loaded = true;
	}

	/**
	 * Gets the root property node, and loads the property tree if it's not already loaded.
	 *
	 * @return The root property node, with the entire property tree loaded
	 */
	public static synchronized PropertyNode getRoot() {
		if (!loaded) {
			throw new IllegalStateException("Property tree is not loaded yet");
		}
		return ROOT;
	}

	public String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}

	public PropertyNode getChild(String name) {
		PropertyNode child = children.get(name);
		return child == null ? new PropertyNode() : child;
	}

	private PropertyNode makeOrGetChild(String name) {
		return children.computeIfAbsent(name, k -> new PropertyNode());
	}

	@Override
	public String toString() {
		return "PropertyNode{"
				+ "children=" + children
				+ ", value='" + value + '\''
				+ '}';
	}
}
