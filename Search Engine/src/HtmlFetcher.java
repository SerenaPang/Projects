import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A specialized version of {@link HttpsFetcher} that follows redirects and
 * returns HTML content if possible.
 *
 * @see HttpsFetcher
 */
public class HtmlFetcher {

	/**
	 * Returns {@code true} if and only if there is a "Content-Type" header and the
	 * first value of that header starts with the value "text/html"
	 * (case-insensitive).
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isHTML(Map<String, List<String>> headers) {

		if (headers.containsKey("Content-Type")) {
			if (headers.get("Content-Type").toString().toLowerCase().contains("text/html")) {
				return true;
			}
		}
		return false;

	}

	/**
	 * @param text
	 * @param regex
	 * @return matches
	 */
	private static String getMatches(String text, String regex) {
		ArrayList<String> matches = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		int index = 0;
		while ((index < text.length()) && m.find(index)) {
			matches.add(text.substring(m.start(), m.end()));
			if (m.start() == m.end()) {
				index = m.end() + 1;
			} else {
				index = m.end();
			}
		}
		return String.join("", matches);
	}

	/**
	 * Parses the HTTP status code from the provided HTTP headers, assuming the
	 * status line is stored under the {@code null} key.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return the HTTP status code or -1 if unable to parse for any reasons
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		if (headers.containsKey(null)) {
			return Integer.parseInt(getMatches(headers.get(null).toString(), "\\d{3}").trim());
		}
		return -1;
	}

	/**
	 * Returns {@code true} if and only if the HTTP status code is between 300 and
	 * 399 (inclusive) and there is a "Location" header with at least one value.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		int code = getStatusCode(headers);
		return code >= 300 && code < 400 && headers.containsKey("Location") && !headers.get("Location").isEmpty();
	}

	/**
	 * Fetches the resource at the URL using HTTP/1.1 and sockets. If the status
	 * code is 200 and the content type is HTML, returns the HTML as a single
	 * string. If the status code is a valid redirect, will follow that redirect if
	 * the number of redirects is greater than 0. Otherwise, returns {@code null}.
	 *
	 * @param url       the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 * @throws IOException
	 *
	 * @see HttpsFetcher#openConnection(URL)
	 * @see HttpsFetcher#printGetRequest(PrintWriter, URL)
	 * @see HttpsFetcher#getHeaderFields(BufferedReader)
	 * @see HttpsFetcher#getContent(BufferedReader)
	 *
	 * @see String#join(CharSequence, CharSequence...)
	 *
	 * @see #isHTML(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetchHTML(URL url, int redirects) throws IOException {
		var header = HttpsFetcher.fetchURL(url);
//		System.out.println(header);
		int statusCode = getStatusCode(header);
//		System.out.println(statusCode);
		if (isHTML(header) && statusCode >= 200 && statusCode < 300) {
			return String.join("\n", header.get("Content"));
		} else if (isRedirect(header) && redirects > 0) {
			var location = header.get("Location").toString();
			return fetchHTML(location.substring(1, location.length() - 1), redirects - 1);
		}
		return null;
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetchHTML(URL, int)}.
	 *
	 * @param url       the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 * @throws IOException
	 *
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url, int redirects) throws IOException {
		try {
			return fetchHTML(new URL(url), redirects);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetchHTML(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 * @throws IOException
	 *
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url) throws IOException {
		return fetchHTML(url, 0);
	}

	/**
	 * Calls {@link #fetchHTML(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 * @throws IOException
	 */
	public static String fetchHTML(URL url) throws IOException {
		return fetchHTML(url, 0);
	}
}
