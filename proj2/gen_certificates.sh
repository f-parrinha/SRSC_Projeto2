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

dispatcherStoreName=fserver-dispatcher
clientStoreName=client
trustedSecret=password
secret=password
serverResourcesPath=FServer/src/main/resources
clientResourcesPath=FClient/src/main/resources
server=("$dispatcherStoreName")




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
keytool -genkeypair -keyalg RSA -keysize 2048 -keystore ./"$str"-ks.jks -validity 365 -alias "$str" -storepass "$secret" -keypass "$secret"<< EOF
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
keytool -export -file "$str".crt -keystore "$str"-ks.jks -storepass "$secret" -alias "$str" << EOF
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

# Imports certificate
echo
echo
echo - Importing Certificate into Client Truststore
echo
keytool -import -file "$str".crt -keystore "$clientStoreName"-ts.jks -storepass "$trustedSecret" -alias "$str" << EOF
yes
EOF
echo
echo
echo - Imported server certificate to client truststore
echo
keytool -import -file "$clientStoreName".crt -keystore "$str"-ts.jks -storepass "$secret" -alias client-ts << EOF
yes
EOF
echo
echo
echo - Imported client certificate to server truststore
echo
done

echo
echo
echo - Certificate, truststore and keystore generation completed.




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

