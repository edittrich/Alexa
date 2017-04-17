package de.edittrich.alexa.pokewelt;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Evaluation {
	private String Name;
	private int BonbonAnzahl;
	private String BonbonTyp;
	private int ItemAnzahl;
	private String ItemTyp;

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    
    public int getBonbonAnzahl() {
        return BonbonAnzahl;
    }
    public void setBonbonAnzahl(int BonbonAnzahl) {
        this.BonbonAnzahl = BonbonAnzahl;
    }
    
    public String getBonbonTyp() {
        return BonbonTyp;
    }
    public void setBonbonTyp(String BonbonTyp) {
        this.BonbonTyp = BonbonTyp;
    }
    
    public int getItemAnzahl() {
        return ItemAnzahl;
    }
    public void setItemAnzahl(int ItemAnzahl) {
        this.ItemAnzahl = ItemAnzahl;
    }
  
    public String getItemTyp() {
        return ItemTyp;
    }
    public void setItemTyp(String ItemTyp) {
        this.Name = ItemTyp;
    }
    
    @Override
    public String toString() {
        return "Pokemon [Name = " + Name
        	+ ", BonbonsAnzahl = " + BonbonAnzahl
        	+ ", BonbonTyp = " + BonbonTyp
        	+ ", ItemAnzahl = " + ItemAnzahl
        	+ ", ItemTyp = " + ItemTyp + "]";
    }

}