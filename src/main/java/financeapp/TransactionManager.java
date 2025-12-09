package financeapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class TransactionManager {

    private final MongoCollection<Document> collection;

    public TransactionManager() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("transactions");
    }

    public void addTransaction(Transaction t) {
        collection.insertOne(t.toDocument());
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()) {
            Document d = cursor.next();

            // Sigurno čitanje polja amount
            Object amountObj = d.get("amount");
            double amount = 0;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            }

            // Sigurno čitanje tipa i opisa
            String type = d.getString("type") != null ? d.getString("type") : "Nepoznato";
            String description = d.getString("description") != null ? d.getString("description") : "";

            list.add(new Transaction(type, amount, description));
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
}
