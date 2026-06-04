package com.mangomusic.ui;

import com.mangomusic.data.ReportsDao;
import com.mangomusic.models.ReportResult;
import com.mangomusic.util.ConsoleColors;
import com.mangomusic.util.InputValidator;

import java.util.List;

public class SpecialReportsScreen {

    private final ReportsDao reportsDao;

    public SpecialReportsScreen(ReportsDao reportsDao) {
        this.reportsDao = reportsDao;
    }

    public void display() {
        boolean running = true;

        while (running) {
            InputValidator.clearScreen();
            displayMenu();

            int choice = InputValidator.getIntInRange("Select an option: ", 0, 4);

            switch (choice) {
                case 1:
                    showMangoMusicMapped();
                    break;
                case 2:
                    showMostPlayedAlbumsByGenre();
                    break;
                case 3:
                    showUserDiversityScore();
                    break;
                case 4:
                    showPeakListeningHours();
                    break;
                case 0:
                    running = false;
                    break;
            }
        }
    }

    private void displayMenu() {
        ConsoleColors.printHeader("SPECIAL REPORTS");

        System.out.println("\nADVANCED ANALYTICS:");
        System.out.println("1. MangoMusic Mapped (Year in Review)");
        System.out.println("2. Most Played Albums by Genre");
        System.out.println("3. User Listening Diversity Score");
        System.out.println("4. Peak Listening Hours Analysis");

        System.out.println("\n0. Back to main menu");
        System.out.println();
    }

    private void showMangoMusicMapped() {
        InputValidator.clearScreen();
        ConsoleColors.printHeader("🎵 MANGOMUSIC MAPPED 🎵");
        System.out.println("Your personalized year in review\n");

        int userId = InputValidator.getIntInRange("Enter user ID: ", 1, Integer.MAX_VALUE);

        ReportResult mapped = reportsDao.getMangoMusicMapped(userId);

        int year = mapped.getInt("year");

        if (mapped.getInt("total_plays") == 0) {
            ConsoleColors.printWarning("No listening data found for user ID " + userId + " in " + year + ".");
        } else {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("YOUR " + year + " LISTENING STORY");
            System.out.println("=".repeat(70));

            System.out.println("\n🎧 LISTENING STATS:");
            System.out.println("   Total Plays: " + mapped.getInt("total_plays"));
            System.out.println("   Albums Explored: " + mapped.getInt("unique_albums"));
            System.out.println("   Artists Discovered: " + mapped.getInt("unique_artists"));
            System.out.println("   Completion Rate: " +
                    String.format("%.1f%%", (mapped.getInt("completed_plays") * 100.0 / mapped.getInt("total_plays"))));

            System.out.println("\n⭐ YOUR TOP PICKS:");
            System.out.println("   #1 Artist: " + mapped.getString("top_artist") +
                    " (" + mapped.getInt("top_artist_plays") + " plays)");
            System.out.println("   Favorite Genre: " + mapped.getString("top_genre"));
            System.out.println("   Most Active Month: " + mapped.getString("top_month") +
                    " (" + mapped.getInt("top_month_plays") + " plays)");

            System.out.println("\n🔥 FUN FACTS:");
            System.out.println("   Longest Listening Streak: " + mapped.getInt("longest_streak") + " days");
            System.out.println("   Listener Personality: " + mapped.getString("listener_personality"));

            System.out.println("\n" + "=".repeat(70));
            System.out.println("Thanks for making " + year + " a year full of music! 🎶");
            System.out.println("=".repeat(70));
        }

        InputValidator.pressEnterToContinue();
    }

    private void showMostPlayedAlbumsByGenre() {
        InputValidator.clearScreen();
        ConsoleColors.printSection("Most Played Albums by Genre");
        System.out.println("Shows top 5 albums per genre by play count\n");

        List<ReportResult> results = reportsDao.getMostPlayedAlbumsByGenre();

        if (results.isEmpty()) {
            ConsoleColors.printWarning("No data available for this report.");
        } else {
            System.out.printf("%-20s %-50s %-40s %12s %6s%n",
                    "Genre", "Album", "Artist", "Play Count", "Rank");
            System.out.println("-".repeat(135));

            String currentGenre = "";
            for (ReportResult result : results) {
                String genre = result.getString("genre");
                if (!genre.equals(currentGenre)) {
                    if (!currentGenre.isEmpty()) {
                        System.out.println();
                    }
                    currentGenre = genre;
                }

                System.out.printf("%-20s %-50s %-40s %12s %6s%n",
                        truncate(result.getString("genre"), 20),
                        truncate(result.getString("album_title"), 50),
                        truncate(result.getString("artist_name"), 40),
                        result.getInt("play_count"),
                        result.getInt("genre_rank"));
            }
        }

        InputValidator.pressEnterToContinue();
    }

    private void showUserDiversityScore() {
        InputValidator.clearScreen();
        ConsoleColors.printSection("User Listening Diversity Score");
        System.out.println("Shows users who explore different genres\n");

        List<ReportResult> results = reportsDao.getUserDiversityReport();

        if (results.isEmpty()) {
            ConsoleColors.printWarning("No data available for this report.");
        } else {
            System.out.printf("%-8s %-20s %-15s %10s %10s %15s %18s%n",
                    "User ID", "Username", "Subscription", "Genres", "Artists", "Total Plays", "Diversity Score");
            System.out.println("-".repeat(110));

            int displayCount = Math.min(results.size(), 50);
            for (int i = 0; i < displayCount; i++) {
                ReportResult result = results.get(i);
                System.out.printf("%-8s %-20s %-15s %10s %10s %15s %17.2f%n",
                        result.getInt("user_id"),
                        truncate(result.getString("username"), 20),
                        result.getString("subscription_type"),
                        result.getInt("distinct_genres_played"),
                        result.getInt("distinct_artists_played"),
                        result.getInt("total_plays"),
                        result.getDouble("diversity_score"));
            }

            if (results.size() > 50) {
                System.out.println("\n... and " + (results.size() - 50) + " more users");
            }
        }

        InputValidator.pressEnterToContinue();
    }

    private void showPeakListeningHours() {
        InputValidator.clearScreen();
        ConsoleColors.printSection("Peak Listening Hours Analysis");
        System.out.println("Shows what time of day users are most active\n");

        List<ReportResult> results = reportsDao.getPeakListeningHoursReport();

        if (results.isEmpty()) {
            ConsoleColors.printWarning("No data available for this report.");
        } else {
            System.out.printf("%-6s %15s %15s %20s%n",
                    "Hour", "Total Plays", "Unique Users", "Avg Plays/User");
            System.out.println("-".repeat(60));

            for (ReportResult result : results) {
                int hour = result.getInt("hour_of_day");
                String timeLabel = String.format("%02d:00", hour);

                System.out.printf("%-6s %15s %15s %19.2f%n",
                        timeLabel,
                        result.getInt("total_plays"),
                        result.getInt("unique_users"),
                        result.getDouble("avg_plays_per_user"));
            }
        }

        InputValidator.pressEnterToContinue();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}