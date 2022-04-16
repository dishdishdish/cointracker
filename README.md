## Setup
1. If you don't have Java installed, visit https://java.com/en/download/help/download_options.html.
2. cd into `cryptotracker` folder
3. DataFechApi contains the API function that fetch crypto transaction data and also an executable main class for testing the functions

## API

This project is the API layer of the system. The frontend calls the APIs to get the required data for presentation.
Following are the assumptions:
- The data is all fetched from https://api.blockchair.com/
- When frontend make the call, it will first check with local database and if it could not find it or the data is too
outdated, it will then fetch through the external endpoint. This part is not included in the actual API implementation

In DataFechApi.java, it contains two APIs:
- `List<Transaction> getTransactions(String cryptoType, String address, int pageLimit, int offSet)`

Since the example URL does not provide pagination feature. This function only fetch one page and models "pageLimit" 
and "offSet" without fetching data from all pages. This API is called by the fontend to present transactions of an address.
After the data is fetched, the data is saved into "Transaction" table. The schema is defined in Transaction class in DataFechApi.java

- `Address getBalance(String cryptoType, String address)`

This API get the balance summary of an address. After data is fetched, the data is saved into "Address" table. The schema
is defined in Address class in DataFechApi

## Test

All the test cases is included in TestDataFechApi.java

## interesting architecture decisions

1. Local database is needed to minimize traffic to external API. It could be either slow or expensive. In practice, it can
have an offline job to maintain the freshness of the user data in local DB by doing periodic DB scan and external API data fetch.

2. The key part is the schema design for "Transaction" and "Address". Both enties contains "CyryptoType" and
"AddressId". This is to ensure convenient query with "CyryptoType" or "AddressId" for presentation in frontend. Keep in 
mind that the core purpose of this is to "track" and "present" addresses for end users

3. The API contains "cryptoType" parameter to support different types, so that user can establish a central place for all.

## IDE/Windows-specific bugs (skip unless you're having problems on Windows or with your IDE):
Make sure you have completed setup before proceeding!

IntelliJ:
- Error: `java: package org.apache.commons.lang3 does not exist`
  - Right-click `pom.xml` in the directory window > "Add as Maven Project"; this should reload the project with `org.apache.commons:commons-lang3:3.10` as a dependency now.
- Error: `java: cannot access <class>`
  - File > Invalidate Caches / Restart...

Visual Studio Code/Windows:
- `JAVA_HOME` not updating
  - Quit ALL VSCode windows, otherwise machine might not pick up updated `JAVA_HOME`
- VSCode Maven extension is not enough
  - Install in your home subdirectory - instructions: https://maven.apache.org/install.html
  - Update Windows PATH to include that subdirectory
