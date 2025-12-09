package financeapp;

import org.bson.Document;

public class Transaction {

    private String type;
    private double amount;
    private String description;

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public Document toDocument() {
        return new Document("type", type)
                .append("amount", amount)
                .append("description", description);
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
}
