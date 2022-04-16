package com.cointracker.crypotracker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class DataFechApi {
  private static final String BASE_URL = "https://api.blockchair.com/";
  private static final String DASHBOARDS_ADDRESSES = "/dashboards/address/";
  private static final String QUERY_PARAMETERS = "?transaction_details=true";
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * Get transactions for and address and crypto type
   *
   * @param cryptoType The type of the cryto for the address
   * @param address The address of the dashboard
   * @param pageLimit The number of records to be returned
   * @param offSet The offset from which to start fetching records from
   * @return The list of Transactions for this address
   */
  public List<Transaction> getTransactions(String cryptoType, String address, int pageLimit, int offSet) {
    List<Transaction> transactions = new ArrayList<>();
    String uriStr = BASE_URL + cryptoType + DASHBOARDS_ADDRESSES + address + QUERY_PARAMETERS;
    try {
      HttpResponse<String> response = getHttResponseWithUri(uriStr);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readValue(response.body(), JsonNode.class);
      JsonNode transactionList = node.get("data").get(address).get("transactions");
      for (int i = offSet; i < Math.min(transactionList.size(), pageLimit); i++) {
        JsonNode transactionJsonNoe = transactionList.get(i);
        //Transaction(String address, String cryptoType, String blockId, String hash, Date time, int balance_change)
        Transaction transaction = new Transaction(address, cryptoType, transactionJsonNoe.get("block_id").asText(), transactionJsonNoe.get("hash").asText(),
            parseDateTime(transactionJsonNoe.get("time").asText(), DATE_FORMAT), transactionJsonNoe.get("balance_change").asInt());
        transactions.add(transaction);
      }
    } catch (Exception e) {
      System.out.println("Failed to get tranactions: " + e);
    }
    return transactions;
  }

  /**
   * Get balance for an address and crypto type
   *
   * @param cryptoType The type of the crypto
   * @param address The address of the dashboard
   * @return The "Address" which contains balance summary of the address
   */
  @Nullable
  public Address getBalance(String cryptoType, String address) {
    String uriStr = BASE_URL + cryptoType + DASHBOARDS_ADDRESSES + address + QUERY_PARAMETERS;
    Address addressSummary = null;
    try {
      HttpResponse<String> response = getHttResponseWithUri(uriStr);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readValue(response.body(), JsonNode.class);
      JsonNode addressSummaryJsonNode = node.get("data").get(address).get("address");
      // Address (String addressId, String cryptoType, int balance, BigDecimal balance_usd, int received, BigDecimal received_usd)
      addressSummary = new Address(address, cryptoType, addressSummaryJsonNode.get("balance").asInt(),
          new BigDecimal(addressSummaryJsonNode.get("balance_usd").asText()),
          addressSummaryJsonNode.get("received").asInt(),
          new BigDecimal(addressSummaryJsonNode.get("received_usd").asText()));
    } catch (Exception e) {
      System.out.println("Failed to get tranactions: " + e);
    }
    System.out.println(addressSummary);
    return addressSummary;
  }


  /**
   * @param uriStr The full uri string to get data from
   * @return HttpResponse which contains the requested data
   * When http request fails, it will return null response
   */
  @Nullable
  public HttpResponse<String> getHttResponseWithUri(String uriStr) {
    HttpResponse<String> response = null;
    try {
      HttpRequest request =
          HttpRequest.newBuilder().uri(new URI(uriStr)).headers("Accept", "application/json").GET().build();
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      System.out.println("http request failed: " + e);
    }
    return response;
  }

  /**
   *
   * @param dateStr The date string to parse
   * @param dateFormat The format of the date string
   * @return Date which is structured data
   * When there is exception it returns null
   */
  @Nullable
  public Date parseDateTime(String dateStr, String dateFormat) {
    try {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      return df.parse(dateStr);
    } catch (ParseException e) {
      System.out.println("Parse date time failed! " + e);
    }
    return null;
  }

  /**
   * This entity saves the balance information of an "Address"
   * For simplicity, using a JAVA class to indicate the schema design for transaction.
   * In real practice, this can be defined with PDL: https://linkedin.github.io/rest.li/pdl_schema
   */
  public static class Address {
    /**
     * The addressId for the cryto address. This uniquely identify an address
     * This field is required.
     */
    String addressId;

    /**
     * The type of this crypto address, such as Bitcoin, BitcoinCash, Litcoin, Dogecoin, Zcash, and etc..
     * Adding this field for enabling presenting statics of crypto types for an end user. This field is required.
     */
    String cryptoType;

    /**
     * The balance of this address in the unit of the crypto type
     */
    int balance;

    /**
     * The balance in US dollar
     */
    BigDecimal balance_usd;

    /**
     * The received number of crytos
     */
    int received;

    /**
     * The received US dollars
     */
    BigDecimal received_usd;

    public Address (String addressId, String cryptoType, int balance, BigDecimal balance_usd, int received, BigDecimal received_usd) {
      this.addressId = addressId;
      this.cryptoType = cryptoType;
      this.balance = balance;
      this.balance_usd = balance_usd;
      this.received = received;
      this.received_usd = received_usd;
    }
  }



  /**
   * The transaction entity schema that can be presented at the frontend.
   * We fetch and save transactions data to our own DB so that it can be easily queried and presented at our own frontend
   *
   * For simplicity, using a JAVA class to indicate the schema design for transaction.
   * In real practice, this can be defined with PDL: https://linkedin.github.io/rest.li/pdl_schema
   */
  public static class Transaction {
    /**
     * The address for the cryto dashboard. By adding this field to transaction schema, we can easily query all the related
     * transactions to present at the frontend. This field is required.
     */
    String address;

    /**
     * The type of this crypto transaction, such as Bitcoin, BitcoinCash, Litcoin, Dogecoin, Zcash, and etc..
     * Adding this field for enabling presenting statics of crypto types for an end user. This field is required.
     */
    String cryptoType;

    /**
     * The block id that uniquely identify the block for which the transaction occurred
     * This field is required.
     */
    String blockId;
    /**
     * The hash corresponding to this block
     * This field is required.
     */
    String hash;
    /**
     * The time for the transaction
     */
    Date time;
    /**
     * The balance change due to this transaction
     * If this needs to preserve decimal, the type should be changed to BigDecimal type, rather than using float
     */
    int balance_change;

    public Transaction(String address, String cryptoType, String blockId, String hash, Date time, int balance_change) {
      this.address = address;
      this.cryptoType = cryptoType;
      this.blockId = blockId;
      this.hash = hash;
      this.time = time;
      this.balance_change = balance_change;
    }
  }

}
