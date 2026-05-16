
# dsp-common-java/api-commons-v2

Creation Date 22-08-2023

This package will be used for common classes that can be utilized throughout the Digital Store Platform.
It includes logging configuration so services can adopt consistent logging with minimal configuration.

## Logging

### Logging Formats

#### Using Local (or Dev) Profile

When the spring profile `local` or `dev` is enabled, logs will be in a space-delimited, aligned/tabular format:

```text
12:15:30.638-07:00 INFO c.u.d.c.c.a.ctp.UltaCartCTAdapterImpl    : updateCart - cart c4acca0d-3dd3-4d0b-ad1f-f8eb21a2e7ff - version : 7
```

These logs are optimized for human readability during local development.
They include fewer fields since some fields (e.g. PID or request sessionId) are less useful in this context,
and having too many fields can force additional horizontal scrolling.

#### In Deployed Environments

When the `local` or `dev` profiles or not enabled (as is the case in deployed environments),
logs are optimized for parsing/indexing and include additional log fields.

```text
{"timestampSeconds":1725477804,"timestampNanos":175000000,"severity":"INFO","thread":"main","logger":"org.springframework.boot.web.embedded.netty.NettyWebServer","message":"Netty started on port 8090","context":"default"}
```

These loggers are configured in [logback-spring.xml](./src/main/resources/logback-spring.xml).

#### Overriding the Log Format

If you would like tweak the format (e.g. to add an additional field or adjust colors),
there are two ways to override the log format:

1. Set the CONSOLE_LOG_PATTERN environment variable:

   ```text
   # This will add the thread name to the log lines
   > export CONSOLE_LOG_PATTERN="${CONSOLE_LOG_PATTERN:-%clr(%d{${LOG_DATEFORMAT_PATTERN:-HH:mm:ss.SSSXXX}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(%-40.40logger{39}){cyan} %clr([%.10thread]){faint} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"
   > ./gradlew bootRun
   # or in a single line
   > CONSOLE_LOG_PATTERN="${CONSOLE_LOG_PATTERN:-%clr(%d{${LOG_DATEFORMAT_PATTERN:-HH:mm:ss.SSSXXX}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(%-40.40logger{39}){cyan} %clr([%.10thread]){faint} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}" ./gradlew bootRun
   ```

2. Set the `logging.pattern.console` configuration property in `application.properties`
   or `application-<profile>.properties` (e.g. `application-local.properties`) of your service repository (not this repository).

   ```text
   [in e.g. application.properties]
   [This is Spring's default logging format]
   logging.pattern.console=%clr(%d{${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd'T'HH:mm:ss.SSSXXX}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}
   ```

Note that the configuration property will take precedence over the environment variable if both are set.

