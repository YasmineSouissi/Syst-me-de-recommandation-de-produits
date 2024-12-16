package Main;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataPreparation {
public static void main(String[] args) {
    String moviesFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//movies.csv";
    String outputFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//encoded_movies.csv"; // Fichier de sortie

    List<String[]> moviesData = loadCSV(moviesFile);

    if (!moviesData.isEmpty()) {
        System.out.println("Encodage des genres...");
        List<String[]> encodedData = encodeGenres(moviesData);

        // Afficher les 5 premières lignes encodées
        System.out.println("\nLes 5 premières lignes avec genres encodés :");
        printFirstLines(encodedData, 5);

        // Enregistrer les résultats dans un fichier CSV
        saveCSV(encodedData, outputFile);
    } else {
        System.out.println("Les données de movies.csv sont introuvables ou vides.");
    }
}

    // Méthode pour charger un fichier CSV
    public static List<String[]> loadCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Gère les valeurs avec des guillemets
                data.add(row);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier : " + filePath);
            e.printStackTrace();
        }
        return data;
    }

 // Méthode pour enregistrer les données dans un fichier CSV
    public static void saveCSV(List<String[]> data, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                String line = String.join(",", row);
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Les données ont été enregistrées dans le fichier : " + filePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement du fichier : " + filePath);
            e.printStackTrace();
        }
    }

    
    // Méthode pour encoder les genres dans des colonnes binaires et supprimer la colonne genres
    public static List<String[]> encodeGenres(List<String[]> data) {
        // Récupérer les genres uniques
        Set<String> uniqueGenres = new HashSet<>();
        int genresColumnIndex = Arrays.asList(data.get(0)).indexOf("genres");
        if (genresColumnIndex == -1) {
            System.out.println("Colonne 'genres' introuvable.");
            return data;
        }

        // Extraire les genres uniques
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            if (genresColumnIndex < row.length) {
                String genres = row[genresColumnIndex];
                uniqueGenres.addAll(Arrays.asList(genres.split("\\|")));
            }
        }

        // Trier les genres pour garantir un ordre fixe
        List<String> sortedGenres = uniqueGenres.stream().sorted().collect(Collectors.toList());

        // Créer un nouvel ensemble de données avec les genres encodés (sans la colonne genres)
        List<String[]> encodedData = new ArrayList<>();
        String[] originalHeader = data.get(0);
        String[] newHeader = Arrays.copyOf(originalHeader, originalHeader.length + sortedGenres.size() - 1);
        
        // Ajouter les colonnes des genres encodés, mais exclure "genres"
        for (int i = 0; i < sortedGenres.size(); i++) {
            newHeader[originalHeader.length + i - 1] = sortedGenres.get(i);
        }
        encodedData.add(newHeader);

        // Encoder chaque ligne sans la colonne genres
        for (int i = 1; i < data.size(); i++) {
            String[] originalRow = data.get(i);
            String[] newRow = new String[originalHeader.length + sortedGenres.size() - 1];

            // Remplir les colonnes de genres encodés avec 0 par défaut
            Arrays.fill(newRow, originalHeader.length - 1, newRow.length, "0");

            // Mettre à 1 pour les genres présents dans la ligne
            if (genresColumnIndex < originalRow.length) {
                String genres = originalRow[genresColumnIndex];
                for (String genre : genres.split("\\|")) {
                    int genreIndex = sortedGenres.indexOf(genre);
                    if (genreIndex != -1) {
                        newRow[originalHeader.length - 1 + genreIndex] = "1";
                    }
                }
            }

            // Copier toutes les autres colonnes sauf la colonne genres
            System.arraycopy(originalRow, 0, newRow, 0, genresColumnIndex);
            System.arraycopy(originalRow, genresColumnIndex + 1, newRow, genresColumnIndex, originalRow.length - genresColumnIndex - 1);

            encodedData.add(newRow);
        }

        return encodedData;
    }

    public static void printFirstLines(List<String[]> data, int lines) {
        for (int i = 0; i < Math.min(lines, data.size()); i++) {
            System.out.println(Arrays.toString(data.get(i)));
        }
    }
}
