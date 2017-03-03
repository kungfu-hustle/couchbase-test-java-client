# Client cert authentication demo code with java client

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

1. Java applications use [keystore](https://en.wikipedia.org/wiki/Keystore) to store the certificate informations.
2. Couchbase java client uses the java keystore for the same purpose.

#### Creating keystore

Java keystore can be create by using keytool utility.Folowing command creates a new key store with name keystore_new.jks:

keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore_new.jks
-storepass 123456 -validity 360 -keysize 2048

#### Generating Certificate Signing Request(CSR) 

CSR needs to be generated to get it signed from the Certificate Authority(CA).

keytool -certreq -alias selfsigned -keyalg RSA -file my.csr -keystore
keystore_new.jks

This will create a CSR named my.csr

#### Signing the Certificate from CSR

CA uses the CSR to sign the certificate using its certificate and key. 
We have int.pem which is intermediate CA's certificate and int.key which is
intermediate CA's private key. 

openssl x509 -req -in my.csr -CA int.pem -CAkey
int.key -CAcreateserial -out clientcert.pem -days 365

#### Importing CA certificates
  1. Importing root CA
  keytool -import -trustcacerts -file ca.pem -alias root
-keystore keystore_new.jks

  2. Importing intermediate CA
  keytool -import -trustcacerts -file int.pem -alias int
-keystore keystore_new.jks

#### Importing client certificate

keytool -import -keystore keystore_new.jks -file clientcert.pem -alias
selfsigned
  
## Running the Couchbase java client test code

1. Couchbase client code is included in Example.java
2. In that code, we use the newly created keystore (keystore_new.jks) while
   creating CouchbaseEnvironment
3. That CouchbaseEnvironment is passed to CouchbaseCluster class for creating
   a new cluster
4. Run the example code, after replacing it with correct server ip
