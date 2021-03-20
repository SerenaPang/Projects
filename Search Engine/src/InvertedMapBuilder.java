import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * InvertedMapBuilder class processes files and strings and places them in an
 * inverted index data structure.
 *
 */
public class InvertedMapBuilder {
	/**
	 * Read through the given directory, find the file in the given directory
	 * 
	 * @param file  directory
	 * @param index data structure
	 * @throws IOException
	 */
	public static void buildMap(Path file, InvertedIndex index) throws IOException {
		if (Files.isDirectory(file)) {
			try (DirectoryStream<Path> list = Files.newDirectoryStream(file)) {
				Iterator<Path> directoryStreamIt = list.iterator();
				while (directoryStreamIt.hasNext()) {
					buildMap(directoryStreamIt.next(), index);
				}
			}
		} else {
			String fileName = file.toString().toLowerCase();
			if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
				stemFile(file, index);
			}
		}
	}

	/**
	 * Reads a file and put each word into the data structure
	 * 
	 * @param inputFile the input file to parse
	 * @param index     data structure
	 * @throws IOException if unable to read the file
	 * 
	 */
	public static void stemFile(Path inputFile, InvertedIndex index) throws IOException {
		var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
			String line;
			int start = 1;
			String name = inputFile.toString();
			while ((line = br.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					index.add(stemmer.stem(word).toString(), name, start++);
				}
			}
		}
	}

	/**
	 * Take in a input file and return a inverted index
	 * 
	 * @param inputFile file
	 * @return inverted index
	 * @throws IOException
	 */
	public static InvertedIndex stemFile(Path inputFile) throws IOException {
		InvertedIndex temp = new InvertedIndex();
		stemFile(inputFile, temp);
		return temp;
	}

}