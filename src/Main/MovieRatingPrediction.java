package Main;

import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.Evaluation;
import java.util.*;
import java.io.*;
import weka.core.Attribute;
import weka.core.DenseInstance;



public class MovieRatingPrediction {

    public static void main(String[] args) throws Exception {
        String moviesFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//encoded_movies.csv";

        // Charger les données
        List<String[]> encodedData = loadCSV(moviesFile);
        Instances data = prepareWekaInstances(encodedData);

        // Diviser les données en train (70%), test (15%) et validation (15%)
        Instances[] splitData = splitData(data, 0.7, 0.15); // 70% pour l'entraînement, 15% pour le test et 15% pour la validation
        Instances trainData = splitData[0];
        Instances testData = splitData[1];
        Instances validationData = splitData[2];

        // Entraîner le modèle RandomForest
        RandomForest rf = new RandomForest();
        rf.buildClassifier(trainData);

        // Évaluer le modèle sur l'ensemble de test
        evaluateModel(rf, testData, "Test");
        
        // Évaluer le modèle sur l'ensemble de validation
        evaluateModel(rf, validationData, "Validation");
    }

    // Méthode pour charger le fichier CSV
    public static List<String[]> loadCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Préparer les données en format Instances pour Weka
    public static Instances prepareWekaInstances(List<String[]> data) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Ajout des attributs pour chaque genre encodé (en supposant qu'ils commencent après 'movieId' et 'title')
        for (int i = 2; i < data.get(0).length - 1; i++) {
            attributes.add(new Attribute("genre" + (i - 2)));  // Nommer les genres encodés
        }

        // Ajouter l'attribut cible (moyenne des notes)
        attributes.add(new Attribute("average_rating"));

        // Créer une structure d'Instances
        Instances dataset = new Instances("MovieRatings", attributes, data.size());

        // Remplir les données (en ignorant l'en-tête et la colonne 'movieId' et 'title')
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);

            // Extraire les valeurs des genres
            double[] rowValues = new double[dataset.numAttributes()];
            for (int j = 2; j < row.length - 1; j++) {
                rowValues[j - 2] = Double.parseDouble(row[j]);
            }

            // Ajouter la note moyenne
            rowValues[rowValues.length - 1] = Double.parseDouble(row[row.length - 1]);  // La note moyenne est la dernière colonne

            // Créer une instance et l'ajouter au dataset
            dataset.add(new DenseInstance(1.0, rowValues));
        }

        // Définir l'attribut cible (ici, la note moyenne) comme l'attribut à prédire
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }

    // Diviser les données en trois ensembles : train (70%), test (15%) et validation (15%)
    public static Instances[] splitData(Instances data, double trainRatio, double testRatio) {

    	int trainSize = (int) Math.round(data.numInstances() * trainRatio);
        int testSize = (int) Math.round(data.numInstances() * testRatio);
        int validationSize = data.numInstances() - trainSize - testSize;


        data.randomize(new Random(1));


        Instances trainData = new Instances(data, 0, trainSize);
        Instances testData = new Instances(data, trainSize, testSize);
        Instances validationData = new Instances(data, trainSize + testSize, validationSize);

        return new Instances[]{trainData, testData, validationData};
    }

    // Méthode pour évaluer le modèle (calculer le RMSE)
    public static void evaluateModel(RandomForest model, Instances data, String datasetName) throws Exception {

    	Evaluation eval = new Evaluation(data);
        eval.evaluateModel(model, data);
        System.out.println(datasetName + " - RMSE: " + eval.rootMeanSquaredError());
    }
}
