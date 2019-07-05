Alpaca Trade API Java
=====================
[![Build Status](https://travis-ci.org/maseev/alpaca-trade-api-java.svg?branch=master)](https://travis-ci.org/maseev/alpaca-trade-api-java)
[![Coverage Status](https://coveralls.io/repos/github/maseev/alpaca-trade-api-java/badge.svg?branch=master)](https://coveralls.io/github/maseev/alpaca-trade-api-java?branch=master)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/maseev/alpaca-trade-api-java.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/maseev/alpaca-trade-api-java/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/maseev/alpaca-trade-api-java.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/maseev/alpaca-trade-api-java/context:java)
[![GitHub](https://img.shields.io/github/license/maseev/alpaca-trade-api-java.svg)](https://github.com/maseev/alpaca-trade-api-java/blob/master/LICENSE)

Java API for [Alpaca](https://alpaca.markets) - a commission-free API-first stock brokerage
-------------------------------------------------------------------------------------------

How to build
------------
* Clone this repository
* Run `./mvn clean install` in the project folder to build the project and install it to the local Maven repository

How to use
----------

Add `alpaca-trade-api-java` as a dependency:

##### Maven
```xml
<dependency>
  <groupId>io.github.maseev</groupId>
  <artifactId>alpaca-trade-api-java</artifactId>
  <version>1.0</version>
</dependency>
```

### Initialization

```java
String keyId = "Your API key ID";
String secretKey = "Your secret key";

AlpacaAPI api = new AlpacaAPI(TEST, V1, keyId, secretKey);
```

`alpaca-trade-api-java` provides two versions of API, asynchronous:

```java
api.account()
  .get()
  .onComplete(new ResponseHandler<Account>() {

    @Override
    public void onSuccess(Account result) {
    }

    @Override
    public void onError(Exception ex) {
    }
  });
```

and synchronous:

```java
Account account = api.account().get().await();
```

### Account

#### [Get the account](https://docs.alpaca.markets/api-documentation/web-api/account/#get-the-account)

```java
Account account = api.account().get().await();
```

### Orders
#### [Get a list of orders](https://docs.alpaca.markets/api-documentation/web-api/orders/#get-a-list-of-orders)

```java
Status status = Status.OPEN;
int limit = 10;
LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
LocalDateTime until = of(2009, Month.DECEMBER, 1, 10, 00, 10);
Direction direction = Direction.ASC;
    
List<Order> orders =
  api.orders()
    .get(status, limit, after, until, direction)
    .await();
```

#### [Request a new order](https://docs.alpaca.markets/api-documentation/web-api/orders/#request-a-new-order)

```java
OrderRequest request =
  ImmutableOrderRequest.builder()
    .symbol("AAPL")
    .qty(1)
    .side(BUY)
    .type(MARKET)
    .timeInForce(DAY)
    .build();

Order order = api.orders().place(request).await();
```

#### [Get an order](https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order)

```java
Order order = api.orders().get("id").await();
```

#### [Get an order by client order id](https://docs.alpaca.markets/api-documentation/web-api/orders/#get-an-order-by-client-order-id)

```java
Order order = api.orders().getByClientOrderId("id").await();
```
#### [Cancel an order](https://docs.alpaca.markets/api-documentation/web-api/orders/#cancel-an-order)

```java
api.orders().cancel("id").await();
```

### Positions
#### [Get open positions](https://docs.alpaca.markets/api-documentation/web-api/positions/#get-open-positions)

```java
List<Position> positions = api.positions().get().await();
```
#### [Get an open position](https://docs.alpaca.markets/api-documentation/web-api/positions/#get-an-open-position)

```java
Position position = api.positions().get("AAPL").await();
```

### Assets
#### [Get assets](https://docs.alpaca.markets/api-documentation/web-api/assets/#get-assets)

```java
List<Asset> assets = api.assets().get(ACTIVE, US_EQUITY).await();
```
#### [Get an asset](https://docs.alpaca.markets/api-documentation/web-api/assets/#get-an-asset)

```java
Asset asset = api.assets().get("AAPL").await();
```

### Calendar
#### [Get the calendar](https://docs.alpaca.markets/api-documentation/web-api/calendar/#get-the-calendar)

```java
LocalDate start = LocalDate.now();
LocalDate end = start.plusDays(10);

List<Calendar> calendars = api.calendar().get(start, end).await();
```
### Clock
#### [Get the clock](https://docs.alpaca.markets/api-documentation/web-api/clock/#get-the-clock)

```java
Clock clock = api.clock().get().await();
```

### Market Data
#### [Get a list of bars](https://docs.alpaca.markets/api-documentation/api-v2/market-data/bars/#get-a-list-of-bars)

```java
String symbol = "AAPL";
Timeframe timeframe = Timeframe.DAY;
OffsetDateTime start = of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
OffsetDateTime end = start.plusWeeks(3);
boolean timeInclusive = false;
int limit = 10;
    
Map<String, List<Bar>> bars =
  api.bars()
    .get(symbol, timeframe, start, end, timeInclusive, 10)
    .await();
```

### Streaming
There are four types of events you can subscribe on `AccountUpdate`, `TradeUpdate`, 
`ConnectionClose`, and `ConnectionCrash`.

You can subscribe to these events by calling the `subscribe` method:

```java
api.streaming().subscribe((AccountUpdate event) -> {});
api.streaming().subscribe((TradeUpdate event) -> {});
api.streaming().subscribe((ConnectionClose event) -> {});
api.streaming().subscribe((ConnectionCrash event) -> {});
```

In order to connect to the Alpaca Streaming API, you need to call the `connect` method:

```java
api.streaming().connect();
```

You also have to handle the situation when the connection gets closed. In order to 
reconnect to the Streaming API in this situation, you have to subscribe to the `ConectionClose` event and call the `connect` method from the event handler:

```java
api.streaming().subscribe((ConnectionClose event) -> { api.streaming().connect(); });
```

Notice, that you don't have to resubscribe to all events because all your subscriptions are stored
 separately from the connection to the Streaming API.