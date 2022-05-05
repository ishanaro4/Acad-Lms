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
import com.mongodb.starter.models.PlacementMaterial;
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
public class MongoDBPlacementMaterial implements PlacementMatRepository {

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<PlacementMaterial> placementMaterialMongoCollection;

    public MongoDBPlacementMaterial(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        placementMaterialMongoCollection = client.getDatabase("acad-lms").getCollection("placementMaterial", PlacementMaterial.class);
    }

    @Override
    public PlacementMaterial save(PlacementMaterial mat) {
        mat.setId(new ObjectId());
        placementMaterialMongoCollection.insertOne(mat);
        return mat;
    }

    @Override
    public List<PlacementMaterial> saveAll(List<PlacementMaterial> persons) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(() -> {
                persons.forEach(p -> p.setId(new ObjectId()));
                placementMaterialMongoCollection.insertMany(clientSession, persons);
                return persons;
            }, txnOptions);
        }
    }

    @Override
    public List<PlacementMaterial> findAll() {
        return placementMaterialMongoCollection.find().into(new ArrayList<>());
    }

    @Override
    public List<PlacementMaterial> findAll(List<String> ids) {
        return placementMaterialMongoCollection.find(in("_id", mapToObjectIds(ids))).into(new ArrayList<>());
    }

    @Override
    public PlacementMaterial findOne(String username) {
        return placementMaterialMongoCollection.find(eq("username", username)).first();
    }

    @Override
    public long count() {
        return placementMaterialMongoCollection.countDocuments();
    }

    @Override
    public long delete(String id) {
        return placementMaterialMongoCollection.deleteOne(eq("_id", new ObjectId(id))).getDeletedCount();
    }

    @Override
    public long delete(List<String> ids) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> placementMaterialMongoCollection.deleteMany(clientSession, in("_id", mapToObjectIds(ids))).getDeletedCount(),
                    txnOptions);
        }
    }

    @Override
    public long deleteAll() {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> placementMaterialMongoCollection.deleteMany(clientSession, new BsonDocument()).getDeletedCount(), txnOptions);
        }
    }

    /*@Override
    public PlacementMaterial update(PlacementMaterial exp) {
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(AFTER);
        return placementMaterialMongoCollection.findOneAndReplace(eq("username", exp.getUsername()), exp, options);
    }*/

    @Override
    public long update(List<PlacementMaterial> persons) {
        List<WriteModel<PlacementMaterial>> writes = persons.stream()
                .map(p -> new ReplaceOneModel<>(eq("_id", p.getId()), p))
                .collect(Collectors.toList());
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> placementMaterialMongoCollection.bulkWrite(clientSession, writes).getModifiedCount(), txnOptions);
        }
    }

    @Override
    public double getAverageAge() {
        List<Bson> pipeline = asList(group(new BsonNull(), avg("averageAge", "$age")), project(excludeId()));
        return placementMaterialMongoCollection.aggregate(pipeline, AverageAgeDTO.class).first().getAverageAge();
    }

    @Override
    public PlacementMaterial findByUsername(String username){
        return  placementMaterialMongoCollection.find(eq("username", username)).first();
    }
    @Override
    public Boolean existsByUsername(String username){
        PlacementMaterial user = placementMaterialMongoCollection.find(eq("username", username)).first();
        if(user ==null)
            return false;
        else
            return true;
    }

    private List<ObjectId> mapToObjectIds(List<String> ids) {
        return ids.stream().map(ObjectId::new).collect(Collectors.toList());
    }
    @Override
    public List<PlacementMaterial> findAllBySubject(String sub){
        return placementMaterialMongoCollection.find(eq("subject", sub)).into(new ArrayList<>());
    }
}

