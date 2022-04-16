package com.cointracker.cryptotracker;

import com.cointracker.crypotracker.DataFechApi;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;

public class TestDataFechApi {

  @org.junit.jupiter.api.Test
  void testGetBalance() {
    DataFechApi dataFechApi = new DataFechApi();
    DataFechApi.Address address = dataFechApi.getBalance("bitcoin", "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S");
    assert address != null;
  }

  @org.junit.jupiter.api.Test
  void testGetTransactionsWithPageSizeAndOffset() {
    DataFechApi dataFechApi = new DataFechApi();
    int pageLimit = 10;
    List<DataFechApi.Transaction> transactions = dataFechApi.getTransactions("bitcoin", "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S", pageLimit, 60);
    Assertions.assertEquals(0, transactions.size());
  }

  @org.junit.jupiter.api.Test
  void testGetTransactionsWithPageSize() {
    DataFechApi dataFechApi = new DataFechApi();
    int pageLimit = 10;
    List<DataFechApi.Transaction> transactions = dataFechApi.getTransactions("bitcoin", "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S", pageLimit, 0);
    Assertions.assertEquals(pageLimit, transactions.size());
  }

  @org.junit.jupiter.api.Test
  void testParseDateTime() {
    DataFechApi dataFechApi = new DataFechApi();
    Date date = dataFechApi.parseDateTime("Tue Jan 18 23:09:15 PST 2022", "yyyy-MM-dd HH:mm:ss");
    assert date != null;
  }

  @org.junit.jupiter.api.Test
  void testGetHttResponseWithUri() throws IOException {
    String uriStr = "https://api.blockchair.com/bitcoin/dashboards/address/12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S?transaction_details=true";
    DataFechApi dataFechApi = new DataFechApi();
    HttpResponse<String> response = dataFechApi.getHttResponseWithUri(uriStr);
    Assertions.assertEquals(200, response.statusCode());
  }

}
