package de.edittrich.alexa.pokewelt;

import java.util.List;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Pokemon")
public class Pokemon {
    private String Name;
    private int Number;
    private Set<String> Types;
    private List<Evaluation> Evaluations;
    private int WP;
    private int KP;
    private int Angriff;
    private int Verteidigung;
    private int Distanz;
    private int Ei;

    @DynamoDBHashKey(attributeName="Name")
    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    @DynamoDBAttribute(attributeName="Number")
    public int getNumber() {
        return Number;
    }
    public void setNumber(int Number) {
        this.Number = Number;
    }

    @DynamoDBAttribute(attributeName="Types")
    public Set<String> getTypes() {
        return Types;
    }
    public void setTypes(Set<String> Types) {
        this.Types = Types;
    }

    @DynamoDBAttribute(attributeName="Evaluations")
    public List<Evaluation> getEvaluations() {
        return Evaluations;
    }
    public void setEvaluations(List<Evaluation> Evaluations) {
        this.Evaluations = Evaluations;
    }
        
    @DynamoDBAttribute(attributeName="WP")
    public int getWP() {
        return WP;
    }
    public void setWP(int WP) {
        this.WP = WP;
    }
    
    @DynamoDBAttribute(attributeName="KP")
    public int getKP() {
        return KP;
    }
    public void setKP(int KP) {
        this.KP = KP;
    }
    
    @DynamoDBAttribute(attributeName="Angriff")
    public int getAngriff() {
        return Angriff;
    }
    public void setAngriff(int Angriff) {
        this.Angriff = Angriff;
    }
    
    @DynamoDBAttribute(attributeName="Verteidigung")
    public int getVerteidigung() {
        return Verteidigung;
    }
    public void setVerteidigung(int Verteidigung) {
        this.Verteidigung = Verteidigung;
    }
    
    @DynamoDBAttribute(attributeName="Distanz")
    public int getDistanz() {
        return Distanz;
    }
    public void setDistanz(int Distanz) {
        this.Distanz = Distanz;
    }

    @DynamoDBAttribute(attributeName="Ei")
    public int getEi() {
        return Ei;
    }
    public void setEi(int Ei) {
        this.Ei = Ei;
    }

    @Override
    public String toString() {
        return "Pokemon [Name = " + Name 
        	+ ", Number = " + Number
            + ", Types = " + Types
            + ", Evaluations = " + Evaluations
            + ", WP = " + WP
            + ", KP = " + KP
            + ", Angriff = " + Angriff
            + ", Verteidigung = " + Verteidigung
            + ", Distanz = " + Distanz
            + ", Ei = " + Ei + "]";
    }

}
