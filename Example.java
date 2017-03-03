import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
public class Example {
 public static void main(String[] args) {

// Ports in the code according to cluster_run. Please remove the ports
// bootstrapHttpDirectPort, .bootstrapCarrierSslPort, .bootstrapHttpSslPort,
// .bootstrapCarrierDirectPort while using with couchbase package.
CouchbaseEnvironment env = DefaultCouchbaseEnvironment
        .builder()
        .sslEnabled(true)
        .sslKeystoreFile("keystore_new.jks")
        .sslKeystorePassword("123456")
        .computationPoolSize(5)
        .bootstrapCarrierSslPort(11996)
        .bootstrapHttpDirectPort(9000)
        .bootstrapHttpSslPort(19000)
        .bootstrapCarrierDirectPort(12000)
        .build();

String[] nodes = {"localhost"};
CouchbaseCluster cluster = CouchbaseCluster.create(env, nodes);

Bucket bucket = cluster.openBucket("default");
// Create a JSON document and store it with the ID "helloworld"
JsonObject content = JsonObject.create().put("hello", "world");
bucket.upsert(JsonDocument.create("java_client",content));

// Close all buckets and disconnect
cluster.disconnect();
 }
}
