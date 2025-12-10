package financeapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class TransactionManager {

    private final MongoCollection<Document> collection;

    public TransactionManager() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("transactions");
    }

    public void addTransaction(Transaction t) {
        collection.insertOne(t.toDocument());
    }

    public void updateTransaction(Transaction t) {
        collection.replaceOne(eq("_id", t.getId()), t.toDocument());
    }

    public void deleteTransaction(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();

        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {

            Document d = cursor.next();

            ObjectId id = d.getObjectId("_id");

            Object amountObj = d.get("amount");
            double amount = 0;

            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            }

            String type = d.getString("type") != null ? d.getString("type") : "Nepoznato";
            String desc = d.getString("description") != null ? d.getString("description") : "";
            String category = d.getString("category") != null ? d.getString("category") : "ostalo";

            list.add(new Transaction(id, type, amount, desc, category));
        }

        return list;
    }

    public double getTotalIncome() {
        double total = 0;
        for (Transaction t : getAllTransactions()) {
            if (t.getType().equals("Prihod")) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getTotalExpense() {
        double total = 0;
        for (Transaction t : getAllTransactions()) {
            if (t.getType().equals("Rashod")) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getCategoryTotal(String category) {
        double total = 0;

        for (Transaction t : getAllTransactions()) {
            if (t.getType().equals("Rashod") && t.getCategory().equalsIgnoreCase(category)) {
                total += t.getAmount();
            }
        }

        return total;
    }

}
