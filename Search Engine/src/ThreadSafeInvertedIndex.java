import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

/**
 * Thread safe version of inverted index
 *
 * 
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/**
	 * The lock object to keep the data being corrupted in thread safe way
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * Invoke the parent constructor and initialize the read write lock
	 */
	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new SimpleReadWriteLock();
	}

	/**
	 * Return a set view of all word invoke the parent method
	 * 
	 * @see InvertedIndex#getWordSet()
	 */
	@Override
	public Set<String> getWordSet() {
		lock.readLock().lock();
		try {
			return super.getWordSet();
		} finally {
			lock.readLock().lock();
		}
	}

	/**
	 * Return set view of all path key invoke the parent method
	 * 
	 * @see InvertedIndex#getPathSet(String)
	 */
	@Override
	public Set<String> getPathSet(String word) {
		lock.readLock().lock();
		try {
			return super.getPathSet(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Return the set of given key at given position invoke the parent method
	 * 
	 * @see InvertedIndex#getPositionSet(String, String)
	 */
	@Override
	public SortedSet<Integer> getPositionSet(String word, String path) {
		lock.readLock().lock();
		try {
			return super.getPositionSet(word, path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Return whether the map contains a word invoke the parent method
	 * 
	 * @see InvertedIndex#containsWord(String)
	 */
	@Override
	public boolean containsWord(String word) {
		lock.readLock().lock();
		try {
			return super.containsWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Check in the given word, is there a given path exist invoke the parent method
	 * 
	 * @see InvertedIndex#containsPath(String, String)
	 */
	@Override
	public boolean containsPath(String word, String path) {
		lock.readLock().lock();
		try {
			return super.containsPath(word, path);
		} finally {
			lock.readLock().unlock();
		}
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
	@Override
	public boolean containsPosition(String word, String path, int position) {
		lock.readLock().lock();
		try {
			return super.containsPosition(word, path, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Output the location map
	 * 
	 * @param path output file location
	 * @throws IOException
	 * 
	 */
	@Override
	public void toLocationsJSON(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.toLocationsJSON(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Return the total number of words for given path
	 * 
	 * @param path key
	 * @return total number of words
	 */
	@Override
	public int getTotalWords(String path) {
		lock.readLock().lock();
		try {
			return super.getTotalWords(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Outputs the inverted index as pretty JSON to file
	 * 
	 * @param path output file location
	 * @throws IOException
	 * 
	 */
	@Override
	public void toIndexJSON(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.toIndexJSON(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Return true if the data structure is empty
	 * 
	 * @return true if the data structure is empty, false otherwise
	 */
	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		try {
			return super.isEmpty();
		} finally {
			lock.readLock().unlock();
		}
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
	@Override
	public boolean add(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			return super.add(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Add a temporary inverted index to this index
	 * 
	 * @param temp inverted index
	 */
	@Override
	public void addAll(InvertedIndex temp) {
		lock.writeLock().lock();

		try {
			super.addAll(temp);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Search method
	 * 
	 * @param queries words to search
	 * @param exact   exact or partial search
	 * @return the search result
	 */
	@Override
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {

		lock.readLock().lock();
		try {
			return exact ? this.exactSearch(queries) : this.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Perform a exact search to the inverted index data structure
	 * 
	 * @param oneLineQuery one line of query file
	 * @return a sorted array list
	 */
	@Override
	public ArrayList<SearchResult> exactSearch(Collection<String> oneLineQuery) {
		lock.readLock().lock();
		try {
			return super.exactSearch(oneLineQuery);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Perform a partial search to the inverted index data structure
	 * 
	 * @param oneLineQuery a line of the query file
	 * @return a sorted array list
	 */
	@Override
	public ArrayList<SearchResult> partialSearch(Collection<String> oneLineQuery) {
		lock.readLock().lock();
		try {
			return super.partialSearch(oneLineQuery);
		} finally {
			lock.readLock().unlock();
		}
	}

}
