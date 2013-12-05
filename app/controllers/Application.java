package controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssImageBean;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
    	
    	RssParser rss = new RssParser();
    	ArrayList<GreveItem> grevistes = new ArrayList<GreveItem>();

		try{
		        RssFeed feed = rss.load("http://news.google.com/news?q=greve&hl=fr&output=rss&num=20&ned=fr");
			
				//RssFeed feed = rss.load("/Users/quentin/Documents/workspace/Workspace-Perso/FrenchWhine-test/src/test.rss");
		        
		        // Gets the channel information of the feed and 
		        // display its title
		        RssChannelBean channel = feed.getChannel();
		        //System.out.println("Feed Title: " + channel.getTitle());
		        
		        // Gets the image of the feed and display the image URL
		        RssImageBean image = feed.getImage();
		        //System.out.println("Feed Image: " + image.getUrl());
		        
		        // Gets and iterate the items of the feed 
		        List<RssItemBean> items = feed.getItems();
		        
		        for (int i=0; i<items.size(); i++){
		             RssItemBean item = items.get(i); 
		             //System.out.println("Title: " + item.getTitle());
		             //System.out.println("Gréviste: "+ extractGreviste(item.getTitle()));
		             String grev = extractGreviste(item.getTitle());
		             if (!grev.isEmpty() && !grevistes.contains(new GreveItem(grev, null))){
		            	 grevistes.add(new GreveItem(grev, item.getLink()));
		             }
		             //System.out.println("Link : " + item.getLink());
		             //System.out.println("Desc.: " + item.getDescription()); 
		             //System.out.println("************");
		        }
		        
		        System.out.println(grevistes);
		        
		}catch(Exception e){
				System.out.println(e);
		}
    	
        return ok(index.render(grevistes));
    }
    
public static String extractGreviste(String linkTitle){
		
		ArrayList<Pattern>  grevisteFindingPatterns= new ArrayList<>();
		
		grevisteFindingPatterns.add(Pattern.compile("(?<greviste>(%|'|\\w|\\s)+)(sont)? en grève",Pattern.UNICODE_CHARACTER_CLASS));
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
