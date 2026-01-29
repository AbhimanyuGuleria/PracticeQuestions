// --- Learning Objective ---
// This tutorial will teach you how to read data from a CSV file
// and create a very basic graphical bar chart using Java.
// We will focus on using the OpenCSV library for CSV parsing
// and a simple plotting library for visualization.
// This will cover file I/O, data parsing, and basic charting concepts.

// Import necessary libraries for file handling, CSV parsing, and charting.
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// For CSV reading, we'll use the popular OpenCSV library.
// Make sure you have this dependency added to your project.
// Example Maven dependency:
// <dependency>
//     <groupId>com.opencsv</groupId>
//     <artifactId>opencsv</artifactId>
//     <version>5.8</version>
// </dependency>
import com.opencsv.CSVReader;

// For simple plotting, we'll use the XChart library.
// It's easy to integrate and create basic charts.
// Example Maven dependency:
// <dependency>
//     <groupId>org.knowm.xchart</groupId>
//     <artifactId>xchart</artifactId>
//     <version>3.8.5</version>
// </dependency>
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;

public class CsvVisualizer {

    public static void main(String[] args) {
        // --- Step 1: Define the CSV file path ---
        // This is the location of our data file.
        // For this example, we assume a file named "data.csv" is in the same directory.
        String csvFilePath = "data.csv";

        // --- Step 2: Read data from CSV and prepare for plotting ---
        // We will store our data in lists, suitable for plotting libraries.
        List<String> categories = new ArrayList<>(); // For labels on the X-axis (e.g., product names)
        List<Number> values = new ArrayList<>();     // For numerical data on the Y-axis (e.g., sales figures)

        try {
            // Use a FileReader to open the CSV file.
            FileReader fileReader = new FileReader(csvFilePath);
            // Create a CSVReader object from OpenCSV to easily parse the file.
            CSVReader csvReader = new CSVReader(fileReader);

            // Read all lines from the CSV file.
            List<String[]> allData = csvReader.readAll();

            // Close the reader and file resources to prevent memory leaks.
            csvReader.close();
            fileReader.close();

            // --- Step 3: Process the CSV data ---
            // We assume the first row is a header and skip it.
            // The actual data starts from the second row.
            // We also assume the first column is the category and the second is the value.
            for (int i = 1; i < allData.size(); i++) { // Start from index 1 to skip header
                String[] row = allData.get(i); // Get the current row of data

                // Extract the category (e.g., "Apple", "Banana") from the first column.
                String category = row[0];
                categories.add(category); // Add to our list of categories

                // Extract the value (e.g., "100", "150") from the second column.
                // We need to convert it to a number (e.g., Integer or Double).
                // Using Double here for flexibility.
                try {
                    double value = Double.parseDouble(row[1]);
                    values.add(value); // Add to our list of values
                } catch (NumberFormatException e) {
                    // If a value cannot be parsed as a number, print an error and skip the row.
                    System.err.println("Skipping row " + (i + 1) + ": Invalid number format for value: " + row[1]);
                }
            }

            // --- Step 4: Create and configure the bar chart ---
            // Use CategoryChartBuilder to create a new chart.
            CategoryChart chart = new CategoryChartBuilder()
                    .width(800) // Set the width of the chart image.
                    .height(600) // Set the height of the chart image.
                    .title("Sales Data Visualization") // Set the main title of the chart.
                    .xAxisTitle("Product Category") // Set the label for the X-axis.
                    .yAxisTitle("Sales Volume") // Set the label for the Y-axis.
                    .build(); // Build the chart object.

            // Customize the chart style for better readability.
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW); // Position the legend in the North-West corner.
            chart.getStyler().setAvailableCompleteness(true); // Enable all style options.
            chart.getStyler().setChartPadding(15); // Add some padding around the chart.
            chart.getStyler().setPlotContentHorizontalAlign(Styler.ChartHorizontalAlignment.CENTER); // Center the plot horizontally.

            // Add the series (the actual data to be plotted) to the chart.
            // The first argument is the series name (can be the Y-axis title or something descriptive).
            // The second argument is the list of categories (X-axis labels).
            // The third argument is the list of numerical values (Y-axis data).
            chart.addSeries("Sales", categories, values);

            // --- Step 5: Save the chart as an image file ---
            // We use BitmapEncoder to save the chart in various image formats.
            // For this example, we'll save it as a PNG file.
            BitmapEncoder.saveBitmapToFile(chart, "./sales_chart.png");
            System.out.println("Chart saved successfully to sales_chart.png");

        } catch (IOException e) {
            // Handle any errors that occur during file reading or processing.
            System.err.println("Error reading CSV file or generating chart: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for detailed error information.
        } catch (Exception e) {
            // Catch any other unexpected exceptions.
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// --- Example Usage ---
// To run this code:
// 1. Save it as CsvVisualizer.java.
// 2. Create a file named "data.csv" in the same directory with the following content:
//
// Category,Sales
// Apple,150
// Banana,200
// Orange,120
// Grapes,180
// Mango,90
//
// 3. Make sure you have the OpenCSV and XChart libraries added to your project's classpath
//    (e.g., by adding them as dependencies in your build tool like Maven or Gradle).
//
// 4. Compile and run the Java file from your terminal:
//    javac CsvVisualizer.java
//    java CsvVisualizer
//
// 5. After execution, you should find a "sales_chart.png" image file generated in the same directory,
//    displaying a bar chart of the sales data.