For information on crafting these format strings, check the
[logback PatternLayout documentation](https://logback.qos.ch/manual/layouts.html#ClassicPatternLayout).

### Method Execution Tracing via AspectJ

This library includes logging configuration in
[LoggingConfig.java](./src/main/java/com/ulta/dsp/common/config/LoggingConfig.java) that utilizes aspect-oriented
programming (AOP) via AspectJ to write log statements when entering & exiting every method call.

Here's an example of what those log statements look like:

```text
12:02:49.858-07:00 DEBUG c.ulta.dsp.common.config.LoggingConfig   : Entered c.u.d.c.c.c.s.UltaCtApiClientProperties.getSocketTimeout()
12:02:49.858-07:00 DEBUG c.ulta.dsp.common.config.LoggingConfig   : Exited c.u.d.c.c.c.s.UltaCtApiClientProperties.getSocketTimeout()
```

Note that these logs will only appear in classes for which debug logs are enabled.

### Setting the Log Level

To set the log verbosity, set the configuration property `logging.level.<fully-qualified package name>`.
This will apply for all sub-packages.

For example:

```text
[in e.g. application.properties]
# Use debug logs for Ulta code
logging.level.com.ulta.dsp=DEBUG

# Use debug logs to print requests/responses
# With standard RestTemplate:
logging.level.org.springframework.web.client.RestTemplate=DEBUG
# With webflux:
logging.level.org.springframework.web.reactive.function.client=DEBUG
```

### Colorized output

Spring theoretically auto-detects terminals that support colorized output via ANSI escape sequences, but
I have not gotten this work as expected. As a workaround (to support a better out-of-the-box experience for developers),
including this library will also configure spring to force colorized output when using the `local` or `dev` profiles
(see [application-local.properties](./src/main/resources/application-local.properties)).
To disable this behavior, include the following in your service's `application-local.properties`:

```text
spring.output.ansi.enabled=never
```

Log levels will be colored based on the specific level, which is a useful
feature for visually finding warning & error logs.

Note that this is probably not desirable in deployed environments and pipelines,
since escape sequences can be printed and make the output less readable/parseable.

### Additional Information

For more info on how to configure logging and how Spring interacts with logback,
check the [Spring logging documentation](https://docs.spring.io/spring-boot/reference/features/logging.html)
and the [logback documentation](https://logback.qos.ch/manual/configuration.html).

### Request/Response Logging

Contains classes to automatically log the request and response details for inbound and outbound requests
(When WebClient is created using UltaWebClientBuilderCreatorService component ) reactive requests

```text
[These logs use an older format and don't reflect the current logs]
2020-Aug-12 14:55:27.907 [reactor-http-nio-3] sid= did= INFO  c.u.d.c.f.UltaRequestResponseLoggingFilter filter - Request: method=POST, uri=/core/catalog/productsearch/autocomplete, headers=[Content-Type:"application/json", X-ULTA-COUNTRY:"US", X-ULTA-LOCALE:"en-ca", User-Agent:"PostmanRuntime/7.26.2", Accept:"*/*", Cache-Control:"no-cache", Postman-Token:"974d6821-516f-490e-aa64-d39b4f7d2905", Host:"localhost:8080", Accept-Encoding:"gzip, deflate, br", Connection:"keep-alive", Content-Length:"93"]
2020-Aug-12 14:55:28.087 [reactor-http-nio-3] sid= did= INFO  c.u.d.c.f.UltaRequestLoggingInterceptor lambda$getBody$0 - Request: method=POST, uri=/core/catalog/productsearch/autocomplete, headers=[Content-Type:"application/json", X-ULTA-COUNTRY:"US", X-ULTA-LOCALE:"en-ca", User-Agent:"PostmanRuntime/7.26.2", Accept:"*/*", Cache-Control:"no-cache", Postman-Token:"974d6821-516f-490e-aa64-d39b4f7d2905", Host:"localhost:8080", Accept-Encoding:"gzip, deflate, br", Connection:"keep-alive", Content-Length:"93"], payload={
    "query":"bath",
    "locale":"en-ca",
    "suggestionCount":5,
    "productCount":5


}
2020-Aug-12 14:55:28.222 [reactor-http-nio-3] sid= did= INFO  c.u.d.c.p.a.UltaSearchAMAdapter lambda$8 - Request Object : UltaRequestContext(country=US, locale=en_CA, additionalProperties=null), 
2020-Aug-12 14:55:33.140 [reactor-http-nio-3] sid= did= INFO  c.u.d.c.w.f.UltaWebClientRequestResponseLogger filter - Request: method=POST, url=https://ulta-search-development.gcp.adeptmind.ai/autocomplete-search?locale=en-ca, headers=[Authorization:"Bearer abc_us", Content-Type:"application/json"]
2020-Aug-12 14:55:40.239 [reactor-http-nio-7] sid= did= INFO  c.u.d.c.w.f.UltaWebClientRequestResponseLogger lambda$filter$0 - Response:  url=https://ulta-search-development.gcp.adeptmind.ai/autocomplete-search?locale=en-ca, headers=[x-powered-by:"Express", access-control-allow-origin:"*", content-type:"application/json; charset=utf-8", content-length:"15739", etag:"W/"3d7b-TywRIdwpUDTeoTF/yGIycPFFs2A"", vary:"Accept-Encoding", date:"Wed, 12 Aug 2020 14:55:35 GMT", server:"istio-envoy", via:"1.1 google"], status=200 OK
2020-Aug-12 14:55:40.240 [reactor-http-nio-7] sid= did= INFO  c.u.d.c.p.a.UltaSearchAMAdapter lambda$14 - Request Object : UltaRequestContext(country=US, locale=en_CA, additionalProperties=null), 
2020-Aug-12 14:55:40.421 [reactor-http-nio-7] sid= did= INFO  c.u.d.c.f.UltaResponseLoggingInterceptor lambda$writeWith$0 - Response(12513 ms): status=200, payload={"meta":{"timestamp":"2020-08-12T14:55:40.305084Z"},"data":{"suggestedResult":{"key":"RegularSuggestions","suggestions":[{"id":"ULTA Collection>Bath & Body>Moisturizers","name":"ULTA Collection>Bath & Body>Moisturizers","products":[]},{"id":"Lavender Chamomile Bubble Bath","name":"Lavender Chamomile Bubble Bath","products":[]},{"id":"Amazing Grace Ballet Rose Shampoo, Bath & Shower Gel","name":"Amazing Grace Ballet Rose Shampoo, Bath & Shower Gel","products":[]},{"id":"Argan Oil & Lavender Soothing Spa Bath Bombs","name":"Argan Oil & Lavender Soothing Spa Bath Bombs","products":[]},{"id":"Pink Sorbet Bath Fizz","name":"Pink Sorbet Bath Fizz","products":[]}]},"productResult":[{"productId":"xlsImpprod16411151","productDisplayName":"Bath Pillow","brand":{"id":"NA","displayName":"The Vintage Cosmetic Company","location":"/brand/The Vintage Cosmetic Company"},"skus":[{"id":"2515285","displayName":"Bath Pillow","brand":{"id":"NA","displayName":"The Vintage Cosmetic Company","location":"/brand/The Vintage Cosmetic Company"},"images":{"mainImage":{"location":"http://images.ulta.com/is/image/Ulta/2515285?$lg$","type":"IMAGE","description":"http://images.ulta.com/is/image/Ulta/2515285?$lg$"}},"reviewSummary":{"rating":3.5,"reviewCount":9},"prices":{"listPrice":{"centAmount":1199,"currencyCode":"CAD"},"salePrice":{"centAmount":1199,"currencyCode":"CAD"}}}]},{"productId":"xlsImpprod19031003","productDisplayName":"Bath & Shower Oil","brand":{"id":"NA","displayName":"Ahava","location":"/brand/Ahava"},"skus":[{"id":"2530919","displayName":"Bath & Shower Oil","brand":{"id":"NA","displayName":"Ahava","location":"/brand/Ahava"},"images":{"mainImage":{"location":"https://images.ulta.com/is/image/Ulta/2530919?$lg$","type":"IMAGE","description":"https://images.ulta.com/is/image/Ulta/2530919?$lg$"}},"reviewSummary":{"rating":4.0,"reviewCount":5},"prices":{"listPrice":{"centAmount":2100,"currencyCode":"CAD"},"salePrice":{"centAmount":2100,"currencyCode":"CAD"}}}]},{"productId":"xlsImpprod12812899","productDisplayName":"Bath & Shower Gloves","brand":{"id":"NA","displayName":"EcoTools","location":"/brand/EcoTools"},"skus":[{"id":"2291856","displayName":"Bath & Shower Gloves","brand":{"id":"NA","displayName":"EcoTools","location":"/brand/EcoTools"},"images":{"mainImage":{"location":"https://images.ulta.com/is/image/Ulta/2291856?$lg$","type":"IMAGE","description":"https://images.ulta.com/is/image/Ulta/2291856?$lg$"}},"reviewSummary":{"rating":4.5,"reviewCount":102},"prices":{"listPrice":{"centAmount":399,"currencyCode":"CAD"},"salePrice":{"centAmount":399,"currencyCode":"CAD"}}}]},{"productId":"pimprod2010824","productDisplayName":"Bath Treats Gift Set","brand":{"id":"NA","displayName":"ULTA","location":"/brand/ULTA"},"skus":[{"id":"2549173","displayName":"Bath Treats Gift Set","brand":{"id":"NA","displayName":"ULTA","location":"/brand/ULTA"},"images":{"mainImage":{"location":"http://images.ulta.com/is/image/Ulta/2549173?$lg$","type":"IMAGE","description":"http://images.ulta.com/is/image/Ulta/2549173?$lg$"}},"reviewSummary":{"rating":4.5,"reviewCount":2},"prices":{"listPrice":{"centAmount":300,"currencyCode":"CAD"},"salePrice":{"centAmount":300,"currencyCode":"CAD"}}}]},{"productId":"pimprod2002925","productDisplayName":"Bath and Shower Liquid Body Cleanser Grapefruit","brand":{"id":"NA","displayName":"Kiehl's Since 1851","location":"/brand/Kiehl's Since 1851"},"skus":[{"id":"2536626","displayName":"Bath and Shower Liquid Body Cleanser Grapefruit","brand":{"id":"NA","displayName":"Kiehl's Since 1851","location":"/brand/Kiehl's Since 1851"},"images":{"mainImage":{"location":"https://images.ulta.com/is/image/Ulta/2536626?$lg$","type":"IMAGE","description":"https://images.ulta.com/is/image/Ulta/2536626?$lg$"}},"reviewSummary":{"rating":5.0,"reviewCount":2},"prices":{"listPrice":{"centAmount":2800,"currencyCode":"CAD"},"salePrice":{"centAmount":2800,"currencyCode":"CAD"}}}]}]},"messages":[]}
2020-Aug-12 14:55:40.439 [reactor-http-nio-3] sid= did= INFO  c.u.d.c.f.UltaRequestResponseLoggingFilter logResponse - Response(12532 ms): status=200, headers=[Content-Type:"application/json;charset=UTF-8", Content-Length:"3933"]
```

## BaseResponse

Here is a sample extension of BaseResponse using an extendable BuilderPattern notation

```java
/** This is a sample class that could be used to extend the BaseResponse object */
public class UltaResponseExample extends UltaBaseResponse {
  /** Field that represents additional response data */
  private String data;

  private UltaMessage message;

  /**
   * Constructor for a new UltaResponseExample.
   *
   * @param builder the builder
   */
  UltaResponseExample(final UltaResponseExample.Builder builder) {
    super(builder);
    this.data = builder.data;
    this.message = builder.message;
  }

  /**
   * Get the objects additional data
   *
   * @return data
   */
  public String getData() {
    return data;
  }

  /**
   * Gets the message
   *
   * @return message
   */
  public UltaMessage getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UltaResponseExample)) return false;
    if (!super.equals(o)) return false;
    UltaResponseExample that = (UltaResponseExample) o;
    return Objects.equals(getData(), that.getData()) && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getData(), message);
  }

  /**
   * Returns the Builder for UltaResponseExample
   *
   * @return UltaResponseExample.Builder
   */
  public static UltaResponseExample.Builder builder() {
    return new UltaResponseExample.Builder();
  }

  /**
   * Builder class that will set the attributes and build the final immutable UltaExampleResponse
   * object
   */
  public static class Builder extends UltaBaseResponse.Builder<UltaResponseExample.Builder> {
    String data;
    UltaMessage message;

    /**
     * Set data
     *
     * @param data additional data for the response
     * @return {@link UltaResponseExample.Builder} object
     */
    public UltaResponseExample.Builder withData(final String data) {
      this.data = data;
      return self();
    }

    /**
     * Set message
     *
     * @param message additional data for the response
     * @return {@link UltaResponseExample.Builder} object
     */
    public UltaResponseExample.Builder withMessage(final UltaMessage message) {
      this.message = message;
      return self();
    }

    /**
     * Override Build so the UltaResponseExample and BaseResponse are created with the given
     * attributes.
     *
     * @return {@link UltaResponseExample} object
     */
    @Override
    public UltaResponseExample build() {
      return new UltaResponseExample(this);
    }

    /**
     * Method that will return itself for additional fields to be set
     *
     * @return the object
     */
    @Override
    protected UltaResponseExample.Builder self() {
      return this;
    }
  }
}
```

Example Usage:

```java
  public class ExampleService {

// Messages can be added individually
     UltaResponseExample extendedResponse = new UltaResponseExample.Builder()
                    .withData("Some Data Value(s) and/or Object(s)")
                    .withMeta(UltaMeta.builder()
                            .withTimestamp(UltaMeta.now())
                            .withStatus(UltaResponseType.SUCCESS)
                            .build()) 
                    .addMessage(UltaMessage.builder()
                            .withType(UltaMessageType.INFO)
                            .withCode("200")
                            .withDescription("Message Description")
                            .build())
                    .build();

// Messages can be added as a group
    List<Message> messages = Arrays.asList(UltaMessage.builder().withType(UltaMessageType.INFO).withCode("200").withDescription("Successful").build(),
                                           UltaMessage.builder().withType(UltaMessageType.ERROR).withCode("400").withDescription("Validation Error".build()));

        final UltaResponseExample response = UltaResponseExample.builder()
                .withData("Some Data Value(s) and/or Object(s)")
                .withMessages(messages)
                .addMessage(UltaMessage.builder().withType(UltaMessageType.INFO).withCode("200").withDescription("Successful").build())
                .addMessage(UltaMessage.builder().withType(UltaMessageType.ERROR).withCode("500").withDescription("Downstream Not Successful").build())
                .withMeta(UltaMeta.Builder().withTimestamp(UltaMeta.now()).withStatus(UltaResponseType.SUCCESS).build())
                .build();
  }
```

---
### Spring WebClient ###

Contains helper Spring Component to create reactive Spring WebClient to provide out of the box support for logging and Context (country and language) injection in headers for Orchestration to Orchestration outbound call or as URL query parameters for  Orchestration to Core outbound call  .

#### Configuration properties: ####

Below properties can be configured for creating webclient.

| Property Name                        | Default value | Description                                                                                                                                   |
|--------------------------------------|---------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| ulta.webclient.config.timeout        | 5000          | WebClient connection timeout in milliseconds.                                                                                                 |
| ulta.logging.requestresponse.enabled | false         | Set this property to true to enable logging of reactive request and response. ( Note: Currently request and response body is not supported. ) |

#### Example Usage: Creating Spring WebClient ###

```java
public class ExampleService {
  UltaWebClientBuilderCreatorService ultaWebClientBuilderCreatorService;

  WebCLient webClient;
    public ExampleService(){
     webClient  =  this.ultaWebClientBuilderCreatorService.createWebClientBuilder(UltaWebClientBuilderType.EXTERNAL)
      
          .baseUrl(this.ultaAdeptmindUSConfiguration.getBaseUrl())
          .defaultHeader(
                  HttpHeaders.AUTHORIZATION,
                  String.format(
                          AUTHORIZATION_HEADER_VALUE_PLACEHOLDER,
                          this.ultaAdeptmindUSConfiguration.getAuthorizationHeader()))
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .defaultUriVariables(Collections.singletonMap(UltaProductSearchConstant.PROJECT_ID, this.ultaAdeptmindUSConfiguration.getAmProjectId()))
          .build();
    }
}
```

#### Example Usage: Get country and locale from request context in a reactive/Mono code ###

```java
public class ExampleService {
  UltaWebClientBuilderCreatorService ultaWebClientBuilderCreatorService;

  /**
   * Calls product-search-provider api to search products for the specified search criteria.
   *
   * @param ultaSearchRequest
   * @return
   */
  public Mono<UltaProductSearchData> searchProductsByKeywords(UltaSearchRequest ultaSearchRequest) {
    LOG.debug("Begin searchProductsByKeywords");
    UltaAMSearchRequest amSearchRequest =
        Mappers.getMapper(UltaAMSearchRequestMapper.class).map(ultaSearchRequest);

    return Mono.subscriberContext()
    .flatMap(context -> {
      UltaRequestContext requestContext = (UltaRequestContext) context
          .getOrEmpty(com.ulta.dsp.common.constants.UltaCommonConstants.REQUEST_CONTEXT_NAME)
          .orElse(null);
      LOG.info("Request Object : {}, ", requestContext);

      return ultaAdeptMindClientService.getClient(requestContext)
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(UltaAdeptmindConstants.SEARCH_URI)
                    .queryParam(LOCALE, amSearchRequest.getLocale())
                    .build())
        .body(BodyInserters.fromObject(amSearchRequest))
        .exchange()
        .flatMap(this::getSearchProductsResponse)
        .map(Mappers.getMapper(UltaSearchAMMapper.class)::map)
        .doOnSuccess(
            productSearchData ->
                LOG.debug("Product search looked up for request: {}", amSearchRequest))
        .doOnError(
            ex ->
                LOG.error(
                    "Exception looking up product search for request : {}", amSearchRequest, ex));
    });
  }
}
```

