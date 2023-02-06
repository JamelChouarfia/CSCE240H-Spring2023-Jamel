/*
 * Written by Jamel Chouarfia
 */

public class MainExtractor {
    public static void main(String[] args) {
        SiteHandler site = new SiteHandler();
        
        if (site.DownloadSite("https://www.cdc.gov/flu/avianflu/avian-in-humans.htm", "avian-flu-cdc.txt")) {
        	System.out.println("Information from cdc.gov successfully downloaded");
        }
        
        if (site.DownloadSite("https://www.webmd.com/cold-and-flu/flu-guide/what-know-about-bird-flu", "avian-flu-webmd.txt")) {
        	System.out.println("Information from webmd.com successfully downloaded");
        	System.out.println();
        }
                
        if (site.FilterSite("avian-flu-cdc.txt", "cdc-filtered.txt")) {
        	System.out.println("Data from cdc.gov successfully filtered");
        }
                
        if (site.FilterSite("avian-flu-webmd.txt", "webmd-filtered.txt")) {
        	System.out.println("Data from webmd.com successfully filtered");
        }
    }
}