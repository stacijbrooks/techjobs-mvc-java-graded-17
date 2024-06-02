package org.launchcode.techjobsmvc.models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.launchcode.techjobsmvc.models.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to manage job data and provide search functionality.
 */
public class JobData {

    // Constants
    private static final String DATA_FILE = "job_data.csv";

    // Flags to indicate whether data has been loaded
    private static boolean isDataLoaded = false;

    // Lists to store all jobs and related entities
    private static ArrayList<Job> allJobs;
    private static ArrayList<Employer> allEmployers = new ArrayList<>();
    private static ArrayList<Location> allLocations = new ArrayList<>();
    private static ArrayList<PositionType> allPositionTypes = new ArrayList<>();
    private static ArrayList<CoreCompetency> allCoreCompetency = new ArrayList<>();

    /**
     * Fetch list of all job objects from loaded data, without duplicates, then return a copy.
     */
    public static ArrayList<Job> findAll() {
        // Load data if not already loaded
        loadData();
        // Return a copy of the list of all jobs
        return new ArrayList<>(allJobs);
    }

    /**
     * Returns the results of searching the Jobs data by field and search term.
     *
     * @param column Job field that should be searched.
     * @param value  Value of the field to search for.
     * @return List of all jobs matching the criteria.
     */
    public static ArrayList<Job> findByColumnAndValue(String column, String value) {
        // Load data if not already loaded
        loadData();
        // Initialize list to store matching jobs
        ArrayList<Job> jobs = new ArrayList<>();

        // Handle special case where value is "all"
        if (value.toLowerCase().equals("all")) {
            return findAll();
        }

        // Search for jobs based on the specified column and value
        if (column.equals("all")) {
            jobs = findByValue(value);
            return jobs;
        }

        // Iterate through all jobs and add matching jobs to the list
        for (Job job : allJobs) {
            String aValue = getFieldValue(job, column);
            if (aValue != null && aValue.toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            }
        }

        return jobs;
    }

    /**
     * Search all Job fields for the given term.
     *
     * @param value The search term to look for.
     * @return List of all jobs with at least one field containing the value.
     */
    public static ArrayList<Job> findByValue(String value) {
        // Load data if not already loaded
        loadData();
        // Initialize list to store matching jobs
        ArrayList<Job> jobs = new ArrayList<>();

        // Iterate through all jobs and add matching jobs to the list
        for (Job job : allJobs) {
            if (job.getName().toLowerCase().contains(value.toLowerCase()) ||
                    job.getEmployer().toString().toLowerCase().contains(value.toLowerCase()) ||
                    job.getLocation().toString().toLowerCase().contains(value.toLowerCase()) ||
                    job.getPositionType().toString().toLowerCase().contains(value.toLowerCase()) ||
                    job.getCoreCompetency().toString().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            }
        }

        return jobs;
    }

    /**
     * Get the value of a specific field for a given job.
     *
     * @param job       The job object.
     * @param fieldName The name of the field.
     * @return The value of the field.
     */
    public static String getFieldValue(Job job, String fieldName) {
        String theValue;
        if (fieldName.equals("name")) {
            theValue = job.getName();
        } else if (fieldName.equals("employer")) {
            theValue = job.getEmployer().toString();
        } else if (fieldName.equals("location")) {
            theValue = job.getLocation().toString();
        } else if (fieldName.equals("positionType")) {
            theValue = job.getPositionType().toString();
        } else {
            theValue = job.getCoreCompetency().toString();
        }

        return theValue;
    }

    /**
     * Load job data from the CSV file.
     */
    private static void loadData() {
        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {
            // Open the CSV file and parse its contents
            Resource resource = new ClassPathResource(DATA_FILE);
            InputStream is = resource.getInputStream();
            Reader reader = new InputStreamReader(is);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            // Initialize the list of all jobs
            allJobs = new ArrayList<>();

            // Iterate through CSV records and populate job and related entity lists
            for (CSVRecord record : records) {
                String aName = record.get(0);
                String anEmployer = record.get(1);
                String aLocation = record.get(2);
                String aPosition = record.get(3);
                String aSkill = record.get(4);

                // Create new instances of related entities if not already existing
                Employer newEmployer = (Employer) findExistingObject(allEmployers, anEmployer);
                Location newLocation = (Location) findExistingObject(allLocations, aLocation);
                PositionType newPosition = (PositionType) findExistingObject(allPositionTypes, aPosition);
                CoreCompetency newSkill = (CoreCompetency) findExistingObject(allCoreCompetency, aSkill);

                // Add new related entities to respective lists if not already existing
                if (newEmployer == null) {
                    newEmployer = new Employer(anEmployer);
                    allEmployers.add(newEmployer);
                }
                if (newLocation == null) {
                    newLocation = new Location(aLocation);
                    allLocations.add(newLocation);
                }
                if (newSkill == null) {
                    newSkill = new CoreCompetency(aSkill);
                    allCoreCompetency.add(newSkill);
                }
                if (newPosition == null) {
                    newPosition = new PositionType(aPosition);
                    allPositionTypes.add(newPosition);
                }

                // Create new job instance and add it to the list of all jobs
                Job newJob = new Job(aName, newEmployer, newLocation, newPosition, newSkill);
                allJobs.add(newJob);
            }

            // Set data loaded flag to true
            isDataLoaded = true;

        } catch (IOException e) {
            // Handle IO exceptions
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

    /**
     * Find an existing object in a list by comparing its string representation.
     *
     * @param list  The list to search.
     * @param value The value to search for.
     * @return The existing object if found, otherwise null.
     */
    private static Object findExistingObject(ArrayList list, String value) {
        for (Object item : list) {
            if (item.toString().toLowerCase().equals(value.toLowerCase())) {
                return item;
            }
        }
        return null;
    }

    public static ArrayList<Employer> getAllEmployers() {
        // Ensure data is loaded
        loadData();
        // Return the list of all employers
        return allEmployers;
    }

    public static ArrayList<Location> getAllLocations() {
        // Ensure data is loaded
        loadData();
        // Return the list of all locations
        return allLocations;
    }

    public static ArrayList<PositionType> getAllPositionTypes() {
        // Ensure data is loaded
        loadData();
        // Return the list of all position types
        return allPositionTypes;
    }

    public static ArrayList<CoreCompetency> getAllCoreCompetency() {
        // Ensure data is loaded
        loadData();
        // Return the list of all core competencies
        return allCoreCompetency;
    }
}
