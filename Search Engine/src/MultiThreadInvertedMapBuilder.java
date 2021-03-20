import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Multi thread version of InvertedMapBuilder. Find all text or txt file in the
 * directory and add in the work queue
 *
 */
public class MultiThreadInvertedMapBuilder extends InvertedMapBuilder {

	/**
	 * Initialize the work queue and wait all work to be finish
	 * 
	 * @param file   the file we want to store in the data structure
	 * @param index  inverted index data structure
	 * @param worker queue of worker threads
	 * @throws IOException when buffered reader cannot read the file
	 */
	public static void buildMap(Path file, ThreadSafeInvertedIndex index, WorkQueue worker) throws IOException {

		buildMapHelper(file, index, worker);
		worker.finish();
	}

	/**
	 * Read through the given directory, find the file in the given directory and
	 * add the work to the work queue
	 * 
	 * @param file   directory to be go through
	 * @param index  inverted index data structure
	 * @param worker queue of worker threads
	 * @throws IOException
	 * 
	 */
	private static void buildMapHelper(Path file, ThreadSafeInvertedIndex index, WorkQueue worker) throws IOException {

		if (Files.isDirectory(file)) {
			try (DirectoryStream<Path> list = Files.newDirectoryStream(file)) {
				Iterator<Path> directoryStreamIt = list.iterator();
				while (directoryStreamIt.hasNext()) {
					buildMapHelper(directoryStreamIt.next(), index, worker);
				}
			}
		} else {
			String fileName = file.toString().toLowerCase();
			if (fileName.endsWith(".text") || fileName.endsWith(".txt")) {
				worker.execute(new StemFileTask(file, index));
			}
		}
	}

	/**
	 * StemFile class stem the given file and add in the inverted index data
	 * structure
	 * 
	 */
	private static class StemFileTask implements Runnable {

		/**
		 * file: inputFile
		 */
		private final Path file;
		/**
		 * inverted index data structure
		 */
		private final ThreadSafeInvertedIndex index;

		/**
		 * StemFileTask to stem files
		 * 
		 * @param file  file to be stem
		 * @param index inverted index data structure
		 */
		public StemFileTask(Path file, ThreadSafeInvertedIndex index) {
			this.file = file;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				InvertedIndex temp = InvertedMapBuilder.stemFile(file);
				index.addAll(temp);
			} catch (IOException e) {
				System.err.println("Unable to stem file: " + file.toString());
			}
		}

	}
}
