package Main;

import java.io.*;
import weka.core.*;

public class CSVtoARFF {

    public static void main(String[] args) throws Exception {
        String inputFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//encoded_movies.csv"; // Fichier CSV d'entrée
        String outputFile = "output.arff"; // Fichier ARFF de sortie
        
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        // Créer l'instance d'un fichier ARFF
        FastVector attributes = new FastVector();
        FastVector data = new FastVector();

        // Lire l'en-tête du fichier CSV pour récupérer les noms des attributs
        String headerLine = reader.readLine();
        String[] headers = headerLine.split(",");
        
        for (String header : headers) {
            // Ajouter des attributs à partir des noms de colonnes (généralement des attributs numériques ou catégoriels)
            attributes.addElement(new Attribute(header.trim()));
        }

        // Lire chaque ligne de données
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            // Créer une instance
            Instance instance = new DenseInstance(headers.length);
            for (int i = 0; i < headers.length; i++) {
                instance.setValue((Attribute) attributes.elementAt(i), values[i]);
            }
            data.addElement(instance);
        }
        
        // Créer une structure d'instances ARFF
        Instances instances = new Instances("Movies", attributes, data.size());
        for (int i = 0; i < data.size(); i++) {
            instances.add((Instance) data.elementAt(i));
        }

        // Sauvegarder le fichier ARFF
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(instances.toString());
        writer.close();
        
        System.out.println("Fichier ARFF généré avec succès à : " + outputFile);
    }
}
