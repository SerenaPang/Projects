import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans simple, validating HTML 4/5 into links and plain text.
 */
public class HtmlCleaner {

	/*
	 * Instead of creating several get methods, these are all public for direct
	 * access since they are constant and/or unmodifiable.
	 */

	/**
	 * The base URL of the HTML provided.
	 */
	public final URL base;

	/**
	 * The original HTML before cleaning.
	 */
	public final String html;

	/**
	 * The remaining text after removing HTML comments, elements, tags, and
	 * entities.
	 */
	public final String text;

	/**
	 * An unmodifiable list of URLs parsed from the HTML.
	 */
	public final List<URL> urls;

	/**
	 * Given a base URL and its HTML content, removes the comments and non-HTML
	 * elements, parses the remaining links from the anchor tags, and then removes
	 * all remaining HTML tags and entities.
	 *
	 * @param base the base URL
	 * @param html the HTML content of that base URL
	 * @throws MalformedURLException
	 */
	public HtmlCleaner(URL base, String html) throws MalformedURLException {
		this.base = base;
		this.html = html;

		if (html != null) {
			// remove comments
			html = stripComments(html);

			// remove elements that should not be parsed
			html = stripElement(html, "head");
			html = stripElement(html, "style");
			html = stripElement(html, "script");
			html = stripElement(html, "noscript");
			html = stripElement(html, "svg");

			// should happen after elements are removed but before tags are removed
			this.urls = Collections.unmodifiableList(listLinks(base, html));

			html = stripTags(html);
			html = stripEntities(html);

			this.text = html;
		} else {
			this.urls = null;
			this.text = null;
		}
	}

	/**
	 * Given a base URL, fetches its HTML content, removes the comments and non-HTML
	 * elements, parses the remaining links from the anchor tags, and then removes
	 * all remaining HTML tags and entities.
	 *
	 * @param base the base URL
	 * @throws IOException
	 * @see #HtmlCleaner(URL, String)
	 * @see HtmlFetcher#fetchHTML(URL)
	 */
	public HtmlCleaner(URL base) throws IOException {
		this(base, HtmlFetcher.fetchHTML(base));
	}

	/**
	 * Removes all HTML tags and certain block elements from the provided text. The
	 * block elements removed include: head, style, script, noscript, and svg.
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripElement(html, "noscript");
		html = stripElement(html, "svg");

		html = stripTags(html);
		html = stripEntities(html);

		return html;
	}

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url the url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base the base url used to convert relative links to absolute3
	 * @param html the raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 * @throws MalformedURLException
	 */
	public static ArrayList<URL> listLinks(URL base, String html) throws MalformedURLException {
//		System.out.println("PATH -> " + base.getPath());
//		System.out.println("HOST -> " + base.getHost());
//		System.out.println(base);
		try {
			System.out.println(new URL(base, "testing.html"));
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		// System.out.println(html);

		ArrayList<URL> links = new ArrayList<URL>();
		int GROUP = 1;
		String REGEX = "(?i)<a(?:[^<>]*?)href=\"([^\"]+?)\"";
		URL link = null;
		if (html == null) {
			// System.out.println(base.toString());
		}
		Matcher match = Pattern.compile(REGEX).matcher(html.replaceAll("\\s", "").trim());

		while (match.find()) {
			try {
				String site = match.group(GROUP);
				if (!site.startsWith("http")) {
					System.out.println("site:   ->" + site);
					link = new URL(base, site);

				} else {
					link = new URL(site);
				}
				if (link.toString().startsWith("http")) {
					links.add(clean(link));
				}

			} catch (MalformedURLException e) {
				System.err.println("Illegal url:  please check your argument");

			}

		}
		return links;
	}

	// THE FOLLOWING REPLACE WITH THE EMPTY STRING

	/**
	 * Replaces all HTML entities with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) {
		String regex = "&[\\d|<p>{#}|a-zA-Z]+\\.?;";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);

		return sb.toString();
	}

	/**
	 * Replaces all HTML tags with an empty string. For example, "A<b>B</b>C" will
	 * become "ABC".
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) {
		String regex = "(?s)<(.*?)>";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// THE FOLLOWING REPLACE WITH A SINGLE SPACE

	/**
	 * Replaces all HTML comments with a single space. For example, "A<!-- B -->C"
	 * will become "A C".
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) {
		String regex = "(?i)<!-([\\s\\S]*?)->";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, " ");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Replaces everything between the element tags and the element tags themselves
	 * with a single space. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with a single space.
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) {
		String regex = "(?s)(?i)<" + name + "(.*?)" + name + "(\\s*?)>";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, " ");
		}

		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Demonstrates this class.
	 * 
	 * @param args unused
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String input = "My phone is 659 09 and my other phone is 627 80 bye";
		Pattern pattern = Pattern.compile("[0-9]{3} [0-9]{2}");
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			String match = matcher.group();

			System.out.println(match);
		}

	}
}
