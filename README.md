# Database access proxy service
## Deploy
1. mvn clean packag
2. sudo cp target/das-worker-1.0.0.tar.gz /opt/app
3. cd /opt/app
4. sudo tar -zxvf das-worker-1.0.0.tar.gz .
5. sudo mv das-worker-1.0.0 dasworker
6. cd ./dasworker/bin
7. sudo chmod +x worker wrapper-linux-x86-64
8. sudo ./worker start 

## TODO List
1. worker service discovery
2. load balance
3. datasource manager for jdbc connection on worker
4. security
