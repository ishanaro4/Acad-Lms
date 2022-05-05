package com.mongodb.starter.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.starter.dtos.AverageAgeDTO;
import com.mongodb.starter.models.ApplicationUser;
import com.mongodb.starter.models.Experience;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.ReturnDocument.AFTER;
import static java.util.Arrays.asList;

@Repository
public class MongoDBApplicationUser implements ApplicationUserRepository {

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<ApplicationUser> userCollection;

    public MongoDBApplicationUser(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        userCollection = client.getDatabase("acad-lms").getCollection("users", ApplicationUser.class);
    }

    @Override
    public ApplicationUser save(ApplicationUser person) {
        person.setId(new ObjectId());
        //person.setDescription("");
        //person.setExperience(new Experience("",""));
        userCollection.insertOne(person);
        return person;
    }

    @Override
    public List<ApplicationUser> saveAll(List<ApplicationUser> persons) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(() -> {
                persons.forEach(p -> p.setId(new ObjectId()));
                userCollection.insertMany(clientSession, persons);
                return persons;
            }, txnOptions);
        }
    }

    @Override
    public List<ApplicationUser> findAll() {
        return userCollection.find().into(new ArrayList<>());
    }

    @Override
    public List<ApplicationUser> findAll(List<String> ids) {
        return userCollection.find(in("_id", mapToObjectIds(ids))).into(new ArrayList<>());
    }

    @Override
    public ApplicationUser findOne(String username) {
        return userCollection.find(eq("username", username)).first();
    }

    @Override
    public long count() {
        return userCollection.countDocuments();
    }

    @Override
    public long delete(String id) {
        return userCollection.deleteOne(eq("_id", new ObjectId(id))).getDeletedCount();
    }

    @Override
    public long delete(List<String> ids) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> userCollection.deleteMany(clientSession, in("_id", mapToObjectIds(ids))).getDeletedCount(),
                    txnOptions);
        }
    }

    @Override
    public long deleteAll() {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> userCollection.deleteMany(clientSession, new BsonDocument()).getDeletedCount(), txnOptions);
        }
    }

    @Override
    public ApplicationUser update(ApplicationUser person) {
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(AFTER);
        return userCollection.findOneAndReplace(eq("username", person.getUsername()), person, options);
    }

    @Override
    public long update(List<ApplicationUser> persons) {
        List<WriteModel<ApplicationUser>> writes = persons.stream()
                .map(p -> new ReplaceOneModel<>(eq("_id", p.getId()), p))
                .collect(Collectors.toList());
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> userCollection.bulkWrite(clientSession, writes).getModifiedCount(), txnOptions);
        }
    }

    @Override
    public double getAverageAge() {
        List<Bson> pipeline = asList(group(new BsonNull(), avg("averageAge", "$age")), project(excludeId()));
        return userCollection.aggregate(pipeline, AverageAgeDTO.class).first().getAverageAge();
    }

    @Override
    public ApplicationUser findByUsername(String username){
        return  userCollection.find(eq("username", username)).first();
    }
    @Override
    public Boolean existsByUsername(String username){
        ApplicationUser user = userCollection.find(eq("username", username)).first();
        if(user ==null)
            return false;
        else
            return true;
    }

    private List<ObjectId> mapToObjectIds(List<String> ids) {
        return ids.stream().map(ObjectId::new).collect(Collectors.toList());
    }
}

