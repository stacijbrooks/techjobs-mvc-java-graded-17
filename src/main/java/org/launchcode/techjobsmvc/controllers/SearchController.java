package org.launchcode.techjobsmvc.controllers;

import org.launchcode.techjobsmvc.models.Job;
import org.launchcode.techjobsmvc.models.JobData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

import static org.launchcode.techjobsmvc.controllers.ListController.columnChoices;


/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("search")
public class SearchController {

    @GetMapping(value = "")
    public String search(Model model) {
        model.addAttribute("columns", columnChoices);
        return "search";
    }

    // TODO #3 - Create a handler to process a search request and render the updated search view.

    // Handler method to process search request and display search results
    @PostMapping(value = "results")
    public String displaySearchResults(Model model, @RequestParam String searchType, @RequestParam String searchTerm) {
        // Declare a list to store search results
        ArrayList<Job> jobs;

        // Check if searchType is "all"
        if (searchType.equals("all")) {
            // If searchType is "all", check if searchTerm is null or empty
            if (searchTerm == null || searchTerm.equals("")) {
                // If searchTerm is null or empty, retrieve all jobs
                jobs = JobData.findAll();
                searchTerm = "";  // Set searchTerm to an empty string when it's null or empty
            } else {
                // Otherwise, perform a search by value
                jobs = JobData.findByValue(searchTerm);
            }
        } else {
            // If searchType is not "all", perform a search by column and value
            jobs = JobData.findByColumnAndValue(searchType, searchTerm);
        }

        // Add search results, column choices, searchTerm, and searchType to the model
        model.addAttribute("jobs", jobs);
        model.addAttribute("columns", ListController.columnChoices);
        model.addAttribute("searchTerm", searchTerm);  // Pass searchTerm to the view
        model.addAttribute("searchType", searchType);  // Pass searchType to the view

        // Return the name of the view to render
        return "search";
    }
}