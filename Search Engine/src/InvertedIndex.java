import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * InvertedMap class 2 main data structure to store the map
 *
 */
public class InvertedIndex {
	// Data structure
	/**
	 * index data structure to store the word, it's file location and word indexes
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	/**
	 * data structure to store file path and it's total word count
	 */
	private final TreeMap<String, Integer> count;

	/**
	 * Initializes the inverted index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.count = new TreeMap<String, Integer>();
	}

	/**
	 * Return the total number of words for given path
	 * 
	 * @param path key
	 * @return total number of words
	 */
	public int getTotalWords(String path) {
		return count.get(path) != null ? count.get(path) : 0;
	}

	/**
	 * Output the location map
	 * 
	 * @param path output file location
	 * @throws IOException
	 * 
	 */
	public void toLocationsJSON(Path path) throws IOException {
		PrettyJSONWriter.asObject(count, path);
	}

	/**
	 * Outputs the inverted index as pretty JSON to file
	 * 
	 * @param path output file location
	 * @throws IOException
	 * 
	 */
	public void toIndexJSON(Path path) throws IOException {
		PrettyJSONWriter.asDoubleTreeMapNestedObject(index, path);
	}

	/**
	 * Adding the word location and word positions to the nested treeMap index
	 * 
	 * @param word     word to add
	 * @param location location of the file
	 * @param position position index of the word
	 * @return true if new word and its location and position is added to index,
	 *         false otherwise
	 */
	public boolean add(String word, String location, int position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(location, new TreeSet<>());
		boolean result = index.get(word).get(location).add(position);
		if (result) {
			Integer number = this.count.getOrDefault(location, 0);
			this.count.put(location, number + 1);
		}
		return true;
	}

	/**
	 * Add a temporary inverted index to this index
	 * 
	 * @param temp inverted index
	 */
	public void addAll(InvertedIndex temp) {
		for (String word : temp.index.keySet()) {
			if (this.index.containsKey(word)) {
				for (String path : temp.index.get(word).keySet()) {
					if (this.index.get(word).containsKey(path)) {
						this.index.get(word).get(path).addAll(temp.index.get(word).get(path));
					} else {
						this.index.get(word).put(path, temp.index.get(word).get(path));
					}
				}
			} else {
				this.index.put(word, temp.index.get(word));
			}
		}

		this.count.putAll(temp.count);
	}

	/**
	 * Return a set view of all word
	 * 
	 * @return Set<String>
	 * 
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<String> getWordSet() {
		Set<String> result = index.keySet();
		return (result != null ? Collections.unmodifiableSet(result) : null);
	}

	/**
	 * Return set view of all path key
	 * 
	 * @param word key
	 * @return Set<String>
	 * 
	 * @see java.util.TreeMap#keySet()
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<String> getPathSet(String word) {
		TreeMap<String, TreeSet<Integer>> result = index.get(word);
		return (result != null ? Collections.unmodifiableSet(result.keySet()) : null);
	}

	/**
	 * Return the set of given key at given position
	 * 
	 * @param word key
	 * @param path key
	 * @return value of the given word at given position
	 * 
	 * @see Collections#unmodifiableSortedSet(SortedSet)
	 */
	public SortedSet<Integer> getPositionSet(String word, String path) {
		TreeSet<Integer> result = getterPosition(word, path);
		return (result != null ? Collections.unmodifiableSortedSet(result) : null);
	}

	/**
	 * Helper method return a TreeSet of given word and path
	 * 
	 * @param word key for the TreeMap
	 * @param path key for the TreeMap
	 * @return TreeSet<Integer>
	 */
	private TreeSet<Integer> getterPosition(String word, String path) {
		TreeMap<String, TreeSet<Integer>> result = index.get(word);
		return (result != null ? result.get(path) : null);
	}

	/**
	 * Return true if the data structure is empty
	 * 
	 * @return true if the data structure is empty, false otherwise
	 */
	public boolean isEmpty() {
		return index.isEmpty();
	}

	/**
	 * Return whether the map contains a word
	 * 
	 * @param word key
	 * @return true if the map contains a key, false otherwise
	 */
	public boolean containsWord(String word) {
		return (!isEmpty() ? index.containsKey(word) : false);
	}

	/**
	 * Check in the given word, is there a given path exist
	 * 
	 * @param word word in the file
	 * @param path the path of the file
	 * @return true if the path in the given word already exist, false otherwise
	 */
	public boolean containsPath(String word, String path) {
		TreeMap<String, TreeSet<Integer>> result = index.get(word);
		return (result != null ? result.containsKey(path) : false);
	}

	/**
	 * Check in the given word, given path, is there a given position
	 * 
	 * @param word     word in the file
	 * @param path     the path of the file
	 * @param position the position of the word in the file
	 * @return true if the word in the given file and given position already exist,
	 *         false otherwise
	 */
	public boolean containsPosition(String word, String path, int position) {
		TreeSet<Integer> result = getterPosition(word, path);
		return (result != null ? result.contains(position) : false);
	}

	/**
	 * Search method
	 * 
	 * @param queries words to search
	 * @param exact   exact or partial search
	 * @return the search result
	 */
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {

		return exact ? exactSearch(queries) : partialSearch(queries);
	}

	/**
	 * Perform a partial search to the inverted index data structure
	 * 
	 * @param oneLineQuery a line of the query file
	 * @return a sorted array list
	 */
	public ArrayList<SearchResult> partialSearch(Collection<String> oneLineQuery) {
		HashMap<String, SearchResult> result = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> list = new ArrayList<>();
		for (String oneQuery : oneLineQuery) {
			for (String indexKey : index.tailMap(oneQuery).keySet()) {
				if (indexKey.startsWith(oneQuery)) {
					searchHelper(result, indexKey, list);
				} else {
					break;
				}
			}
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * Perform a exact search to the inverted index data structure
	 * 
	 * @param oneLineQuery one line of query file
	 * @return a sorted array list
	 */
	public ArrayList<SearchResult> exactSearch(Collection<String> oneLineQuery) {
		HashMap<String, SearchResult> result = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> list = new ArrayList<>();
		for (String oneQuery : oneLineQuery) {
			searchHelper(result, oneQuery, list);
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * Search helper method
	 * 
	 * @param result           hashMap to store the OneResult
	 * @param oneQuery         index word, one query word
	 * @param listOfOneResults returned array list
	 */
	private void searchHelper(HashMap<String, SearchResult> result, String oneQuery,
			ArrayList<SearchResult> listOfOneResults) {
		var pathTreeMap = index.get(oneQuery);
		if (pathTreeMap != null) {
			for (String path : pathTreeMap.keySet()) {
				if (result.containsKey(path)) {
					result.get(path).updateCount(this.index.get(oneQuery).get(path).size());
				} else {
					SearchResult oneResult = new SearchResult(path, this.count.get(path),
							this.index.get(oneQuery).get(path).size());
					result.put(path, oneResult);
					listOfOneResults.add(oneResult);
				}
			}
		}
	}

}