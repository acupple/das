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
## Usage
Connection connection = null;
try{
  connection = new Connection(HOST, PORT);
  connection.connect();
  DalClient client = new DalClient(connection, "db_name");
  RecordMapper<TestBig> mapper = AutoRecordMapper.create(TestBig.class);
  DalModels.DalHints hints = DalModels.DalHints.getDefaultInstance();
  List<TestBig> persons = client.query("SELECT * FROM Table_name", mapper, hints);
}cactch(Exception e){
  e.printStackTrace();
}
