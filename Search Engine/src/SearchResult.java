/**
 * OneResult class data structure to store one search result
 *
 * 
 */
public class SearchResult implements Comparable<SearchResult> {
	/**
	 * the path of the file which found the query word
	 */
	private final String path;
	/**
	 * how many word in that file
	 */
	private final int totalWords;
	/**
	 * how many times the query word being found in the file
	 */
	private int count;
	/**
	 * weight of the query word
	 */
	private double score;

	/**
	 * Constructor initialize one search result
	 * 
	 * @param path      the path of the file which found the query word
	 * @param totalWord how many word in that file
	 * @param count     how many times the query word being found in the file
	 */
	public SearchResult(String path, int totalWord, int count) {
		this.path = path;
		this.totalWords = totalWord;
		this.count = count;
		this.score = (double) count / totalWord;
	}

	/**
	 * Update the count and recalculate the score for the search result
	 * 
	 * 
	 * @param otherCount the number of the count need to be increase since there are
	 *                   other words in the query
	 */
	public void updateCount(int otherCount) {
		this.count += otherCount;
		this.score = (double) count / totalWords;

	}

	/**
	 * Return the path for this search result
	 * 
	 * @return String representation of the search result
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Return the score of this search result
	 * 
	 * @return the score of the search result(double)
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * Return the count of the search result
	 * 
	 * @return the count of the search result(int)
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Override hashCode method return the hashCode for path
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	/**
	 * ToString method return a String representation of the search result
	 */
	@Override
	public String toString() {
		return this.getPath() + " " + this.getCount() + " " + this.totalWords;
	}

	/**
	 * CompareTo method compare the search result first by its score in descending
	 * order, if same, compare by count in descending order, if same, compare by
	 * path in alphabetic order
	 */
	@Override
	public int compareTo(SearchResult other) {
		if (Double.compare(this.getScore(), other.getScore()) == 0) {
			if (Integer.compare(this.getCount(), other.getCount()) == 0) {
				return String.CASE_INSENSITIVE_ORDER.compare(this.getPath(), other.getPath());
			}
			return Integer.compare(other.getCount(), this.getCount());
		}
		return Double.compare(other.getScore(), this.getScore());
	}

}
