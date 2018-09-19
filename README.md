# prudential

Change the path of the two variables in the code for the URL to crawl
	DOMAIN_PROTOCOL = "http";
	DOMAIN_TO_CRAWL = "prudential.co.uk";

websites.txt -> Look at the sites crawled in the file at the project workspace
error.txt -> Look at the sites crawled resulted in error [Read timeout / Parse Error]

while(the list of unvisited URLs is not empty) {	`
      add URL to the list
      fetch the  content of the URL
      parse the content of URL from link
      for each URL {
                if it's not already in the visited list
                 	add it to the visited list
          }
     }
}