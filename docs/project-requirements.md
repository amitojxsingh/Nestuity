# Project Requirements

---
## Executive Summary
Nestuity supports parents with newborn(s) with managing daily essentials necessary to raise their newborn child.
Essentially, the app will reduce the need of using multiple apps, reduce financial strain, and provide mental 
relief through smart pricing insights tracking costs of essentials, a usage calculator forecasting diaper usage
and intelligent reminders for supplies and milestones.
---
## Project Glossary
- **Developmental milestone**: Certain behaviours or skills that babies should exhibit at a particular age.
- **Store(s)**: Different companies that parents can choose to purchase essentials from.
- **Baby Profile**: A record for each child, including name, birthdate, sex, and growth metrics used for calculations and reminders.
- **Supply Tracker**: Section of the app where parents log quantities of baby essentials (diapers, wipes, formula, clothes, toys, etc.).
- **Usage Calculator**: Tool estimating how much of each essential the baby will consume over time based on age, weight, and other data.
- **Supply Prediction**: Forecast of when the user will run low on a product, based on logged usage and standard consumption patterns.
- **Milestone Reminder**: Automatic alert for child-development events such as vaccinations, growth milestones, and checkups.
- **Price Trends Chart**: Graph showing historical prices of baby essentials across retailers to guide purchasing decisions.
- **Price Comparison Grid**: Side-by-side view of multiple retailers’ prices, shipping costs, and ratings for a product.
- **Parent Profile**: Section containing parent’s name, contact details, notification preferences, and points total.
- **Supply Reminder**: Notification triggered when predicted stock levels reach a threshold to prompt timely reordering.

---
## User Stories
### US 1.0X - User Account Management
>
> *1.01: As a new parent, I want to create an account, so that I can access baby product management features.*
>

- Story Points: 2
- Acceptance Tests:
    - Test registration with valid email and password
    - Test entering basic information about self and baby
    - Test receiving a confirmation email upon registration
    - Test being put on starting flow to set up a baby after registration

> 
> *1.02: As a returning parent, I want to login/logout, so that I can access personal data & preferences.*
>

- Story Points: 2
- Acceptance Tests:
    - Test log in with email and password, and logout
    - Test reset password
    - Test stay logged in across sessions
    - Test being directed to dashboard upon login
    - Test authentication is secure

>
> *1.03: As a returning parent, I want to update my profile & baby info, so that I keep data current & get good recommendations.*
>

- Story Points: 2
- Acceptance Tests:
    - Test viewing and updating personal information (name, email, password, etc.).
    - Test viewing and updating baby information (name, birthdate, weight, sex, etc.).
    - Test that user changes are saved and reflected immediately in the app.

>
> *1.04:  As a parent with multiple babies, I want to add and manage several babies in my account, so that I can track info and supplies for each child separately.*
>

- Story Points: 3
- Acceptance Tests:
    - Test adding multiple baby profiles under a single user account
    - Test editing and updating each baby’s individual profile information
    - Test switching between baby profiles and reflecting changes accurately

### US 2.0X - User Interface

>
> *2.01: As a high-tech parent, I want to use this app to work on different devices, so that I can use it whenever & wherever.*
>

- Story Points: 5
- Acceptance Tests:
    - Test app responsiveness across various screen sizes (mobile, tablet, desktop).
    - Test touch-friendliness on mobile devices.
    - Test functionality and usability across different devices and browsers.

>
> *2.02: As a technologically inept parent, I want to use an intuitive app, so that I get information without wasting time.*
>

- Story Points: 3
- Acceptance Tests:
    - Test that the app has a clear and simple navigation structure.
    - Test that key features and information are easily accessible from the main dashboard.
    - Test search functionality via the search bar to quickly find information.

### US 3.0X - Usage Calculators & Supply Predictions

>
> *3.01: As a new parent, I want to calculate & predict how much product my baby needs, so that I can plan purchases & avoid over/underbuying.*
>

