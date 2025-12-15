package com.nestuity.service.seeder;

import com.nestuity.service.dto.BabyProductResponse;
import com.nestuity.service.dto.CreateBabyProductRequest;
import com.nestuity.service.dto.PriceHistoryDTO;
import com.nestuity.service.service.BabyProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with sample BabyProduct data.
 * Only runs when 'dev' profile is active.
 * Checks if data already exists to avoid duplicates.
 */
@Component
@Profile("dev")
public class BabyProductSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(BabyProductSeeder.class);
    private final BabyProductService babyProductService;

    public BabyProductSeeder(BabyProductService babyProductService) {
        this.babyProductService = babyProductService;
    }

    @Override
    public void run(String... args) {
        // Only seed if database is empty
        if (babyProductService.findAll().isEmpty()) {
            log.info("🌱 Database is empty. Seeding baby product data...");
            seedData();
            long productCount = babyProductService.findAll().size();
            log.info("✅ Seeding complete! {} products created.", productCount);
        } else {
            log.info("📦 Data already exists. Skipping seeding.");
        }
    }

    private void seedData() {
        // Seed various baby products
        createBabyFormula();
        createDiapers();
        createBabyWipes();
        createBottles();
        createPacifiers();
        createBabyFood();
        createStroller();
        createCarSeat();
    }

    private void createBabyFormula() {
        CreateBabyProductRequest formula = new CreateBabyProductRequest();
        formula.setName("Organic Baby Formula - Infant");
        formula.setBrand("Similac");
        formula.setCategory("Feeding");
        formula.setDescription("Organic baby formula for newborns and infants up to 12 months. Non-GMO, easy to digest.");
        formula.setCurrency("CAD");
        formula.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        // Amazon prices over time
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/similac-organic-123", 
            new BigDecimal("32.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/similac-organic-123", 
            new BigDecimal("30.50"), LocalDateTime.now().minusDays(45)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/similac-organic-123", 
            new BigDecimal("29.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/similac-organic-123", 
            new BigDecimal("31.50"), LocalDateTime.now().minusDays(15)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/similac-organic-123", 
            new BigDecimal("29.99"), LocalDateTime.now().minusDays(1)));
        
        // Walmart prices
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/similac-organic-456", 
            new BigDecimal("31.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/similac-organic-456", 
            new BigDecimal("28.99"), LocalDateTime.now().minusDays(45)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/similac-organic-456", 
            new BigDecimal("27.50"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/similac-organic-456", 
            new BigDecimal("26.99"), LocalDateTime.now().minusDays(1)));
        
        // Costco prices (usually best deal but less frequent updates)
        priceHistory.add(createPriceEntry("Costco", "https://costco.ca/similac-organic-789", 
            new BigDecimal("25.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Costco", "https://costco.ca/similac-organic-789", 
            new BigDecimal("24.99"), LocalDateTime.now().minusDays(1)));

        formula.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(formula);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createDiapers() {
        CreateBabyProductRequest diapers = new CreateBabyProductRequest();
        diapers.setName("Pampers Pure Diapers Size 4");
        diapers.setBrand("Pampers");
        diapers.setCategory("Diapering");
        diapers.setDescription("Hypoallergenic diapers made with premium cotton and plant-based materials. Size 4 (22-37 lbs), 58 count.");
        diapers.setCurrency("CAD");
        diapers.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        // Amazon prices
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/pampers-pure-size4", 
            new BigDecimal("42.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/pampers-pure-size4", 
            new BigDecimal("39.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/pampers-pure-size4", 
            new BigDecimal("44.50"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/pampers-pure-size4", 
            new BigDecimal("41.99"), LocalDateTime.now().minusDays(7)));
        
        // Walmart prices
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/pampers-pure-size4", 
            new BigDecimal("40.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/pampers-pure-size4", 
            new BigDecimal("38.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/pampers-pure-size4", 
            new BigDecimal("37.50"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/pampers-pure-size4", 
            new BigDecimal("39.99"), LocalDateTime.now().minusDays(7)));

        diapers.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(diapers);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createBabyWipes() {
        CreateBabyProductRequest wipes = new CreateBabyProductRequest();
        wipes.setName("Water Wipes Original Baby Wipes");
        wipes.setBrand("WaterWipes");
        wipes.setCategory("Diapering");
        wipes.setDescription("99.9% water baby wipes, gentle for sensitive skin. 12 packs of 60 wipes (720 total).");
        wipes.setCurrency("CAD");
        wipes.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/waterwipes-original", 
            new BigDecimal("54.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/waterwipes-original", 
            new BigDecimal("49.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/waterwipes-original", 
            new BigDecimal("52.50"), LocalDateTime.now().minusDays(7)));
        
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/waterwipes-original", 
            new BigDecimal("51.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/waterwipes-original", 
            new BigDecimal("48.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/waterwipes-original", 
            new BigDecimal("47.50"), LocalDateTime.now().minusDays(7)));

        wipes.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(wipes);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createBottles() {
        CreateBabyProductRequest bottles = new CreateBabyProductRequest();
        bottles.setName("Dr. Brown's Natural Flow Baby Bottles (8oz, 4-Pack)");
        bottles.setBrand("Dr. Brown's");
        bottles.setCategory("Feeding");
        bottles.setDescription("Anti-colic baby bottles with internal vent system. BPA-free, 8oz capacity, includes level 1 nipples.");
        bottles.setCurrency("CAD");
        bottles.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/drbrowns-bottles-8oz", 
            new BigDecimal("29.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/drbrowns-bottles-8oz", 
            new BigDecimal("24.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/drbrowns-bottles-8oz", 
            new BigDecimal("27.50"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/drbrowns-bottles-8oz", 
            new BigDecimal("25.99"), LocalDateTime.now().minusDays(7)));
        
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/drbrowns-bottles-8oz", 
            new BigDecimal("28.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/drbrowns-bottles-8oz", 
            new BigDecimal("26.50"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/drbrowns-bottles-8oz", 
            new BigDecimal("27.99"), LocalDateTime.now().minusDays(7)));

        bottles.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(bottles);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createPacifiers() {
        CreateBabyProductRequest pacifiers = new CreateBabyProductRequest();
        pacifiers.setName("MAM Perfect Pacifier (0-6 months, 2-Pack)");
        pacifiers.setBrand("MAM");
        pacifiers.setCategory("Soothing");
        pacifiers.setDescription("Orthodontic pacifiers for newborns. BPA-free, self-sterilizing case included.");
        pacifiers.setCurrency("CAD");
        pacifiers.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/mam-pacifier-0-6", 
            new BigDecimal("12.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/mam-pacifier-0-6", 
            new BigDecimal("10.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/mam-pacifier-0-6", 
            new BigDecimal("11.50"), LocalDateTime.now().minusDays(7)));
        
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/mam-pacifier-0-6", 
            new BigDecimal("11.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/mam-pacifier-0-6", 
            new BigDecimal("10.50"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/mam-pacifier-0-6", 
            new BigDecimal("9.99"), LocalDateTime.now().minusDays(7)));

        pacifiers.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(pacifiers);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createBabyFood() {
        CreateBabyProductRequest food = new CreateBabyProductRequest();
        food.setName("Gerber Organic Baby Food Pouches (Variety Pack, 18 Count)");
        food.setBrand("Gerber");
        food.setCategory("Feeding");
        food.setDescription("Organic fruit and vegetable pouches for babies 6+ months. No artificial flavors or colors.");
        food.setCurrency("CAD");
        food.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/gerber-organic-pouches", 
            new BigDecimal("22.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/gerber-organic-pouches", 
            new BigDecimal("19.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/gerber-organic-pouches", 
            new BigDecimal("21.50"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/gerber-organic-pouches", 
            new BigDecimal("20.99"), LocalDateTime.now().minusDays(7)));
        
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/gerber-organic-pouches", 
            new BigDecimal("21.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/gerber-organic-pouches", 
            new BigDecimal("18.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/gerber-organic-pouches", 
            new BigDecimal("19.50"), LocalDateTime.now().minusDays(7)));

        priceHistory.add(createPriceEntry("Costco", "https://costco.ca/gerber-organic-pouches", 
            new BigDecimal("17.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Costco", "https://costco.ca/gerber-organic-pouches", 
            new BigDecimal("16.99"), LocalDateTime.now().minusDays(7)));

        food.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(food);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createStroller() {
        CreateBabyProductRequest stroller = new CreateBabyProductRequest();
        stroller.setName("Baby Jogger City Mini GT2 Stroller");
        stroller.setBrand("Baby Jogger");
        stroller.setCategory("Gear");
        stroller.setDescription("All-terrain stroller with quick-fold technology. One-hand fold, adjustable handlebar, large canopy.");
        stroller.setCurrency("CAD");
        stroller.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/babyjogger-citymini-gt2", 
            new BigDecimal("549.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/babyjogger-citymini-gt2", 
            new BigDecimal("499.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/babyjogger-citymini-gt2", 
            new BigDecimal("524.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/babyjogger-citymini-gt2", 
            new BigDecimal("519.99"), LocalDateTime.now().minusDays(7)));
        
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/babyjogger-citymini-gt2", 
            new BigDecimal("539.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/babyjogger-citymini-gt2", 
            new BigDecimal("509.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/babyjogger-citymini-gt2", 
            new BigDecimal("529.99"), LocalDateTime.now().minusDays(7)));

        stroller.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(stroller);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    private void createCarSeat() {
        CreateBabyProductRequest carSeat = new CreateBabyProductRequest();
        carSeat.setName("Graco 4Ever DLX 4-in-1 Car Seat");
        carSeat.setBrand("Graco");
        carSeat.setCategory("Safety");
        carSeat.setDescription("Convertible car seat grows with child from infant to booster. 10 years of use, Simply Safe Adjust Harness.");
        carSeat.setCurrency("CAD");
        carSeat.setInStock(true);

        List<PriceHistoryDTO> priceHistory = new ArrayList<>();
        
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/graco-4ever-carseat", 
            new BigDecimal("399.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/graco-4ever-carseat", 
            new BigDecimal("349.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/graco-4ever-carseat", 
            new BigDecimal("369.99"), LocalDateTime.now().minusDays(30)));
        priceHistory.add(createPriceEntry("Amazon", "https://amazon.ca/graco-4ever-carseat", 
            new BigDecimal("359.99"), LocalDateTime.now().minusDays(7)));
        
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/graco-4ever-carseat", 
            new BigDecimal("389.99"), LocalDateTime.now().minusDays(90)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/graco-4ever-carseat", 
            new BigDecimal("339.99"), LocalDateTime.now().minusDays(60)));
        priceHistory.add(createPriceEntry("Walmart", "https://walmart.ca/graco-4ever-carseat", 
            new BigDecimal("349.99"), LocalDateTime.now().minusDays(7)));

        carSeat.setPriceHistory(priceHistory);
        
        BabyProductResponse created = babyProductService.createBabyProduct(carSeat);
        log.info("  ✓ Created: {} (ID: {})", created.getName(), created.getId());
    }

    /**
     * Helper method to create a price history entry
     */
    private PriceHistoryDTO createPriceEntry(String retailer, String url, BigDecimal price, LocalDateTime date) {
        PriceHistoryDTO priceEntry = new PriceHistoryDTO();
        priceEntry.setRetailer(retailer);
        priceEntry.setProductUrl(url);
        priceEntry.setPrice(price);
        priceEntry.setDate(date);
        return priceEntry;
    }
}

