-- Stock Trading Management System Initial Data
-- Author: Kongloih

-- Simple test data with minimal records
INSERT INTO transactions (account_number, amount, trans_type, unit, price, trans_date, security_code, description, timestamp, currency, balance) VALUES
('1234567890123456', 10000.00, 'BUY', 1000, 10.00, '2024-01-15', '000001', 'Buy Stock', '2024-01-15 09:30:00', 'CNY', 0);
