package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssImageBean;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import controllers.GreveItem;

public class Grevistes implements Runnable{
	
	public static ArrayList<GreveItem> grevistes = new ArrayList<GreveItem>();
	public static ArrayList<GreveItem> strikers = new ArrayList<GreveItem>();

	@Override
	public void run() {
		RssParser rss = new RssParser();

		try{
		        RssFeed feed = rss.load("http://news.google.com/news?q=greve&hl=fr&output=rss&num=20&ned=fr");
		        RssChannelBean channel = feed.getChannel();
		        
		        // Gets and iterate the items of the feed 
		        List<RssItemBean> items = feed.getItems();
		        for (int i=0; i<items.size(); i++){
		             RssItemBean item = items.get(i); 
		             
		             String grev = extractGreviste(item.getTitle());
		             if (!grev.isEmpty() && !grevistes.contains(new GreveItem(grev, null))){
		            	 grevistes.add(new GreveItem(grev, item.getLink()));
		             }
		        }
		        
		        System.out.println(grevistes);
		        
		}catch(Exception e){
				System.out.println(e);
		}
		
	}
	
public static String extractGreviste(String linkTitle){
		
		ArrayList<Pattern>  grevisteFindingPatterns= new ArrayList<>();
		
		grevisteFindingPatterns.add(Pattern.compile("(?<greviste>(-|%|'|\\w|\\s)+)(sont)? en grève",Pattern.UNICODE_CHARACTER_CLASS));
		grevisteFindingPatterns.add(Pattern.compile("(g|G)rève( générale| nationale)? (de|à) (?<greviste>la \\w+)",Pattern.UNICODE_CHARACTER_CLASS));
		grevisteFindingPatterns.add(Pattern.compile("(g|G)rève( générale| nationale)? (?<greviste>des \\w+)",Pattern.UNICODE_CHARACTER_CLASS));
		
		String greviste = "";
		
		for(Pattern pattern: grevisteFindingPatterns){
		    Matcher matcher = pattern.matcher(linkTitle);
		    if(matcher.find()){
		    	greviste = matcher.group("greviste");
		    	//greviste = greviste.toLowerCase();
		 	    greviste = greviste.trim();
		 	    break;
		    }
		}
		
		greviste = greviste.replaceAll("^(d|D)es ", "les ");
		greviste = greviste.replaceAll("^Les ", "les ");
	    
	    
		return greviste;
	}
}
