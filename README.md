# das

Database access proxy service

Deploy:
1. mvn clean packag
2. sudo cp target/dal-das-worker-1.0.0.tar.gz /opt/app
3. cd /opt/app
4. sudo tar -zxvf dal-das-worker-1.0.0.tar.gz .
5. sudo mv dal-das-worker-1.0.0 dasworker
6. cd ./dasworker/bin
7. sudo chmod +x worker wrapper-linux-x86-64
8. sudo ./worker start 
