# Client cert authentication with java client

## Setting up the Couchbase Server

### Creating ssl certificates

1. Root certificate authority, intermediate certificate authority can be
   created and by following the [couchbase
documentation](https://developer.couchbase.com/documentation/server/current/security/security-x509certsintro.html)
  1. Files ca.pem,ca.key are root CA generated for testing 
  2. Files int.pem, int.key are intermediate CA generated
2. Create chain.pem and pkey.key as mentioned in the previous link.
  1. chain.pem and pkey.key are included in the repo for testing
     purpose
2. Install all the certificates on the Couchbase node

### Enabling the cliet cert auth

1. Use the couchbase-cli to enable/disable the client cert auth

./couchbase-cli ssl-manage --set-client-auth "mandatory" -c
http://<serverip:port> -u <username> -p <password>

## Setting up java client

### Using java keystore

1. Java applications use [keystore](https://en.wikipedia.org/wiki/Keystore) to store the certificate information.

#### Creating keystore

Java keystore can be created by using keytool utility.Folowing command creates a new key store with name keystore_new.jks:

keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore_new.jks
-storepass 123456 -validity 360 -keysize 2048

Note : this also generates a key-pair that is stored in the newly generated keystore. 

#### Generating Certificate Signing Request(CSR) 

Next generate a CSR which will need to be signed by the root CA

keytool -certreq -alias selfsigned -keyalg RSA -file my.csr -keystore keystore_new.jks

This command creates a CSR named my.csr

#### Signing the Certificate from CSR

CA uses the CSR to sign the certificate using its certificate and key. 
We have int.pem which is intermediate CA's certificate and int.key which is intermediate CA's private key. 

openssl x509 -req -in my.csr -CA int.pem -CAkey int.key -CAcreateserial -out clientcert.pem -days 365

This commands outputs a signed clientcert.pem file which will need to be imported in the keystore along with the root CA and intermediate CA certificates. 

#### Importing CA certificates
  1. Importing root CA
  keytool -import -trustcacerts -file ca.pem -alias root -keystore keystore_new.jks

  2. Importing intermediate CA
  keytool -import -trustcacerts -file int.pem -alias int -keystore keystore_new.jks

#### Importing client certificate

keytool -import -keystore keystore_new.jks -file clientcert.pem -alias selfsigned
  
## Running the Couchbase java client test code

1. Couchbase client code is included in Example.java
2. In the example code, we use the newly created keystore (keystore_new.jks) while
   creating CouchbaseEnvironment
3. That CouchbaseEnvironment is passed to CouchbaseCluster class for creating
   a new cluster
4. Run the example code after replacing it with correct server ip
