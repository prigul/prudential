package com.prudential.webcrawler.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileCrawler {

	// Final variable to hold the domain to be crawled
	public final static String DOMAIN_PROTOCOL = "http";
	public final static String DOMAIN_TO_CRAWL = "prudential.co.uk";
	
	public final static File DIR = new File(".");
	public final static String FILE_TRACKER = "websites.txt";

	public static void main(String[] args) throws IOException {

		String filePath = DIR.getCanonicalPath() + File.separator + FILE_TRACKER;
		createFile(filePath,"");
		crawlSite(DOMAIN_PROTOCOL + "://" + DOMAIN_TO_CRAWL, filePath);

	}

	public static void createFile(String file, String content) throws IOException {

		try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) {
			if(!content.isEmpty())
			{
				out.write(content);
				out.newLine();
			}
		}
		
	}

	public static void crawlSite(String url, String fileLocation) throws IOException {

		// crawl the URL
		if (url.contains(DOMAIN_TO_CRAWL)) {
			if (url.endsWith("/"))
				url = url.substring(0, url.length() - 1);
		} else {
			// URL outside of domain
			return;
		}

		String locErr = DIR.getCanonicalPath() + File.separator + "error.txt";
		String locErrRemain = DIR.getCanonicalPath() + File.separator + "errorRemain.txt";
		
		// check if URL is already been crawled and tracked in the file
		boolean isExist = checkExist(url, fileLocation);

		if (!isExist) {
			createFile(fileLocation, url);

			// Try connect to the tracked URL and parse the HTML
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (UnsupportedMimeTypeException umte) {

				createFile(locErr, url+"\t"+umte.getLocalizedMessage());

			} catch (Exception exp) {

				createFile(locErrRemain, url+"\t"+exp.getLocalizedMessage());

			}

			// Find links from the web page
			Elements elements = doc.select("a[href]");
			for (Element link : elements) {

				crawlSite(link.attr("abs:href"), fileLocation);
			}
		} else {
			// do nothing
			return;
		}

	}

	// Method to check if URL already been crawled
	public static boolean checkExist(String url, String fileName) throws IOException {

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