- Story Points: 8
- Acceptance Tests:
    - Test input of baby details (age, weight, diaper size, etc.).
    - Test that product usage estimates are generated based on input data and standard patterns.
    - Test that daily, weekly, and monthly projections for product needs are generated.
    - Test that estimates adjust based on personal usage patterns.

>
> *3.02: As an organized parent, I want to track & manage my supply inventory, so that I know what I have, what I still need, & get more accurate supply predictions.*
>

- Story Points: 5
- Acceptance Tests:
    - Test logging of product usage and inventory levels.
    - Test updating inventory levels when new purchases are added.
    - Test that inventory levels update in real time after changes.
    - Test that visuals of usage trends and inventory status are displayed.
    - Test exporting of inventory data for personal records.

>  
> *3.03: As an organized parent, I want to track different products, so that I am informed about all baby essentials.*
>

- Story Points: 3
- Acceptance Tests:
    - Test adding multiple product types (diapers, wipes, formula, food, clothes, toys, etc.).

### US 4.0X - Smart Price Analysis & Price Predictions

>
> *4.01: As a cheap parent, I want to see price trends for baby essentials from multiple retailers, so that I can make informed buying choices.*
> 

- Story Points: 5
- Acceptance Tests:
    - Test visuals of price trends over time for various products.
    - Test viewing major retailers’ prices for comparison.
    - Test filtering and sorting by product type, retailer, and price range.
    - Test that prices update regularly.

>
> *4.02: As a busy parent, I want to get recommendations for when to buy, so that I can save time from researching prices.*
>

- Story Points: 8
- Acceptance Tests:
    - Test app analysis of price trends to predict best times to buy.
    - Test notifications for current deals and discounts on tracked products.
    - Test notifications for predicted price drops and sales events.

>
> *4.03: As a cheap parent, I want to see prices side-by-side, so that I find the best deals quickly.*
>

- Story Points: 5
- Acceptance Tests:
    - Test visual comparison of two or more products from different retailers.
    - Test viewing of key details (price, shipping, ratings, etc.) in the comparison.
    - Test selecting products to compare easily.
    - Test links to purchase directly from the comparison screen.
    - Test inclusion of shipping and delivery costs in total price for accurate comparison.

>
> *4.04: As a cheap parent, I want to see today’s cheapest prices, so that I can buy the best deal today.*
>

- Story Points: 1
- Acceptance Tests:
    - Test viewing a list of products with the lowest prices available today.

### US 5.0X - Intelligence Reminders & Notifications

>
> *5.01: As a busy parent, I want to get reminders for milestones, so that I am informed about my child’s stages.*
> 

- Story Points: 3
- Acceptance Tests:
    - Test notifications for vaccinations, growth spurts, and developmental milestones.
    - Test customizing timing and frequency of reminders (hours/days/weeks in advance).
    - Test access to additional information and resources related to each milestone.
    - Test ability to mark reminders as completed.

>
> *5.02: As a busy parent, I want to get reminders for my supply, so that I do not run out of stock.*
>

- Story Points: 8
- Acceptance Tests:
    - Test reminders based on supply predictions and inventory levels.
    - Test factoring in price drop predictions and current deals for optimal reminder timing.
    - Test customizing timing and frequency of supply reminders (hours/days/weeks in advance).

>
> *5.03: As a busy parent, I want to get reminders for my tasks, so that the tasks get completed.*
>

- Story Points: 5
- Acceptance Tests:
    - Test creating user-made reminders (feeding, medication, activities).
    - Test scheduling reminders with frequency options (daily, weekly, monthly).
    - Test sharing reminders with other users (other parent, nanny).
    - Test adding multiple emails for one account to receive reminders.
    - Test sharing baby profiles with other users (similar to Google Docs sharing).

>
> *5.04: As a busy parent, I want to customize my notification style, so that it suits my needs and lifestyle.*
>

