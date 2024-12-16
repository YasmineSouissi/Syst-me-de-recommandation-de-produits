package Main;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class EDA {
    public static void main(String[] args) {
        // Spécifier les chemins des fichiers CSV
        String moviesFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//movies.csv";
        String ratingsFile = "E://Docs//Weka Projects//ProductRecommendation//DataSets//ratings.csv";
        
        // Charger les données via DataLoader
        List<String[]> moviesData = DataLoader.loadCSV(moviesFile);
        List<String[]> ratingsData = DataLoader.loadCSV(ratingsFile);
        
        // Calculer la note moyenne pour chaque film
        Map<Integer, Double> averageRatings = calculateAverageRatings(ratingsData);
        
        // Identifier les 5 films les mieux notés
        List<String[]> topRatedMovies = getTopRatedMovies(averageRatings, moviesData);
        
        // Afficher les 5 films les mieux notés dans la console
        System.out.println("Les 5 films les mieux notés :");
        for (String[] movie : topRatedMovies) {
            int movieId = Integer.parseInt(movie[0]);
            System.out.println(movie[1] + " - " + movie[2] + " - Note moyenne: " + averageRatings.get(movieId));
        }
    }

    // Méthode pour calculer la note moyenne pour chaque film
    public static Map<Integer, Double> calculateAverageRatings(List<String[]> ratingsData) {
        Map<Integer, List<Double>> ratingsMap = new HashMap<>();
        
        // Parcourir les données de ratings pour accumuler les évaluations pour chaque film
        for (int i = 1; i < ratingsData.size(); i++) { // Ignorer la première ligne (en-tête)
            String[] row = ratingsData.get(i);
            int movieId = Integer.parseInt(row[1]); // ID du film
            double rating = Double.parseDouble(row[2]); // Note du film
            ratingsMap.computeIfAbsent(movieId, k -> new ArrayList<>()).add(rating);
        }
        
        // Calculer la note moyenne pour chaque film
        Map<Integer, Double> averageRatings = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> entry : ratingsMap.entrySet()) {
            double averageRating = entry.getValue().stream().mapToDouble(r -> r).average().orElse(0.0);
            averageRatings.put(entry.getKey(), averageRating);
        }
        
        return averageRatings;
    }

    // Méthode pour obtenir les 5 films les mieux notés
    public static List<String[]> getTopRatedMovies(Map<Integer, Double> averageRatings, List<String[]> moviesData) {
        // Ignorer la première ligne (en-tête) du fichier movies.csv
        List<String[]> moviesWithoutHeader = moviesData.subList(1, moviesData.size());

        return moviesWithoutHeader.stream()
            .sorted((m1, m2) -> Double.compare(
                averageRatings.getOrDefault(Integer.parseInt(m2[0]), 0.0),
                averageRatings.getOrDefault(Integer.parseInt(m1[0]), 0.0)))
            .limit(5)
            .collect(Collectors.toList());
    }
}
