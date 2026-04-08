# 1. Build 
mvn clean package

# 2. run - command
java -jar target/laundry-cli-1.0.0.jar auth login
java -jar target/laundry-cli-1.0.0.jar customer list
java -jar target/laundry-cli-1.0.0.jar order create
java -jar target/laundry-cli-1.0.0.jar payment pay <order-id>
java -jar target/laundry-cli-1.0.0.jar report daily

# 3. List all command
java -jar target/laundry-cli-1.0.0.jar --help

# simplify
alias laundry="java -jar target/laundry-cli-1.0.0.jar"
laundry auth login
laundry customer add