- Story Points: 3
- Acceptance Tests:
    - Test enabling and disabling multiple notification styles (email, text message, push notification, etc.).
    - Test setting different notification frequencies (immediate notifications, daily summary, weekly summary).
    - Test creating calendar events (.ics file download) and uploading to an external calendar.

### US 6.0X - Gamification

>
> *6.01: As a parent, I want to earn points using the app, so that I am motivated to stay consistent with the app.*
> 

- Story Points: 8
- Acceptance Tests:
    - Test earning points for defined actions (updating inventory, completing milestones, etc.).
    - Test displaying clear point values for each activity.
    - Test viewing total points in the user profile.
    - Test viewing point history to track progress over time.

>
> *6.02: As a parent, I want to redeem my points for rewards, so that I have a tangible benefit from using the app.*
>

- Story Points: 3
- Acceptance Tests:
    - Test redeeming points for digital rewards (e.g. coupons).
    - Test confirmation and tracking of redeemed rewards.
    - Test updating point total after redemption.

### US 7.0X - Data Privacy & Security

>
> *7.01: As a private parent, I want to control how my data is used & shared, so that I can keep my data private.*
> 

- Story Points: 3
- Acceptance Tests:
    - Test displaying clear privacy policy and terms of service.
    - Test viewing and exporting personal data.
    - Test deleting an account and all associated data.
    - Test deactivating and reactivating an account (if implemented).
    - Test adjusting detailed privacy controls for data sharing (if implemented).
  
>
> *7.02: As a parent, I want to have securely stored data, so that I trust the app with sensitive information.*
>

- Story Points: 3
- Acceptance Tests:
    - Test encryption of stored and transmitted data.
    - Test secure authentication and session management.

---
## MoSCoW
- Must Have
    - US 1.02 - Login/logout
    - US 3.01 - Essentials stock calculation
    - US 3.02 - Track and manage essentials supply

- Should Have
    - US 1.01 - Account creation for new parents
    - US 2.02 - Intuitive design
    - US 4.01 - Price trends from multiple retailers
    - US 4.03 - Side-by-side price comparison
    - US 5.02 – Low stock notifications
    - US 5.05 - Add and check off tasks
    - US 7.01 - User data/privacy controls
    - US 7.02 - Securely store user data

- Could Have (nice-to-have)
    - US 1.03 - Update profile and baby info
    - US 1.04 - Add and manage multiple babies   
    - US 2.01 - Cross-device support
    - US 3.03 - Track the price of different product types (eg. diapers, wipes, and formula)
    - US 4.04 - View the day's cheapest prices
    - US 5.01 - Developmental milestone notifications
    - US 5.03 - Task notifications
    - US 5.04 - Notification settings 

- Would Like Won’t Have
    - US 4.02 - Future price predictions
    - US 6.01 - Earning points
    - US 6.02 - Point redemption
  
---
## Similar Products
- [CamelCamelCamel](https://3cmls.co/CA/B0C9JXHCBN)
    - Amazon price tracker
    - Graphs price history of certain products

---
## Open-Source Products
- [Baby Buddy](https://github.com/babybuddy/babybuddy)
    - Also focused on baby’s routine tracking
    - Ext. required

- [LunaTracker](https://f-droid.org/en/packages/it.danieleverducci.lunatracker/)
    - Tracks usage
    - Android app, WebDAV for privacy and syncing
  
---
## Technical Resources
### Backend
- [Spring Boot](https://spring.io/projects/spring-boot) – Framework for building Java-based backend apps.
- [PostgreSQL](https://www.postgresql.org/) – Database management system.
- [Gradle](https://gradle.org/) – Build and dependency tools for Java apps.
### Frontend
- [Next.js](https://nextjs.org/) – React framework for server-side rendering and static site generation.
- [Tailwind CSS](https://tailwindcss.com/) – CSS framework for styling.
### DevOps & Deployment
- [Docker](https://www.docker.com/) – Containerization for applications.
- [GitHub Actions](https://docs.github.com/en/actions) - CI/CD workflows.
- [Nginx](https://www.nginx.com/) – Reverse proxy and web server.