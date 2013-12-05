package controllers;

import java.util.HashSet;


public class GreveItem{
	public String greviste;
	public String newsUrl;
	
	public static HashSet<String> profs;
	static {
		profs =  new HashSet<String>();	
		profs.add("profs");
		profs.add("instituteurs");
		profs.add("professeurs");
		profs.add("enseignants");
	}
	
	public GreveItem(String greviste, String newsUrl){
		this.greviste = greviste;
		this.newsUrl = newsUrl;
	}
	
	@Override
	public boolean equals(Object otherGreveItem) {
		
		if(otherGreveItem.getClass().equals(String.class)){
			return this.greviste.equals(otherGreveItem);
		} else if (otherGreveItem.getClass().equals(this.getClass())){
			String firstWord = this.greviste.replaceFirst("^les ", "").toLowerCase();
			String secondWord = ((GreveItem)otherGreveItem).greviste.replaceFirst("^les ", "").toLowerCase();
			return areSynonyms(firstWord, secondWord) || firstWord.contains(secondWord) || secondWord.contains(firstWord);
		} else {
			return false;
		}
		
	}
	
	@Override
	public String toString() {
		return greviste;
	}
	
	public static boolean areSynonyms(String firstWord, String secondWord){
		return profs.contains(firstWord) && profs.contains(secondWord);
	}
}

