#!/bin/bash

# DELETE ALL KEYSTORES
rm -f *.jks
rm -f *.cert

# VARIABLES
clientResourcesPath="/home/fparrinha/Documents/Projetos-FCT/4th_Year/Segurança de Redes e Sistemas de Computadores/SRSC_Projeto2/proj2/FClient/src/main/resources"
serverResourcesPath="/home/fparrinha/Documents/Projetos-FCT/4th_Year/Segurança de Redes e Sistemas de Computadores/SRSC_Projeto2/proj2/FServer/src/main/resources"
SERVERS=("fserver-dispatcher")

# Creates a client trustore containing CACERTS
set +x
echo "Creating Client Truststore"
cp cacerts client-ts.jks
 
# Genereates keystores and certificates for all servers
for str in ${SERVERS[@]}
do
# Generates one keystore file for one server
keytool -genkey -alias $str -keyalg RSA -validity 365 -keystore ./$str.jks -storetype pkcs12 -ext SAN=dns:$str << EOF
123users
123users
Users.Users
TP2
SD2223
LX
LX
PT
yes
123users
123users
EOF

# Exports certificate
echo
echo
echo "Exporting Certificates"
echo
echo

keytool -exportcert -alias $str -keystore $str.jks -file $str.cert -ext SAN=dns:$str<< EOF
123users
EOF

# Exports certificate
echo
echo
echo "Adding Certificate To Client Trustore"
echo
echo
keytool -importcert -file $str.cert -alias $str -keystore client-ts.jks -ext SAN=dns:$str<< EOF
changeit
yes
EOF
done

set -x
echo Certificate, truststore and keystore generation completed.

# Copy truststore to the client and keystores to the servers
echo Copying trsustores and keystores to their relative modules...
cp -v "client-ts.jks" "$clientResourcesPath"
cp -v "fserver-dispatcher.jks" "$serverResourcesPath"
echo Done

