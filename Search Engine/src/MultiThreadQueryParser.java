import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * MultiThreadQueryParser class perform a stem to the query file and add in a
 * partial or exact search task to the work queue
 */

public class MultiThreadQueryParser implements QueryParserInterface {

	/**
	 * queue of worker threads
	 */
	private final WorkQueue worker;
	// key: one line of query file
	// value: sorted array list
	/**
	 * one line query and its search result
	 */
	private final TreeMap<String, ArrayList<SearchResult>> searchResult;
	/**
	 * inverted index data structure
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Constructor initialize the data structure
	 * 
	 * @param wq    work queue
	 * @param index data structure store word position, path
	 */
	public MultiThreadQueryParser(WorkQueue wq, ThreadSafeInvertedIndex index) {
		this.searchResult = new TreeMap<String, ArrayList<SearchResult>>();
		this.index = index;
		this.worker = wq;
	}

	@Override
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				worker.execute(new SearchTask(line, exact));
			}
		}
		worker.finish();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toSearchResult(Path resultPath) throws IOException {
		synchronized (searchResult) {
			PrettyJSONWriter.asSearchResult(searchResult, resultPath);
		}
	}

	/**
	 * Perform a partial or exact search to the inverted index
	 *
	 */
	private class SearchTask implements Runnable {

		/**
		 * one line query
		 */
		private final String line;
		/**
		 * exact/partial search flag
		 */
		private final boolean exact;

		/**
		 * @param line  queries
		 * @param exact exact or partial search flag
		 */
		public SearchTask(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			TreeSet<String> oneLineQuery = new TreeSet<String>();
			var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			for (String s : TextParser.parse(line)) {
				String word = stemmer.stem(s).toString();
				if (!word.isEmpty()) {
					oneLineQuery.add(word);
				}
			}

			String queryLine = String.join(" ", oneLineQuery);

			if (!oneLineQuery.isEmpty()) {
				synchronized (searchResult) {
					if (searchResult.containsKey(queryLine)) {
						return;
					}
				}

				ArrayList<SearchResult> result;
				if (exact) {
					result = index.exactSearch(oneLineQuery);
				} else {
					result = index.partialSearch(oneLineQuery);
				}
				synchronized (searchResult) {
					searchResult.put(queryLine, result);
				}
			}
		}
	}

}
