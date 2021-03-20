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
 * Search class perform a stem for the query file and a partial or exact search
 * for the index passed in
 */
public class QueryParser implements QueryParserInterface {
	// Key: one line of query file
	// Value: sorted array list of search results
	/**
	 * query line and its search result of path count and score
	 */
	private final TreeMap<String, ArrayList<SearchResult>> searchResult;
	/**
	 * inverted index data structure
	 */
	private final InvertedIndex index;

	/**
	 * Constructor initialize the data structure
	 * 
	 * @param index inverted index data structure
	 */
	public QueryParser(InvertedIndex index) {
		this.searchResult = new TreeMap<String, ArrayList<SearchResult>>();
		this.index = index;
	}

	/**
	 * Stem the query file and perform a partial or exact search to the inverted
	 * index
	 * 
	 * @param queryFile
	 * @param exact
	 * @throws IOException
	 */

	@Override
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader br = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				TreeSet<String> oneLineQuery = new TreeSet<String>();
				for (String queryWord : TextParser.parse(line)) {
					String word = stemmer.stem(queryWord).toString();
					if (!word.isEmpty()) {
						oneLineQuery.add(word);
					}
				}
				String queryLine = String.join(" ", oneLineQuery);
				if (!searchResult.containsKey(queryLine)) {
					if (!oneLineQuery.isEmpty()) {
						searchResult.put(queryLine, index.search(oneLineQuery, exact));
					}
				}
			}
		}
	}

	/**
	 * Print out the search result to JSON format
	 * 
	 * @param resultPath the JSON file path
	 * @throws IOException exception during print process
	 */
	@Override
	public void toSearchResult(Path resultPath) throws IOException {
		PrettyJSONWriter.asSearchResult(searchResult, resultPath);
	}
}