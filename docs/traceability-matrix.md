# Requirement Traceability Matrix

This document maps user stories to their corresponding acceptance test cases and results, ensuring complete coverage and traceability throughout the development lifecycle.
> **Note:** A lot more features have been implemented than have been tested. This matrix represents the current state of tested functionality only.

---

## Traceability Matrix

| Req. ID | Requirement Description                           | User Story | Test Case ID | Test Result |
|---------|---------------------------------------------------|------------|--------------|-------------|
| 1       | Create an account as a new parent                 | US 1.01    | TC-1.01.01   | Pass        |
| 2       | Register with valid email and password            | US 1.01    | TC-1.01.02   | Pass        |
| 3       | Enter basic information about self and baby       | US 1.01    | TC-1.01.03   | Pass        |
| 4       | Receive confirmation email upon registration      | US 1.01    | TC-1.01.04   | Pass        |
| 5       | Be directed to baby setup flow after registration | US 1.01    | TC-1.01.05   | Pass        |
| 6       | Login with email and password                     | US 1.02    | TC-1.02.01   | Pass        |
| 7       | Logout from account                               | US 1.02    | TC-1.02.02   | Pass        |
| 8       | Reset forgotten password                          | US 1.02    | TC-1.02.03   | Fail        |
| 9       | Stay logged in across sessions                    | US 1.02    | TC-1.02.04   | Pass        |
| 10      | Be directed to dashboard upon login               | US 1.02    | TC-1.02.05   | Pass        |
| 11      | Verify authentication is secure                   | US 1.02    | TC-1.02.06   | Pass        |
| 12      | View and update personal information              | US 1.03    | TC-1.03.01   | Pass        |
| 13      | View and update baby information                  | US 1.03    | TC-1.03.02   | Pass        |
| 14      | Save user changes and reflect immediately         | US 1.03    | TC-1.03.03   | Pass        |
| 15      | Add multiple baby profiles under single account   | US 1.04    | TC-1.04.01   | Fail        |
| 16      | Edit and update each baby's profile               | US 1.04    | TC-1.04.02   | Pass        |
| 17      | Switch between baby profiles accurately           | US 1.04    | TC-1.04.03   | Fail        |
| 18      | App responsiveness across screen sizes            | US 2.01    | TC-2.01.01   | Pass        |
| 19      | Touch-friendliness on mobile devices              | US 2.01    | TC-2.01.02   | Fail        |
| 20      | Functionality across devices and browsers         | US 2.01    | TC-2.01.03   | Fail        |
| 21      | Clear and simple navigation structure             | US 2.02    | TC-2.02.01   | Pass        |
| 22      | Key features accessible from main dashboard       | US 2.02    | TC-2.02.02   | Pass        |
| 23      | Search functionality via search bar               | US 2.02    | TC-2.02.03   | Pass        |
| 24      | Input baby details for calculations               | US 3.01    | TC-3.01.01   | Pass        |
| 25      | Generate product usage estimates                  | US 3.01    | TC-3.01.02   | Pass        |
| 26      | Generate daily, weekly, monthly projections       | US 3.01    | TC-3.01.03   | Pass        |
| 27      | Adjust estimates based on usage patterns          | US 3.01    | TC-3.01.04   | Pass        |
| 28      | Log product usage and inventory levels            | US 3.02    | TC-3.02.01   | Pass        |
| 29      | Update inventory levels with new purchases        | US 3.02    | TC-3.02.02   | Fail        |
| 30      | Real-time inventory level updates                 | US 3.02    | TC-3.02.03   | Pass        |
| 31      | Display usage trends and inventory visuals        | US 3.02    | TC-3.02.04   | Pass        |
| 32      | Export inventory data for records                 | US 3.02    | TC-3.02.05   | Pass        |
| 33      | Add multiple product types                        | US 3.03    | TC-3.03.01   | Pass        |
| 34      | View price trends over time                       | US 4.01    | TC-4.01.01   | Pass        |
| 35      | View prices from multiple retailers               | US 4.01    | TC-4.01.02   | Pass        |
| 36      | Filter and sort by product, retailer, price       | US 4.01    | TC-4.01.03   | Pass        |
| 37      | Regular price updates                             | US 4.01    | TC-4.01.04   | Pass        |
| 38      | Analyze price trends to predict best buy times    | US 4.02    | TC-4.02.01   | Pass        |
| 39      | Notify of current deals and discounts             | US 4.02    | TC-4.02.02   | Pass        |
| 40      | Notify of predicted price drops and sales         | US 4.02    | TC-4.02.03   | Pass        |
| 41      | Visual comparison of products from retailers      | US 4.03    | TC-4.03.01   | Pass        |
| 42      | View key details in comparison                    | US 4.03    | TC-4.03.02   | Pass        |
| 43      | Select products to compare easily                 | US 4.03    | TC-4.03.03   | Pass        |
| 44      | Links to purchase from comparison screen          | US 4.03    | TC-4.03.04   | Pass        |
| 45      | Include shipping costs in total price             | US 4.03    | TC-4.03.05   | Fail        |
| 46      | View list of lowest prices today                  | US 4.04    | TC-4.04.01   | Pass        |
| 47      | Notify of vaccinations and milestones             | US 5.01    | TC-5.01.01   | PASS        |
| 48      | Customize timing and frequency of reminders       | US 5.01    | TC-5.01.02   | PASS        |
| 49      | Access milestone information and resources        | US 5.01    | TC-5.01.03   | PASS        |
| 50      | Mark reminders as completed                       | US 5.01    | TC-5.01.04   | PASS        |
| 51      | Send reminders based on supply predictions        | US 5.02    | TC-5.02.01   | PASS        |
| 52      | Factor in price drops for reminder timing         | US 5.02    | TC-5.02.02   | Fail        |
| 53      | Customize supply reminder timing                  | US 5.02    | TC-5.02.03   | Fail        |
| 54      | Create user-made reminders                        | US 5.03    | TC-5.03.01   | PASS        |
| 55      | Schedule reminders with frequency options         | US 5.03    | TC-5.03.02   | PASS        |
| 56      | Share reminders with other users                  | US 5.03    | TC-5.03.03   | Fail        |
| 57      | Add multiple emails for reminders                 | US 5.03    | TC-5.03.04   | PASS        |
| 58      | Share baby profiles with other users              | US 5.03    | TC-5.03.05   | Fail        |
| 59      | Enable/disable notification styles                | US 5.04    | TC-5.04.01   | Pass        |
| 60      | Set notification frequencies                      | US 5.04    | TC-5.04.02   | Pass        |
| 61      | Create and download calendar events               | US 5.04    | TC-5.04.03   | Fail        |
| 62      | Earn points for defined actions                   | US 6.01    | TC-6.01.01   | Fail        |
| 63      | Display clear point values for activities         | US 6.01    | TC-6.01.02   | Fail        |
| 64      | View total points in user profile                 | US 6.01    | TC-6.01.03   | Fail        |
| 65      | View point history to track progress              | US 6.01    | TC-6.01.04   | Fail        |
| 66      | Redeem points for digital rewards                 | US 6.02    | TC-6.02.01   | Fail        |
| 67      | Confirm and track redeemed rewards                | US 6.02    | TC-6.02.02   | Fail        |
| 68      | Update point total after redemption               | US 6.02    | TC-6.02.03   | Fail        |
| 69      | Display privacy policy and terms of service       | US 7.01    | TC-7.01.01   | Fail        |
| 70      | View and export personal data                     | US 7.01    | TC-7.01.02   | Pass        |
| 71      | Delete account and all associated data            | US 7.01    | TC-7.01.03   | Pass        |
| 72      | Deactivate and reactivate account                 | US 7.01    | TC-7.01.04   | Fail        |
| 73      | Adjust privacy controls for data sharing          | US 7.01    | TC-7.01.05   | Fail        |
| 74      | Encrypt stored and transmitted data               | US 7.02    | TC-7.02.01   | Pass        |
| 75      | Secure authentication and session management      | US 7.02    | TC-7.02.02   | Pass        |

---    