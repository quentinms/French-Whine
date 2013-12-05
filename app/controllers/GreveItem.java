package controllers;

public class GreveItem{
	public String greviste;
	public String newsUrl;
	public GreveItem(String greviste, String newsUrl){
		this.greviste = greviste;
		this.newsUrl = newsUrl;
	}
	
	@Override
	public boolean equals(Object otherGreveItem) {
		
		if(otherGreveItem.getClass().equals(String.class)){
			return this.greviste.equals(otherGreveItem);
		} else if (otherGreveItem.getClass().equals(this.getClass())){
			return this.greviste.replaceFirst("^les ", "").equals(((GreveItem)otherGreveItem).greviste.replaceFirst("^les ", ""));
		} else {
			return false;
		}
		
	}
	
	@Override
	public String toString() {
		return greviste;
	}
}