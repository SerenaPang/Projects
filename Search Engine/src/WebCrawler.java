import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Craw the seed url base on given limit number
 * 
 * Add support to build the inverted index from a seed URL instead of a
 * directory using a web crawler. The web crawler is multithreaded using a work
 * queue such that each URL that must be crawled is handled by a single worker
 * thread.
 */
public class WebCrawler {

	/**
	 * all url links
	 */
	private final HashSet<URL> allLink;
	/**
	 * work queue
	 */
	private final WorkQueue worker;
	/**
	 * inverted index data structure
	 */
	private final ThreadSafeInvertedIndex sharedInvertedIndex;

	/**
	 * @param worker
	 * @param sharedInvertedIndex
	 */
	public WebCrawler(WorkQueue worker, ThreadSafeInvertedIndex sharedInvertedIndex) {
		this.worker = worker;
		this.allLink = new HashSet<URL>();
		this.sharedInvertedIndex = sharedInvertedIndex;
	}

	/**
	 * Craw the given seed
	 * 
	 * @param seed  url seed
	 * @param limit the maximum number of url to craw
	 */
	public void craw(URL seed, int limit) {
		allLink.add(seed);
		worker.execute(new WebCrawlerTask(seed, limit));
		worker.finish();
	}

	/**
	 * Craw the url and the link in this url if the limit has not exceeded
	 * 
	 */
	private class WebCrawlerTask implements Runnable {

		/**
		 * eachURL
		 */
		private final URL urlToProcess;
		/**
		 * the maximum number of url to craw
		 */
		private final int limit;

		/**
		 * @param url
		 * @param limit
		 */
		public WebCrawlerTask(URL url, int limit) {
			this.urlToProcess = url;
			this.limit = limit;
		}

		@Override
		public void run() {
			try {
				System.out.println("Processing: " + urlToProcess);
				int redirects = 3;
				var html = HtmlFetcher.fetchHTML(urlToProcess, redirects);
				if (html == null) {
					return;
				}

				if (allLink.size() < limit) {
					ArrayList<URL> links = HtmlCleaner.listLinks(urlToProcess, html);
					for (URL link : links) {
						if (allLink.size() >= limit) {
							break;
						} else {
							if (allLink.contains(link) == false) {
								allLink.add(link);
								worker.execute(new WebCrawlerTask(link, limit));

							}
						}
					}
				}

				InvertedIndex temp = new InvertedIndex();
				var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
				int start = 1;
				for (String s : TextParser.parse(HtmlCleaner.stripHtml(html))) {
					temp.add(stemmer.stem(s).toString(), urlToProcess.toString(), start++);
				}
				sharedInvertedIndex.addAll(temp);
			} catch (IOException e) {

				e.printStackTrace();
				System.err.println("Unable to read the page: " + urlToProcess.toString());
			}

		}

	}
}
