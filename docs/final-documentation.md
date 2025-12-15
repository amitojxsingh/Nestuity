# Final Documentation

## Deployment Instructions

### Description
This section provides step-by-step guidance to deploy and run our system on their own servers. 

### Resources 
- [Nestuity Deployment Guide](./deployment-instructions.md)

## User Manual 

### Description
We have created a user manual for Nestuity to ensure that all end-users will easily be able to use our app. 

### Key Features
- Clear and easy-to-follow steps to use our key features
- Troubleshooting tips

### Resources
- [Nestuity User Manual](documents/User-Manual.pdf)

## Job Description for Software Developer 
Our client might need someone on board to install and configure our system, maintain and update our system, fix accidental bugs, and develop new features. We have provided a [ready-to-publish job description](documents/Nestuity-Job-Description.pdf) that includes all the necessary skills that someone would need to work on Nestuity. 

## CMPUT 401 Portal
To check out similar projects and our project information, feel free to check out our [CMPUT 401 portal](https://cmput401.ca/)!

## Screencast
Want to see Nestuity in action? Watch the [screencast](./screencast.md)!

## Codacy Results - Static Test Analysis
Please see the results of our codebase's static test analysis below. 
- [SpotBugs](./static-analysis.md)
- [Codacy results](https://docs.google.com/document/d/1PBQe7aUHmbB4tj7r97MJon8u3Mp4a69EteEs_rsMlZg/edit?tab=t.0)

## API Documentation

### Description

Our backend provides a set of RESTful APIs for managing baby products, reminders, inventory, price predictions, user accounts, emails, and usage calculations. Each controller is documented separately, and the full documentation can be viewed under the `/api` directory. Each controller has its own Markdown (`.md`) file detailing endpoints, parameters, request/response examples, and expected behavior.

- [see a more in depth API documentation here](./api-documentation.md)

### Accessing the Documentation

* Navigate to the `/api` directory in the project repository.
* Each controller has a corresponding Markdown file:
    * `BabyController.md`
    * `BabyProductController.md`
    * `BabyReminderController.md`
    * `EmailTestController.md`
    * `InventoryController.md`
    * `PricePredictionController.md`
    * `PriceScraperAdminController.md`
    * `PriceUpdateController.md`
    * `UsageCalculatorController.md`
    * `UserController.md`
  
These documentations will allow the developers and testers to quickly access documentation for any API without needing to read through code.
