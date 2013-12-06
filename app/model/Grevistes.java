package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import controllers.GreveItem;

public class Grevistes implements Runnable{
	
	static HashSet<String> falsePositives;
	static {
		falsePositives =  new HashSet<String>();	
		falsePositives.add("toujours en grève");
		falsePositives.add("grève de la faim");
	}
	
	public static ArrayList<GreveItem> grevistes = new ArrayList<GreveItem>();;
	public static ArrayList<GreveItem> strikers = new ArrayList<GreveItem>();

	@Override
	public void run() {
		RssParser rss = new RssParser();
		
		ArrayList<GreveItem> newGrevistes = new ArrayList<GreveItem>();

		try{
		        RssFeed feed = rss.load("http://news.google.com/news?q=greve&hl=fr&output=rss&num=20&ned=fr");
		       
		        // Gets and iterate the items of the feed 
		        List<RssItemBean> items = feed.getItems();
		        for (int i=0; i<items.size(); i++){
		             RssItemBean item = items.get(i); 
		             
		             Calendar pubDate  = Calendar.getInstance();
		             pubDate.setTime(item.getPubDate());
		            
		             Calendar today = Calendar.getInstance(); 
		             if(today.get(Calendar.DAY_OF_YEAR) == pubDate.get(Calendar.DAY_OF_YEAR) ){
		            	 
			             String grev = extractGreviste(item.getTitle());
			             if (!grev.isEmpty() && !newGrevistes.contains(new GreveItem(grev, null))){
			            	 
			            	 String url = item.getLink();
			            	 
			            	 Pattern p = Pattern.compile("&url=(.+)&?");
				             Matcher matcher = p.matcher(url);
				             
				             if(matcher.find()){
				            	 url = matcher.group(1);
				             } 
			            	 newGrevistes.add(new GreveItem(grev, url));
			             }
			             
		        }
		        }
		        
		        grevistes = newGrevistes;
		        strikers = translateToEnglish(newGrevistes);
		        System.out.println(grevistes);
		        System.out.println(strikers);
		        
		}catch(Exception e){
				System.out.println(e);
		}
		
	}
	
	public String extractGreviste(String linkTitle){
		
		ArrayList<Pattern>  grevisteFindingPatterns= new ArrayList<>();
		
		grevisteFindingPatterns.add(Pattern.compile("(?<greviste>(-|%|'|\\w|\\s)+)(sont)? en grève",Pattern.UNICODE_CHARACTER_CLASS));
		grevisteFindingPatterns.add(Pattern.compile("(g|G)rève( générale| nationale)? (de|à) (?<greviste>la \\w+)",Pattern.UNICODE_CHARACTER_CLASS));
		grevisteFindingPatterns.add(Pattern.compile("(g|G)rève( générale| nationale)? (?<greviste>des \\w+)",Pattern.UNICODE_CHARACTER_CLASS));
		
		String greviste = "";
		
		for(Pattern pattern: grevisteFindingPatterns){
		    Matcher matcher = pattern.matcher(linkTitle);
		    if(matcher.find() && !falsePositives.contains(matcher.group().toLowerCase())){
		    	greviste = matcher.group("greviste");
		 	    greviste = greviste.trim();
		 	    break;
		    }
		}
		
		if(!greviste.isEmpty()){
			greviste = greviste.replaceFirst("^(d|D)es ", "les ");
			greviste = greviste.replaceFirst(greviste.charAt(0)+"", (greviste.charAt(0)+"").toLowerCase());
		}
	    
	    
		return greviste;
	}

	public ArrayList<GreveItem> translateToEnglish(ArrayList<GreveItem> grevistesToTranslate){
		String[] words = null;
		Translate.setClientId("FrenchWhine");
	    Translate.setClientSecret("5i/ZSL+yrX+lRKVikdgjFB+t0WRU3ztk5UzIbCtk1Nc=");
	    
	    ArrayList<GreveItem> list = new ArrayList<>() ;
	    
	    try {
	    	for(GreveItem greviste: grevistesToTranslate){
	    		list.add(new GreveItem(Translate.execute(greviste.greviste, Language.FRENCH, Language.ENGLISH), "http://translate.google.com/translate?sl=fr&tl=en&u="+greviste.newsUrl));
	    	}
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return list;
	}
}
