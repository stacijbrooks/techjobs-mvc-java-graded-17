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

    @PostMapping(value = "results")
    public String displaySearchResults(Model model, @RequestParam String searchType, @RequestParam String searchTerm) {
        ArrayList<Job> jobs;

        // Check if the search type is 'all'
        if (searchType.equals("all")) {
            // If the search term is null or empty
            if (searchTerm == null || searchTerm.equals("")) {
                // Retrieve all jobs
                jobs = JobData.findAll();
                // Set searchTerm to an empty string if it is null or empty
                //searchTerm = "";
            } else {
                // If the search term is not empty, find jobs by the search term value
                jobs = JobData.findByValue(searchTerm);
            }
        } else {
            // If the search type is not 'all', find jobs by the specified column and value
            jobs = JobData.findByColumnAndValue(searchType, searchTerm);
        }

        // Add the list of jobs to the model
        model.addAttribute("jobs", jobs);
        // Add the column choices to the model
        model.addAttribute("columns", ListController.columnChoices);
        // Add the search term to the model
        model.addAttribute("searchTerm", searchTerm);
        // Add the search type to the model
        model.addAttribute("searchType", searchType);

        // Return the 'search' view
        return "search";
    }
}