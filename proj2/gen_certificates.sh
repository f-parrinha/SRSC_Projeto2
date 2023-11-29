#!/bin/bash

###############################################
#################### INIT #####################
###############################################

echo "##################################"
echo "##                              ##"
echo "##    Certificate Generator     ##"
echo "##                              ##"
echo "##################################"
echo
echo - Welcome
echo - Starting ...
echo
echo - Removing old KeyStores, TrustStores and certificates ...
echo

###############################################
################# VARIABLES ###################
###############################################

dispatcherStoreName=fdispatcher
storageStoreName=fstorage
authStoreName=fauth
accessStoreName=faccess
clientStoreName=client
trustedSecret=password
secret=password
serverResourcesPath=FServer/src/main/resources
clientResourcesPath=FClient/src/main/resources
server=("$dispatcherStoreName" "$storageStoreName" "$authStoreName" "$accessStoreName")




###############################################
############### INITIALIZATION ################
###############################################

# Reset files
rm -f API/src/main/resources/*.jks
rm -f API/src/main/resources/*.crt
echo - Done
echo




###############################################
################# TRUSTSTORE ##################
###############################################

# Creates a client TrustStore containing 'cacerts'
echo
echo "########### TrustStore ###########"
echo
echo - Creating Client KeyStore
echo
keytool -genkeypair -keyalg RSA -keysize 2048 -keystore ./"$clientStoreName"-ks.jks -validity 365 -alias "$clientStoreName" -storepass $trustedSecret -keypass $trustedSecret<< EOF
SR.SC
P2
FCT
LX
LX
PT
yes
EOF
echo
echo - Done
echo
echo
echo - Exporting Certificate
echo
keytool -export -file "$clientStoreName".crt -keystore ./"$clientStoreName"-ks.jks -storepass $trustedSecret -alias "$clientStoreName" << EOF
$secret
EOF
echo
echo - Creating Client TrustStore
echo
keytool -genkeypair -keyalg RSA -keysize 2048 -keystore ./"$clientStoreName"-ts.jks -validity 365 -alias "$clientStoreName" -storepass $trustedSecret -keypass $trustedSecret<< EOF
SR.SC
P2
FCT
LX
LX
PT
yes
EOF




###############################################
################# KEYSTORES ###################
###############################################

# Generates KeyStores and certificates for all servers
# shellcheck disable=SC2068
for str in ${server[@]}
do
echo
echo "########### For '$str' ###########"

# Generates one keystore file for one server
echo
echo - Creating KeyStore
echo
keytool -genkeypair -keyalg RSA -keysize 2048 -keystore ./"$str"-ks.jks -validity 365 -alias "$str" -storepass "$secret" -keypass "$secret" -dname CN=localhost -ext SAN=dns:localhost<< EOF
SR.SC
P2
FCT
LX
LX
PT
yes
EOF

# Exports certificate
echo
echo
echo - Exporting Certificate
echo
keytool -export -file "$str".crt -keystore "$str"-ks.jks -storepass "$secret" -alias "$str" -dname CN=localhost -ext SAN=dns:localhost << EOF
$secret
EOF

# Generates truststore for server
echo
echo - Creating TrustStore
echo
keytool -genkeypair -keyalg RSA -keysize 2048 -keystore ./"$str"-ts.jks -validity 365 -alias "$str" -storepass "$secret" -keypass "$secret"<< EOF
SR.SC
P2
FCT
LX
LX
PT
yes
EOF

# Add Dispatcher certificate on the client's truststore
if [ "$str" == "$dispatcherStoreName" ]; then
  echo
  echo
  echo - Importing "$str" Certificate into "$clientStoreName" Truststore
  echo
  keytool -import -file "$str".crt -keystore "$clientStoreName"-ts.jks -storepass "$trustedSecret" -alias "$str" -dname CN=localhost -ext SAN=dns:localhost << EOF
  yes
EOF
  echo
  echo
  echo - Imported "$str" Certificate to "$clientStoreName" TrustStore
  echo
  echo - Importing Client Certificate into Server TrustStore
  echo
  keytool -import -file "$clientStoreName".crt -keystore "$str"-ts.jks -storepass "$secret" -alias "$clientStoreName" << EOF
  yes
EOF
  echo
  echo - Imported "$clientStoreName" Certificate to "$dispatcherStoreName" TrustStore
  echo

# Add other modules' certificate on the dispatcher's truststore
else
  echo
  echo - Importing "$str" Certificate into "$dispatcherStoreName" TrustStore
  echo
  keytool -import -file "$str".crt -keystore "$dispatcherStoreName"-ts.jks -storepass "$trustedSecret" -alias "$str" -dname CN=localhost -ext SAN=dns:localhost << EOF
  yes
EOF
  echo
  echo - Imported "$str" Certificate to "$dispatcherStoreName" TrustStore
  echo
  echo - Importing "$dispatcherStoreName" Certificate into "$str" TrustStore
  echo
  keytool -import -file "$dispatcherStoreName".crt -keystore "$str"-ts.jks -storepass "$trustedSecret" -alias "$dispatcherStoreName" -dname CN=localhost -ext SAN=dns:localhost << EOF
  yes
EOF
  echo
  echo - Imported "$dispatcherStoreName" Certificate to "$str" TrustStore
  echo
fi
done

echo
echo
echo - Certificate, TrustStore and KeyStore generation completed.
echo



###############################################
################ FINALIZATION #################
###############################################

# Copy TrustStores to the client and KeyStores to the servers
echo
echo
echo - Copying TrustStores and KeyStores to their relative modules...
echo
cp "$clientStoreName"-ts.jks "$clientResourcesPath"
cp "$clientStoreName"-ks.jks "$clientResourcesPath"
cp "$dispatcherStoreName"-ts.jks "$serverResourcesPath"
cp "$dispatcherStoreName"-ks.jks "$serverResourcesPath"
cp "$storageStoreName"-ts.jks "$serverResourcesPath"
cp "$storageStoreName"-ks.jks "$serverResourcesPath"
cp "$authStoreName"-ks.jks "$serverResourcesPath"
cp "$authStoreName"-ts.jks "$serverResourcesPath"
cp "$accessStoreName"-ks.jks "$serverResourcesPath"
cp "$accessStoreName"-ts.jks "$serverResourcesPath"
echo - Done
echo
echo - Removing temp files...
rm -f *.jks
rm -f *.crt
echo
echo - Done
echo
echo
echo - KeyPair generation was successfully executed. Closing...

