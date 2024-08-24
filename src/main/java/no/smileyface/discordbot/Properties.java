package no.smileyface.discordbot;

import no.smileyface.discordbotframework.data.Node;

/**
 * Represents a node in the property tree.
 */
public class Properties {
	private static Properties instance;
	private static boolean loaded = false;

	private final Node<String, String> root;

	/**
	 * Constructor.
	 */
	private Properties(Node<String, String> root) {
		this.root = root;
	}

	/**
	 * Calls {@link Node#getChild(Object)} on the root property node.
	 *
	 * @param key The key to find a child node for
	 * @return The found child node, or an empty node if no node is found
	 * @throws IllegalStateException If {@link #initialize(Node)} has not been called yet
	 */
	public static Node<String, String> getChild(String key) {
		if (!loaded) {
			throw new IllegalStateException("Properties are not initialized");
		}
		return instance.root.getChild(key);
	}

	/**
	 * Initializes properties.
	 *
	 * @param propertyNode The root property node
	 * @throws IllegalStateException If this has already been called
	 */
	static synchronized void initialize(Node<String, String> propertyNode) {
		if (loaded) {
			throw new IllegalStateException("Properties are already initialized");
		}
		instance = new Properties(propertyNode);
		loaded = true;
	}
}
