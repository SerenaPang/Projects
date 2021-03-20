import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Serena
 *
 */
public interface QueryParserInterface {

	/**
	 * Stem the query file and perform a partial or exact search to the inverted
	 * index
	 * 
	 * @param queryFile
	 * @param exact
	 * @throws IOException
	 */

	public default void stemQuery(Path queryFile, boolean exact) throws IOException {

	}

	/**
	 * Print out the search result to JSON format
	 * 
	 * @param resultPath the JSON file path
	 * @throws IOException exception during print process
	 * 
	 */
	public void toSearchResult(Path resultPath) throws IOException;

}
