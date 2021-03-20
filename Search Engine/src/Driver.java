import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Serena Pang
 * @version Spring 2019
 */
public class Driver {

	/**
	 * flag for input path
	 */
	private static final String PATH_INPUT = "-path";

	/**
	 * flag for index output
	 */
	private static final String INDEX_OUTPUT = "-index";

	/**
	 * flag for location output
	 */
	private static final String LOCATIONS_OUTPUT = "-locations";
	/**
	 * flag for exact search
	 */
	private static final String EXACT_SEARCH = "-exact";
	/**
	 * result flag
	 */
	private static final String RESULTS_SEARCH = "-results";
	/**
	 * query flag
	 */
	private static final String QUERY_SEARCH = "-query";
	/**
	 * thread flag
	 */
	private static final String THREAD_FLAG = "-threads";

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 * 
	 * @param args flag/value pairs used to start this program
	 * 
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();
		InvertedIndex invertedIndex = null;
		ArgumentMap mapForPath = new ArgumentMap(args);
		QueryParserInterface queryParser = null;

		ThreadSafeInvertedIndex threadSafe = null;
		WorkQueue worker = null;
		WebCrawler crawler = null;
		int threads = 1;
		boolean urlFlag = mapForPath.hasFlag("-url");
		boolean threadFlag = mapForPath.hasFlag(THREAD_FLAG);

		if (urlFlag) {
			threadFlag = true;
			System.out.println(threadFlag);
		}

		if (!mapForPath.hasFlag(THREAD_FLAG)) {
			invertedIndex = new InvertedIndex();
			queryParser = new QueryParser(invertedIndex);
		} else {
			try {
				threads = Integer.parseInt(mapForPath.getString(THREAD_FLAG, "5"));
			} catch (NumberFormatException e) {
				System.out.println("Number format exception.");
			}
			if (threads < 1) {
				threads = 5;
			}
			worker = new WorkQueue(threads);
			threadSafe = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafe;

			queryParser = new MultiThreadQueryParser(worker, threadSafe);
		}

		// -path
		// -url
		if (urlFlag) {
			crawler = new WebCrawler(worker, threadSafe);
			String seedStr = mapForPath.getString("-url");
			URL seed;
			int limit;
			try {
				seed = new URL(mapForPath.getString("-url"));
				limit = Integer.parseInt(mapForPath.getString("-limit", "50"));
			} catch (MalformedURLException e) {
				System.err.println("Illegal url: " + seedStr + " please check your argument");
				return;
			} catch (NumberFormatException numEx) {
				System.err.println("Illegal limit number: " + mapForPath.getString("-limit"));
				return;
			}

			crawler.craw(seed, limit);
		}

		else if (mapForPath.hasFlag(PATH_INPUT)) {
			Path pathInput = mapForPath.getPath(PATH_INPUT);

			if (pathInput != null && Files.exists(pathInput)) {

				try {
					if (!mapForPath.hasFlag(THREAD_FLAG)) {
						InvertedMapBuilder.buildMap(pathInput, invertedIndex);
					} else {
						MultiThreadInvertedMapBuilder.buildMap(pathInput, threadSafe, worker);
					}
				} catch (IOException e) {
					System.err.println("Unbale to read the path or stem the file: " + pathInput.toString()
							+ "\n\tplease check your argument");
				}
			} else {
				if (pathInput == null) {
					System.err.println("Missing argument for -path");
				} else {
					System.err.println(
							"Invalid value for path flag: " + pathInput.toString() + "\n\tplease check your argument");
				}
			}
		} else {
			System.err.println("Missing flag for -path");
		}

		// -index
		if (mapForPath.hasFlag(INDEX_OUTPUT)) {
			Path indexPath = mapForPath.getPath(INDEX_OUTPUT, Paths.get("index.json"));
			try {
				invertedIndex.toIndexJSON(indexPath);
			} catch (IOException e) {
				System.err.println(
						"Unable to print out to file: " + indexPath.toString() + "\n\tPlease check your argument.");
			}
		}

		// -location
		if (mapForPath.hasFlag(LOCATIONS_OUTPUT)) {
			Path locationsPath = mapForPath.getPath(LOCATIONS_OUTPUT, Paths.get("locations.json"));
			try {
				invertedIndex.toLocationsJSON(locationsPath);
			} catch (IOException e) {
				System.err.println(
						"Unable to print out to file: " + locationsPath.toString() + "\n\tPlease check your argument.");
			}
		}

		// -exact
		boolean exact = mapForPath.hasFlag(EXACT_SEARCH);

		// -query
		if (mapForPath.hasFlag(QUERY_SEARCH) && mapForPath.getPath(QUERY_SEARCH) != null) {
			Path queryFile = mapForPath.getPath(QUERY_SEARCH);
			if (Files.exists(queryFile)) {
				try {

					queryParser.stemQuery(queryFile, exact);
				} catch (IOException e) {
					System.err
							.println("Unable to search on: " + queryFile.toString() + "\n\tplease check your argument");
				}
			} else {
				System.err.println("Missing query path");
			}

		} else {
			System.err.println("Invalid query flag: " + mapForPath.toString() + "\n\tplease check your argument");
		}

		// -results
		if (mapForPath.hasFlag(RESULTS_SEARCH)) {
			Path resultPath = mapForPath.getPath(RESULTS_SEARCH, Paths.get("results.json"));
			try {
				queryParser.toSearchResult(resultPath);
			} catch (IOException e) {
				System.err.println("Unable to generate the search result file: " + resultPath.toString()
						+ "\n\tplease check your argument");
			}
		}

		if (worker != null) {
			worker.shutdown();
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}
