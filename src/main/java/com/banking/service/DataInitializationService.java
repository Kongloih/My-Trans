package com.banking.service;

import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import com.banking.repository.TransactionJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stock trading data initialization service
 * Used to generate test data and mock data
 * 
 * @author Kongloih Zhang F
 */
@Service
@Profile("!test") // Only run in non-test environment
public class DataInitializationService implements CommandLineRunner {
    
    private final TransactionJpaRepository transactionRepository;
    private final Random random = new Random();
    
    // Test account numbers
    private final String[] TEST_ACCOUNTS = {
        "1234567890123456", "2345678901234567", "3456789012345678", 
        "4567890123456789", "5678901234567890", "6789012345678901", 
        "7890123456789012", "8901234567890123", "9012345678901234", 
        "0123456789012345"
    };
    
    // China stock codes
    private final String[] SECURITY_CODES = {
        "000001", "000002", "000858", "000725", "002415",  // Shenzhen stocks
        "600036", "600519", "600000", "601318", "601939",  // Shanghai stocks
        "300015", "300059", "300750", "300888", "300122"   // ChiNext stocks
    };
    
    // Stock names mapping
    private final String[] STOCK_NAMES = {
        "Ping An Bank", "Vanke A", "Wuliangye", "East Money", "Hikvision",
        "China Merchants Bank", "Kweichow Moutai", "SPD Bank", "Ping An Insurance", "CCB",
        "East Money Info", "East Money Tech", "CATL", "Kingsoft Office", "Sanhuan Group"
    };
    
    // Buy transaction description templates
    private final String[] BUY_DESCRIPTIONS = {
        "Buy {} Stock", "Increase Holdings {}", "{} Stock Position Building", "Regular Investment {}",
        "Bottom Fishing {}", "{} Position Adding", "Optimistic About {} Prospects", "{} Value Investment"
    };
    
    // Sell transaction description templates
    private final String[] SELL_DESCRIPTIONS = {
        "Sell {} Stock", "Reduce Holdings {}", "{} Stock Profit Taking", "{} Profit Realization",
        "{} Stop Loss", "{} Position Adjustment", "{} Secure Profits", "{} Stock Cash Out"
    };
    
    @Autowired
    public DataInitializationService(TransactionJpaRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (transactionRepository.count() > 0) {
            System.out.println("Data already exists in database, skipping initialization");
            return;
        }
        
        System.out.println("Starting stock trading test data initialization...");
        
        // Generate test data
        List<Transaction> testTransactions = generateTestData();
        
        // Save to database
        transactionRepository.saveAll(testTransactions);
        
        System.out.println("Stock trading test data initialization completed! Generated " + testTransactions.size() + " transaction records");
    }
    
    /**
     * Generate test data
     */
    private List<Transaction> generateTestData() {
        List<Transaction> transactions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Generate transaction history for each account
        for (String accountNumber : TEST_ACCOUNTS) {
            int transactionCount = 20 + random.nextInt(21); // 20-40 transactions per account
            
            for (int i = 0; i < transactionCount; i++) {
                // Generate random date within past 30 days
                LocalDate transDate = today.minusDays(random.nextInt(30));
                Transaction transaction = createRandomStockTransaction(accountNumber, transDate);
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }
    
    /**
     * Create random stock transaction
     */
    private Transaction createRandomStockTransaction(String accountNumber, LocalDate transDate) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(accountNumber);
        
        // Randomly select stock
        int stockIndex = random.nextInt(SECURITY_CODES.length);
        String securityCode = SECURITY_CODES[stockIndex];
        String stockName = STOCK_NAMES[stockIndex];
        transaction.setSecurityCode(securityCode);
        
        // Randomly select transaction type
        TransactionType transType = random.nextBoolean() ? TransactionType.BUY : TransactionType.SELL;
        transaction.setTransType(transType);
        
        // Generate random units (multiples of 100, following A-share trading rules)
        long unit = (1 + random.nextInt(50)) * 100; // 100-5000 shares
        transaction.setUnit(unit);
        
        // Generate random price (5-200 CNY)
        BigDecimal price = BigDecimal.valueOf(5 + random.nextDouble() * 195)
                .setScale(2, RoundingMode.HALF_UP);
        transaction.setPrice(price);
        
        // Calculate total amount = units * price
        BigDecimal amount = price.multiply(BigDecimal.valueOf(unit));
        transaction.setAmount(amount);
        
        // Set transaction date
        transaction.setTransDate(transDate);
        
        // Set description
        String[] descriptions = transType == TransactionType.BUY ? BUY_DESCRIPTIONS : SELL_DESCRIPTIONS;
        String descriptionTemplate = descriptions[random.nextInt(descriptions.length)];
        String description = descriptionTemplate.replace("{}", stockName);
        transaction.setDescription(description);
        
        // Set timestamp (9:30-15:00 on trading day)
        LocalDateTime timestamp = transDate.atTime(9 + random.nextInt(6), 30 + random.nextInt(30));
        transaction.setTimestamp(timestamp);
        
        transaction.setCurrency("CNY");
        
        return transaction;
    }
} 