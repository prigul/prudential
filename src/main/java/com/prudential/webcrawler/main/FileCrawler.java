package com.prudential.webcrawler.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileCrawler {

	// Default assignment if not defined in property
	public static String DOMAIN_PROTOCOL = "http";
	public static String DOMAIN_TO_CRAWL = "prudential.co.uk";

	public static File DIR = new File(".");
	public static String TRACKER_FILE = "websites.txt";
	public static String ERROR_FILE = "error1.txt";

	public static void main(String[] args) throws IOException {

		InputStream is = null;
		Properties prop = null;
		try {
			prop = new Properties();
			is = new FileInputStream(new File(ClassLoader.getSystemResource(
					"config.properties").getFile()));
			prop.load(is);
			DOMAIN_PROTOCOL = prop.getProperty("website.protocol");
			DOMAIN_TO_CRAWL = prop.getProperty("website.domain");
			TRACKER_FILE = prop.getProperty("website.tracker.filepath");
			ERROR_FILE = prop.getProperty("website.error.filepath");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String filePath = "";
		if (TRACKER_FILE.equals("websites.txt"))
			filePath = DIR.getCanonicalPath() + File.separator + TRACKER_FILE;
		else
			filePath = TRACKER_FILE;

		System.out.println("DIR: " + filePath);

		createFile(filePath, "");
		crawlSite(DOMAIN_PROTOCOL + "://" + DOMAIN_TO_CRAWL);

	}

	public static void createFile(String filePath, String content)
			throws IOException {

		try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath,
				true))) {
			if (!content.isEmpty()) {
				out.write(content);
				out.newLine();
			}
		}

	}

	public static void crawlSite(String url) throws IOException {

		// crawl the URL
		if (url.contains(DOMAIN_TO_CRAWL)) {
			if (url.endsWith("/"))
				url = url.substring(0, url.length() - 1);
		} else {
			// URL outside of domain
			return;
		}

		String trackerFilePath = "";
		String errorFilePath = "";
		if (TRACKER_FILE.equals("websites.txt"))
			trackerFilePath = DIR.getCanonicalPath() + File.separator
					+ TRACKER_FILE;
		else
			trackerFilePath = TRACKER_FILE;

		if (TRACKER_FILE.equals("error.txt"))
			errorFilePath = DIR.getCanonicalPath() + File.separator
					+ ERROR_FILE;
		else
			errorFilePath = ERROR_FILE;

		// check if URL is already been crawled and tracked in the file
		boolean isExist = checkExist(url, trackerFilePath);

		if (!isExist) {
			createFile(trackerFilePath, url);

			// Try connect to the tracked URL and parse the HTML
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (Exception exp) {

				createFile(errorFilePath,
						url + "\t" + exp.getLocalizedMessage());
				return;

			}

			// Find links from the web page
			Elements elements = doc.select("a[href]");
			for (Element link : elements) {

				crawlSite(link.attr("abs:href"));
			}
		} else {
			// do nothing
			return;
		}

	}

	// Method to check if URL already been crawled
	public static boolean checkExist(String url, String fileName)
			throws IOException {

		// Read the file to check if URL exists
		FileInputStream fis = new FileInputStream(new File(fileName));
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));

		String aLine = null;
		while ((aLine = in.readLine()) != null) {
			// If found return true else false
			if (aLine.trim().contains(url)) {
				in.close();
				fis.close();
				return true;
			}
		}

		in.close();
		fis.close();

		return false;
	}

}