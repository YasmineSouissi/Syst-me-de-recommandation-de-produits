package Main;
import java.io.*;
import java.util.*;

public class DataLoader {
    public static void main(String[] args) {
        String moviesFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//movies.csv";

        String ratingsFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//ratings.csv";

        List<String[]> moviesData = loadCSV(moviesFile);
        List<String[]> ratingsData = loadCSV(ratingsFile);

        // Afficher les 5 premières lignes
        System.out.println("Les 5 premières lignes de movies.csv :");
        printFirstLines(moviesData, 5);

        System.out.println("\nLes 5 premières lignes de ratings.csv :");
        printFirstLines(ratingsData, 5);

        // Vérifier les valeurs manquantes
        System.out.println("\nValeurs manquantes dans ratings.csv :");
        checkMissingValues(ratingsData);
    }

    // Méthode pour charger un fichier CSV
    public static List<String[]> loadCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Divise la ligne par les virgules, en tenant compte des valeurs avec des guillemets
                String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                data.add(row);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier : " + filePath);
            e.printStackTrace();
        }
        return data;
    }

    // Méthode pour afficher les premières lignes d'un fichier
    public static void printFirstLines(List<String[]> data, int lines) {
        for (int i = 0; i < Math.min(lines, data.size()); i++) {
            System.out.println(Arrays.toString(data.get(i)));
        }
    }

    // Méthode pour vérifier les valeurs manquantes dans un fichier
    public static void checkMissingValues(List<String[]> data) {
        if (data.isEmpty()) {
            System.out.println("Le fichier est vide ou introuvable.");
            return;
        }

        // Vérifier ligne par ligne
        String[] headers = data.get(0);
        int[] missingCounts = new int[headers.length];
        boolean hasMissingValues = false;

        for (int i = 1; i < data.size(); i++) { // Ignorer l'en-tête
            String[] row = data.get(i);
            for (int j = 0; j < headers.length; j++) {
                if (j >= row.length || row[j] == null || row[j].trim().isEmpty()) {
                    missingCounts[j]++;
                    hasMissingValues = true;
                }
            }
        }

        // Afficher les résultats
        if (hasMissingValues) {
            System.out.println("Colonnes avec valeurs manquantes :");
            for (int i = 0; i < headers.length; i++) {
                if (missingCounts[i] > 0) {
                    System.out.printf("%s : %d valeurs manquantes\n", headers[i], missingCounts[i]);
                }
            }
        } else {
            System.out.println("Aucune valeur manquante trouvée dans le fichier.");
        }
    }

}
