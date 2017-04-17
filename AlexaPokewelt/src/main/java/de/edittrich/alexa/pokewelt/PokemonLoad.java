package de.edittrich.alexa.pokewelt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import de.edittrich.alexa.pokewelt.Pokemon;

public class PokemonLoad {

	public static void main(String[] args) {
		
        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
            	.withRegion(Regions.US_EAST_1)
            	.withCredentials(new DefaultAWSCredentialsProviderChain())
            	.build();
        
        //
        
        String csvFile = "pokemon.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String setSplitBy = ",";
        String mapSplitBy = "/";
        int i = 0;
        Pokemon pokemon = null;
        Evaluation evaluation = null;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] pokemonData = line.split(cvsSplitBy);
                
                i++;
                if (i > 1) {
	                System.out.println("Pokemon [Number = " + pokemonData[0] + ", Name=" + pokemonData[1] + ", Typ=" + pokemonData[2] + ", Evaluation=" + pokemonData[3]+ "]");
	                
	                pokemon = new Pokemon();
	                pokemon.setName(pokemonData[1]);
	                pokemon.setNumber(new Integer(pokemonData[0]));
	                
	                String[] pokemonTypes = pokemonData[2].split(setSplitBy);
	                Set<String> types = new TreeSet<String>();	                
	                for (int j = 0; j < pokemonTypes.length; j++)
	                	types.add(pokemonTypes[j]);
	                pokemon.setTypes(types);
	                
	                String[] pokemonEvaluations = pokemonData[3].split(mapSplitBy);
	                List<Evaluation> evaluations = new ArrayList<Evaluation>();	                
	                for (int j = 0; j < pokemonEvaluations.length; j++)
	                	if (pokemonEvaluations[j].length() > 0) {
	                		evaluation = new Evaluation();
	                		String[] pokemonEvaluationsValues = pokemonEvaluations[j].split(setSplitBy);
	                		evaluation.setName(pokemonEvaluationsValues[0]);
	                		evaluation.setBonbonAnzahl(new Integer(pokemonEvaluationsValues[1]));
	                		evaluation.setBonbonTyp(pokemonEvaluationsValues[2]);
	                		if (pokemonEvaluationsValues.length == 5) {
		                		evaluation.setItemAnzahl(new Integer(pokemonEvaluationsValues[3]));
		                		evaluation.setItemTyp(pokemonEvaluationsValues[4]);
	                		}
	                		evaluations.add(evaluation);
	                	}
	                if (!evaluations.isEmpty())
	                		pokemon.setEvaluations(evaluations);
	                
	                pokemon.setWP(new Integer(pokemonData[4]));
	                pokemon.setKP(new Integer(pokemonData[5]));
	                pokemon.setAngriff(new Integer(pokemonData[6]));
	                pokemon.setVerteidigung(new Integer(pokemonData[7]));
	                pokemon.setDistanz(new Integer(pokemonData[8]));
	                
	                if (pokemonData.length == 10) {
	                	pokemon.setEi(new Integer(pokemonData[9]));
	                }
	                
	                DynamoDBMapper mapper = new DynamoDBMapper(ddb);
	                mapper.save(pokemon);
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        //
        
        ddb.shutdown();

	}

}