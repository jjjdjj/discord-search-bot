package databasing.Database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

public class ConnectionHelper {
    private MongoClient _mongoClient;

    public ConnectionHelper(String database, String username, String password){
        String uri = String.format("mongodb+srv://%s:%s@%s?retryWrites=true&w=majority",
                                        username, password, database);
        MongoClientURI mongoURI = new MongoClientURI(uri);
        _mongoClient = new MongoClient(mongoURI);
    }

    private MongoDatabase GetDatabase(String database){
        return _mongoClient.getDatabase(database);
    }

    public void InsertMessage(String user, String channel, String message, String timestamp){
        MongoDatabase db = GetDatabase("data1");
        Document dBMessage = new Document("_id", new ObjectId());
        dBMessage.append("User", user);
        dBMessage.append("Channel", channel);
        dBMessage.append("MessageText", message);
        dBMessage.append("Timestamp", timestamp);

        MongoCollection<Document> collection = db.getCollection("messages");
        collection.insertOne(dBMessage);
    }
}
