package model;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import controllers.GreveItem;
import edu.stanford.nlp.international.french.process.FrenchTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class Grevistes implements Runnable{
	
	static HashSet<String> falsePositives;
	static {
		falsePositives =  new HashSet<String>();	
		falsePositives.add("toujours en grève");
		falsePositives.add("grève de la faim");
	}
	
	public static ArrayList<GreveItem> grevistes = new ArrayList<GreveItem>();;
	public static ArrayList<GreveItem> strikers = new ArrayList<GreveItem>();
	static LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");


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
		
		String greviste = "";
		
		//Tokenize the title
		TokenizerFactory<CoreLabel> tokenizerFactory=FrenchTokenizer.ftbFactory();
	    Reader reader = new StringReader(linkTitle);
        Tokenizer<CoreLabel> tokenizer=tokenizerFactory.getTokenizer(reader);
        
        List<String> tokens =new ArrayList<String>();
        while(tokenizer.hasNext()){
        	tokens.add(tokenizer.next().word());
        }
        
        //Part of Speech Tagging
        List<CoreLabel> rawWords = Sentence.toCoreLabelList(tokens.toArray(new String[0]));
	    Tree parse = lp.apply(rawWords);
	    parse = lp.apply(rawWords);
	    parse.pennPrint();
	    System.out.println();
	   
	    
	    ArrayList<TregexPattern>  grevisteFindingPatterns= new ArrayList<>();
	    
	    //En grève
	     grevisteFindingPatterns.add(TregexPattern.compile("NP=greviste $+ (VN << grève)"));
	     grevisteFindingPatterns.add(TregexPattern.compile("NP=greviste $+ (PP << grève)"));
	   //Grève verb XXXX
	     grevisteFindingPatterns.add(TregexPattern.compile("NP << grève $+ (VN $+ NP=greviste)"));
	     grevisteFindingPatterns.add(TregexPattern.compile("NP << grève $+ (VN $+ (PP < NP=greviste))"));
	   //Grève dans XXXX
	     grevisteFindingPatterns.add(TregexPattern.compile("N << Grève $ (PP < NP=greviste)"));
	   
	    
	    for(TregexPattern tp: grevisteFindingPatterns){
		    TregexMatcher m = tp.matcher(parse);
		    if (m.find()) {
		      Tree t = m.getNode("greviste");
		      for (Tree leaf : t.getLeaves()){
		    	  greviste += leaf+" ";
		    	  
		      }
		    }
	    }
		
	    greviste = greviste.trim();
 	    System.out.println(greviste);
 	    
 	   if(!greviste.isEmpty()){
			greviste = greviste.replaceFirst("^(d|D)es ", "les ");
			greviste = greviste.replaceFirst(greviste.charAt(0)+"", (greviste.charAt(0)+"").toLowerCase());
		}
 	    
		return greviste;
	}

	//TODO handle [] case
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
