
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several tree-based data structures in "pretty" JSON format where
 * newlines are used to separate elements, and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author Serena Pang
 * @version Spring 2019
 */
public class PrettyJSONWriter {
	/**
	 * FORMATTER decimal formatter
	 */
	private static DecimalFormat FORMATTER = new DecimalFormat("0.00000000");

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 * 
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 * 
	 * @param elements the element to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		writer.write('[');
		writer.write('\n');

		if (!elements.isEmpty()) {
			for (Integer element : elements.headSet(elements.last())) {
				indent(writer, level + 1);
				writer.write(element.toString());
				writer.write(",");
				writer.write("\n");

			}
			indent(writer, level + 1);
			writer.write(elements.last().toString());
			writer.write("\n");
		}
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * 
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {
		writer.write("{");
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			String theFirstKey = elements.firstKey();
			PrettyJSONWriter.indent(writer, level + 1);
			writer.write("\"" + theFirstKey + "\": " + elements.get(theFirstKey).toString());
			String theNextKey = elements.higherKey(theFirstKey);

			while (theNextKey != null) {
				writer.write(",");
				writer.write(System.lineSeparator());

				PrettyJSONWriter.indent(writer, level + 1);
				writer.write("\"" + theNextKey + "\": " + elements.get(theNextKey).toString());

				theNextKey = elements.higherKey(theNextKey);
			}

			writer.write(System.lineSeparator());
		}
		writer.write("}");

	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object.
	 *
	 * 
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write("{");
		writer.write("\n");
		if (!elements.isEmpty()) {
			for (String key : elements.headMap(elements.lastKey()).keySet()) {
				indent(writer, level + 1);
				quote(key, writer);
				writer.write(": ");
				asArray(elements.get(key), writer, level + 1);
				writer.write(",");
				writer.write("\n");
			}
			indent(writer, level + 1);
			quote(elements.lastKey(), writer);
			writer.write(": ");
			asArray(elements.get(elements.lastKey()), writer, level + 1);
			writer.write("\n");
		}
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}

	/**
	 * Writes the elements as a Double TreeMap nested pretty JSON object.
	 * 
	 * @param mapOfElements the elements to use
	 * @return containing the elements in pretty JSON format
	 */
	public static String asDoubleTreeMapNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> mapOfElements) {
		try {
			StringWriter writer = new StringWriter();
			asDoubleTreeMapNestedObject(mapOfElements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a Double TreeMap nested pretty JSON object to file.
	 * 
	 * @param mapOfElements the elements to use
	 * @param path          the file path to use
	 * @throws IOException
	 */
	public static void asDoubleTreeMapNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> mapOfElements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleTreeMapNestedObject(mapOfElements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a Double TreeMap nested pretty JSON object.
	 * 
	 * @param mapOfWordPathInvertedIndex the elements to write
	 * @param writer                     the writer path to use
	 * @param level                      the initial indent level
	 * @throws IOException
	 */
	public static void asDoubleTreeMapNestedObject(
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> mapOfWordPathInvertedIndex, Writer writer, int level)
			throws IOException {
		indent(writer, level);
		writer.write("{");
		writer.write("\n");
		if (!mapOfWordPathInvertedIndex.isEmpty()) {
			String firstKey = mapOfWordPathInvertedIndex.firstKey();
			indent(writer, level + 1);
			writer.write("\"" + firstKey + "\": ");
			PrettyJSONWriter.asNestedObject(mapOfWordPathInvertedIndex.get(firstKey), writer, level + 1);
			String nextKey = mapOfWordPathInvertedIndex.higherKey(firstKey);
			while (nextKey != null) {
				writer.write(",");
				writer.write(System.lineSeparator());
				indent(writer, level + 1);
				writer.write("\"" + nextKey + "\": ");
				PrettyJSONWriter.asNestedObject(mapOfWordPathInvertedIndex.get(nextKey), writer, level + 1);
				nextKey = mapOfWordPathInvertedIndex.higherKey(nextKey);
			}
			writer.write(System.lineSeparator());
		}
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Writes the search result of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param searchResult the search result to convert to JSON
	 * @param resultPath   the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asSearchResult(TreeMap<String, ArrayList<SearchResult>> searchResult, Path resultPath)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(resultPath, StandardCharsets.UTF_8)) {
			asSearchResult(searchResult, writer, 0);
		}
	}

	/**
	 * Write the query word and the corresponding search result out using JSON
	 * format
	 * 
	 * @param searchResult search result data structure
	 * @param writer       bufferedWriter to write to the file
	 * @param level        indentation level, start from 0
	 * @throws IOException
	 */
	public static void asSearchResult(TreeMap<String, ArrayList<SearchResult>> searchResult, Writer writer, int level)
			throws IOException {
		indent(writer, level);
		writer.write("{");
		writer.write(System.lineSeparator());
		if (!searchResult.isEmpty()) {
			Iterator<String> iterator = searchResult.keySet().iterator();
			String current = iterator.next();
			asQuery(current, searchResult.get(current), writer, level + 1);
			while (iterator.hasNext()) {
				current = iterator.next();
				writer.write(",");
				writer.write(System.lineSeparator());
				asQuery(current, searchResult.get(current), writer, level + 1);
			}
		}
		writer.write(System.lineSeparator());
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Write the query word and the corresponding search result out using JSON
	 * format
	 * 
	 * @param query  query word
	 * @param list   list of search results
	 * @param writer bufferedWriter to write to the file
	 * @param level  indentation level, start from 0
	 * @throws IOException
	 * 
	 * @see #asArrayList(ArrayList, Writer, int)
	 * @see #quote(String, Writer)
	 */
	private static void asQuery(String query, ArrayList<SearchResult> list, Writer writer, int level)
			throws IOException {
		indent(writer, level);
		quote(query, writer);
		writer.write(": ");
		writer.write("[");
		asArrayList(list, writer, level);
	}

	/**
	 * Write the array list of search result out using JSON format
	 * 
	 * @param elements array list of search result
	 * @param writer   bufferedWriter to write to the file
	 * @param level    indentation level, start from 0
	 * @throws IOException
	 * 
	 */
	private static void asArrayList(ArrayList<SearchResult> elements, Writer writer, int level) throws IOException {
		if (!elements.isEmpty()) {
			writer.write(System.lineSeparator());
			var iterator = elements.iterator();
			asOneSearchResult(iterator.next(), writer, level + 1);
			while (iterator.hasNext()) {
				writer.write(",");
				writer.write(System.lineSeparator());
				asOneSearchResult(iterator.next(), writer, level + 1);
			}
		}
		writer.write(System.lineSeparator());
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writer one search result out using JSON format
	 * 
	 * @param elements one search result
	 * @param writer   bufferedWriter to write to the file
	 * @param level    indentation level
	 * @throws IOException
	 * 
	 * @see SearchResult
	 */
	private static void asOneSearchResult(SearchResult elements, Writer writer, int level) throws IOException {
		indent(writer, level);
		writer.write("{");
		if (elements != null) {
			writer.write(System.lineSeparator());
			indent(writer, level + 1);
			quote("where", writer);
			writer.write(": ");
			quote(elements.getPath(), writer);
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(writer, level + 1);
			quote("count", writer);
			writer.write(": ");
			writer.write(String.valueOf(elements.getCount()));
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(writer, level + 1);
			quote("score", writer);
			writer.write(": ");
			writer.write(FORMATTER.format(elements.getScore()));
		}
		writer.write(System.lineSeparator());
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * A simple main method that demonstrates this class.
	 *
	 * @param args unused
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Modify as needed to debug!
		TreeSet<Integer> elements = new TreeSet<>();
		System.out.println("Empty:");
		System.out.println(asArray(elements));

		elements.add(65);
		System.out.println("\nSingle:");
		System.out.println(asArray(elements));

		elements.add(66);
		elements.add(67);
		System.out.println("\nSimple:");
		System.out.println(asArray(elements));

		System.out.println("\n\nAsNestedObject");
		TreeMap<String, TreeSet<Integer>> tree = new TreeMap<String, TreeSet<Integer>>();
		tree.put("/usr/some/ljljd/lsjljfsad", elements);
		String s = asNestedObject(tree);
		System.out.println(s);

		System.out.println("asObject");
		TreeMap<String, Integer> tree2 = new TreeMap<String, Integer>();
		tree2.put("/usr/some/ljljd/lsjljfsad", 45);
		tree2.put("/usr/some/ljfsad", 45);
		s = asObject(tree2);
		System.out.println(s);

		TreeMap<String, TreeMap<String, TreeSet<Integer>>> map = new TreeMap<>();
		map.put("capybara", tree);
		map.put("platypus", tree);

		s = asDoubleTreeMapNestedObject(map);
		System.out.println(s);

		asDoubleTreeMapNestedObject(map, Paths.get("/tmp/tobedeleted.txt"));
	}
}
